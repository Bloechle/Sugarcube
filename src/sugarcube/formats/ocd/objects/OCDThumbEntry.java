package sugarcube.formats.ocd.objects;

import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Image3;
import sugarcube.common.system.io.ZipItem;
import sugarcube.formats.ocd.OCD;

public class OCDThumbEntry extends OCDDataEntry
{
  public static final String TAG = "thumb-entry";

  public OCDThumbEntry()
  {
    super(TAG, null);
  }

  public OCDThumbEntry(OCDNode parent, String filePath)
  {
    super(TAG, parent, filePath);
  }

  public OCDThumbEntry(OCDNode parent, ZipItem zip)
  {
    super(TAG, parent, zip.path());
  }

  public OCDThumbEntry(OCDNode parent, byte[] data, String filename)
  {
    super(TAG, parent, data, OCD.THUMBS_DIR + filename);
  }

  public OCDThumbEntry(OCDNode parent, OCDImage image)
  {
    super(TAG, parent, image.data(), OCD.THUMBS_DIR + image.filename());
  }

  public Image3 image()
  {
    return Image3.Read(data());
  }

  @Override
  public void paint(Graphics3 g, OCD.ViewProps props)
  {
    g.draw(image());
  }
}
