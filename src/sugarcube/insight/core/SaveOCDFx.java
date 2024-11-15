package sugarcube.insight.core;

import sugarcube.common.interfaces.DoneListener;
import sugarcube.common.system.io.File3;
import sugarcube.common.ui.fx.task.FxWorker;
import sugarcube.common.ui.fx.task.Taskable;
import sugarcube.formats.ocd.writer.OCDWriter;
import sugarcube.formats.ocd.objects.OCDPageProcessor;
import sugarcube.formats.ocd.objects.OCDDocument;
import sugarcube.formats.ocd.objects.OCDPage;

public class SaveOCDFx extends FxFinalEnvironment implements Taskable<File3, Void>, DoneListener
{
    private OCDPageProcessor processor;
    private DoneListener listener;

    public SaveOCDFx(FxEnvironment env, File3 file, OCDPageProcessor processor, DoneListener listener)
    {
        super(env);
        this.processor = processor;
        this.listener = listener;
        new FxWorker(true, this, file);
    }

    @Override
    public Void taskWork(File3 file)
    {
        OCDDocument ocd = env.ocd();
        if (ocd == null)
            return null;
        OCDPage pageInMemory = env.page();
        OCDWriter writer = new OCDWriter(ocd, file)
        {
            @Override
            public void pageWritten(OCDPage page)
            {
                if (page != pageInMemory)
                    page.freeFromMemory();
            }
        };
        writer.progressor(env).processor(processor).write();
        return null;
    }

    @Override
    public void taskDone(Void done)
    {
        done();
        if (listener != null && listener != this)
            listener.done();
    }

    @Override
    public void done()
    {

    }

}
