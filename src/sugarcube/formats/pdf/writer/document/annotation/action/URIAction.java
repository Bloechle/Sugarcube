package sugarcube.formats.pdf.writer.document.annotation.action;

import sugarcube.common.system.log.Log;
import sugarcube.formats.pdf.writer.PDFWriter;
import sugarcube.formats.pdf.writer.core.object.DictionaryObject;
import sugarcube.formats.pdf.writer.core.writer.Writer;

public class URIAction extends DictionaryObject
{
  private String uri;

  public URIAction(PDFWriter environment, String uri)
  {
    super(environment);
    this.uri = uri;
    try
    {
      write();
    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  @Override
  public void addDictionaryEntries()
  {
    addDictionaryEntry("S", "URI", Writer.NAME);
    try
    {
      addDictionaryEntry("URI", new String(uri.getBytes(), 0, uri.length(), "ASCII"), Writer.HEXADECIMAL);
    } catch (Exception e)
    {
      Log.info(this, ".addDictionaryEntries - unable to add link address: " + uri + "'");
    }
  }
}
