
package sugarcube.formats.epub.structure.nav;

import sugarcube.formats.ocd.objects.OCDNode;


public class NavTitle extends NavNode
{
  public static final String TAG = "title";
  
  public NavTitle(OCDNode parent)
  {
    super(TAG, parent);
  }
}
