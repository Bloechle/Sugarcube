package sugarcube.formats.ocd.analysis;

import sugarcube.common.data.collections.Map3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.formats.ocd.objects.OCDNode;

public class BoundsMap extends Map3<OCDNode, Rectangle3>
{

  public Rectangle3 box(OCDNode node)
  {
    Rectangle3 box = get(node);
    if (box == null)
      this.put(node, box = node.bounds());
    return box;
  }
}
