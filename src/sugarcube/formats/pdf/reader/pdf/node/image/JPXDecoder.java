package sugarcube.formats.pdf.reader.pdf.node.image;

import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.Image3;
import jj2000.j2k.decoder.Decoder;

public class JPXDecoder
{
  public static Image3 Decode(PDFImage image)
  {
    Decoder decoder = new Decoder();
    Image3 img = decoder.decode(image);
    if (img == null)
    {
      Log.debug(JPXDecoder.class, ".decode - JP2000 image is empty");
      return PDFImage.errorImage(image.width, image.height);      
    }
    return img;
  }
}
