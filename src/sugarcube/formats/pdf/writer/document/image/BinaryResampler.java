package sugarcube.formats.pdf.writer.document.image;

import sugarcube.common.graphics.Image3;
import sugarcube.common.system.io.File3;

public class BinaryResampler
{
  public static void main(String... args)
  {
    
    Image3 img = Image3.Read(File3.desktop("RO.png"));
    
    Image3 sup = img.scale(2);
    sup.write(File3.desktop("ROut.png"));
    
    img.binary(true,  240).write(File3.desktop("RObin.png"));
    
    sup.binary(true,  240).write(File3.desktop("ROutbin.png"));
  }
}
