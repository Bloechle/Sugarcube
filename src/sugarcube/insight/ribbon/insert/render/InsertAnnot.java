package sugarcube.insight.ribbon.insert.render;

import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.Color3;
import sugarcube.insight.render.interaction.FxInteractor;
import sugarcube.formats.ocd.objects.OCDAnnot;

public class InsertAnnot extends InsertNode<OCDAnnot>
{

  public InsertAnnot(InsertPager pager, final OCDAnnot annot)
  {
    super(pager, annot);
    this.box.fill(Color3.SC_BLUE.alpha(0.1));
    this.box.stroke(Color3.SC_BLUE.alpha(0.5), 1);
    this.focusOnMouseOver();
    this.setMouseTransparent(false);
  }

  @Override
  public InsertAnnot refresh()
  {
    this.boxing(node.bounds());
    Log.debug(this, ".refresh - " + this.bounds());
    box.paint(Color3.SC_BLUE.alpha(0.1), Color3.SC_BLUE.alpha(0.8), 1);
    return this;
  }

  @Override
  public void interacted(FxInteractor interactor)
  {
    Log.debug(this, ".interacted - " + interactor.extent() + ", state=" + interactor.state() + ", annot=" + node.id());
    this.node.setBounds(interactor.extent().bounds());

    this.refresh();
  }
}