package sugarcube.formats.ocd.objects.lists;

import sugarcube.formats.ocd.objects.OCDLayer;

public class OCDLayers extends OCDPaintables<OCDLayer>
{
  public OCDLayers()
  {

  }

  public OCDLayers(Iterable<OCDLayer> iterable)
  {
    for (OCDLayer t : iterable)
      this.add(t);
  }
}
