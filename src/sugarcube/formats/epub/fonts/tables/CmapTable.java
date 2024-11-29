package sugarcube.formats.epub.fonts.tables;

import sugarcube.formats.epub.fonts.CmapSegment;
import sugarcube.formats.epub.fonts.FontOutputStream;
import sugarcube.formats.epub.fonts.Glyph;
import sugarcube.formats.epub.fonts.OTFWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CmapTable {


    public static byte[] generate(Map<Integer, Glyph> glyphs) throws IOException {
        FontOutputStream fos = new FontOutputStream();
        TreeMap<Integer, Integer> charMap = new TreeMap<>();
        glyphs.forEach((k, v) -> charMap.put(k, v.glyphIndex));

        List<CmapSegment> segments = buildCmapSegments(charMap);
        int segCount = segments.size();
        int subtableOffset = 12;

        fos.shorts(
                0, // version
                1 // numTables
        );
        fos.shorts(
                3, // platformID (Microsoft)
                1  // encodingID (Unicode BMP)
        );
        fos.ints(subtableOffset);

        fos.shorts(
                4, // format
                16 + (segCount * 8), // length
                0, // language
                segCount * 2 // segCountX2
        );

        int searchRange = 2 * (int)Math.pow(2, Math.floor(Math.log(segCount)/Math.log(2)));
        fos.shorts(
                searchRange,
                (int)Math.floor(Math.log(searchRange/2)/Math.log(2)),
                2 * segCount - searchRange
        );

        // Write arrays
        segments.forEach(s -> {
            try {
                fos.shorts(s.endCode);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        fos.shorts(0); // reservedPad

        segments.forEach(s -> {
            try {
                fos.shorts(s.startCode);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        segments.forEach(s -> {
            try {
                fos.shorts(s.idDelta);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        segments.forEach(s -> {
            try {
                fos.shorts(s.idRangeOffset);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return fos.bytes();
    }

    private static List<CmapSegment> buildCmapSegments(TreeMap<Integer, Integer> charMap) {
        List<CmapSegment> segments = new ArrayList<>();

        if (charMap.isEmpty()) {
            segments.add(new CmapSegment(0xFFFF, 0xFFFF, 1, 0));
            return segments;
        }

        int startCode = -1;
        int prevCode = -1;
        int prevGlyphId = -1;

        for (Map.Entry<Integer, Integer> entry : charMap.entrySet()) {
            int charCode = entry.getKey();
            int glyphId = entry.getValue();

            if (startCode == -1) {
                startCode = charCode;
            } else if (charCode != prevCode + 1 || glyphId != prevGlyphId + 1) {
                segments.add(new CmapSegment(startCode, prevCode, 1, 0));
                startCode = charCode;
            }

            prevCode = charCode;
            prevGlyphId = glyphId;
        }

        if (startCode != -1) {
            segments.add(new CmapSegment(startCode, prevCode, 1, 0));
        }

        segments.add(new CmapSegment(0xFFFF, 0xFFFF, 1, 0));
        return segments;
    }


}
