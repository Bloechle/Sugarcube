package sugarcube.insight.ribbon.file;

import sugarcube.common.system.io.File3;
import sugarcube.common.system.process.Progression;
import sugarcube.common.ui.fx.task.FxWorker;
import sugarcube.common.ui.fx.task.Taskable;
import sugarcube.insight.core.FxEnvironment;
import sugarcube.insight.core.FxFinalEnvironment;
import sugarcube.formats.pdf.writer.PDFParams;
import sugarcube.formats.pdf.writer.PDFWriter;

public class PDFFx extends FxFinalEnvironment implements Taskable<Void, Void>
{
  private boolean processing = false;
  private Progression.Listener[] listeners;
  private PDFParams params = new PDFParams();

  public PDFFx(FxEnvironment env, Progression.Listener... listeners)
  {
    super(env);
    this.listeners = listeners;
  }

  public PDFFx setParams(PDFParams params)
  {
    this.params = params;
    return this;
  }

  public PDFFx start()
  {
    new FxWorker(true, this, null);
    return this;
  }

  @Override
  public Void taskWork(Void work)
  {
    this.processing = true;
    File3 pdfFile = File3.Get(File3.Extense(env.ocd.filePath(), ".pdf"));
    if(pdfFile.exists())
      pdfFile = pdfFile.postfix("_ocd");
      
    PDFWriter pdf = new PDFWriter(env.ocd, pdfFile);
    pdf.setParams(this.params);
    pdf.progression.addListeners(env);
    pdf.progression.addListeners(listeners);
    pdf.write();
    return null;
  }

  @Override
  public void taskDone(Void done)
  {
    this.processing = false;
  }

}
