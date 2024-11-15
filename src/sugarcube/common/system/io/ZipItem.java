package sugarcube.common.system.io;

import sugarcube.common.data.collections.Str;
import sugarcube.common.graphics.Image3;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;

public class ZipItem implements Comparable<ZipItem>
{
  public Zip zip;
  public ZipEntry entry;
  private String path;

  public ZipItem(Zip zip, ZipEntry entry)
  {
    this.zip = zip;
    this.entry = entry;
  }

  public boolean hasZipEntry()
  {
    return entry != null;
  }

  public boolean exists()
  {
    return zip.has(path());
  }

  public int intSize()
  {
    return (int) size();
  }

  public long size()
  {
    return entry.getSize();
  }

  public boolean write(File3 file)
  {
     return file.write(stream(), true);
  }

  public BufferedInputStream bufferedStream()
  {
    InputStream stream = stream();
    return stream instanceof BufferedInputStream ? (BufferedInputStream) stream : new BufferedInputStream(stream());
  }

  public InputStream stream()
  {
    return zip.stream(entry);
  }

  public byte[] bytes()
  {
    return IO.ReadBytes(stream());
  }
  
  public String string()
  {
    return IO.ReadText(stream());
  }
  
  public String[] lines()
  {
    return Str.Split(string(), "[\r\n]+");
  }

  public Image3 image()
  {
    InputStream stream = stream();
    Image3 image = Image3.read(stream);
    IO.Close(stream);
    return image;
  }

  public boolean starts(String... starts)
  {
    String path = this.path();
    for (String start : starts)
      if (path.startsWith(start))
        return true;
    return false;
  }

  public String path()
  {
    return path == null ? path = File3.normalize(entry.getName()) : path;
  }
  
  public boolean isName(String name)
  {
    return name().equals(name);
  }

  public String name()
  {
    return File3.Filename(path());
  }

  public String name(boolean keepExtension)
  {
    return File3.Filename(path(), !keepExtension);
  }

  public String directory()
  {
    return File3.directory(path());
  }

  public boolean hasDir(String dir)
  {
    if (dir.startsWith("/"))
      dir = dir.substring(1);
    if (dir.endsWith("/"))
      dir = dir.substring(0, dir.length() - 1);

    for (String token : path().split("/"))
      if (dir.equals(token))
        return true;
    return false;
  }

  public boolean isExt(String... extensions)
  {
    return isExt(true, extensions);
  }

  public boolean isExt(boolean ignoreCase, String... extensions)
  {
    String path = ignoreCase ? path().toLowerCase() : path();
    for (String ext : extensions)
    {
      ext = ignoreCase ? ext.toLowerCase() : ext;
      if (ext.isEmpty() && File3.extension(path, false).isEmpty())
        return true;
      else if (path.endsWith(ext.startsWith(".") ? ext : "." + ext))
        return true;
    }
    return false;
  }

  @Override
  public String toString()
  {
    String name = entry.getName();
    return name.equals(path()) ? name : name + " - " + path();
  }

  @Override
  public int compareTo(ZipItem that)
  {
    return this.path().compareTo(that.path());
  }
}
