package sugarcube.formats.ocd.objects;

import sugarcube.common.data.collections.StringMap;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.formats.ocd.OCD;

import java.util.Collection;
import java.util.Iterator;

public class OCDStyles extends OCDEntry implements Iterable<OCDStyle>
{
  private static final String TAG = "styles";

  private StringMap<OCDStyle> map = new StringMap<OCDStyle>();

  public OCDStyles(OCDNode parent)
  {
    super(TAG, parent);
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    return null;
  }

  @Override
  public void readAttributes(DomNode e)
  {

  }

  @Override
  public XmlINode newChild(DomNode child)
  {
    return new OCDStyle(this);
  }

  @Override
  public void endChild(XmlINode child)
  {
    if (child == null)
      return;
    else
      this.add((OCDStyle) child);
  }

  public void add(OCDStyle style)
  {
  }

  @Override
  public Collection<? extends OCDStyle> children()
  {
    // used by treezable to visit tree
    return this.map.values();
  }

  @Override
  public Iterator<OCDStyle> iterator()
  {
    return this.map.values().iterator();
  }

  public void write(StringBuilder sb)
  {
    Xml xml = new Xml(sb);
    xml.write(this);
    return;
  }

  @Override
  public String sticker()
  {
    return tag();
  }

  @Override
  public void paint(Graphics3 g, OCD.ViewProps props)
  {
  }

  @Override
  public OCDStyles copy()
  {
    OCDStyles styles = new OCDStyles(parent());
    for (OCDStyle style : this)
      styles.add(style);
    return styles;
  }

  @Override
  public String toString()
  {
    return Xml.toString(this);
  }
}
