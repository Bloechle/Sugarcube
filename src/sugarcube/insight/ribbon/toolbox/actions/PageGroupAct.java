package sugarcube.insight.ribbon.toolbox.actions;

import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.system.process.Progressor;
import sugarcube.common.ui.fx.task.FxWorker;
import sugarcube.common.ui.fx.task.Taskable;
import sugarcube.insight.core.FxRibbonAction;
import sugarcube.insight.ribbon.toolbox.ToolboxRibbon;
import sugarcube.formats.ocd.objects.OCDPage;
import sugarcube.formats.ocd.objects.handlers.OCDPageHandler;

public class PageGroupAct extends FxRibbonAction<ToolboxRibbon> implements Taskable<OCDPage[], Void>
{
  private String type;
  private boolean even;
  private boolean odd;

  public PageGroupAct(ToolboxRibbon tab, boolean even, boolean odd, String text, String type)
  {
    super(tab, text, null);
    this.type = type;
    this.even = even;
    this.odd = odd;
    // this.ctrlAccelerator(KeyCode.T);
  }

  public OCDPage[] pages()
  {
    OCDPageHandler o = this.tab.ocd().pageHandler;
    return even && odd ? o.array() : (odd ? o.odd() : (even ? o.even() : new OCDPage[]
    { this.tab.page() }));
  }

  @Override
  public void act()
  {
    new FxWorker<OCDPage[], Void>(true, this, pages());
  }

  @Override
  public Void taskWork(OCDPage[] pages)
  {
    Rectangle3 box = this.tab.selection();
    Progressor progressor = new Progressor("Processing page", "page", 0);

    env.progress(progressor);

    try
    {
      for (int i = 0; i < pages.length; i++)
      {
        OCDPage page = pages[i];  
        page.content().regroup(box, type);
        env.progress(progressor.update((i + 1) / (float) pages.length, "Processing page " + (i + 1) + "/" + pages.length));
      }
    } catch (Exception e)
    {
      e.printStackTrace();
    }

    env.progress(progressor.complete("OCD Pages Processed"));
    return null;
  }

  @Override
  public void taskDone(Void done)
  {
    this.resetInteractor();    
    this.tab.update();
    env.progress(null);
  }

}
