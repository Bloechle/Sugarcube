package sugarcube.formats.pdf.reader;


import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.*;

public class MyOCDReader
{
  public static void main(String... args)
  {
    String pdfFile = "C:/Users/Jean-Luc/Desktop/FactureEDF.pdf";
    String ocdFile = "C:/Users/Jean-Luc/Desktop/FactureEDF.ocd";

    Dexter pdf2ocd = new Dexter(OCD.canonizerProps);
    OCDDocument ocd = pdf2ocd.convert(pdfFile, ocdFile);
    
    for(OCDPage page: ocd)
    {
      log("#### PAGE "+page.number()+" ################################################################");
      page.ensureInMemory();
            
      OCDPageContent content=page.content();//contains OCD graphics such as images, paths and text
      for(OCDTextBlock block: content.blocks())
      {
        log("============================================================================");
        for(OCDTextLine line: block)
        {
          for(OCDText text: line)//an OCDText is a run of text having identical properties: fontname, fontsize, color, etc.
          {
            log("Text["+text.glyphString()+"]");//uniString does its best to get meaningful unicodes :-)
            log("Font["+text.fontname()+"]");
            log("Size["+text.fontsize()+"]");
            log("Bounds"+text.bounds());
            log("");
          }
        }
      }            
      page.freeFromMemory();
    }
    
    ocd.close();
  }
  
  public static void log(String msg) //for concision purpose
  {
    System.out.println(msg);
  }
}
