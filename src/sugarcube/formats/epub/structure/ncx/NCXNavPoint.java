package sugarcube.formats.epub.structure.ncx;

import sugarcube.common.data.xml.XmlNodeProps;

public class NCXNavPoint extends XmlNodeProps
{
  public static class NCXNavLabel extends XmlNodeProps
  {
    public String filename;

    public NCXNavLabel(String filename)
    {
      super("navLabel");
      this.filename = filename;
      this.addChild("text", filename);
    }
  }  
  
  
  protected NCXNavPoint(String tag)
  {
    super(tag);
  }
  
  public NCXNavPoint(String id, int playOrder, String href, String label)
  {
    super("navPoint");
    this.addAttribute("id", id);
    this.addAttribute("playOrder", ""+playOrder);
    this.addChild(new NCXNavLabel(label));
    this.addChild("content", "src", href);
  }

//  public NCXNavPoint(NavItem item)
//  {
//    super("navPoint");
//
//    for (OCDNode node : item.children())
//    {
////      Log.debug(this, " node: "+node.tag);
//      
//      this.addAttribute("id", navID());
//      this.addAttribute("playOrder", playOrder(href));        
//      
//      if (node.is(NavLink.TAG))
//      {
//        NavLink link = (NavLink) node;
//        String label = link.cdata();
//        String href = link.href();
//
//        this.addChild(new NCXNavLabel(label));
//        this.addChild("content", "src", href);
//      }
//      else if (node.is(NavList.TAG))
//        for (OCDNode child : node.children())
//          this.addChild(new NCXNavLabel((NavItem) child));
//    }
//
//  }
//  
//  public NCXNavLabel addListItem(NCXNav nav)
//  {
//    NCXNavLabel li=new NCXNavLabel(this);
//    this.addChild(li);
//    return li;
//  }  
}