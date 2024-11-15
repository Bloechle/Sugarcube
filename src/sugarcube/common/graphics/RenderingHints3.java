package sugarcube.common.graphics;

import sugarcube.common.data.collections.Map3;

import java.awt.*;
import java.util.Map;

public class RenderingHints3 extends RenderingHints
{
  public static final Map3<RenderingHints.Key, Object> HQ_HINTS = new Map3<RenderingHints.Key, Object>();

  static
  {
    HQ_HINTS.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    HQ_HINTS.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    HQ_HINTS.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    HQ_HINTS.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    HQ_HINTS.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
  }

  public RenderingHints3(Map<Key, ?> map)
  {
    super(map);
  }

  public RenderingHints3(Object... hints)
  {
    this(new Map3());
    for (int i = 0; i < hints.length - 1; i += 2)
      this.put(hints[i], hints[i + 1]);
  }

  public RenderingHints3(Key key, Object o)
  {
    super(key, o);
  }

  public static RenderingHints3 hqHints()
  {
    return new RenderingHints3(HQ_HINTS);
  }
}
