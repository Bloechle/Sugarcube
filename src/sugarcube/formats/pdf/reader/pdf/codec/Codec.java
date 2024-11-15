package sugarcube.formats.pdf.reader.pdf.codec;

import sugarcube.common.system.log.Log;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.PDFStream;

public class Codec
{
  public String name;
  public PDFDictionary parms;

  public Codec(String name)
  {
    this.name = name;
  }

  public boolean is(String... names)
  {
    for (String codec : names)
      if (name.equals(codec))
        return true;
    return false;
  }

  public byte[] decode(PDFStream map, byte[] stream)
  {
    try
    {
      if (is("ASCII85Decode", "A85"))
        return ASCII85Decode.decode(stream);
      else if (is("ASCIIHexDecode", "AHx"))
        return ASCIIHexDecode.decode(stream);
      else if (is("FlateDecode", "Fl"))
        return FlateDecode.decode(stream, parms);
      else if(is("RunLengthDecode", "RL"))
        return RunLengthDecode.decode(stream);
      else if (is("LZWDecode", "LZW"))
        return LZWDecode.decode(stream, parms);
    }
    catch (Exception e)
    {
      Log.warn(Codec.class, ".decode - stream decode error in " + name + map.reference() + ": " + e);
      e.printStackTrace();
    }
    return stream;
  }
  
  @Override
  public String toString()
  {
    return name+parms;
  }
}
