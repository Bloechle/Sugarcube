package sugarcube.formats.epub.replica.svg;

import sugarcube.common.data.collections.StringMap;
import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.geom.Rectangle3;

public class SVGPageClips extends StringMap<SVGClip>
{
  public SVGPage page;

  public SVGPageClips(SVGPage page)
  {
    this.page = page;
  }

  public void add(String clipID, Rectangle3 box)
  {
    if (box != null)
      this.put(clipID, new SVGClip(page, box, clipID));
  }

  public void add(String clipID, Path3 path)
  {
    if (path != null)
      this.put(clipID, new SVGClip(page, path, clipID));
  }
}
