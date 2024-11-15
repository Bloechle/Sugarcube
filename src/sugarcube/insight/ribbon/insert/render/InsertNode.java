package sugarcube.insight.ribbon.insert.render;

import sugarcube.insight.render.FxOCDNode;
import sugarcube.formats.ocd.objects.OCDPaintable;

public class InsertNode<T extends OCDPaintable> extends FxOCDNode<T>
{
  public InsertPager pager;


  public InsertNode(InsertPager pager, T node, String... styles)
  {
    super(pager, node, styles);
    this.pager = pager;
  }
 
}
