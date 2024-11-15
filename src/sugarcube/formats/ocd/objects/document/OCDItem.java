package sugarcube.formats.ocd.objects.document;

import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Image3;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDNode;
import sugarcube.formats.ocd.objects.font.SVGFont;

import java.util.Collection;

public class OCDItem extends OCDNode
{
  public static final String TAG = "item";
  public static final String TYPE_MIME = "mime";
  public static final String TYPE_PAGE = "page";
  public static final String TYPE_FONT = "font";
  public static final String TYPE_IMAGE = "image";
  public static final String TYPE_THUMB = "thumb";
  public static final String TYPE_VIDEO = "video";
  public static final String TYPE_AUDIO = "audio";
  public static final String TYPE_ADDON = "addon";
  public static final String TYPE_RESOURCE = "resource";      
  protected String filepath;
  protected String type;


  public OCDItem(OCDNode parent)
  {
    super(TAG, parent);
  }

  public OCDItem(OCDNode parent, String filepath, String type, String... properties)
  {
    super(TAG, parent);    
    this.filepath = filepath;
    this.type = type;
    if (properties.length > 0)
      this.props.putAll(properties);
  }

  public OCDItem(String filepath, String type, String... properties)
  {
    this(null, filepath, type, properties);
  }

  public String type()
  {
    return this.type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public boolean isType(String type)
  {
    return this.type.equals(type);
  }

  public boolean isPageType()
  {
    return this.type.equals(TYPE_PAGE);
  }

  public boolean isFontType()
  {
    return this.type.equals(TYPE_FONT);
  }

  public boolean isImageType()
  {
    return this.type.equals(TYPE_IMAGE);
  }
  
  public boolean isThumbType()
  {
    return this.type.equals(TYPE_THUMB);
  }  

  public boolean isAudioType()
  {
    return this.type.equals(TYPE_AUDIO);
  }

  public boolean isVideoType()
  {
    return this.type.equals(TYPE_VIDEO);
  }

  public boolean isAddonType()
  {
    return this.type.equals(TYPE_ADDON);
  }

  public boolean isResourceType()
  {
    return this.type.equals(TYPE_RESOURCE);
  }

  public String filePath()
  {
    return this.filepath;
  }

  public void setFilePath(String filepath)
  {
    this.filepath = filepath;
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    xml.write("file", filepath);
    xml.write("type", type);    
    props.writeAttributes(xml);
    return this.children();
  }

  @Override
  public void readAttributes(DomNode dom)
  {
    this.type = dom.value("type");
    this.filepath = dom.value("file");   
    props.readAttributes(dom);
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
    String desc = "";
    if (this.isFontType())
    {
      SVGFont font = this.document().fontHandler.font(filepath);
      if (font != null)
        desc = ", italic=" + font.isItalic() + ", bold=" + font.isBold();
    }
    return type + "[" + filepath + "]" + desc;
  }

  @Override
  public String toString()
  {
    if (this.isFontType())
    {
      SVGFont font = this.document().fontHandler.font(filepath);
      if (font != null)
        return font.toString();
    }
    return this.getClass().getSimpleName();
  }

  @Override
  public Rectangle3 bounds()
  {
    if (this.isFontType())
    {
      int nbGlyphs = this.document().fontHandler.font(filepath).nbOfGlyphs() + 1;
      int w = 900; //(int)this.page().bounds().width;
      int h = (nbGlyphs * SVGFont.CELL_SIZE / w + 1) * SVGFont.CELL_SIZE;
      return new Rectangle3(0, 0, w, h);
    }
    else
      return super.bounds();
  }

  @Override
  public void paint(Graphics3 g, OCD.ViewProps props)
  {
    if (this.isFontType())
    {
      SVGFont font = this.document().fontHandler.font(filepath);
      if (font != null)
        font.paint(g, props);
    }
    else if (this.isImageType())
      g.draw(Image3.Read(this.document().imageHandler.data(filepath)), null);
    else if (this.isPageType())
      this.document().pageHandler.get(filepath).paint(g, props);
  }

  @Override
  public OCDItem copy()
  {
    OCDItem copy = new OCDItem((OCDNode) parent, filepath, type, props.array());
    super.copyTo(copy);
    return copy;
  }
}
