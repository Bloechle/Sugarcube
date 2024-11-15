package sugarcube.formats.pdf.reader.gui;

import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Image3;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PDFPanelMouseListener extends MouseAdapter
{
  private InspectorBoard pdfPanel;

  public PDFPanelMouseListener(InspectorBoard panel)
  {
    this.pdfPanel = panel;
    this.pdfPanel.paintPanel.addMouseMotionListener(this);
    this.pdfPanel.paintPanel.addMouseListener(this);
  }

  @Override
  public void mousePressed(MouseEvent e)
  {
    if (e.getButton() != MouseEvent.BUTTON1)
      return;

    pdfPanel.requestFocusInWindow();
//    if (gui.page != null)
//    {
//      Point2D p = gui.displayProps.displayTransform().inverse().transform(e.getPoint());
//      for (OCDLink link : gui.page.metadata().links())
//        if (link.bounds().contains(p))
//        {
//          Zen.Native.openURL(link.url());
//          break;
//        }
//    }
//    gui.selector.startID = gui.selector.endID = -1;
//    gui.selector.startPoint = gui.selector.endPoint = null;
    pdfPanel.repaint();
  }

  @Override
  public void mouseDragged(MouseEvent e)
  {
//    Point2D p = gui.displayProps.displayTransform().inverse().transform(e.getPoint());
//    OCDText text = gui.page == null ? null : gui.page.hoveringText(p);
//    if (gui.selector.startPoint == null && text != null)
//    {
//      if (gui.selector.startID == -1)
//        gui.selector.endID = gui.selector.startID = text.zOrder;
//      else
//        gui.selector.endID = text.zOrder;
//      gui.repaint();
//    }
//    else if (gui.selector.startID == -1)
//    {
//      if (gui.selector.startPoint == null)
//        gui.selector.endPoint = gui.selector.startPoint = p;
//      else
//        gui.selector.endPoint = p;
//      gui.repaint();
//    }
  }

  @Override
  public void mouseMoved(MouseEvent e)
  {
//    if (gui.page != null)
//    {
//      Point2D p = gui.displayProps.displayTransform().inverse().transform(e.getPoint());
//      OCDLink link = gui.page.hoveringLink(p);
//      OCDText text = gui.page.hoveringText(p);
//      gui.setCursor(link == null && text == null ? Zen.Cursor.standard() : text == null ? Zen.Cursor.hand() : Zen.Cursor.text());
//
//      gui.stateLabel.setText(" x=" + e.getX() + " y=" + e.getY() + " " + (link != null ? link : "") + (text != null ? " font=" + text.fontname() + " size: " + text.fontsize() : ""));    
//    }

    Image3 image = pdfPanel.paintPanel.lastPaintedImage;


    Color3 color = null;
    int x = e.getX();
    int y = e.getY();
    if (image != null && x < image.width() && y < image.height())
      color = image.getPixel(x, y);
    String rgb = "";
    if (color != null)
      rgb = " rgba(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + "," + color.getAlpha() + ")";
    pdfPanel.stateLabel.setText(rgb + " at (" + x + "," + y + ")");
  }
}
