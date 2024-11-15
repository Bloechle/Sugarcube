package sugarcube.insight.render;

import sugarcube.formats.ocd.objects.OCDText;

public class ISText extends FxOCDNode<OCDText>
{
  public ISText(FxPager pager, OCDText text, String... styles)
  {
    super(pager, text, styles);
  }
  
  @Override
  public ISText refresh()
  {    
    this.clip(node.fxClip());
    this.set(node.fx());    
    this.boxing();
    return this;
  }

}
