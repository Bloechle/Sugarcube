package sugarcube.formats.ocd;

import sugarcube.common.system.log.Log;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.IO;
import sugarcube.common.system.process.Arguments;
import sugarcube.formats.ocd.objects.*;

public class OCDTextBlockReaderExample
{
  public String path;
  public StringBuilder sb;
  public boolean detail = false;

  public OCDTextBlockReaderExample(String path)
  {
    this.path = path;
    this.sb = new StringBuilder();
  }

  public void log(String msg)
  {
    System.out.println(msg);
    sb.append(msg + "\n");
  }

  public void read()
  {
    File3 file = new File3(path);
    if (!file.exists() || !file.isExtension(".ocd"))
    {
      Log.warn(this, ".read - ocd file not found: " + path);
      return;
    }

    OCDDocument ocd = OCDDocument.open(file);
    int pageNb = 0;
    for (OCDPage page : ocd)
    {
      pageNb++;
      page.ensureInMemory();

      // contains OCD graphics such as images, paths and text
      OCDPageContent content = page.content();

      int blockNb = 0;
      for (OCDTextBlock block : content.blocks())
      {
        blockNb++;

        int lineNb = 0;

        if (detail)
          for (OCDTextLine line : block)
          {
            lineNb++;
            // an OCDText is a run of text having identical properties:
            // fontname,
            // fontsize, color, etc.
            int runNb = 0;
            for (OCDText text : line)
            {
              runNb++;
              log("Page " + pageNb + ", Block " + blockNb + ", Line " + lineNb + ", Run " + runNb);
              // uniString does its best to get meaningful unicodes :-)
              log("Text[" + text.glyphString() + "]");
              log("Font[" + text.fontname() + ", " + text.fontsize() + "]");
              log("Bounds" + text.bounds());
              log("");
            }
          }
        else
        {
          OCDText text = block.firstText();
          log("=============================================================================\nPage " + pageNb + ", Paragraph " + blockNb + ", "+ text.fontname() + " " + text.scaledFontsize()+", Bounds" + block.bounds().round()+"\n"); //
          log(block.stringValue(false));
        }
      }
      page.freeFromMemory();
    }

    ocd.close();
  }

  public static void main(String... args)
  {
    Arguments arguments = new Arguments(args);
    File3 file = arguments.firstFile(".ocd");
    if (file == null)
      file = File3.desktop("PhDThesis-GuyGenilloud.ocd");
    Log.debug(OCDTextBlockReaderExample.class, ".main - file: " + file.path());

    OCDTextBlockReaderExample reader = new OCDTextBlockReaderExample(file.path());
    reader.read();

    IO.WriteText(file.extense("txt"), reader.sb.toString());
  }

}
