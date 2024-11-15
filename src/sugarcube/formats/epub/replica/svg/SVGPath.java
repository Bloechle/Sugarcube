package sugarcube.formats.epub.replica.svg;

import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDNode;
import sugarcube.formats.ocd.objects.OCDPath;

import java.awt.*;
import java.util.Collection;

public class SVGPath extends SVGPaintable
{  
  private Transform3 transform;
  private Shape path;
  private boolean isNonzero;

  public SVGPath(OCDNode parent, SVGPage page, OCDPath ocdPath, Rectangle3 viewBox)
  {
    super("path", parent, page);
    this.path = ocdPath.path();
    this.clipID = ocdPath.hasClip() ? ocdPath.clipID() : SVGClip.NONE;
    this.transform = ocdPath.transform();
    this.blend = ocdPath.svgBlendMode();
    if (!ocdPath.transform().isScaledOrSheared(0.001))
    {
//      Log.debug(this, " - path before: " + (new Path3(path)).stringValue());
//      Log.debug(this, " - clip: " + ocdPath.clip().path().stringValue());
      this.path = this.transform.shapeTransform(this.path);

//      if (ocdPath.hasClip())
//        ((Area) (path = new Area(this.path))).intersect(new Area(ocdPath.clip().path()));
//      this.clipID = SVGClip.NONE;
//      Log.debug(this, " - path after: " + (new Path3(path)).stringValue());
      this.path = transform.translateBack(viewBox.origin()).transform(path);
      this.transform = new Transform3();
    }
    else
      this.transform = transform.translateBack(viewBox.origin());
    this.setFillColor(ocdPath.fillColor());
    this.setStroke(ocdPath.strokeColor(), ocdPath.isStroked() ? ocdPath.stroke() : null, 1);
    if(this.hasBlend())
      this.setBlendMode(this.blend);
    this.isNonzero = ocdPath.isNonZero();
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
//    xml.write("clip-path", !clipID.equals(OCDClip.REF_NONE) ? "url(#" + clipID + ")" : null);
    String sPath = OCD.path2xml(path, xml.numberFormat());
    xml.write("d", sPath.trim().isEmpty() ? "m 0 0" : sPath);
    if (!isNonzero)
      xml.write("fill-rule", "evenodd");
    if (!transform.isIdentity())
      xml.write("transform", "matrix(" + xml.toString(transform.floatValues()) + ")");
    this.writeXmlBlend(xml);
    this.writeXmlClasses(xml);
    return this.children();
  }
}
