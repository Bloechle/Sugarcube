package sugarcube.formats.ocd.objects.metadata.dc;

import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;

public class DCElement extends OCDNode
{
  public static final String SEPARATOR = ";";

  public DCElement(String tag)
  {
    super(tag, null);
  }

  public DCElement(String tag, OCDNode parent)
  {
    super(tag, parent);
  }

  public DCElement(String tag, OCDNode parent, String... props)
  {
    super(tag, parent);
    this.addAttributes(props);
  }

  public boolean hasAttribute(String prop)
  {
    return this.props.containsKey(prop);
  }

  public DCElement addAttributes(String... props)
  {
    this.props.putAll(props);
    return this;
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    props.writeAttributes(xml);
    return this.children();
  }

  @Override
  public final void readAttributes(DomNode dom)
  {
    props.readAttributes(dom, true);  
  }

  public final DCElement setDoEscapeCData(boolean doEscape)
  {
    this.props.escapeCData(doEscape);
    return this;
  }

  public final boolean doEscapeCData()
  {
    return this.props.escapeCData();
  }

  public final DCElement setCData(String cdata, boolean doEscape)
  {
    this.setCData(cdata);
    this.setDoEscapeCData(doEscape);
    return this;
  }

  public void setCData(String cdata)
  {
    this.props.setEmptyValue(cdata);
  }

  public String cdata()
  {
    return this.props.emptyValue();
  }

  public boolean hasCData()
  {
    return this.props.containsEmptyKey();
  }

  @Override
  public String toString()
  {
    return "DCElement[" + tag + "]\n"
      + Xml.toString(this) + "\n";
  }

  @Override
  public DCElement copy()
  {
    DCElement element = new DCElement(tag);
    element.props = this.props.copy();
    return element;
  }
}
