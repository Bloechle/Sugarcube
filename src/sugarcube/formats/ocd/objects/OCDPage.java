package sugarcube.formats.ocd.objects;

import sugarcube.common.data.json.JsonMap;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.*;
import sugarcube.common.data.Base;
import sugarcube.common.graphics.geom.*;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Image3;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.ZipItem;
import sugarcube.common.numerics.Math3;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.common.data.xml.XmlNode;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.document.OCDItem;
import sugarcube.formats.ocd.objects.document.OCDProperties;
import sugarcube.formats.ocd.objects.font.SVGFont;
import sugarcube.formats.ocd.objects.lists.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.OutputStream;
import java.util.List;
import java.util.*;

public class OCDPage extends OCDEntry
{
    public interface SaveListener
    {
        void saving(OCDPage page);
    }

    public static final String TAG = "page";
    public static final String SPREAD_LEFT = "left";
    public static final String SPREAD_RIGHT = "right";
    protected OCDAnnotations annotations;
    protected OCDDefinitions definitions;
    protected OCDPageContent content;
    public float width = 595;
    public float height = 842;
    public float[] columns = new float[0];
    public Margins margins = new Margins();
    public String canon;// canonprops name
    public String type;// "title-page", "TOC"
    public String prod;// oz, pdf,...
    public String spread;
    public String rOrder;// ltr,rtl,ttb,mix
    public File3 tmp;
    private transient boolean modified = false;

    public OCDPage(OCDDocument parent, ZipItem entry)
    {
        this(parent, entry.path());
    }

    public OCDPage(OCDDocument parent, String entryPath)
    {
        super(TAG, parent, entryPath);
        // this.properties = new OCDProperties(this);
        this.annotations = new OCDAnnotations(this);
        this.definitions = new OCDDefinitions(this);
        this.content = new OCDPageContent(this);
        this.id = autoID();
        this.isTreeViewExpanded = true;
    }

    public OCDList links(Rectangle3 box, boolean withAnchors)
    {
        OCDList links = new OCDList();
        for (OCDAnnot annot : annots().links(withAnchors))
            if (box == null || box.overlap(annot.bounds()) > 0.5)
                links.add(annot);
        for (OCDPaintable node : content().links(withAnchors))
            if (box == null || box.overlap(node.bounds()) > 0.5)
                links.add(node);
        return links;
    }

    public OCDMap idMap()
    {
        OCDMap idMap = new OCDMap();
        for (OCDAnnot annot : annotations)
            OCDContent.Populate(idMap, annot);
        for (OCDPaintable node : content)
            OCDContent.Populate(idMap, node);
        return idMap;
    }

    public String autoID(String prefix)
    {
        OCDMap idMap = idMap();
        String id = "";
        int index = 1;
        do
        {
            id = prefix + (index++);
        } while (idMap.has(id));
        return id;
    }

    public OCDPage modify()
    {
        this.modified = true;
        return this;
    }

    @Override
    public boolean modified()
    {
        return modified || super.modified();
    }

    public OCDPages remainingPages(boolean keepCurrent)
    {
        OCDPages pages = new OCDPages();
        boolean doAdd = false;
        for (OCDPage page : this.doc().pageHandler)
        {
            if (doAdd)
                pages.add(page);
            else if (page == this)
            {
                if (keepCurrent)
                    pages.add(page);
                doAdd = true;
            }
        }
        return pages;
    }

    public void setSizeViewBoxesAndClip()
    {
        Rectangle3 box = this.bounds();
        annots().addViewboxAnnot(box.copy().height(box.height * 2), OCDAnnot.ID_CANVASBOX);
        annots().addViewboxAnnot(box, OCDAnnot.ID_VIEWBOX);

        OCDDefinitions defs = this.defs();
        OCDClip clip = new OCDClip(defs, box, OCDClip.ID_PAGE);
        defs.addDefinition(clip);
    }

    public void setBackgroundImage(File3 file)
    {
        this.setBackgroundImage(file.name(), file.bytes());
    }

    public void setBackgroundImage(String filename, byte[] data)
    {
        Image3 img = Image3.Read(data);

        int imageWidth = img.width();
        int imageHeight = img.height();

        // ensures OCDPage does not try to dynamically load page from file
        this.setInMemory(true);
        this.setSize(imageWidth, imageHeight);
        this.setSizeViewBoxesAndClip();

        OCDPageContent ocdContent = this.content();
        OCDImage ocdImage = new OCDImage(ocdContent);
        ocdImage.setSize(imageWidth, imageHeight);
        ocdImage.setFilename(filename);
        ocdImage.setData(data);
        ocdImage.setTransform(1, 0, 0, 1, 0, 0);
        ocdImage.setName("background");
        ocdContent.add(ocdImage);

        this.content().zOrderize(0);
    }

    public boolean isLandscape()
    {
        return width > height;
    }

    public boolean existsTmp()
    {
        return tmp != null && tmp.exists();
    }

    public File3 needTmp()
    {
        if (tmp == null)
            tmp = OCD.Temp(doc().needID() + "_" + page().entryFilename());
        tmp.parent().mkdirs();
        return tmp == null ? tmp = OCD.Temp(doc().needID() + "_" + page().entryFilename()) : tmp;
    }

    public boolean hasBlending()
    {
        return visit((node) ->
        {
            if (node.isPaintableLeaf())
            {
                OCDPaintableLeaf leaf = (OCDPaintableLeaf) node;
                if (leaf.hasBlendMode())
                    return true;
            }
            return false;
        });
    }

    public StringMap<OCDAnnot> layoutAnnots(String... classnames)
    {
        StringSet set = new StringSet(classnames);
        StringMap<OCDAnnot> annots = new StringMap<>();
        for (OCDAnnot annot : this.annotations)
            if (annot.isLayout() && (classnames.length == 0 || set.has(annot.classname())))
                annots.put(annot.id(), annot);
        return annots;
    }

    public StringMap<OCDAnnot> layoutAnnots(Rectangle3 box)
    {
        StringMap<OCDAnnot> annots = new StringMap<>();
        for (OCDAnnot annot : this.annotations)
            if (annot.isLayout() && annot.overlap(box) > 0.5)
                annots.put(annot.id(), annot);
        return annots;
    }

    public String[] layoutClassnames(Rectangle3 box)
    {
        OCDAnnot[] annots = layoutAnnots(box).values().toArray(new OCDAnnot[0]);
        String[] classnames = new String[annots.length];
        for (int i = 0; i < annots.length; i++)
            classnames[i] = annots[i].name();
        Arrays.sort(classnames);
        return classnames;
    }

    @Override
    public String autoID()
    {
        return "pg" + Base.x32.random8();
    }

    @Override
    public OCDItem item()
    {
        return new OCDItem(entryPath, OCDItem.TYPE_PAGE, "width", "" + (int) width, "height", "" + (int) height, "page-type", this.type());
    }

    @Override
    public OCDPage clear()
    {
        this.props.clear();
        this.annotations.clear();
        this.definitions.clear();
        this.content.clear();
        this.id = null;
        return this;
    }

    public OCDGroup<OCDPaintable> createGroup(String id, Rectangle3 bounds, String name)
    {
        return createGroup(id, bounds, name, -1, null);
    }

    public OCDGroup<OCDPaintable> createGroup(String id, Rectangle3 bounds, String name, double inclusion, List3<OCDPaintable> noText)
    {
        OCDGroup<OCDPaintable> group = new OCDGroup<>(OCDGroup.CONTENT, null);
        group.setID(id);
        group.setName(name);
        Iterator<OCDPaintable> graphit = content().iterator();
        while (graphit.hasNext())
        {
            OCDPaintable node = graphit.next();

            boolean include = inclusion <= 0 || inclusion >= 1;
            Rectangle3 bbox = node.bounds();

            if (noText != null && (node.isTextBlock() || node.isText()))
            {
                noText.add(node);
            } else if (include && bounds.contains(bbox) || !include && bounds.intersection(bbox).area() / bbox.area() > inclusion)
            {
                // Log.debug(this, ".createGroup - intersecting node: " + node.tag +
                // ", bound=" + node.bounds());
                graphit.remove();
                group.add(node);
            }
        }
        return group;
    }

    public void deleteTexts()
    {
        for (OCDTextBlock block : content().blocks())
            block.remove();
    }

    public void deletePaths()
    {
        for (OCDPaintable node : content().graphics())
            if (!node.isTextBlock() && !node.isImage())
                node.remove();
    }

    public void removeGroup(OCDGroup<OCDPaintable> group)
    {
        if (group.isTextBlock())
            for (OCDPaintable line : group.children())
                content().add(new OCDTextBlock(content(), (OCDTextLine) line));
        else
            content().addAll(group.children());

        content().remove(group);
    }

    @Override
    public OCDPage page()
    {
        return this;
    }

    public void setOCDPageAnnotations(OCDAnnotations annotations)
    {
        this.ensureInMemory();
        this.annotations = annotations;
    }

    public void setOCDPageDefinitions(OCDDefinitions definitions)
    {
        this.ensureInMemory();
        this.definitions = definitions;
    }

    public List3<SVGFont> fonts()
    {
        Map3<String, SVGFont> fonts = new Map3<>();
        for (OCDText text : this.texts())
            if (!fonts.containsKey(text.fontname()))
                fonts.put(text.fontname(), text.font());
        return fonts.list();
    }

    public List3<OCDImage> images()
    {
        return this.content().allImages();
    }

    public List3<OCDText> texts()
    {
        return this.content().allTexts();
    }

    public OCDBlocks blocks()
    {
        return this.content().blocks();
    }

    public OCDContents subContents()
    {
        return this.content().subContents();
    }

    public String reading()
    {
        return this.content().reading;
    }

    public void setReading(String reading)
    {
        this.content().reading = reading;
    }

    public String spread()
    {
        return isSpreadUndef() ? (this.pageNb() % 2 == 0 ? SPREAD_LEFT : SPREAD_RIGHT) : spread;
    }

    public void setSpread(String spread)
    {
        this.spread = spread;
    }

    public boolean hasSpread()
    {
        String spread = spread();
        return OCDPage.SPREAD_LEFT.equals(spread) || OCDPage.SPREAD_RIGHT.equals(spread);
    }

    public boolean isSpreadLeft()
    {
        return SPREAD_LEFT.equals(spread());
    }

    public boolean isSpreadRight()
    {
        return SPREAD_RIGHT.equals(spread());
    }

    public boolean isSpreadUndef()
    {
        return Zen.isVoid(spread);
    }

    public String type()
    {
        return this.type;
    }

    public String type(String def)
    {
        return this.type == null || this.type.isEmpty() ? def : this.type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public boolean isType(String type)
    {
        return this.type == null ? type == null : this.type.equals(type);
    }

    public String prod()
    {
        return this.prod;
    }

    public void setProd(String prod)
    {
        this.prod = prod;
    }

    public boolean isProd(String prod)
    {
        return this.prod == null ? prod == null : this.prod.equals(prod);
    }

    public String canon()
    {
        return this.canon;
    }

    public void setCanon(String canon)
    {
        this.canon = canon;
    }

    public boolean isCanon(String canon)
    {
        return this.canon == null ? canon == null : this.canon.equals(canon);
    }

    public Dimension3 dimension()
    {
        return new Dimension3(width, height);
    }

    public Rectangle3 viewBox(OCD.ViewProps props)
    {
        return props == null ? new Rectangle3(0, 0, width, height) : viewBox(props.box);
    }

    public Rectangle3 viewBox(String id)
    {
        OCDAnnot annot = props == null ? null : annotations().get(id);
        if (annot == null)
            for (OCDGroup g : content().groups())
                if (g.isID(id))
                    return g.bounds();
        return annot == null ? new Rectangle3(0, 0, width, height) : annot.bounds();
    }

    public void resize(int width, int height)
    {
        this.width = width;
        this.height = height;
        setViewBox(0, 0, width, height);
    }

    public void setViewBox(int x, int y, int width, int height)
    {
        setViewBox(new Rectangle3(x, y, width, height));
    }

    public void setViewBox(Rectangle2D box)
    {
        setViewBox(new Rectangle3(box));
    }

    public void setViewBox(Rectangle3 box)
    {
        setViewBox(box, null);
    }

    public void setViewBox(Rectangle3 box, String id)
    {
        annotations.addViewboxAnnot(box, id == null ? OCDAnnot.ID_VIEWBOX : id);
    }

    public Rectangle3 viewBox()
    {
        OCDDocument doc = document();
        return viewBox(doc == null ? null : doc.viewProps);
    }

    public OCDAnnot annotationAt(Point2D p, String... types)
    {
        for (OCDAnnot annot : annotations().type(types))
            if (annot.viewBounds().contains(p))
                return annot;
        return null;
    }

    public OCDAnnot annotAt(Point2D p, String... types)
    {
        return annotationAt(p, types);
    }

    public OCDGroup<OCDPaintable> graphicsGroupAt(Point2D p)
    {
        for (OCDGroup<OCDPaintable> group : content().groups(OCDGroup.CONTENT, "pgroup"))
            if (group.viewBounds().contains(p))
                return group;
        return null;
    }

    public OCDPaintable paintAt(Point2D p)
    {
        return content().paintAt(p);
    }

    public OCDPaintable nodeAt(Point2D p)
    {
        OCDPaintable node = paintAt(p);
        if (node == null)
            node = annotAt(p);
        return node;
    }

    public OCDTextBlock blockAt(Point2D p)
    {
        if (p == null)
            return null;
        OCDTextBlock block = null;
        for (OCDText text : content.allTexts())
            if (text.bounds().contains(p) && (block = text.textBlock()) != null)
                return block;
        for (OCDTextBlock text : content.paragraphs(-1))
            if (text.bounds().contains(p))
                return text;
        return null;
    }

    public OCDText textAt(Point2D p)
    {
        for (OCDText text : content().allTexts())
            if (text.bounds().contains(p))
                return text;
        return null;
    }

    public OCDTextLine textLineAt(Point2D p)
    {
        for (OCDTextLine line : content.textlines())
            if (line.bounds().contains(p))
                return line;
        return null;
    }

    public OCDText text(int id)
    {
        return text("" + id);
    }

    public OCDText text(String id)
    {
        return (OCDText) this.content.identify(id, OCDText.TAG);
    }

    public StringMap<OCDText> textIDs()
    {
        StringMap<XmlNode> map = content.idMap(null, new StringSet(OCDText.TAG));
        StringMap<OCDText> texts = new StringMap<>();
        for (Map.Entry<String, XmlNode> e : map.entrySet())
            texts.put(e.getKey(), (OCDText) e.getValue());
        return texts;
    }

    public OCDImage imageAt(Point2D p)
    {
        for (OCDImage image : this.content().allImages())
            if (image.viewBounds().contains(p))
                return image;
        return null;
    }

    // public OCDPath pathAt(Point2D p)
    // {
    // for (OCDPath path : this.content.paths(true))
    // if (path.viewBounds().contains(p) && path.viewShape().contains(p))
    // return path;
    // return null;
    // }

    public OCDAnnot annotAt(Point2D p, String type)
    {
        for (OCDAnnot annot : annotations())
            if (annot.isType(type) && annot.bounds().contains(p))
                return annot;
        return null;
    }

    public OCDAnnot annotAt(Point2D p)
    {
        for (OCDAnnot annot : annotations())
            // Log.debug(this, ".annotAt - "+annot);
            if (!annot.isViewbox() && annot.bounds().contains(p))
                return annot;
        return null;
    }

    public OCDImage backgroundImage()
    {
        List3<OCDLayer> layers = content().layers(OCDLayer.NAME_BACKGROUND);
        for (OCDPaintable node : layers.isPopulated() ? layers.first() : content)
            if (node.isImage())
            {
                Rectangle3 box = node.bounds();
                if (box.width > this.width / 2 && box.height > this.height / 2)
                    return node.asImage();
            }

        return null;
    }

    // public OCDGroup groupAt(Point2D p)
    // {
    // for (OCDGroup g : this.content.groups())
    // if (g.viewBounds().contains(p))
    // return g;
    // return null;
    // }

    public OCDAnnotations annotations()
    {
        return this.annots();
    }

    public OCDAnnotations annots()
    {
        return this.annotations;
    }

    public OCDDefinitions definitions()
    {
        return defs();
    }

    public OCDDefinitions defs()
    {
        return this.definitions;
    }

    public OCDPageContent content()
    {
        this.ensureInMemory();
        return this.content;
    }

    public OCDPageContent content(boolean ensureInMemory)
    {
        if (ensureInMemory)
            this.ensureInMemory();
        return this.content;
    }

    @Override
    public Collection<? extends OCDNode> writeAttributes(Xml xml)
    {
        xml.writeID(id);
        xml.write("width", width);
        xml.write("height", height);
        xml.write("top", margins.top);
        xml.write("right", margins.right);
        xml.write("bottom", margins.bottom);
        xml.write("left", margins.left);
        if (columns != null && columns.length > 0)
            xml.write("columns", columns);
        if (Str.HasChar(this.type))
            xml.write("type", type);
        if (Str.HasChar(this.prod))
            xml.write("prod", prod);
        if (Str.HasChar(this.canon))
            xml.write("canon", canon);
        if (isSpreadUndef())
            xml.write("spread", spread);
        xml.write("checksum", checksum);
        // if (dpiX > 0)
        // xml.write("dpi-x", dpiX);
        // if (dpiY > 0)
        // xml.write("dpi-y", dpiY);
        return this.children();
    }

    @Override
    public void readAttributes(DomNode dom)
    {
        this.id = dom.id();
        if (id == null || id.isEmpty())
            this.id = autoID();
        this.width = dom.real("width", width);
        this.height = dom.real("height", height);
        this.margins.top = dom.real("top", margins.top);
        this.margins.right = dom.real("right", margins.right);
        this.margins.bottom = dom.real("bottom", margins.bottom);
        this.margins.left = dom.real("left", margins.left);
        this.columns = dom.reals("columns", columns);
        this.type = dom.value("type");
        this.prod = dom.value("prod");
        this.canon = dom.value("canon");
        this.spread = dom.value("spread");
        this.checksum = dom.value("checksum");
        // this.dpiX = dom.real("dpi-x", dpiX);
        // this.dpiY = dom.real("dpi-y", dpiY);
    }

    @Override
    public XmlINode newChild(DomNode child)
    {
        // children are already created in constructor
        if (OCD.isTag(child, OCDProperties.TAG))
            return this.properties().clear();
        else if (OCD.isTag(child, OCDAnnotations.TAG))
            return annotations.clear();
        else if (OCD.isTag(child, OCDDefinitions.TAG))
            return definitions.clear();
        else if (OCD.isTag(child, OCDContent.TAG))
            return content.clear();
        return null;
    }

    @Override
    public void endChild(XmlINode child)
    {
        // if (child == null)
        // return;
        // else if (OCD.resources.is(child))
        // this.resources = (OCDPageResources) child;
        // else if (OCD.annotations.is(child))
        // this.annotations = (OCDPageAnnotations) child;
        // else if (OCD.definitions.is(child))
        // this.definitions = (OCDPageDefinitions) child;
        // else if (OCD.content.is(child))
        // this.content = (OCDPageContent) child;
    }

    @Override
    public synchronized List<? extends OCDNode> children()
    {
        return new List3<>(this.properties(), this.annotations, this.definitions, this.content);
    }

    public OCDPage first()
    {
        return document().pageHandler.firstPage();
    }

    public OCDPage last()
    {
        return document().pageHandler.lastPage();
    }

    public int number()
    {
        return document().pageHandler.pageNumber(this);
    }

    public OCDPage turn()
    {
        return hasNext() ? next() : document().pageHandler.firstPage();
    }

    public OCDPage previous()
    {
        return document().pageHandler.getPage(number() - 1);
    }

    public OCDPage next()
    {
        return document().pageHandler.getPage(number() + 1);
    }

    public boolean hasPrevious()
    {
        return document().pageHandler.hasPage(number() - 1);
    }

    public boolean hasNext()
    {
        return document().pageHandler.hasPage(number() + 1);
    }

    public Transform3 viewTransform(OCD.ViewProps props, boolean forward)
    {
        return forward ? toView(props) : fromView(props);
    }

    public Transform3 toView(OCD.ViewProps props)
    {
        float s = props == null ? 1 : props.scale;
        Point3 o = (props.cropbox == null ? viewBox(props) : props.cropbox).origin();
        return new Transform3(s, 0, 0, s, -s * o.x, -s * o.y);
    }

    public Transform3 fromView(OCD.ViewProps props)
    {
        double s = props == null ? 1 : props.scale;
        Point3 o = (props.cropbox == null ? viewBox(props) : props.cropbox).origin();
        return new Transform3(1.0 / s, 0, 0, 1.0 / s, o.x, o.y);
    }

    public Transform3 viewTransform(boolean forward)
    {
        return forward ? toView() : fromView();
    }

    public Transform3 toView()
    {
        OCDDocument doc = document();
        return toView(doc == null ? null : doc.viewProps);
    }

    public Transform3 fromView()
    {
        OCDDocument doc = document();
        return fromView(doc == null ? null : doc.viewProps);
    }

    public Path3 toView(Shape s)
    {
        return this.toView().transform(s);
    }

    public Path3 fromView(Shape s)
    {
        return this.fromView().transform(s);
    }

    public Path3 viewTransform(Shape s, boolean forward)
    {
        return forward ? toView(s) : fromView(s);
    }

    public Point3 toView(Point2D p)
    {
        OCDDocument doc = document();
        float s = doc == null ? 1 : doc.viewProps.scale;
        Point3 o = viewBox().origin();
        return new Point3(s * (p.getX() - o.x), s * (p.getY() - o.y));
    }

    public Point3 fromView(Point2D p)
    {
        OCDDocument doc = document();
        float s = doc == null ? 1 : doc.viewProps.scale;
        Point3 o = viewBox().origin();
        return new Point3(p.getX() / s + o.x, p.getY() / s + o.y);
    }

    public Rectangle3 viewTransform(Rectangle2D r, boolean forward)
    {
        return forward ? toView(r) : fromView(r);
    }

    public Rectangle3 toView(Rectangle2D r)
    {
        OCDDocument doc = document();
        float s = doc == null ? 1 : doc.viewProps.scale;
        Point3 o = viewBox().origin();
        return new Rectangle3(s * (r.getX() - o.x), s * (r.getY() - o.y), s * r.getWidth(), s * r.getHeight());
    }

    public Rectangle3 fromView(Rectangle2D r)
    {
        OCDDocument doc = document();
        float s = doc == null ? 1 : doc.viewProps.scale;
        Point3 o = viewBox().origin();
        return new Rectangle3(r.getX() / s + o.x, r.getY() / s + o.y, r.getWidth() / s, r.getHeight() / s);
    }

    @Override
    public Rectangle3 viewBounds()
    {
        OCDDocument doc = document();
        return viewBounds(doc == null ? null : doc.viewProps);
    }

    public Rectangle3 viewBounds(String ref)
    {
        return new Rectangle3(this.viewTransform().transform(viewBox(ref)));
    }

    public Rectangle3 viewBounds(OCD.ViewProps props)
    {
        Rectangle3 viewbox = viewBox(props);
        return props == null ? viewbox : new Rectangle3(props.scale().transform(viewbox));
    }

    @Override
    public Rectangle3 bounds()
    {
        return new Rectangle3(0, 0, width, height);
    }

    public Rectangle3 topBounds()
    {
        return new Rectangle3(true, 0, 0, width, margins.top);
    }

    public Rectangle3 bottomBounds()
    {
        return new Rectangle3(true, 0, height - margins.bottom, width, height);
    }

    public Rectangle3 leftBounds()
    {
        return new Rectangle3(true, 0, margins.top, margins.left, height - margins.bottom);
    }

    public Rectangle3 rightBounds()
    {
        return new Rectangle3(true, width - margins.left, margins.top, width, height - margins.bottom);
    }

    public Rectangle3[] marginBounds()
    {
        return Rectangle3.array(topBounds(), rightBounds(), bottomBounds(), leftBounds());
    }

    public void setSize(Rectangle2D bounds)
    {
        this.width = (float) bounds.getWidth();
        this.height = (float) bounds.getHeight();
    }

    public void setSize(double w, double h)
    {
        this.width = (float) w;
        this.height = (float) h;
    }

    public void setWidth(double width)
    {
        this.width = (float) width;
    }

    public void setHeight(double height)
    {
        this.height = (float) height;
    }

    public float width()
    {
        return width;
    }

    public float height()
    {
        return height;
    }

    public int intWidth()
    {
        return (int) this.width;
    }

    public int intHeight()
    {
        return (int) this.height;
    }

    public int nbOfPages()
    {
        return document().pageHandler.nbOfPages();
    }

    public OCDPage getPage()
    {
        return this;
    }

    public OCDPage getPage(int pageNumber)
    {
        return document() == null ? null : document().pageHandler.getPage(pageNumber);
    }

    @Override
    public String sticker()
    {
        return "Page " + this.number();
    }

    @Override
    public synchronized void freeFromMemory()
    {
        this.freeFromMemory(false);
    }

    @Override
    public void dispose()
    {
        this.freeFromMemory(true);
        super.dispose();
    }

    public synchronized void forceFreeFromMemory()
    {
        this.freeFromMemory(true);
    }

    public synchronized void freeFromMemory(boolean evenIfModified)
    {
        if (evenIfModified || !modified())
        {
            this.annotations.clear();
            this.definitions.clear();
            this.content.clear();
            this.checksum = "";
            this.setInMemory(false);
        }
    }

    @Override
    public synchronized boolean ensureInMemory()
    {
        return this.ensureInMemory(null);
    }

    public synchronized boolean ensureInMemory(File3 file)
    {
        boolean inMem = isInMemory();
        if (!inMem)
            loadInMemory(file);
        return inMem;
    }

    public void loadInMemory()
    {
        loadInMemory(null);
    }

    public void loadInMemory(File3 file)
    {
        this.clear();
        this.document().pageHandler.loadPage(this, file);
        this.setInMemory(true);
    }


    @Override
    public boolean writeNode(OutputStream stream)
    {
        // careful, page generation needs graphics state initialization
        content().initState();
        boolean written = super.writeNode(stream);
        return written;
    }

    @Override
    public void paint(Graphics3 g, OCD.ViewProps props)
    {
        this.content().paint(g, props);
    }

    public Image3 createImage()
    {
        return createImage(document().viewProps, null);
    }

    public Image3 createImage(double scale)
    {
        return createImage(scale, null);
    }

    public Image3 createImage(double scale, ImageThumberCache thumber)
    {
        OCD.ViewProps props = new OCD.ViewProps();
        props.scale = (float) scale;
        return createImage(props, thumber);
    }

    public Image3 createImage(int maxSide, boolean antialias, ImageThumberCache thumber)
    {
        double w = viewBox().width;
        double h = viewBox().height;
        if (antialias)
            maxSide *= 2;
        Image3 image = createImage(maxSide / (w > h ? w : h), thumber);
        if (antialias)
            image = image.decimate(0.5, thumber);
        return image;
    }

    public Image3 createImage(OCD.ViewProps props)
    {
        return createImage(props, null);
    }

    public Image3 createImage(OCD.ViewProps props, ImageThumberCache thumber)
    {
        return content().createImage(props, Color3.WHITE, thumber);
    }

    public Image3 subImage(Rectangle3 box, double scale)
    {
        return createImage(scale, null).crop(box.scale(scale));
    }

    public void paintImage(Graphics3 g)
    {
        paintImage(document().viewProps, g);
    }

    public void paintImage(OCD.ViewProps props, Graphics3 g)
    {
        content().paintImage(props, g, Color3.WHITE);
    }

    // public OCDPage copyText()
    // {
    // this.ensureInMemory();
    // OCDPage page = new OCDPage(document(), this.entryPath);
    // page.annotations = this.annotations;
    // page.manifest = this.manifest;
    // page.content = new OCDPageContent(page);
    // for (OCDPaintable node : this.content)
    // if (OCD.isTextBlock(node))
    // page.content.add(((OCDTextBlock) node).copy());
    // else if (node.is(OCD.text))
    // page.content.add(((OCDText) node).copy());
    // page.width = this.width;
    // page.height = this.height;
    // page.dpi = this.dpi;
    // page.type = this.type;
    // page.spread = this.spread;
    // page.number = this.number();
    // page.descriptor = this.descriptor;
    // return page;
    // }
    public List3<OCDPaintable> filteredContent(String... filterType)
    {
        OCDAnnotations annots = annots().type(filterType);
        List3<OCDPaintable> list = new List3<>();
        primitives:
        for (OCDPaintable node : content())
        {
            Rectangle3 nodeBounds = node.viewBounds();
            for (String annotID : annots.ids())
            {
                Rectangle3 bounds = annots.get(annotID).viewBounds();
                if (Math3.r1ContainsR2(bounds, nodeBounds))
                    continue primitives;
            }
            list.add(node);
        }
        return list;
    }

    public void inject(OCDPage page, boolean keepImageFiles, boolean overrideFonts)
    {
        OCDDocument ocd = this.doc();
        OCDDocument src = page.doc();

        Log.debug(this, ".inject in " + ocd.fileName() + " from " + src.fileName());

        this.props.putAll(page.props);

        for (OCDAnnot annot : page.annotations())
            this.annotations.addAnnotation(annot.copy());

        for (OCDPaintable node : page.definitions())
            this.definitions.addDefinition(node.copy());

        for (OCDPaintable node : page.content())
            this.content.add(node.copy());

        if (keepImageFiles)
            for (OCDImage image : page.images())
                ocd.imageHandler.addEntry(image);

        if (!overrideFonts)
        {
            for (OCDText text : this.texts())
            {
                text.fontname = src.needID() + "-" + text.fontname;
                Log.debug(this, ".inject: " + text.string() + ", " + text.fontname + ", " + text.doc().fileName());
            }

            for (SVGFont font : src.fontHandler)
            {
                font.fontFamily = src.needID() + "-" + font.fontFamily;
                font.setEntryFilename(font.fontFamily);
                ocd.fontHandler.add(font);
                Log.debug(this, ".inject - font: " + font.fontFamily + ", fonts=" + ocd.fontHandler.map().keys());
            }
        }
    }

    public void addBackground(File3 file)
    {
        // ensures OCDPage does not try to dynamically load page from file
        // this.setInMemory(true);
        Log.debug(this, ".addBackground - " + file.path());
        Image3 img = Image3.Read(file);
        if (img != null)
        {
            OCDPageContent ocdContent = this.content();

            OCDPaintable oldBg = ocdContent.first();
            if (oldBg != null && oldBg.isImage() && oldBg.bounds().overlap(this.bounds()) > 0.8)
            {
                Log.debug(this, ".addBackground - removed");
                oldBg.remove();
            }

            for (OCDPaintable node : ocdContent)
                if (node.isImage() && node.isRole("background"))
                {
                    Log.debug(this, ".addBackground - removed");
                    node.remove();
                }

            double sx = this.width / (double) img.width();
            double sy = this.height / (double) img.height();

            double scale = Math.min(sx, sy);

            this.annots().addViewboxAnnot(new Rectangle3(0, 0, width, height), OCDAnnot.ID_VIEWBOX);

            // ocdPage.props().put64("alto-description", this.description.toString());

            OCDDefinitions defs = this.defs();
            OCDClip clip = new OCDClip(defs, this.bounds(), OCDClip.ID_PAGE);
            defs.addDefinition(clip);

            OCDImage ocdImage = new OCDImage(ocdContent);
            ocdImage.modify().setImage(file.name(), img.width(), img.height(), file.bytes());

            Transform3 tm = new Transform3();

            // tm.rotate(-skew / 1440.0, ocdImage.width() / 2 * sx, ocdImage.height()
            // / 2 * sy);
            tm.scale(scale, scale);

            ocdImage.setTransform(tm);

            // if (box.width > box.height != bg.width() > bg.height()) {
            // tm.rotate(Math.PI / 2);
            // ocdImage.setTransform(tm);
            // ocdImage.setX(ocdImage.x() + box.width);
            // }

            ocdImage.setRole("background");
            ocdContent.add(0, ocdImage);
            this.doc().imageHandler.addEntry(ocdImage);
        }

        // ocdPage.margins.top = Omni.ocd(desc.page.marginTop);
        // ocdPage.margins.bottom = Omni.ocd(desc.page.marginBottom);
        // ocdPage.margins.left = Omni.ocd(desc.page.marginLeft);
        // ocdPage.margins.right = Omni.ocd(desc.page.marginRight);

        // for (OmniPaintable content : body.nodes) {
        // // ocdPage.annots().addSpaceAnnot(content.bounds().scale(conv.trans.sx,
        // // conv.trans.sy), "print", print.ID);
        // this.content2ocd(conv, ocdPage, ocdContent, bg, content);
        // }
        //
        // ocdPage.content().zOrderize(0);
        //
        // int gap = 100;
        // ocdPage.annots().addViewboxAnnot(new Rectangle3(-gap, -gap, box.width + 2
        // * gap, box.height + 2 * gap),
        // OCDViewboxAnnot.ID_CANVASBOX);
        // ocdPage.annots().addViewboxAnnot(new Rectangle3(box.x, box.y, box.width,
        // box.height), OCDViewboxAnnot.ID_DATABOX);
        //
        //
        // }
        // else
        // Log.warn(this, ".addBackground - img not found: "+file.path());
    }

    public OCDAnnot addAnnotation(String id, String type, Rectangle3 box)
    {
        return annots().addAnnotation(id, type, box);
    }

    public JsonMap toJson() {
        JsonMap json = new JsonMap();

        json.put("class", "Page");
        json.put("type" , "page");
        json.put("pageNumber",pageNb());
        json.put("width", width);
        json.put("height", height);


        // Add content, annotations, and definitions if they exist
        if (annotations != null) json.put("annotations", annotations.toJson());
        if (definitions != null) json.put("definitions", definitions.toJson());
        if (content != null) json.put("content", content.toJson());

        return json;
    }

    @Override
    public String toString()
    {
        return tag + "[" + id() + "]" + "\nXML[\n" + Xml.toString(this) + "\n]";
    }
}
