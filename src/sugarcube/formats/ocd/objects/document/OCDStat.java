package sugarcube.formats.ocd.objects.document;

import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;

public class OCDStat extends OCDNode
{
  public static final String TAG = "stat";
  

  public OCDStat(OCDNode parent)
  {
    super(TAG, parent);
  }

  public OCDStat(OCDNode parent, String... properties)
  {
    super(TAG, parent);        
    if (properties.length > 0)
      this.props.putAll(properties);
  }

  public OCDStat(String... properties)
  {
    this((OCDNode)null, properties);
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {    
    props.writeAttributes(xml);
    return this.children();
  }

  @Override
  public void readAttributes(DomNode dom)
  {
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
    return props.toString();
  }

  @Override
  public String toString()
  {
    return sticker();
  }

  @Override
  public Rectangle3 bounds()
  {
    return null;
  }

  @Override
  public void paint(Graphics3 g, OCD.ViewProps props)
  {
  }

  @Override
  public OCDStat copy()
  {
    OCDStat copy = new OCDStat((OCDNode) parent, props.array());
    super.copyTo(copy);
    return copy;
  }
}
