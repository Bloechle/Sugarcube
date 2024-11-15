package sugarcube.formats.ocd.objects;

import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.StringSet;
import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Stroke3;
import sugarcube.common.data.Clipboard;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.OCD;

import java.awt.geom.Point2D;
import java.util.Collection;

public class OCDPageContent extends OCDContent
{
  
  private transient State state;

  public OCDPageContent(OCDPage parent)
  {
    super(null, parent, OCDContent.TAG);
    this.isTreeViewExpanded = true;
  }
  
  public boolean isPageContent()
  {
    return true;
  }

  public OCDPaintable identify(String id, String... tags)
  {
    return (OCDPaintable) this.identify(id, new StringSet(tags));
  }

  @Override
  public OCDPageContent clear()
  {
    this.nodes.clear();
    this.state = null;
    return this;
  }

  public OCDPageContent removeTables()
  {
    for (OCDTable table : tables())
      removeTable(table);
    return this;
  }
  
  public void removeTable(OCDTable table)
  {
    for (OCDTableCell cell : table)
      addAll(cell.children());
    remove(table);
  }

  public State state()
  {
    return this.state == null ? this.state = new State() : this.state;
  }

  public void initState()
  {
    state().clear();
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    this.initState();
    return this.children();
  }

  @Override
  public void readAttributes(DomNode e)
  {
    this.initState();
  }

  @Override
  public Rectangle3 bounds()
  {
    return page().bounds();
  }

  @Override
  public String sticker()
  {
    return "PageContent["+this.nbOfChildren()+"]";
  }


  public OCDPaintable writeToClipboard(Point2D p, OCD.ViewProps props)
  {
    OCDPaintable node = paintAt(p);
    if (node != null)
      if (OCD.isTextBlock(node))
        Clipboard.clip(node.asTextBlock().stringValue(true));
      else if (node.isImage())
        Clipboard.clip(node.asImage().image3());
      else
        Clipboard.clip(createImage(props, Color3.WHITE));
    return node;
  }

  public class State
  {
    public OCDTextLine LINE;
    public String CLIP, BLEND, CAP, JOIN, WIND, FONT, MODE;
    public float X, Y, Z, R, PEN, FONTSIZE, PHASE;
    public float[] SCALE, SHEAR, DASH;
    public int FILL, STROKE;

    public State()
    {
      this.clear();
    }

    public final State clear()
    {
      this.LINE = null;
      this.CLIP = BLEND = null;
      this.X = Y = Z = 0;
      this.SCALE = Zen.Array.Floats(1, 1);
      this.SHEAR = Zen.Array.Floats(0, 0);
      this.FILL = Color3.BLACK.argb();
      this.STROKE = Color3.BLACK.argb();
      this.PEN = 1;
      this.CAP = Stroke3.BUTT;
      this.JOIN = Stroke3.ROUND;
      this.DASH = new float[0];
      this.PHASE = 0;
      this.WIND = Path3.NONZERO;
      this.FONT = null;
      this.FONTSIZE = 0;
      this.MODE = OCDText.MODE_LTR;
      return this;
    }
  }
}
