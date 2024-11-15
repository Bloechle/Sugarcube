package sugarcube.formats.ocd.objects;

import sugarcube.common.data.json.Json;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.system.io.IO;
import sugarcube.common.system.io.ZipItem;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.document.OCDItem;
import sugarcube.formats.ocd.objects.handlers.OCDAddonHandler;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

public class OCDAddon extends OCDEntry
{
  public static final String TAG = "addon";
  protected XmlINode node;// if xml addon
  protected Object json;// if json addon
  protected byte[] data;// if not xml addon

  public OCDAddon(OCDAddonHandler parent)
  {
    this(parent, "addon.xml");
  }

  public OCDAddon(OCDAddonHandler parent, String filename)
  {
    this(parent, filename, null);
  }

  public OCDAddon(OCDAddonHandler parent, String filename, XmlINode node)
  {
    super(TAG, parent, OCD.ADDONS_DIR + filename);
    this.node = node;
    if (node instanceof OCDNode)
      ((OCDNode) node).setParent(this);
  }

  public OCDAddon(OCDAddonHandler parent, ZipItem zipEntry)
  {
    super(TAG, parent, zipEntry);
  }

  @Override
  public OCDItem item()
  {
    return new OCDItem(entryPath, OCDItem.TYPE_ADDON);
  }

  public XmlINode xmlNode()
  {
    return this.node;
  }

  public OCDAddon setXmlNode(XmlINode node)
  {
    this.node = node;
    if (node instanceof OCDNode)
      ((OCDNode) node).setParent(this);
    return this;
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    return this.children();
  }

  @Override
  public void readAttributes(DomNode e)
  {
  }

  @Override
  public XmlINode newChild(DomNode child)
  {
    return null;
  }

  @Override
  public void endChild(XmlINode child)
  {
  }

  @Override
  public String sticker()
  {
    return tag + "[" + this.entryFilename() + "]";
  }

  @Override
  public void paint(Graphics3 g, OCD.ViewProps props)
  {
    // OCDPlugPainter.paintBounds(g, bounds, props.displayTransform(),
    // Color3.GREEN, (type == null ? "" : type + " ") + (name == null ? "" :
    // name + " ") + (data == null ? "" : data + " "));
    // g.setStroke(new BasicStroke(1));
    // g.setColor(new Color(0, 150, 0, 20));
    // g.fill(bounds());
    // g.setColor(new Color(0, 150, 0));
    // g.draw(bounds(props.displayTransform()));
  }

  @Override
  public boolean writeNode(OutputStream stream)
  {
    if (this.node != null)
      return Xml.write(node, stream, 1, true);
    else if (this.data != null)
      return IO.bytesToOutputStream(data, stream);
    else if (this.json != null)
      IO.writeText(Json.Stringify(this), stream, false);
    return false;
  }

  @Override
  public boolean readNode(InputStream stream)
  {
    if (this.node != null)
      Xml.Load(node, stream);
    else if (this.data != null)
      this.data = IO.ReadBytes(stream);
    else if (this.json != null)
    {
      Json.Populate(IO.ReadText(stream), this);
      this.setInMemory(true);
    }
    return true;
  }

}
