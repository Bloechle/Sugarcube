package sugarcube.formats.ocd.objects.lists;

import sugarcube.formats.ocd.objects.OCDGroup;

public class OCDGroups extends OCDPaintables<OCDGroup>
{
  public OCDGroups()
  {

  }

  public OCDGroups(Iterable<OCDGroup> iterable)
  {
    for (OCDGroup t : iterable)
      this.add(t);
  }
}
