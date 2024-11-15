package sugarcube.formats.pdf.reader.pdf.object;

import sugarcube.common.system.log.Log;

public class PDFName extends PDFObject
{
  public static final PDFName NULL_PDFNAME = new PDFName();
  private static final String NULL_NAME = "null_name";
  private final String name;

  private PDFName()
  {
    super(Type.Name);
    this.name = NULL_NAME;
  }

  public PDFName(PDFObject pdfObject, String name)
  {
    super(Type.Name, pdfObject);
    this.name = normalizeValue(name);
  }

  private String normalizeValue(String name)
  {
    char sharp = '#';
    char[] chars = name.toCharArray();
    try
    {
      for (int i = 0; i < chars.length; i++)
        if (chars[i] == sharp)
        {
          String hexa = new String(chars, i, 3);
          name = name.replaceAll(hexa, new String(new int[]
            {
              Integer.decode(hexa).intValue()
            }, 0, 1));
          chars = name.toCharArray();
          i = 0;
        }
    }
    catch (Exception e)
    {
      Log.warn(this, ".normalizeValue - strange hexa string: " + name);
      e.printStackTrace();
    }
    return name;
  }

  public boolean isName(String name)
  {
    return this.name.equals(name);
  }

  @Override
  public String stringValue()
  {
    return name;
  }

  @Override
  public String toString()
  {
    return name;
  }

  @Override
  public String sticker()
  {
    return nodeNamePrefix() + name;
  }
}
