package sugarcube.formats.ocd.objects;

import sugarcube.common.system.io.ZipItem;
import sugarcube.formats.ocd.OCD;

public class OCDAudioEntry extends OCDDataEntry
{
  public static final String TAG = "audio_entry";
  
  public OCDAudioEntry()
  {
    super(TAG, null);
  }

  public OCDAudioEntry(OCDNode parent, byte[] data, String filename)
  {
    super(TAG, parent, data, OCD.AUDIO_DIR + filename);    
  }

  public OCDAudioEntry(OCDNode parent, ZipItem zip)
  {
    super(TAG, parent, zip.path());
  }
//
//  public OCDAudioEntry(OCDNode parent, OCDImage image)
//  {
//    super(OCD.audio_entry, parent, image.data(), OCD.AUDIO_DIR + image.filename());    
//  }    

}
