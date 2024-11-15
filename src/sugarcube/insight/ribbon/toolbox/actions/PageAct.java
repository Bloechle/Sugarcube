package sugarcube.insight.ribbon.toolbox.actions;

import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.ui.fx.menus.FxMenu;
import sugarcube.insight.core.FxRibbonAction;
import sugarcube.insight.ribbon.toolbox.ToolboxRibbon;
import sugarcube.formats.ocd.objects.OCDAnnot;
import sugarcube.formats.ocd.objects.OCDClip;
import sugarcube.formats.ocd.objects.OCDImage;
import sugarcube.formats.ocd.objects.OCDPage;

public class PageAct extends FxRibbonAction<ToolboxRibbon>
{
  public static final int ROTATE_90 = 1;
  public static final int REBOX = 2;
  public static final int REBOX_AS_IMAGE = 3;
  public int method = 0;

  public PageAct(ToolboxRibbon tab, int method)
  {
    super(tab);
    switch (this.method = method)
    {
    case ROTATE_90:
      this.text = "Rotate 90° @Ctrl+9";
      this.setAction(() -> rotateAct());
      break;
    case REBOX:
      this.text = "Set ViewBox";
      this.setAction(() -> rebox());
      break;
    // case REBOX_AS_IMAGE:
    // this.text = "Set Page as Image";
    // this.actor(() -> image());
    // break;
    }
  }

  public void rotateAct()
  {
    OCDPage page = this.tab.page();
    Rebox(page, page.height, page.width);
    for (OCDImage image : page.images())
    {
      Transform3 tm = image.transform();
      tm.rotate(Math.PI / 2);
      image.setTransform(tm);
      image.setX(image.x() + page.width);
    }
    this.done();
  }

  public void rebox()
  {
    OCDPage page = this.tab.page();
    if (this.tab.pager.hasInteractor())
    {
      Rectangle3 box = this.tab.pager.interactor.bounds();

      Rebox(page, box);
    }
    this.done();
  }

  // public void image()
  // {
  // OCDPage page = src.page();
  // Rectangle3 box = src.pager.hasInteractor() ? src.pager.interactor.bounds()
  // : page.viewBounds();
  // TextAct.Delete(src);
  // new AnnotAct(src, AnnotAct.IMAGE).boxes(box).act();
  // Rebox(page, box);
  // this.done();
  // }

  public void addHalfWidthAndHeight()
  {
    OCDPage page = this.tab.page();
    Rebox(page, page.width + page.width / 2, page.height + page.height / 2);
    this.done();
  }

  public static void Rebox(OCDPage page, double w, double h)
  {
    Rebox(page, new Rectangle3(0, 0, w, h));
  }

  public static boolean Rebox(OCDPage page, Rectangle3 box)
  {
    OCDImage image = page.backgroundImage();

    page.width = box.width;
    page.height = box.height;

    if (image != null)
      image.setScale(page.width / image.width(), page.height / image.height());

    OCDAnnot annot = page.annots().get(OCDAnnot.ID_VIEWBOX);
    if (annot == null)
      annot = page.annots().addViewboxAnnot(box, OCDAnnot.ID_VIEWBOX);
    else
      annot.setBounds(box);
    if ((annot = page.annots().get(OCDAnnot.ID_DATABOX)) != null)
      annot.setBounds(box.copy());
    if ((annot = page.annots().get(OCDAnnot.ID_CANVASBOX)) != null)
      annot.setBounds(box.copy().inflate(page.width, page.height));

    OCDClip clip = page.definitions().clip("c0");
    if (clip == null)
      page.definitions().clip("clip0");
    if (clip != null)
      clip.setPath(new Path3(box));

    page.modify();
    return true;
  }

  public static void Rotate(ToolboxRibbon tab, int mode)
  {
    Log.debug(PageAct.class, ".Rotate");

    new PageAct(tab, mode).act();
  }

  public static void Populate(FxMenu menu, ToolboxRibbon tab)
  {
    menu.sepItems(new PageAct(tab, ROTATE_90), new PageAct(tab, REBOX));
    menu.messageIfNoItem();
  }
}