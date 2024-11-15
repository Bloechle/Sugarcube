package sugarcube.common.data.xml;

import java.util.Collection;

public interface Treezable
{
  Collection<? extends Treezable> children();

  default Treezable parent()
  {
    return null;
  }

  default String sticker()
  {
    return toString();
  }
}
