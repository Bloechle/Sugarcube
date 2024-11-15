package sugarcube.formats.ocd.objects.lists;

import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.formats.ocd.objects.OCDPath;

public class OCDPaths extends OCDPaintables<OCDPath>
{
  public OCDPaths()
  {

  }

  public OCDPaths(Iterable<OCDPath> iterable)
  {
    for (OCDPath t : iterable)
      this.add(t);
  }
  
  public OCDPaths withBounds(Rectangle3 box, double epsilon)
  {
    OCDPaths paths = new OCDPaths();
    for(OCDPath path: this)
      if(path.bounds().equals(box,  epsilon))
        paths.add(path);
    return paths;
  }
  
  public OCDPaths minWidth(double size)
  {
    OCDPaths paths = new OCDPaths();
    for(OCDPath path: this)
      if(path.bounds().width>size)
        paths.add(path);
    return paths;
  }


}
