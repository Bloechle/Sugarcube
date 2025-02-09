package sugarcube.formats.ocd.objects;

import sugarcube.common.data.collections.Str;
import sugarcube.common.data.json.JsonMap;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Stroke3;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.formats.ocd.OCD;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

public class OCDAnnot extends OCDPaintable
{

    public static final String TAG = "annot";

    public static final String ID_CANVASBOX = "CanvasBox";
    public static final String ID_PAGEBOX = "PageBox";
    public static final String ID_VIEWBOX = "ViewBox";
    public static final String ID_TRIMBOX = "TrimBox";
    public static final String ID_DATABOX = "DataBox";
    public static final String ID_TEXTBOX = "TextBox";
    public static final String ID_LEFTBOX = "LeftBox";
    public static final String ID_RIGHTBOX = "RightBox";

    public static final String PROP_TYPE = "type";
    public static final String PROP_LINK = "link";
    public static final String PROP_ACTION = "action";
    public static final String PROP_NAME = "name";
    public static final String PROP_FIELD = "field";
    public static final String PROP_WIDGET = "widget";

    public static final String TYPE_VIEWBOX = "Viewbox";
    public static final String TYPE_LAYOUT = "Layout";
    public static final String TYPE_VALIDATION = "Validation";
    public static final String TYPE_LINK = "Link";
    public static final String TYPE_FORM = "Form";
    public static final String TYPE_REFERENCE = "Reference";
    public static final String TYPE_QUIZ = "Quiz";
    public static final String TYPE_COMMENT = "Comment";
    public static final String TYPE_OCR = "OCR";

    public static final String CLASS_HEADER = "Header";
    public static final String CLASS_FOOTER = "Footer";
    public static final String CLASS_SIDE_INNER = "InnerSide";
    public static final String CLASS_SIDE_OUTER = "OuterSide";

    public static final String CLASS_IGNORE = "Ignore";
    public static final String CLASS_TOC = "TableOfContent";
    public static final String CLASS_BIBLIO = "Bibliographie";
    public static final String CLASS_INDEX = "Index";
    public static final String CLASS_FIXED = "Fixed";

    public static String[] VIEWBOX_IDS =
            {ID_CANVASBOX, ID_PAGEBOX, ID_VIEWBOX, ID_TRIMBOX, ID_DATABOX};
    public static String[] LAYOUT_IDS =
            {CLASS_HEADER, CLASS_FOOTER, CLASS_SIDE_INNER, CLASS_SIDE_OUTER};

    protected Rectangle3 bounds;

    public OCDAnnot()
    {
        super(TAG, null);
    }

    public OCDAnnot(OCDNode parent)
    {
        super(TAG, parent);
    }

    public OCDAnnot(OCDNode parent, String id, String type)
    {
        super(TAG, parent);
        this.setID(id);
        this.setType(type);
    }

    public OCDAnnot(OCDNode parent, String id, String type, Rectangle2D bounds, String... props)
    {
        super(TAG, parent);
        this.setID(id);
        this.setType(type);
        this.bounds = new Rectangle3(bounds);
        this.props.putAll(props);
    }

//  public OCDAnnot(OCDNode parent, Rectangle2D bounds, Map<String, String> props)
//  {
//    super(TAG, parent);
//    this.bounds = new Rectangle3(bounds);
//    this.props.putAll(props);
//  } 

    public OCDAnnot set(String name, String value)
    {
        super.set(name, value);
        ;
        return this;
    }

    @Override
    public OCDAnnot setID(String id)
    {
        super.setID(id);
        return this;
    }

    @Override
    public OCDAnnot setClassname(String classname)
    {
        super.setClassname(classname);
        return this;
    }

    public boolean isWWW()
    {
        return IsWWW(link());
    }

    public static boolean IsWWW(String uri)
    {
        return uri != null && (uri.startsWith("http://") || uri.startsWith("https://") || uri.startsWith("www.") || uri.startsWith("ftp://")
                || uri.startsWith("ftps://"));
    }

    public boolean isMailto()
    {
        return IsMailto(link());
    }

    public static boolean IsMailto(String uri)
    {
        return uri != null && uri.trim().startsWith("mailto:");
    }

    public OCDAnnotations annots()
    {
        OCDNode node = this;
        while ((node = node.parent()) != null)
            if (node instanceof OCDAnnotations)
                return (OCDAnnotations) node;
        return null;
    }

    public OCDAnnot annot(String ref)
    {
        OCDAnnotations annots = this.annots();
        return annots == null ? null : annots.get(ref);
    }

//  @Override
//  public String autoID()
//  {
//    String type = this.type().toLowerCase();
//    if(type.length()>3)
//      type = type.substring(0,3);
//    return (Text.IsVoid(type) ? "def" : type) + "_" + Base.x32.random8();
//  }
//
//  public String autoID(String prefix)
//  {
//    return autoID();
//  }

    public String key()
    {
        return this.id();
    }

    public String cdata()
    {
        return this.props.emptyValue();
    }

    public void setCData(String cdata)
    {
        this.props.setEmptyValue(cdata);
    }

    public String type()
    {
        return this.props.get(PROP_TYPE);
    }

    public void setType(String type)
    {
        this.props.put(PROP_TYPE, type);
    }

    public boolean isType(String type)
    {
        return type.equalsIgnoreCase(type());
    }

    public String action()
    {
        return this.props.get(PROP_ACTION);
    }

    public void setAction(String type)
    {
        this.props.put(PROP_ACTION, type);
    }

    public boolean isAction(String type)
    {
        return type.equalsIgnoreCase(action());
    }

    public boolean hasLink()
    {
        return !Str.IsVoid(link());
    }

    public OCDAnnot setLink(String data)
    {
        this.props.put(PROP_LINK, data);
        return this;
    }

    public String link()
    {
        String link = props.get(PROP_LINK);
        if (link == null || link.isEmpty())
            link = props.get("link");
        return link == null ? "" : link;
    }

    public boolean isLayout(String classname)
    {
        return isLayout() && isClassname(classname);
    }

    public boolean isComment()
    {
        return isType(TYPE_COMMENT);
    }

    public boolean isOCR()
    {
        return isType(TYPE_OCR);
    }

    public boolean isViewbox()
    {
        return isType(TYPE_VIEWBOX);
    }

    public boolean isLayout()
    {
        return isType(TYPE_LAYOUT);
    }

    public boolean isForm()
    {
        return isType(TYPE_FORM);
    }

    public boolean isQuiz()
    {
        return isType(TYPE_QUIZ);
    }

    public boolean isLink()
    {
        return isType(TYPE_LINK);
    }

    public boolean isAnchorLink()
    {
        return isLink() && link().startsWith("#");
    }

    public String text()
    {
        return props.emptyValue();
    }

    public void setText(String data)
    {
        this.props.setEmptyValue(data);
    }

    public String property(String name)
    {
        return this.props.get(name);
    }

    public void setProperty(String name, String value)
    {
        this.props.put(name, value);
    }

    public OCDAnnot setBounds(Rectangle2D box)
    {
        this.bounds = new Rectangle3(box);
        return this;
    }

    public boolean intersects(Shape shape)
    {
        return this.bounds.intersects(shape.getBounds2D());
    }

    public boolean contains(Shape shape)
    {
        return this.bounds.contains(shape.getBounds2D());
    }

    public double overlap(Rectangle3 box)
    {
        return this.bounds.overlapThat(box);
    }

    public boolean contains(Point2D point)
    {
        return this.bounds.contains(point);
    }

    @Override
    public Collection<? extends OCDNode> writeAttributes(Xml xml)
    {
        this.writeXmlID(xml);
        xml.write("bbox", bounds.toOCD(xml.numberFormat()));
        xml.write("name", Str.HasChar(name) ? name : null);
        props.writeAttributes(xml);
        return this.children();
    }

    @Override
    public void readAttributes(DomNode dom)
    {
        this.readXmlID(dom);
        this.bounds = Rectangle3.fromOCD(dom.value("bbox"), null);
        this.name = dom.value("name", "");
        props.readAttributes(dom, true);
    }

    @Override
    public XmlINode newChild(DomNode child)
    {
        return null;
    }

    @Override
    public void endChild(XmlINode child)
    {
    }

    @Override
    public Rectangle3 bounds()
    {
        return bounds;
    }

    public float x()
    {
        return bounds.x;
    }

    public float y()
    {
        return bounds.y;
    }

    public float width()
    {
        return bounds.width;
    }

    public float height()
    {
        return bounds.height;
    }

    public String babel()
    {
        return cdata();
    }

    @Override
    public String sticker()
    {
        return type() + "[" + id() + "]";
    }

    @Override
    public void paint(Graphics3 g, OCD.ViewProps props)
    {
        g.setStroke(Stroke3.LINE2);
        g.setColor(Color3.GREEN.alpha(0.2));
        g.fill(bounds());
        g.draw(bounds());
    }

    public OCDAnnot copy(String id)
    {
        OCDAnnot copy = copy();
        copy.setID(id);
        return copy;
    }

    @Override
    public OCDAnnot copy()
    {
        OCDAnnot copy = new OCDAnnot((OCDNode) parent, id(), type(), bounds == null ? null : bounds.copy());
        copy.props = props.copy();
        super.copyTo(copy);
        return copy;
    }

    public JsonMap toJson() {
        JsonMap json = new JsonMap();

        json.put("class", "Annotation");
        json.put("id", this.id());
        json.put("type", this.type());
        json.put("name", this.name());
        json.put("link", this.link());
        json.put("action", this.action());

        // Bounding box
        if (this.bounds != null) {
            JsonMap boundsJson = new JsonMap();
            boundsJson.put("x", this.bounds.x);
            boundsJson.put("y", this.bounds.y);
            boundsJson.put("width", this.bounds.width);
            boundsJson.put("height", this.bounds.height);
            json.put("bounds", boundsJson);
        }


        return json;
    }
}
