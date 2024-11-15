package sugarcube.formats.ocd.objects.handlers;

import sugarcube.common.system.log.Log;
import sugarcube.common.system.io.IO;
import sugarcube.common.system.io.ZipItem;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.formats.ocd.objects.OCDDocument;
import sugarcube.formats.ocd.objects.OCDAddon;

public class OCDAddonHandler extends OCDHandler<OCDAddon>
{
  public static final String TAG = "addons";
  
  public OCDAddonHandler(OCDDocument ocd)
  {
    super(TAG, ocd);
  }

  @Override
  public OCDAddon addEntry(ZipItem entry)
  {
    return this.add(new OCDAddon(this, entry));
  }

  public boolean hasAddon(String filename)
  {
    return this.map.has(filename);
  }

  public OCDAddon addAddon(String filename, XmlINode node)
  {
    if (node == null)
      return null;
    else
    {
      OCDAddon addon = new OCDAddon(this, filename, node);
      this.map.put(filename, addon);
      return addon;
    }
  }

  public OCDAddon loadAddon(String filename, XmlINode node)
  {
    OCDAddon addon = loadAddon(has(filename) ? get(filename).setXmlNode(node) : new OCDAddon(this, filename, node));
    if (addon != null)
      this.map.put(filename, addon);
    return addon;
  }

  public OCDAddon loadAddon(OCDAddon addon)
  {
    try
    {
      IO.Close(Xml.Load(addon.xmlNode(), ocd.zipStream(addon.entryPath())));
      return addon;
    }
    catch (Exception ex)
    {
      Log.error(this, ".loadAddon - exception: " + addon.entryPath());
      ex.printStackTrace();
    }
    return null;
  }
}
