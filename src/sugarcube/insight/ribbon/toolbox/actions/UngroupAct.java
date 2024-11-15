package sugarcube.insight.ribbon.toolbox.actions;

import sugarcube.insight.core.FxRibbon;
import sugarcube.insight.core.FxRibbonAction;
import sugarcube.formats.ocd.objects.OCDGroup;
import sugarcube.formats.ocd.objects.OCDPage;
import sugarcube.formats.pdf.resources.icons.Icon;


public class UngroupAct extends FxRibbonAction<FxRibbon>
{
  private OCDGroup group;

  public UngroupAct(FxRibbon tab, OCDGroup group)
  {
    super(tab, "Ungroup", Icon.Get(Icon.REMOVE, 16));
    this.group = group;    
  }

  @Override
  public void act()
  {
    OCDPage page = env.page();
    page.removeGroup(group);    
//    src.selected().clear();
    this.done(true);
  }
}
