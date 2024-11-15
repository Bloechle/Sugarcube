package sugarcube.formats.pdf.writer.core.writer;

import sugarcube.formats.pdf.writer.core.Lexic;
import sugarcube.formats.pdf.writer.document.PDFUtil;
import sugarcube.formats.pdf.writer.exception.PDFException;

public abstract class AbstractWriter
{
  public final static int BOOLEAN = 0;
  public final static int INTEGER = 1;
  public final static int REAL = 2;
  public final static int LITERAL = 3;
  public final static int HEXADECIMAL = 4;
  public final static int NAME = 5;
  public final static int COMMENT = 6;
  public final static int INDIRECT_REFERENCE = 7;
  public static final int INTEGER_ARRAY = 8;
  public static final int REAL_ARRAY = 9;
  public static final int STRING_ARRAY = 10;
  public static final int GENERIC_STRING = 11;

  public abstract void write(long value) throws PDFException;

  public abstract void write(Float value) throws PDFException;

  public abstract void write(String value) throws PDFException;

  public abstract void write(byte value) throws PDFException;

  public abstract void write(byte[] values, int numberOfBytes) throws PDFException;

  public abstract long getWrittenBytes();

  public void writeBoolean(boolean value) throws PDFException
  {
    write(value ? "true" : "false");
  }

  public void writeNull() throws PDFException
  {
    write("null");
  }

  public void writeInteger(long value) throws PDFException
  {
    write(value);
  }

  public void writeReal(float value) throws PDFException
  {
    if (value == Math.round(value))
    {
      write((long) value);
      return;
    }
    write(PDFUtil.Print(value));
  }

  public void writeLiteral(String value) throws PDFException
  {
    value = value.replaceAll("[" + Lexic.LEFT_PARENTHESIS + "]+", Lexic.REVERSED_SOLIDUS + Lexic.LEFT_PARENTHESIS);
    value = value.replaceAll("[" + Lexic.RIGHT_PARENTHESIS + "]+", Lexic.REVERSED_SOLIDUS + Lexic.RIGHT_PARENTHESIS);
    write(Lexic.LEFT_PARENTHESIS);
    write(value);
    write(Lexic.RIGHT_PARENTHESIS);
  }

  public void writeHexa(String value) throws PDFException
  {
    write(Lexic.LESS_THAN);
    String temp;
    for (int c = 0; c < value.length(); c++)
    {
      temp = Integer.toHexString(value.charAt(c));
      if (temp.length() == 1)
        temp = "0" + temp;
      write(temp);
    }
    write(Lexic.GREATER_THAN);
  }

  public void writeName(String value) throws PDFException
  {
    write(Lexic.SOLIDUS);
    write(value);
  }

  public void writeComment(String value) throws PDFException
  {
    write(Lexic.PERCENT_SIGN);
    write(value);
    write(Lexic.LINE_FEED);
  }

  public void openDictionary() throws PDFException
  {
    write(Lexic.LESS_THAN);
    write(Lexic.LESS_THAN);
    // write(Lexic.SPACE);
  }

  public void writeDictionaryPair(String name, Object value, int valueType) throws PDFException
  {
    writeName(name);
    if (valueType == INTEGER || valueType == REAL || valueType == INDIRECT_REFERENCE || valueType == BOOLEAN)
      write(Lexic.SPACE);
    // System.out.println(name + " " + value);
    write(value, valueType);
    // write(Lexic.LINE_FEED);
  }

  public void closeDictionary() throws PDFException
  {
    // write(Lexic.SPACE);
    write(Lexic.GREATER_THAN);
    write(Lexic.GREATER_THAN);
    write(Lexic.LINE_FEED);
  }

  public void openArray() throws PDFException
  {
    write(Lexic.LEFT_SQUARE_BRACKET);
  }

  public void closeArray() throws PDFException
  {
    write(Lexic.RIGHT_SQUARE_BRACKET);
    // write(Lexic.LINE_FEED);
  }

  public void writeIndirectReference(int id) throws PDFException
  {
    write(id);
    write(Lexic.SPACE);
    write(0);
    write(Lexic.SPACE);
    write("R");
  }

  public void openObject(int id) throws PDFException
  {
    write(id);
    write(Lexic.SPACE);
    write(0);
    write(Lexic.SPACE);
    write("obj");
    write(Lexic.LINE_FEED);
  }

  public void closeObject() throws PDFException
  {
    write("endobj");
    write(Lexic.LINE_FEED);
  }

  public void openStream() throws PDFException
  {
    write("stream");
    write(Lexic.LINE_FEED);
  }

  public void closeStream() throws PDFException
  {
    write("endstream");
    write(Lexic.LINE_FEED);
  }

  public void write(Object value, int objectType) throws PDFException
  {
    switch (objectType)
    {
    case BOOLEAN:
      writeBoolean((Boolean) value);
      break;
    case INTEGER:
      writeInteger(value instanceof Long ? (Long) value : (Integer) value);
      break;
    case REAL:
      writeReal((Float) value);
      break;
    case LITERAL:
      writeLiteral((String) value);
      break;
    case HEXADECIMAL:
      writeHexa((String) value);
      break;
    case NAME:
      writeName((String) value);
      break;
    case COMMENT:
      writeComment((String) value);
      break;
    case INDIRECT_REFERENCE:
      writeIndirectReference((Integer) value);
      break;
    case INTEGER_ARRAY:
      Integer[] integerValues = (Integer[]) value;
      openArray();
      for (int v = 0; v < integerValues.length; v++)
      {
        if (v > 0)
          write(Lexic.SPACE);
        writeInteger(integerValues[v]);
      }
      closeArray();
      break;
    case REAL_ARRAY:
      Float[] floatValues = (Float[]) value;
      openArray();
      for (int v = 0; v < floatValues.length; v++)
      {
        if (v > 0)
          write(Lexic.SPACE);
        writeReal(floatValues[v]);
      }
      closeArray();
      break;
    case STRING_ARRAY:
      String[] stringValues = (String[]) value;
      openArray();
      for (int v = 0; v < stringValues.length; v++)
        write(stringValues[v]);
      closeArray();
      break;
    case GENERIC_STRING:
      write((String) value);
      break;
    default:
      throw new PDFException("Unknown operator '" + objectType + "' - '" + value + "'");
    }
  }
}
