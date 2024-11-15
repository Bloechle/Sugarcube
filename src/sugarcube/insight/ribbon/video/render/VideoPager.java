package sugarcube.insight.ribbon.video.render;

import sugarcube.common.data.collections.List3;
import sugarcube.insight.render.FxOCDNode;
import sugarcube.insight.render.FxPager;
import sugarcube.insight.ribbon.video.VideoRibbon;
import sugarcube.formats.ocd.objects.OCDPaintable;

public class VideoPager extends FxPager<VideoRibbon>
{

  public VideoPager(VideoRibbon tab)
  {
    super(tab, true);
  }

  public boolean preventFocusOver()
  {
    return tab.insertToggles.isSelected();
  }

  public FxOCDNode createFxNode(OCDPaintable node, FxOCDNode parent)
  {
    switch (node.cast())
    {
    case "OCDImage":
      if (node.asImage().isMP4())
        return new VideoImage(this, node.asImage());
    }
    return super.createFxNode(node, parent);
  }

  public List3<VideoImage> videos()
  {
    List3<VideoImage> videos = new List3<>();
    board.visitContent(fx -> {
      if (fx instanceof VideoImage)
        videos.add((VideoImage) fx);
      return false;
    });
    return videos;
  }

}
