package sugarcube.common.ui.gui;

import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Stroke3;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class Border3 implements Border
{
  private Border border;

  public Border3(Border border)
  {
    this.border = border;
  }

  public Border border()
  {
    return border;
  }

  @Override
  public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
  {
    border.paintBorder(c, g, x, y, width, height);
  }

  @Override
  public Insets getBorderInsets(Component c)
  {
    return border.getBorderInsets(c);
  }

  @Override
  public boolean isBorderOpaque()
  {
    return border.isBorderOpaque();
  }

  public static Border3 line(Color color, int... b)
  {
    if (b == null || b.length == 0)
      return new Border3(BorderFactory.createEmptyBorder());
    if (b.length == 1)
      return new Border3(BorderFactory.createLineBorder(color, b[0]));
    else
      return new Border3(BorderFactory.createMatteBorder(b[0], b[1], b.length > 2 ? b[2] : b[0], b.length > 3 ? b[3] : b[1], color));
  }

  public static Border3 compound(Border... borders)
  {
    Border border = borders.length == 0 ? BorderFactory.createEmptyBorder() : borders[0];
    for (int i = 1; i < borders.length; i++)
      border = BorderFactory.createCompoundBorder(border, borders[i]);
    return new Border3(border);
  }

  public static Border3 empty()
  {
    return new Border3(BorderFactory.createEmptyBorder());
  }
  
  public static Border3 empty(int size)
  {
    return empty(size, size);
  }

  public static Border3 empty(int dx, int dy)
  {
    return new Border3(BorderFactory.createEmptyBorder(dy, dx, dy, dx));
  }

  public static Border3 empty(int top, int left, int bottom, int right)
  {
    return new Border3(BorderFactory.createEmptyBorder(top, left, bottom, right));
  }

  public static Border3 etched()
  {
    return new Border3(BorderFactory.createEtchedBorder());
  }

  public static Border3 titled(String title)
  {
    return new Border3(BorderFactory.createTitledBorder(title));
  }

  public static Border3 titled(Border border, String title)
  {
    return new Border3(BorderFactory.createTitledBorder(border, title));
  }

  public static class Light implements Border
  {
    private static int FS = 12;
//    private static Stroke3 stroke = new Stroke3(0.5f, Stroke3.CAP_ROUND, Stroke3.JOIN_MITER, 0, new float[]
//      {
//        1, 2
//      });
    private static Stroke3 stroke = Stroke3.LINE3;
    private String title;
    private int tw = -1;

    public Light(String title)
    {
      this.title = title;
    }

    @Override
    public void paintBorder(Component c, Graphics graphics, int x, int y, int width, int height)
    {
      Graphics3 g = new Graphics3(graphics);

      Font font = g.getFont().deriveFont(12);
      g.setFont(font);
      if (tw < 0)
        tw = (int) font.createGlyphVector(g.context().getFontRenderContext(), title).getVisualBounds().getWidth();

      Path3 p = new Path3();
      p.moveTo(x + tw + 2 + 0.5, y + FS / 2 + 0.5);
      p.lineTo(x + width - 5 + 0.5, y + FS / 2 + 0.5);
//      p.quadTo(x + width - 5, y + FS / 2, x + width - 5, y + FS / 2 + 10);
      p.lineTo(x + width - 5 + 0.5, y + 2 * height - 0.5);
      g.setColor(Color3.GRAY);
      g.context().drawString(title, x, y + FS - 3);
      g.setStroke(Stroke3.LINE3);
      g.setColor(Color3.WHITE);
      g.draw(p);
      g.setStroke(Stroke3.LINE);
      g.setColor(Color3.LIGHT_GRAY);
      g.draw(p);
//      g.context().drawArc(x, y+height-2*r, 2*r, 2*r, 180, 90);      
    }

    @Override
    public Insets getBorderInsets(Component c)
    {
      return new Insets(FS, 2, 2, 10);
    }

    @Override
    public boolean isBorderOpaque()
    {
      return true;
    }
  }
}
