
package sugarcube.formats.pdf.reader.pdf.node.font;

import sugarcube.common.data.collections.Unicodes;
import sugarcube.formats.pdf.reader.pdf.object.PDFEnvironment;
import sugarcube.formats.pdf.reader.pdf.object.PDFObject;
import sugarcube.formats.pdf.reader.pdf.object.PDFStream;
import sugarcube.formats.pdf.reader.pdf.object.StreamReader;
import sugarcube.formats.pdf.reader.pdf.util.Mapper;

import java.io.InputStream;
import java.nio.charset.Charset;

//toUnicode map
public class CMapReader
{
  private static int cmapSrc(PDFObject obj)
  {
    return obj.toPDFString().hexa();
  }

  private static Unicodes InternalCmapDst(PDFObject obj)
  {
    return new Unicodes(Charset.forName("UTF-16BE").decode(obj.toPDFString().byteBuffer()).array());
  }

  private static Unicodes ResourceCmapDst(PDFObject obj)
  {
    return new Unicodes(obj.toPDFNumber().intValue());
  }

  public static void ReadInternalCMap(PDFStream cmapStream, Mapper<Integer, Unicodes> cmap)
  {
    if (cmapStream.isValid())
    {
      StreamReader reader = new StreamReader(cmapStream);
      String word;
      while ((word = reader.token()) != null)
        if (word.equals("beginbfchar"))
          while (!(word = reader.token()).equals("endbfchar"))
          {
            int srcCode = cmapSrc(cmapStream.parsePDFObject(word, reader));
            Unicodes dstCode = InternalCmapDst(cmapStream.parsePDFObject(reader.token(), reader));
            cmap.put(srcCode, dstCode.containsCode(Unicodes.ASCII_SP) ? new Unicodes(Unicodes.ASCII_SP) : dstCode);
          }
        else if (word.equals("beginbfrange"))
          while (!(word = reader.token()).equals("endbfrange"))
          {
            int srcCode1 = cmapSrc(cmapStream.parsePDFObject(word, reader).toPDFString());
            int srcCode2 = cmapSrc(cmapStream.parsePDFObject(reader.token(), reader).toPDFString());

            PDFObject dst = cmapStream.parsePDFObject(reader.token(), reader);
            if (dst.type == PDFObject.Type.Array)
            {
              PDFObject[] dstCodes = dst.toPDFArray().array();
              for (int i = 0; srcCode1 <= srcCode2; srcCode1++, i++)
                cmap.put(srcCode1, InternalCmapDst(dstCodes[i]));
            }
            else
            {
              Unicodes dstCode = InternalCmapDst(dst);
              while (srcCode1 <= srcCode2)
              {
                cmap.put(srcCode1++, dstCode);
                dstCode = dstCode.increment(1);
              }
            }
          }

    }
  }

  public static void ReadResourceCMap(InputStream cmapStream, Mapper<Integer, Unicodes> cmap)
  {
    PDFEnvironment env = new PDFEnvironment();
    StreamReader reader = new StreamReader(cmapStream);
    String word;
    while ((word = reader.token()) != null)
      if (word.equals("begincidchar"))
        while (!(word = reader.token()).equals("endcidchar"))
        {
          int srcCode = cmapSrc(env.parsePDFObject(word, reader));
          Unicodes dstCode = ResourceCmapDst(env.parsePDFObject(reader.token(), reader));
          cmap.put(srcCode, dstCode);
        }
      else if (word.equals("begincidrange"))
        while (!(word = reader.token()).equals("endcidrange"))
        {
          int srcCode1 = cmapSrc(env.parsePDFObject(word, reader).toPDFString());
          int srcCode2 = cmapSrc(env.parsePDFObject(reader.token(), reader).toPDFString());

          PDFObject dst = env.parsePDFObject(reader.token(), reader);
          if (dst.type == PDFObject.Type.Array)
          {
            PDFObject[] dstCodes = dst.toPDFArray().array();
            for (int i = 0; srcCode1 <= srcCode2; srcCode1++, i++)
              cmap.put(srcCode1, ResourceCmapDst(dstCodes[i]));
          }
          else
          {
            Unicodes dstCode = ResourceCmapDst(dst);
            while (srcCode1 <= srcCode2)
            {
              cmap.put(srcCode1++, dstCode);
              dstCode = dstCode.increment(1);
            }
          }
        }
  }
}
