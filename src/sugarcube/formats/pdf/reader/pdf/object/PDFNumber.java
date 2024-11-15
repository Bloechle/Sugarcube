package sugarcube.formats.pdf.reader.pdf.object;

public class PDFNumber extends PDFObject
{
  public static final PDFNumber NULL_PDFNUMBER = new PDFNumber();
  private final double value;

  private PDFNumber()
  {
    super(Type.Number);
    this.value = 0;
  }

  public PDFNumber(PDFObject po, double value)
  {
    super(Type.Number, po);
    this.value = value;
  }

  public PDFNumber(PDFObject po, String token)
  {
    super(Type.Number, po);

    double number = 0;
    try
    {
      number = Double.parseDouble(token);
    }
    catch (NumberFormatException e)
    {
      this.invalidate();
    }
    this.value = number;
  }

  @Override
  public int intValue()
  {
    return (int) value;
  }

  public long longValue()
  {
    return (long) value;
  }

  @Override
  public int[] intValues()
  {
    return new int[]
      {
        (int) value
      };
  }

  @Override
  public double doubleValue()
  {
    return value;
  }

  @Override
  public double[] doubleValues()
  {
    return new double[]
      {
        value
      };
  }

  @Override
  public float floatValue()
  {
    return (float) value;
  }

  @Override
  public float[] floatValues()
  {
    return new float[]
      {
        (float) value
      };
  }

  @Override
  public String stringValue()
  {
    return Double.toString(value);
  }

  @Override
  public String toString()
  {
    return Double.toString(value);
  }

  @Override
  public String sticker()
  {
    return nodeNamePrefix() + stringValue();
  }
}
