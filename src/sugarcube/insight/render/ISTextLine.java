package sugarcube.insight.render;

import sugarcube.formats.ocd.objects.OCDText;
import sugarcube.formats.ocd.objects.OCDTextLine;

public class ISTextLine extends FxOCDNode<OCDTextLine>
{
  public ISTextLine(FxPager pager, OCDTextLine line, String... styles)
  {
    super(pager, line, styles);    
    // float y = box.height()-(bounds.maxY()-ocd.y());
    // FxLine baseline = new FxLine(0, y, box.width, y,
    // Color3.COBALT.alpha(0.25).fx(), 1);
    // this.add(baseline);
  }

  @Override
  public ISTextLine refresh()
  {
    this.clear();
    this.boxing();
    for (OCDText text : node.zTexts())
    {
      ISText fxText = new ISText(pager, text).refresh();
      this.add(fxText);
    }

    return this;
  }

}
