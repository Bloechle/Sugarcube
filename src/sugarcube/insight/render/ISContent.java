package sugarcube.insight.render;

import sugarcube.formats.ocd.objects.OCDContent;
import sugarcube.formats.ocd.objects.OCDPaintable;

public class ISContent extends FxOCDNode<OCDPaintable>
{
  public ISContent(FxPager pager, OCDContent content)
  {
    super(pager, content);    
    for (OCDPaintable node : content.zOrderedGraphics())
      this.add(pager.fxNode(node, this));    
  }
}
