package sugarcube.insight.ribbon.toolbox.actions;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Str;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.ui.fx.menus.FxMenu;
import sugarcube.insight.core.FxRibbon;
import sugarcube.insight.core.FxRibbonAction;
import sugarcube.insight.ribbon.textedit.EditRibbon;
import sugarcube.formats.ocd.objects.*;
import sugarcube.formats.ocd.objects.lists.OCDBlocks;
import sugarcube.formats.ocd.objects.lists.OCDTexts;
import sugarcube.formats.pdf.resources.icons.Icon;

public class TextAct extends FxRibbonAction<FxRibbon>
{
  public static int GROUP = 0;
  public int mode = 0;
  public Rectangle3[] boxes = new Rectangle3[0];

  public TextAct(FxRibbon tab, int mode)
  {
    super(tab, "Group as Paragraph @Ctrl+P", Icon.PARAGRAPH.get(tab.iconSize));
  }

  public TextAct boxes(Rectangle3... boxes)
  {
    this.boxes = boxes;
    return this;
  }

  @Override
  public void act()
  {
    OCDBlocks blocks = Group(this.tab.page(), boxes.length == 0 ? new Rectangle3[]
    { this.tab.selection() } : boxes);
    done(true);
    if (blocks.isPopulated())
      this.tab.pager().pleaseInteract(blocks.last());
  }

  public static OCDBlocks Group(OCDPage page, Rectangle3... boxes)
  {
    OCDBlocks blocks = new OCDBlocks();
    String classname = null;
    for (Rectangle3 box : boxes)
    {
      if (box == null)
        continue;
      OCDTexts texts = new OCDTexts();
      OCDGroup<OCDPaintable> parent = null;

      classname = null;
      for (OCDTextBlock block : page.content().blocks())
      {
        boolean grouping = false;

        for (OCDTextLine line : block.lines())
        {
          for (OCDText text : line.texts())
            if (box.overlapThat(text.bounds()) > 0.5)
            {
              texts.add(text);
              line.remove(text);
              parent = block.parentGroup();
              grouping = true;
            }
          if (line.isEmpty())
            block.remove(line);
        }
        if (block.isEmpty())
          block.remove();

        if (grouping)
        {
          String cls = block.classname();
          if (Str.HasData(cls))
            if (Str.IsVoid(classname))
              classname = cls;
        }
      }

      if (texts.isPopulated())
      {

        if (parent == null)
        {
          Log.warn(TextAct.class, ".act - null parent: " + texts.string());
          parent = page.content();
        }

        OCDTextBlock block = texts.blockize(new OCDTextBlock(parent), false);
        if (classname != null)
        {
          if (Str.HasChar(classname))
            block.setClassname(classname);
        }

        float y = block.firstY();
        OCDTextBlock prev = null;
        for (OCDTextBlock b : parent.blocks())
        {
          if (b.firstY() < y)
            prev = b;
          else
            break;
        }
        blocks.add(block);
        parent.add(block, prev);
      }
    }
    return blocks;
  }

  private void actSplit()
  {
    // OCDText text = this.src.selectedText();
    // OCDTextBlock block = text == null ? null : text.textBlock();
    //
    // Log.debug(this, ".act - block=" + block);
    // if (block != null)
    // {
    // block.groupID = -1;
    // for (OCDTextLine line : block)
    // {
    // int groupID = Canonizer.randomGroupID();
    // for (OCDText t : line)
    // t.groupID = groupID;
    // }
    // }
    //
    // this.resetInteractor();
    // src.update();
  }

  public static void NewLine(EditRibbon tab, OCDText last)
  {
    OCDTextLine line = last.textLine();
    if (line == null)
      return;
    OCDText first = line.first();
    OCDTextBlock block = line.textBlock();
    if (first == null || block == null)
      return;
    NewText(tab, new Point3(first.x(), first.y() + first.fontsize()), block.newLine().newText());
  }

  public static void NewText(EditRibbon tab, Point3 p)
  {
    NewText(tab, p, null);
  }

  public static void NewText(EditRibbon tab, Point3 p, OCDText text)
  {
    if (text == null)
      text = tab.pager.page.content().newBlock().newLine().newText();
    text.setUnicodes("");
    text.setX(p.x);
    text.setY(p.y);
    text.setFontsize(tab.fontsize());
    text.setFont(tab.fontname());
    tab.update();
    tab.selector.select(text, text);
  }

  // public static void NewWord(FedlexRibbon tab)
  // {
  // OCDText text = tab.pager.page().content().newBlock().newLine().newText();
  // Rectangle3 box = tab.pager.interactor.bounds();
  // text.setUnicodes("x");
  // text.setX(box.x);
  // text.setY(box.maxY());
  // text.setFontsize(tab.fontsize());
  // text.setFont(tab.fontname());
  // text.needOCR();
  // text.ocr.box = box;
  // tab.update();
  // }
  //
  // public static void NewWord(FedlexRibbon tab, boolean before)
  // {
  // OCDText text = tab.selectedText();
  // OCDTextLine line = text == null ? null : text.textLine();
  // if (line == null)
  // return;
  // OCDText word = new OCDText(line, "x", text.fontname(), text.fontsize());
  // word.setY(text.y());
  // word.setX(before ? text.x() - text.fontsize() : text.bounds().maxX());
  // word.needOCR();
  // line.add(word, text, before);
  // tab.update();
  // }

  public static void SplitWord(EditRibbon tab)
  {
    OCDText text = tab.selector.end;
    int index = tab.selector.endIndex;
    if (text != null && index > 0 && index < text.length())
    {
      OCDText split = text.split(index);
      if (split != null)
      {
        OCDTextLine line = text.textLine();
        if (line != null)
          line.add(split, text, false);
      }
    }
    tab.update();
  }

//  public static void Delete(TextEditRibbon tab)
//  {
//    for (OCDText text : tab.pager.selectedTexts())
//      Delete(tab, text);
//    tab.pager.stopInteract();
//    tab.selector.reset();
//    tab.update();
//  }

  public static void Delete(FxRibbon tab, OCDText text)
  {
    OCDTextLine line = text.textLine();
    if (line != null)
    {
      OCDTextBlock block = line.textBlock();
      line.remove(text);
      if (line.isEmpty())
        line.remove();
      if (block != null && block.isEmpty())
        block.remove();
    }
  }

  public static void Populate(FxMenu menu, FxRibbon tab)
  {
    // if (tab.pager.hasInteractor())
    // {
    // // menu.item("Create Paragraph", Iconic.Add(tab.iconSize)).act(e ->
    // NewWord(tab));
    menu.sepItems(GroupAsParagraph(tab));
    // menu.sep();
    // menu.item("Insert Text Before",
    // Iconic.CHEVRON_CIRCLE_LEFT.get(tab.iconSize)).act(e -> NewWord(tab,
    // true));
    // menu.item("Insert Text After",
    // Iconic.CHEVRON_CIRCLE_RIGHT.get(tab.iconSize)).act(e -> NewWord(tab,
    // false));
    // }
//    if (tab.pager.hasSelector())
//      menu.sep().item("Split Text").act(e -> SplitWord(tab));

//    menu.sep().item(DeleteTextItem(tab));
    menu.sepItems("Use 'ctrl' to split horizontally", "Use 'shift' to split vertically");
    menu.messageIfNoItem();
  }
  
  public static TextAct GroupAsParagraph(FxRibbon tab)
  {
    return new TextAct(tab, GROUP);
  }
  
//  public static FxMenuItem DeleteTextItem(ToolboxRibbon tab)
//  {
//    return new FxMenuItem("Delete @Del", Icon.Awesome(Icon.REMOVE, tab.iconSize, Color3.RED)).act(e -> Delete(tab));
//  }

}