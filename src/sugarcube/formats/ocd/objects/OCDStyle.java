package sugarcube.formats.ocd.objects;

import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlINode;

import java.util.Collection;

public class OCDStyle extends OCDNode
{
  public static final String TAG = "style";
  
  public OCDStyle(OCDStyles styles)
  {
    super(TAG, styles);
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    xml.write("id", id);
    return this.children();
  }

  @Override
  public void readAttributes(DomNode e)
  {
    this.id = e.value("id", id());
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
}
