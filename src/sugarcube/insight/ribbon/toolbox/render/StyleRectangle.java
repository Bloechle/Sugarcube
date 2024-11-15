package sugarcube.insight.ribbon.toolbox.render;

import sugarcube.common.graphics.geom.Rectangle3;

public class StyleRectangle extends Rectangle3
{
  public String name;

  public StyleRectangle(String name, double x, double y, double w, double h)
  {
    super(x, y, w, h);
    this.name = name;
  }

  public String name()
  {
    return name;
  }
}
