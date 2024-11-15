package sugarcube.formats.pdf.reader.pdf.codec;

import sugarcube.common.system.log.Log;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;

import java.io.ByteArrayOutputStream;
import java.util.zip.Inflater;

public class FlateDecode
{
  public static byte[] decode(byte[] stream, PDFDictionary map) throws Exception
  {
    int bufSize = 512000;
    ByteArrayOutputStream bos = null;
    Exception failed = new Exception();
    while (failed != null)
    { // sometimes partly valid so loop to try this
      Inflater inf = new Inflater();
      inf.setInput(stream);
      int size = stream.length;
      bos = new ByteArrayOutputStream(size);
      if (size < bufSize)
        bufSize = size;
      byte[] buf = new byte[bufSize];
      // int debug = 20;
      int count;
      long total = 0;
      try
      {
        while (!inf.finished())
        {
          // Log.debug(FlateDecode.class, ".decode - buf=" + bufSize);
          count = inf.inflate(buf);
          total += count;
          bos.write(buf, 0, count);
          if (inf.getRemaining() == 0)
            break;
        }

        failed = null;
      } catch (Exception e)
      {
        Log.debug(FlateDecode.class, ".decode - " + e.getMessage() + ", ref=" + map.reference() + ", stream.length=" + size + ", read=" + total);
        //e.printStackTrace();
        failed = e;
        if (stream.length > 10)
        {
          byte[] newData = new byte[stream.length - 1];
          System.arraycopy(stream, 0, newData, 0, stream.length - 1);
          stream = newData;
        } else
          failed = null;
      }
    }

    if (failed != null)
      Log.warn(FlateDecode.class, " - exception while applying deflate: " + failed.getMessage());

//     Log.debug(FlateDecode.class, ".decode - decoded length=" + bos.toByteArray().length+", predictor="+Predictor.getPredictor(map));
    return Predictor.getPredictor(map).unpredict(bos.toByteArray());

    // Inflater inf = new Inflater();
    // inf.setInput(stream);
    // ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    // byte[] buffer = new byte[stream.length];
    // int read = 0;
    // int counter = 0;
    // try
    // {
    // while (!inf.finished())
    // {
    // counter++;
    // read = inf.inflate(buffer);
    // if (read <= 0)
    // if (inf.needsDictionary())
    // throw new
    // Exception("Don't know how to ask for a dictionary in FlateDecode");
    // else
    // //XED.LOG.warn(FlateDecode.class,
    // ".decode - end of Inflater not found, end of stream used instead...");
    // break;
    // bytes.write(buffer, 0, read);
    // }
    // }
    // catch (Exception e)
    // {
    // Zen.LOG.warn(FlateDecode.class,
    // " - inflate exception: counter="+counter);
    // e.printStackTrace();
    // }
    //
    // //XED.LOG.info(FlateDecode.class,
    // ".decode - predictor: "+Predictor.getPredictor(stream).algorithm);
    // return Predictor.getPredictor(map).unpredict(bytes.toByteArray());
  }
}
