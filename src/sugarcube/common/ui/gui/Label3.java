package sugarcube.common.ui.gui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class Label3 extends JLabel
{
  public Label3()
  {
    super();
  }

  public Label3(Icon icon)
  {
    super(icon);
  }

  public Label3(String text)
  {
    super(text);
  }
  
  public Label3(String text, int width)
  {
    this(text);
    this.setPreferredSize(new Dimension(width,-1));
  }
  

  public Label3(Icon icon, String text)
  {
    this(icon);
    this.setText(text);
  }

  public Label3(String text, Font font)
  {
    super(text);
    this.setFont(font);
  }

  public Label3 addBorder(Border border)
  {
    this.setBorder(border);
    return this;
  }

  public Label3 toCenter()
  {
    this.setHorizontalAlignment(SwingConstants.CENTER);
    return this;
  }

  public Label3 toLeft()
  {
    this.setHorizontalAlignment(SwingConstants.LEFT);
    return this;
  }

  public Label3 toRight()
  {
    this.setHorizontalAlignment(SwingConstants.RIGHT);
    return this;
  }
}
