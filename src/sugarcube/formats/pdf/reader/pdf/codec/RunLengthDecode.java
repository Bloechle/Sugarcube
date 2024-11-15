package sugarcube.formats.pdf.reader.pdf.codec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class RunLengthDecode
{
  private static final int RUN_LENGTH_EOD = 128;

  public RunLengthDecode()
  {

  }

  /**
   * This is a filter for the RunLength Decoder.
   *
   * From the PDF Reference
   * <pre>
   * The RunLengthDecode filter decodes data that has been encoded in a simple
   * byte-oriented format based on run length. The encoded data is a sequence of
   * runs, where each run consists of a length byte followed by 1 to 128 bytes of data. If
   * the length byte is in the range 0 to 127, the following length + 1 (1 to 128) bytes
   * are copied literally during decompression. If length is in the range 129 to 255, the
   * following single byte is to be copied 257 ? length (2 to 128) times during decompression.
   * A length value of 128 denotes EOD.
   *
   * The compression achieved by run-length encoding depends on the input data. In
   * the best case (all zeros), a compression of approximately 64:1 is achieved for long
   * files. The worst case (the hexadecimal sequence 00 alternating with FF) results in
   * an expansion of 127:128.
   * </pre>
   */  
  public static byte[] decode(byte[] data) throws Exception
  {
    int dupAmount = -1;

    ByteArrayInputStream in = new ByteArrayInputStream(data);
    ByteArrayOutputStream out = new ByteArrayOutputStream(data.length);

    byte[] buffer = new byte[128];
    while ((dupAmount = in.read()) != -1 && dupAmount != RUN_LENGTH_EOD)
    {
      if (dupAmount <= 127)
      {
        int amountToCopy = dupAmount + 1;
        int compressedRead = 0;
        while (amountToCopy > 0)
        {
          compressedRead = in.read(buffer, 0, amountToCopy);
          out.write(buffer, 0, compressedRead);
          amountToCopy -= compressedRead;
        }
      } else
      {
        int dupByte = in.read();
        for (int i = 0; i < 257 - dupAmount; i++)
        {
          out.write(dupByte);
        }
      }
    }
    return out.toByteArray();
  }

}
