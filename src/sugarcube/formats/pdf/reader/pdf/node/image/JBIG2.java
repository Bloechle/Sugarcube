package sugarcube.formats.pdf.reader.pdf.node.image;

import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.JBIG2Decoder;
import sugarcube.formats.pdf.reader.pdf.object.PDFStream;

import java.awt.image.BufferedImage;

public class JBIG2
{
  public static BufferedImage Decode(PDFImage image) throws Exception
  {
    JBIG2Decoder decoder = new JBIG2Decoder();
    if (image.hasDecodeParam("JBIG2Globals"))
    {
      PDFStream global = image.decodeParms().get("JBIG2Globals").toPDFStream();
      decoder.setGlobalData(global.byteValues());
    }
    decoder.decodeJBIG2(image.stream().byteValues());
    return decoder.getPageAsJBIG2Bitmap(0).getBufferedImage();
  }
}
