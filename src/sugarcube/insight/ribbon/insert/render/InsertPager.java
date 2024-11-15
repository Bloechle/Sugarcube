package sugarcube.insight.ribbon.insert.render;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Set3;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.ui.fx.shapes.FxCircle;
import sugarcube.insight.core.IS;
import sugarcube.insight.render.FxOCDNode;
import sugarcube.insight.render.FxPager;
import sugarcube.insight.ribbon.insert.InsertRibbon;
import sugarcube.formats.ocd.objects.OCDAnnot;
import sugarcube.formats.ocd.objects.OCDPage;
import sugarcube.formats.ocd.objects.OCDPaintable;
import sugarcube.formats.ocd.objects.OCDText;
import sugarcube.formats.ocd.objects.lists.OCDList;

public class InsertPager extends FxPager<InsertRibbon>
{  

  public InsertPager(InsertRibbon tab)
  {
    super(tab, true);
  }
  
  public boolean preventFocusOver()
  {
    return tab.insertToggles.isSelected();
  }

  @Override
  public void update(OCDPage page)
  {
    boolean updated = false;
    if (this.page != page)
    {

    }

    if (!updated)
      super.update(page);
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
    // if (this.showAnnotsLayer)
    // for (OCDAnnot annot : page.annots())
    // if (!annot.isViewbox())
    // board.annots.add(newFxNode(annot));

  }

  public OCDList selected()
  {
    OCDList list = new OCDList();
    Log.debug(this, ".selected");

    // if (isMode(INTERACTION))
    // list.setAll(pager.interactor.blocks());
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

  public FxOCDNode createFxNode(OCDPaintable node, FxOCDNode parent)
  {
    switch (node.cast())
    {
    case "OCDPath":
      return new InsertPath(this, node.asPath());
    case "OCDText":
      return new InsertText(this, node.asText());
    case "OCDTextLine":
      return new InsertTextLine(this, node.asTextLine());
    case "OCDTextBlock":
      return new InsertTextBlock(this, node.asTextBlock(), parent);
    case "OCDImage":
      return new InsertImage(this, node.asImage());
    case "OCDContent":
      return new InsertContent(this, node.asContent());
    case "OCDAnnot":
      return new InsertAnnot(this, node.asAnnot());
    }
    return super.createFxNode(node, parent);
  }

  public void draw(Point3 p)
  {
    board.glass.add(new FxCircle(p.x, p.y, 2).fill(IS.HIGHLIGHTED_COLOR));
  }

}
