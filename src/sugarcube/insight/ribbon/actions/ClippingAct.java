package sugarcube.insight.ribbon.actions;

import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.ui.fx.menus.FxMenu;
import sugarcube.insight.core.FxRibbon;
import sugarcube.insight.core.FxRibbonAction;
import sugarcube.formats.ocd.objects.OCDClip;
import sugarcube.formats.ocd.objects.OCDPaintable;
import sugarcube.formats.pdf.resources.icons.Icon;

public class ClippingAct extends FxRibbonAction<FxRibbon>
{
  public int method = 0;

  public ClippingAct(FxRibbon tab)
  {
    super(tab);
    this.text = "Add Clip";
    this.icon = Icon.PAPERCLIP.get(tab.iconSize);
    this.setAction(() -> addClip(null));
  }

  public ClippingAct(FxRibbon tab, OCDPaintable node, String clipID)
  {
    super(tab);
    this.text = "Set Clip ID to " + clipID;
    this.icon = Icon.PAPERCLIP.get(tab.iconSize);
    this.setAction(() -> setClipID(node, clipID));
  }

  public void addClip(Rectangle3 box)
  {
    if (box == null)
      box = this.tab.pager.interactor.bounds();
    OCDClip clip = tab.page().defs().newClip(box);
    tab.update();
    // tab.pleaseInteract(content);
  }

  public void setClipID(OCDPaintable node, String clipID)
  {
    node.setClipID(clipID);
    tab.update();
  }

  public static void Populate(FxMenu menu, FxRibbon tab)
  {
    if (tab.pager.hasInteractor())
    {

      menu.items(new ClippingAct(tab));
    }

    OCDPaintable focus = tab.pager.node();
    if (focus != null)
    {
      menu.sep();
      for (String clipID : tab.page().defs().clipIDs())
        menu.items(new ClippingAct(tab, focus, clipID));
    }

    menu.messageIfNoItem();
  }

}
