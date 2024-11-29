package sugarcube.formats.epub.fonts;

import java.util.List;

// Basic glyph data structure
public class Glyph {
    public int unicode;           // Unicode code point
    public List<PathCommand> pathCommands; // Vector path commands
    public int advanceWidth;     // Character width
    public int leftSideBearing;  // Space before glyph
    public int glyphIndex;       // Index in the font

    public Glyph(int unicode, List<PathCommand> pathCommands, int advanceWidth) {
        this.unicode = unicode;
        this.pathCommands = pathCommands;
        this.advanceWidth = advanceWidth;
        this.leftSideBearing = 0; // Default value
    }
}
