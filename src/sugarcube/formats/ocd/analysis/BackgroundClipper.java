package sugarcube.formats.ocd.analysis;

import sugarcube.common.graphics.geom.Path3;
import sugarcube.formats.ocd.objects.OCDPage;
import sugarcube.formats.ocd.objects.OCDTextBlock;

import java.awt.geom.Area;

public class BackgroundClipper
{
  public BackgroundClipper()
  {
  }
  
  public Path3 clip(OCDPage page)
  {
    Area area = new Area(page.bounds());
    for(OCDTextBlock block: page.content().blocks())    
      area.subtract(new Area(block.bounds().inflate(2)));
    return new Path3(area);
  }
}
