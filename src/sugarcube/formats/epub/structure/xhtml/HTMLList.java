package sugarcube.formats.epub.structure.xhtml;

public class HTMLList extends HTMLNode
{
  public final static String OL = "ol";
  public final static String UL = "ul";
  

  public HTMLList(String tag)
  {
    this(IsOL(tag));
  }
  
  public HTMLList(boolean ordered)
  {
    super(ordered ? OL : UL);
  }

  public boolean ordered()
  {
    return this.isTag(OL);
  }
  
  public HTMLListItem addItem(HTMLNode... children)
  {
    HTMLListItem li= new HTMLListItem();
    li.addChildren(children);
    this.addChild(li);
    return li;
  }
  
  public static boolean IsOL(String tag)
  {
    return OL.equalsIgnoreCase(tag);
  }
  
  public static boolean IsUL(String tag)
  {
    return UL.equalsIgnoreCase(tag);
  }


}
