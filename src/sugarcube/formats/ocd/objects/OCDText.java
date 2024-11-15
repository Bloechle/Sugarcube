package sugarcube.formats.ocd.objects;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.*;
import sugarcube.common.graphics.geom.*;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Stroke3;
import sugarcube.common.interfaces.Glyph;
import sugarcube.common.ui.fx.shapes.FxPath;
import sugarcube.common.ui.gui.Font3;
import sugarcube.common.data.xml.CharRef;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Nb;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.css.CSS;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDPageContent.State;
import sugarcube.formats.ocd.objects.font.SVGFont;
import sugarcube.formats.ocd.objects.font.SVGGlyph;

import java.awt.*;
import java.util.Collection;

public class OCDText extends OCDPaintableLeaf {
    public static final String TAG = "text";
    // \r ocd XML soft line delimiter (induced by paragraph width)
    public static final char SOFT_RETURN = Unicodes.ASCII_CR;
    // \n ocd paragraph delimiter (user keyboard return)
    public static final char HARD_RETURN = Unicodes.ASCII_LF;
    public static final char SPACE = Unicodes.ASCII_SP;
    public static final String MODE_LTR = "ltr";// left-to-right
    public static final String MODE_RTL = "rtl";// right-to-left
    public static final String MODE_TTB = "ttb";// top-to-bottom

    private static final float CS_XML_FACTOR = 1000;
    private static Font3 DISABLED_FONT = null;
    public static final int FLAG_EOL = 0x01000000;
    public OCDCanon canon = null;
    // public OCDOcr ocr = null;// ocr data (alto, omnipage, etc.)
    public String field;// mapping to dynamic field such as database fields (keys)
    protected Unicodes unicodes; // unicodes if value > 0, fontcode otherwise
    protected String fontname;
    protected float fontsize;
    protected float[] cs;
    protected String decoration;
    protected String mode;
    protected float scriptDx = 0;//
    protected float scriptDy = 0;// sub/superscript
    protected float scriptScale = 1;// sub/superscript
    protected boolean eol = false;

    public OCDText(OCDNode parent) {
        super(TAG, parent);
        this.cs = new float[0];// always initialize cs !
    }

    public OCDText(OCDNode parent, String text, String fontname, float fontsize, float... cs) {
        this(parent);
        this.unicodes = new Unicodes(text);
        this.fontname = fontname;
        this.fontsize = fontsize;
        this.cs = cs;
    }

    public OCDText paragrapher() {
        if (this.unicodes.first() != HARD_RETURN)
            this.prepend(HARD_RETURN + "");
        return this;
    }

    public OCDText needEndSpace() {
        if (!unicodes.endsWithSpace())
            this.append(" ");
        return this;
    }

    public OCDText prepend(String text, float... cs) {
        int size = text.length();
        float[] oldCS = charSpaces(true);
        float[] newCS = new float[oldCS.length + size];
        System.arraycopy(oldCS, 0, newCS, size, oldCS.length);
        for (int i = 0; i < size; i++)
            newCS[i] = cs != null && i < cs.length ? cs[i] : 0;
        this.cs = newCS;
        this.unicodes.prepend(text);
        this.recanonize();
        return this;
    }

    public OCDText append(String text, float... cs) {
        int size = text.length();
        float[] oldCS = charSpaces(true);
        float[] newCS = new float[oldCS.length + size];
        System.arraycopy(oldCS, 0, newCS, 0, oldCS.length);
        for (int i = 0; i < size; i++)
            newCS[i + size] = cs != null && i < cs.length ? cs[i] : 0;
        this.cs = newCS;
        this.unicodes.append(text);
        this.recanonize();
        return this;
    }

    public OCDText recanonize() {
        if (canon != null) {
            this.canon = null;
            this.canonize();
        }
        return this;
    }

    @Override
    public OCDText canonize() {
        if (canon == null) {
            canon = new OCDCanon(this);
            canon.radians = (float) transform().rotation();
            if (Math.abs(canon.radians) < 0.001)
                canon.radians = 0f;
            canon.coords = coords(shifts());

            if (canon.radians != 0f) {
                canon.tm = transform().floatValues();
                Transform3 tm = transform();
                tm.rotate(-canon.radians);

                setTransform(tm);
                canon.coords = canon.coords.rotate(-canon.radians);
            }
        }
        canon.refresh();
        // Log.debug(this, ".canonize - " + this.uniString() + ": " +
        // canon.toString() + ", coords=" + canon.coords);
        return this;
    }

    @Override
    public OCDText uncanonize() {
        if (canon != null) {
            if (canon.radians != 0f) {
                setTransform(canon.tm);
                canon.coords = canon.coords.rotate(canon.radians);
            }
            updateCS(canon.coords);
            this.canon = null;
        }
        return this;
    }

    public static OCDText lineReturn(OCDTextLine line, String fontname, float fontsize, boolean hard) {
        return new OCDText(line, "" + (hard ? HARD_RETURN : SOFT_RETURN), fontname, fontsize);
    }

    public boolean isVerticalMode() {
        return mode == null ? false : mode.equals(MODE_TTB);
    }

    public boolean isMode(String mode) {
        return (mode == null ? MODE_LTR : mode).equals(this.mode == null ? MODE_LTR : this.mode);
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String mode() {
        return this.mode == null ? MODE_LTR : mode;
    }

    // public void setOCR(OCDOcr oz)
    // {
    // this.ocr = oz;
    // }
    //
    // public OCDOcr ocr()
    // {
    // return this.ocr;
    // }
    //
    // public OCDOcr needOCR()
    // {
    // if (ocr == null)
    // ocr = new OCDOcr();
    // if (ocr.box == null)
    // ocr.box = ocr.box(this);
    // return ocr;
    // }

    // public boolean hasOCR()
    // {
    // return this.ocr != null;
    // }

    public OCDText next(boolean crossLines) {
        OCDGroup<? extends OCDPaintable> group = (OCDGroup) this.parent();
        boolean next = false;

        for (OCDPaintable node : group) {
            if (next && OCD.isText(node))
                return (OCDText) node;
            if (node == this)
                next = true;
        }

        if (crossLines && group instanceof OCDTextLine) {
            OCDTextLine line = ((OCDTextLine) group);
            line = line.next(true);
            if (line != null)
                return line.first();
        }

        return null;
    }

    @Override
    public boolean hasContent() {
        return !this.unicodes.isEmpty();
    }

    @Override
    public Cmd command(String key) {
        switch (key) {
            case CSS.FontName:
                return new Cmd(key, fontname);
            case CSS.FontSize:
                return new Cmd(key, fontsize);
            case CSS.TextDecoration:
                return new Cmd(key, decoration == null ? CSS._normal : decoration);
            case CSS.TextScript:
                return new Cmd(key, isSuperscript() ? CSS._superscript : isSubscript() ? CSS._subscript : CSS._normal);
        }
        return super.command(key);
    }

    @Override
    public void command(Cmd cmd) {
        Log.debug(this, ".command - " + cmd);
        switch (cmd.key) {
            case CSS.FontName:// not font family!!!
                fontname = cmd.string(fontname);
                break;
            case CSS.FontSize:
                fontsize = cmd.real(fontsize);
                break;
            case CSS.TextDecoration:
                decoration = cmd.string(decoration);
                break;
            case CSS.TextScript:

                String script = cmd.string("");
                if (script.equals(CSS._superscript)) {
                    scriptScale = 0.6f;
                    scriptDy = -0.35f;
                } else if (script.equals(CSS._subscript)) {
                    scriptScale = 0.6f;
                    scriptDy = 0.25f;
                } else {
                    scriptScale = 1f;
                    scriptDy = 0;
                }
                break;
        }
        super.command(cmd);
    }

    public boolean isApparent() {
        return !isTransparent() && hasPaths() && clip().bounds().intersects(bounds(true));
    }

    public boolean hasPaths() {
        for (Glyph g : glyphs())
            if (!g.path(1).isEmpty())
                return true;
        return false;
    }

    public void setFontname(String fontname) {
        this.fontname = fontname;
    }

    public String fontname() {
        return this.fontname;
    }

    public String fontname(String def) {
        return Zen.isVoid(fontname) ? def : fontname;
    }

    public String decoration() {
        return decoration;
    }

    public void setDecoration(String decoration) {
        this.decoration = decoration;
    }

    public float scriptSize() {
        return this.fontsize * scriptScale;
    }

    public float scriptScale() {
        return this.scriptScale;
    }

    public float scriptDx() {
        return scriptDx;
    }

    public float scriptDy() {
        return scriptDy;
    }

    public void setScriptScale(double scale) {
        this.scriptScale = (float) scale;
    }

    public void setScriptDx(double dx) {
        this.scriptDx = (float) dx;
    }

    public void setScriptDy(double dy) {
        this.scriptDy = (float) dy;
    }

    public void setFontsize(double fontsize) {
        this.fontsize = (float) fontsize;
    }

    public float fs() {
        return this.fontsize;
    }

    public float fontsize() {
        return this.fontsize;
    }

    public float scaledFontsize() {
        return fontsize * (scaleY() != 0 ? scaleY() : 1);
    }

    public float fontScale() {
        return scriptScale * fontsize * (isVerticalMode() ? -1 : 1);
    }

    public int scaledRoundFS() {
        return Math.round(scaledFontsize());
    }

    public float scaledFontsizeX() {
        return fontsize * (scaleX() != 0 ? scaleX() : 1);
    }

    public void setFont(String fontname) {
        this.fontname = fontname;
    }

    public void setFont(SVGFont font) {
        this.fontname = font.fontname();
    }

    public SVGFont font() {
        OCDDocument ocd = this.document();
        if (ocd == null) {
            OCDNode p = this;
            StringBuilder sb = new StringBuilder();
            while ((p = p.parent()) != null)
                sb.append(OCD.isGroup(p) ? ((OCDGroup) p).type : p.tag);
            Log.debug(this, ".font - ocd document not found: " + sb.toString() + ", text=" + this.string());
        }
        if (Str.IsVoid(fontname)) {
            fontname = "Helvetica";
            Log.debug(this, ".font - ooops, null or empty fontname: " + this.string() + ", " + doc().fontHandler.map().keys());
        }
        return ocd == null ? null : ocd.fontHandler.needFont(this.fontname, this.string());
    }

    public void addSpace() {
        this.unicodes.append(" ");
    }

    public boolean isSpace(int index) {
        return unicodes.codeAt(index) == SPACE;
    }

    public boolean isSpace() {
        return this.length() == 1 && this.first() == SPACE;
    }

    public boolean isEmptyPath() {
        for (Glyph g : glyphs()) {
            Path3 path = g.path();
            if (path != null && !path.isEmpty())
                return false;
        }
        return true;
    }

    public boolean hasSpace() {
        for (int i = 0; i < unicodes.length(); i++)
            if (unicodes.codeAt(i) == SPACE)
                return true;
        return false;
    }

    public int countSpaces() {
        int counter = 0;
        for (int i = 0; i < unicodes.length(); i++)
            if (unicodes.codeAt(i) == SPACE)
                counter++;
        return counter;
    }

    public boolean endsWithSpace() {
        return this.last() == SPACE;
    }

    public boolean startsWithSpace() {
        return this.first() == SPACE;
    }

    public boolean startsWithHardReturn() {
        return this.first() == HARD_RETURN;
    }

    public boolean startsWithSoftReturn() {
        return this.first() == SOFT_RETURN;
    }

    public boolean startsWithReturn(boolean hard) {
        return hard ? this.first() == HARD_RETURN : this.first() == SOFT_RETURN;
    }

    public boolean startsWithReturn() {
        return this.first() == HARD_RETURN || this.first() == SOFT_RETURN;
    }

    public void clearCharSpaces() {
        this.cs = new float[0];
    }

    public void setCharSpaces(float... cs) {
        this.cs = cs;
    }

    public void inferCS(double width) {
        this.setCharSpaces(inferCS(width, fontsize * xScale, this.glyphs()));
    }

    public static float[] inferCS(double width, double scale, Glyph[] glyphs) {
        int csn = glyphs.length - 1;
        float[] cs = new float[csn + 1];
        if (csn > 0) {
            float w = 0;
            for (Glyph g : glyphs)
                w += g.width();
            w *= scale;
            float dx = (float) ((width - w) / csn);
            dx /= scale;
            for (int i = 0; i < csn + 1; i++)
                cs[i] = dx;
            cs[csn] = 0;
        }
        return cs;
    }

    public float[] cs() {
        return this.cs;
    }

    public float meanCS() {
        return Zen.Array.Mean(charSpaces(true));
    }

    public float[] charSpaces() {
        return this.cs;
    }

    public float[] charSpaces(boolean extended) {
        int size = unicodes.length();
        return cs.length < size ? Zen.Array.expand(cs, cs.length == 0 ? 0 : cs[cs.length - 1], size) : cs;
    }

    public float charSpace(int index) {
        return index < cs.length ? cs[index] : cs.length == 0 ? 0 : cs[cs.length - 1];
    }

    public void setLastCharSpace(float csi) {
        if (lastCharSpace() != csi) {
            this.cs = charSpaces(true);
            this.cs[cs.length - 1] = csi;
        }
    }

    public float lastCharSpace() {
        float csi = 0;
        for (int i = 0; i < cs.length; i++)
            if (!Float.isNaN(cs[i]))
                csi = cs[i];
        return csi;
    }

    public void updateCS(double y, double... x) {
        Point3[] coords = new Point3[x.length];
        for (int i = 0; i < coords.length; i++)
            coords[i] = new Point3(x[i], y);
        updateCS(coords);
    }

    public void updateCS(Collection<Point3> points) {
        updateCS(points.toArray(new Point3[0]));
    }

    public void updateCS(Point3... points) {
        updateCS(new Coords(points));
    }

    public void updateCS(Coords coords) {
        // Log.debug(this, ".updateCS - [" + string() + "]: " + coords.xStrInt());
        this.setXY(coords.x(), coords.y());
        Transform3 tm = this.transform();
        Coords noords = tm.inverse().transform(coords);
        // System.out.println("\n\nrotation="+
        // (int)(this.rotation()*Zen.Math.TO_DEGREE));
        // System.out.println("transform="+transform);
        // System.out.println("text=" + this.string());
        // System.out.println("coords=" + coords.toString());
        // System.out.println("noords=" + noords.toString());
        boolean vMode = isVerticalMode();
        float[] delta = vMode ? noords.derivateY() : noords.derivateX();
        Glyph[] glyphs = this.glyphs();
        if (!this.startsWithHardReturn())
            if (delta.length < glyphs.length - 1) {
                StringBuilder sb = new StringBuilder();
                for (Glyph g : glyphs) {
                    String c = g.code();
                    sb.append(c.length() < 2 ? c : "[" + c + "]");
                }
                Log.warn(this, ".updateCS - cs=" + delta.length + " < glyphs=" + glyphs.length + "-1, font=" + this.fontname + ", text=" + sb.toString());
            }
        this.cs = new float[glyphs.length];
        for (int i = 0; i < cs.length && i < delta.length; i++)
            cs[i] = delta[i] / fontsize - (vMode ? -glyphs[i].height() : glyphs[i].width());
        // Zen.debug(this,
        // ".updateCS - v13="+v13+", fontsize="+fontsize+",
        // cs="+Zen.Array.toString(cs));
    }

    // public int glyphIndex(int unicodeIndex)
    // {
    // Glyph[] glyphs = this.glyphs();
    // int gIndex = 0;
    // int counter = 0;
    // while (counter < unicodeIndex && gIndex < glyphs.length)
    // counter += glyphs[gIndex++].nbOfUnicodes();
    // return counter == unicodeIndex ? gIndex : -gIndex;
    // }

    public Unicodes unicodes() {
        return this.unicodes;
    }

    public void setUnicodes(String unicodes) {
        this.unicodes = new Unicodes(unicodes);
    }

    public void setUnicodes(int... unicodes) {
        this.unicodes = new Unicodes(unicodes);
    }

    public void setUnicodes(Unicodes unicodes) {
        this.unicodes = unicodes;
    }

    public void setAsWhiteSpace() {
        this.setUnicodes(SPACE);
        this.field = null;
    }

    public double[] shifts(Glyph... glyphs) {
        double[] shifts = deltas(glyphs);
        for (int i = 1; i < shifts.length; i++)
            shifts[i] += shifts[i - 1];
        return shifts;
    }

    public double[] deltas(Glyph... glyphs) {
        if (glyphs.length == 0)
            glyphs = this.glyphs();
        boolean vMode = isVerticalMode();
        float csi = 0; // char space at textLineIndex i
        double[] deltas = new double[glyphs.length];
        for (int i = 0; i < glyphs.length; i++) {
            if (i < cs.length && !Float.isNaN(cs[i]))
                csi = cs[i];
            deltas[i] = ((vMode ? -glyphs[i].height() : glyphs[i].width()) + csi) * fontsize;
        }
        return deltas;
    }

    public Coords coords(Glyph... glyphs) {
        return canon == null ? this.coords(shifts(glyphs)) : canon.coords;
    }

    public Coords coords(double[] shifts) {
        return normCoords(shifts).transform(transform());
    }

    public Coords normCoords(double[] shifts) {
        boolean vMode = isVerticalMode();
        Coords c = new Coords(0.0, 0.0);
        for (int i = 0; i < shifts.length; i++)
            if (vMode)
                c.add(0, shifts[i]);
            else
                c.add(shifts[i], 0);
        return c;
    }

    public Point3 coordAt(int index) {
        boolean vMode = isVerticalMode();
        float csi = 0;
        Glyph[] glyphs = this.glyphs();
        double shift = 0;
        for (int i = 0; i < glyphs.length && i < index; i++) {
            if (i < cs.length && !Float.isNaN(cs[i]))
                csi = cs[i];
            shift += ((vMode ? -glyphs[i].height() : glyphs[i].width()) + csi) * fontsize;
        }
        return (vMode ? new Point3(0, shift) : new Point3(shift, 0)).transform(transform());
    }

    public Coords coordsAt(int... indexes) {
        if (indexes == null || indexes.length == 0)
            return new Coords();

        Coords coords = new Coords(indexes.length);
        boolean vMode = isVerticalMode();
        float csi = 0;
        Glyph[] glyphs = this.glyphs();
        double shift = 0;

        int index = 0;
        if (indexes[index] == 0)
            coords.set(index++, new Point3(0, 0));

        for (int i = 0; i < glyphs.length && index < indexes.length; i++) {
            if (i < cs.length && !Float.isNaN(cs[i]))
                csi = cs[i];
            shift += ((vMode ? -glyphs[i].height() : glyphs[i].width()) + csi) * fontsize;

            if (indexes[index] - 1 == i)
                coords.set(index++, vMode ? new Point3(0, shift) : new Point3(shift, 0));
        }
        return coords.transform(transform());
    }

    public String compressCS(float[] floats) {

        StringBuilder sb = new StringBuilder(floats.length * 2);
        for (int i = 0; i < floats.length; i++)
            sb.append(i == 0 ? "" : " ").append(Math.round(floats[i] * CS_XML_FACTOR));

        return sb.toString();
    }

    public static float[] uncompressCS(String string) {
        FloatArray floats = new FloatArray(string.length() * 4);
        for (String token : Str.Split(string.trim())) {
            int i = token.indexOf("#");
            if (i > 0) {
                int repeat = Nb.Int(token.substring(0, i), 1);
                float value = Nb.Float(token.substring(i + 1)) / CS_XML_FACTOR;
                floats.add(Zen.Array.instance(repeat, value));
            } else
                floats.add(Nb.Float(token, 0) / CS_XML_FACTOR);
        }
        return floats.array();
    }

    @Override
    public Collection<? extends OCDNode> writeAttributes(Xml xml) {
        OCDPage page = this.page();
        if (this.unicodes.isEmpty())
            this.unicodes.append(SPACE);
        this.cs = cs == null ? new float[unicodes.length()] : Zen.Array.expand(cs, unicodes.length());
        State state = page.content(false).state();
        this.writeXmlID(xml);
        this.writeXmlName(xml);
        this.writeXmlClip(xml, state);
        this.writeXmlBlend(xml, state);
        this.writeXmlZOrder(xml, state);
        this.writeXmlTransform(xml, state);
        if (!this.isSpace())
            this.writeXmlFillAndStroke(xml, state);
        xml.write("field", field);
        xml.write("mode", xml.equals(state.MODE, mode()) ? null : (state.MODE = mode()));
        xml.write("fontsize", xml.equals(state.FONTSIZE, fontsize) ? Float.NaN : (state.FONTSIZE = fontsize));
        // Log.debug(this, ".writeAttributes - font="+this.fontname);
        xml.write("font", xml.equals(state.FONT, fontname) ? null : (state.FONT = fontname));
        xml.write("decoration", decoration == null || decoration.equals(CSS._normal) ? null : decoration);
        xml.write("script-dx", xml.isZero(scriptDx) ? Float.NaN : scriptDx);
        xml.write("script-dy", xml.isZero(scriptDy) ? Float.NaN : scriptDy);
        xml.write("script-scale", xml.isOne(scriptScale) ? Float.NaN : scriptScale);

        float[] ncs = cs;
        if (!xml.isZero(ncs))
            xml.write("cs", compressCS(ncs));

        // xml.write("ocr", ocr == null || ocr.boxIDs == null ? null :
        // ocr.toString());
        // if (ocr != null && ocr.box != null)
        // xml.write("box", ocr.boxString());

        props.writeAttributes(xml);

        String cdata = this.unicodes.toCharRef();
        if (eol)
            cdata += "&#" + (int) SOFT_RETURN + ";";// used as textline replacement
        xml.write("d", cdata, false);

        Coords coords = coords();
        state.X = coords.lastX();
        state.Y = coords.lastY();
        return this.children();
    }

    @Override
    public void readAttributes(DomNode dom) {
        State state = this.page().content(false).state();

        if (dom.has("d"))
            this.unicodes = new Unicodes(dom.value("d"));
        else
            this.unicodes = new Unicodes(dom.cdata());// since Java XML API does
        // unescaping itself

        if (this.eol = unicodes.last() == SOFT_RETURN)
            this.unicodes.removeLast();

        if (this.unicodes.isEmpty())
            this.unicodes.append(SPACE);

        this.readXmlID(dom);
        this.readXmlName(dom);
        this.readXmlClip(dom, state);
        this.readXmlBlend(dom, state);
        this.readXmlZOrder(dom, state);
        this.readXmlTransform(dom, state);
        this.readXmlFillAndStroke(dom, state);
        // this.runID = node.integer("run", runID);
        this.field = dom.value("field", null);
        this.mode = state.MODE = dom.value("mode", state.MODE);
        this.fontsize = state.FONTSIZE = dom.real("fontsize", state.FONTSIZE);
        this.fontname = state.FONT = dom.value("font", state.FONT);
        this.decoration = dom.value("decoration", decoration);
        this.scriptDx = dom.real("script-dx", scriptDx);
        this.scriptDy = dom.real("script-dy", scriptDy);
        this.scriptScale = dom.real("script-scale", scriptScale);
        // org.w3c.dom.Node unescapes Entity Ref itself;

        this.cs = uncompressCS(dom.value("cs", ""));

        // String ocrData = dom.value("ocr", null);
        // this.ocr = ocrData == null ? null : new OCDOcr(ocrData);
        // String[] vals = dom.values("box");
        // if (vals.length == 4)
        // {
        // if (this.ocr == null)
        // this.ocr = new OCDOcr();
        // this.ocr.setBox(vals);
        // }

        Coords coords = coords();
        state.X = coords.lastX();
        state.Y = coords.lastY();
        props.readAttributes(dom);

        // Log.debug(this, "readAttributes - "+this.uniString());
    }

    public boolean isTextLineLast() {
        OCDTextLine line = textLine();
        return line != null && line.last() == this;
    }

    public boolean isTextBlockLast() {
        OCDTextBlock block = textBlock();
        return block != null && block.lastText() == this;
    }

    public OCDTextLine textLine() {
        return OCD.isGroup(parent(), OCDGroup.TEXTLINE) ? (OCDTextLine) parent : null;
    }

    public OCDTextBlock textBlock() {
        OCDTextLine line = textLine();
        return line == null ? null : line.textBlock();
    }

    public boolean isOnly(char c) {
        for (int i = 0; i < unicodes.length(); i++)
            if (unicodes.codeAt(i) != c)
                return false;
        return true;
    }

    public boolean isBold() {
        String norm = fontname.toLowerCase().replace("-", "_").replace(" ", "_");
        return norm.contains("_bold");
    }

    public boolean isItalic() {
        String norm = fontname.toLowerCase().replace("-", "_").replace(" ", "_").replace("bold", "");
        return norm.contains("_italic") || norm.contains("_oblique");
    }

    public boolean isSubscript() {
        if (scriptDy > 0.1)
            return true;
        OCDTextLine line = this.textLine();
        if (line == null)
            return false;
        double lineHeight = line.bounds().height;
        double tokenHeight = this.bounds().height;
        if (lineHeight > tokenHeight && tokenHeight / lineHeight < 0.68)// 0.68 for
            // Fred
            if (this.bounds().cy() > line.bounds().cy())
                return true;
        return false;
    }

    public boolean isSuperscript() {
        if (scriptDy < -0.1)
            return true;
        OCDTextLine line = this.textLine();
        if (line == null)
            return false;
        double lineHeight = line.bounds().height;
        double tokenHeight = this.bounds().height;
        if (lineHeight > tokenHeight && tokenHeight / lineHeight < 0.68)// 0.68 for
            // Fred
            if (this.bounds().cy() < line.bounds().cy())
                return true;
        return false;
    }

    public boolean isEmpty() {
        return unicodes.isEmpty();
    }

    public int codeAt(int index) {
        return unicodes.codeAt(index);
    }

    public char charAt(int index) {
        return unicodes.charAt(index);
    }

    public int length() {
        return this.unicodes.length();
    }

    public void setContent(String content) {
        this.unicodes = new Unicodes(content);
    }

    public boolean isFont(OCDText token) {
        return this.fontname.equals(token.fontname);
    }

    public float rotation() {
        return (float) transform().rotation();
    }

    public Point3 firstPoint() {
        return new Point3(x(), y());
    }

    public Point3 lastPoint() {
        return new Point3(lastX(), lastY());
    }

    public float x(int index) {
        return coords().x(index);
    }

    public float y(int index) {
        return coords().y(index);
    }

    public float lastX() {
        return coords().lastX();
    }

    public float lastY() {
        return coords().lastY();
    }

    public int last() {
        return unicodes.last();
    }

    public int first() {
        return unicodes.first();
    }

    public int nbOfDigits() {
        int digits = 0;
        for (char c : string().toCharArray())
            if (Character.isDigit(c))
                digits++;
        return digits;
    }

    public double percentageOfDigits() {
        return nbOfDigits() / (double) string().trim().length();
    }

    public int nbOfChars() {
        return unicodes.length();
    }

    public int nbOf(String c) {
        String text = this.glyphString();
        int counter = 0;
        int index = 0;
        while ((index = text.indexOf(c, index)) > -1) {
            counter++;
            index++;
        }
        return counter;
    }

    public double width() {
        return lastX() - x();
    }

    public Rectangle3 bounds(int start, int end) {
        float ascent = SVGFont.DEFAULT_ASCENT;
        float descent = SVGFont.DEFAULT_DESCENT;
        SVGFont font = font();
        if (font != null) {
            ascent = font.ascent(ascent);
            descent = font.descent(descent);
        }
        Coords coords = this.coords();
        Rectangle3 r = new Rectangle3(0, -ascent * fontsize, 0, (ascent - descent) * fontsize);
        Shape shape1 = transform().moveTo(start <= 0 || start > coords.size() - 1 ? coords.first() : coords.get(start)).transform(r);
        Shape shape2 = transform().moveTo(end <= 0 || end > coords.size() - 1 ? coords.last() : coords.get(end)).transform(r);
        r = new Rectangle3(shape1.getBounds2D());
        r.add(shape2.getBounds2D());
        // Zen.debug(this, ".bounds - ascent=" + ascent + ", descent=" + descent +
        // ", width=" + r.width + ", height=" + r.height + ", fontsize=" +
        // fontsize);
        if (r.width <= 0)// may happen with "l", breaking intersection checks
            r.width = 0.001f;
        if (r.height <= 0)// may happen with "-", breaking intersection checks
            r.height = 0.001f;
        return r;
    }

    @Override
    public Rectangle3 bounds() {
        return canon != null ? canon : this.bounds(0, -1);
    }

    public Rectangle3 textBounds() {
        return bounds(startsWithSpace() ? 1 : 0, endsWithSpace() ? length() - 1 : length());
    }

    @Override
    public Rectangle3 bounds(boolean bbox) {
        if (bbox) {
            Rectangle3 box = null;
            try {
                Glyph[] glyphs = this.glyphs();
                Coords coords = coords();
                for (int i = 0; i < glyphs.length; i++) {
                    Glyph glyph = glyphs[i];
                    Path3 path = transform().moveTo(coords.pointAt(i)).transform(glyph.path(scriptScale * fontsize * (isVerticalMode() ? -1 : 1)));
                    box = box == null ? path.bounds().copy() : box.include(path.bounds());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return box;
        } else
            return bounds();
    }

    public Rectangle3[] charactersBBox() {
        try {
            Glyph[] glyphs = glyphs();
            Rectangle3[] box = new Rectangle3[glyphs.length];
            Coords coords = coords();
            for (int i = 0; i < glyphs.length; i++) {
                Glyph glyph = glyphs[i];
                Path3 path = transform().moveTo(coords.pointAt(i)).transform(glyph.path(scriptScale * fontsize * (isVerticalMode() ? -1 : 1)));
                box[i] = path.bounds().copy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Rectangle3[0];
    }

    public Point3 origin() {
        return new Point3(this.x(), this.y());
    }

    // public Shape endCaret()
    // {
    // float ascent = SVGFont.DEFAULT_ASCENT;
    // float descent = SVGFont.DEFAULT_DESCENT;
    // SVGFont font = font();
    // if (font != null)
    // {
    // ascent = font.ascent(ascent);
    // descent = font.descent(descent);
    // }
    // Coords coords = this.coords();
    // Line3 l = new Line3(0, -ascent * fontsize, 0, -descent * fontsize);
    // return transform().moveTo(coords.get(coords.size() - 1)).transform(l);
    // }
    @Override
    public Shape3 path() {
        return shape();
    }

    @Override
    public Shape3 shape() {
        double rotation = transform().rotation();
        return rotation == 0.0 ? bounds() : Transform3.rotateInstance(rotation).transform(bounds());
    }

    public float glyphsWidth() {
        float w = 0;
        for (Glyph g : glyphs())
            w += g.width();
        return w;
    }

    public Glyph[] glyphs() {
        SVGFont font = this.font();
        if (font == null) {
            Font3 font3 = Font3.Seek(fontname, null);
            if (font3 == null) {
                font3 = Font3.SANS_FONT;
                Str low = new Str(fontname.toLowerCase());
                if (low.containsOne("times"))
                    font3 = Font3.SERIF_FONT;
                else if (low.containsOne("mono", "courier"))
                    font3 = Font3.MONO_FONT;
            }
            font3 = font3.derive(1);
            SVGGlyph[] glyphs = new SVGGlyph[this.unicodes.length()];
            for (int i = 0; i < glyphs.length; i++) {
                int c = unicodes.charAt(i);
                if (c < 32)
                    glyphs[i] = new SVGGlyph(null, "" + unicodes.charAt(i), new Path3(), 0, (int) unicodes.charAt(i));
                else if (font3.canDisplay(unicodes.characters()))
                    glyphs[i] = font3.svgGlyph(c);
                else
                    glyphs[i] = new SVGGlyph(null, "" + unicodes.charAt(i), new Path3(new Circle3(0, 0, 0.01)), 0.5f, (int) unicodes.charAt(i));
            }
            return glyphs;
        }
        return font.glyphs(unicodes.characters());
    }

    public String string() {
        return unicodes.stringValue();
    }

    public String glyphString() {
        StringBuilder sb = new StringBuilder();
        for (Glyph glyph : this.glyphs())
            sb.append(glyph.unicode());
        return sb.length() == 0 ? this.unicodes.stringValue() : sb.toString();
    }

    public String htmlString() {
        return htmlString(null).toString();
    }

    public StringBuilder htmlString(StringBuilder sb) {
        if (sb == null)
            sb = new StringBuilder();
        Glyph[] glyphs = glyphs();
        if (glyphs.length == 0)
            sb.append(unicodes.stringValue());
        else
            for (Glyph glyph : glyphs)
                sb.append(CharRef.Html(glyph.unicode()));
        return sb;
    }

    public int[] glyphUnicodes() {
        return glyphString().codePoints().toArray();
    }

    @Override
    public String sticker() {
        return glyphString();
    }

    @Override
    public String toString() {
        OCDPage page = page();
        if (page != null)
            page.content(false).state().clear();
        return "OCDText[" + this.unicodes.stringValue() + "]" + "\nCodes[" + this.unicodes.toIntegerString() + "]" + "\nClassID[" + this.groupID + "]"
                + "\nBounds" + this.bounds() + "\nParent[" + (parent == null ? "null" : parent.sticker()) + "]" + "\n\nXML" + Xml.toString(this) + "\n";
    }

    public OCDPath[] ocdPaths() {
        Path3[] paths = paths();
        OCDPath[] ocdPaths = new OCDPath[paths.length];
        for (int i = 0; i < paths.length; i++) {
            OCDPath path = new OCDPath(this.parent());
            path.path = paths[i];
            path.fillColor = this.fillColor;
            path.strokeColor = this.strokeColor;
            path.stroke = this.stroke == null ? null : this.stroke.copy();
            path.clipID = this.clipID;
            path.zOrder = this.zOrder;
            path.setTransform(1, 0, 0, 1, 0, 0);
            ocdPaths[i] = path;
        }
        return ocdPaths;
    }

    protected Path3 path(Glyph glyph) {
        if (scriptDx == 0 && scriptDy == 0)
            return glyph.path(isVerticalMode() ? -fontsize : fontsize);
        else {
            Point3 origin = isVerticalMode() ? glyph.vertOrigin() : new Point3();
            return glyph.path(1).translate(scriptDx - origin.x, scriptDy + origin.y).scale(fontsize);
        }
    }

    public Path3[] paths() {
        List3<Path3> paths = new List3<>();
        try {
            Glyph[] glyphs = this.glyphs();
            Coords coords = coords();
            for (int i = 0; i < glyphs.length; i++)
                paths.add(transform().moveTo(coords.pointAt(i)).transform(path(glyphs[i])));

        } catch (Exception e) {
            Log.warn(this, ".convertToPaths - exception thrown while converting text to paths: " + this.string());
            e.printStackTrace();
        }
        return paths.toArray(new Path3[0]);
    }

    public Rectangle3[] glyphBounds() {
        Path3[] paths = paths();
        Rectangle3[] bounds = new Rectangle3[paths.length];
        for (int i = 0; i < paths.length; i++)
            bounds[i] = paths[i].bounds();
        return bounds;
    }

    @Override
    public void paint(Graphics3 g, OCD.ViewProps props) {
        if (!props.use_fonts && DISABLED_FONT == null)
            DISABLED_FONT = Font3.calibri(1);
        this.paintClip(g, props);
        this.paintBlend(g, props);
        if (props.paint_text)
            try {
                g.setStroke(this.stroke());

                Glyph[] glyphs = this.glyphs();
                Transform3 otm = g.transform();
                double[] shifts = shifts();
                Coords coords = coords(shifts);

                Transform3 tm = this.transform();
                float fs = scriptScale * fontsize;
                for (int i = 0; i < glyphs.length; i++) {
                    Glyph glyph = glyphs[i];

                    String chars = glyph.code();

                    g.setTransform(otm.concat(tm.moveTo(coords.pointAt(i))));

                    Path3 path = path(glyph);

                    if (!props.use_fonts)
                        path = DISABLED_FONT.glyph(chars).transform(fs);
                    if (isFilled() || !props.use_alphas) {
                        Color3 color = props.use_colors ? fillColor() : fillColor().grayColor();
                        color = props.use_alphas ? color : color.alpha(1);
                        if (props.linkColor != null && page().annots().type(OCDAnnot.TYPE_LINK).isAnnot(coords.pointAt(i)))
                            color = props.linkColor;
                        g.setColor(color);
                        g.fill(path);
                    }
                    if (isStroked()) {
                        g.setColor(props.use_colors ? strokeColor() : strokeColor().grayColor());
                        g.draw(path);
                    }
                    if (decoration != null) {
                        double[] deltas = deltas();
                        g.setStroke(new Stroke3(fontsize / 20));
                        if (decoration.contains("overline"))
                            g.draw(new Line3(0, -4 * fontsize / 5, deltas[i], -4 * fontsize / 5));
                        if (decoration.contains("line-through"))
                            g.draw(new Line3(0, -fontsize / 4, deltas[i], -fontsize / 4));
                        if (decoration.contains("underline"))
                            g.draw(new Line3(0, fontsize / 5, deltas[i], fontsize / 5));
                    }

                    if (props.show_spaces && path != null && path.isEmpty() && i + 1 < coords.size())
                        g.fill(new Rectangle3(0, -2 * fs / 3, 0.2 * fs, 2 * fs / 3), props.selectColor.alpha(0.5));
                    if (props.show_runs) {
                        if (i == 0)
                            g.draw(new Line3(0, -3 * fs / 4, 0, fs / 3), props.selectColor.alpha(0.8), 0.75 / scaleX());
                        if (chars.endsWith("\n"))
                            g.fill(new Rectangle3(-fs / 3, -fs / 3, fs / 3, fs / 3), props.selectColor.alpha(0.5));
                    }

                }
                // g.setTransform(otm.concat(tm.moveTo(coords.last())));
                // g.fill(new Circle3(0,0,2/this.scaledFontsize()), Color3.RED);
                g.setTransform(otm);
            } catch (Exception e) {
                Log.warn(this, ".paint - exception thrown while painting word: " + this.string());
                e.printStackTrace();
            }
    }

    // @Override
    public OCDText normalize() {
        if (scaleY() != 1f) {
            Coords coords = this.coords();
            this.fontsize = fontsize * scaleY();
            this.setTransform(scaleX() / scaleY(), shearY(), shearX(), x(), y());
            this.updateCS(coords);
        }
        this.clipID = OCDClip.ID_PAGE;
        return this;
    }

    public List3<OCDText> characterize() {
        Glyph[] glyphs = this.glyphs();
        List3<OCDText> chars = new List3<>();
        if (glyphs.length < 2)
            chars.add(this);
        else {
            Coords coords = coords();
            for (int i = 0; i < glyphs.length; i++) {
                Glyph glyph = glyphs[i];
                OCDText text = new OCDText(this.parent());
                super.copyTo(text);
                // text.ocr = ocr != null && i < ocr.length() ? ocr.get(i) : null;
                text.field = field;
                text.unicodes = new Unicodes(glyph.code());
                text.fontname = fontname;
                text.fontsize = fontsize;
                text.decoration = decoration;
                text.mode = mode;
                text.scriptDx = scriptDx;
                text.scriptDy = scriptDy;
                text.scriptScale = scriptScale;
                Point3 p = coords.pointAt(i);
                // text.updateCS(p, new Point3(p.x+glyph.width()*text.scaledFontsize(),
                // p.y));
                text.updateCS(p, coords.pointAt(i + 1));
                chars.add(text);
            }
        }
        return chars;
    }

    public void copyTo(OCDText text) {
        super.copyTo(text);
        text.unicodes = unicodes.copy();
        text.fontname = fontname;
        text.fontsize = fontsize;
        text.decoration = decoration;
        text.mode = mode;
        text.scriptDx = scriptDx;
        text.scriptDy = scriptDy;
        text.scriptScale = scriptScale;
        text.cs = Zen.Array.copy(cs);
        text.field = field;
        // if (ocr != null)
        // text.ocr = ocr.copy();

        if (canon != null) {
            text.canonize();
            canon.copyTo(text.canon);
        }
    }

    @Override
    public OCDText copy() {
        OCDText text = new OCDText(parent());
        this.copyTo(text);
        return text;
    }

    public OCDText ensureFont() {
        OCDDocument ocd = this.document();
        if (ocd != null)
            this.fontname = ocd.needFont(this.fontname, this.unicodes.string());
        return this;
    }

    public List3<OCDText> splitByCharSpaces(double csMax) {
        List3<OCDText> texts = new List3<>();
        texts.add(this);
        OCDText text = this;
        // Log.debug(this, ".splitSpaces - " + this.string() + ", cs=" +
        // Arrays.toString(cs));
        boolean split;
        this.trimFirstSpaces();
        if (this.isEmpty())
            return texts;

        do {
            split = false;
            float[] cs = text.charSpaces(true);
            if (cs[cs.length - 1] > 1) {
                cs[cs.length - 1] = 0;
                text.setCharSpaces(cs);
            }
            for (int i = 0; i < cs.length - 1; i++)
                if (text != null && cs[i] > csMax && (text = text.split(i + 1)) != null) {
                    this.trimLastSpaces();
                    texts.add(text);
                    split = true;
                    break;
                }

        } while (split);

        return texts;
    }

    public OCDText split(int index) {
        if (index < 1 || index >= this.length())
            return null;

        Coords coords = this.coords();
        OCDText split = this.copy();

        String text = this.string();

        int[] codes = this.unicodes.ints();
        int[] codesA = new int[index];
        int[] codesB = new int[codes.length - index];
        for (int i = 0; i < codesA.length; i++)
            codesA[i] = codes[i];
        for (int i = 0; i < codesB.length; i++)
            codesB[i] = codes[i + index];
        this.setUnicodes(codesA);
        // this.unicodes.append(" ");
        split.setUnicodes(codesB);

        float[] cs = this.charSpaces(true);

        float[] csA = new float[index];
        float[] csB = new float[cs.length - index];
        for (int i = 0; i < csA.length; i++)
            csA[i] = cs[i];
        for (int i = 0; i < csB.length; i++)
            csB[i] = cs[i + index];

        csA[csA.length - 1] = 0;
        this.setCharSpaces(csA);
        split.setCharSpaces(csB);

        split.setXY(coords.get(index));

        // Log.debug(this, ".split - index=" + index + ", text=" + text + ", cs=" +
        // Arrays.toString(csA));

        // if (this.ocr != null && split.ocr != null)
        // {
        // ocr.boxIDs = null;
        // split.ocr.boxIDs = null;
        // split.ocr.box = new Rectangle3(true, split.x(), ocr.box.y,
        // ocr.box.maxX(), ocr.box.maxY());
        // ocr.box = new Rectangle3(true, ocr.box.x, ocr.box.y, split.x(),
        // ocr.box.maxY());
        // }

        return split;

    }

    public OCDText trimFirstSpaces() {
        while (unicodes.startWithSpace()) {
            Coords coords = this.coords();
            this.unicodes.trim(1, unicodes.length());
            coords.removeFirst();
            this.updateCS(coords);
        }
        return this;
    }

    public OCDText trimLastSpaces() {
        while (unicodes.endsWithSpace()) {
            int size = unicodes.length() - 1;
            this.cs = Zen.Array.trim(charSpaces(true), 0, size);
            this.unicodes.trim(0, size);
        }
        return this;
    }

    public OCDText trim(int start) {
        return trim(start, length());
    }

    public OCDText trim(int start, int end) {
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (start < 0)
            start = 0;
        if (end < 0 || end > this.length())
            end = this.length();
        this.cs = Zen.Array.trim(charSpaces(true), start, end);
        this.unicodes.trim(start, end);
        return this;
    }

    public OCDText delete(int start) {
        return delete(start, length());
    }

    public OCDText delete(int start, int end) {
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (start < 0)
            start = 0;
        if (end < 0 || end > length())
            end = this.length();
        if (start == end)
            return this;
        else if (start == 0 && end == length()) {
            this.delete();
            return null;
        } else {
            this.unicodes.delete(start, end);
            this.clearCharSpaces();
        }
        return this;
    }

    public String string(int start) {
        return string(start, length());
    }

    public String string(int start, int end) {
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (start < 0)
            start = 0;
        if (end < 0 || end > length())
            end = length();

        return start == end ? "" : (start == 0 && end == length() ? glyphString() : glyphString().substring(start, end));
    }

    public void insert(int index, int... chars) {
        this.cs = new float[0];
        this.unicodes.insert(index, chars);
    }

    public FxPath fx() {
        return fx(false, false, -1);
    }

    public FxPath fx(boolean showSpaces, boolean disableFont, double alpha) {
        Glyph[] glyphs = this.glyphs();
        Coords coords = this.normCoords(shifts());

        Path3 path = new Path3();
        float fs = scriptScale * fontsize;
        for (int i = 0; i < glyphs.length; i++) {
            Glyph glyph = glyphs[i];

            Point3 p = coords.pointAt(i);

            Path3 sub = disableFont ? Font3.SERIF_FONT_1.glyph(glyph.unicode()).scale(fontsize) : path(glyph);

            sub = sub.translate(p.x, p.y);

            if (showSpaces && sub.isEmpty()) {
                path.append(new Circle3(0.1 * fontsize + p.x, -0.25 * fontsize + p.y, 0.08 * fontsize), false);
            } else
                path.append(sub, false);
        }

        FxPath fx = path.fx();
        if (isStroked()) {
            stroke().into(fx);
            fx.setStroke(strokeColor().fx());
        } else
            fx.setStroke(null);

        if (isFilled() || alpha > 0)
            fx.setFill(alpha > 0 ? fillColor().alpha(alpha).fx() : fillColor().fx());
        else
            fx.setFill(null);

        // Log.debug(this,
        // ".fx - "+this.string()+": "+transform()+", sx="+this.scaleX());
        fx.getTransforms().add(this.transform().fx());
        // fx.setClip(this.fxClip());

        return fx;
    }

    public void moveTo(double x, double y) {
        Rectangle3 box = this.bounds();
        double dx = x - box.x;
        double dy = y - box.y;

        this.setXY(this.x() + dx, this.y() + dy);
    }

    public boolean isLeftFrom(OCDText text, double maxDist) {
        Rectangle3 r0 = this.bounds();
        Rectangle3 r1 = text.bounds();
        return r0.x > r1.x || !r0.hasOverlapY(r1) ? false : r1.minX() - r0.maxX() < maxDist;
    }

    public float computeWidth() {
        return computeWidth(0);
    }


    public float computeWidth(float charspace) {
        float w = 0;
        for (Glyph glyph : glyphs())
            w += (glyph.width() + charspace) * scriptSize();
        return w;
    }

    public static OCDText Cast(Object o) {
        return o instanceof OCDText ? (OCDText) o : null;
    }


}
