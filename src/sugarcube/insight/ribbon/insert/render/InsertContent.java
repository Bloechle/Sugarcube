package sugarcube.insight.ribbon.insert.render;

import sugarcube.formats.ocd.objects.OCDContent;
import sugarcube.formats.ocd.objects.OCDPaintable;

public class InsertContent  extends InsertNode<OCDPaintable>
{
  public InsertContent(InsertPager pager, OCDContent content)
  {
    super(pager, content);    
    for (OCDPaintable node : content.zOrderedGraphics())
      this.add(pager.fxNode(node,this));        
    this.focusOnMouseOver();  
  }  
}
  



