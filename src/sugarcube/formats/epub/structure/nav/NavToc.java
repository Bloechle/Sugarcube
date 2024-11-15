package sugarcube.formats.epub.structure.nav;

import sugarcube.formats.ocd.objects.OCDNode;

public class NavToc extends NavRoot
{
  public static final String ID = "toc";
  
  public NavToc(OCDNode parent)
  {
    super(parent, TYPE_TOC);
    this.addAttributes("id", ID);     
  }

}
