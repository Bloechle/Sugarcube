package sugarcube.insight.ribbon.actions.annot;

import sugarcube.common.data.collections.Str;
import sugarcube.common.interfaces.Item;

public class AnnotItem implements Item
{
  public String type;
  public String className;

  public AnnotItem(String type, String className)
  {
    this.type = type;
    this.className = className;
  }
  
  public boolean hasClassname()
  {
    return !Str.IsVoid(className);
  }
  
  @Override
  public String toString()
  {
    return type + (Str.IsVoid(className) ? "" : " - "+className);
  }
}
