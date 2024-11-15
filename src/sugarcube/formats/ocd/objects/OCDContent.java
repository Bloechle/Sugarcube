package sugarcube.formats.ocd.objects;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Str;
import sugarcube.common.data.collections.StringOccurrences;
import sugarcube.common.data.Base;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.formats.ocd.objects.lists.OCDBlocks;
import sugarcube.formats.ocd.objects.lists.OCDList;
import sugarcube.formats.ocd.objects.lists.OCDMap;
import sugarcube.formats.ocd.objects.lists.OCDTexts;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class OCDContent extends OCDGroup<OCDPaintable> implements Iterable<OCDPaintable>
{
  public static final String TAG = "content";

  public OCDContent(OCDNode parent)
  {
    super(parent);
  }

  public OCDContent(String type, OCDNode parent)
  {
    super(type, parent);
  }

  protected OCDContent(String type, OCDNode parent, String tag)
  {
    super(type, parent, tag);
  }

  public boolean isPageContent()
  {
    return false;
  }

  public boolean isCellContent()
  {
    return false;
  }

  public OCDMap idMap()
  {
    OCDMap idMap = new OCDMap();
    for (OCDPaintable node : this)
      Populate(idMap, node);
    return idMap;
  }

  public static OCDMap Populate(OCDMap idMap, OCDPaintable node)
  {
    if (idMap == null)
      idMap = new OCDMap();
    if (node != null)
    {
      if (node.hasID())
        idMap.put(node.id(), node);
      if (node.isGroup())
        for (OCDPaintable child : node.asGroup())
          Populate(idMap, child);
    }
    return idMap;
  }

  public String autoID()
  {
    return this.tag.substring(0, 1) + "o" + Base.x32.random12();
  }

  public OCDTextBlock newBlock()
  {
    return newTextBlock();
  }

  public OCDTextBlock newTextBlock()
  {
    OCDTextBlock block = new OCDTextBlock(this);
    this.add(block);
    return block;
  }

  public OCDPath newPath()
  {
    OCDPath path = new OCDPath(this);
    this.add(path);
    return path;
  }

  public OCDTable newTable()
  {
    OCDTable table = new OCDTable(this);
    this.add(table);
    return table;
  }

  public OCDFlow newFlow()
  {
    OCDFlow flow = new OCDFlow(this);
    this.add(flow);
    return flow;
  }

  public OCDContent newContent()
  {
    return newContent(-1);
  }

  public OCDContent newContent(int index)
  {
    return newContent(OCDGroup.CONTENT, index);
  }

  public OCDContent newContent(String type)
  {
    return newContent(type, -1);
  }

  public OCDContent newContent(String type, int index)
  {
//    Log.debug(this, ".newContent - type=" + type);
    OCDContent g = new OCDContent(Str.IsVoid(type) ? OCDGroup.CONTENT : type, this);
    if (index > -1 && index < size())
      this.add(index, g);
    else
      this.add(g);
    return g;
  }

  public OCDImage newImage()
  {
    OCDImage image = new OCDImage(this);
    this.addOnTop(image);
    return image;
  }

  public OCDImage addImage(BufferedImage bi, double compression, AffineTransform tm)
  {
    OCDPage page = page();
    if (page != null)
    {
      OCDDocument ocd = page.document();
      if (ocd != null)
      {
        OCDImage image = new OCDImage(page);
        image.setImage(bi, compression);
        image.setTransform(tm == null ? new Transform3() : tm);
        this.add(image);
        ocd.imageHandler.addEntry(image);
        return image;
      }
    }
    return null;
  }

  public OCDFootnote newFootnote()
  {
    OCDFootnote footnote = new OCDFootnote(this);
    this.add(footnote);
    return footnote;
  }

  public OCDContent regroup(Rectangle3 box, String type)
  {
    return regroup(overlappingNodes(box), type);
  }

  public OCDContent regroup(OCDList list, String type)
  {
    for (OCDPaintable node : list)
    {
      node.delete(false);
      if (node.isText())
      {
        OCDTextBlock block = node.asText().textBlock();
        if (block != null && block.nbOfTexts() == 0)
          block.remove();
      }
    }

    Log.debug(this, ".regroup - type=" + type + ", children=" + list.size());
    if (list.isPopulated())
    {
      OCDContent group = this.newContent(type);
      group.needID("sub");

      for (OCDPaintable node : list)
      {
        if (node.isGroupContent())
        {
          for (OCDPaintable child : node.asContent())
            group.add(child);
        } else
          group.add(node);
      }

      return group;
      // Log.debug(this, ".regroup - " + content.xmlString());
    }
    return null;
  }

  public OCDTextBlock blockize()
  {
    OCDBlocks blocks = this.blocks();
    if (blocks.isEmpty())
      return null;
    else if (blocks.isSizeOne())
      return blocks.first();

    OCDTexts texts = new OCDTexts();
    StringOccurrences classnames = new StringOccurrences();

    for (OCDTextBlock block : blocks())
    {
      classnames.inc(block.classname());
      for (OCDTextLine line : block.lines())
      {
        for (OCDText text : line.texts())
        {
          texts.add(text);
          line.remove(text);
        }
        block.remove(line);
      }
      block.remove();
    }

    if (texts.isPopulated())
    {
      OCDTextBlock block = texts.blockize(new OCDTextBlock(this), false);
      String classname = classnames.max();
      if (Str.HasData(classname))
        block.setClassname(classname);

      this.add(block);
      return block;
    }

    return null;
  }

  @Override
  public String sticker()
  {
    String id = this.id();
    return "Content[" + type + (hasLabel() ? ", label=" + label() : "") + "]" + (id == null ? "" : " #" + id);
  }
}
