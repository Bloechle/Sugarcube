package sugarcube.formats.pdf.reader.pdf.node;

import sugarcube.common.data.collections.List3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Image3;
import sugarcube.common.system.io.File3;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.object.PDF;

public class PDFContext
{
  public int counter = 0;
  public PDFDisplayProps props = null;
  public Image3 context = null;
  public Graphics3 g = null;
  public PDFContent content;
  public Rectangle3 bbox;
  public List3<PDFNode> nodes = new List3<PDFNode>();

  public PDFContext(PDFContent content)
  {
    this.content = content;
    this.bbox = content.bbox().rectangle();
    this.props = content.document().displayProps.copy(bbox);
    this.props.displayScaling = props.contextScaling;
  }

  public PDFContext write()
  {
    if (context != null)
    {
      context.writePng(File3.desktop("tmp/ctx-" + content.reference + "_" + (counter++) + ".png"));
    }
    return this;
  }

  public PDFContext add(PDFNode node)
  {
    if (g == null)
      nodes.add(node);
    else
      node.paint(g, props);
    return this;
  }

  private Image3 ctx()
  {
    if (context == null)
    {
      Transform3 t3 = props.displayTransform();
      Rectangle3 r = t3.transform(bbox).bounds();
      this.context = PDF.ImageARGB(r);
      this.g = context.graphics();
      g.clearWhite();
      g.setTransform(t3);
      g.setClip(null);
      for (PDFNode node : nodes)
        node.paint(g, props);
    }
    return context;
  }

  public Image3 subImage(Rectangle3 r, double ratio)
  {
    Image3 sub = PDF.ImageARGB(r);
    Graphics3 gSub = sub.graphics();
    gSub.draw(ctx(), new Transform3(ratio, 0, 0, ratio, -r.x * ratio, -r.y * ratio));
    gSub.dimension();
    return sub;
  }
}
