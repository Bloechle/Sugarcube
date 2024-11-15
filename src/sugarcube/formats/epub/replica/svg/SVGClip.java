package sugarcube.formats.epub.replica.svg;

import sugarcube.common.data.collections.List3;
import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDClip;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;

public class SVGClip extends OCDNode
{
  public class SVGClipPath extends OCDNode
  {
    public static final String TAG = "path";
    private Path3 path;

    public SVGClipPath(Path3 path)
    {
      super(TAG, SVGClip.this);
      this.path = path;
    }

    @Override
    public Collection<? extends OCDNode> writeAttributes(Xml xml)
    {
      xml.write("d", OCD.path2xml(path, xml.numberFormat()));
      if (!path.isNonZero())
        xml.write("clip-rule", "evenodd");
      // xml.write("shape-rendering", "crispEdges");
      return this.children();
    }
  }

  public class SVGClipRect extends OCDNode
  {
    public static final String TAG = "rect";
    private Rectangle3 rect;

    public SVGClipRect(Rectangle3 rect)
    {
      super(TAG, SVGClip.this);
      this.rect = rect;
    }

    @Override
    public Collection<? extends OCDNode> writeAttributes(Xml xml)
    {
      xml.write("x", rect.x);
      xml.write("y", rect.y);
      xml.write("width", rect.width);
      xml.write("height", rect.height);
      xml.write("shape-rendering", "geometricPrecision");
      return this.children();
    }
  }

  public static final String TAG = "clipPath";
  public static final String NONE = OCDClip.ID_PAGE;
  private OCDNode clipPath;
  private String clipID;
  private SVGPage page;

  public SVGClip(OCDNode parent, SVGPage page, OCDClip clip, Rectangle3 viewBox, boolean trimClip)
  {
    super(TAG, parent);
    this.page = page;    
    Path3 path = clip.path().translate(-viewBox.x, -viewBox.y);
    // if (path.isBBox(0.1))
    // this.clipPath = new SVGClipRect(path.bounds());
    // else if (trimClip && path.isMultiBBox(0.1))
    // this.clipPath = new SVGClipRect(path.bounds());
    // else
    this.clipPath = new SVGClipPath(path);
    this.clipID = clip.id();
  }

  public SVGClip(SVGPage page, Rectangle3 rectangle, String clipID)
  {
    this(page, new Path3(rectangle), clipID);
  }

  public SVGClip(SVGPage page, Path3 path, String clipID)
  {
    super(TAG, page.defs());
    this.page = page;
    this.clipPath = new SVGClipPath(path);
    this.clipID = clipID;
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    if (clipID != null)
      xml.writeID(clipID + page.viewIndex());
    return this.children();
  }

  @Override
  public Collection<? extends OCDNode> children()
  {
    return new List3<OCDNode>(this.clipPath);
  }
}