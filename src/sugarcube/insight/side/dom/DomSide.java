package sugarcube.insight.side.dom;

import javafx.scene.control.SelectionMode;
import sugarcube.common.system.log.Log;
import sugarcube.common.ui.fx.containers.FxScrollPane;
import sugarcube.common.ui.fx.event.FxContext;
import sugarcube.common.ui.fx.event.FxHandle;
import sugarcube.common.ui.fx.menus.FxPopup;
import sugarcube.insight.core.FxEnvironment;
import sugarcube.insight.side.InsightSide;
import sugarcube.formats.ocd.objects.*;
import sugarcube.formats.ocd.objects.nav.OCDNavItem;
import sugarcube.formats.ocd.objects.nav.OCDNavigation;
import sugarcube.formats.pdf.resources.icons.Icon;

public class DomSide extends InsightSide
{
  private FxDomTree tree;
  private FxPopup popup;
  private FxScrollPane scroll;
  private OCDNavItem selected;

  public DomSide(FxEnvironment env)
  {
    super(env, "Page DOM", null);
    this.scroll = new FxScrollPane().fit(true, true);
    this.setContent(scroll);
  }

  public OCDNavigation nav()
  {
    return env.ocd.nav();
  }

  public FxDomTree tree()
  {
    return this.tree;
  }

  public void update()
  {
    if (env.page != null)
      try
      {
        Log.debug(this, ".update ");
        if (tree != null)
          tree.persistExpansion();

        tree = new FxDomTree(this, env.page);
        tree.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
          if (val != null)
          {
            OCDNode node = val.getValue();

            Log.debug(this, ".update - item selected: " + node.tag);
            if (node instanceof OCDPaintable)
              env.ribbon().pager().pleaseInteract((OCDPaintable) node);
            // if (node.link != null)
            // env.updatePage(node);
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

  public void onMouseOver(FxDomCell cell, boolean isOver)
  {
    env.ribbon().pager.focus.updateOver(cell != null && isOver ? cell.nodeFx() : null);
  }

  public DomSide clear()
  {

    return this;
  }

  // public void flatten()
  // {
  // nav().populatePagesTOC();
  // this.update();
  // }
  //
  // public void html()
  // {
  // nav().populateStylesTOC();
  // this.update();
  // }

  public void delete()
  {
    for (OCDNode node : tree.selected())
    {
      node.delete();
    }
    ribbon().update();
  }

  public void group(OCDNode groupingNode)
  {
    OCDNode[] items = tree.selected();
    if (groupingNode == null || items.length == 0)
      return;

    OCDPage page = groupingNode.modifyPage();
    
    Log.debug(this,  ".group - "+groupingNode.tag);

    if (groupingNode instanceof OCDContent)
    {
      OCDGroup<OCDPaintable> group = (OCDGroup<OCDPaintable>) groupingNode;
      group.needID("sub");
      for (OCDNode node : items)
      {
        if (node.isPaintable() && node != groupingNode)
        {
          
          
          node.delete(false);
          group.add((OCDPaintable) node);
        }
      }
    } else
    {
      OCDContent content = page.content().newContent(groupingNode.index());
      content.needID("sub");
      for (OCDNode node : items)
      {
        if (groupingNode.isPaintable())
        {
          groupingNode.delete(false);
          content.add((OCDPaintable) groupingNode);
        }

        if (node.isPaintable() && node != groupingNode)
        {
          node.delete(false);
          content.add((OCDPaintable) node);
        }
      }

    }
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

    this.popup.item(" Delete node ", Icon.Get(Icon.REMOVE, 16)).act(e -> delete());

    this.popup.show(env.gui.board.canvas, ctx.screenXY());
  }

}
