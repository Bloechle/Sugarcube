package sugarcube.formats.epub.fonts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FontTester {
    private static final int UNITS_PER_EM = 1000;
    private static final int STROKE_WIDTH = 100;
    private static final int ASCENT = 800;
    private static final int DESCENT = 200;
    private static final int X_HEIGHT = 500;
    private static final int CAP_HEIGHT = 700;

    /**
     * Generates a basic font file containing uppercase letters A-Z, lowercase a-z,
     * numbers 0-9, and basic punctuation
     * @param outputPath The path where the font file should be saved
     * @throws IOException If there's an error writing the font file
     */
    public static void generateBasicFont(String outputPath) throws IOException {
        OTFWriter writer = new OTFWriter();

        // Set font metadata
        writer.setFontName("SugarCubeBasic");
        writer.setFontVersion("Version 1.0");

        // Generate uppercase letters A-Z
        for (int unicode = 65; unicode <= 90; unicode++) {
            writer.addGlyph(createUppercaseGlyph(unicode));
        }

        // Generate lowercase letters a-z
        for (int unicode = 97; unicode <= 122; unicode++) {
            writer.addGlyph(createLowercaseGlyph(unicode));
        }

        // Generate numbers 0-9
        for (int unicode = 48; unicode <= 57; unicode++) {
            writer.addGlyph(createNumberGlyph(unicode));
        }

        // Add punctuation marks
        addPunctuationGlyphs(writer);

        // Save the font
        writer.writeFont(outputPath);
    }

    private static Glyph createUppercaseGlyph(int unicode) {
        List<PathCommand> commands = new ArrayList<>();

        // Calculate position in the uppercase area
        int baseline = ASCENT - DESCENT;
        int top = baseline - CAP_HEIGHT;

        switch (unicode) {
            case 65: // 'A'
                commands.addAll(createLetterA(baseline, top));
                break;
            case 66: // 'B'
                commands.addAll(createLetterB(baseline, top));
                break;
            case 67: // 'C'
                commands.addAll(createLetterC(baseline, top));
                break;
            case 79: // 'O'
                commands.addAll(createLetterO(baseline, top));
                break;
            default:
                // Default rectangular shape with variations
                commands.addAll(createDefaultUppercase(unicode, baseline, top));
        }

        return new Glyph(unicode, commands, UNITS_PER_EM);
    }

    private static Glyph createLowercaseGlyph(int unicode) {
        List<PathCommand> commands = new ArrayList<>();

        // Calculate position in the lowercase area
        int baseline = ASCENT - DESCENT;
        int top = baseline - X_HEIGHT;

        switch (unicode) {
            case 97: // 'a'
                commands.addAll(createLetterSmallA(baseline, top));
                break;
            case 111: // 'o'
                commands.addAll(createLetterSmallO(baseline, top));
                break;
            default:
                // Default rectangular shape with variations
                commands.addAll(createDefaultLowercase(unicode, baseline, top));
        }

        return new Glyph(unicode, commands, (int)(UNITS_PER_EM * 0.8));
    }

    private static List<PathCommand> createLetterA(int baseline, int top) {
        List<PathCommand> commands = new ArrayList<>();

        // Create an 'A' shape
        commands.add(new PathCommand.MoveTo(STROKE_WIDTH, baseline));
        commands.add(new PathCommand.LineTo(UNITS_PER_EM/2, top));
        commands.add(new PathCommand.LineTo(UNITS_PER_EM - STROKE_WIDTH, baseline));

        // Add crossbar
        int crossbarY = baseline - (baseline - top)/2;
        commands.add(new PathCommand.MoveTo(UNITS_PER_EM/4, crossbarY));
        commands.add(new PathCommand.LineTo(3*UNITS_PER_EM/4, crossbarY));

        return commands;
    }

    private static List<PathCommand> createLetterB(int baseline, int top) {
        List<PathCommand> commands = new ArrayList<>();

        // Vertical stem
        commands.add(new PathCommand.MoveTo(STROKE_WIDTH, baseline));
        commands.add(new PathCommand.LineTo(STROKE_WIDTH, top));

        // Upper and lower bowls
        int midY = (baseline + top)/2;

        // Upper bowl
        commands.add(new PathCommand.MoveTo(STROKE_WIDTH, top));
        commands.add(new PathCommand.CurveTo(
                STROKE_WIDTH, top,
                UNITS_PER_EM - STROKE_WIDTH, top,
                UNITS_PER_EM - STROKE_WIDTH, midY
        ));
        commands.add(new PathCommand.CurveTo(
                UNITS_PER_EM - STROKE_WIDTH, midY + STROKE_WIDTH,
                STROKE_WIDTH, midY + STROKE_WIDTH,
                STROKE_WIDTH, midY
        ));

        // Lower bowl
        commands.add(new PathCommand.MoveTo(STROKE_WIDTH, midY));
        commands.add(new PathCommand.CurveTo(
                STROKE_WIDTH, midY,
                UNITS_PER_EM - STROKE_WIDTH, midY,
                UNITS_PER_EM - STROKE_WIDTH, baseline
        ));
        commands.add(new PathCommand.CurveTo(
                UNITS_PER_EM - STROKE_WIDTH, baseline + STROKE_WIDTH,
                STROKE_WIDTH, baseline + STROKE_WIDTH,
                STROKE_WIDTH, baseline
        ));

        return commands;
    }

    private static List<PathCommand> createLetterC(int baseline, int top) {
        List<PathCommand> commands = new ArrayList<>();

        // Create a 'C' shape using curves
        commands.add(new PathCommand.MoveTo(UNITS_PER_EM - STROKE_WIDTH, baseline));
        commands.add(new PathCommand.CurveTo(
                UNITS_PER_EM - STROKE_WIDTH, baseline + STROKE_WIDTH,
                STROKE_WIDTH, baseline + STROKE_WIDTH,
                STROKE_WIDTH, (baseline + top)/2
        ));
        commands.add(new PathCommand.CurveTo(
                STROKE_WIDTH, top - STROKE_WIDTH,
                UNITS_PER_EM - STROKE_WIDTH, top - STROKE_WIDTH,
                UNITS_PER_EM - STROKE_WIDTH, top
        ));

        return commands;
    }

    private static List<PathCommand> createLetterO(int baseline, int top) {
        List<PathCommand> commands = new ArrayList<>();

        // Create an 'O' shape using curves
        commands.add(new PathCommand.MoveTo(UNITS_PER_EM/2, top));
        commands.add(new PathCommand.CurveTo(
                UNITS_PER_EM - STROKE_WIDTH, top,
                UNITS_PER_EM - STROKE_WIDTH, baseline,
                UNITS_PER_EM/2, baseline
        ));
        commands.add(new PathCommand.CurveTo(
                STROKE_WIDTH, baseline,
                STROKE_WIDTH, top,
                UNITS_PER_EM/2, top
        ));

        return commands;
    }

    private static List<PathCommand> createLetterSmallA(int baseline, int top) {
        List<PathCommand> commands = new ArrayList<>();

        // Create a single-story 'a'
        int width = (int)(UNITS_PER_EM * 0.8);

        // Bowl
        commands.add(new PathCommand.MoveTo(width - STROKE_WIDTH, baseline));
        commands.add(new PathCommand.CurveTo(
                width - STROKE_WIDTH, baseline + STROKE_WIDTH,
                STROKE_WIDTH, baseline + STROKE_WIDTH,
                STROKE_WIDTH, (baseline + top)/2
        ));
        commands.add(new PathCommand.CurveTo(
                STROKE_WIDTH, top - STROKE_WIDTH,
                width - STROKE_WIDTH, top - STROKE_WIDTH,
                width - STROKE_WIDTH, top
        ));

        return commands;
    }

    private static List<PathCommand> createLetterSmallO(int baseline, int top) {
        List<PathCommand> commands = new ArrayList<>();

        // Create a small 'o' shape
        int width = (int)(UNITS_PER_EM * 0.8);

        commands.add(new PathCommand.MoveTo(width/2, top));
        commands.add(new PathCommand.CurveTo(
                width - STROKE_WIDTH, top,
                width - STROKE_WIDTH, baseline,
                width/2, baseline
        ));
        commands.add(new PathCommand.CurveTo(
                STROKE_WIDTH, baseline,
                STROKE_WIDTH, top,
                width/2, top
        ));

        return commands;
    }

    private static List<PathCommand> createDefaultUppercase(int unicode, int baseline, int top) {
        List<PathCommand> commands = new ArrayList<>();

        // Create a rectangular shape with variations based on unicode value
        int width = UNITS_PER_EM - 2 * STROKE_WIDTH;
        width = width - ((unicode % 5) * STROKE_WIDTH);

        commands.add(new PathCommand.MoveTo(STROKE_WIDTH, baseline));
        commands.add(new PathCommand.LineTo(STROKE_WIDTH + width, baseline));
        commands.add(new PathCommand.LineTo(STROKE_WIDTH + width, top));
        commands.add(new PathCommand.LineTo(STROKE_WIDTH, top));
        commands.add(new PathCommand.LineTo(STROKE_WIDTH, baseline));

        return commands;
    }

    private static List<PathCommand> createDefaultLowercase(int unicode, int baseline, int top) {
        List<PathCommand> commands = new ArrayList<>();

        // Create a smaller rectangular shape with variations
        int width = (int)(UNITS_PER_EM * 0.8) - 2 * STROKE_WIDTH;
        width = width - ((unicode % 3) * STROKE_WIDTH);

        commands.add(new PathCommand.MoveTo(STROKE_WIDTH, baseline));
        commands.add(new PathCommand.LineTo(STROKE_WIDTH + width, baseline));
        commands.add(new PathCommand.LineTo(STROKE_WIDTH + width, top));
        commands.add(new PathCommand.LineTo(STROKE_WIDTH, top));
        commands.add(new PathCommand.LineTo(STROKE_WIDTH, baseline));

        return commands;
    }

    private static Glyph createNumberGlyph(int unicode) {
        List<PathCommand> commands = new ArrayList<>();

        int baseline = ASCENT - DESCENT;
        int top = baseline - CAP_HEIGHT;

        // Create a rectangular shape with a diagonal line
        commands.add(new PathCommand.MoveTo(STROKE_WIDTH, baseline));
        commands.add(new PathCommand.LineTo(UNITS_PER_EM - STROKE_WIDTH, baseline));
        commands.add(new PathCommand.LineTo(UNITS_PER_EM - STROKE_WIDTH, top));
        commands.add(new PathCommand.LineTo(STROKE_WIDTH, top));
        commands.add(new PathCommand.LineTo(STROKE_WIDTH, baseline));

        // Add diagonal line
        commands.add(new PathCommand.MoveTo(STROKE_WIDTH, baseline));
        commands.add(new PathCommand.LineTo(UNITS_PER_EM - STROKE_WIDTH, top));

        return new Glyph(unicode, commands, UNITS_PER_EM);
    }

    private static void addPunctuationGlyphs(OTFWriter writer) {
        writer.addGlyph(createPunctuationGlyph('.', 46));
        writer.addGlyph(createPunctuationGlyph(',', 44));
        writer.addGlyph(createPunctuationGlyph('!', 33));
        writer.addGlyph(createPunctuationGlyph('?', 63));
        writer.addGlyph(createPunctuationGlyph('-', 45));
        writer.addGlyph(createSpaceGlyph());
    }

    private static Glyph createPunctuationGlyph(char symbol, int unicode) {
        List<PathCommand> commands = new ArrayList<>();
        int baseline = ASCENT - DESCENT;

        switch (symbol) {
            case '.':
                commands.add(new PathCommand.MoveTo(UNITS_PER_EM/2 - STROKE_WIDTH, baseline));
                commands.add(new PathCommand.LineTo(UNITS_PER_EM/2 + STROKE_WIDTH, baseline));
                commands.add(new PathCommand.LineTo(UNITS_PER_EM/2 + STROKE_WIDTH, baseline - 2*STROKE_WIDTH));
                commands.add(new PathCommand.LineTo(UNITS_PER_EM/2 - STROKE_WIDTH, baseline - 2*STROKE_WIDTH));
                commands.add(new PathCommand.LineTo(UNITS_PER_EM/2 - STROKE_WIDTH, baseline));
                break;

            case ',':
                commands.add(new PathCommand.MoveTo(UNITS_PER_EM/2 - STROKE_WIDTH, baseline));
                commands.add(new PathCommand.LineTo(UNITS_PER_EM/2 + STROKE_WIDTH, baseline));
                commands.add(new PathCommand.LineTo(UNITS_PER_EM/2 + STROKE_WIDTH, baseline + STROKE_WIDTH));
                commands.add(new PathCommand.LineTo(UNITS_PER_EM/2 - STROKE_WIDTH, baseline + STROKE_WIDTH));
                commands.add(new PathCommand.LineTo(UNITS_PER_EM/2 - STROKE_WIDTH, baseline));
                break;

            case '!':
                // Vertical line
                commands.add(new PathCommand.MoveTo(UNITS_PER_EM/2 - STROKE_WIDTH/2, baseline - CAP_HEIGHT));
                commands.add(new PathCommand.LineTo(UNITS_PER_EM/2 + STROKE_WIDTH/2, baseline - CAP_HEIGHT));
                commands.add(new PathCommand.LineTo(UNITS_PER_EM/2 + STROKE_WIDTH/2, baseline - 3*STROKE_WIDTH));
                commands.add(new PathCommand.LineTo(UNITS_PER_EM/2 - STROKE_WIDTH/2, baseline - 3*STROKE_WIDTH));
                commands.add(new PathCommand.LineTo(UNITS_PER_EM/2 - STROKE_WIDTH/2, baseline - CAP_HEIGHT));

                // Dot
                commands.add(new PathCommand.MoveTo(UNITS_PER_EM/2 - STROKE_WIDTH/2, baseline));
                commands.add(new PathCommand.LineTo(UNITS_PER_EM/2 + STROKE_WIDTH/2, baseline));
                commands.add(new PathCommand.LineTo(UNITS_PER_EM/2 + STROKE_WIDTH/2, baseline - STROKE_WIDTH));
                commands.add(new PathCommand.LineTo(UNITS_PER_EM/2 - STROKE_WIDTH/2, baseline - STROKE_WIDTH));
                commands.add(new PathCommand.LineTo(UNITS_PER_EM/2 - STROKE_WIDTH/2, baseline));
                break;

            case '?':
                // Hook shape
                commands.add(new PathCommand.MoveTo(UNITS_PER_EM/2 - STROKE_WIDTH, baseline - CAP_HEIGHT));
                commands.add(new PathCommand.CurveTo(
                        UNITS_PER_EM/2 + 2*STROKE_WIDTH, baseline - CAP_HEIGHT,
                        UNITS_PER_EM/2 + 2*STROKE_WIDTH, baseline - CAP_HEIGHT/2,
                        UNITS_PER_EM/2, baseline - CAP_HEIGHT/2
                ));

                // Dot
                commands.add(new PathCommand.MoveTo(UNITS_PER_EM/2 - STROKE_WIDTH/2, baseline));
                commands.add(new PathCommand.LineTo(UNITS_PER_EM/2 + STROKE_WIDTH/2, baseline));
                commands.add(new PathCommand.LineTo(UNITS_PER_EM/2 + STROKE_WIDTH/2, baseline - STROKE_WIDTH));
                commands.add(new PathCommand.LineTo(UNITS_PER_EM/2 - STROKE_WIDTH/2, baseline - STROKE_WIDTH));
                commands.add(new PathCommand.LineTo(UNITS_PER_EM/2 - STROKE_WIDTH/2, baseline));
                break;

            case '-':
                commands.add(new PathCommand.MoveTo(STROKE_WIDTH, baseline - X_HEIGHT/2));
                commands.add(new PathCommand.LineTo(UNITS_PER_EM - STROKE_WIDTH, baseline - X_HEIGHT/2));
                commands.add(new PathCommand.LineTo(UNITS_PER_EM - STROKE_WIDTH, baseline - X_HEIGHT/2 - STROKE_WIDTH));
                commands.add(new PathCommand.LineTo(STROKE_WIDTH, baseline - X_HEIGHT/2 - STROKE_WIDTH));
                commands.add(new PathCommand.LineTo(STROKE_WIDTH, baseline - X_HEIGHT/2));
                break;
        }

        return new Glyph(unicode, commands, UNITS_PER_EM/2);
    }

    private static Glyph createSpaceGlyph() {
        // Space character has no visible paths but needs width
        return new Glyph(32, new ArrayList<>(), UNITS_PER_EM/3);
    }

    /**
     * Main method to generate a sample font file
     * @param args Command line arguments. If provided, first argument will be used as output path
     */
    public static void main(String[] args) {
        try {
            // Default output path if none provided
            String outputPath = args.length > 0 ? args[0] : "test.otf";

            System.out.println("Generating font: " + outputPath);
            generateBasicFont(outputPath);
            System.out.println("Font generated successfully!");

            // Print some information about the generated font
            File fontFile = new File(outputPath);
            System.out.println("Font file size: " + fontFile.length() + " bytes");
            System.out.println("Font file location: " + fontFile.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("Error generating font: " + e.getMessage());
            e.printStackTrace();
        }
    }
}