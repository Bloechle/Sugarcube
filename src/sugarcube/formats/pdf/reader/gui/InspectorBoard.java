package sugarcube.formats.pdf.reader.gui;

import sugarcube.common.data.collections.Set3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Image3;
import sugarcube.common.ui.gui.Label3;
import sugarcube.common.ui.gui.Panel3;
import sugarcube.common.ui.gui.ScrollPane3;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.node.PDFPage;
import sugarcube.formats.ocd.analysis.DexterProps;

import javax.swing.*;
import java.awt.*;

public class InspectorBoard extends Panel3
{
  public static String ACTION_PAGE_UPDATED = "page-updated";
  
  protected PDFDisplayProps displayProps = new PDFDisplayProps();
  protected DexterProps dexterProps = null;
  protected JPopupMenu popup;
  protected PaintPanel paintPanel = new PaintPanel();
  protected Label3 stateLabel = new Label3(" ");
  protected PDFPage page = null;
  protected Set3<PDFNode> objects = new Set3<PDFNode>();
  protected PDFInspectorGUI inspectorFrame;
  protected JPanel background = new JPanel();

  public InspectorBoard(PDFInspectorGUI inspectorFrame, DexterProps dexterProps)
  {
    super("PDFPanel");
    this.inspectorFrame = inspectorFrame;
    this.dexterProps = dexterProps;
    this.paintPanel = new PaintPanel();
    this.background.setBackground(displayProps.backgroundColor);
    this.background.add(paintPanel);

    new PDFPanelMouseListener(this);

    this.add(new ScrollPane3(background, true, true, 30), BorderLayout.CENTER);
    this.add(stateLabel, BorderLayout.SOUTH);
    this.setPage(page);
    this.setFocusable(true);
    this.requestFocusInWindow();
  }

  public PDFPage page()
  {
    return page;
  }

  public void setPage(PDFPage page)
  {
    if (page == null)
      return;   
    this.page = page;
    this.page.ensureInMemory();
    // this.page.generateDeepZOrder(new Counter(page.number() * 1000000));
    this.inspectorFrame.toolbar.updatePageField(page);
    this.paintPanel.repaint();
  }

  public void setObjects(Set3<PDFNode> objects)
  {
    if (!objects.isEmpty())
    {
      this.objects = objects;
      this.page = this.objects.first().isPage() ? this.objects.first().toPage() : null;
      this.paintPanel.repaint();
    }
  }

  public void paint(Graphics3 g, PDFPage page)
  {
  }

  public class PaintPanel extends JPanel
  {    
    public Image3 lastPaintedImage = null;

    public PaintPanel()
    {
    }

    @Override
    public void paintComponent(Graphics graphics)
    {
      super.paintComponent(graphics);
      PDFPage page = page();
      if (page != null)
        page.ensureInMemory();

      Rectangle3 bounds = page == null ? PDFDisplayProps.A4bounds72dpi() : page.bounds();

      if (!objects.isEmpty())
        if (objects.first().isResourceChild())
        {
          page = null;
          bounds = PDFDisplayProps.A4bounds72dpi();
        } else
          bounds = objects.first().page().bounds();

      Transform3 displayTransform = displayProps.displayTransform();

      bounds = new Rectangle3(displayTransform.transform(bounds).getBounds2D());
      bounds = new Rectangle3(0, 0, bounds.width, bounds.height);

      Graphics3 g = new Graphics3(graphics, bounds.intWidth() + 1, bounds.intHeight() + 1);
      this.setPreferredSize(new Dimension(bounds.intWidth() + 1, bounds.intHeight() + 1));
      this.revalidate();

      if (page != null)
      {
        g.paint(bounds, Color3.WHITE, Color3.ANTHRACITE);
        g.draw(page.createImage(displayProps.copy(page.mediaBox())));
      } else if (!objects.isEmpty() && objects.first().page() != null)
      {
        if (objects.first().isResourceChild())
          bounds = PDFDisplayProps.A4bounds72dpi();

        Image3 image = new Image3(bounds.width, bounds.height, true);

        Graphics3 gImage = image.graphics();
        gImage.clearChessBoard(Color3.WHITE, Color3.DUST_WHITE, 16);
        g.paint(bounds, Color3.WHITE, Color3.ANTHRACITE);

        PDFDisplayProps props = displayProps.copy(bounds);
        props.highlightClips = true;
        props.highlightImages = true;
        props.highlightPaths = true;
        props.highlightTexts = true;

        for (PDFNode node : objects)
        {
          node.paint(gImage, props.copy(bounds));

        }

        g.draw(bounds, Color3.BLACK);
        InspectorBoard.this.paint(gImage, page);
        this.lastPaintedImage = image;
        g.draw(image, 0, 0);
      }
    }

  }

}
