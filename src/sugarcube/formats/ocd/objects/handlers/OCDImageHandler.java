package sugarcube.formats.ocd.objects.handlers;

import sugarcube.common.system.log.Log;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.ZipItem;
import sugarcube.formats.ocd.objects.OCDDocument;
import sugarcube.formats.ocd.objects.OCDImage;
import sugarcube.formats.ocd.objects.OCDImageEntry;

import java.io.InputStream;

public class OCDImageHandler extends OCDHandler<OCDImageEntry>
{
  public static final String TAG = "images";

  public OCDImageHandler(OCDDocument ocd)
  {
    super(TAG, ocd);
  }

  @Override
  public boolean has(String ref)
  {
    if (map.has(ref))
      return true;
    if (map.has(ref = File3.Filename(ref, true)))
      return true;
    if (map.has(ref + ".png"))
      return true;
    return map.has(ref + ".jpg");
  }

  @Override
  public OCDImageEntry get(String ref)
  {
    if (map.has(ref))
      return map.get(ref);
    if (map.has(ref = File3.Filename(ref, true)))
      return map.get(ref);
    if (map.has(ref + ".png"))
      return map.get(ref + ".png");
    return map.get(ref + ".jpg");
  }

  public byte[] data(String ref)
  {
    return data(ref, false);
  }

  private byte[] data(String ref, boolean keepInMemory)
  {
    OCDImageEntry entry = this.get(ref);
    if (entry != null)
      return entry.data(keepInMemory);
    Log.warn(this, ".data - not found: " + ref);
    return null;
  }

  public InputStream stream(String ref)
  {
    OCDImageEntry entry = this.get(ref);
    if (entry != null)
      return entry.stream();
    Log.warn(this, ".stream - not found: " + ref);
    return null;
  }

  public OCDImageEntry addEntry(OCDImage image)
  {
    return image.isView() ? null : this.add(new OCDImageEntry(this, image));
  }

  public OCDImageEntry addEntry(byte[] data, String filename)
  {
    return this.add(new OCDImageEntry(this, data, filename));
  }

  @Override
  public OCDImageEntry addEntry(ZipItem entry)
  {
    return this.add(new OCDImageEntry(this, entry));
  }

  public void freeFromMemory()
  {
    freeFromMemory(false);
  }

  public void freeFromMemory(boolean force)
  {
    for (OCDImageEntry entry : this)
      entry.freeFromMemory(force);
  }

  public long bytesAllocated()
  {
    long bytes = 0;
    for (OCDImageEntry entry : this)
    {
      bytes += entry.dataSize();
    }
    return bytes;
  }

}
