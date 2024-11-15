package sugarcube.common.system.io;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.StringMap;
import sugarcube.common.data.collections.StringSet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Zip implements Iterable<ZipItem>
{
  protected ZipFile zip = null;
  protected File3 file = null;

  public interface ZipItemHandler
  {
    void handleZipItem(ZipItem entry);
  }

  private boolean closed = false;

  public Zip(String path)
  {
    this(new File3(path));
  }

  public Zip(File file)
  {
    if (file == null)
      Log.warn(this, " - file is null");
    else if (!file.exists())
      Log.warn(this, " - file does not exists: " + file);
    else
    {
      this.file = File3.Wrap(file);
      if (!file.isDirectory())
        try
        {
          this.zip = new ZipFile(file);// true zip file mode
        } catch (IOException ex)
        {
          // this.file = this.file.parent();// directory mode
          ex.printStackTrace();
          Log.debug(this, " - no zip : " + this.file);
        }
      else
        Log.debug(this,  " - directory zip: "+file);
    }
  }

  public String name()
  {
    return zip == null ? file.path() : zip.getName();
  }

  public synchronized ZipEntry zipEntry(String path)
  {
    // if entry==null unzipped folder mode
    ZipEntry entry = zip == null ? null : zip.getEntry(path);
    return entry == null ? new ZipEntry(path) : entry;
  }

  public synchronized ZipItem entry(String path)
  {
    return new ZipItem(this, zipEntry(path));
  }

  // public synchronized boolean closed()
  // {
  // try
  // {
  // return this.entries().hasMoreElements();
  // }
  // catch (Exception e)
  // {
  // Log.debug(this, ".alive - zipFile has been closed: " + this.getName());
  // return false;
  // }
  // }

  public synchronized boolean has(String path)
  {
    if (this.closed)
      return false;

    if (zip != null && zip.getEntry(path) != null)
      return true;

    File3 pathFile = new File3(path);
    if (pathFile.isAbsolute() && pathFile.exists())
      return true;

    if (file != null && new File3(file, path).exists())
      return true;

    return false;
  }

  public synchronized byte[] bytes(String path)
  {
    ZipEntry entry = this.zipEntry(path);
    return IO.ReadBytes(stream(entry), (int) entry.getSize());
  }

  public synchronized InputStream stream(ZipEntry entry)
  {
    InputStream stream = null;
    if (zip != null)
      try
      {
        stream = zip.getInputStream(entry);// check zip file if exists
      } catch (Exception ex)
      {
        Log.debug(this, ".stream - " + (zip == null ? "null" : zip.getName()) + ": " + (entry == null ? "null" : entry.getName()));
        ex.printStackTrace();
        return null;
      }
    if (stream == null)
    {
      File3 entryFile = new File3(entry.getName());
      // check absolute entry filename if exists
      if (entryFile.isAbsolute() && entryFile.exists())
        return entryFile.inputStream();
      else if (file != null)
      {
        File3 newFile = new File3(file, entry.getName());
        // check relative entry filename if exists
        if (newFile.exists())
          stream = new File3(file, entry.getName()).inputStream();
        else
          Log.debug(this, ".stream - file not found: " + newFile);
      }
    }
    return stream;
  }

  public synchronized InputStream stream(String path)
  {
    return stream(zipEntry(path));
  }
  
  public synchronized StringMap<ZipItem> map(boolean onlyFilename)
  {
    StringMap<ZipItem> map=new StringMap<>();
    for(ZipItem item:this)
      map.put(onlyFilename ? item.name() : item.path() ,  item);
    return map;
  }
  
  public synchronized List3<ZipItem> sortedList()
  {
    List3<ZipItem> list = list();
    Collections.sort(list);
    return list;
  }

  public synchronized List3<ZipItem> list()
  {
    List3<ZipItem> files = new List3<>();
    if (zip != null)
    {
      Enumeration<? extends ZipEntry> entries = zip.entries();
      ZipEntry entry = null;
      while (entries.hasMoreElements())
        if ((entry = entries.nextElement()) != null)
          files.add(new ZipItem(this, entry));
    } else if (file != null)
    {
      String root = file.path();
      for (File3 entry : file.listFiles(true))
        if (!entry.isDirectory() && !entry.isHidden())
          files.add(new ZipItem(this, new ZipEntry(entry.path().substring(root.length()))));
      // Log.debug(this, ".list - directory: " + files);
    }
    return files;
  }
  
  public ZipItem[] array()
  {
    return list().toArray(new ZipItem[0]);
  }

  @Override
  public Iterator<ZipItem> iterator()
  {
    return this.list().iterator();
  }

  public List3<ZipItem> list(boolean doSort)
  {
    List3<ZipItem> files = list();
    if (doSort)
      Collections.sort(files);
    return files;
  }

  public synchronized StringSet paths()
  {
    return this.paths(false);
  }

  public synchronized StringSet paths(boolean doSort)
  {
    StringSet paths = new StringSet();
    for (ZipItem entry : list(doSort))
      paths.add(entry.path());
    return paths;
  }

  public synchronized void handleZipItems(ZipItemHandler handler)
  {
    for (ZipItem entry : list())
      handler.handleZipItem(entry);
  }

  public synchronized boolean dispose()
  {
    try
    {
      this.closed = true;
      if (this.zip != null)
        this.zip.close();
      return true;
    } catch (IOException e)
    {
      e.printStackTrace();
      return false;
    }
  }

  public static Zip Get(File file)
  {
    return new Zip(file);
  }

  public static Zip Get(String path)
  {
    return path != null && File3.Exists(path) ? new Zip(path) : null;
  }

  public static boolean IsZip(File3 file)
  {
    return file.isExt(".zip", ".ZIP");
  }

}
