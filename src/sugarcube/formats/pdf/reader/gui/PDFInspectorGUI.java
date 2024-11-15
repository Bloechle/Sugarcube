package sugarcube.formats.pdf.reader.gui;

import sugarcube.common.graphics.geom.Compass;
import sugarcube.common.system.io.DragAndDrop;
import sugarcube.common.system.io.File3;
import sugarcube.common.ui.gui.Action3;
import sugarcube.common.ui.gui.FileChooser3;
import sugarcube.common.ui.gui.Frame3;
import sugarcube.common.ui.gui.icons.ImageIcon3;
import sugarcube.formats.pdf.reader.Dexter;
import sugarcube.formats.ocd.analysis.DexterProps;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class PDFInspectorGUI extends Frame3 implements DragAndDrop.Listener
{
  public static final ImageIcon3 PDF_ICON = new ImageIcon3("pdf-16.png");
  public static final ImageIcon3 OCD_ICON = new ImageIcon3("ocd-16.png");
  private File3 file = null;
  // private Action3 openA3 = new OpenA3();
  // private Action3 exitA3 = new ExitA3();
  private DexterProps dexterProps = new DexterProps();
  protected InspectorToolbar toolbar;
  protected InspectorPanel panel;

  public PDFInspectorGUI()
  {
    super("PDF Inspector - Dexter v" + Dexter.VERSION.versionValue(), 0.75, 1, Compass.EAST);
    this.sugarcubize();
    this.panel = new InspectorPanel(this);
    this.setComponent(panel);
    this.toolbar = new InspectorToolbar(this);
    this.setTransferHandler(new DragAndDrop(this));
    this.display();
  }

  public boolean isStreamChecked()
  {
    return toolbar.streamCheck.isSelected();
  }

  public DexterProps canonizerProps()
  {
    return this.dexterProps;
  }

  public File file()
  {
    return file;
  }

  @Override
  public void dropped(DragAndDrop dnd)
  {
    updateFile(dnd.file());
  }

  public class OpenA3 extends Action3
  {
    public OpenA3()
    {
      super("Open (PDF, OCD)...");
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
      FileChooser3 fileChooser = new FileChooser3(file).filter("pdf", "PDF", "ocd", "OCD", "zip", "ZIP");
      if (fileChooser.showOpenDialog(PDFInspectorGUI.this) == JFileChooser.APPROVE_OPTION)
        updateFile(fileChooser.getSelectedFile());
    }
  }

  public class ExitA3 extends Action3.Exit
  {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      PDFInspectorGUI.this.close();
    }
  }


  private synchronized void updateFile(File file)
  {
    this.file = new File3(file);
    String name = file.getName();
    if (name.toLowerCase().endsWith(".pdf"))
      this.panel.setFile(file);
  }

  public static void main(String... args)
  {
    SwingUtilities.invokeLater(() -> new PDFInspectorGUI());
  }

  @Override
  public void close()
  {
    System.out.println("InspectorGUI.close - exiting framework");
    super.close();
    System.exit(0);
  }
}
