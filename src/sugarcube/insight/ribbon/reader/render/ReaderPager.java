package sugarcube.insight.ribbon.reader.render;

import sugarcube.insight.render.FxOCDNode;
import sugarcube.insight.render.FxPager;
import sugarcube.insight.ribbon.reader.ReaderRibbon;
import sugarcube.formats.ocd.objects.OCDPaintable;

public class ReaderPager extends FxPager<ReaderRibbon>
{

  public ReaderPager(ReaderRibbon tab)
  {
    super(tab, false);
    this.interactor.disable();
  }


  public FxOCDNode createFxNode(OCDPaintable node, FxOCDNode parent)
  {
    switch (node.cast())
    {
    case "OCDImage":
      return new ReaderImage(this, node.asImage());
    }
    return super.createFxNode(node, parent);
  }
  
}
