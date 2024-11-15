package sugarcube.common.data.xml;

import java.util.Collection;

public interface Xmlizer
{
  public static class Adapter<T extends XmlINode> implements Xmlizer
  {
    private T node;

    public Adapter(T node)
    {
      this.node = node;
    }

    @Override
    public Collection<? extends XmlINode> writeAttributes(Xml xml)
    {
      return node.children();
    }

    @Override
    public void readAttributes(DomNode dom)
    {
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

  public Collection<? extends XmlINode> writeAttributes(Xml xml);

  public void readAttributes(DomNode e);

  public XmlINode newChild(DomNode child);

  public void endChild(XmlINode child);
}
