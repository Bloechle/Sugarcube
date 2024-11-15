package sugarcube.insight.ribbon.toolbox.render;

import sugarcube.insight.render.FxOCDNode;
import sugarcube.formats.ocd.objects.OCDPaintable;

public class ToolNode<T extends OCDPaintable> extends FxOCDNode<T>
{
  public ToolPager pager;

  public ToolNode(ToolPager pager, T node, String... styles)
  {
    super(pager, node, styles);
    this.pager = pager;
  }



}
