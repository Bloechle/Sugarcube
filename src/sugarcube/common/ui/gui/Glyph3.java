package sugarcube.common.ui.gui;

import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.interfaces.Glyph;

import java.awt.*;

public class Glyph3 implements Glyph
{
  public String chars;
  public String name;
  public Path3 path;
  public float width;
  public float height;
  public Point3 vertOrigin;

  public Glyph3(String name, Shape path, double width, int... unicode)
  {
    this(name, path, new Point3(width, 0), null, unicode);
  }
  
  public Glyph3(String name, Shape path, Point3 advance, int... unicode)
  {
    this(name, path, advance, null, unicode);
  }
  
  public Glyph3(String name, Shape path, Point3 advance, Point3 origin, int... unicode)  
  {
    this.name = name;
    this.path = path instanceof Path3 ? (Path3)path : new Path3(path);
    this.width = advance.x;
    this.height = advance.y;
    this.vertOrigin = origin;
    this.chars = new String(unicode, 0, unicode.length);
  }

  @Override
  public String code()
  { 
    return chars;
  }

  @Override
  public String name()
  {
    return name == null ? chars : name;
  }

  @Override
  public Path3 path(double fontsize)
  {
    return path.scale(fontsize);
  }

  @Override
  public float width()
  {
    return width;
  }

  @Override
  public float height()
  {
    return height;
  }

  @Override
  public Point3 vertOrigin()
  {
    return vertOrigin == null ? new Point3() : vertOrigin;
  }

  @Override
  public int override()
  {
    return -1;
  }

}
