package sugarcube.formats.pdf.reader;

import sugarcube.common.system.util.Sys;
import sugarcube.common.data.Zen;
import sugarcube.common.system.io.File3;
import sugarcube.common.ui.fx.task.FxWorker;
import sugarcube.common.ui.fx.task.Taskable;
import sugarcube.insight.core.FxEnvironment;
import sugarcube.insight.core.FxFinalEnvironment;
import sugarcube.insight.interfaces.FxFileProcessor;

public class DexterFx extends FxFinalEnvironment implements Taskable<Void,Void>
{  
  public static FxFileProcessor PROCESSOR = (env, file, files) ->
  {
    if (file.isExtension("pdf"))
    {
      new DexterFx(env, file, env.insight.config.debugMode);
      return true;
    }
    return false;
  };
  
  private File3 file;  
  private Dexter dexter = new Dexter();

  public DexterFx(FxEnvironment env, File3 file, boolean debugMode)
  {
    super(env);    
    this.file = file;
    this.dexter.debugMode = debugMode; 
    dexter.ocdProgression.addListeners(env); 
//    Log.debug(this,  ".process - "+file);
    new FxWorker<Void,Void>(true, this, Sys.Void());
  }

  @Override
  public Void taskWork(Void work)
  {
    dexter.convert(file, file=file.extense(".ocd"));        
    Zen.sleep(200);
    return null;
  }

  @Override
  public void taskDone(Void done)
  {
    if (file.isExtension("ocd"))
    {
//      dexter.ozModel.reload=false;
//      env.addons.put(OzModel.FILENAME, converter.ozModel);
      env.load(file);
    }
  }

}
