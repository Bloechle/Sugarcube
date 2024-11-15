package sugarcube.common.data.xml;

import sugarcube.common.data.collections.List3;

public class XmlChildren<T extends XmlINode> extends List3<T>
{
  public XmlChildren(Iterable<T> data)
  {
    super(data);
  }

  public XmlChildren(T... data)
  {
    super(data);
  }

  @Override
  public XmlChildren<T> addIter3(Iterable<T>... data)
  {
    super.addIter3(data);
    return this;
  }

  @Override
  public XmlChildren<T> addAll3(T... data)
  {
    super.addAll3(data);
    return this;
  }
}