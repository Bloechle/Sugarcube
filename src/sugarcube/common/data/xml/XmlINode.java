package sugarcube.common.data.xml;

import sugarcube.common.data.collections.Str;
import sugarcube.common.interfaces.Taggable;

import java.util.Collection;

public interface XmlINode extends Treezable, Taggable
{
  public static final String CDATA = "cdata";

  @Override
  public Collection<? extends XmlINode> children();

  // used especially with tree menus, not mandatory when used only for XML
  // generation and reading
  @Override
  public XmlINode parent();

  @Override
  public String tag();

  public void setParent(XmlINode parent);

  public Xmlizer xmlizer();

  public default boolean isTag(String tag)
  {
    return Str.Equals(tag(), tag);
  }
  
}
