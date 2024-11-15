package sugarcube.insight.ribbon.video.render;

import sugarcube.insight.render.FxOCDNode;
import sugarcube.formats.ocd.objects.OCDPaintable;

public class VideoNode<T extends OCDPaintable> extends FxOCDNode<T>
{
  public VideoPager pager;
  
  public VideoNode(VideoPager pager, T node, String... styles)
  {
    super(pager, node, styles);
    this.pager = pager;
  }
  




}
