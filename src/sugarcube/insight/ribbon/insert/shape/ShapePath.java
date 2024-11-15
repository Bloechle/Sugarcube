package sugarcube.insight.ribbon.insert.shape;

import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.controls.FxLabel;
import sugarcube.common.ui.fx.shapes.FxPath;
import sugarcube.insight.ribbon.insert.InsertRibbon;

public class ShapePath extends FxLabel
{
  public FxPath fx;
  public Path3 path;
  public InsertRibbon tab;
  
  public ShapePath(InsertRibbon tab, Path3 path)
  {    
    this.tab = tab;
    this.style("shape-glyph");
    this.path = path;
    this.setGraphic(path()); 
    this.setOnMouseClicked(e->tab.insertShape(path));
  }
   
  public FxPath path()
  {
    return fx == null ? fx = path.fx().fill(Color3.ANTHRACITE).stroke(Color3.DUST_WHITE) : fx;
  }
  
  @Override
  public String toString()
  {
    return "Shape";
  }
  

}
