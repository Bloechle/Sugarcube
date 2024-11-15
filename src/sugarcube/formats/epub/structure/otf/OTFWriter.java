package sugarcube.formats.epub.structure.otf;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.List3;
import sugarcube.common.system.io.IO;
import sugarcube.formats.ocd.objects.font.SVGFont;

import java.io.*;

public class OTFWriter
{
  public DirectoryTable dirTable;
  public SVGFont font;
  public DataOutputStream os;

  public OTFWriter(SVGFont font)
  {
    this.dirTable = new DirectoryTable(font);
    // http://scripts.sil.org/cms/scripts/page.php?item_id=IWS-AppendixC
    // http://www.microsoft.com/typography/otspec/otff.htm#otttables
    // http://www.microsoft.com/typography/otspec/recom.htm
  }

  public void write(File file)
  {
    try
    {
      this.write(new FileOutputStream(file));
    } catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    this.close();
  }

  public void write(OutputStream os)
  {
    this.os = os instanceof DataOutputStream ? (DataOutputStream) os : new DataOutputStream(os);
    this.dirTable.process();
    List3<Table> tables = dirTable.tables();

    if (dirTable.fontFileSize() == 0)
    {
      Log.debug(this, ".open - font size is zero");
      return;
    }

    for (Table table : tables)
    {
      FontData fd = table.fontData();
      try
      {
        os.write(fd.data());
      } catch (IOException e)
      {
        e.printStackTrace();
      }

    }
  }

  public byte[] bytes()
  {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    write(os);
    close();
    return os.toByteArray();
  }

  public void close()
  {
    IO.Close(os);
  }
}
