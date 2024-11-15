package sugarcube.formats.pdf.reader.gui;

import sugarcube.common.graphics.Image3;
import sugarcube.common.system.io.File3;
import sugarcube.common.ui.gui.*;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.node.PDFPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;

public class InspectorToolbar extends ToolBar3
{
  private PDFInspectorGUI gui;
  private Frame3 propertiesFrame = null;
  private JTextField pageField;
  private JButton imageBt = new JButton("Save Image");
  private ComboBox8 zoomCombo = new ComboBox8("0.25", "0.50", "0.75", "1.0", "1.25", "1.50", "1.75", "2.0", "3.0", "4.0", "5.0");
  public CheckBox3 streamCheck;

  protected InspectorToolbar(final PDFInspectorGUI gui)
  {
    super(true);
    this.gui = gui;
    this.setMargin(new Insets(0, 0, 0, 0));
    this.setBorder(BorderFactory.createEmptyBorder(-4, -4, -4, -4));
    this.pageField = new JTextField("  0/0  ");
    this.pageField.setColumns(7);
    this.pageField.setHorizontalAlignment(JTextField.CENTER);
    this.pageField.setAction(new PageFieldA3());
    this.pageField.setMargin(new Insets(0, 0, 0, 0));

    this.addSeparator();
    this.add(new Action3("First Page", "page-first-24.png")
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        gui.panel.board.setPage(gui.panel.board.page == null ? null : gui.panel.board.page.first());
      }
    });
    this.add(new Action3("Previous Page", "page-prev-24.png")
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        gui.panel.board.setPage(gui.panel.board.page == null ? null : gui.panel.board.page.previous());
      }
    });
    this.add(pageField);
    this.add(new Action3("Next Page", "page-next-24.png")
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        gui.panel.board.setPage(gui.panel.board.page == null ? null : gui.panel.board.page.next());
      }
    });
    this.add(new Action3("Last Page", "page-last-24.png")
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        gui.panel.board.setPage(gui.panel.board.page == null ? null : gui.panel.board.page.last());
      }
    });
    this.addSeparator();
    this.add(zoomCombo);
    this.addSeparator();

    this.add(this.streamCheck = new CheckBox3("Show Streams", true));

    this.add(this.imageBt);
    imageBt.addActionListener(e -> {
      PDFPage page = gui.panel.board.page;      
      Image3 image= page.createImage(new PDFDisplayProps(4).copy(page.mediaBox()));
      image.write(File3.Desk(page.document().fileName().replace(".pdf", "")+"-"+page.number()+".png"));
    });

    // this.add(new RefreshA3());
    // this.add(new InformationA3());
    this.zoomCombo.setSelectedItem("1.0");
    this.zoomCombo.addActionListener(new ZoomListener());

    this.gui.panel.board.paintPanel.addMouseWheelListener(new WheelListener());
    this.gui.add(this, BorderLayout.NORTH);
  }

  public class WheelListener extends MouseAdapter
  {
    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
      int ticks = e.getWheelRotation();
      if (gui.panel.board.page != null || !gui.panel.board.objects.isEmpty())
        if (e.isControlDown())
          zoomTicks(ticks);
        else
          turnTicks(ticks);
    }
  }

  public double scale()
  {
    return Double.parseDouble((String) zoomCombo.getSelectedItem());
  }

  public void zoomTicks(int ticks)
  {
    int index = this.zoomCombo.getSelectedIndex() - ticks;
    int max = this.zoomCombo.getItemCount() - 1;
    index = index < 0 ? 0 : index > max ? max : index;
    this.zoomCombo.setSelectedIndex(index);
  }

  public void turnTicks(int ticks)
  {
    PDFPage page = gui.panel.board.page();
    if (page == null)
      return;
    if (ticks > 0)
      while (ticks-- > 0)
        gui.panel.board.setPage(page.next());
    else
      while (ticks++ < 0)
        gui.panel.board.setPage(page.previous());
  }

  public void updatePageField(PDFPage page)
  {
    if (page != null)
      this.pageField.setText(page.number() + "/" + page.nbOfPages());
  }

  public int getPageField()
  {
    String[] tokens = this.pageField.getText().trim().split("\\D");
    if (tokens.length > 0)
      try
      {
        return Integer.parseInt(tokens[0]);
      } catch (NumberFormatException ex)
      {
      }
    return -1;
  }

  private class PageFieldA3 extends Action3
  {
    public PageFieldA3()
    {
      super("page field");
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
      pageField.getAction().setEnabled(false);
      if (gui.panel.board.page != null)
      {
        PDFPage p = gui.panel.board.page.getPage(getPageField());
        gui.panel.board.setPage(p == null ? gui.panel.board.page : p);
      }
      pageField.getAction().setEnabled(true);
    }
  }

  private class ZoomListener implements ActionListener
  {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      gui.panel.board.displayProps.displayScaling = (float) scale();
      gui.panel.board.repaint();
    }
  }
}
