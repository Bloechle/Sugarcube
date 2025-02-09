package sugarcube.formats.ocd.objects;

import sugarcube.common.data.json.Json;
import sugarcube.common.data.json.JsonArray;
import sugarcube.common.data.json.JsonMap;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Str;
import sugarcube.common.data.collections.StringMap;
import sugarcube.common.data.collections.StringSet;
import sugarcube.common.graphics.geom.Line3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Image3;
import sugarcube.common.graphics.Stroke3;
import sugarcube.common.interfaces.ObjectTester;
import sugarcube.common.system.io.File3;
import sugarcube.common.ui.gui.Font3;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Nb;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.analysis.ROrder;
import sugarcube.formats.ocd.objects.lists.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class OCDGroup<T extends OCDPaintable> extends OCDPaintable implements Iterable<T>
{

    public static final String TAG = "g";

    public static final String FLOW = "flow";
    public static final String PARAGRAPH = "paragraph";
    public static final String TEXTLINE = "tline";
    public static final String CONTENT = "content";
    public static final String LAYER = "layer";
    public static final String FOOTNOTE = "footnote";
    public static final String TABLE = "table";
    public static final String CELL = "cell";
    public static final String COLUMN = "column";
    protected OCDPaintables<T> nodes = new OCDPaintables<>();
    protected String type = OCD.UNDEF;
    protected String reading = ROrder.UNDEF;
    protected Line3 extent = null;// null required if undef

    public OCDGroup()
    {
        super(TAG);
    }

    public OCDGroup(OCDNode parent)
    {
        super(TAG, parent);
    }

    public OCDGroup(OCDNode parent, T... nodes)
    {
        super(TAG, parent);
        for (T node : nodes)
            this.add(node);
    }

    public OCDGroup(String type)
    {
        super(TAG);
        this.type = type;
    }

    public OCDGroup(String type, OCDNode parent)
    {
        super(TAG, parent);
        this.type = type;
    }

    protected OCDGroup(String type, OCDNode parent, String tag)
    {
        super(tag, parent);
        if (type != null)
            this.type = type;
    }

    public OCDList links(boolean withAnchors)
    {
        return links(new OCDList(), withAnchors);
    }

    public OCDList links(OCDList list, boolean withAnchors)
    {
        if (list == null)
            list = new OCDList();
        for (OCDPaintable node : this)
        {
            if (node.hasLink() && (withAnchors || !node.hasAnchorLink()))
                list.add(node);
            if (node.isGroup())
                node.asGroup().links(list, withAnchors);
        }
        return list;
    }

    public OCDList events()
    {
        return events(new OCDList());
    }

    public OCDList events(OCDList list)
    {
        if (list == null)
            list = new OCDList();
        for (OCDPaintable node : this)
        {
            if (node.hasEvent())
                list.add(node);
            if (node.isGroup())
                node.asGroup().events(list);
        }
        return list;
    }

    public void setClipID(String clipID)
    {
        for (OCDPaintable node : this)
            node.setClipID(clipID);
    }

    public void shift(float dx, float dy)
    {
        for (OCDPaintable node : this)
            node.shift(dx, dy);
    }

    public OCDGroup<T> remove(ObjectTester<T> removeTester)
    {
        Iterator<T> it = this.iterator();
        while (it.hasNext())
            if (removeTester.condition(it.next()))
                it.remove();
        return this;
    }

    public boolean isType(String... types)
    {
        if (types == null || types.length == 0)
            return true;
        for (String t : types)
            if (this.type.equals(t))
                return true;
        return false;
    }

    public void ungroup()
    {
        OCDGroup<OCDPaintable> parent = (OCDGroup<OCDPaintable>) parent();
        for (OCDPaintable node : this)
            parent.add(node);
        parent.remove(this);
    }

    @Override
    public Rectangle3 bounds()
    {
        if (extent != null)
            return extent.bounds();
        Rectangle3 box = null;
        for (OCDNode node : children())
        {
            Rectangle3 r = node.bounds();
            if (r.width > 0 || r.height > 0)
                box = box == null ? node.bounds().copy() : box.include(node.bounds());
        }
        return box == null ? new Rectangle3() : box;
    }

    public void writeBoundsToJson(JsonMap json)
    {

        Rectangle3 bounds = this.bounds();
        JsonMap boundsJson = new JsonMap();
        boundsJson.put("x", bounds.x);
        boundsJson.put("y", bounds.y);
        boundsJson.put("width", bounds.width);
        boundsJson.put("height", bounds.height);
        json.put("bounds", boundsJson);
    }

    public float width()
    {
        return bounds().width;
    }

    public float height()
    {
        return bounds().height;
    }

    public String type()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public boolean isReading(String reading)
    {
        return this.reading.equals(reading);
    }

    public String reading()
    {
        return reading;
    }

    public void setReading(String reading)
    {
        this.reading = reading;
    }

    @Override
    public Line3 extent()
    {
        return extent == null ? bounds().extent() : extent;
    }

    public Line3 needExtent()
    {
        return extent = extent();
    }

    public boolean hasExtent()
    {
        return extent != null;
    }

    @Override
    public void setExtent(Line3 extent)
    {
        if (this.extent == null)
            this.extent = extent.copy();
        else
            this.extent.setLine(extent);
    }

    @Override
    public boolean hasContent()
    {
        return !this.nodes.isEmpty();
    }

    @Override
    public void ensureParents()
    {
        super.ensureParents();
    }

    public int generateZOrder()
    {
        return zOrderize(0);
    }

    public int zOrderize(int counter)
    {
        for (OCDPaintable node : this)
        {
            node.zOrder = ++counter;
            // Log.debug(this, ".zOrderize - "+node.tag+": "+counter);
            if (node.isGroup())
                counter = node.asGroup().zOrderize(counter);
        }
        return counter;
    }

    public void setZOrder(int z)
    {
        this.zOrder = z;
        for (OCDPaintable node : this)
            node.zOrder = z;
    }

    @Override
    public float zOrder()
    {
        return zOrder > 0 ? zOrder : zOrderMax();
    }

    public float zOrderMean()
    {
        float sum = 0;
        int size = 0;
        for (OCDPaintable p : this)
        {
            sum += p.zOrder();
            size++;
        }
        return size == 0 ? 0 : sum / size;
    }

    public float zOrderMin()
    {
        float min = Float.POSITIVE_INFINITY;
        float z;
        for (OCDPaintable p : this)
            if ((z = p.isGroup() ? p.asGroup().zOrderMin() : p.zOrder) < min)
                min = z;
        return min == Float.POSITIVE_INFINITY ? 0 : min;
    }

    public float zOrderMax()
    {
        float max = Float.NEGATIVE_INFINITY;
        float z;
        for (OCDPaintable p : this)
            if ((z = p.isGroup() ? p.asGroup().zOrderMax() : p.zOrder) > max)
                max = z;
        return max == Float.NEGATIVE_INFINITY ? 0 : max;
    }

    public List3<T> zOrderedGraphics()
    {
        List3<T> zOrdered = new List3<>(nodes);
        Collections.sort(zOrdered, OCDPaintable.ZOrderComparator());
        return zOrdered;
    }

    public OCDList zOrderedPrimitives(boolean keepTextBlockStructure)
    {
        OCDList list = addPrimitivesToList(new OCDList(), keepTextBlockStructure);
        list.sortZOrder();
        return list;
    }

    public OCDList addPrimitivesToList(OCDList list, boolean keepTextBlockStructure)
    {
        for (OCDPaintable node : nodes)
            if (node instanceof OCDGroup && !(keepTextBlockStructure && node instanceof OCDTextBlock))
                ((OCDGroup<OCDPaintable>) node).addPrimitivesToList(list, keepTextBlockStructure);
            else
                list.add(node);
        return list;
    }

    public List3<OCDPaintableLeaf> zOrderedLeaves()
    {
        List3<OCDPaintableLeaf> leaves = leaves();
        Collections.sort(leaves, OCDPaintable.ZOrderComparator());
        return leaves;
    }

    public List3<OCDPaintableLeaf> leaves()
    {
        return new Flatten<OCDPaintableLeaf>(this).list(OCDText.TAG, OCDImage.TAG, OCDPath.TAG);
    }

    public OCDList allNodes()
    {
        return (OCDList) new Flatten<OCDPaintable>(this).list(new OCDList());
    }

    public OCDTexts allTexts()
    {
        return (OCDTexts) new Flatten<OCDText>(this).list(new OCDTexts(), OCDText.TAG);
    }

    public OCDPaths allPaths()
    {
        return (OCDPaths) new Flatten<OCDPath>(this).list(new OCDPaths(), OCDPath.TAG);
    }

    public List3<OCDImage> allImages()
    {
        return new Flatten<OCDImage>(this).list(OCDImage.TAG);
    }

    public List3<OCDGroup<OCDPaintable>> allGroups()
    {
        return new Flatten<OCDGroup<OCDPaintable>>(this).list(OCDGroup.TAG);
    }

    public StringMap<OCDImage> imagesMap()
    {
        StringMap<OCDImage> idMap = new StringMap<>();
        for (OCDImage image : allImages())
            if (image.hasID())
                idMap.put(image.id(), image);

        return idMap;
    }

    public StringMap<OCDContent> subContentsMap()
    {
        StringMap<OCDContent> idMap = new StringMap<>();
        for (OCDContent content : subContents())
            if (content.hasID())
                idMap.put(content.id(), content);
        return idMap;
    }

    public boolean contains(OCDPaintable node)
    {
        for (OCDPaintable child : nodes)
            if (child == node || child.isGroup() && child.asGroup().contains(node))
                return true;
        return false;
    }

    public OCDContents subContents()
    {
        return subContents(null);
    }

    public OCDContents subContents(OCDContents list)
    {
        if (list == null)
            list = new OCDContents();
        for (OCDPaintable child : this)
        {
            if (child.isGroupContent())
                list.add(child.asContent());
            if (child.isGroup())
                child.asGroup().subContents(list);
        }
        return list;
    }

    public OCDGroups groups(String... types)
    {
        // -1 means deep search
        return this.groups(-1, types);
    }

    public OCDGroups groups(int level, String... types)
    {
        return groups(new OCDGroups(), new StringSet(types), level);
    }

    protected OCDGroups groups(OCDGroups list, StringSet types, int level)
    {
        // since content is and is not a group, and is obviously not what we are
        // looking for... (extends group but not OCD.g)...
        if ((types == null || types.isEmpty() || types.contains(type)) && !this.is(OCDContent.TAG))
            list.add(this);// if we add this group, we do not dig into it any more
        else if (level != 0)
            for (OCDPaintable node : syncGraphics())
                if (node.is(TAG))
                    node.asGroup().groups(list, types, level - 1);
        return list;
    }

    public OCDTable table(String... names)
    {
        OCDTables tables = tables(names);
        return tables == null || tables.isEmpty() ? null : tables.first();
    }

    public OCDTables tables(String... names)
    {
        OCDGroups tables = groups(OCDGroup.TABLE);
        OCDTables select = new OCDTables();
        StringSet set = new StringSet(names);
        for (OCDGroup group : tables)
        {
            OCDTable table = (OCDTable) group;
            if (set.isEmpty() || set.has(table.name))
                select.add(table);
        }
        return select;
    }

    public OCDLayer layer(String... names)
    {
        OCDLayers layers = layers(names);
        return layers == null || layers.isEmpty() ? null : layers.first();
    }

    public OCDLayers layers(String... names)
    {
        OCDGroups layers = groups(OCDGroup.LAYER);
        OCDLayers select = new OCDLayers();
        StringSet set = new StringSet(names);
        for (OCDGroup group : layers)
        {
            OCDLayer layer = (OCDLayer) group;
            if (set.isEmpty() || set.has(layer.name))
                select.add(layer);
        }
        return select;
    }

    public OCDLines textlines()
    {
        OCDLines blocks = new OCDLines();
        for (OCDGroup g : this.groups(-1, OCDGroup.TEXTLINE))
            blocks.add((OCDTextLine) g);
        return blocks;
    }

    public OCDBlocks paragraphs()
    {
        return paragraphs(-1);
    }

    public OCDBlocks paragraphs(int level)
    {
        OCDBlocks blocks = new OCDBlocks();
        for (OCDGroup g : this.groups(level, OCDGroup.PARAGRAPH))
            blocks.add((OCDTextBlock) g);
        return blocks;
    }

    public OCDBlocks blocks()
    {
        return paragraphs();
    }

    public OCDTextBlock firstBlock()
    {
        return blocks().first();
    }

    public OCDTextLine firstLine()
    {
        OCDTextBlock block = firstBlock();
        return block == null ? null : block.first();
    }

    public StringSet blockNeedIDs()
    {
        StringSet set = new StringSet();
        for (OCDTextBlock block : blocks())
            set.add(block.needID());
        return set;
    }

    public void setGraphics(OCDPaintables<T> nodes)
    {
        this.nodes = nodes;
    }

    public OCDPaintables<T> graphics()
    {
        return nodes;
    }

    public OCDPaintables<T> syncGraphics()
    {
        OCDPaintables<T> copy = new OCDPaintables<>();
        copy.addAll(nodes);
        return copy;
    }

    public OCDPaintable paintAt(Point2D p)
    {
        OCDTextBlock block = null;
        OCDGroup group = null;
        OCDImage image = null;
        OCDPath path = null;
        for (OCDPaintable node : this)
            if (node.viewBounds().contains(p))
                if (OCD.isTextBlock(node))
                    block = node.asTextBlock();
                else if (node.isGroup())
                    group = node.asGroup();
                else if (node.isImage())
                    image = node.asImage();
                else if (node.isPath() && node.asPath().viewShape().contains(p))
                    path = node.asPath();
        return block != null ? block : group != null ? group : image != null ? image : path;
    }

    // @Override
    // public synchronized OCDGroup normalize()
    // {
    // for (OCDPaintable node : this)
    // node.normalize();
    // return this;
    // }

    @Override
    public boolean isGrouped(int level, String... types)
    {
        if (types == null || types.length == 0)
            return true;
        for (String t : types)
            if (type.equals(t))
                return true;
        return level == 0 || parent == null ? false : parent().isGrouped(level - 1, types);
    }

    @Override
    public String autoID()
    {
        if (this.isEmpty())
            return super.autoID();
        // don't change that (don't even override)
        String firstID = this.first().autoID();
        int i = firstID.indexOf("-");
        return firstID.substring(0, i < 0 ? 0 : i) + "-s" + type + firstID.substring(i < 0 ? 0 : i);
    }

    public boolean isEmpty()
    {
        return this.nodes.isEmpty();
    }

    public boolean isPopulated()
    {
        return !this.nodes.isEmpty();
    }

    public int size()
    {
        return this.nbOfChildren();
    }

    public void writeGroupAttributes(Xml xml)
    {
        this.writeXmlID(xml);
        this.writeXmlName(xml);
        if (type != null && !type.equals(OCD.UNDEF))
            xml.write("type", type);
        if (name != null && !name.isEmpty())
            xml.write("name", name);
        this.writeXmlGroupID(xml);
        if ((type.equals(CONTENT) || type.equals("pgroup")) && !reading.equals(ROrder.UNDEF))
            xml.write("reading", reading);
        if (extent != null)
            xml.write("extent", extent.array());
    }

    @Override
    public Collection<? extends OCDNode> writeAttributes(Xml xml)
    {
        this.writeGroupAttributes(xml);
        this.props.writeAttributes(xml);
        return this.children();
    }

    @Override
    public void readAttributes(DomNode dom)
    {
        this.readXmlID(dom);
        this.readXmlName(dom);
        this.type = dom.value("type", OCD.UNDEF);
        this.readXmlGroupID(dom);
        this.reading = dom.value("reading", ROrder.UNDEF);
        if (dom.has("extent"))
            this.extent = new Line3(dom.reals("extent", 0));
        this.props.readAttributes(dom);
    }

    @Override
    public XmlINode newChild(DomNode child)
    {
        switch (child.tag())
        {
            case OCDGroup.TAG:
                switch (child.value("type", ""))
                {
                    // backcomp
                    case "tblock":
                    case OCDGroup.PARAGRAPH:
                        return new OCDTextBlock(this);
                    case OCDGroup.TEXTLINE:
                        return new OCDTextLine(this);
                    case OCDGroup.TABLE:
                        return new OCDTable(this);
                    case OCDGroup.CELL:
                        return new OCDTableCell(this);
                    case OCDGroup.FLOW:
                        return new OCDFlow(this);
                    case OCDGroup.LAYER:
                        return new OCDLayer(this, "");
                    case OCDGroup.FOOTNOTE:
                        return new OCDFootnote(this);
                    default:
                        return new OCDContent(this);
                }
            case OCDImage.TAG:
                return new OCDImage(this);
            case OCDPath.TAG:
                return new OCDPath(this);
            case OCDText.TAG:
                return new OCDText(this);
            case OCDClip.TAG:
                return new OCDClip(this);
        }
        return null;
    }

    @Override
    public void endChild(XmlINode child)
    {
        if (child == null)// i.e., all children have been added
            this.zOrder = this.zOrderMax();
        else
            add((T) child);
    }

    public void add(T node, T anchor)
    {
        add(node, anchor, false);
    }

    public void addBefore(T node, T anchor)
    {
        add(node, anchor, true);
    }

    public void addAfter(T node, T anchor)
    {
        add(node, anchor, false);
    }

    public void add(T node, T anchor, boolean before)
    {
        if (anchor == null)
        {
            this.add(node);
            return;
        }

        int index = 0;
        for (T n : nodes)
        {
            index++;
            if (n == anchor)
                break;
        }
        this.add(before ? index - 1 : index, node);
    }

    public void addOnTop(T node)
    {
        node.zOrder = this.zOrderMax() + 1;
        this.add(node);
    }

    public void add(int index, T node)
    {
        node.setParent(this);
        this.nodes.add(index, node);
    }

    public void add(T node)
    {
        node.setParent(this);
        this.nodes.add(node);
    }

    public void addInFlow(T node)
    {
        node.setParent(this);
        this.nodes.add(node);
    }

    public void add(T node, boolean doSetParent)
    {
        if (doSetParent)
            node.setParent(this);
        this.nodes.add(node);
    }

    public boolean put(T node)
    {
        for (OCDPaintable p : this)
            if (node == p)
                return false;
        add(node);
        return true;
    }

    public void addAll(T... nodes)
    {
        for (T node : nodes)
            this.add(node);
    }

    public void addAll(Iterable<? extends T> nodes)
    {
        for (T node : nodes)
            this.add(node);
    }

    public void setAll(Iterable<? extends T> nodes)
    {
        this.clear();
        this.addAll(nodes);
    }

    public synchronized boolean replace(T oldNode, T newNode)
    {
        int i = this.nodes.indexOf(oldNode);
        if (i >= 0 && oldNode != null && newNode != null)
        {
            this.nodes.remove(i);
            newNode.setParent(this);
            this.nodes.add(i, newNode);
            return true;
        }
        return false;
    }

    public synchronized boolean remove(T node)
    {
        return this.nodes.remove(node);
    }

    @Override
    public OCDGroup<T> clear()
    {
        while (!nodes.isEmpty())
        {
            OCDNode node = nodes.removeFirst();
            node.setParent(null);
        }
        return this;
    }

    public OCDList findByName(String name)
    {
        return findByName(name, new OCDList());
    }
    public OCDList findByName(String name, OCDList list)
    {
        for(OCDPaintable node: nodes)
        {
            if(node.isName(name))
                list.add(node);
            if(node.isGroup())
                node.asGroup().findByName(name, list);
        }
        return list;
    }

    public OCDPaintables<T> nodes()
    {
        return nodes;
    }

    @Override
    public synchronized List3<T> children()
    {
        return nodes;
    }

    @Override
    public Iterator<T> iterator()
    {
        return nodes.iterator();
    }

    public T first(boolean first)
    {
        return first ? first() : last();
    }

    public T last(boolean last)
    {
        return last ? last() : first();
    }

    public T first()
    {
        return nodes.first();
    }

    public T second()
    {
        return nodes.second();
    }

    public T last()
    {
        return nodes.last();
    }

    public T penultimate()
    {
        return nodes.penultimate();
    }

    public T get(int index)
    {
        return nodes.get(index);
    }

    @Override
    public String sticker()
    {
        String id = this.id();
        return "Group[" + type + (hasLabel() ? ", label=" + label() : "") + "]" + (id == null ? "" : " #" + id);
    }

    public void copyTo(OCDGroup<T> g)
    {
        this.copyAttributesTo(g);
        for (T node : this.nodes)
        {
            T copy = (T) node.copy();
            copy.setParent(g);
            g.nodes.add(copy);
        }
    }

    public void copyAttributesTo(OCDGroup<T> g)
    {
        super.copyTo(g);
        g.type = this.type;
        g.extent = extent == null ? null : this.extent.copy();
        g.reading = this.reading;
        g.props.putAll(this.props);
    }

    @Override
    public OCDGroup<T> copy()
    {
        OCDGroup<T> copy = new OCDGroup<T>(parent());
        this.copyTo(copy);
        return copy;
    }

    public boolean hasText()
    {
        return textIt().hasNext();
    }

    public OCDText delete(OCDText start, int startIndex, OCDText end, int endIndex)
    {
        if (start == end && startIndex == endIndex)
            return end;

        NodeIt<OCDText> it = this.textIt(start, end);
        while (it.hasNext())
        {
            OCDText text = it.next();
            if (text == start && startIndex > 0 || text == end && endIndex < end.length())
                continue;
            else
                it.remove();
        }

        if (start == end)
            return start.trim(startIndex, endIndex);

        if (startIndex > 0)
            start.trim(startIndex, -1);

        if (endIndex < end.length())
            return end.trim(0, endIndex);

        return start;
    }

    public String string()
    {
        return string(false);
    }

    public String string(boolean withReturn)
    {
        StringBuilder sb = new StringBuilder();
        for (OCDText text : this.textIt())
        {
            String str = text.glyphString();
            OCDTextLine line = text.textLine();
            if (line != null && line.last() == text)
            {
                str = Str.Unends(str, " ");
                str = withReturn ? str + " \n" : (str.endsWith("-") ? Str.Unends(str, "-") : str + " ");
            }

            sb.append(str);

        }
        return sb.toString().trim();
    }

    public String string(String sep)
    {
        StringBuilder sb = new StringBuilder();
        for (OCDText text : this.textIt())
            sb.append(text.glyphString() + sep);
        return sb.toString();
    }

    public NodeIt<OCDText> textIt()
    {
        return new NodeIt<OCDText>(this).filter(OCDText.TAG);
    }

    public NodeIt<OCDText> textIt(OCDText start, OCDText end)
    {
        return new NodeIt<OCDText>(this, start, end).filter(OCDText.TAG);
    }

    public NodeIt<OCDTextLine> lineIt()
    {
        return new NodeIt<OCDTextLine>(this).filter(OCDGroup.TEXTLINE);
    }

    public NodeIt<OCDTextBlock> blockIt()
    {
        return new NodeIt<OCDTextBlock>(this).filter(OCDGroup.PARAGRAPH);
    }

    public Image3 createImage(OCD.ViewProps props, Color3 bgColor)
    {
        return createImage(props, bgColor, null);
    }

    public Image3 createImage(OCD.ViewProps props, Color3 bgColor, ImageThumberCache thumber)
    {
        Rectangle3 box = page().viewBounds(props);
        int type = Image3.TYPE_INT_RGB;
        if (bgColor != null && bgColor.isTransparent())
            type = Image3.TYPE_INT_ARGB;
        else if (!props.use_colors)
            type = Image3.TYPE_BYTE_GRAY;

        Image3 image = thumber == null ? new Image3(box.intWidth(), box.intHeight(), type) : thumber.needIn(box.intWidth(), box.intHeight(), type, bgColor == null || bgColor.isTransparent());
        image.setAccelerationPriority(0.8f);
        paintImage(props, image.graphics(), bgColor);
        return image;
    }

    public Image3 createImage(float sampling, int margin)
    {
        return createImage(this, sampling, 2, bounds().inflate(2 * margin));
    }

    public static Image3 createImage(OCDGroup group, float sampling, float supersampling, Rectangle3 bbox)
    {
        if (group.isEmpty())
            return null;
        Rectangle3 box = bbox == null ? group.bounds(true) : bbox;
        double scale = sampling * supersampling;
        Image3 image = new Image3(box.width * scale, box.height * scale, true);
        OCD.ViewProps props = new OCD.ViewProps(scale);
        props.cropbox = box;
        props.paint_chessboard = false;
        props.box = group.needID();
        group.paintImage(props, image.graphics(), null);
        return image.decimate(1 / supersampling);
    }

    public OCDImage rasterize(float sampling, float supersampling, boolean wrap, Rectangle3 bbox)
    {
        if (this.isEmpty())
            return null;
        Rectangle3 box = bbox == null ? this.bounds(true).inflate(8, 8) : bbox;
        float zOrderMax = this.zOrderMax();

        Image3 raster = createImage(this, sampling, supersampling, box);
        if (raster == null)
            return null;

        OCDImage image = new OCDImage(this.parent());
        image.setImage(raster, 0.95f);
        image.setTransform(1 / sampling, 0, 0, 1 / sampling, box.x, box.y);
        image.setZOrder(zOrderMax);

        this.document().imageHandler.addEntry(image);
        OCDPaintable node = image;
        if (wrap)
            node = new OCDGroup(this.parent(), image);
        if (this.parent instanceof OCDGroup)
            ((OCDGroup) parent).replace(this, node);
        return image;
    }

    @Override
    public void paint(Graphics3 g, OCD.ViewProps props)
    {
        int nbOfPaths = 0;
        for (OCDPaintable node : this.zOrderedLeaves())
            if (!node.isPath() || nbOfPaths++ < OCD.MAX_NB_OF_DISPLAY_PATHS)
                node.paint(g, props);
    }

    public Image3 backImage(OCD.ViewProps props)
    {
        List<OCDNode> list = new Flatten<OCDNode>(this).list(OCDImage.TAG, OCDPath.TAG);
        Image3 context = new Image3((int) page().width(), (int) page().height(), BufferedImage.TYPE_INT_RGB);
        Graphics3 g = context.graphics();
        g.clearWhite();
        for (OCDNode node : list)
            node.paint(g, props);
        return context;
    }

    public void paintImage(OCD.ViewProps props, Graphics3 g, Color3 bgColor)
    {
        if (bgColor != null && !bgColor.isTransparent())
            g.clear(bgColor);

        if (props.paint_chessboard)
            g.clearChessBoard();

        Transform3 otm = g.transform();
        Transform3 vtm = page().toView(props);

        g.concatTransform(vtm);
        this.paint(g, props);
        g.setTransform(otm);

        g.setClip(null);
        Color3 color = props.selectColor.alpha(0.25);
        for (OCDTextBlock block : blocks())
        {
            if (props.show_lines)
                for (OCDTextLine line : block)
                    g.paint(vtm.transform(line.bounds()), color, null);

            if (props.show_blocks)
                g.paint(vtm.transform(block.bounds()).getBounds2D(), color, null);
            // g.drawText(block.readingOrder + "", box.getMinX() - 4, box.getMinY() +
            // 3, XFont.MONOSPACED_FONT.bold(), XColor.BLUE_PIGMENT, XColor.WHITE);
        }

        // Log.debug(this, ".paintImage - show_annots=" + props.show_annots);
        for (OCDAnnot annot : page().annotations())
            if (annot.isViewbox() && props.show_views || !annot.isViewbox() && props.show_annots)
            {
                Rectangle3 r = annot.viewBounds();
                paintBounds(g, r, null, color, color, null);
                Font font = Font3.MONO_FONT.derive(12);
                g.setFont(font);
                String sticker = annot.sticker();
                Rectangle3 box = Graphics3.bounds(sticker, font);
                g.setColor(Color3.WHITE.alpha(0.8));
                g.fill(new Rectangle3(r.x, r.maxY(), box.width, box.height));
                g.setColor(Color3.BURNT_ORANGE);
                g.draw(sticker, r.x - box.x, r.maxY() - box.y);
            }

        Rectangle3 box = page().viewBox(props);
        double grid = props.show_grid;
        double sx = vtm.scaleX();
        double sy = vtm.scaleY();

        double x0 = vtm.x();
        double y0 = vtm.y();
        x0 = x0 < 0 ? (-x0 % grid) : -(x0 % grid);
        y0 = y0 < 0 ? (-y0 % grid) : -(y0 % grid);

        if (grid > 0)
            for (double y = y0; y < box.height + grid; y += grid)
                for (double x = x0; x < box.width + grid; x += grid)
                    g.line(x * sx - 1, y * sy - 1, x * sx, y * sy, Color3.ANTHRACITE.alpha(0.8), 1);

        // g.setTransform(otm);
    }

    public OCDList overlappingNodes(Rectangle3 box)
    {
        OCDList list = new OCDList();
        this.overlappingNodes(box, list);
        return list;
    }

    public void overlappingNodes(Rectangle3 box, List3<OCDPaintable> list)
    {
        for (OCDPaintable node : this)
            if (node.isGroupContent())
                node.asContent().overlappingNodes(box, list);
            else if (node.bounds().overlapThis(box) > Nb.TWO_THIRDS)
                list.add(node);
    }

    public OCDGroup<T> sortX()
    {
        Collections.sort(this.nodes, XComparator());
        return this;
    }

    public OCDGroup<T> sortY()
    {
        Collections.sort(this.nodes, YComparator());
        return this;
    }

    public OCDGroup<T> sortByIndex()
    {
        Collections.sort(this.nodes, IndexComparator());
        return this;
    }

    public static void paintBounds(Graphics3 g3, Shape shape, Transform3 transform, Color3 fill, Color3 stroke, String text)
    {
        int fontsize = 14;
        if (transform != null)
            shape = transform.transform(shape);
        g3.fill(shape, fill.alpha() == 1 ? fill.alpha(0.25) : fill);
        g3.stroke(shape, stroke.alpha() == 1 ? stroke.alpha(0.5) : stroke, Stroke3.LINE);
        if (text != null && !text.isEmpty())
        {
            Rectangle3 bounds = g3.bounds(text, Font3.CALIBRI_FONT.derive(fontsize).bold());
            int x = (int) (shape.getBounds2D().getMaxX() - bounds.width);
            int y = (int) (shape.getBounds2D().getMaxY() - bounds.height);
            g3.draw(text, x - fontsize / 4, y + fontsize, Font3.CALIBRI_FONT.derive(fontsize).bold(), Color3.ANTHRACITE.alpha(0.8));
        }
    }

    public JsonMap toJson() {
        JsonMap json = new JsonMap();
        // Group-specific attributes
        json.put("type", type);
        json.put("type", "group");
        json.put("size", this.size());


        // Add child nodes
        JsonArray childrenArray = new JsonArray();
        for (OCDPaintable node : this.nodes) {
            childrenArray.add(node.toJson());
        }
        json.put("children", childrenArray);

        return json;
    }

    public static void main(String... args)
    {
        OCDDocument ocd = new OCDDocument(new File3(File3.userDesktop(), "P.ocd"));
        OCDPage page = ocd.pageHandler.firstPage();
        page.ensureInMemory();
        OCDTextBlock block = page.content().paragraphs().first();

        NodeIt<OCDText> it = new NodeIt(block);
        if (it.hasNext())
            Log.debug(OCDGroup.class, ".main - hasNext=true");

        while (it.hasNext())
        {
            OCDText text = it.next();
            Log.debug(OCDGroup.class, ".main - text=" + text.string());
        }
        Log.debug(OCDGroup.class, ".main - block was=" + block);
    }

}
