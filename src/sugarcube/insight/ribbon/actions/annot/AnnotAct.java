package sugarcube.insight.ribbon.actions.annot;

import sugarcube.common.data.collections.Str;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.ui.fx.menus.FxMenu;
import sugarcube.insight.core.FxRibbon;
import sugarcube.insight.core.FxRibbonAction;
import sugarcube.formats.ocd.objects.OCDAnnot;
import sugarcube.formats.ocd.objects.OCDPage;

public class AnnotAct extends FxRibbonAction<FxRibbon>
{

  public Rectangle3[] boxes = new Rectangle3[0];
  public AnnotItem item;

  public AnnotAct(FxRibbon tab, AnnotItem item)
  {
    super(tab);
    this.item = item;
    this.text = item.toString();
    this.setAction(() -> insertAnnot());
  }

  public static AnnotItem A(String type, String className)
  {
    return new AnnotItem(type, className);
  }

  public AnnotAct boxes(Rectangle3... boxes)
  {
    this.boxes = boxes;
    return this;
  }

  public void insertAnnot()
  {
    OCDPage page = this.tab.page();
    if (this.tab.pager.hasInteractor())
    {
      Rectangle3 box = this.tab.pager.interactor.bounds();
//      if (tab.batchDialog != null)
//        tab.batchDialog.process(p -> insert(p, box, item.type, item.className));
//      else
        insert(page, box, item.type, item.className);
    }

    this.done(false);
//    src.refreshObjectPane();
    // pager.pleaseInteract(annot);
  }

  public boolean insert(OCDPage page, Rectangle3 box, String type, String className)
  {
    if (box == null)
      box = page.bounds();
    OCDAnnot annot = page.modify().addAnnotation(page.autoID(OCDAnnot.TAG), type, box);
    if (Str.HasChar(className))
      annot.setClassname(className);
    return true;

  }

  // public void layoutAct(String classname)
  // {
  // for (Rectangle3 box : box())
  // src.page().annots().addLayoutAnnot(box, classname);
  // this.done();
  // }

  public static void Populate(FxMenu menu, FxRibbon tab)
  {
    if (tab.pager.hasInteractor())
    {
      for (AnnotItem item : AnnotDialog.ITEMS)
        menu.item(new AnnotAct(tab, item));
    }
    menu.messageIfNoItem();
  }
}