package sugarcube.insight.render;

import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Color3;
import sugarcube.insight.render.interaction.FxInteractor;
import sugarcube.formats.ocd.objects.OCDAnnot;

public class ISAnnot extends FxOCDNode<OCDAnnot>
{
  public ISAnnot(final FxPager pager, final OCDAnnot annot)
  {
    super(pager, annot);
    this.box.fill(Color3.SC_BLUE.alpha(0.1));
    this.box.stroke(Color3.SC_BLUE.alpha(0.5), 1);
  }

  @Override
  public FxOCDNode refresh()
  {
    this.boxing(node.bounds());
    Log.debug(this, ".refresh - " + this.bounds());
    return this;
  }

  @Override
  public Rectangle3 bounds()
  {
    return node.bounds();
  }

  @Override
  public void interacted(FxInteractor interactor)
  {
    Log.debug(this, ".interacted - " + interactor.extent() + ", state=" + interactor.state()+", annot="+node.id());
    this.node.setBounds(interactor.extent().bounds());

    this.refresh();
  }

}