package sugarcube.insight.ribbon.toolbox.render;

import javafx.print.*;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Set3;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.shapes.FxCircle;
import sugarcube.common.ui.fx.shapes.FxLine;
import sugarcube.insight.core.IS;
import sugarcube.insight.render.FxOCDNode;
import sugarcube.insight.render.FxPager;
import sugarcube.insight.ribbon.toolbox.ToolboxRibbon;
import sugarcube.formats.ocd.objects.OCDAnnot;
import sugarcube.formats.ocd.objects.OCDPage;
import sugarcube.formats.ocd.objects.OCDPaintable;
import sugarcube.formats.ocd.objects.OCDText;
import sugarcube.formats.ocd.objects.lists.OCDList;

public class ToolPager extends FxPager<ToolboxRibbon>
{
  public ToolMetaLayer metaLayer = new ToolMetaLayer(this);
  public FxLine glassSplitLine = new FxLine().pen(1).dash(4, 4).stroke(IS.SELECTED_COLOR.alpha(0.9).fx()).mouseTransparent();
  public FxLine glassDragLine = new FxLine().pen(3).stroke(Color3.SC_BLUE.alpha(0.5).fx()).mouseTransparent();

  public boolean showROrderLayer = false;

  public ToolPager(ToolboxRibbon tab)
  {
    super(tab, true);
  }

  @Override
  public void update(OCDPage page)
  {
    super.update(page);
  }

  public void displayROrder(boolean doDisplay)
  {
    if (doDisplay != showROrderLayer)
    {
      this.showROrderLayer = doDisplay;
      this.update();
    }
  }

  @Override
  public void refresh()
  {
    super.refresh();

    board.annots.clear();

    if (page == null)
      return;

    for (OCDAnnot annot : page.annots())
      if (!annot.isViewbox())
        board.annots.add(fxNode(annot, null));

    board.metaLayer("modeler").clear();

    if (showROrderLayer)
    {
      board.metaLayer("tool", metaLayer);
      metaLayer.refresh();
    } else
      board.removeMetaLayer("tool");

    // background.setOpacity(opacity > 0.5 ? 1 : opacity * 2);
    // content.setOpacity(opacity < 0.5 ? 1 : (1 - opacity) * 2);

    // this.visit(node -> {
    // if (node.isImageNode())
    // node.setOpacity(opacity);
    // return false;
    // });

  }





  public OCDList selected()
  {
    OCDList list = new OCDList();
    Log.debug(this, ".selected");

    if (list.isEmpty() && interactor.hasSelection())
      list.setAll(interactor.blocks());
    if (list.isEmpty() && !focus.selected.isEmpty())
      list.addAll(focus.selectedNodes());

    return list;
  }

  // public OCDText selectedText()
  // {
  //
  // OCDPaintable node = interactor.node();
  // if (node != null && node.isText())
  // {
  // text = node.asText();
  // index = -1;
  // }
  //
  // if (text == null && mouseClick != null)
  // text = page.textAt(mouseClick.xy());
  // return text;
  // }

  public List3<OCDText> selectedTexts()
  {

    Rectangle3 box = interactor.bounds();
    Set3<OCDText> texts = new Set3<>();

    for (OCDPaintable node : focus.selectedNodes())
      if (node.isText())
        texts.add(node.asText());

    for (OCDText text : page.content().allTexts())
      if (box.overlapThat(text.bounds()) > 0.5)
        texts.add(text);

    return texts.list();
  }

  public void print()
  {
    Printer printer = Printer.getDefaultPrinter();
    PageLayout pageLayout = printer.createPageLayout(Paper.NA_LETTER, PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT);
    // double scaleX = pageLayout.getPrintableWidth() /
    // node.getBoundsInParent().getWidth();
    // double scaleY = pageLayout.getPrintableHeight() /
    // node.getBoundsInParent().getHeight();
    // node.getTransforms().add(new Scale(scaleX, scaleY));

    PrinterJob job = PrinterJob.createPrinterJob();
    Log.debug(this, ".print - job=" + job);
    if (job != null)
    {
      boolean success = job.printPage(board.page);
      if (success)
      {
        job.endJob();
      }
    }
  }

  public FxOCDNode createFxNode(OCDPaintable node, FxOCDNode parent)
  {
    switch (node.cast())
    {
    case "OCDPath":
      return new ToolPath(this, node.asPath());
    case "OCDText":
      return new ToolText(this, node.asText());
    case "OCDTextLine":
      return new ToolTextLine(this, node.asTextLine());
    case "OCDTextBlock":
      return new ToolTextBlock(this, node.asTextBlock());
    case "OCDImage":
      return new ToolImage(this, node.asImage());
    case "OCDAnnot":
      return new ToolAnnot(this, node.asAnnot());

    }

    return super.createFxNode(node, parent);
  }

  // public void insertAudio(File3 file, Line3 extent)
  // {
  // if (page == null || file == null)
  // return;
  //
  // List8 entries = new List8();
  // entries.add(ocd().audioHandler.addEntry(file.bytes(),
  // file.name()).entryPath);
  // OCDAudioAnnot annot = page.annotations().addAudioAnnot(extent.bounds(),
  // entries);
  // page().annotations().addRelativeAnnot(new Rectangle3(extent.maxX() + 20,
  // extent.cy() - 10, 200, 20), "controls", annot.id());
  //
  // this.pleaseInteract(addContentNode(newAnnot(annot)));
  // tab.refresh();
  // }
  //
  //
  // public void insertVideo(File3 file, Line3 extent)
  // {
  // if (page == null || file == null)
  // return;
  //
  // List8 entries = new List8();
  // entries.add(ocd().videoHandler.addEntry(file.bytes(),
  // file.name()).entryPath);
  // OCDVideoAnnot annot = page.annotations().addVideoAnnot(extent.bounds(),
  // entries);
  // page().annotations().addRelativeAnnot(new Rectangle3(extent.maxX() + 20,
  // extent.cy() - 10, 320, 240), "controls", annot.id());
  //
  // this.pleaseInteract(addContentNode(newAnnot(annot)));
  // tab.refresh();
  // }

  public void draw(Point3 p)
  {
    board.glass.add(new FxCircle(p.x, p.y, 2).fill(IS.HIGHLIGHTED_COLOR));
  }

  public void splitLine(Rectangle3 box, Point3 p, boolean horiz)
  {
    if (horiz)
      splitLine(box.minX(), p.y(), box.maxX(), p.y());
    else
      splitLine(p.x(), box.minY(), p.x(), box.maxY());
  }

  public void splitLine(double x, double y, double x2, double y2)
  {
    board.glass.add(glassSplitLine.set(x, y, x2, y2));
  }

  public void dragLine(double x, double y, double x2, double y2)
  {
    board.glass.add(glassDragLine.set(x, y, x2, y2));
  }

  public void clearGlass()
  {
    board.glass.remove(glassSplitLine);
    board.glass.remove(glassDragLine);
  }

}
