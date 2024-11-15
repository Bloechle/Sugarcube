package sugarcube.common.graphics.vectorize;

import java.util.HashMap;

public class DTOptions extends HashMap<String, Float>
{
  public static String DEBUG = "debug";
  public static String SCALE = "scale";
  public static String LTRES = "ltres";
  public static String QTRES = "qtres";
  public static String PATHOMIT = "pathomit";
  public static String COLORSAMPLING = "colorsampling";
  public static String NB_OF_COLORS = "numberofcolors";
  public static String MINCOLORRATION = "mincolorratio";
  public static String COLORQUANTCYCLES = "colorquantcycles";
  public static String SIMPLIFYTOLERANCE = "simplifytolerance";
  public static String ROUNDCOORDS = "roundcoords";
  public static String LCPR = "lcpr";
  public static String QCPR = "qcpr";
  public static String DESC = "desc";
  public static String VIEWBOX = "viewbox";
  public static String BLURRADIUS = "blurradius";
  public static String BLURDELTA = "blurdelta";
  public static String WIDTH = "width";
  public static String HEIGHT = "height";

  public DTOptions()
  {
    checkOptions();
  }

  // creating options object, setting defaults for missing values
  public DTOptions checkOptions()
  {
    ensure(WIDTH, 0f);
    ensure(HEIGHT, 0f);
    // Tracing
    ensure(LTRES, 1f);
    ensure(QTRES, 1f);
    ensure(PATHOMIT, 8f);
    // Color quantization
    ensure(COLORSAMPLING, 1f);
    ensure(NB_OF_COLORS, 16f);
    ensure(MINCOLORRATION, 0.02f);
    ensure(COLORQUANTCYCLES, 3f);
    // SVG rendering
    ensure(SCALE, 1f);
    ensure(SIMPLIFYTOLERANCE, 0f);
    ensure(ROUNDCOORDS, 2f);
    ensure(LCPR, 0f);
    ensure(QCPR, 0f);
    ensure(DESC, 1f);
    ensure(VIEWBOX, 0f);
    // Blur
    ensure(BLURRADIUS, 0f);
    ensure(BLURDELTA, 20f);
    return this;
  }

  public DTOptions put(String key, int value)
  {
    super.put(key, (float) value);
    return this;
  }

  public DTOptions put(String key, boolean value)
  {
    super.put(key, value ? 1f : 0f);
    return this;
  }

  public boolean bool(String key)
  {
    return bool(key, false);
  }

  public boolean bool(String key, boolean def)
  {
    return value(key, def ? 1f : 0f) > 0.5f;
  }

  public float value(String key, float def)
  {
    Float v = get(key);
    return v == null ? def : v;
  }

  public int integer(String key, int def)
  {
    Float v = get(key);
    return v == null ? def : (int) Math.round(v);
  }

  public int floor(String key)
  {
    return (int) (Math.floor(get(key)));
  }

  private void ensure(String key, float value)
  {
    if (!containsKey(key))
      put(key, value);
  }
}
