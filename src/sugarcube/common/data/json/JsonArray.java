package sugarcube.common.data.json;

import sugarcube.common.data.collections.Array3;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

public class JsonArray extends Array3 implements Json.Able
{
  public JsonArray()
  {
    super();
  }

  public JsonArray(Collection c)
  {
    super(c);
  }

  @Override
  public void writeJson(Writer out, String indent) throws IOException
  {
    writeJson(this, out, indent);
  }

  @Override
  public String toJson(String indent)
  {
    return toJson(this, indent);
  }

  public Array3<JsonMap> objects()
  {
    Array3<JsonMap> array = new Array3<>(this.size());
    for (Object o : this)
      if (o instanceof JsonMap)
        array.add((JsonMap) o);
    return array;
  }

  public String[] strings()
  {
    String[] array = new String[this.size()];
    Object o;
    for (int i = 0; i < array.length; i++)
      array[i] = (o = get(i)) == null ? null : o.toString();
    return array;
  }

  public double[] doubles()
  {
    double[] array = new double[this.size()];
    Object o;
    for (int i = 0; i < array.length; i++)
      array[i] = (o = get(i)) == null ? 0 : (Double)o;
    return array;
  }

  public float[] floats()
  {
    float[] array = new float[this.size()];
    Object o;
    for (int i = 0; i < array.length; i++)
      array[i] = (float)((o = get(i)) == null ? 0 : (Double)o);
    return array;
  }

  public JsonArray array(int index)
  {
    return (JsonArray) get(index);
  }

  public static void writeJson(Collection collection, Writer out, String indent) throws IOException
  {
    if (collection == null)
    {
      out.write("null");
      return;
    }

    boolean first = true;
    Iterator iter = collection.iterator();

    out.write('[');
    while (iter.hasNext())
    {
      if (first)
        first = false;
      else
        out.write(',');

      Object value = iter.next();
      if (value == null)
      {
        out.write("null");
        continue;
      }

      Json.WriteJson(value, out, indent);
    }
    out.write(']');
  }

  public static String toJson(Collection collection, String indent)
  {
    final StringWriter writer = new StringWriter();

    try
    {
      writeJson(collection, writer, indent);
      return writer.toString();
    } catch (IOException e)
    {
      // This should never happen for a StringWriter
      throw new RuntimeException(e);
    }
  }

  public static void writeJson(byte[] array, Writer out) throws IOException
  {
    if (array == null)
    {
      out.write("null");
    } else if (array.length == 0)
    {
      out.write("[]");
    } else
    {
      out.write("[");
      out.write(String.valueOf(array[0]));

      for (int i = 1; i < array.length; i++)
      {
        out.write(",");
        out.write(String.valueOf(array[i]));
      }

      out.write("]");
    }
  }

  public static String toJson(byte[] array)
  {
    final StringWriter writer = new StringWriter();

    try
    {
      writeJson(array, writer);
      return writer.toString();
    } catch (IOException e)
    {
      // This should never happen for a StringWriter
      throw new RuntimeException(e);
    }
  }

  public static void writeJson(short[] array, Writer out) throws IOException
  {
    if (array == null)
    {
      out.write("null");
    } else if (array.length == 0)
    {
      out.write("[]");
    } else
    {
      out.write("[");
      out.write(String.valueOf(array[0]));

      for (int i = 1; i < array.length; i++)
      {
        out.write(",");
        out.write(String.valueOf(array[i]));
      }

      out.write("]");
    }
  }

  public static String toJson(short[] array)
  {
    final StringWriter writer = new StringWriter();

    try
    {
      writeJson(array, writer);
      return writer.toString();
    } catch (IOException e)
    {
      // This should never happen for a StringWriter
      throw new RuntimeException(e);
    }
  }

  public static void writeJson(int[] array, Writer out) throws IOException
  {
    if (array == null)
    {
      out.write("null");
    } else if (array.length == 0)
    {
      out.write("[]");
    } else
    {
      out.write("[");
      out.write(String.valueOf(array[0]));

      for (int i = 1; i < array.length; i++)
      {
        out.write(",");
        out.write(String.valueOf(array[i]));
      }

      out.write("]");
    }
  }

  public static String toJson(int[] array)
  {
    final StringWriter writer = new StringWriter();

    try
    {
      writeJson(array, writer);
      return writer.toString();
    } catch (IOException e)
    {
      // This should never happen for a StringWriter
      throw new RuntimeException(e);
    }
  }

  public static void writeJson(long[] array, Writer out) throws IOException
  {
    if (array == null)
    {
      out.write("null");
    } else if (array.length == 0)
    {
      out.write("[]");
    } else
    {
      out.write("[");
      out.write(String.valueOf(array[0]));

      for (int i = 1; i < array.length; i++)
      {
        out.write(",");
        out.write(String.valueOf(array[i]));
      }

      out.write("]");
    }
  }

  public static String toJSONString(long[] array)
  {
    final StringWriter writer = new StringWriter();

    try
    {
      writeJson(array, writer);
      return writer.toString();
    } catch (IOException e)
    {
      // This should never happen for a StringWriter
      throw new RuntimeException(e);
    }
  }

  public static void writeJson(float[] array, Writer out) throws IOException
  {
    if (array == null)
    {
      out.write("null");
    } else if (array.length == 0)
    {
      out.write("[]");
    } else
    {
      out.write("[");
      out.write(String.valueOf(array[0]));

      for (int i = 1; i < array.length; i++)
      {
        out.write(",");
        out.write(String.valueOf(array[i]));
      }

      out.write("]");
    }
  }

  public static String toJson(float[] array)
  {
    final StringWriter writer = new StringWriter();

    try
    {
      writeJson(array, writer);
      return writer.toString();
    } catch (IOException e)
    {
      // This should never happen for a StringWriter
      throw new RuntimeException(e);
    }
  }

  public static void writeJson(double[] array, Writer out) throws IOException
  {
    if (array == null)
    {
      out.write("null");
    } else if (array.length == 0)
    {
      out.write("[]");
    } else
    {
      out.write("[");
      out.write(String.valueOf(array[0]));

      for (int i = 1; i < array.length; i++)
      {
        out.write(",");
        out.write(String.valueOf(array[i]));
      }

      out.write("]");
    }
  }

  public static String toJson(double[] array)
  {
    final StringWriter writer = new StringWriter();

    try
    {
      writeJson(array, writer);
      return writer.toString();
    } catch (IOException e)
    {
      // This should never happen for a StringWriter
      throw new RuntimeException(e);
    }
  }

  public static void writeJson(boolean[] array, Writer out) throws IOException
  {
    if (array == null)
    {
      out.write("null");
    } else if (array.length == 0)
    {
      out.write("[]");
    } else
    {
      out.write("[");
      out.write(String.valueOf(array[0]));

      for (int i = 1; i < array.length; i++)
      {
        out.write(",");
        out.write(String.valueOf(array[i]));
      }

      out.write("]");
    }
  }

  public static String toJson(boolean[] array)
  {
    final StringWriter writer = new StringWriter();

    try
    {
      writeJson(array, writer);
      return writer.toString();
    } catch (IOException e)
    {
      // This should never happen for a StringWriter
      throw new RuntimeException(e);
    }
  }

  public static void writeJson(char[] array, Writer out) throws IOException
  {
    if (array == null)
    {
      out.write("null");
    } else if (array.length == 0)
    {
      out.write("[]");
    } else
    {
      out.write("[\"");
      out.write(String.valueOf(array[0]));

      for (int i = 1; i < array.length; i++)
      {
        out.write("\",\"");
        out.write(String.valueOf(array[i]));
      }

      out.write("\"]");
    }
  }

  public static String toJson(char[] array)
  {
    final StringWriter writer = new StringWriter();

    try
    {
      writeJson(array, writer);
      return writer.toString();
    } catch (IOException e)
    {
      // This should never happen for a StringWriter
      throw new RuntimeException(e);
    }
  }

  public static void writeJson(Object[] array, Writer out, String indent) throws IOException
  {
    if (array == null)
    {
      out.write("null");
    } else if (array.length == 0)
    {
      out.write("[]");
    } else
    {
      out.write('[');
      Json.WriteJson(array[0], out, indent);

      for (int i = 1; i < array.length; i++)
      {
        out.write(',');
        ;
        Json.WriteJson(array[i], out, indent);
      }

      out.write("]");
    }
  }

  public static String toJson(Object[] array, String indent)
  {
    final StringWriter writer = new StringWriter();

    try
    {
      writeJson(array, writer, indent);
      return writer.toString();
    } catch (IOException e)
    {
      // This should never happen for a StringWriter
      throw new RuntimeException(e);
    }
  }

  // public static String toJson(String[][] array, String indent)
  // {
  // final StringWriter writer = new StringWriter();
  //
  // try
  // {
  // writeJson(array, writer, indent);
  // return writer.toString();
  // } catch (IOException e)
  // {
  // // This should never happen for a StringWriter
  // throw new RuntimeException(e);
  // }
  // }

  @Override
  public String toString()
  {
    return toJson("");
  }
}
