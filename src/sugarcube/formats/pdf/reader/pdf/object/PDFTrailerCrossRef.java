package sugarcube.formats.pdf.reader.pdf.object;

import sugarcube.common.data.collections.List3;

public class PDFTrailerCrossRef
{
  public int[] nbOfBytes;
  public byte[] bytes;

  public PDFTrailerCrossRef(int[] nbOfBytes, byte[] bytes)
  {
    this.nbOfBytes = nbOfBytes;
    this.bytes = bytes;
  }

  public int field(int index)
  {
    int fieldIndex = 0;
    for (int i = 0; i < index; i++)
      fieldIndex += nbOfBytes[i];
    byte[] field = new byte[nbOfBytes[index]];
    System.arraycopy(bytes, fieldIndex, field, 0, field.length);

    // big-endian? useful?
    switch (field.length)
    {
    case 1:
      return field[0] & 0xFF;
    case 2:
      return (bytes[0] & 0xFF) << 8 | (bytes[1] & 0xFF);
    case 3:
      return (bytes[0] & 0xFF) << 16 | (bytes[1] & 0xFF) << 8 | (bytes[2] & 0xFF);
    case 4:
      return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }
    return 0;

  }

  @Override
  public String toString()
  {
    String fields = "";
    for (int i = 0; i < nbOfBytes.length; i++)
      fields += field(i) + " ";
    return fields;
  }

  public static PDFTrailerCrossRef[] Parse(int[] nbOfBytes, byte[] data)
  {
    List3<PDFTrailerCrossRef> crossRefs = new List3<>();

    int size = 0;
    for (int i = 0; i < nbOfBytes.length; i++)
      size += nbOfBytes[i];

    for (int i = 0; i < (data.length - size + 1); i += size)
    {
      byte[] bytes = new byte[size];
      System.arraycopy(data, i, bytes, 0, bytes.length);
      crossRefs.add(new PDFTrailerCrossRef(nbOfBytes, bytes));
    }

    return crossRefs.toArray(new PDFTrailerCrossRef[0]);
  }
}
