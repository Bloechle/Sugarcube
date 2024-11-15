package sugarcube.formats.pdf.reader.gui;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Set3;
import sugarcube.common.ui.gui.*;
import sugarcube.common.data.xml.Treezable;
import sugarcube.formats.pdf.reader.pdf.node.PDFDocument;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.node.PDFPage;
import sugarcube.formats.pdf.reader.pdf.object.*;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.io.File;

public class InspectorPanel extends Panel3 implements Worker3.Processable<File>
{
  protected SplitPane3 horizontalSplit;
  protected SplitPane3 verticalSplit;
  protected TextArea3 objInfo;
  protected TextArea3 pdfInfo;
  protected InspectorBoard board;
  protected Tree3 pdfTree;
  protected Tree3 objTree;
  protected PDFInspectorGUI gui;
  protected PDFEnvironment env;
  protected PDFDocument doc;

  public InspectorPanel(PDFInspectorGUI gui)
  {
    this.gui = gui;
    this.objTree = new Tree3();
    this.objTree.setCellRenderer(new PDFTreeRenderer());
    this.objTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    this.objTree.addTreeSelectionListener(new PDFObjectTreeListener());
    this.pdfTree = new Tree3();
    this.pdfTree.setCellRenderer(objTree.getCellRenderer());
    this.pdfTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    this.pdfTree.addTreeSelectionListener(new PDFNodeTreeListener());
    this.board = new InspectorBoard(gui, gui.canonizerProps());
    this.objInfo = new TextArea3();
    this.pdfInfo = new TextArea3();

    SplitPane3 objSplit = new SplitPane3(false, new ScrollPane3(objTree), new ScrollPane3(objInfo), 500);
    SplitPane3 pdfSplit = new SplitPane3(false, new ScrollPane3(pdfTree), new ScrollPane3(pdfInfo), 500);
    SplitPane3 leftSplit = new SplitPane3(true, objSplit, board);
    SplitPane3 rightSplit = new SplitPane3(true, leftSplit, pdfSplit);

    leftSplit.getLeftComponent().setMinimumSize(new Dimension(300, -1));
    rightSplit.getRightComponent().setMinimumSize(new Dimension(300, -1));
    leftSplit.setResizeWeight(0);
    rightSplit.setResizeWeight(1);

    this.add(rightSplit);
    this.env = new PDFEnvironment();
    this.doc = new PDFDocument();
  }

  public void setFile(File file)
  {
    this.env = new PDFEnvironment();
    this.doc = new PDFDocument();
    new Worker3<File>(this, file);
  }

  @Override
  public void process(Worker3 worker, File... data)
  {
    env.parse(data.length > 0 ? data[0] : null);
    try {
		doc.parse(env, false);
	} catch (Exception e) {
		e.printStackTrace();
	}
  }

  @Override
  public void done(Worker3 worker, File... data)
  {
    this.objTree.setModel(new Tree3.Model(env));
    this.pdfTree.setModel(new Tree3.Model(doc));
    PDFPage page = doc.firstPage();
    if (page != null)
      this.board.setPage(page);
  }

  public class PDFNodeTreeListener implements TreeSelectionListener
  {
    @Override
    public void valueChanged(TreeSelectionEvent e)
    {
      Treezable[] treezables = pdfTree.getSelectedPathComponents();
      Set3<PDFNode> objects = new Set3<PDFNode>();
      for (Treezable treezable : treezables)
        if (treezable instanceof PDFNode)
          objects.add((PDFNode) treezable);
        else
          Log.info(this, ".valueChanged - treezable not instance of VirtualObject: " + treezable.getClass());


      if (!objects.isEmpty())
      {
        PDFNode node = objects.first();

        StreamLocator locator = node.streamLocator();

        if (locator != null && gui.isStreamChecked())
          highlightLocation(locator);

        pdfInfo.setText(node.toString());
        pdfInfo.setCaretPosition(0);

        PDFPage page = node.page();
        if (page != null && !page.isInMemory())
        {
          page.ensureInMemory();
          pdfTree.refreshStructure(page.contents());
        }
        board.setObjects(objects);
      }
    }
  }

  public class PDFObjectTreeListener implements TreeSelectionListener
  {
    @Override
    public void valueChanged(TreeSelectionEvent e)
    {
      Treezable treezable = objTree.getLastSelectedPathComponent();

      if (treezable instanceof PDFObject)
      {
        PDFObject po = (PDFObject) treezable;
        objInfo.setText(po.toString());
        objInfo.setCaretPosition(0);

        if (po.parent() != null && po.parent().isPDFStream())
        {
          PDFStream pdfStream = po.parent().toPDFStream();
          objInfo.setText(pdfStream.toString());
          objInfo.setCaretPosition(0);
        }
        else if (po.isPDFPointer())
          for (Treezable node : objTree.getModel().getNodes())
            if (((PDFObject) node).reference().equals(((PDFPointer) po).get()))
              objTree.setSelectionPath(objTree.getModel().getTreePath(node));
      }
    }
  }

  public void highlightLocation(StreamLocator locator)
  {
    //XED.LOG.debug(this, ".highlightLocation: locator=" + locator);
    if (locator.reference == null)
      return;

    PDFObject po = null;
    for (Treezable node : objTree.getModel().getNodes())
      if (((PDFObject) node).reference().equals(locator.reference))
      {
        po = (PDFObject) node;
        objTree.setSelectionPath(objTree.getModel().getTreePath(node));
      }

    if (po != null)
    {
      objInfo.requestFocus();
      objInfo.setText(po.isPDFStream() ? po.toPDFStream().asciiValue() : po.toString());
      objInfo.select(locator.length > 0 ? locator.pointer : locator.pointer - 1, locator.pointer + locator.length);
    }
  }
}
