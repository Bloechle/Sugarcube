package sugarcube.formats.ocd.objects.handlers;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Stringer;
import sugarcube.common.system.io.ZipItem;
import sugarcube.formats.ocd.objects.OCDAudioEntry;
import sugarcube.formats.ocd.objects.OCDDocument;

public class OCDAudioHandler extends OCDHandler<OCDAudioEntry>
{
  public static final String TAG = "audios";
  
  public OCDAudioHandler(OCDDocument ocd)
  {
    super(TAG, ocd);
  }

  public byte[] audio(String ref)
  {
    if (this.has(ref))
      return this.get(ref).data();

    Stringer ms = new Stringer();
    for (OCDAudioEntry entry : this)
      ms.append(entry.entryPath + ";");

    Log.warn(this, ".audio - not found: " + ref + " in " + ms);
    return null;
  }

  public OCDAudioEntry addEntry(byte[] data, String filename)
  {
    return this.add(new OCDAudioEntry(this, data, filename));
  }

  @Override
  public OCDAudioEntry addEntry(ZipItem entry)
  {
    return this.add(new OCDAudioEntry(this, entry));
  }

  public void freeFromMemory()
  {
    freeFromMemory(false);
  }

  public void freeFromMemory(boolean force)
  {
    for (OCDAudioEntry entry : this)
      entry.freeFromMemory(force);
  }

}
