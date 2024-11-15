package sugarcube.formats.pdf.reader.pdf.object;

import java.util.Iterator;

public class PDFArray extends PDFObject
{
  public static final PDFArray NULL_PDFARRAY = new PDFArray();

  private PDFArray()
  {
    super(Type.Array);
  }

  public PDFArray(PDFObject parent)
  {
    super(Type.Array, parent);
  }

  public PDFArray(PDFObject parent, StreamReader reader)
  {
    super(Type.Array, parent);
    String word;

    while (((word = reader.token()) != null) && !word.equals("]"))
    {
      PDFObject child = parsePDFObject(word, reader);
      if (child.type == Type.IndirectReference)
        child.toPDFPointer().set(removeLastReference());
      if (child.isValid())
        add(child);
    }


    PDFArray lastArray = this.environment().lastMaybeSameArray;
    if(lastArray!=null && lastArray.nbOfChildren() == this.nbOfChildren())
    {
      Iterator<PDFObject> lastIt = lastArray == null ? null : lastArray.iterator();
      Iterator<PDFObject> it = this.iterator();

      boolean isSameArray = true;
      while(lastIt.hasNext() && it.hasNext())
      {
        PDFObject o1 = lastIt.next();
        PDFObject o2 = it.next();

        if(!(o1 instanceof PDFNumber && o2 instanceof PDFNumber && Math.abs(o1.doubleValue()-o2.doubleValue())<0.0001))
        {
          isSameArray = false;
          break;
        }
      }

      if(isSameArray)
        this.children = lastArray.children;
    }

    this.environment().lastMaybeSameArray = this;
  }

  private Reference removeLastReference()
  {
    int indirectGeneration = removeLast().toPDFNumber().intValue();
    int indirectId = removeLast().toPDFNumber().intValue();
    return new Reference(indirectId, indirectGeneration);
  }

  @Override
  public PDFObject[] array()
  {
    return children.toArray(new PDFObject[0]);
  }

  public int size()
  {
    return this.nbOfChildren();
  }

  public float floatValue(int index, float recover)
  {
    return index < children.size() ? children.get(index).toPDFNumber().floatValue(recover) : recover;
  }

  @Override
  public int[] intValues()
  {
    int index = 0;
    int[] values = new int[size()];
    for (PDFObject child : this)
      values[index++] = child.toPDFNumber().intValue();
    return values;
  }

  @Override
  public double[] doubleValues()
  {
    int index = 0;
    double[] values = new double[size()];
    for (PDFObject child : this)
      values[index++] = child.toPDFNumber().doubleValue();
    return values;
  }

  @Override
  public float[] floatValues()
  {
    int index = 0;
    float[] values = new float[size()];
    for (PDFObject child : this)
      values[index++] = child.toPDFNumber().floatValue();
    return values;
  }

  @Override
  public boolean[] booleanValues()
  {
    int index = 0;
    boolean[] values = new boolean[size()];
    for (PDFObject child : this)
      values[index++] = child.toPDFBoolean().booleanValue();
    return values;
  }

  public String[] stringValues()
  {
    int index = 0;
    String[] values = new String[size()];
    for (PDFObject child : this)
      values[index++] = child.toPDFString().stringValue();
    return values;
  }
    
  public PDFDictionary[] dicoValues()
  {
    int index = 0;
    PDFDictionary[] values = new PDFDictionary[size()];
    for (PDFObject child : this)
      values[index++] = child.toPDFDictionary();
    return values;
  }  

  public PDFRectangle pdfRectangle()
  {
    return new PDFRectangle(this, doubleValues());
  }

  public PDFString[] pdfStrings()
  {
    int index = 0;
    PDFString[] values = new PDFString[size()];
    for (PDFObject child : this)
      values[index++] = child.toPDFString();
    return values;
  }

  public PDFPointer[] pdfIndirectReferences()
  {
    int index = 0;
    PDFPointer[] values = new PDFPointer[size()];
    for (PDFObject child : this)
      values[index++] = child.toPDFPointer();
    return values;
  }

  @Override
  public String stringValue()
  {
    return "Array[" + size() + "]";
  }

  @Override
  public String toString()
  {
    return stringValue();
  }

  @Override
  public String sticker()
  {
    return nodeNamePrefix() + "Array[" + size() + "]";
  }
}
