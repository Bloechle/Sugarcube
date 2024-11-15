package sugarcube.insight.ribbon.insert.render;

import sugarcube.common.graphics.geom.Line3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.shapes.FxPath;
import sugarcube.insight.render.interaction.FxInteractor;
import sugarcube.formats.ocd.objects.OCDText;

public class InsertText extends InsertNode<OCDText>
{
  public InsertText(InsertPager pager, OCDText text)
  {
    super(pager, text);
    this.mouseTransparent();
//    this.onMouseOver();    
  }

  @Override
  public InsertText refresh()
  {    
    this.clip(node.fxClip());
    FxPath path = node.fx();
    
//    FxPath path = node.fx(pager.props).fill(Color3.BLACK.fx()).mouseTransparent();
    
    if (display().highlightTexts)
    {
      this.set(node.position().fxCircle(1).fill(Color3.RED), path);
    } else
      this.set(path);

//    this.boxing();
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
//    OCDOcr.rebox(node);
    this.refresh();
  }
  

//  @Override
//  public void mouseClicked(FxMouse ms)
//  {    
//    Log.debug(this,  ".mouseClicked");
//    if (tab().isMode(RibbonTab.MODE_INTERACTION) && ms.isPrimaryBt())
//    {
//      pager.pleaseInteract(this, ms);
//      
//      OCDTextBlock block = node.textBlock();
//    }
//  }
  
}
