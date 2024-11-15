package sugarcube.formats.ocd;

import sugarcube.common.system.Prefs;
import sugarcube.common.system.reflection.Annot._Bean;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Set3;
import sugarcube.common.data.collections.Str;
import sugarcube.common.data.Base;
import sugarcube.common.graphics.geom.Dimension3;
import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.geom.Path3.Op;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Metric;
import sugarcube.common.interfaces.Taggable;
import sugarcube.common.interfaces.Unjammable;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.SoftVersion;
import sugarcube.common.system.reflection.Bean;
import sugarcube.common.data.xml.Nb;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlDecimalFormat;
import sugarcube.formats.pdf.reader.Dexter;
import sugarcube.formats.ocd.analysis.DexterProps;
import sugarcube.formats.ocd.writer.OCDWriter;
import sugarcube.formats.ocd.objects.*;
import sugarcube.formats.ocd.objects.handlers.OCDPageHandler;

import java.awt.*;
import java.awt.geom.PathIterator;

public class OCD implements Unjammable
{

    public static final SoftVersion VERSION = new SoftVersion("OCD", "2023-08-09", 2, 0, 6);
    //public static final String DEXTER_CLASSPATH = "sugarcube.Dexter";
    // public static final Color3 MENU_BG_COLOR = new Color3(250, 250, 250);
    public static final String FILE_EXTENSION = ".ocd";// never change this (do
    // not remove the dot)
    public static final String EXT = FILE_EXTENSION;
    public static final DexterProps canonizerProps = new DexterProps();
    public static final ViewProps displayProps = new ViewProps();

    public static Color3 SELECT_COLOR = Color3.TEAL;
    public static Color3 HIGHLIGHT_COLOR = Color3.GREEN_DARK;

    public static final String COVER_FILENAME = "Cover.jpg";
    // prefixes and directories should be kept as is
    public static final String IMAGES_DIR = "images/";
    public static final String THUMBS_DIR = "thumbs/";
    public static final String AUDIO_DIR = "audio/";
    public static final String VIDEO_DIR = "video/";
    public static final String FONTS_DIR = "fonts/";
    public static final String ADDONS_DIR = "addons/";
    public static final String PAGES_DIR = "pages/";
    //public static final int CS_FACTOR = 1000;

    //public static final StringCache REFS = new StringCache(1000000);
    public static final String URL = "url";
    public static final String REF = "ref";
    public static final String ROLE = "role";
    public static final String NEXT = "next";
    public static final String ITEM = "item";
    // public static final String PARENT = "parent";
    public static final String LIST = "list";
    public static final String HOOK = "hook";// auto-generated to simplify
    // Freddy's life
    //public static final String NEXT_LIST = HOOK;
    public static final String SUB = "sub";
    public static final String END_SUB = "end-sub";
    public static final String END = "end";
    public static final String LABEL = "label";
    public static final String ACT = "act";
    public static final String CLASS_HTML = "class-html";
    public static final String CLASS = "class";
    public static final String CLASS_AUTO_PREFIX = "^";
    public static final String CLASS_ID = "class-rule";
    public static final String UNDEF = "undef";
    public static final String NONE = "none";
    // public static final String TRUE = "true";
    // public static final String FALSE = "false";
    public static final int DPI = 72;
    public static int MAX_NB_OF_DISPLAY_PATHS = 15000;

    public static String AutoID(OCDNode node)
    {
        return node.tag.charAt(0) + Base.x32.random8();
    }

    public static boolean isGroup(OCDNode node, String... type)
    {
        return node != null && (node instanceof OCDGroup) && ((OCDGroup) node).isType(type);
    }

    public static boolean isLayer(OCDNode node, String... names)
    {
        boolean layer = isGroup(node, OCDGroup.LAYER);
        return layer ? (names == null || names.length == 0 ? true : ((OCDLayer) node).isName(names)) : false;
    }

    public static boolean isFlow(OCDNode node)
    {
        return isGroup(node, OCDGroup.FLOW);
    }

    public static boolean isTextBlock(OCDNode node)
    {
        return isParagraph(node);
    }

    public static boolean isParagraph(OCDNode node)
    {
        return isGroup(node, OCDGroup.PARAGRAPH);
    }

    public static boolean isColumn(OCDNode node)
    {
        return isGroup(node, OCDGroup.COLUMN);
    }

    public static boolean isTextLine(OCDNode node)
    {
        return isGroup(node, OCDGroup.TEXTLINE);
    }

    public static boolean isGraphics(OCDNode node)
    {
        return isGroup(node, OCDGroup.CONTENT);
    }

    public static boolean isPath(Taggable node)
    {
        return isTag(node, OCDPath.TAG);
    }

    public static boolean isImage(Taggable node)
    {
        return isTag(node, OCDImage.TAG);
    }

    public static boolean isText(Taggable node)
    {
        return isTag(node, OCDText.TAG);
    }

    public static boolean isClip(Taggable node)
    {
        return isTag(node, OCDClip.TAG);
    }

    public static boolean isGroup(Taggable node)
    {
        return isTag(node, OCDGroup.TAG);
    }

    public static boolean isGroupContent(Taggable node)
    {
        return isTag(node, OCDGroup.TAG) && node instanceof OCDContent;
    }

    public static boolean isPageContent(Taggable node)
    {
        return isTag(node, OCDContent.TAG);
    }

    public static boolean isAnnot(Taggable node)
    {
        return isTag(node, OCDAnnot.TAG);
    }

    public static boolean isTag(Taggable node, String tag)
    {
        return node != null && tag != null && tag.equals(node.tag());
    }

    public static class ViewProps extends Bean implements Unjammable
    {
        public ViewProps()
        {
        }

        public ViewProps(double scale)
        {
            this.scale = (float) scale;
        }

        public String box = OCDAnnot.ID_VIEWBOX;
        public float scale = 1f;
        // primitives rendering constraints
        @_Bean(name = "Enable Fonts")
        public boolean use_fonts = true;
        @_Bean(name = "Enable Clipping")
        public boolean use_clips = true;
        @_Bean(name = "Enable Colors")
        public boolean use_colors = true;
        @_Bean(name = "Enable Strokes")
        public boolean use_strokes = true;
        @_Bean(name = "Enable Alphas")
        public boolean use_alphas = true;
        // paints (i.e., renders) primitives
        @_Bean(name = "Paint Text")
        public boolean paint_text = true;
        @_Bean(name = "Paint Images")
        public boolean paint_images = true;
        @_Bean(name = "Paint Graphics")
        public boolean paint_graphics = true;
        @_Bean(name = "Paint Chessboard")
        public boolean paint_chessboard = false;
        // shows (i.e., highlights) elements after rendering
        @_Bean(name = "View Clips")
        public boolean show_clips = false;
        @_Bean(name = "View Textblocks")
        public boolean show_blocks = false;
        @_Bean(name = "View Textlines")
        public boolean show_lines = false;
        @_Bean(name = "View Spaces")
        public boolean show_spaces = false;
        @_Bean(name = "View Textruns")
        public boolean show_runs = false;
        @_Bean(name = "View Annotations")
        public boolean show_annots = false;
        @_Bean(name = "View Viewbox")
        public boolean show_views = false;
        // links color
        public Color3 linkColor = null;
        // selection and highlight colors
        public Color3 selectColor = SELECT_COLOR;
        public Color3 highlightColor = HIGHLIGHT_COLOR;
        // display a grid overlay
        public double show_grid = -1;
        public Rectangle3 cropbox = null;

        public Transform3 scale()
        {
            return Transform3.scaleInstance(scale < 0.001 ? 0.001 : scale);
        }

        public static ViewProps Scale(double scale)
        {
            return new ViewProps(scale);
        }
    }

    public static String path2xml(Shape path, XmlDecimalFormat format)
    {
        return path2xml(false, path, format);
    }

    public static String path2xml(boolean shiftToOrigin, Shape path, XmlDecimalFormat format)
    {
        StringBuilder sb = new StringBuilder();
        float ox = 0f;
        float oy = 0f;
        float x = 0f;
        float y = 0f;
        PathIterator it = path.getPathIterator(null);
        float[] p = new float[6];
        if (!it.isDone())
            do
            {
                Op op = Op.type(it.currentSegment(p));
                sb.append(op.code());
                switch (op)
                {
                    case MOVE:
                        if (shiftToOrigin)
                        {
                            sb.append(" 0 0");
                            shiftToOrigin = false;
                        } else
                        {
                            sb.append(" ").append(Xml.toString(p[0] - x, format));
                            sb.append(" ").append(Xml.toString(p[1] - y, format));
                        }
                        ox = p[0];
                        oy = p[1];
                        x = p[0];
                        y = p[1];
                        break;
                    case LINE:
                        sb.append(" ").append(Xml.toString(p[0] - x, format));
                        sb.append(" ").append(Xml.toString(p[1] - y, format));
                        x = p[0];
                        y = p[1];
                        break;
                    case QUAD:
                        sb.append(" ").append(Xml.toString(p[0] - x, format));
                        sb.append(" ").append(Xml.toString(p[1] - y, format));
                        sb.append(" ").append(Xml.toString(p[2] - x, format));
                        sb.append(" ").append(Xml.toString(p[3] - y, format));
                        x = p[2];
                        y = p[3];
                        break;
                    case CUBIC:
                        sb.append(" ").append(Xml.toString(p[0] - x, format));
                        sb.append(" ").append(Xml.toString(p[1] - y, format));
                        sb.append(" ").append(Xml.toString(p[2] - x, format));
                        sb.append(" ").append(Xml.toString(p[3] - y, format));
                        sb.append(" ").append(Xml.toString(p[4] - x, format));
                        sb.append(" ").append(Xml.toString(p[5] - y, format));
                        x = p[4];
                        y = p[5];
                        break;
                    case CLOSE:
                        x = ox;
                        y = oy;
                        break;
                }
                it.next();
                if (!it.isDone())
                    sb.append(" ");
            } while (!it.isDone());

        String xml = sb.toString();
        return xml.isEmpty() ? "m 0 0" : xml;
    }

    public static Path3 xml2path(String xml, boolean nullIfError)
    {
        xml = xml.trim();
        if (!xml.isEmpty())
            try
            {
                float ox = 0f;
                float oy = 0f;
                String[] s = Str.Split(xml);
                Path3 path = new Path3(Path3.NONZERO, s.length / 2);
                float[] p = new float[6];

                Op op = null;
                for (int i = 0; i < s.length; i++)
                {
                    int c = s[i].charAt(0);
                    boolean upper = false;

                    if (Character.isUpperCase(c))
                    {
                        upper = true;
                        c = Character.toLowerCase(c);
                    }

                    if (s[i].length() > 0 && (op = Op.code(c)) != null)
                    {
                        for (int di = 0; di < op.size(); di++)
                            p[di] = Nb.Float(s[i + 1 + di]);
                        i += op.size();

                        switch (op)
                        {
                            case MOVE:
                                if (upper)
                                    path.moveTo(path.x = p[0], path.y = p[1]);
                                else
                                    path.moveTo(path.x += p[0], path.y += p[1]);
                                ox = path.x;
                                oy = path.y;
                                break;
                            case LINE:
                                if (upper)
                                    path.lineTo(path.x = p[0], path.y = p[1]);
                                else
                                    path.lineTo(path.x += p[0], path.y += p[1]);
                                break;
                            case CUBIC:
                                if (upper)
                                    path.curveTo(p[0], p[1], p[2], p[3], path.x = p[4], path.y = p[5]);
                                else
                                    path.curveTo(path.x + p[0], path.y + p[1], path.x + p[2], path.y + p[3], path.x += p[4], path.y += p[5]);
                                break;
                            case QUAD:
                                if (upper)
                                    path.quadTo(p[0], p[1], path.x = p[2], path.y = p[3]);
                                else
                                    path.quadTo(path.x + p[0], path.y + p[1], path.x += p[2], path.y += p[3]);
                                break;
                            case CLOSE:
                                path.closePath();
                                path.x = ox;
                                path.y = oy;
                                break;
                        }
                    }
                }
                return path;
            } catch (Exception e)
            {
                Log.error(Path3.class, ".xml2path - parse error: " + e);
            }
        return nullIfError ? null : new Path3();
    }

    public static OCDDocument createOCDFile(File3 file, Dimension3 millimeters, int nbOfPages)
    {
        int dpi = 72;
        OCDDocument ocd = new OCDDocument();
        ocd.setDpi(dpi);
        Rectangle3 bounds = new Metric(Metric.MM, dpi).toPx(millimeters).asRectangle();
        final OCDWriter writer = new OCDWriter(ocd, file).create();
        boolean ok;
        if (ok = writer.writeHeader())
        {
            for (int i = 0; i < nbOfPages; i++)
            {
                OCDPage page = ocd.addPage(PageFilename(i + 1, nbOfPages));
                page.setInMemory(true);// ensures that OCDPage does not try to
                // dynamically load page from unexisting OCD file
                page.setSize(bounds);
                page.annots().addViewboxAnnot(bounds, OCDAnnot.ID_VIEWBOX);
                writer.writeEntry(page);
                page.freeFromMemory();
            }
            writer.writeEntry(ocd);
        }
        writer.close(ok);
        return ocd;
    }

    public static OCDDocument Load(String path)
    {
        return Load(new File3(path));
    }

    public static OCDDocument Load(File3 file)
    {
        if (!file.isExtension(".pdf", OCD.EXT))
        {
            Log.warn(OCD.class, ".convert requires an OCD/PDF file as input: " + file.path());
            return null;
        }

        if (file.isExtension(".pdf"))
            Dexter.Convert(file).close();
        return new OCDDocument(file.extense(OCD.EXT));
    }

    public static Rectangle3 Rect(String data, Rectangle3 def)
    {
        return Rectangle3.fromOCD(data, def);
    }

    public static String TrimClassnameAutoPrefix(String classname)
    {
        return classname != null && classname.startsWith(CLASS_AUTO_PREFIX) ? classname.substring(1) : classname;
    }

    public static boolean Exists(File3 ocdFile)
    {
        return ocdFile != null && ocdFile.exists() && ocdFile.isExt(EXT);
    }

    public static String PageFilename(int pageNb)
    {
        return PageFilename(pageNb, 9999);
    }

    public static String PageFilename(int pageNb, int totalNbOfPages)
    {
        int length = ("" + totalNbOfPages).length();
        if (length < 4)
            length = 4;
        String filename = "" + pageNb;
        while (filename.length() < length)
            filename = "0" + filename;
        return "page_" + filename + Xml.FILE_EXTENSION;
    }

    public static OCDPage[] Pages(OCDPage current, String type, String range)
    {

        OCDPageHandler pages = current.doc().pageHandler;
        OCDPage[] array = null;

        switch (type)
        {
            case "current":
                return new OCDPage[]
                        {current};
            case "all":
                array = pages.array();
                break;
            case "odd":
                array = pages.half(false);
                break;
            case "even":
                array = pages.half(true);
                break;
        }

        if (Str.HasChar(range))
            return new Set3<>(array).intersection(pages.ranges(range.trim().split(","))).toArray(new OCDPage[0]);

        return array;
    }

    public static File3 Temp(String path)
    {
        return Prefs.Temp(path);
    }

    public static String RenameFile(String name)
    {
        return name.replace("  ", " ").replace(" ", "_");
    }

}
