package sugarcube.formats.ocd.objects.lists;

import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.StringSet;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.*;

public class OCDList extends OCDPaintables<OCDPaintable>
{
  public OCDList()
  {

  }

  public OCDList(List3<OCDPaintable> nodes)
  {
    for (OCDPaintable node : nodes)
      this.add(node);    
  }

  public OCDList(OCDNode[] nodes)
  {
    set(nodes);
  }

  public OCDList set(OCDNode[] nodes)
  {
    this.clear();
    if (nodes != null)
      for (OCDNode node : nodes)
        if (node.isPaintable())
          this.add((OCDPaintable) node);
    return this;
  }

  public OCDList set(OCDPaintable... nodes)
  {
    this.clear();
    if (nodes != null)
      for (OCDPaintable node : nodes)
        this.add(node);
    return this;
  }

  @Override
  public boolean add(OCDPaintable node)
  {
    return node == null ? false : super.add(node);
  }

  public OCDList withTags(String... tags)
  {
    StringSet set = new StringSet(tags).trimVoid();
    if (set.isEmpty())
      return this;
    OCDList list = new OCDList();
    for (OCDPaintable node : this)
      if (set.has(node.tag))
        list.add(node);
    return list;
  }

  public OCDList withTypes(String... types)
  {
    StringSet set = new StringSet(types).trimVoid();
    if (set.isEmpty())
      return this;
    OCDList list = new OCDList();
    for (OCDPaintable node : this)
      if (node.isGroup() && set.has(node.asGroup().type()))
        list.add(node);
    return list;
  }

  public OCDPaintable[] array()
  {
    return this.toArray(new OCDPaintable[0]);
  }

  public OCDTextBlock textBlock()
  {
    for (OCDPaintable node : this)
      if (OCD.isTextBlock(node))
        return (OCDTextBlock) node;
    return null;
  }

  public OCDPath path()
  {
    for (OCDPaintable node : this)
      if (node.is(OCDPath.TAG))
        return (OCDPath) node;
    return null;
  }

  public OCDImage image()
  {
    for (OCDPaintable node : this)
      if (node.is(OCDImage.TAG))
        return (OCDImage) node;
    return null;
  }

  public OCDGroup group()
  {
    for (OCDPaintable node : this)
      if (node.is(OCDGroup.TAG))
        return (OCDGroup) node;
    return null;
  }

  public OCDAnnot annot()
  {
    for (OCDNode node : this)
      if (node.is(OCDAnnot.TAG))
        return (OCDAnnot) node;
    return null;
  }

  public List3<OCDAnnot> annots(String... type)
  {
    StringSet types = new StringSet(type);
    List3<OCDAnnot> annots = new List3<OCDAnnot>();
    for (OCDNode node : this)
      if (node.is(OCDAnnot.TAG))
      {
        OCDAnnot annot = (OCDAnnot) node;
        if (types.isEmpty() || types.has(annot.type()))
          annots.add(annot);
      }
    return annots;
  }

  public boolean has(String tag)
  {
    for (OCDNode node : this)
      if (node.is(tag))
        return true;
    return false;
  }

  public boolean is(String tag)
  {
    return this.size() == 1 && this.first().is(tag);
  }

  public boolean isGroup()
  {
    return is(OCDGroup.TAG);
  }

  public boolean hasGroup()
  {
    return has(OCDGroup.TAG);
  }

  public boolean isGraphics()
  {
    return isGroup() && OCD.isGraphics(this.first());
  }

  public boolean isTextBlock()
  {
    return isGroup() && OCD.isTextBlock(this.first());
  }

  public boolean isText()
  {
    return is(OCDText.TAG);
  }

  public boolean isImage()
  {
    return is(OCDImage.TAG);
  }

  public boolean isPath()
  {
    return is(OCDPath.TAG);
  }

  public boolean isAnnot()
  {
    return is(OCDAnnot.TAG);
  }

  public Rectangle3 bounds()
  {
    Rectangle3 box = null;
    for (OCDNode node : this)
      box = box == null ? node.bounds().copy() : box.include(box);
    return box;
  }

  public String string()
  {
    StringBuilder sb = new StringBuilder();
    for (OCDNode node : this)
    {
      OCDText text = OCDText.Cast(node);
      if (text != null)
        sb.append(text.string());
    }
    return sb.toString();
  }

  public boolean hasImage()
  {
    for (OCDPaintable node : this)
      if (node.is(OCDImage.TAG))
        return true;
    return false;
  }
  
  public OCDList reverse()
  {
    super.reverse();
    return this;
  }

}
