package sugarcube.formats.pdf.resources;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class RS
{
  public static final int MIN_BUFFER_SIZE = 65536;  
  
  public static byte[] ReadBytes(InputStream stream)
  {
    return ReadBytes(stream, -1);
  }
  
  
  public static byte[] ReadBytes(InputStream stream, int size)
  {
    if (stream != null)
    {
      byte[] bytes;
      size = BufferSize(stream, size);
      InputStream is = stream instanceof BufferedInputStream ? stream : new BufferedInputStream(stream);
      ByteArrayOutputStream os = new ByteArrayOutputStream(size);
      try
      {
        byte[] buffer = new byte[size];
        while ((size = is.read(buffer)) != -1)
          os.write(buffer, 0, size);
        bytes = os.toByteArray();
        is.close();
        os.close();
        return bytes;
      } catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    return null;
  }
  
  public static int BufferSize(InputStream stream, int size)
  {
    if (size <= 0)
      try
      {
        size = stream.available();
      } catch (IOException ex)
      {
      }
    return Math.max(size, MIN_BUFFER_SIZE);
  }  
  
  public static void main(String... args)
  {
    System.out.println("RS");
  }
}
