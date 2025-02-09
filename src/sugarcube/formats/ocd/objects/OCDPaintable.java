package sugarcube.formats.ocd.objects;

import sugarcube.common.data.json.JsonMap;
import sugarcube.common.system.reflection.Annot._Bean;
import sugarcube.common.data.collections.*;
import sugarcube.common.graphics.geom.Line3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Shape3;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.OCD;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

public class OCDPaintable extends OCDNode
{
    private static final List3<OCDPaintable> NO_CHILD = new List3<>();
    @_Bean(name = "Name")
    public String  name = "";
    @_Bean(name = "Z-Order")
    public float zOrder = 0;// z order (i.e., rendering order)
    // used to merge/group OCDText to OCDTextblock (MS Word)
    public int groupID = -1;
    // used to sort nodes by indexes...
    public transient int index = -1;


    public OCDPaintable(String tag)
    {
        super(tag);
    }

    public OCDPaintable(String tag, OCDNode parent)
    {
        super(tag, parent);
    }


    public String type()
    {
        return "";
    }

    public void setClipID(String clipID)
    {

    }


    public void shift(float dx, float dy)
    {

    }

    public int rOrder()
    {
        OCDGroup<? extends OCDPaintable> g = this.parentGroup();
        if (g != null)
        {
            int rOrder = 0;
            for (OCDPaintable node : g)
            {
                rOrder++;
                if (node == this)
                    return rOrder;
            }
        }
        return -1;
    }

    public OCDPage modifyPage()
    {
        OCDPage page = page();
        return page == null ? null : page.modify();
    }

    protected void writeXmlGroupID(Xml xml)
    {
        if (this.groupID >= 0)
            xml.write("gid", groupID);
    }

    protected void readXmlGroupID(DomNode dom)
    {
        this.groupID = dom.integer("gid", -1);
    }

    public String name()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isName(String name)
    {
        return Str.Equals(name, name());
    }

    public boolean isName(String[] names)
    {
        if (names == null || names.length == 0)
            return true;
        for (String t : names)
            if (this.name.equals(t))
                return true;
        return false;
    }

    public void setZOrder(float z)
    {
        this.zOrder = z;
    }

    public void incZOrder(float z)
    {
        this.zOrder += z;
    }

    public float zOrder()
    {
        return zOrder;
    }

    public OCDGroup<OCDPaintable> parentGroup(String... types)
    {
        OCDNode node = this;
        while ((node = node.parent()) != null)
            if (OCD.isGroup(node, types))
                return (OCDGroup<OCDPaintable>) node;
        return null;
    }

    public boolean hasParentGroup(String... types)
    {
        return parentGroup(types) != null;
    }

    public boolean has(String name)
    {
        return this.props.has(name);
    }

    public boolean has(String name, String value)
    {
        String tokens = this.props.get(name, "").trim();
        if (!tokens.isEmpty())
            for (String token : tokens.split(Tokens.SPLIT_SPACE))
                if (token.trim().equals(value))
                    return true;
        return false;
    }

    public boolean hasWithout(String name, String... values)
    {
        String value = props.get(name, null);
        if (value == null)
            return false;
        for (String v : values)
            if (value.equals(v))
                return false;
        return true;
    }

    public boolean hasnt(String name)
    {
        return this.props.hasnt(name);
    }

    public boolean hasnt(String name, String value)
    {
        return !has(name, value);
    }

    public boolean hasNone(String name, String... values)
    {
        for (String v : values)
            if (has(name, v))
                return false;
        return true;
    }

    public String get(String name)
    {
        return Str.Equals(name, "name") ? this.name : props.get(name);
    }

    public String get(String name, String def)
    {
        return Str.Equals(name, "name") ? this.name : props.get(name, def);
    }

    public OCDPaintable set(String name, String value)
    {
        if (Str.Equals(name, "name"))
            this.name = value;
        else
            this.props.put(name, value);
        return this;
    }

    public void set(String name, Object value)
    {
        this.set(name, value == null ? null : value.toString());
    }

    public void add(String name, String... values)
    {
        String tokens = this.props.get(name, "").trim();
        StringSet set = new StringSet();
        if (!tokens.isEmpty())
            set.addAll(tokens.split(Tokens.SPLIT_SPACE));
        set.addAll(values);
        if (set.isPopulated())
            if (Str.Equals(name, "name"))
                this.name = set.toString(" ");
            else
                this.props.put(name, set.toString(" "));
        else
            this.props.remove(name);
    }

    public void remove(String name, String... values)
    {
        if (values.length == 0)
            this.props.remove(name);
        else
        {
            String tokens = this.props.get(name, "").trim();
            StringSet set = new StringSet();
            if (!tokens.isEmpty())
                set.addAll(tokens.split(Tokens.SPLIT_SPACE));
            set.removeAll(values);
            if (set.isPopulated())
                this.props.put(name, set.toString(" "));
            else
                this.props.remove(name);
        }
    }

    public void toggle(String name, String... values)
    {
        String tokens = this.props.get(name, "").trim();
        StringSet set = new StringSet();
        if (!tokens.isEmpty())
            set.addAll(tokens.split(Tokens.SPLIT_SPACE));
        for (String value : values)
            if (set.has(value))
                set.remove(value);
            else
                set.add(value);
        if (set.isPopulated())
            this.props.put(name, set.toString(" "));
        else
            this.props.remove(name);
    }

    public void cet(String name, String value)
    {
        if (value == null)
            this.props.remove(name);
        else
            this.props.put(name, value);
    }

    public void clear(String name, String... names)
    {
        this.props.remove(name);
        for (String n : names)
            this.props.remove(n);
    }

    public void set(Map<String, String> props)
    {
        this.props.putAll(props);
    }

    public void set(Props props)
    {
        this.props = props;
    }

    public void add(Props props)
    {
        this.props.putAll(props);
    }

    public Line3 extent()
    {
        return this.bounds().extent();
    }

    public void setExtent(Line3 extent)
    {
        this.path().setExtent(extent);
    }

    @Override
    public boolean hasContent()
    {
        return !this.bounds().isEmpty();
    }

    @Override
    public boolean isGrouped(int level, String... types)
    {
        return parent == null ? false : parent().isGrouped(level, types);
    }

    public boolean hasEvent()
    {
        return Str.Has(onload()) || Str.Has(onclick());
    }

    public String onload()
    {
        return get("onload");
    }

    public String onclick()
    {
        return get("onclick");
    }

    public boolean isLink(String link)
    {
        return Str.Equal(link(), link);
    }

    public boolean hasLink()
    {
        return Str.HasData(link());
    }

    public boolean hasAnchorLink()
    {
        String link = link();
        return link != null && link.startsWith("#");
    }

    public boolean hasPageLink()
    {
        String link = link();
        return link != null && link.endsWith(".xml");
    }

    public String link()
    {
        return this.get(OCDAnnot.PROP_LINK);
    }

    public OCDPaintable setLink(String link)
    {
        this.set(OCDAnnot.PROP_LINK, link);
        return this;
    }

    public boolean hasClassname(String... styles)
    {
        for (String style : styles)
            if (isClassname(style))
                return true;
        return false;
    }

    public boolean isClassname(String style)
    {
        String s = classname(true);
        if (s == null)
            return style == null;
        return s.equals(style);
    }

    public String classname()
    {
        return props.get(OCD.CLASS);
    }

    public String classname(String def)
    {
        return props.get(OCD.CLASS, def);
    }

    public String classname(boolean trimAutoPrefix)
    {
        return classname(trimAutoPrefix, null);
    }

    public String classname(boolean trimAutoPrefix, String def)
    {
        return trimAutoPrefix ? OCD.TrimClassnameAutoPrefix(classname(def)) : classname(def);
    }

    public OCDPaintable setClassname(String classname)
    {
        if (classname == null)
            props.remove(OCD.CLASS);
        else
            props.put(OCD.CLASS, classname);
        return this;
    }

    public void removeClassname()
    {
        props.remove(OCD.CLASS);
    }

    public void removeClassnameIf(boolean condition)
    {
        if (condition)
            props.remove(OCD.CLASS);
    }

    public String classID()
    {
        return props.get(OCD.CLASS_ID);
    }

    public String classID(String def)
    {
        return props.get(OCD.CLASS_ID, def);
    }

    public void setClassID(String rulename)
    {
        if (rulename == null)
            props.remove(OCD.CLASS_ID);
        else
            props.put(OCD.CLASS_ID, rulename);
    }

    public void removeClassID()
    {
        props.remove(OCD.CLASS_ID);
    }

    public boolean hasRole()
    {
        return this.has(OCD.ROLE);
    }

    public void setRole(String role)
    {
        this.set(OCD.ROLE, role);
    }

    public boolean isRole(String role)
    {
        return Str.Equals(role, role(null));
    }

    public String role(String recover)
    {
        return this.get("role", recover);
    }

    public boolean isLabel(String tag)
    {
        return label().equals(tag);
    }

    public boolean hasLabel()
    {
        return !"".equals(label());
    }

    public String label()
    {
        return props.get(OCD.LABEL, "");
    }

    public void clearLabel()
    {
        this.props.remove(OCD.LABEL);
    }

    public void setLabel(String label)
    {
        this.props.put(OCD.LABEL, label);
    }

    protected void writeXmlName(Xml xml)
    {
        xml.write("name", name == null || name.isEmpty() ? null : name);
    }

    protected void readXmlName(DomNode dom)
    {
        this.name = dom.value("name", "");
    }

    @Override
    public Collection<? extends OCDPaintable> children()
    {
        return NO_CHILD;
    }

    // path is the unmodified object path, shape is the default dpi device
    // transformed one...
    public Shape3 path()
    {
        return new Rectangle3(0.0, 0.0, 844.72406, 1289.764);
    }

    public Shape3 shape()
    {
        return this.bounds();
    }

    public Rectangle3 sharpBounds()
    {
        Rectangle3 r = bounds();
        return new Rectangle3((int) r.x, (int) r.y, r.width, r.height);
    }

    public void copyTo(OCDPaintable node)
    {
        super.copyTo(node);
        node.name = name;
        node.zOrder = zOrder;
        node.groupID = groupID;
    }

    public static Comparator<OCDPaintable> ZOrderComparator()
    {
        return (a, b) -> Float.compare(a.zOrder(), b.zOrder());
    }

    public static Comparator<OCDPaintable> XComparator()
    {
        return (a, b) -> Float.compare(a.bounds().x, b.bounds().x);
    }

    public static Comparator<OCDPaintable> YComparator()
    {
        return (a, b) -> Float.compare(a.bounds().y, b.bounds().y);
    }

    public static Comparator<OCDPaintable> IndexComparator()
    {
        return (a, b) -> Integer.compare(a.index, b.index);
    }

    public boolean isDefinition()
    {
        return this.parent != null && OCD.isTag(parent, OCDDefinitions.TAG);
    }

    public boolean isAnnot()
    {
        return OCD.isAnnot(this);
    }

    public boolean isText()
    {
        return OCD.isText(this);
    }

    public boolean isPath()
    {
        return OCD.isPath(this);
    }

    public boolean isImage()
    {
        return OCD.isImage(this);
    }

    public boolean isClip()
    {
        return OCD.isClip(this);
    }

    public boolean isGroup()
    {
        return OCD.isGroup(this);
    }

    public boolean isGroupContent()
    {
        return OCD.isGroupContent(this);
    }

    public boolean isPageContent()
    {
        return OCD.isPageContent(this);
    }

    public boolean isGroupOrContent()
    {
        return isGroup() || isPageContent();
    }

    public boolean isTable()
    {
        return OCD.isGroup(this, OCDGroup.TABLE);
    }

    public boolean isCell()
    {
        return OCD.isGroup(this, OCDGroup.CELL);
    }

    public boolean isTextLine()
    {
        return OCD.isTextLine(this);
    }

    public boolean isGraphicsGroup()
    {
        return OCD.isGroup(this, OCDGroup.CONTENT);
    }

    public boolean isColumn()
    {
        return OCD.isGroup(this, OCDGroup.COLUMN);
    }

    public boolean isLayer(String... names)
    {
        return OCD.isLayer(this, names);
    }

    public boolean isTextBlock()
    {
        return OCD.isTextBlock(this);
    }

    public boolean isParagraph()
    {
        return OCD.isTextBlock(this);
    }

    public boolean isFlow()
    {
        return OCD.isFlow(this);
    }

    public OCDAnnot asAnnot()
    {
        return this instanceof OCDAnnot ? (OCDAnnot) this : null;
    }

    public OCDContent asContent()
    {
        return this instanceof OCDContent ? (OCDContent) this : null;
    }

    public OCDGroup<OCDPaintable> asGroup()
    {
        return this instanceof OCDGroup ? (OCDGroup<OCDPaintable>) this : null;
    }

    public OCDTable asTable()
    {
        return this instanceof OCDGroup ? (OCDTable) this : null;
    }

    public OCDTableCell asCell()
    {
        return this instanceof OCDGroup ? (OCDTableCell) this : null;
    }

    public OCDTextBlock asTextBlock()
    {
        return this instanceof OCDTextBlock ? (OCDTextBlock) this : null;
    }

    public OCDTextLine asTextLine()
    {
        return this instanceof OCDTextLine ? (OCDTextLine) this : null;
    }

    public OCDText asText()
    {
        return (OCDText) this;
    }

    public OCDPath asPath()
    {
        return (OCDPath) this;
    }

    public OCDImage asImage()
    {
        return this instanceof OCDImage ? (OCDImage) this : null;
    }

    public OCDPaintableLeaf asLeaf()
    {
        return asPaintableLeaf();
    }

    public OCDPaintableLeaf asPaintableLeaf()
    {
        return this instanceof OCDPaintableLeaf ? (OCDPaintableLeaf) this : null;
    }

    public OCDFlow asFlow()
    {
        return (OCDFlow) this;
    }

    public OCDPaintable canonize()
    {
        return this;
    }

    public OCDPaintable uncanonize()
    {
        return this;
    }

    public synchronized boolean remove()
    {
        OCDNode node = this.parent();
        return node instanceof OCDGroup ? ((OCDGroup) node).remove(this) : false;
    }

    public void freeMemory()
    {
        for (OCDPaintable node : children())
            node.freeMemory();
    }

    @Override
    public OCDPaintable copy()
    {
        return this;
    }

    public JsonMap toJson() {
        JsonMap json = new JsonMap();

        json.put("class", this.getClass().getSimpleName());
        json.put("name", this.name);

        // Bounding box
        Rectangle3 bounds = this.bounds();
        JsonMap boundsJson = new JsonMap();
        boundsJson.put("x", bounds.x);
        boundsJson.put("y", bounds.y);
        boundsJson.put("width", bounds.width);
        boundsJson.put("height", bounds.height);
        json.put("bounds", boundsJson);

        return json;
    }
}
