package sugarcube.formats.epub.structure.nav;

import sugarcube.formats.ocd.objects.OCDNode;

public class NavH1 extends NavNode
{
  public static final String TAG = "h1";  
  
  public NavH1(OCDNode parent)
  {
    super(TAG, parent);
  }
  
  public NavH1(OCDNode parent, String cdata)
  {
    super(TAG, parent);
    this.setCData(cdata);
  }      
  
  @Override
  public String sticker()
  {
    return cdata();
  }

  
}