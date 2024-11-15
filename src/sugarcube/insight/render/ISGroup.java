package sugarcube.insight.render;

import sugarcube.formats.ocd.objects.OCDGroup;
import sugarcube.formats.ocd.objects.OCDPaintable;

public class ISGroup extends FxOCDNode<OCDPaintable>
{
  public ISGroup(FxPager pager, OCDGroup<? extends OCDPaintable> group)
  {
    super(pager, group);    
    for (OCDPaintable node : group.zOrderedGraphics())
      this.add(pager.fxNode(node, this));    
  }
}
