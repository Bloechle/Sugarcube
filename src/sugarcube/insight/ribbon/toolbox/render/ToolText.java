package sugarcube.insight.ribbon.toolbox.render;

import sugarcube.common.graphics.geom.Line3;
import sugarcube.common.ui.fx.shapes.FxPath;
import sugarcube.insight.core.IS;
import sugarcube.insight.render.interaction.FxInteractor;
import sugarcube.insight.ribbon.toolbox.ToolboxRibbon;
import sugarcube.formats.ocd.objects.OCDText;

public class ToolText extends ToolNode<OCDText>
{
  private ToolPager pager;

  public ToolText(ToolPager pager, OCDText text)
  {
    super(pager, text);
    this.pager = pager;
    this.mouseTransparent();
    // this.onMouseOver();
  }

  public ToolPager pager()
  {
    return (ToolPager) pager;
  }

  @Override
  public ToolText refresh()
  {
    ToolboxRibbon tab = pager.tab;
    

      this.clip(node.fxClip());
    
    FxPath path = node.fx(display().highlightTexts, !display().fonts, -1);

    double op = pager.tab.opacitySlider.getValue();
    if(op<0)
      path.setOpacity((100+op)/100);
    // FxPath path =
    // node.fx(pager.props).fill(Color3.BLACK.fx()).mouseTransparent();

    if (display().highlightTexts)
    {
      this.set(node.position().fxCircle(2).fill(IS.RED.alpha(0.5)), path);
    } else
      this.set(path);

    // this.boxing();
    return this;
  }

  @Override
  public boolean isInteractable()
  {
    return true;
  }

  @Override
  public void interacted(FxInteractor interactor)
  {
    Line3 l = interactor.extent();
    // node.moveTo(l.x(), l.y());
    // OCDOcr.rebox(node);
    this.refresh();
  }

  // @Override
  // public void mouseClicked(FxMouse ms)
  // {
  // Log.debug(this, ".mouseClicked");
  // if (tab().isMode(RibbonTab.MODE_INTERACTION) && ms.isPrimaryBt())
  // {
  // pager.pleaseInteract(this, ms);
  //
  // OCDTextBlock block = node.textBlock();
  // }
  // }

}
