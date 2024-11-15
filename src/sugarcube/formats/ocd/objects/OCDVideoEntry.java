package sugarcube.formats.ocd.objects;

import sugarcube.common.system.io.ZipItem;
import sugarcube.formats.ocd.OCD;

public class OCDVideoEntry extends OCDDataEntry
{
  public static final String TAG = "video_entry";
  
  public OCDVideoEntry()
  {
    super(TAG, null);
  }

  public OCDVideoEntry(OCDNode parent, byte[] data, String filename)
  {
    super(TAG, parent, data, OCD.VIDEO_DIR + filename);    
  }

  public OCDVideoEntry(OCDNode parent, ZipItem zip)
  {
    super(TAG, parent, zip.path());
  }

  public OCDVideoEntry(OCDNode parent, OCDImage image)
  {
    super(TAG, parent, image.data(), OCD.AUDIO_DIR + image.filename());    
  }    
}
