package sugarcube.formats.epub.replica;

import sugarcube.common.system.util.Sys;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.IO;
import sugarcube.common.system.process.Progression;
import sugarcube.common.ui.fx.task.FxWorker;
import sugarcube.common.ui.fx.task.Taskable;
import sugarcube.formats.epub.EPubProps;
import sugarcube.insight.core.FxEnvironment;
import sugarcube.insight.core.FxFinalEnvironment;
import sugarcube.formats.ocd.objects.OCDPageProcessor;

public class ReplicaFx extends FxFinalEnvironment implements Taskable<Void, Void>
{
  private File3 file;
  private Replica replica;
  private boolean doUnzip;

  public ReplicaFx(FxEnvironment env, EPubProps props, boolean doUnzip, OCDPageProcessor processor, Progression.Listener... listeners)
  {
    super(env);
    this.doUnzip = doUnzip;
    this.file = env.ocd.file();
    this.replica = new Replica(props);
    replica.processor(processor);
    replica.outProgression.addListeners(env);
    replica.outProgression.addListeners(listeners);
    // Log.debug(this, ".process - "+file);
    new FxWorker<Void, Void>(true, this, Sys.Void());
  }

  @Override
  public Void taskWork(Void work)
  {
    File3 epub = file.extense(".epub");
    epub.delete();
    File3 folder = file.extense("");
    folder.deleteDirectory();
    replica.convert(file, epub);
    if (doUnzip)
      IO.Unzip(epub, folder);
    return null;
  }

  @Override
  public void taskDone(Void done)
  {

  }

}
