package sugarcube.insight.ribbon.reader.render;

import sugarcube.insight.render.FxOCDNode;
import sugarcube.formats.ocd.objects.OCDPaintable;

public class ReaderNode<T extends OCDPaintable> extends FxOCDNode<T>
{
  public ReaderPager pager;

  public ReaderNode(ReaderPager pager, T node, String... styles)
  {
    super(pager, node, styles);
  }




}
