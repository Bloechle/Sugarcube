package sugarcube.insight.ribbon.toolbox.actions;

import sugarcube.insight.core.FxRibbon;
import sugarcube.insight.core.FxRibbonAction;
import sugarcube.insight.render.FxOCDNode;
import sugarcube.insight.ribbon.toolbox.ToolboxRibbon;
import sugarcube.formats.pdf.resources.icons.Icon;

public class DeleteAct extends FxRibbonAction<ToolboxRibbon>
{
  public DeleteAct(ToolboxRibbon tab, int mode)
  {
    super(tab, "Delete @Del", Icon.PARAGRAPH.get(tab.iconSize));
  }

  @Override
  public void act()
  {
    Delete(this.tab);
  }

  public static void Delete(FxRibbon tab)
  {
    if (tab.pager.focus.selected.isPopulated())
    {
      for (FxOCDNode node : tab.pager.focus.selected)
        node.delete();
      tab.pager.interactor.reset();
      tab.update();
    } 

  }

}