package sugarcube.insight.side.toc;

import javafx.scene.control.SelectionMode;
import sugarcube.common.system.log.Log;
import sugarcube.common.ui.fx.containers.FxScrollPane;
import sugarcube.common.ui.fx.event.FxContext;
import sugarcube.common.ui.fx.event.FxHandle;
import sugarcube.common.ui.fx.menus.FxPopup;
import sugarcube.insight.core.FxEnvironment;
import sugarcube.insight.side.InsightSide;
import sugarcube.formats.ocd.objects.nav.OCDNavItem;
import sugarcube.formats.ocd.objects.nav.OCDNavigation;
import sugarcube.formats.pdf.resources.icons.Icon;

public class NavSide extends InsightSide
{
  private FxNavTree tree;
  private FxPopup popup;
  private FxScrollPane scroll;
  private OCDNavItem selected;

  public NavSide(FxEnvironment env)
  {
    super(env, "Navigation", null);
    this.scroll = new FxScrollPane().fit(true,  true);;
    this.setContent(scroll);   
  }

  public OCDNavigation nav()
  {
    return env.ocd.nav();
  }

  public OCDNavItem navRoot()
  {
    return nav().navRoot();
  }

  public void update()
  {
    try
    {
      Log.debug(this, ".update ");
      if (tree != null)
        tree.persistExpansion();

      tree = new FxNavTree(this, navRoot());      
      tree.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
        if (val != null)
        {
          OCDNavItem item = val.getValue();
          if (item.link != null)
            env.updatePage(item.link);
        }
      });

      scroll.setContent(tree);
      tree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

      FxHandle handle = FxHandle.Get(tree);
      handle.popup(ctx -> popup(ctx));

      tree.select(selected);
    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public NavSide clear()
  {
    return this;
  }

  public void generatePagesTOC()
  {
    nav().populatePagesTOC();
    this.update();
  }

  public void generateStylesTOC()
  {
    nav().populateStylesTOC();
    this.update();
  }
  
  public void generatePageList()
  {
    nav().populatePageList();
    this.update();
  }
  
  public void delete()
  {
    for(OCDNavItem item: tree.selected())
    {
      item.delete();
    }    
    this.update();
  }


  public void group(OCDNavItem parent)
  {
    OCDNavItem[] items = tree.selected();
    if (items.length == 0)
      return;
    OCDNavItem.Group(parent == null ? items[0].parentItem() : parent, items);
    this.update();
  }

  public void popup(FxContext ctx)
  {
    if (ctx.isConsumed())
      return;

    if (popup == null)
      this.popup = new FxPopup();

    this.popup.clear();

    int selected = tree.getSelectionModel().getSelectedItems().size();

    if (selected > 0)
    {
      this.popup.item(" Group Items ", Icon.Get(Icon.GROUP, 16)).act(e -> group(null));
    }

    this.popup.item(" Delete selected items", Icon.Get(Icon.REMOVE, 16)).act(e -> delete());
    this.popup.sep();
    this.popup.item(" Generate TOC from pages", Icon.Get(Icon.BARS, 16)).act(e -> generatePagesTOC());
    this.popup.item(" Generate TOC from styles ", Icon.Get(Icon.BARS, 16)).act(e -> generateStylesTOC());
    this.popup.sep();
    this.popup.item(" Generate PageList", Icon.Get(Icon.BARS, 16)).act(e -> generatePageList());    

    this.popup.show(env.gui.board.canvas, ctx.screenXY());
  }

}
