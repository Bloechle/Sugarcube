package sugarcube.formats.pdf.reader.pdf.object;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.List3;
import sugarcube.formats.pdf.reader.pdf.codec.Codec;
import sugarcube.formats.pdf.reader.pdf.encryption.PDFCipher;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * PDF Reference says: All streams must be indirect objects (see Section 3.2.9,
 * �Indirect Objects�) and the stream dictionary must be a direct object. The
 * keyword stream that follows the stream dictionary should be followed by an
 * end-of-line marker consisting of either a carriage return and a line feed or
 * just a line feed, and not by a carriage return alone. The sequence of bytes
 * that make up a stream lie between the stream and endstream keywords; the
 * stream dictionary specifies the exact number of bytes. It is recommended that
 * there be an end-of-line marker after the data and before endstream; this
 * marker is not included in the stream length.
 */
public class PDFStream extends PDFDictionary
{
  public static final PDFStream NULL_PDFSTREAM = new PDFStream();
  private static final BoyerMoore endstreamMatcher = new BoyerMoore("endstream");
  protected Codec[] filters = null;
  private byte[] stream = null;
  private RandomAccessFile file = null;
  private StreamLocator locator = null;
  public long rawLength = -1;// debug
  public long decLength = -1;// debug

  private PDFStream()
  {
    super(Type.Stream);
  }

  public PDFStream(PDFObject po, RandomAccessFile file)
  {
    // only used for source PDF file
    super(Type.Stream, new PDFDictionary(po));
    this.stream = null;
    this.file = file;
    try
    {
      this.locator = new StreamLocator(0, (int) file.length(), this.reference);
    } catch (IOException ex)
    {
      ex.printStackTrace();
    }
  }

  public PDFStream(PDFObject po, byte[] stream)
  {
    this(new PDFDictionary(po), stream);
  }

  public PDFStream(PDFDictionary map, byte[] stream)
  {
    super(Type.Stream, map);
    this.stream = stream;
    this.file = null;
    this.locator = new StreamLocator(0, stream.length, this.reference);
  }

  public PDFStream(PDFDictionary map, StreamReader reader)
  {
    super(Type.Stream, map);
    PDFNumber length = this.get("Length").toPDFNumber();
    if (!length.isValid() && this.get("Length").isPDFPointer())
      this.addTrigger(this.get("Length").toPDFPointer().get(), this);
    if (reader.pdfStreams(0).isSourceFile())
    {
      this.stream = null;
      this.file = reader.pdfStreams(0).file;
      this.locator = reader.locateStream(length.intValue(0), endstreamMatcher, false);
    } else
    {
      this.stream = reader.readStream(length.intValue(0), endstreamMatcher, false);
      this.file = null;
      this.locator = new StreamLocator(0, stream.length, this.reference);
    }
  }

  public boolean isObjStm()
  {
    return is("Type", "ObjStm");
  }

  public synchronized Codec lastFilter()
  {
    Codec[] codecs = filters();
    return codecs.length == 0 ? null : codecs[codecs.length - 1];
  }

  public synchronized Codec[] filters()
  {
    if (filters == null)
    {
      List3<Codec> codecs = new List3<Codec>();

      // read when needed since Filter PDFObject may not yet have been reached
      // when reaching this stream object
      PDFObject object = this.get("Filter", "F").unreference();
      // Log.debug(this, " - ref=" + this.reference + ", filter=" + filters);
      if (object.isValid())
        if (object.isPDFName())
          codecs.add(new Codec(object.stringValue()));
        else if (object.isPDFArray())
          for (PDFObject filter : object)
            codecs.add(new Codec(filter.stringValue()));
        else
          Log.warn(this, " - filter expected: " + map);

      this.filters = codecs.toArray(new Codec[0]);

      int index = 0;
      object = this.get("DecodeParms", "DP").unreference();

      if (object.isValid)
        if (object.isPDFDictionary())
        {
          if (index < this.filters.length)
            this.filters[index++].parms = object.unreference().toPDFDictionary();
        } else if (object.isPDFArray())
          for (PDFObject parm : object)
            if (index < this.filters.length)
              this.filters[index++].parms = parm.unreference().isPDFDictionary() ? parm.toPDFDictionary() : null;

    }
    return this.filters;
  }

  public RandomAccessFile file()
  {
    return this.file;
  }

  public StreamLocator locator()
  {
    return this.locator;
  }

  public boolean isSourceFile()
  {
    return this.file != null && this.locator.pointer == 0;
  }

  public boolean isEncoded()
  {
    return !this.isSourceFile() && (this.filters().length > 0 || this.isEncrypted());
  }

  // TODO - add a "freeMemory" method
  private synchronized byte[] stream()
  {
    byte[] data = stream;
    if (data == null)
      try
      {
        data = new byte[(int) locator.length];
        this.file.seek(locator.pointer);
        this.file.readFully(data, 0, (int) locator.length);
      } catch (Exception e)
      {
        try
        {
          Log.warn(this, ".stream - reading file error: " + this.reference + " length=" + locator.length + " file.length=" + this.file.length());
        } catch (IOException ex)
        {
          ex.printStackTrace();
        }
        e.printStackTrace();
      }

    // Log.debug(this, ".stream - raw length=" + data.length);
    if (rawLength < 0)
      this.rawLength = data.length;

    if (isEncrypted())
    {
      PDFCipher cipher = environment().getCipher();
      if (cipher == null)
      {
        Log.warn(this, ".stream - null cipher");
      } else
        data = environment().getCipher().decrypt(this, data);

      // Log.debug(this, ".stream - decrypted length=" + data.length);
    }
    for (Codec codec : this.filters())
      data = codec.decode(this, data);
    // Log.debug(this, ".stream - decoded length=" + data.length);
    if (this.locator.length > 0 && this.locator.length < data.length)
      System.arraycopy(data, 0, data, 0, (int) this.locator.length);
    if (decLength < 0)
      this.decLength = data.length;
    return data;
  }

  public InputStream inputStream()
  {
    return new ByteArrayInputStream(stream());
  }

  public String hexaSpaced()
  {
    byte[] data = this.stream();
    StringBuilder sb = new StringBuilder(3 * data.length);
    for (byte b : data)
      sb.append(Integer.toHexString(b & 0xff).toUpperCase()).append(" ");
    if (sb.length() > 0)
      sb.deleteCharAt(sb.length() - 1);
    return sb.toString();
  }

  public String hexa()
  {
    return hexa(new StringBuilder()).toString();
  }

  public StringBuilder hexa(StringBuilder sb)
  {
    byte[] data = this.stream();
    for (byte b : data)
      sb.append(Integer.toHexString(b & 0xff).toUpperCase());
    return sb;
  }

  @Override
  protected void trigger(Reference reference, PDFObject pdfObject)
  {
    int length = pdfObject.toPDFNumber().intValue(-1);
    if (length > 0 && length < this.locator.length)
      this.locator.length = length;
  }

  public long length()
  {
    return this.locator.length;
  }

  public byte[] bpcByteValues(int bitsPerComponent)
  {
    byte[] data = this.stream();
    byte[] bpcBytes;
    if (bitsPerComponent == 1)
    {
      bpcBytes = new byte[data.length * 8];
      for (int i = 0; i < data.length; i++)
        for (int j = 7; j >= 0; j--)
          bpcBytes[i * 8 + 7 - j] = ((data[i] & (1 << j)) != 0) ? (byte) 1 : (byte) 0;
    } else
      bpcBytes = data;
    if (bitsPerComponent != 1 && bitsPerComponent != 8)
      Log.warn(this, ".bpcByteValues - not yet implemented: bitsPerComponent=" + bitsPerComponent);
    return bpcBytes;
  }

  public byte[] byteValues()
  {
    return stream();
  }

  public byte[] byteValues(int begin, int end)
  {
    byte[] data = this.stream();
    byte[] trimmed;
    if (data == null)
      trimmed = new byte[0];
    else if (begin == 0 && end == data.length)
      trimmed = data;
    else
    {
      if (begin < 0)
        begin = 0;
      if (end > data.length)
        end = data.length;
      trimmed = new byte[end - begin];
      System.arraycopy(data, begin, trimmed, 0, trimmed.length);
    }
    return trimmed;
  }

  public String asciiValue()
  {
    return asciiValue(stream());
  }

  public static String asciiValue(byte[] stream)
  {
    Log.debug(PDFStream.class, ".asciiValue - stream.length=" + stream.length);
    StringBuilder sb = new StringBuilder(stream.length);
    for (int i = 0; i < stream.length; i++)
    {
      char c = (char) stream[i];
      if (c == PDF.CR)
        sb.append('\n');
      else
        sb.append(c);
    }
    return sb.toString();
  }

  @Override
  public String stringValue()
  {
    return asciiValue();
  }

  public String stringValue(int begin, int end)
  {
    return asciiValue(byteValues(begin, end));
  }

  public String byteString()
  {
    StringBuilder sb = new StringBuilder();
    for (byte b : byteValues())
      sb.append((int) (b & 0xff)).append(" ");
    return sb.toString();
  }

  public String byteString(int begin, int end)
  {
    StringBuilder sb = new StringBuilder();
    for (byte b : byteValues(begin, end))
      sb.append((int) (b & 0xff)).append(" ");
    return sb.toString();
  }

  @Override
  public String toString()
  {
    return asciiValue() + "\n\n" + super.toString() + "\n\n";
  }

  @Override
  public String sticker()
  {
    ArrayList<String> filters = new ArrayList<String>();
    if (this.isEncrypted())
      filters.add("Encrypted");
    for (Codec codec : filters())
      filters.add(codec.name);
    if (filters.isEmpty())
      filters.add("Identity");
    return nodeNamePrefix() + (this.stream == null ? "." : "") + "Stream" + filters + "[" + length() + "bytes]" + debug;
  }
}
