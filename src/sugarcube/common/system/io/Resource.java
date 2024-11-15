package sugarcube.common.system.io;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import sugarcube.common.system.log.Log;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class Resource
{
  private final String name;
  private final String path;
  private InputStream stream;

  public Resource(String name, String path)
  {
    this.name = name;
    this.path = path;
  }

  public InputStream load()
  {
    try
    {
      if (new File(path).isFile())
        return new FileInputStream(new File(path));
      else
        return this.stream = Resource.load(path);
    }
    catch (Exception ex)
    {
      Log.debug(this, ".load resource failed: " + path);
      ex.printStackTrace();
      return null;
    }
  }

  public String name()
  {
    return name;
  }

  public String path()
  {
    return path;
  }

  public void close()
  {
    if (stream != null)
      IO.Close(stream);
    this.stream = null;
  }

  @Override
  public boolean equals(Object resource)
  {
    if (this == resource)
      return true;
    if (resource == null || this.getClass() != resource.getClass())
      return false;
    return this.path.equals(((Resource) resource).path);
  }

  @Override
  public int hashCode()
  {
    int hash = 5;
    hash = 37 * hash + (this.path != null ? this.path.hashCode() : 0);
    return hash;
  }

  @Override
  public String toString()
  {
    return name;
  }

  public static BufferedImage loadImage(Class root, String name)
  {
    try
    {
      InputStream stream = root.getResourceAsStream(name);
      if (stream == null)
        Log.warn(Resource.class, ".loadImage - loading resource failed: " + name);
      BufferedImage bi = ImageIO.read(stream);
      stream.close();
      return bi;
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
      return new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
    }
  }

  public static BufferedImage loadImage(String path)
  {
    try
    {
      InputStream stream = load(path);
      BufferedImage bi = ImageIO.read(stream);
      IO.Close(stream);
      return bi;
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
      return new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
    }
  }

  public static BufferedImage loadImage(URL url)
  {
    try
    {
      return ImageIO.read(url);
    }
    catch (IOException e)
    {
      e.printStackTrace();
      return new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
    }
  }

  public static List<Resource> list(String path)
  {
    return list(path, "");
  }

  public static List<Resource> list(String path, String... extensions)
  {
    Log.debug(Resource.class,  ".list - path="+path);
    List<Resource> list = new LinkedList<Resource>();    
    if (new File(path).exists())
    {
      File directory = new File(path);
      if (!directory.isDirectory())
        directory = directory.getParentFile();
      try
      {
        for (File file : directory.listFiles())
          if (doMatch(file.getCanonicalPath(), extensions))
            list.add(new Resource(file.getName(), file.getCanonicalPath()));

      }
      catch (Exception e)
      {
        Log.error(Resource.class, ".list, file listing failed: " + e.getMessage());
        e.printStackTrace();
      }
    }
    else
      try
      {
        InputStream stream = Resource.load(path + "Resources.xml");
        Element document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream).getDocumentElement();
        IO.Close(stream);

        NodeList resources = document.getElementsByTagName("resource");
        for (int j = 0; j < resources.getLength(); j++)
        {
          Element resource = (Element) resources.item(j);
          String file = resource.getAttribute("name");
          if (doMatch(file, extensions))
            list.add(new Resource(file, path + file));
        }
      }
      catch (Exception e)
      {
        Log.error(Resource.class, ".list - resource XML file parsing failed: " + e.getMessage());
        e.printStackTrace();
      }
    return list;
  }

//  public static String jarPath(Class c, String filename)
//  {
//    String path = c.getCanonicalName();
//    return path.substring(0, path.lastIndexOf(".") + 1).replaceAll("\\.", "/") + filename;
//  }

//  public static InputStream load(Class c, String filename)
//  {    
//    InputStream stream = c.getResourceAsStream(filename);
//    if (stream == null)
//      Log.info(Resource.class, ".load - file not found: " + jarPath(c, filename));
//    return stream;
//  }

  public static InputStream load(String path)
  {
    InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    if (stream == null)
      Log.info(Resource.class, ".load - file not found: " + path);
    return stream;
  }

  public static BufferedReader loadReader(String path)
  {
    return new BufferedReader(new InputStreamReader(load(path)));
  }

  public static boolean doMatch(String name, String... extensions)
  {
    if (extensions == null || extensions.length == 0 || (extensions.length == 1 && extensions[0].isEmpty()))
      return true;

    for (String extension : extensions)
      if (name.toLowerCase().endsWith((extension.startsWith(".") ? extension : "." + extension).toLowerCase()))
        return true;

    return false;
  }
}