package sugarcube.insight.core;

import sugarcube.common.system.Prefs;
import sugarcube.common.system.log.Log;
import sugarcube.common.interfaces.DoneListener;
import sugarcube.common.system.io.File3;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.task.FxWorker;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDDocument;

public class FxDocumentLoader extends FxWorker<File3, OCDDocument>
{
  private FxEnvironment env;
  private int pageNb;

  public FxDocumentLoader(FxEnvironment env, File3 file, int pageNb, DoneListener listener)
  {
    super(file);
    this.env = env;
    this.pageNb = pageNb < 1 ? 1 : pageNb;
    doneListener(listener);
  }

  @Override
  public OCDDocument taskWork(File3 file)
  {
    if (file.isExtension(OCD.EXT))
    {
      OCDDocument newOcd = new OCDDocument();
      newOcd.load(file);

      Prefs.screenDpi = newOcd.dpi();
      newOcd.viewProps.scale = env.gui.scale();

      // if (model.isPopulated())
      // Log.info(this, ".process - dolores-model loaded with " +
      // model.nbOfSamples() + " samples");
      return newOcd;
    } else
      Log.warn(this, ".taskWork - file format not supported: " + file.getName());
    return null;
  }

  @Override
  public void taskDone(OCDDocument ocd)
  {
    // OCD loading done
    try
    {
      if (env.ocd != null)
        env.ocd.close();

      if (ocd == null)
        return;
      env.clearCaches();
      env.ocd = ocd;
      env.updateTitle(ocd.fileName());
      env.insight.prefs.putLastFile(ocd.filePath(), env.pageName(), -1);

      for (FxRibbon tab : env.ribbons())
        tab.ocdLoaded();
      env.updatePage(ocd.pageHandler.getPage(pageNb));
      Fx.Run(() -> env.updateLeftSide());
    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }

}
