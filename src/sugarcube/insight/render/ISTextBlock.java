package sugarcube.insight.render;

import sugarcube.formats.ocd.objects.OCDTextBlock;
import sugarcube.formats.ocd.objects.OCDTextLine;

public class ISTextBlock extends FxOCDNode<OCDTextBlock>
{
  public ISTextBlock(FxPager pager, OCDTextBlock block, String... styles)
  {
    super(pager, block, styles);
  }

  @Override
  public ISTextBlock refresh()
  {
    if (node == null)
      return this;
    this.clear();

    for (OCDTextLine line : node)
      this.add(new ISTextLine(pager, line).refresh());

    this.boxing(true);
    return this;
  }

}
