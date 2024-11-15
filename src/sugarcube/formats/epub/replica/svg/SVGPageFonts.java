package sugarcube.formats.epub.replica.svg;

import sugarcube.common.data.collections.StringMap;
import sugarcube.formats.ocd.objects.font.SVGFont;

public class SVGPageFonts extends StringMap<SVGFont>
{
  public SVGPage page;

  public SVGPageFonts(SVGPage page)
  {
    this.page = page;
  }
}
