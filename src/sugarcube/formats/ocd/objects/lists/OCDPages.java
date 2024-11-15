package sugarcube.formats.ocd.objects.lists;

import sugarcube.common.data.collections.List3;
import sugarcube.formats.ocd.objects.OCDPage;

public class OCDPages extends List3<OCDPage>
{
  
  public OCDPage[] array()
  {
    return this.toArray(new OCDPage[0]);
  }
}
