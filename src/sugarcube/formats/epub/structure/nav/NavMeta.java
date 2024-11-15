
package sugarcube.formats.epub.structure.nav;

import sugarcube.formats.ocd.objects.OCDNode;


public class NavMeta extends NavNode
{
  public static final String TAG = "meta";
  
  public NavMeta(OCDNode parent)
  {
    super(TAG, parent);
  }
  
}
