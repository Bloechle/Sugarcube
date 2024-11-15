package sugarcube.insight.render;

import sugarcube.formats.ocd.objects.OCDPath;

public class ISPath extends FxOCDNode<OCDPath>
{
  public ISPath(final FxPager pager, final OCDPath path, String... styles)
  {
    super(pager, path, styles);    
  }


  @Override
  public ISPath refresh()
  {
    this.clip(node.fxClip());
    this.set(node.fx());   
    this.boxing();
    return this;
  }
 
}
