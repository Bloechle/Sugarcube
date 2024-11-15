package sugarcube.formats.ocd.objects.handlers;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Stringer;
import sugarcube.common.graphics.Image3;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.ZipItem;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.*;

public class OCDThumbHandler extends OCDHandler<OCDThumbEntry>
{
  public static final String TAG = "thumbs";
  public static final int DEFAULT_THUMB_SIZE = 300;
  public static final double DEFAULT_THUMB_QUALITY = 0.95;
  public int thumbSize = DEFAULT_THUMB_SIZE;
  public double thumbQuality = DEFAULT_THUMB_QUALITY;
  public ImageThumberCache thumber = new ImageThumberCache();

  public OCDThumbHandler(OCDDocument ocd)
  {
    super(TAG, ocd);
  }

  public byte[] image(String ref)
  {
    if (has(ref))
      return get(ref).data();
    else if (has(File3.Filename(ref)))
      return get(ref).data();

    Stringer ms = new Stringer();
    for (OCDThumbEntry entry : this)
      ms.append(entry.entryPath + ";");

    Log.warn(this, ".image - not found: " + ref + " in " + ms);
    return null;
  }

  public OCDThumbEntry get(OCDPage page)
  {
    return this.get(File3.Extense(page.entryFilename(), ".jpg"));
  }

  public OCDThumbEntry need(OCDPage page)
  {
    OCDThumbEntry entry = get(page);
    if (entry == null)
      Log.debug(this, ".need - null entry: page " + page.number());
    return entry == null ? add(page, false) : entry;
  }

  public OCDThumbEntry add(OCDPage page, boolean doCreate)
  {
    String entryPath = OCD.THUMBS_DIR + File3.Extense(page.entryFilename(), ".jpg");
    return doCreate || !page.doc().existsZipEntry(entryPath) ? update(page) : addEntry(entryPath);
  }

  public OCDThumbEntry update(OCDPage page)
  {
    Image3 preview = page.createImage(thumbSize, true, thumber);
    OCDThumbEntry entry = addEntry(preview.write(thumbQuality), File3.Extense(page.entryFilename(), ".jpg"));
    entry.setInMemory(true);
    return entry;
  }

  public OCDThumbEntry addEntry(OCDImage image)
  {
    return add(new OCDThumbEntry(this, image));
  }

  public OCDThumbEntry addEntry(byte[] data, String filename)
  {
    return add(new OCDThumbEntry(this, data, filename));
  }

  public OCDThumbEntry addEntry(String entryPath)
  {
    return add(new OCDThumbEntry(this, entryPath));
  }

  @Override
  public OCDThumbEntry addEntry(ZipItem entry)
  {
    return add(new OCDThumbEntry(this, entry));
  }

  public void freeFromMemory()
  {
    freeFromMemory(false);
  }

  public void freeFromMemory(boolean force)
  {
    for (OCDThumbEntry entry : this)
      entry.freeFromMemory(force);
  }


}
