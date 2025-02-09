package sugarcube.formats.ocd.objects;

import sugarcube.common.data.json.JsonArray;
import sugarcube.common.data.json.JsonMap;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Map3;
import sugarcube.common.data.collections.Set3;
import sugarcube.common.data.collections.StringSet;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.formats.ocd.OCD;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class OCDAnnotations extends OCDNode implements Iterable<OCDAnnot>
{
  public static final String TAG = "annotations";

  private final Map3<String, OCDAnnot> map = new Map3<>();

  public OCDAnnotations(OCDNode parent)
  {
    super(TAG, parent);
  }

  public boolean isEmpty()
  {
    return map.isEmpty();
  }

  public OCDAnnot first()
  {
    return map.firstValue();
  }

  public OCDAnnotations put(OCDAnnot annot)
  {
    map.put(annot.needID(), annot);
    return this;
  }

  public boolean isAnnot(Shape shape)
  {
    for (OCDAnnot annot : map.values())
      if (annot.contains(shape))
        return true;
    return false;
  }

  public boolean isAnnot(Point2D point)
  {
    for (OCDAnnot annot : map.values())
      if (annot.contains(point))
        return true;
    return false;
  }

  public String[] ids()
  {
    return map.keySet().toArray(new String[0]);
  }

  @Override
  public OCDAnnotations clear()
  {
    for (OCDAnnot annot : map)
      annot.setParent(null);
    this.map.clear();
    return this;
  }

  @Override
  public boolean delete(OCDNode child, boolean dispose)
  {
    if (map.remove(child.id()) != null)
    {
      if (dispose)
        child.dispose();
      return true;
    }
    return false;
  }

  public boolean has(String id)
  {
    return this.map.has(id);
  }

  public boolean hasnt(String id)
  {
    return this.map.hasnt(id);
  }

  public boolean contains(String id)
  {
    return this.map.has(id);
  }

  public Rectangle3 bounds(String id, Rectangle3 def)
  {
    return has(id) ? get(id).bounds() : def;
  }

  public OCDAnnot get(String id)
  {
    return this.map.get(id);
  }

  public void clear(String... types)
  {
    if (types == null || types.length == 0)
      map.clear();

    clear(new StringSet(types));
  }

  public void clear(Set<String> types)
  {
    for (OCDAnnot annot : this.type(types))
    {
      map.remove(annot.id());
    }
  }

  public OCDAnnotations overlap(Rectangle3 box, double ratio)
  {
    OCDAnnotations annots = new OCDAnnotations(null);
    for (OCDAnnot annot : this)
      if (annot.bounds().overlap(box) >= ratio)
        annots.put(annot);
    return annots;
  }

  public OCDAnnotations layouts(String classname)
  {
    OCDAnnotations annots = new OCDAnnotations(null);
    for (OCDAnnot annot : this)
      if (annot.isLayout(classname))
        annots.put(annot);
    return annots;
  }

  public OCDAnnotations id(String... ids)
  {
    OCDAnnotations annots = new OCDAnnotations(null);
    for (String id : ids)
      if (map.has(id))
        annots.put(map.get(id));
    return annots;
  }

  public OCDAnnotations classnamed(String... classnames)
  {
    StringSet set = new StringSet(classnames);
    return set.isEmpty() ? this : classnamed(set);
  }

  public OCDAnnotations classnamed(Set3<String> classnames)
  {
    OCDAnnotations annots = new OCDAnnotations(null);
    for (OCDAnnot annot : this)
      if (classnames.has(annot.classname(true, "")))
        annots.put(annot);
    return annots;
  }

  public OCDAnnotations layouts()
  {
    return type(OCDAnnot.TYPE_LAYOUT);
  }

  public OCDAnnotations viewBoxes()
  {
    return type(OCDAnnot.TYPE_VIEWBOX);
  }

  public OCDAnnotations quizzes()
  {
    return type(OCDAnnot.TYPE_QUIZ);
  }

  public OCDAnnotations links()
  {
    return type(OCDAnnot.TYPE_LINK);
  }

  public OCDAnnotations links(boolean withAnchors)
  {
    OCDAnnotations annots = new OCDAnnotations(null);
    for (OCDAnnot annot : map.values())
      if (annot.isLink() && (withAnchors || !annot.isAnchorLink()))
        annots.put(annot);
    return annots;
  }

  public OCDAnnotations anchorLinks()
  {
    OCDAnnotations annots = new OCDAnnotations(null);
    for (OCDAnnot annot : map.values())
      if (annot.isAnchorLink())
        annots.put(annot);
    return annots;
  }

  public OCDAnnotations type(String... types)
  {
    if (types == null || types.length == 0)
      return this;
    if (types.length == 1)
    {
      OCDAnnotations annots = new OCDAnnotations(null);
      for (OCDAnnot annot : map.values())
        if (annot.isType(types[0]))
          annots.put(annot);
      return annots;
    } else
      return type(new StringSet(types));
  }

  public OCDAnnotations type(Set<String> types)
  {
    OCDAnnotations annots = new OCDAnnotations(null);
    for (OCDAnnot annot : map.values())
      if (types.contains(annot.type()))
        annots.put(annot);
    return annots;
  }

  public OCDAnnotations links(String... links)
  {
    if (links == null || links.length == 0)
      return this;
    return links(new StringSet(links));
  }

  public OCDAnnotations links(Set<String> links)
  {
    OCDAnnotations annots = new OCDAnnotations(null);
    for (OCDAnnot annot : map.values())
      if (links.contains(annot.link()))
        annots.put(annot);
    return annots;
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
    if (OCD.isAnnot(child))
      return new OCDAnnot(this, child.value("id", null), child.value("type", ""));
    return null;
  }

  @Override
  public void endChild(XmlINode child)
  {
    if (child == null)
      return;
    else if (OCD.isAnnot(child))
      addAnnotation((OCDAnnot) child);
  }

  public OCDAnnot addAnnotation(OCDAnnot annot)
  {
    annot.setParent(this);
    // Log.debug(this,
    // ".addAnnotation - "+annot.type()+": "+annot.reference()+annot.bounds());
    this.put(annot);
    return annot;
  }

  public OCDAnnot addAnnotation(String id, String type, Rectangle3 bounds)
  {
    return addAnnotation(new OCDAnnot(this, id, type, bounds));
  }

  public OCDAnnot addViewboxAnnot(Rectangle3 bounds, String id)
  {
    return this.addAnnotation(id, OCDAnnot.TYPE_VIEWBOX, bounds.copy());
  }

  public OCDAnnot addLayoutAnnot(Rectangle3 bounds, String classname)
  {
    return addAnnotation(null, OCDAnnot.TYPE_LAYOUT, bounds.copy()).setClassname(classname);
  }

  public OCDAnnot addValidationAnnot(Rectangle3 bounds, String classname)
  {
    return addAnnotation(null, OCDAnnot.TYPE_VALIDATION, bounds.copy()).setClassname(classname);
  }

  public OCDAnnot addLinkAnnot(Rectangle3 bounds, String link)
  {
    return addAnnotation(null, OCDAnnot.TYPE_LINK, bounds.copy()).setLink(link);
  }

  public OCDAnnot addFormAnnot(Rectangle3 bounds, String field)
  {
    return addAnnotation(null, OCDAnnot.TYPE_FORM, bounds.copy()).set(OCDAnnot.PROP_FIELD, field);
  }

  public OCDAnnot addReferenceAnnot(Rectangle3 bounds, String id)
  {
    return addAnnotation(null, OCDAnnot.TYPE_REFERENCE, bounds.copy()).setLink(id);
  }

  public OCDAnnot removeAnnotation(String ref)
  {
    return this.map.remove(ref);
  }

  public void removeLayoutAnnotations(String classname)
  {
    Iterator<OCDAnnot> it = this.iterator();
    while (it.hasNext())
      if (it.next().isLayout(classname))
      {
        Log.debug(this, ".removeAnnotations - " + classname);
        it.remove();
      }
  }

  public OCDAnnot removeAnnotation(OCDAnnot annot)
  {
    return this.removeAnnotation(annot.id());
  }

  @Override
  public List<? extends OCDNode> children()
  {
    return map.list();
  }

  @Override
  public String sticker()
  {
    return "Annotations[" + this.nbOfChildren() + "]";
  }

  // @Override
  // public void generateIDs(Counter counter)
  // {
  // for (String name : annots.keySet())
  // annots.get(name).generateIDs(counter);
  // }
  @Override
  public void paint(Graphics3 g, OCD.ViewProps props)
  {
    for (OCDNode node : children())
      node.paint(g, props);
  }

  @Override
  public Iterator<OCDAnnot> iterator()
  {
    return this.map.values().iterator();
  }

  @Override
  public OCDAnnotations copy()
  {
    OCDAnnotations copy = new OCDAnnotations((OCDPage) parent);
    for (OCDAnnot annot : this)
      copy.addAnnotation(annot.copy());
    return copy;
  }

  public JsonArray toJson() {
    // Annotations collection
    JsonArray annotationsArray = new JsonArray();
    for (OCDAnnot annot : this) {
      annotationsArray.add(annot.toJson());
    }

    return annotationsArray;
  }

}
