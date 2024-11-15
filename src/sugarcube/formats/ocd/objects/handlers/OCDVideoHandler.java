package sugarcube.formats.ocd.objects.handlers;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Stringer;
import sugarcube.common.system.io.ZipItem;
import sugarcube.formats.ocd.objects.OCDDocument;
import sugarcube.formats.ocd.objects.OCDVideoEntry;

public class OCDVideoHandler extends OCDHandler<OCDVideoEntry>
{
  public static final String TAG = "videos";
  
  public OCDVideoHandler(OCDDocument ocd)
  {
    super(TAG, ocd);
  }

  public byte[] video(String ref)
  {
    if (this.has(ref))
      return this.get(ref).data();

    Stringer ms = new Stringer();
    for (OCDVideoEntry entry : this)
      ms.append(entry.entryPath + ";");

    Log.warn(this, ".video - not found: " + ref + " in " + ms);
    return null;
  }

  public OCDVideoEntry addEntry(byte[] data, String filename)
  {
    return this.add(new OCDVideoEntry(this, data, filename));
  }

  @Override
  public OCDVideoEntry addEntry(ZipItem entry)
  {
    return this.add(new OCDVideoEntry(this, entry));
  }

  public void freeFromMemory()
  {
    freeFromMemory(false);
  }

  public void freeFromMemory(boolean force)
  {
    for (OCDVideoEntry entry : this)
      entry.freeFromMemory(force);
  }

}
