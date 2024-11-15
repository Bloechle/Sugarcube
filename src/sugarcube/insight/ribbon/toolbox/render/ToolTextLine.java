package sugarcube.insight.ribbon.toolbox.render;

import sugarcube.insight.core.IS;
import sugarcube.formats.ocd.objects.OCDText;
import sugarcube.formats.ocd.objects.OCDTextLine;

public class ToolTextLine extends ToolNode<OCDTextLine>
{
  private ToolPager pager;
  
  public ToolTextLine(ToolPager pager, OCDTextLine line)
  {
    super(pager, line);
    this.pager = pager;
    boolean lineMode = pager.tab.lineModeTg.isSelected();
    boolean tableMode = pager.tab.tableModeTg.isSelected();
    if(tableMode)
      this.mouseTransparent(true);
    else if (lineMode)
      this.handleMouseEvents(true);
    else
      this.mouseTransparent(true);    
   
  }

  @Override
  public ToolTextLine refresh()
  {
    this.clear();

    if (display().highlightTexts)
    {      
      this.add(node.position().fxCircle(3).fill(IS.ORANGE.alpha(0.5)));
    }

    for (OCDText text : node.zTexts())
    {
      ToolText fxText = new ToolText(pager,text).refresh();
      this.add(fxText);
    }
        
    this.boxing(true);

    return this;
  }

  // @Override
  // public Line3 extent()
  // {
  // Rectangle3 box = node.bounds();
  // return new Line3(box.x, box.y, box.maxX(), node.first().y());
  // }
  //
  // @Override
  // public void mouseClicked(FxMouse ms)
  // {
  // if (tab().isMode(RibbonTab.INTERACTION) && ms.isPrimaryBt())
  // {
  // pager.pleaseInteract(this, ms);
  // }
  // }

}
