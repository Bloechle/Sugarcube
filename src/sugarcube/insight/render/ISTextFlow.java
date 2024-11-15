package sugarcube.insight.render;

import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.formats.ocd.objects.OCDFlow;
import sugarcube.formats.ocd.objects.OCDTextBlock;
import sugarcube.formats.ocd.objects.OCDTextLine;

public class ISTextFlow extends FxOCDNode<OCDFlow>
{

  public ISTextFlow(FxPager pager, OCDFlow flow)
  {
    super(pager, flow, "ocd-flow cursor-text");

    for (OCDTextBlock block : flow.blocks())
      add(new ISTextBlock(pager, block).refresh());
  }
  
  @Override
  public Rectangle3 bounds()
  {
    return node.bounds();
  }

  @Override
  public ISTextFlow refresh()
  {
    if (node == null)
      return this;
    this.clear();
    for (OCDTextBlock block : node.blocks())
      for (OCDTextLine line : block)
        this.add(new ISTextLine(pager, line).refresh());
    this.boxing();
//    Log.debug(this, ".refresh - extent="+this.extent());
    return this;
  }
  


}