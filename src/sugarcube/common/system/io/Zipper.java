package sugarcube.common.system.io;

import sugarcube.common.system.log.Log;
import sugarcube.formats.ocd.objects.OCDEntry;

import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper extends ZipOutputStream
{
  public static final Charset UTF8 = Charset.forName("UTF-8");
  public static final String MIMETYPE = "mimetype";

  public Zipper(File file) throws FileNotFoundException
  {
    this(file, true);
  }
  
  public Zipper(File file, boolean doCompress) throws FileNotFoundException
  {
    this(new FileOutputStream(File3.Wrap(file).needDirs()), doCompress);
  }
  
  public Zipper(OutputStream stream)
  {
    this(stream, true);
  }

  public Zipper(OutputStream stream, boolean doCompress)
  {
    super(stream instanceof BufferedOutputStream ? stream : new BufferedOutputStream(stream));    
    this.setMethod(doCompress ? ZipOutputStream.DEFLATED : ZipOutputStream.STORED);
    this.setLevel(doCompress ? Deflater.BEST_COMPRESSION : Deflater.BEST_SPEED);
  }

  public Zipper writeMimeZip()
  {
    return writeMime(" application/zip ");
  }

  public Zipper writeMimeOCD()
  {
    return writeMime(" application/ocd+zip ");
  }

  public Zipper writeMime(String mimetype)
  {
    try
    {
      ZipEntry entry = new ZipEntry(MIMETYPE);
      entry.setMethod(ZipEntry.STORED);
      entry.setSize(mimetype.length());
      entry.setCompressedSize(mimetype.length());
      CRC32 crc = new CRC32();
      crc.reset();
      crc.update(mimetype.getBytes());
      entry.setCrc(crc.getValue());
      this.putNextEntry(entry);
      this.write(mimetype.getBytes(), 0, mimetype.length());
      this.closeEntry();
    } catch (IOException e)
    {
      e.printStackTrace();
      return null;
    }
    return this;
  }

  public Zipper putNextEntry(String entryPath) throws IOException
  {
    this.putNextEntry(new ZipEntry(entryPath));
    return this;
  }

  public Zipper disposeEntry() throws IOException
  {
    this.flush();
    this.closeEntry();
    return this;
  }

  public Zipper addEntry(String entryPath, OCDEntry entry) throws IOException
  {
    if (entry == null)
      return this;
    this.putNextEntry(entryPath);
    entry.writeEntry(this);
    this.disposeEntry();
    return this;
  }

  public Zipper addEntry(String entryPath, byte[] data) throws IOException
  {
    if (data == null)
      return this;
    this.putNextEntry(entryPath);
    this.write(data, 0, data.length);
    this.disposeEntry();
    return this;
  }

  public Zipper addEntry(String entryPath, InputStream in) throws IOException
  {
    if (in == null)
      return this;
    this.putNextEntry(entryPath);
    byte[] buf = new byte[IO.BufferSize(in)];
    int length;
    while ((length = in.read(buf)) > 0)
      this.write(buf, 0, length);
    in.close();
    this.disposeEntry();
    return this;
  }

  public Zipper addEntry(String entryPath, String data)
  {
    try
    {
      this.putNextEntry(entryPath);
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this, UTF8)); // "UTF-8"
      writer.write(data);
      writer.flush();
      writer = null;
      this.disposeEntry();
    } catch (IOException e)
    {
      Log.warn(this, ".addEntry - error: " + e);
      e.printStackTrace();
    }
    return this;
  }

  public Zipper addEntry(String entryPath, File3 file)
  {
    return addFile(entryPath, file);
  }

  public Zipper addFile(String entryPath, File3 file)
  {
    try
    {
      this.addEntry(entryPath, file.inputStream());
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return this;
  }

  public Zipper zipFile(File3 file)
  {
    return file.isDirectory() ? zipFolder(file) : addFile(file.name(), file);
  }

  public Zipper zipFolder(File folder)
  {
    File3 dir = new File3(folder);
    for (File3 file : dir.listFiles(true))
      if (!file.isDirectory())
        this.addFile(file.relativeTo(dir), file);
    return this;
  }

  public boolean dispose()
  {
    try
    {
      this.finish();
      this.flush();
      this.close();
      return true;
    } catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return false;
  }

  public static Zipper Get(String path)
  {
    return Get(File3.Get(path));
  }

  public static Zipper Get(File file)
  {
    try
    {
      return new Zipper(file);
    } catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  public static Zipper OCD(File file)
  {
    Zipper zipper = null;
    try
    {
      if ((zipper = new Zipper(file)) != null)
        zipper.writeMimeOCD();
    } catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    return zipper;
  }
}
