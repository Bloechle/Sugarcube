package sugarcube.insight.ribbon.toolbox.actions;

import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.ui.fx.base.FxClipboard;
import sugarcube.common.ui.fx.menus.FxMenu;
import sugarcube.insight.core.FxRibbon;
import sugarcube.insight.core.FxRibbonAction;
import sugarcube.insight.ribbon.toolbox.ToolboxRibbon;
import sugarcube.formats.ocd.objects.OCDAnnot;
import sugarcube.formats.ocd.objects.OCDAnnotations;
import sugarcube.formats.ocd.objects.OCDPaintable;

public class CopyAct extends FxRibbonAction<FxRibbon>
{
  public static final int XML_PAGE = 0;

  public int method = 0;

  public CopyAct(FxRibbon tab, int method)
  {
    super(tab);
    switch (this.method = method)
    {
    case XML_PAGE:
      this.text = "Copy to All Pages";
      this.setAction(() -> copyAll());
      break;
    }
  }

  public void copyAll()
  {
    OCDPaintable node = tab.pager.interactorNode();
    if (node != null)
    {
      if (node.isAnnot())
      {
        OCDAnnot annot = node.asAnnot();
        Rectangle3 box = annot.bounds();
        String id = annot.id();
        tab.env().saveOCD(page -> {
          tab.process(page);
          OCDAnnot copy = annot.copy();          
          OCDAnnotations annots = page.annots().id(id);
          if (annots.isEmpty())
            page.modify().annots().addAnnotation(copy);
          else
            Log.debug(this,  ".copyAll - "+annots);
          return true;
        });
      }
    }
    else
      Log.debug(this,  ".copyAll - null node");
  }

  public void copyXmlPage()
  {
    FxClipboard.put(this.tab.page().xmlString());
  }

  public static void Populate(FxMenu menu, ToolboxRibbon tab)
  {
    menu.sepItems(new CopyAct(tab, XML_PAGE));
  }
}