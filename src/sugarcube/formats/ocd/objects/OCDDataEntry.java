package sugarcube.formats.ocd.objects;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Base;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.IO;
import sugarcube.formats.ocd.objects.document.OCDItem;

import java.io.*;
import java.util.Arrays;

public class OCDDataEntry extends OCDEntry
{
  protected byte[] data;

  public OCDDataEntry(String tag, OCDNode parent)
  {
    super(tag, parent);
  }

  public OCDDataEntry(String tag, OCDNode parent, String filepath)
  {
    super(tag, parent, filepath);
  }

  public OCDDataEntry(String tag, OCDNode parent, byte[] data, String filepath)
  {
    super(tag, parent, filepath);
    this.data = data;
    setInMemory(true);
  }

  public String type()
  {
    if (this.is(OCDImageEntry.TAG))
      return OCDItem.TYPE_IMAGE;
    else if (is(OCDThumbEntry.TAG))
      return OCDItem.TYPE_THUMB;
    else if (is(OCDAudioEntry.TAG))
      return OCDItem.TYPE_AUDIO;
    else if (is(OCDVideoEntry.TAG))
      return OCDItem.TYPE_VIDEO;
    else
      return OCDItem.TYPE_RESOURCE;
  }

  @Override
  public OCDItem item()
  {
    return new OCDItem(entryPath, type());// .addPages(pages);
  }

  public void setData(byte[] data)
  {
    this.data = data;
    if (data != null && data.length > 0)
      this.setInMemory(true);
  }

  @Override
  public String sticker()
  {
    return entryFilename();
  }

  public long dataSize()
  {
    return data == null ? 0 : data.length;
  }

  public byte[] data()
  {
    return data(true);
  }

  public byte[] data(boolean keepInMemory)
  {
    return data == null ? loadData(keepInMemory) : data;
  }

  public InputStream stream()
  {
    try
    {
      if (data == null)
      {
        File3 tmp = needTmp();
        if(tmp.exists())
          Log.debug(this,  ".stream - from tmp: "+tmp);
        return tmp.exists() ? tmp.inputStream() : IO.Buffered(document().zipStream(entryPath()));
      } else
        return new ByteArrayInputStream(data);

    } catch (Exception ex)
    {
      Log.error(this, ".stream - " + ex.getMessage());
      ex.printStackTrace();
    }
    return null;
  }

  public boolean exists()
  {
    return document().existsZipEntry(entryPath());
  }

  public byte[] loadData(boolean keepInMemory)
  {
    byte[] bytes = null;
    try
    {
      InputStream inputStream = null;
      File3 tmp = needTmp();
      if(tmp.exists())
        Log.debug(this,  ".loadData - from tmp: "+tmp);
      inputStream = tmp.exists() ? tmp.inputStream() : new BufferedInputStream(document().zipStream(entryPath()));
      bytes = IO.ReadBytes(inputStream);
      inputStream.close();
      if (keepInMemory)
      {
        this.data = bytes;
        this.setInMemory(true);
      }
      this.checksum = checksum(bytes);
    } catch (Exception ex)
    {
      Log.error(this, ".loadData - " + ex.getMessage());
      ex.printStackTrace();
    }
    return bytes;
  }

  @Override
  public boolean writeNode(OutputStream stream)
  {
    // OCD.LOG.debug(this,
    // ".writeNode - writing image: path="+this.entryFilepath()+",
    // size="+this.entryLength);

    if (data != null && data.length > 0)
      try
      {
        stream.write(data);
        return true;
      } catch (IOException ex)
      {
        Log.warn(this, ".writeNode - memory data writing exception " + entryPath + ": " + ex.getMessage());
      }
    else if (existsTmp())
    {
      Log.debug(this, ".writeNode - tmp data");
      try
      {
        stream.write(this.needTmp().bytes());
      } catch (IOException ex)
      {
        Log.warn(this, ".writeNode - tmp file data writing exception " + entryPath + ": " + ex.getMessage());
      }
    } else
      Log.debug(this, ".writeNode - empty data: " + entryPath);
    return false;
  }

  @Override
  public boolean readNode(InputStream stream)
  {
    data = IO.ReadBytes(stream);
    return true;
  }

  @Override
  public boolean ensureInMemory()
  {
    boolean inMem = this.data != null;
    if (!inMem)
      this.loadData(true);
    return inMem;
  }

  @Override
  public void freeFromMemory()
  {
    freeFromMemory(false);
  }

  public void freeFromMemory(boolean force)
  {
    if (force || !modified())
    {
      this.data = null;
      this.inMemory = false;
    }
  }

  @Override
  public void dispose()
  {
    this.data = null;
    this.inMemory = false;
  }

  public static String checksum(byte[] data)
  {
    return data == null ? "" : Base.x32.get(Arrays.hashCode(data)) + "-" + data.length;
  }

  @Override
  public String computeChecksum()
  {
    return this.isInMemory() ? checksum(data) : checksum;
  }

  @Override
  public String toString()
  {
    return "ImageEntry[" + entryPath() + ", inMemory=" + isInMemory() + ", modified=" + modified() + "]";
  }
}
