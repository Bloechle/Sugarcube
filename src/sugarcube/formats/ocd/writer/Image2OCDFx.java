package sugarcube.formats.ocd.writer;

import sugarcube.common.system.util.Sys;
import sugarcube.common.system.io.File3;
import sugarcube.common.ui.fx.task.FxWorker;
import sugarcube.common.ui.fx.task.Taskable;
import sugarcube.insight.core.FxEnvironment;
import sugarcube.insight.core.FxFinalEnvironment;
import sugarcube.insight.interfaces.FxFileProcessor;


public class Image2OCDFx extends FxFinalEnvironment implements Taskable<Void, Void>
{
    public static FxFileProcessor PROCESSOR = (env, file, files) ->
    {
        if (file.isDirectory() || file.isExtension("png", "jpg", "jpeg", "zip"))
        {
            new Image2OCDFx(env, file, files);
            return true;
        }
        return false;
    };

    private File3 inFile, outFile;
    private File3[] allFiles;
    private Image2OCDWriter ocdWriter = new Image2OCDWriter();

    public Image2OCDFx(FxEnvironment env, File3 file, File3... allFiles)
    {
        super(env);
        env.closeOCD(false);
        inFile = file;
        this.allFiles = allFiles;
        outFile = file.extense(".ocd");
        if (env.insight.config.createTmpOCD)
            outFile = File3.TempFile(outFile.name());
        ocdWriter.addProgressionListener(env);
        new FxWorker<>(true, this, Sys.Void());
    }

    @Override
    public Void taskWork(Void work)
    {
        outFile = ocdWriter.convert(inFile, allFiles, outFile);
        Sys.Sleep(100);
        return null;
    }

    @Override
    public void taskDone(Void done)
    {
        if (outFile.exists() && outFile.isExtension("ocd"))
            env.load(outFile);
    }

}
