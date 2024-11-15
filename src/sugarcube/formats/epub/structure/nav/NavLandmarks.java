package sugarcube.formats.epub.structure.nav;

import sugarcube.formats.ocd.objects.OCDNode;


public class NavLandmarks extends NavRoot
{
  public NavLandmarks(OCDNode parent)
  {
    super(parent, TYPE_LANDMARKS);    
  }
  
  public NavLandmarks(OCDNode parent, String title)
  {
    this(parent);
    this.setTitle(title);
  }

  
}
