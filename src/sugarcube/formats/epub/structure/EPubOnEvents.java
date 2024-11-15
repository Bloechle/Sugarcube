package sugarcube.formats.epub.structure;

import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.StringSet;
import sugarcube.formats.ocd.objects.OCDPaintable;

public class EPubOnEvents extends List3<EPubOnEvent>
{
  public StringSet ids = new StringSet();

  public EPubOnEvent addSource(OCDPaintable source)
  {
    if (source != null)
    {
      EPubOnEvent onEvent = new EPubOnEvent(source);
      this.addID(source);
      this.add(onEvent);
      return onEvent;
    }
    return null;
  }
  
  public void addTargetTo(EPubOnEvent onEvent, OCDPaintable target)
  {
    onEvent.setTarget(target);
    addID(target);    
  }

  private EPubOnEvents addID(OCDPaintable node)
  {
    if (node != null)
      ids.addNonVoid(node.id());
    return this;
  }

}
