package sugarcube.insight.ribbon.toolbox.actions;

import javafx.scene.input.KeyCode;
import sugarcube.common.system.log.Log;
import sugarcube.insight.core.FxRibbonAction;
import sugarcube.insight.ribbon.toolbox.ToolboxRibbon;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDGroup;
import sugarcube.formats.ocd.objects.OCDNode;

public class ItemAct extends FxRibbonAction<ToolboxRibbon>
{
  public ItemAct(ToolboxRibbon tab)
  {
    super(tab, "Toggle List Item");
   this.ctrlAccelerator(KeyCode.E);
  }

  @Override
  public void act()
  {
    for (OCDNode node : tab.pager().selected())
      if (OCD.isGroup(node))
      {
        Log.debug(this,  ".act - "+node);
        OCDGroup g = (OCDGroup) node;
        if (g.has(OCD.ITEM, OCD.NONE))
        {
          g.clear(OCD.ITEM);
          g.groupID=-1;
        } else
        {
          g.set(OCD.ITEM, OCD.NONE);
          g.clear(OCD.LIST);
          g.needID();
          g.groupID=-1;
        }
      }
//    src.update();
  }
}
