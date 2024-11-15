package sugarcube.formats.epub.replica.svg;

import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.objects.OCDImage;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;

public class SVGImage extends SVGPaintable
{
  public static final String TAG = "image";
  protected String filepath;
  protected int width;
  protected int height;
  protected Transform3 tm;
  protected String alt = "";
  protected String classname = "";

  public SVGImage(OCDNode parent, SVGPage page, OCDImage image, String filepath, Rectangle3 viewBox)
  {
    this(parent, page, image.width(), image.height(), image.transform().translateBack(viewBox.origin()), image.svgBlendMode(),
        image.hasClip() ? image.clipID() : SVGClip.NONE, filepath);
    this.classname = image.classname();
  }

  public SVGImage(OCDNode parent, SVGPage page, int width, int height, Transform3 tm, String blend, String clipID, String filepath)
  {
    super(TAG, parent, page);
    this.filepath = filepath;
    this.width = width;
    this.height = height;
    this.tm = tm;
    this.blend = blend == null ? "" : blend;
    this.clipID = clipID == null || clipID.isEmpty() ? SVGClip.NONE : clipID;
  }
  
  

  public String filepath()
  {
    return filepath;
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    // xml.write("clip-path", !clipID.equals(OCDClip.REF_NONE) ? "url(#" +
    // clipID + ")" : null);
    xml.write("xlink:href", filepath);
    xml.write("width", width);
    xml.write("height", height);
    xml.write("class", classname);
    if (!tm.isIdentity())
      xml.write("transform", "matrix(" + xml.toString(tm.floatValues()) + ")");
    this.writeXmlBlend(xml);
    return this.children();
  }
}
