package sugarcube.insight.ribbon.insert.render;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Cmd;
import sugarcube.common.data.collections.List3;
import sugarcube.common.graphics.geom.Line3;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.event.FxMouse;
import sugarcube.common.data.xml.css.CSS;
import sugarcube.insight.render.FxOCDNode;
import sugarcube.insight.render.interaction.FxInteractor;
import sugarcube.formats.ocd.objects.OCDTextBlock;
import sugarcube.formats.ocd.objects.OCDTextLine;
import sugarcube.formats.ocd.objects.font.SVGFont;

public class InsertTextBlock extends InsertNode<OCDTextBlock>
{
  public static Font FONT = Font.font("Calibri", javafx.scene.text.FontWeight.BOLD, 10);
  public static Color WHITE = Color3.WHITE.fx();
  public static Color BLUE = Color3.SC_BLUE.alpha(0.6).fx();
  public Color3 color;
  public Rectangle3 bounds;
  public boolean isRootContent = true;

  public InsertTextBlock(InsertPager pager, final OCDTextBlock block, FxOCDNode parent)
  {
    super(pager, block);
    this.parent = parent;

    if (isRootContent = parent == null)
    {
      this.isResizable = true;
      this.isMovable = true;
      this.handleMouseEvents();
      this.focusOnMouseOver();
    } else
      this.mouseTransparent();
  }

  @Override
  public InsertTextBlock refresh()
  {
    if (node == null)
      return this;
    this.clear();

    boxing(bounds = node.bounds());

    for (OCDTextLine line : node)
      this.add(new InsertTextLine(pager, line).refresh());

    // if (isRootContent)
    // box.paint(Color3.SC_BLUE.alpha(0.1), Color3.SC_BLUE.alpha(0.8), 1);
    return this;
  }

  public String classname(OCDTextBlock block, String style)
  {
    return style == null || style.equals("text") ? "" : (style.equals("^text") ? "^" : style);
  }

  @Override
  public Rectangle3 bounds()
  {
    return bounds;
  }

  public void mouseEvent(FxMouse ms)
  {

    if (!ms.hasCtrl() && ms.isPrimaryClick())
    {
      pager.pleaseInteract(this, ms);
    }

//    if (ms.isOut())
//      pager.env.gui.status("");
//    else
//      pager.env.gui.status("style=" + node.classname());
  }

  public void split(OCDTextBlock b, Point3 p, boolean horiz, boolean extend)
  {
    List3<OCDTextBlock> blocks = new List3<OCDTextBlock>(b);

    if (extend)
      for (OCDTextBlock block : pager.page.content().blocks())
        if (block.bounds().hasOverlap(p, !horiz))
          blocks.add(block);

    for (OCDTextBlock block : blocks)
    {
      block.splitXY(horiz ? p.y() : p.x(), horiz);
      block.page().modify();
    }
    pager.tab.update();
  }

  // public void refreshBoxColor()
  // {
  // super.refreshBoxColor();
  // if (!selected && !highlighted && pager.props.highlightTexts)
  // this.box.fill(TB.SMOKED_GLASS);
  // }

  @Override
  public void interacted(FxInteractor interactor)
  {

    // Log.debug(this, ".interacted - " + interactor.extent() + ", this=" +
    // this.extent());
    // this.node.setExtent(interactor.extent());
    if (interactor.isMoving())
    {
      // this.move();
    } else if (interactor.isResizing())
    {
      // node.inflate();
    } else
    {
      Log.debug(this, ".interacted - state: " + interactor.state());
    }

    Line3 l = interactor.extent();
    node.moveTo(l.x(), l.y());
    this.bounds = node.bounds();
    this.refresh();
  }

  // public synchronized void move()
  // {
  // // only moving avoids inflating or altering relative positioning
  // Line3 extent = this.extent();
  // List3<OCDTextBlock> blocks = node.blocks();
  // OCDTextBlock firstBlock = blocks.first();
  //
  // OCDTextLine firstLine = firstBlock == null ? null : firstBlock.first();
  // OCDText first = firstLine == null ? null : firstLine.first();
  //
  // if (first != null)
  // {
  // float fs = firstLine.maxFontsize();
  // float dx = extent.x1 - first.x();
  // float dy = extent.y1 + (fs > 0 ? fs : 12) - first.y();
  // for (OCDTextBlock block : blocks)
  // for (OCDTextLine line : block)
  // for (OCDText text : line)
  // text.setXY(text.x() + dx, text.y() + dy);
  // }
  // // Log.debug(this, ".move - first=" + (first == null ? "null" :
  // // first.string()));
  // this.refresh();
  // }

  public boolean cssNormal(String s)
  {
    return s == null || s.trim().isEmpty() || s.trim().equalsIgnoreCase(CSS._normal);
  }

  public String name(String family)
  {
    return SVGFont.Rename(family, !cssNormal(commands().string(CSS.FontWeight, CSS._normal)),
        !cssNormal(commands().string(CSS.FontStyle, CSS._normal)));
  }

  @Override
  public void command(Cmd cmd)
  {
    Log.debug(this, ".command - " + cmd + ", node=" + (node == null ? "null" : node.tag));
    if (node != null)
      node.command(cmd);
  }

  // @Override
  // public synchronized void command(Cmd cmd,FxBoard board)
  // {
  // super.command(cmd);
  //
  // }

  // @Override
  // public void commandBack()
  // {
  // OCDText end = pager.selector.end == null ? node.firstText() :
  // pager.selector.end;
  //
  // if (end != null && end.fontname() != null && !end.fontname().isEmpty())
  // {
  // String fontname = end.fontname();
  // Commands commands = commands();
  // OCDTextBlock block = end.textBlock();
  // commands.back(FontName, fontname);
  // commands.back(FontSize, end.fontsize());
  // commands.back(TextDecoration, end.decoration());
  // commands.back(TextScript, end.isSuperscript() ? _superscript :
  // end.isSubscript() ? _subscript : _normal);
  // commands.back(TextAlign, block.align());
  // commands.back(LineHeight, block.interline());
  // commands.back(LetterSpacing, block.charspace());
  // commands.back(Color, end.fillColor());
  // commands.back(BorderColor, end.strokeColor());
  // commands.back(BorderWidth, end.strokeWidth());
  // // Log.debug(this, ".recommand - end=" + (end == null ? "null" :
  // // end.string()) + ", align=" + end.textBlock().align());
  // }
  // }

}
