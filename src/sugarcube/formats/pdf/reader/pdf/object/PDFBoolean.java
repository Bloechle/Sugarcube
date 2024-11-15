package sugarcube.formats.pdf.reader.pdf.object;

public class PDFBoolean extends PDFObject
{
  public static final PDFBoolean NULL_PDFBOOLEAN = new PDFBoolean();
  private final boolean value;

  private PDFBoolean()
  {
    super(Type.Boolean);
    this.value = false;
  }

  public PDFBoolean(PDFObject pdfObject, String value)
  {
    super(Type.Boolean, pdfObject);
    this.value = value.equals("true");
  }

  @Override
  public boolean booleanValue()
  {
    return value;
  }

  @Override
  public String stringValue()
  {
    return Boolean.toString(value);
  }

  @Override
  public String toString()
  {
    return Boolean.toString(value);
  }

  @Override
  public String sticker()
  {
    return nodeNamePrefix() + "Boolean[" + Boolean.toString(value) + "]";
  }
}
