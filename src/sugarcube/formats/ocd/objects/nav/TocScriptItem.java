package sugarcube.formats.ocd.objects.nav;

import sugarcube.formats.ocd.objects.OCDAnnot;

public class TocScriptItem
{
  public transient OCDAnnot annot;  
  public int level;
  public String text;
  public String link;

  public TocScriptItem(int level, String text, String link)
  {
    this.level = level;
    this.text = text;
    this.link = link;
  }
  
  public boolean hasText()
  {
    return text!=null && !text.isEmpty();
  }
  
  public boolean hasLink()
  {
    return link!=null && !link.isEmpty();
  }    

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder(" ");

    for (int i = 0; i < level; i++)
      sb.append("-");
    sb.append(" ");

    sb.append(hasLink() ? " " + link + " |" : "");
    sb.append(hasText() ? " " + text : "");
    
    return sb.toString();
  }

}
