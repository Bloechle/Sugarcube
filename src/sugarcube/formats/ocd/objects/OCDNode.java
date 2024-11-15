package sugarcube.formats.ocd.objects;

import sugarcube.common.data.collections.Cmd;
import sugarcube.common.data.collections.Props;
import sugarcube.common.data.collections.Str;
import sugarcube.common.data.Base;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.interfaces.APIUnjammable;
import sugarcube.common.interfaces.Boundable;
import sugarcube.common.interfaces.Visitor;
import sugarcube.common.system.io.SoftVersion;
import sugarcube.common.data.xml.*;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.document.OCDProperties;
import sugarcube.formats.ocd.objects.lists.OCDMap;

import java.awt.geom.AffineTransform;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class OCDNode extends XmlNode implements Boundable, Cmd.Handler, Xmlizer, APIUnjammable
{
  protected static final List<OCDNode> NO_CHILD = Collections.emptyList();
  protected Props props = new Props(2);// minimum init size
  public transient boolean isTreeViewExpanded;
    

  public OCDNode(String tag)
  {
    super(tag, null);
  }

  public OCDNode(String tag, OCDNode parent)
  {
    super(tag, parent);
  }
  
  public Props props()
  {
    return props;
  }
  
  public String prop(String key, String def)
  {
    return props.string(key, def);
  }
  
  public boolean hasPropValue(String key)
  {
    return Str.HasData(prop(key, ""));
  }

  public OCDProperties properties()
  {
    return new OCDProperties(this, props);
  }

  public boolean hasContent()
  {
    return !this.children().isEmpty();
  }

  public boolean isGrouped(String... types)
  {
    return isGrouped(-1, types);
  }

  public boolean isGrouped(int level, String... types)
  {
    return false;// only paintable nodes may be grouped
  }
  
  public int index()
  {
    OCDNode parent = parent();
    return parent == null ? -1 : parent.childIndex(this);
  }

  @Override
  public String id()
  {
    return id == null ? props.get("id", null) : id;
  }

  public boolean hasID()
  {
    return !Str.IsVoid(id());
  }

  public String needID()
  {
    return needID(tag);
  }

  public String needID(String prefix)
  {
    return Str.IsVoid(id) ? id = autoID(prefix) : id();
  }

  public String autoID()
  {
    return autoID(tag);
  }

  public String autoID(String prefix)
  {
    OCDPage page = this.page();
    if (page == null)
      return prefix + Base.x32.random12();
    OCDMap idMap = page.idMap();
    String id = "";
    int index = 1;
    do
    {
      id = prefix + (index++);
    } while (idMap.has(id));
    return id;
  }     

  protected void writeXmlID(Xml xml)
  {
    xml.write("id", id());
  }

  protected void readXmlID(DomNode dom)
  {
    this.id = dom.value("id", null);
  }

  public long hashValue()
  {
    return this.hashCode();
  }

  public void show()
  {
    setVisibility(true);
  }

  public void hide()
  {
    setVisibility(false);
  }

  @Override
  public boolean isVisible()
  {
    return true;
  }

  public void setVisibility(boolean isVisible)
  {

  }

  public boolean modified()
  {
    return false;
  }

  public OCDNode clear()
  {
    return this;
  }

  public Cmd command(String key)
  {
    return null;
  }

  @Override
  public void command(Cmd cmd)
  {
  }

  @Override
  public synchronized Collection<? extends OCDNode> children()
  {
    return NO_CHILD;
  }

  @Override
  public OCDNode parent()
  {
    return (OCDNode) parent;
  }

  @Override
  public Xmlizer xmlizer()
  {
    return this;
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    return this.children();
  }

  @Override
  public void readAttributes(DomNode dom)
  {
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

  public OCDDocument doc()
  {
    return document();
  }

  public OCDDocument document()
  {
    return parent == null ? null : parent().document();
  }

  public SoftVersion version()
  {
    OCDDocument ocd = document();
    return ocd == null ? OCD.VERSION : ocd.version;
  }

  public OCDPage page()
  {
    return parent == null ? null : parent().page();
  }
  
  public OCDPage modifyPage()
  {
    OCDPage page = page();
    return page==null ? null : page.modify();    
  }

  public int pageNb()
  {
    OCDPage page = page();
    return page == null ? 0 : page.number();
  }

  public String pageID()
  {
    OCDPage page = page();
    return page == null ? "pnone" : page.id();
  }

  public Rectangle3 displayBounds()// backcomp
  {
    return viewBounds();
  }

  public Rectangle3 viewBounds()
  {
    return this.viewBounds(page());
  }

  public Rectangle3 viewBounds(OCDPage page)
  {
    OCDDocument doc = page == null ? null : page.document();
    Rectangle3 box = bounds();
    if (box == null)
      return null;
    else
    {
      Point3 o = page == null ? new Point3() : page.viewBox().origin();
      float scale = doc == null ? 1 : doc.viewProps.scale;
      return new Rectangle3(scale * (box.x - o.x), scale * (box.y - o.y), scale * box.width, scale * box.height);
    }
  }

  @Override
  public Rectangle3 bounds()
  {
    Rectangle3 box = null;
    for (OCDNode node : children())
      box = box == null ? node.bounds().copy() : box.include(node.bounds());
    return box == null ? new Rectangle3() : box;
  }

  public Rectangle3 bounds(boolean bbox)
  {
    if (bbox)
    {
      Rectangle3 box = null;
      for (OCDNode node : children())
        box = box == null ? node.bounds(bbox).copy() : box.include(node.bounds(bbox));
      return box == null ? new Rectangle3() : box;
    } else
      return bounds();
  }

  public Transform3 viewTransform()
  {
    return this.viewTransform(page());
  }

  public Transform3 viewTransform(OCDPage page)
  {
    OCDDocument doc = page == null ? null : page.document();
    return page == null ? new Transform3() : page.toView(doc == null ? null : doc.viewProps);
  }

  public void transform(AffineTransform transform)
  {
    for (OCDNode node : children())
      node.transform(transform);
  }

  public boolean isPaintable()
  {
    return this instanceof OCDPaintable;
  }

  public boolean isPaintableLeaf()
  {
    return this instanceof OCDPaintableLeaf;
  }

  public void paint(Graphics3 g, OCD.ViewProps props)
  {
    for (OCDNode node : children())
      node.paint(g, props);
  }

  @Override
  public String toString()
  {
    return Xml.toString(this);
  }

  public OCDNode copy()
  {
    return this;
  }

  public void copyTo(OCDNode node)
  {
    super.copyTo(node);
    node.props = this.props.copy();
  }

  public OCDNode refresh()
  {
    return this;
  }

  public void dispose()
  {
    for (OCDNode node : this.children())
      node.dispose();
    this.children().clear();
    this.parent = null;
  }

  public boolean delete()
  {
    // Log.debug(this, ".delete");
    return delete(true);
  }

  public boolean delete(boolean dispose)
  {
    return this.parent == null ? false : this.parent().delete(this, dispose);
  }

  public boolean delete(OCDNode child)
  {
    return delete(child, true);
  }

  public boolean delete(OCDNode child, boolean dispose)
  {
    if (this.children().remove(child))
    {
      if (dispose)
        child.dispose();
      return true;
    }
    return false;
  }

  public boolean visit(Visitor<OCDNode> visitor)
  {
    return visit(this, visitor);
  }

  public static boolean visit(OCDNode node, Visitor<OCDNode> visitor)
  {
    for (OCDNode child : node.children())
    {
      if (visitor.visit(child))
        return true;
      if (visit(child, visitor))
        return true;
    }
    return false;
  }

  // public void visitNode(Visitable<OCDNode> visitor)
  // {
  // this.visitNode(visitor, new Bool(false));
  // }
  //
  // public void visitNode(Visitable<OCDNode> visitor, Bool doStop)
  // {
  // visitor.visit(this, doStop);
  // for (OCDNode node : children())
  // if (doStop.isFalse())
  // node.visit(visitor, doStop);
  // }
}
