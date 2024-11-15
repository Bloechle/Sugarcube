package sugarcube.formats.epub.structure.otf;

import sugarcube.common.system.log.Log;
import sugarcube.common.system.io.File3;
import sugarcube.formats.ocd.objects.font.SVGFont;

import java.io.File;

public class OTF
{
  public static final String EXT = ".otf";
  public static boolean DEBUG = false;

  public static int RInt(double value)
  {
    return (int) Math.round(value);
  }

  public static int Unit(double value)
  {
    return RInt(value * HeadTable.UNITS);
  }

  public static int ZUnit(double value)
  {
    return RInt(value * 1024);
  }
  
  public static byte[] Bytes(SVGFont font)
  {
    return new OTFWriter(font).bytes();
  }

  public static void Write(SVGFont font, File file)
  {
    new OTFWriter(font).write(file);
  }  
  
  public static void main(String... args)
  {
//     File3 ttfFile = File3.userDesktop("trebucit.ttf");
//    
//     TrueTypeFont ttf = TrueTypeFont.parseFont(ttfFile.bytes());
//     sugarcube.formats.pdf.dexter.pdf.node.font.ttf.GlyfTable glyf =
//     (sugarcube.formats.pdf.dexter.pdf.node.font.ttf.GlyfTable) (ttf.getTable("glyf"));
//    
//     for (int i = 0; i < 256; i++)
//     glyf.getGlyph(i);

    File3 file = File3.desktop("StempelGaramond-Italic.svg");

    SVGFont font = SVGFont.load(file);

    Write(font, file.extense(EXT));
    Log.info(OTF.class, " - OTF file written: " + file.extense(EXT));
  }
    
}
