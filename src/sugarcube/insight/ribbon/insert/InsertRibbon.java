package sugarcube.insight.ribbon.insert;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Cmd;
import sugarcube.common.data.collections.Commands;
import sugarcube.common.graphics.geom.Line3;
import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.system.io.File3;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.base.FxClipboard;
import sugarcube.common.ui.fx.controls.FxToggles;
import sugarcube.common.ui.fx.dnd.FileDnD;
import sugarcube.common.ui.fx.event.FxInput;
import sugarcube.common.ui.fx.event.FxKeyboard;
import sugarcube.common.ui.fx.event.FxMouse;
import sugarcube.common.data.xml.css.CSS;
import sugarcube.insight.Insight;
import sugarcube.insight.core.FxEnvironment;
import sugarcube.insight.core.FxRibbon;
import sugarcube.insight.core.IS;
import sugarcube.insight.interfaces.FxRibbonLoader;
import sugarcube.insight.render.FxOCDNode;
import sugarcube.insight.ribbon.actions.annot.AnnotDialog;
import sugarcube.insight.ribbon.insert.render.InsertPager;
import sugarcube.insight.ribbon.insert.shape.ShapeDialog;
import sugarcube.insight.ribbon.toolbox.actions.DeleteAct;
import sugarcube.formats.ocd.objects.*;
import sugarcube.formats.pdf.resources.icons.Icon;

public class InsertRibbon extends FxRibbon implements Cmd.Handler
{
  public static FxRibbonLoader LOADER = env -> new InsertRibbon(env);

  public @FXML Button openBt, saveBt, localBt, backBt;
  public @FXML TextField xField, yField, wField, hField;
  public @FXML ToggleButton textBt, imageBt, shapeBt, annotBt;
  public @FXML ColorPicker fillBt, strokeBt;
  public @FXML ComboBox<String> dashBox, penBox;
  public FxToggles insertToggles;
  public Commands commands;

  public InsertRibbon(final FxEnvironment env)
  {
    super(env, "Graphic Content");
    this.rightSideSize = 300;
  }

  @Override
  public void init()
  {
    super.init(openBt, saveBt, localBt, backBt);
    super.init(xField, yField, wField, hField);

    this.insertToggles = FxToggles.Handle(textBt, imageBt, shapeBt, annotBt, bt -> startInsertGraphic());
    Color3 green = IS.GREEN_LIGHT;
    Color3 orange = IS.ORANGE_LIGHT;
    Icon.FONT.set(textBt, iconSize, 100, "Insert Text", green);
    Icon.IMAGE.set(imageBt, iconSize, 100, "Insert Image", green);
    Icon.SQUARE.set(shapeBt, iconSize, 100, "Insert Shape", green);
    Icon.EDIT.set(annotBt, iconSize, 100, "Insert Annot", green);

    Fx.Set(dashBox, IS.DASHES, "none", val -> command(CSS.BorderStyle, dashBox));
    Fx.Set(penBox, IS.THICKNESSES, "1.0", val -> command(CSS.BorderWidth, penBox));

    this.commands = new Commands(this);

    strokeBt.setValue(Color3.DUST_WHITE.fx());
    strokeBt.setOnAction(e -> command(CSS.BorderColor, strokeBt));
    fillBt.setOnAction(e -> command(CSS.Color, fillBt));
  }

  @Override
  public InsertPager pager()
  {
    return pager == null ? new InsertPager(this) : (InsertPager) pager;
  }

  public void command(String key, Object o)
  {
    commands.sendIf(events, key, Fx.Value(o), true);
  }

  @Override
  public boolean doHandleClick(FxOCDNode node)
  {
    boolean handleClick = !insertToggles.isSelected() && super.doHandleClick(node);
    Log.debug(this, ".doHandleClick - " + handleClick);
    return handleClick;
  }

  public void refreshNodes()
  {
    this.pager.refreshNodes();
    this.pager.refresh();
  }

  public void startInsertGraphic()
  {
    this.pager.board.restyle("cursor-crosshair");
    this.pager.stopInteract();
  }

  @Override
  public void select()
  {
    super.select();
    this.events = false;
    this.events = true;
  }

  @Override
  public void fileDropped(FileDnD dnd)
  {
    File3[] images = dnd.files("png", "jpg", "mp4");
    if (images.length > 0)
    {
      insertImage(null, images);
      Log.debug(this, ".fileDropped - " + dnd.event().getSceneX());
    }
    this.update();
  }

  public boolean boardKeyEvent(FxKeyboard kb)
  {
    super.boardKeyEvent(kb);
    if (kb.isUp())
    {
      if (!kb.isControlDown())
        switch (kb.getCode())
        {
        case DELETE:
          DeleteAct.Delete(this);
          break;
        default:
          break;
        }
    }
    return false;
  }

  public boolean boardMouseEvent(FxMouse ms)
  {
    // catches all mouse event from board by filtering (not handling)
    super.boardMouseEvent(ms);
    if (ms.isConsumed())
      return true;

    if (ms.isUp())
    {
      ToggleButton toggle = insertToggles.selected();
      if (toggle != null)
      {
        Log.debug(this, ".boardMouseEvent - toggle selected: " + toggle.idProperty());
        if (toggle == imageBt)
          insertImage();
        else if (toggle == textBt)
          insertText();
        else if (toggle == shapeBt)
          ShapeDialog.Show(this);
        else if (toggle == annotBt)
          AnnotDialog.Show(this);
        insertToggles.deselect();
        this.pager.board.restyle();
      }
    }
    return false;
  }

  public void command(Cmd cmd)
  {
    if (!cmd.forward)
    {
      linkAndBrush(cmd);
      return;
    }

    // Log.debug(this, ".intercept - " + cmd);
    // switch (cmd.key)
    // {
    // case TextDecoration:
    // return new Cmd(TextDecoration, ((tab.underBt.isSelected() ? _underline :
    // "") + (tab.strikeBt.isSelected() ? " " + _lineThrough : "")).trim());
    // }
    // return cmd;

    Log.debug(this, ".command - " + cmd);
    if (cmd.map == null)
      this.commands.put(cmd);

    OCDPaintable node = pager.interactor.node();
    if (node != null)
      switch (cmd.key)
      {
      case Cmd.COPY:
        if (node != null)
          FxClipboard.put(node);
        break;
      case Cmd.PASTE:
        if ((node = FxClipboard.ocdNode()) != null)
        {
          OCDAnnot annot = node.asAnnot();
          if (annot == null)
            page().content().add(node.copy());
          else
            page().annots().addAnnotation(annot.copy());
          this.pager.update();
        }
        break;
      case Cmd.DELETE:
        if (node != null)
        {
          this.pager.stopInteract();
          node.delete();
          this.pager.update();
        }
        break;
      default:
        node.command(cmd);
        break;
      }
    update();
  }

  public void linkAndBrush(Cmd cmd)
  {
    events = false;
    switch (cmd.key)
    {
    case CSS.Color:
      fillBt.setValue(cmd.fxColor(null));
      break;
    case CSS.BorderColor:
      strokeBt.setValue(cmd.fxColor(null));
      break;
    }

    events = true;
  }

  public OCDAnnot insertAnnot(String id, String type)
  {
    return insertAnnot(id, type, pager.interactor.activeExtent());
  }

  public OCDAnnot insertAnnot(String id, String type, Line3 extent)
  {
    return insertAnnot(id, type, extent == null ? null : extent.bounds());
  }

  public OCDAnnot insertAnnot(String id, String type, Rectangle3 box)
  {
    if (box == null)
      box = pager.page.bounds();
    OCDAnnot annot = pager.page.modify().addAnnotation(id, type, box);
    refresh();
    pager.pleaseInteract(annot);
    return annot;
  }

  public void insertShape(Path3 path)
  {
    insertShape(path, pager.interactor.activeExtent());
  }

  public void insertShape(Path3 path, Line3 extent)
  {
    OCDPath ocdPath = pager.page.content().newPath();
    ocdPath.setPath(path.newExtent(extent));
    // Log.debug(this, ".act - setting path color: "+tab.commands.toString());
    Log.debug(this, ".act - cmd: " + commands.cmd(CSS.Color).toString());
    ocdPath.setFillColor(commands.color(CSS.Color, ocdPath.fillColor()));
    ocdPath.setStrokeColor(commands.color(CSS.BorderColor, ocdPath.strokeColor()));
    ocdPath.setStrokeWidth(commands.real(CSS.BorderWidth, ocdPath.strokeWidth()));

    // pager.pleaseInteract(pager.board.addContentNode(pager.newPath(ocdPath)),
    // null);
  }

  public void insertImage()
  {
    insertImage(pager.interactor.activeExtent(), Fx.Chooser("Select Image").dir(ocd().fileDirectory()).open(window()));
  }

  public void insertImage(Line3 extent, File3... files)
  {
    OCDPage page = pager.page;
    if (page == null || !File3.HasFile(files))
      return;

    if (extent == null)
      extent = pager.interactorExtent(true);

    for (File3 file : files)
      if (file.exists())
      {
        OCDImage image = page.content().newImage();
        image.setFromFile(file, extent);
        page.document().imageHandler.addEntry(image);
        // pager.pleaseInteract(pager.board.addContentNode(pager.newImage(image)),
        // null);
      }
    
    this.update();
  }

  public void insertText()
  {
    insertText(pager.interactor.activeExtent());
  }

  public void insertText(Line3 extent)
  {
    OCDFlow ocdFlow = pager.page.content().newFlow();

    ocdFlow.setExtent(extent);
    ocdFlow.newParagraph();

    // Log.debug(this, ".act - setting path color: "+tab.commands.toString());
    Log.debug(this, ".act - cmd: " + commands.cmd(CSS.Color).toString());
    // ocdPath.setFillColor(tab.commands.color(CSS.Color, ocdPath.fillColor()));
    // ocdPath.setStrokeColor(tab.commands.color(CSS.BorderColor,
    // ocdPath.strokeColor()));
    // ocdPath.setStrokeWidth(tab.commands.real(CSS.BorderWidth,
    // ocdPath.strokeWidth()));

    // pager.pleaseInteract(pager.board.addContentNode(pager.newTextFlow(ocdFlow)),
    // null);

  }

  @Override
  public boolean boardPopup(FxInput in)
  {
    Log.debug(this, ".boardPopup - context");
    this.popup.clear();

    popup.sep();
    //
    // StyleTree.Generate(modeler.styles()).populate(this, popup.menu("Style",
    // Icon.TAG.get(iconSize, Color3.COLUMBIA_BLUE)), true);
    // TextAct.Populate(popup.sepMenu("Text", Icon.ALIGN_JUSTIFY.get(iconSize)),
    // this);
    // TableAct.Populate(popup.sepMenu("Table", Icon.TABLE.get(iconSize)),
    // this);
    // GroupAct.Populate(popup.sepMenu("Group",
    // Icon.OBJECT_GROUP.get(iconSize)), this);
    // AnnotAct.Populate(popup.sepMenu("Annotation", Icon.EDIT.get(iconSize)),
    // this);
    // // CopyAct.Populate(popup.sepMenu("Copy", Icon.COPY.get(iconSize)),
    // this);
    // PageAct.Populate(popup.sepMenu("Page", Icon.NEWSPAPER_ALT.get(iconSize)),
    // this);
    // ImageAct.Populate(popup.sepMenu("Image", Icon.IMAGE.get(iconSize)),
    // this);

    // OCRAct.Populate(popup.sepMenu("OCR", Icon.FILE_TEXT.get(iconSize)),
    // this);

    // menu.add(new ListAct(this), new ItemAct(this), new
    // SublistAct(this), new SublistEndAct(this));
    // menu.add(new GroupAct(this, false, false, "Current Page",
    // OCDGroup.COLUMN));
    // if (nodes.isAnnot())
    // menu.add("-", new DuplicateAnnotA3(this, nodes.annot()), new
    // RemoveAnnotA3(this));
    this.popup.show(env.gui.board.canvas, in.screenXY());
    return true;
  }

  @Override
  public void dispose()
  {

  }

  public static void main(String... args)
  {
    Insight.main();
  }

}
