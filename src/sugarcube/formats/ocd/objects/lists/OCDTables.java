package sugarcube.formats.ocd.objects.lists;

import sugarcube.common.numerics.Math3;
import sugarcube.formats.ocd.objects.OCDTable;

import java.util.Collections;

public class OCDTables extends OCDPaintables<OCDTable>
{
  public OCDTables()
  {

  }

  public OCDTables(Iterable<OCDTable> iterable)
  {
    for (OCDTable t : iterable)
      this.add(t);
  }
  
  public OCDTables ySort()
  {
    return sortY(true);
  }
  
  public OCDTables sortY(boolean topDown)
  {
    Collections.sort(this, (t1, t2) -> Math3.Sign(t1.box().minY() - t2.box().minY(), topDown));
    return this;
  }
}
