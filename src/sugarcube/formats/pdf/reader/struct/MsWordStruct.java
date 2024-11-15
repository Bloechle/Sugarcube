package sugarcube.formats.pdf.reader.struct;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Map3;
import sugarcube.common.data.collections.Set3;
import sugarcube.formats.pdf.reader.Dexter;
import sugarcube.formats.pdf.reader.pdf.node.struct.PDFPageStruct;
import sugarcube.formats.pdf.reader.pdf.node.struct.PDFStructElem;
import sugarcube.formats.pdf.reader.pdf.node.struct.PDFStructTreeRoot;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.*;
import sugarcube.formats.ocd.objects.lists.OCDPaintables;

public class MsWordStruct extends GenericStruct
{
  public static final Set3<String> P = new Set3<String>("toci", "p", "li", "title");
  private Map3<Integer, OCDGroup> groups = new Map3<Integer, OCDGroup>();

  public MsWordStruct(PDFStructTreeRoot struct, PDFPageStruct pageStruct, OCDPage page, Dexter writer)
  {
    super(struct, pageStruct, page, writer);
  }

  public PDFStructElem paragraph(PDFStructElem elem)
  {
    if (elem == null)
      return null;
    String name = elem.name.toLowerCase();
    if (P.contains(name) || name.length() == 2 && name.charAt(0) == 'h' && Character.isDigit(name.charAt(1)))//h1, h2,...
      return elem;
    return paragraph(elem.parentElement());
  }

//  @Override
//  public OCDPaintable oldAssign(PDFMark mark, OCDPaintable node)
//  {
//    if (pageStruct != null && node!=null)
//    {
////      node.rOrder = mcid;
//      if (mcid > -1)
//      {
//        PDFStructElem elem = paragraph(pageStruct.get(mcid));
//        if (elem != null)
//        {
//          node.groupID = elem.index;
//          node.setClassname(elem.name);
//        }
//        else
//          Log.debug(this,  ".assign - mcid not found: "+mcid);
//      }
//    }
//    return node;
//  }

  public OCDPage oldExtract()
  {
    if (pageStruct != null)
    {
      OCDPageContent content = page.content();
      OCDPaintables<OCDPaintable> graphics = content.graphics();
      content.setGraphics(new OCDPaintables<OCDPaintable>());
      while (!graphics.isEmpty())
      {
        OCDPaintable node = graphics.removeFirst();
        PDFStructElem leaf = struct.get(node.groupID);//textblock for instance with li class
        PDFStructElem elem = leaf;

        OCDGroup listGroup = null;
        OCDGroup rootGroup = null;
        int level = 0;
        if (elem != null)
          while ((elem = elem.parentElement()) != null && elem.isName("l", "toc"))
          {
            level++;
            OCDGroup group = group(content, elem);
            if (rootGroup == null)
            {
              listGroup = group;
              listGroup.setClassname(elem.index + "");
            }
            else
              group.put(rootGroup);
            rootGroup = group;
          }
        if (rootGroup != null && rootGroup != content)
          content.put(rootGroup);

        if (listGroup == null)
          content.add(node);
        else
          listGroup.put(itemGroup(leaf, node));
      }
    }
    return page;
  }

  
  public OCDPage oldExtractToo()
  {
    if (pageStruct != null)
    {
      OCDPageContent content = page.content();
//      OCDPaintables<OCDPaintable> graphics = content.graphics();
//      content.setGraphics(new OCDPaintables<OCDPaintable>());
//      while (!graphics.isEmpty())
//      {
//        OCDPaintable node = graphics.removeFirst();
//        PDFStructElem leaf = struct.get(node.classID());//textblock for instance with li class
//        if (leaf != null && leaf.isName("li"))
//        {
//          OCDGroup item = groups.get(leaf.index);
//          if (item == null)
//          {
//            item = new OCDListItem(null);
//            groups.put(leaf.index, item);
//            content.add(item);
//            int level = 0;
//            PDFStructElem elem = leaf;
//            while ((elem = elem.parentElement()) != null && elem.isName("l"))
//              level++;
//            item.setClassname("LI" +level);
//            item.add(node);
//          }
//          node.setClassname(item.classname());
//        }
//        else
//          content.add(node);
//      }
      
      for(OCDPaintable node: content)
      {
        if (node.isTextBlock())
        {
          OCDTextBlock block = node.asTextBlock();
          if (block.has(OCD.CLASS))
          {
            block.setLabel(block.classname());
          }
          for (OCDTextLine line : block)
          {
            line.clearLabel();
            line.clear(OCD.CLASS);
          }

        }   
    }
    }

    return page;
  }

  public OCDGroup itemGroup(PDFStructElem leaf, OCDPaintable node)
  {
    OCDGroup leafGroup = groups.get(leaf.index);
    if (leafGroup == null)
    {
//      if (leaf.isName("li", "toci"))
//        leafGroup = new OCDListItem(null);
//      else
      {
//        leafGroup = new OCDGroup((OCDNode) null);
        Log.debug(this, ".leafGroup - default leaf group: " + leaf.name);
      }
      groups.put(leaf.index, leafGroup);
    }
    leafGroup.put(node);
    return leafGroup;
  }

  public OCDGroup group(OCDPageContent content, PDFStructElem elem)
  {
    OCDGroup group = groups.get(elem.index);
    if (group == null)
    {
//      if (elem.isName("l", "toc"))
//        group = new OCDList(null);
//      else
        group = content;
      groups.put(elem.index, group);
    }
    return group;
  }
}
