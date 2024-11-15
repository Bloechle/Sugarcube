package sugarcube.formats.ocd.objects.font;

import sugarcube.common.data.collections.Unicodes;
import sugarcube.common.graphics.geom.Path3;

public class SVGGlyphSP extends SVGGlyph
{
  public static final int UNICODE = Unicodes.ASCII_SP;

  public SVGGlyphSP(SVGFont parent, double width)
  {
    super(parent);
    this.code = UNICODE;
    this.glyphName = "space,#32";
    this.path = new Path3();
    this.horizAdvX = (float) width;
  }
}
