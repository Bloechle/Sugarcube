package sugarcube.formats.ocd.objects;

import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Image3;
import sugarcube.common.system.io.ZipItem;
import sugarcube.formats.ocd.OCD;

public class OCDImageEntry extends OCDDataEntry
{
  public static final String TAG = "image_entry";

  public OCDImageEntry()
  {
    super(TAG, null);
  }

  public OCDImageEntry(OCDNode parent, byte[] data, String filename)
  {
    super(TAG, parent, data, OCD.IMAGES_DIR + filename);    
  }

  public OCDImageEntry(OCDNode parent, ZipItem zip)
  {
    super(TAG, parent, zip.path());
  }

  public OCDImageEntry(OCDNode parent, OCDImage image)
  {
    super(TAG, parent, image.data(), OCD.IMAGES_DIR + image.filename());    
  }
  
//  public OCDImageEntry(OCDNode parent, OCDImage image, OCDPage page)
//  {
//    super(OCD.image_entry, parent, image.data(), OCD.IMAGES_DIR + image.filename());    
////    this.pages.add(page.entryFilename());
//  }  
  
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
