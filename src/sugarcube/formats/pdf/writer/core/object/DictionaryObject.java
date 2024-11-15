package sugarcube.formats.pdf.writer.core.object;

import sugarcube.formats.pdf.writer.PDFWriter;
import sugarcube.formats.pdf.writer.core.writer.AbstractWriter;
import sugarcube.formats.pdf.writer.core.writer.Writer;
import sugarcube.formats.pdf.writer.exception.PDFException;

import java.util.ArrayList;

public abstract class DictionaryObject extends ContainerObject
{
  private ArrayList<DictionaryEntry> entries = new ArrayList<>();
  private ObjectStream objectStream;

  public DictionaryObject(PDFWriter environment, ObjectStream objectStream)
  {
    super(environment);
    this.objectStream = objectStream;
  }

  public DictionaryObject(PDFWriter environment)
  {
    this(environment, null);
  }

  public abstract void addDictionaryEntries() throws PDFException;

  @Override
  public void write() throws PDFException
  {
    PDFWriter env = pdfWriter();
    Writer writer = env.getWriter();
    if (objectStream == null)
    {
      env.registerEntry(getID(), writer.getWrittenBytes());
    } else
    {
      env.registerEntry(getID(), objectStream.getID());
    }
    writer.openObject(getID());
    writer.openDictionary();
    addDictionaryEntries();
    writeDictionaryEntries(writer);
    writer.closeDictionary();
    writer.closeObject();
    writer.flush();
  }

  public void writeDictionaryEntries(AbstractWriter writer) throws PDFException
  {
    for (DictionaryEntry dictionaryEntry : entries)
      writer.writeDictionaryPair(dictionaryEntry.name, dictionaryEntry.value, dictionaryEntry.type);
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
