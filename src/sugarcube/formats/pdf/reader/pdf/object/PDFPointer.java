package sugarcube.formats.pdf.reader.pdf.object;

public class PDFPointer extends PDFObject
{
  public static final PDFPointer NULL_POINTER = new PDFPointer();
  private Reference NULL_REFERENCE = new Reference();
  private Reference pointer = new Reference();//called "indirect reference" in PDF Reference

  private PDFPointer()
  {
    super(Type.IndirectReference);
    this.setReference(NULL_REFERENCE);
  }

  public PDFPointer(PDFObject pdfObject, int indirectId, int indirectGeneration)
  {
    super(Type.IndirectReference, pdfObject);
    this.set(indirectId, indirectGeneration);
  }

  public Reference get()
  {
    return pointer;
  }

  public void set(int id, int gen)
  {
    this.pointer = new Reference(id, gen);
  }

  public void set(Reference pointer)
  {
    this.pointer = pointer;
  }

  @Override
  public int intValue(int recover)
  {
    if (this.unreference().isValid())
      return this.unreference().intValue();
    else
      return recover;
  }

  @Override
  public double doubleValue(double recover)
  {
    if (this.unreference().isValid())
      return this.unreference().doubleValue();
    else
      return recover;
  }

  @Override
  public String stringValue(String recover)
  {
    if (this.unreference().isValid())
      return this.unreference().stringValue();
    else
      return recover;
  }

  @Override
  public String stringValue()
  {
    return this.unreference().stringValue();
  }

  @Override
  public int intValue()
  {
    return this.unreference().intValue();
  }

  @Override
  public double doubleValue()
  {
    return this.unreference().doubleValue();
  }

  @Override
  public String toString()
  {
    return "(" + pointer.id() + " " + pointer.generation() + " R)";
  }

  @Override
  public String sticker()
  {
    return nodeNamePrefix() + "(" + pointer.id() + " " + pointer.generation() + " R)";
  }
}
