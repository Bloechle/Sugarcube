package sugarcube.formats.pdf.reader.pdf.object;

public class PDFNull extends PDFObject
{
  public static final PDFNull NULL_PDFNULL = new PDFNull();
  public String value = null;

  private PDFNull()
  {
    super(Type.Null);
    this.isValid = false;
  }

  public PDFNull(PDFObject pdfObject)
  {
    super(Type.Null, pdfObject);
    this.isValid = false;
//    Log.debug(this, ".bou - " + (value == null ? "NULL" : value));
  }

  public PDFNull(PDFObject pdfObject, int id, int generation, String token, StreamReader reader)
  {
    this(pdfObject);
    this.value = "id=" + id + ", gen=" + generation + ", token=" + token;
    this.isValid = false;
  }

  public PDFNull(PDFObject pdfObject, String value)
  {
    super(Type.Null, pdfObject);
    this.value = value;
    // valid if this is a declared null object
    this.isValid = value != null && value.equals("null");
  }

  public PDFObject unreference()
  {
    return this;
  }
  
  public int intValue(int def)
  {
    return isValid ? def : super.intValue(def);
  }

  public int intValue()
  {
    return isValid ? 0 : super.intValue();
  }

  public double doubleValue(double def)
  {
    return isValid ? def : super.doubleValue(def);
  }

  public double doubleValue()
  {
    return isValid ? 0.0 : super.doubleValue();
  }

  public boolean booleanValue(boolean def)
  {
    return isValid ? def : super.booleanValue(def);
  }

  public boolean booleanValue()
  {
    return isValid ? false : super.booleanValue();
  }

  @Override
  public String stringValue()
  {
    return "Null";
  }

  @Override
  public String sticker()
  {
    return "Null[" + value + "]";
  }
}
