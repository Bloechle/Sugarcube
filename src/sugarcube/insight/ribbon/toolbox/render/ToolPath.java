package sugarcube.insight.ribbon.toolbox.render;

import sugarcube.common.ui.fx.shapes.FxPath;
import sugarcube.formats.ocd.objects.OCDPath;

public class ToolPath extends ToolNode<OCDPath>
{

  public ToolPath(ToolPager pager, OCDPath path, String... styles)
  {
    super(pager, path, styles);
  }

  @Override
  public ToolPath refresh()
  {
    this.clip(node.fxClip());
    FxPath path = node.fx(display().highlightPaths);
    double op = pager.tab.opacitySlider.getValue();
    if (op < 0)
      path.setOpacity((100 + op) / 100);
    this.set(path);
    this.boxing();
    return this;
  }

}