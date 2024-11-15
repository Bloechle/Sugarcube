package sugarcube.formats.ocd;

import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.IO;
import sugarcube.formats.ocd.objects.*;

public class OCDTextRunReaderExample
{
  public static final String RULER = "====================================================================================================";

  public static void main(String... args)
  {
    File3 file = new File3("C:/Users/Jean-Luc/Desktop/Test.ocd");

    StringBuilder sb = new StringBuilder();
    OCDDocument ocd = OCDDocument.open(file);
    for (OCDPage page : ocd)
    {
      OCDPageContent content = page.content();
      int blockNb = 0;
      for (OCDTextBlock block : content.blocks())
      {
        OCDText text = block.firstText();
        sb.append(RULER + "\nPage " + page.number() + "-" + ++blockNb + ", " + text.fontname() + " " + text.scaledFontsize() + ", Bounds"
            + block.bounds().round() + "\n\n");
        sb.append(block.stringValue(false) + "\n");
      }
      page.freeFromMemory();
    }

    ocd.close();
    IO.WriteText(file.extense("txt"), sb.toString());
  }

}
