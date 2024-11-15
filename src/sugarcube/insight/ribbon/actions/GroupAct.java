package sugarcube.insight.ribbon.actions;

import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.ui.fx.menus.FxMenu;
import sugarcube.insight.core.FxRibbon;
import sugarcube.insight.core.FxRibbonAction;
import sugarcube.insight.ribbon.toolbox.actions.TextAct;
import sugarcube.formats.ocd.objects.OCDContent;
import sugarcube.formats.ocd.objects.OCDGroup;
import sugarcube.formats.ocd.objects.OCDPage;
import sugarcube.formats.ocd.objects.OCDPaintable;
import sugarcube.formats.ocd.objects.lists.OCDList;
import sugarcube.formats.pdf.resources.icons.Icon;

import java.util.Iterator;

public class GroupAct extends FxRibbonAction<FxRibbon>
{
  public static final int UNGROUP = -1;
  // public static final int GROUP = 0;
  public static final int IMAGE = 1;
  public static final int BACKGROUND = 2;

  public static final int CONTENT = 3;
  public int method = 0;

  public GroupAct(FxRibbon tab, int method)
  {
    super(tab);
    switch (this.method = method)
    {
    case UNGROUP:
      this.text = "Ungroup";
      this.icon = Icon.CHAIN_BROKEN.get(tab.iconSize);
      this.setAction(() -> ungroup());
      break;
    case CONTENT:
      this.text = "Group as Content @Ctrl+G";
      this.icon = Icon.IMAGE.get(tab.iconSize);
      this.setAction(() -> group(null, OCDGroup.CONTENT));
      break;
    // case GROUP:
    // this.text = "Group Graphics";
    // this.actor(() -> groupAct());
    // break;
    // case IMAGE:
    // this.text = "Convert to Image";
    // this.actor(() -> imageAct());
    // break;
    // case BACKGROUND:
    // this.text = "Convert to Background";
    // this.actor(() -> backgroundAct());
    // break;
    }
  }

  public void ungroup()
  {
    for (OCDPaintable node : this.tab.pager.focus.selectedNodes())
      if (node != null && node.isGroup())
      {
        node.asGroup().ungroup();
      }
    tab.update();
  }

  public void group(Rectangle3 box, String type)
  {
    if (box == null)
      box = this.tab.pager.interactor.bounds();

    OCDList list = tab.env().gui.sideDom.tree().selectedList();

    Log.debug(this, ".group - selected dom size=" + list.size());

    OCDPage page = this.tab.pager.page;
    OCDContent subContent;
    if (list.size() > 1)
      subContent = page.content().regroup(list, type);
    else
      subContent = page.content().regroup(box, type);

    Iterator<OCDPaintable> nodeIt = page.content().nodes().iterator();
    while (nodeIt.hasNext())
    {
      OCDPaintable node = nodeIt.next();
      if (node.isGroupContent() && node.asGroup().nodes().isEmpty())
        nodeIt.remove();
    }

    this.tab.pager.page.content().sortY();
    // src.pager.update();

    tab.update();
    tab.pleaseInteract(subContent);
  }

  // public void groupAct()
  // {
  // Rectangle3 box = src.selection();
  // String name = Base.x32.random8();
  // OCDPage page = env.page();
  // OCDGroup<OCDPaintable> group = page.createGroup("Graphics-" + page.number()
  // + "-" + name, box, name);
  // if (group.isPopulated())
  // page.content().add(group);
  // this.done(true);
  // }
  //
  // public void imageAct()
  // {
  // Rectangle3 box = src.selection();
  // String name = Base.x32.random8();
  // OCDPage page = env.page();
  // OCDGroup<OCDPaintable> group = page.createGroup("Graphics-" + page.number()
  // + "-" + name, box, name, 0.5, null);
  // if (group.isPopulated())
  // {
  // page.content().add(group);
  // group.rasterize(2, 4, false, box);
  // }
  // this.done(true);
  // }
  //
  // public void backgroundAct()
  // {
  // Rectangle3 box = src.selection();
  // String name = Base.x32.random8();
  // OCDPage page = env.page();
  // List3<OCDPaintable> texts = new List3<>();
  // OCDGroup<OCDPaintable> group = page.createGroup("Graphics-" + page.number()
  // + "-" + name, box, name, 0.5, texts);
  // if (group.isPopulated())
  // {
  // page.content().add(group);
  // group.rasterize(2, 4, true, box);
  // float zOrderMax = group.zOrderMax();
  // for (OCDPaintable node : texts)
  // {
  // if (node.isText())
  // node.asText().incZOrder(zOrderMax);
  // else if (node.isTextBlock())
  // {
  // for (OCDText text : node.asTextBlock().allTexts())
  // text.incZOrder(zOrderMax);
  // }
  // }
  // }
  // this.done(true);
  // }
  //
  public static void Trigger(FxRibbon tab, int method)
  {
    new GroupAct(tab, method).act();
  }

  public static void Populate(FxMenu menu, FxRibbon tab)
  {

    // this.popup.item(" Group Elements ", Iconic.Get(Iconic.OBJECT_GROUP, 20),
    // FxKeyboard.ctrl(KeyCode.G)
    if (tab.pager.hasInteractor())
    {

      menu.items(TextAct.GroupAsParagraph(tab), GroupAsContent(tab), new GroupAct(tab, UNGROUP));

    }
    menu.messageIfNoItem();
  }

  public static GroupAct GroupAsContent(FxRibbon tab)
  {
    return new GroupAct(tab, CONTENT);
  }

}
