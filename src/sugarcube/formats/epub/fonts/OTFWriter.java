package sugarcube.formats.epub.fonts;

import sugarcube.formats.epub.fonts.tables.CmapTable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class OTFWriter {
    private static final int OTF_VERSION = 0x4F54544F; // 'OTTO' in hex
    private ByteArrayOutputStream fontData;
    private Map<String, TableEntry> tables;
    private Map<Integer, Glyph> glyphs;
    private static final int DEFAULT_UNITS_PER_EM = 1000;
    private String fontName = "CustomFont";
    private String fontVersion = "Version 1.0";

    private static final String[] OPTIMAL_TABLE_ORDER = {
            "head", "hhea", "maxp", "OS/2", "name", "cmap", "post", "CFF ", "DSIG"
    };


    public OTFWriter() {
        this.fontData = new ByteArrayOutputStream();
        this.tables = new LinkedHashMap<>(); // Maintains insertion order
        this.glyphs = new TreeMap<>();

        // Add .notdef glyph by default
        List<PathCommand> notdefCommands = createNotdefPath();
        addGlyph(new Glyph(0, notdefCommands, DEFAULT_UNITS_PER_EM));
    }

    public void setFontName(String name) {
        this.fontName = name;
    }

    public void setFontVersion(String version) {
        this.fontVersion = version;
    }

    public void addGlyph(Glyph glyph) {
        glyph.glyphIndex = glyphs.size();
        glyphs.put(glyph.unicode, glyph);
    }

    private void addTable(String tag, byte[] data) {
        tables.put(tag, new TableEntry(data));
    }

    public void addRequiredTables() throws IOException {
        // Create tables in optimal order
        addHeadTable();
        addHheaTable();
        addMaxpTable();
        addOS2Table();
        addNameTable();
        addTable("cmap", CmapTable.generate(glyphs));
        addPostTable();
        addCFFTable();
        addDSIGTable();

        // Sort tables according to optimal order
        LinkedHashMap<String, TableEntry> sortedTables = new LinkedHashMap<>();
        for (String tag : OPTIMAL_TABLE_ORDER) {
            if (tables.containsKey(tag)) {
                sortedTables.put(tag, tables.get(tag));
            }
        }
        this.tables = sortedTables;
    }

    private void addDSIGTable() throws IOException {
        FontOutputStream fos = new FontOutputStream();

        // DSIG Header
        fos.ints(
                0x00000001,  // Version
                0,           // numSignatures
                0            // flags
        );

        addTable("DSIG", fos.bytes());
    }


    private void addCFFTable() throws IOException {
        FontOutputStream fos = new FontOutputStream();

        // CFF Header
        fos.write(new byte[] {
                0x01, 0x00,        // Major, Minor version
                0x04,              // Header size
                0x04               // Absolute offset size
        });

        // Name INDEX
        writeINDEX(fos, Collections.singletonList(fontName.getBytes(StandardCharsets.US_ASCII)));

        // Generate Top DICT data
        ByteArrayOutputStream topDictData = new ByteArrayOutputStream();
        writeDict(topDictData, Arrays.asList(
                new int[]{0x00, 1},      // Version
                new int[]{0x01, 0},      // Notice
                new int[]{0x02, 0},      // FullName
                new int[]{0x03, 0},      // FamilyName
                new int[]{0x04, 0},      // Weight
                new int[]{0x0C, 2, 0, 0, DEFAULT_UNITS_PER_EM, DEFAULT_UNITS_PER_EM},  // FontBBox
                new int[]{0x0F, DEFAULT_UNITS_PER_EM},  // defaultWidthX
                new int[]{0x10, 0}       // nominalWidthX
        ));

        // Top DICT INDEX
        writeINDEX(fos, Collections.singletonList(topDictData.toByteArray()));

        // String INDEX
        List<byte[]> strings = Arrays.asList(
                "Version 1.0".getBytes(StandardCharsets.US_ASCII),
                "Copyright".getBytes(StandardCharsets.US_ASCII),
                fontName.getBytes(StandardCharsets.US_ASCII),
                "Regular".getBytes(StandardCharsets.US_ASCII)
        );
        writeINDEX(fos, strings);

        // Global Subr INDEX
        writeINDEX(fos, Collections.singletonList(new byte[]{0x0E})); // Return command

        // Write CharStrings
        ByteArrayOutputStream charStrings = new ByteArrayOutputStream();
        // First write .notdef
        charStrings.write(new byte[]{
                0x0E  // endchar operator
        });

        // Write actual glyphs
        for (Glyph glyph : glyphs.values()) {
            if (glyph.unicode != 0) { // Skip .notdef as we already wrote it
                ByteArrayOutputStream glyphData = new ByteArrayOutputStream();
                // Write width
                writeDictNumber(glyphData, glyph.advanceWidth - DEFAULT_UNITS_PER_EM);
                // Write path commands
                for (PathCommand cmd : glyph.pathCommands) {
                    glyphData.write(cmd.toCFFFormat());
                }
                // Add endchar operator
                glyphData.write(0x0E);
                charStrings.write(glyphData.toByteArray());
            }
        }
        writeINDEX(fos, Collections.singletonList(charStrings.toByteArray()));

        // Private DICT
        ByteArrayOutputStream privateDict = new ByteArrayOutputStream();
        writeDict(privateDict, Arrays.asList(
                new int[]{0x06, 0},  // BlueValues
                new int[]{0x07, 0},  // OtherBlues
                new int[]{0x08, 0},  // FamilyBlues
                new int[]{0x09, 0},  // FamilyOtherBlues
                new int[]{0x0A, 0},  // StdHW
                new int[]{0x0B, 0}   // StdVW
        ));
        fos.write(privateDict.toByteArray());

        addTable("CFF ", fos.bytes());
    }

    private void writeINDEX(FontOutputStream fos, List<byte[]> items) throws IOException {
        if (items.isEmpty()) {
            fos.shorts(0); // Count
            return;
        }

        fos.shorts(items.size()); // Count

        // Calculate offSize
        int maxOffset = 1;
        for (byte[] item : items) {
            maxOffset += item.length;
        }

        int offSize;
        if (maxOffset < 256) offSize = 1;
        else if (maxOffset < 65536) offSize = 2;
        else if (maxOffset < 16777216) offSize = 3;
        else offSize = 4;

        fos.write(offSize); // OffSize

        // Write offsets
        int offset = 1;
        for (byte[] item : items) {
            writeOffset(fos, offset, offSize);
            offset += item.length;
        }
        writeOffset(fos, offset, offSize);

        // Write data
        for (byte[] item : items) {
            fos.write(item);
        }
    }

    private void writeDict(ByteArrayOutputStream out, List<int[]> operators) throws IOException {
        for (int[] op : operators) {
            for (int i = 1; i < op.length; i++) {
                writeDictNumber(out, op[i]);
            }
            if (op[0] > 255) {
                out.write(op[0] >> 8);
            }
            out.write(op[0] & 0xFF);
        }
    }

    private void writeDictNumber(ByteArrayOutputStream out, int number) throws IOException {
        if (number >= -107 && number <= 107) {
            out.write(number + 139);
        } else if (number >= 108 && number <= 1131) {
            number -= 108;
            out.write((number >> 8) + 247);
            out.write(number & 0xFF);
        } else if (number >= -1131 && number <= -108) {
            number = -number - 108;
            out.write((number >> 8) + 251);
            out.write(number & 0xFF);
        } else {
            out.write(28);
            out.write((number >> 8) & 0xFF);
            out.write(number & 0xFF);
        }
    }

    private void writeDictOperator(ByteArrayOutputStream out, int operator) throws IOException {
        if (operator > 255) {
            out.write(operator >> 8);
        }
        out.write(operator & 0xFF);
    }

    private void writeOffset(FontOutputStream fos, int offset, int offSize) throws IOException {
        for (int i = offSize - 1; i >= 0; i--) {
            fos.write((offset >> (8 * i)) & 0xFF);
        }
    }
    private void writeHeader() throws IOException {
        writeInt(OTF_VERSION);
        writeShort(tables.size());

        int searchRange = 16 * (int) Math.pow(2, Math.floor(Math.log(tables.size()) / Math.log(2)));
        int entrySelector = (int) Math.floor(Math.log(searchRange / 16) / Math.log(2));
        int rangeShift = tables.size() * 16 - searchRange;

        writeShort(searchRange);
        writeShort(entrySelector);
        writeShort(rangeShift);
    }

    private void writeTableDirectory() throws IOException {
        int offset = 12 + (tables.size() * 16);

        for (Map.Entry<String, TableEntry> entry : tables.entrySet()) {
            String tag = entry.getKey();
            TableEntry table = entry.getValue();
            writeString(tag);
            table.offset = offset;
            writeInt(table.checksum);
            writeInt(offset);
            writeInt(table.data.length);
            offset += (table.data.length + 3) & ~3;
        }
    }

    private void addHeadTable() throws IOException {
        FontOutputStream fos = new FontOutputStream();
        long macTime = (System.currentTimeMillis() / 1000L) + (((1970 - 1904) * 365L + 17L) * 24L * 60L * 60L);

        fos.ints(
                0x00010000, // Version 1.0
                0x00010000, // Font revision
                0, // Checksum adjustment
                0x5F0F3CF5 // Magic number
        );
        fos.shorts(0x000B); // Flags
        fos.shorts(DEFAULT_UNITS_PER_EM);
        fos.longs(macTime * 1000000L, // Created
                macTime * 1000000L); // Modified
        fos.shorts(
                0, // xMin
                0, // yMin
                DEFAULT_UNITS_PER_EM, // xMax
                DEFAULT_UNITS_PER_EM, // yMax
                0, // Mac style
                8, // Lowest rec PPEM
                2, // Font direction hint
                0, // Index to loc format
                0  // Glyph data format
        );

        addTable("head", fos.bytes());
    }

    private void addHheaTable() throws IOException {
        FontOutputStream fos = new FontOutputStream();

        fos.ints(0x00010000); // Version 1.0
        fos.shorts(
                800, // Ascender
                -200, // Descender
                200, // LineGap
                DEFAULT_UNITS_PER_EM, // advanceWidthMax
                0, // minLeftSideBearing
                0, // minRightSideBearing
                DEFAULT_UNITS_PER_EM, // xMaxExtent
                1, // caretSlopeRise
                0, // caretSlopeRun
                0, // caretOffset
                0, // reserved
                0, // reserved
                0, // reserved
                0, // reserved
                0, // metricDataFormat
                glyphs.size() // numberOfHMetrics
        );

        addTable("hhea", fos.bytes());
    }

    private void addHmtxTable() throws IOException {
        FontOutputStream fos = new FontOutputStream();

        for (int i = 0; i < glyphs.size(); i++) {
            final int index = i;  // Make effectively final for lambda
            Glyph glyph = glyphs.values().stream()
                    .filter(g -> g.glyphIndex == index)
                    .findFirst()
                    .orElse(null);

            fos.shorts(
                    glyph != null ? glyph.advanceWidth : DEFAULT_UNITS_PER_EM,
                    glyph != null ? glyph.leftSideBearing : 0
            );
        }

        addTable("hmtx", fos.bytes());
    }

    private void addMaxpTable() throws IOException {
        FontOutputStream fos = new FontOutputStream();
        fos.ints(0x00005000); // Version 0.5 (CFF)
        fos.shorts(glyphs.size()); // numGlyphs
        addTable("maxp", fos.bytes());
    }

    private void addNameTable() throws IOException {
        FontOutputStream fos = new FontOutputStream();
        List<NameRecord> records = new ArrayList<>();

        // Add required name records for both platforms
        addName(records, 1, true, fontName); // Font Family
        addName(records, 2, true, "Regular"); // Font Subfamily
        addName(records, 3, true, fontName + " " + fontVersion); // Unique ID
        addName(records, 4, true, fontName); // Full font name
        addName(records, 5, true, fontVersion); // Version
        addName(records, 6, true, fontName.replaceAll(" ", "-")); // PostScript name

        addName(records, 1, false, fontName);
        addName(records, 2, false, "Regular");
        addName(records, 3, false, fontName + " " + fontVersion);
        addName(records, 4, false, fontName);
        addName(records, 5, false, fontVersion);
        addName(records, 6, false, fontName.replaceAll(" ", "-"));

        fos.shorts(
                0, // Format 0
                records.size(), // Count
                6 + (12 * records.size()) // String offset
        );

        int stringOffset = 0;
        for (NameRecord record : records) {
            fos.shorts(
                    record.platformID,
                    record.encodingID,
                    record.languageID,
                    record.nameID,
                    record.length,
                    stringOffset
            );
            stringOffset += record.length;
        }

        for (NameRecord record : records) {
            fos.write(record.string);
        }

        addTable("name", fos.bytes());
    }

    private void addName(List<NameRecord> records, int nameID, boolean isMicrosoftPlatform, String value) {
        if (isMicrosoftPlatform) {
            records.add(new NameRecord(3, 1, 0x0409, nameID, value.getBytes(StandardCharsets.UTF_16BE)));
        } else {
            records.add(new NameRecord(1, 0, 0, nameID, value.getBytes(StandardCharsets.US_ASCII)));
        }
    }


    private void addOS2Table() throws IOException {
        FontOutputStream fos = new FontOutputStream();

        fos.shorts(
                0x0004, // version
                DEFAULT_UNITS_PER_EM, // xAvgCharWidth
                400, // usWeightClass
                5, // usWidthClass
                0x0000 // fsType
        );
        fos.shorts(500, 500, 0, 0); // ySubscript
        fos.shorts(500, 500, 0, 0); // ySuperscript
        fos.shorts(
                50, // yStrikeoutSize
                250, // yStrikeoutPosition
                0 // sFamilyClass
        );
        fos.write(new byte[10]); // panose
        fos.ints(0, 0, 0, 0); // ulUnicodeRange 1-4
        fos.write("    ".getBytes()); // achVendID
        fos.shorts(
                0x0040, // fsSelection
                32, // usFirstCharIndex
                255, // usLastCharIndex
                800, // sTypoAscender
                -200, // sTypoDescender
                200, // sTypoLineGap
                800, // usWinAscent
                200 // usWinDescent
        );

        addTable("OS/2", fos.bytes());
    }

    private void addPostTable() throws IOException {
        FontOutputStream fos = new FontOutputStream();

        fos.ints(
                0x00030000, // Version 3.0
                0, // italicAngle
                0, // minMemType42
                0, // maxMemType42
                0, // minMemType1
                0  // maxMemType1
        );
        fos.shorts(
                0, // underlinePosition
                0  // underlineThickness
        );

        addTable("post", fos.bytes());
    }

    private List<PathCommand> createNotdefPath() {
        List<PathCommand> commands = new ArrayList<>();
        commands.add(new PathCommand.MoveTo(100, 100));
        commands.add(new PathCommand.LineTo(900, 100));
        commands.add(new PathCommand.LineTo(900, 900));
        commands.add(new PathCommand.LineTo(100, 900));
        commands.add(new PathCommand.LineTo(100, 100));
        return commands;
    }

    private void writeString(String s) throws IOException {
        fontData.write(s.getBytes("ASCII"));
    }

    private void writeInt(int value) throws IOException {
        fontData.write((value >> 24) & 0xFF);
        fontData.write((value >> 16) & 0xFF);
        fontData.write((value >> 8) & 0xFF);
        fontData.write(value & 0xFF);
    }

    private void writeShort(int value) throws IOException {
        fontData.write((value >> 8) & 0xFF);
        fontData.write(value & 0xFF);
    }


    private void writeTableData() throws IOException {
        for (TableEntry table : tables.values()) {
            fontData.write(table.data);

            int padding = (4 - (table.data.length % 4)) % 4;
            for (int i = 0; i < padding; i++) {
                fontData.write(0);
            }
        }
    }

    private void adjustChecksum() throws IOException {
        long checksum = 0;
        for (TableEntry entry : tables.values()) {
            checksum += entry.checksum & 0xFFFFFFFFL;
        }
        checksum = 0xB1B0AFBAL - (checksum & 0xFFFFFFFFL);

        // Update head table checksum adjustment
        byte[] headTable = tables.get("head").data;
        headTable[8] = (byte)((checksum >> 24) & 0xFF);
        headTable[9] = (byte)((checksum >> 16) & 0xFF);
        headTable[10] = (byte)((checksum >> 8) & 0xFF);
        headTable[11] = (byte)(checksum & 0xFF);
    }

    public byte[] writeFont() throws IOException {
        fontData.reset();
        writeHeader();
        writeTableDirectory();
        writeTableData();
        adjustChecksum(); // Added checksum adjustment
        return fontData.toByteArray();
    }

    public void writeFont(String outputPath) throws IOException {
        addRequiredTables();
        byte[] fontBytes = writeFont();
        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            fos.write(fontBytes);
        }
    }
}