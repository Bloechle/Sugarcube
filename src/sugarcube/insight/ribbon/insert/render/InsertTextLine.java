package sugarcube.insight.ribbon.insert.render;

import sugarcube.common.graphics.Color3;
import sugarcube.formats.ocd.objects.OCDText;
import sugarcube.formats.ocd.objects.OCDTextLine;

public class InsertTextLine extends InsertNode<OCDTextLine>
{

  public InsertTextLine(InsertPager pager, OCDTextLine line)
  {
    super(pager, line);    
    this.mouseTransparent();
  }
  

  @Override
  public InsertTextLine refresh()
  {
    this.clear();

    if (display().highlightTexts)
    {
      this.add(node.position().fxCircle(2).fill(Color3.RED));
    }

    for (OCDText text : node.zTexts())
    {
      InsertText fxText = new InsertText(pager,text).refresh();
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
