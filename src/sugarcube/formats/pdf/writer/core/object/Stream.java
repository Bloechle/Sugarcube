package sugarcube.formats.pdf.writer.core.object;

import sugarcube.formats.pdf.writer.PDFWriter;
import sugarcube.formats.pdf.writer.core.writer.AbstractWriter;
import sugarcube.formats.pdf.writer.core.writer.DummyWriter;
import sugarcube.formats.pdf.writer.core.writer.Writer;
import sugarcube.formats.pdf.writer.exception.PDFException;

import java.util.ArrayList;
import java.util.zip.Deflater;

public class Stream
{
  private int id = -1;
  private PDFWriter pdf;
  private ArrayList<DictionaryEntry> entries = new ArrayList<DictionaryEntry>();

  public Stream(PDFWriter pdf)
  {
    this.pdf = pdf;
    this.id = pdf.computeID();
  }

  public void write(StringBuilder sb) throws PDFException
  {
    AbstractWriter writer = pdf.getWriter();
    pdf.registerEntry(id, writer.getWrittenBytes());
    write(writer, sb);
  }

  public long calculateSize(StringBuilder sb) throws PDFException
  {
    DummyWriter writer = new DummyWriter();
    write(writer, sb);
    return writer.getWrittenBytes();
  }

  public void write(AbstractWriter writer, StringBuilder sb) throws PDFException
  {
    writer.openObject(id);
    writer.openDictionary();
    int streamLength;
    int decodedStreamLength = sb.length();
    byte[] compressedStream = new byte[0];
    if (PDFWriter.COMPRESS)
    {
      Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
      deflater.setInput(sb.toString().getBytes());
      deflater.finish();
      compressedStream = new byte[sb.length()];
      streamLength = deflater.deflate(compressedStream);
      writer.writeDictionaryPair("Filter", "FlateDecode", Writer.NAME);
    }
    else
      streamLength = sb.length();
    writer.writeDictionaryPair("Length", streamLength, Writer.INTEGER);
    writer.writeDictionaryPair("DL", decodedStreamLength, Writer.INTEGER);

    for (DictionaryEntry dictionaryEntry : entries)
      writer.writeDictionaryPair(dictionaryEntry.name, dictionaryEntry.value, dictionaryEntry.type);
    writer.closeDictionary();


    writer.openStream();

    if (PDFWriter.COMPRESS)
      writer.write(compressedStream, streamLength);
    else
      writer.write(sb.toString());
    writer.closeStream();
    writer.closeObject();
  }

  public Integer getID()
  {
    return id;
  }

  public void addDictionaryEntry(String name, Object value, int valueType)
  {
    DictionaryEntry entry = new DictionaryEntry();
    entry.name = name;
    entry.value = value;
    entry.type = valueType;
    entries.add(entry);
  }

  public void cleanDictionary()
  {
    entries.clear();
  }

  private class DictionaryEntry
  {
    protected String name;
    protected Object value;
    protected int type;
  }
}
