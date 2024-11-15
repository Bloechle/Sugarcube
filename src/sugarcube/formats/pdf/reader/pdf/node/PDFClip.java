package sugarcube.formats.pdf.reader.pdf.node;

import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.formats.pdf.reader.Dexter;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.object.StreamLocator;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

public class PDFClip extends PDFNode<PDFNode>
{
  // used for backtracking (debugging)
  protected StreamLocator streamLocator;
  protected Path3 path;
  protected boolean doClip = true;

  private PDFClip(PDFClip pdfClip)
  {
    super(Dexter.CLIP, pdfClip.parent);
    this.path = pdfClip.path.copy();
    this.streamLocator = pdfClip.streamLocator;
  }

  public PDFClip(PDFNode parent)
  {
    this(parent, new Rectangle3());
  }

  public PDFClip(PDFNode parent, boolean doClip)
  {
    this(parent, new Path3());
    this.doClip = doClip;
  }

  public PDFClip(PDFNode parent, Rectangle3 rectangle)
  {
    super(Dexter.CLIP, parent);
    this.path = new Path3(rectangle);
  }

  public PDFClip(PDFNode parent, Path3 path)
  {
    super(Dexter.CLIP, parent);
    this.path = path;
  }

  public PDFClip(PDFPath pdfPath, PDFState state, PDFInstr pdfOp)
  {
    super(Dexter.CLIP, pdfPath);
    this.path = pdfPath.path.transform(state.ctm().transform());

    this.streamLocator = pdfPath.streamLocator;
    this.path.closePath();

    if (this.path.isBBox(0.001))
    { 
      Rectangle3 r = this.path.bounds();
      // in order to avoid splitted images to suffer from clipping side effects
      this.path = new Path3(new Rectangle3(r.x, r.y, r.width, r.height));
    }

    int rule = pdfPath.path.getWindingRule();

    if (state.clip != null && !state.clip.isEmpty())
      if (this.path.contains(state.clip.path.getBounds()))
        this.path = state.clip.path;
      else if (!state.clip.path.contains(this.path.getBounds()))
      {
        if (this.path.nbOfSegments(true, true) < 100 || state.clip.path.nbOfSegments(true, true) < 100)
        {
          Area area = new Area(this.path);          
          area.intersect(new Area(state.clip.path));
          this.path = new Path3(area);
        }
        else
        {    
          Log.debug(this, " - complex intersection trimmed:  " + this.path.nbOfSegments(true, true) + ", " + state.clip.path.nbOfSegments(true, true));          
          this.path = state.clip.path;
        }
      }
    // we keep this path unmodified    
    
    this.path.setWindingRule(rule);

    if (this.path.isEmpty())
      this.path = new Path3(new Rectangle3(0, 0, 0, 0));

    // Zen.LOG.debug(this, " - isBBox=" + this.path.isBBox(0.001) + ", rule=" +
    // rule + ", path=" + path.stringValue());
  }

  @Override
  public PDFClip instance(PDFContent content, PDFInstr instr, PDFContext context)
  {
    PDFState currentState = document().content().state();
    PDFClip copy = copy();
    copy.path = copy.path.transform(currentState.ctm.transform());
    this.streamLocator = instr.streamLocator();
    return copy;
  }

  public boolean isEmpty()
  {
    return this.path.isEmpty();
  }

  @Override
  public StreamLocator streamLocator()
  {
    return streamLocator;
  }

  public PathIterator getPathIterator()
  {
    return path.getPathIterator(null);
  }

  public int getClipRule()
  {
    return path.getWindingRule();
  }

  public boolean doClip()
  {
    return this.doClip;
  }

  public PDFClip copy()
  {
    PDFClip clip = new PDFClip(this);
    return clip;
  }

  public Path3 path()
  {
    return path;
  }

  public Path3 path(Rectangle2D bounds)
  {
    return new Path3(new Transform3(1, 0, 0, -1, -bounds.getMinX(), bounds.getMaxY()).transform(path));
  }

  @Override
  public String sticker()
  {
    java.awt.Rectangle r = path.getBounds();
    return id + " » " + type + "[x" + r.x + " y" + r.y + " w" + r.width + " h" + r.height + "]";
  }

  @Override
  public String toString()
  {
    Rectangle r = path.getBounds();
    return type + "[x" + r.x + " y" + r.y + " w" + r.width + " h" + r.height + "]" + "\nData[" + path.stringValue(0.001f) + "]";
  }

  @Override
  public void paint(Graphics3 g, PDFDisplayProps props)
  {
    if (props.highlightClips)
    {
      g.setClip(null);
      g.setStroke(new BasicStroke(1));
      g.setColor(new Color(0, 150, 0, 5));
      g.fill(path(props.pageBounds));
      g.setColor(new Color(0, 150, 0));
      g.draw(path(props.pageBounds));
    }
    g.setClip(props.enableClipping ? (this.doClip ? path(props.pageBounds) : null) : null);
  }

  public boolean sameClippingPath(PDFClip clip)
  {
    return this == clip || this.path == clip.path || this.path.equalsPath(clip.path);
  }
}
