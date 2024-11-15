package sugarcube.formats.epub;

import sugarcube.common.data.collections.Str;
import sugarcube.common.data.collections.StringSet;
import sugarcube.formats.ocd.objects.OCDAnnot;
import sugarcube.formats.ocd.objects.document.OCDProps;

public class EPubProps extends OCDProps
{
  public static final int KINDLE_BEST_WIDTH = 1200;

  public static final String KEY_DECIMALS = "decimals";

  // pre-paginated, reflow
  public static final String KEY_LAYOUT = "layout";
  // none, both, left, right, auto
  public static final String KEY_SPREAD = "spread";
  // landscape, portrait, auto
  public static final String KEY_ORIENTATION = "orientation";
  // page01.xhtml, ...
  public static final String KEY_MAIN_CONTENT = "main_content";

  public static final String KEY_FONTSIZE_DELTA = "fontsize_delta";

  public static final String KEY_OUTPUT = "output";
  public static final String KEY_SAMPLING = "sampling";
  public static final String KEY_ANTIALIAS = "antialias";
  public static final String KEY_PNG = "png";
  public static final String KEY_JPEG = "jpeg";
  public static final String KEY_VIEWBOX = "viewbox";
  public static final String KEY_HTML_TEXT = "html_text";
  public static final String KEY_VEC_GRAPHICS = "vec_graphics";
  public static final String KEY_VEC_TEXT = "vec_text";
  public static final String KEY_TOC_DEPTH = "toc_depth";

  public static final String KEY_FONT_64 = "font_64";
  public static final String KEY_JAVASCRIPT = "javascript";
  public static final String KEY_CSS = "css";
  public static final String KEY_CSS_INTERNAL = "css_internal";
  public static final String KEY_DETECT_URL = "detect_url";
  public static final String KEY_KEEP_TOC = "keep_toc";
  public static final String KEY_SPECIMEN = "specimen";
  public static final String KEY_RESIZE = "resize";
  public static final String KEY_IMG_MAXAREA = "img_maxarea";
  public static final String KEY_DISP_WIDTH = "disp_width";
  public static final String KEY_DISP_HEIGHT = "disp_height";
  public static final String KEY_BLEND_BG = "blend_bg";
  public static final String KEY_OPF_GUIDE = "opf_guide";

  // liquid
  public static final String KEY_LIQUID_SPLIT = "liquid_split";
  public static final String KEY_LIQUID_REMAP = "liquid_remap:";

  // keep output names in lowercase !
  public static final String OUTPUT_EPUB3 = "epub";
  public static final String OUTPUT_MOBI = "mobi";

  public static final String RESIZE_HEIGHT = "height";

  public static final String VIEWBOX_CROP = "CropBox";
  public static final String VIEWBOX_TRIM = OCDAnnot.ID_TRIMBOX;

  public static final String KEY_OCR_MODE = "ocr_mode";

  public static final String OCR_MODE_VECTOR = "vector";
  public static final String KEY_EPUB_CHECK = "epub_check";

  public EPubProps()
  {
  }

  @Override
  public EPubProps set(String key, Object value)
  {
    super.set(key, value);
    return this;
  }

  public EPubProps output(String value)
  {
    return set(KEY_OUTPUT, value);
  }

  public EPubProps epub3()
  {
    return output(OUTPUT_EPUB3);
  }

  public EPubProps mobi()
  {
    return output(OUTPUT_MOBI);
  }

  public EPubProps sampling(String value)
  {
    return set(KEY_SAMPLING, value);
  }

  public EPubProps antialias(String value)
  {
    return set(KEY_ANTIALIAS, value);
  }

  public EPubProps jpeg(String value)
  {
    return set(KEY_JPEG, value);
  }

  public EPubProps jpeg(double value)
  {
    return set(KEY_JPEG, value + "");
  }

  public EPubProps jpegPercent(double value)
  {
    return set(KEY_JPEG, (value <= 1.0 ? value : (value / 100)) + "");
  }

  public EPubProps setFontsizeDelta(int delta)
  {
    return set(KEY_FONTSIZE_DELTA, delta + "");
  }

  public EPubProps setLiquidSplit(String styles)
  {
    return this.set(KEY_LIQUID_SPLIT, styles);
  }

  public EPubProps setLiquidRemap(String key, String val)
  {
    return this.set(KEY_LIQUID_REMAP + key, val);
  }

  public int fontsizeDelta()
  {
    return this.integer(KEY_FONTSIZE_DELTA, 0);
  }

  public StringSet liquidSplit()
  {
    return new StringSet(Str.Split(this.get(KEY_LIQUID_SPLIT)));
  }

  public String liquidRemap(String key, String def)
  {
    return this.get(KEY_LIQUID_REMAP + key, def);
  }

  public String output()
  {
    return get(KEY_OUTPUT, "");
  }
  
  public boolean doEpubCheck()
  {
    return bool(KEY_EPUB_CHECK, true);
  }
  
  public EPubProps disableEpubCheck()
  {
    put(KEY_EPUB_CHECK, false);    
    return this;
  }
  
  public EPubProps disableVectorGraphics()
  {
    put(KEY_VEC_GRAPHICS, false);    
    return this;
  }

  public boolean showOpfGuide()
  {
    return bool(KEY_OPF_GUIDE, false);
  }

  public boolean isFixedLayout()
  {
    return !this.get(KEY_LAYOUT, "").toLowerCase().contains("reflow");
  }

  public float sampling()
  {
    return sampling(-1);
  }

  public float sampling(double def)
  {
    return realize(KEY_SAMPLING, def);
  }

  public float antialias()
  {
    return realize(KEY_ANTIALIAS, 2);
  }

  public float jpeg()
  {
    return realize(KEY_JPEG, real("jpg", 0.95));
  }

  public boolean png()
  {
    return bool(KEY_PNG, false);
  }

  public boolean font64()
  {
    return bool(KEY_FONT_64, false) || bool("page_fonts", false);
  }

  public boolean cssInternal()
  {
    return bool(KEY_CSS_INTERNAL, true);
  }

  public boolean detectUrl()
  {
    return bool(KEY_DETECT_URL, true);
  }

  public boolean keepToc()
  {
    return bool(KEY_KEEP_TOC, true);
  }

  public boolean vecGraphics()
  {
    return bool(KEY_VEC_GRAPHICS, bool("svg_graphics", true));
  }

  public boolean vecText()
  {
    return bool(KEY_VEC_TEXT, bool("svg_text", true));
  }

  public int imageMaxArea(int def)
  {
    return integer(KEY_IMG_MAXAREA, def);
  }

  public int dispWidth()
  {
    return integer(KEY_DISP_WIDTH, -1);
  }

  public int dispHeight()
  {
    return integer(KEY_DISP_HEIGHT, -1);
  }

  public String specimen()
  {
    return get(KEY_SPECIMEN, "");
  }

  public boolean isSpecimen()
  {
    return get(KEY_SPECIMEN, "").length() > 0;
  }

  public boolean isOCRModeVector()
  {
    return this.is(KEY_OCR_MODE, OCR_MODE_VECTOR);
  }

  public EPubProps normalizeChoice(String key, String... values)
  {
    // Log.debug(this,
    // ".normalizeChoice - key="+get(key)+", values="+Zen.A.toString(values)+",
    // props="+this);
    String data = Str.Unnull(get(key, "")).toLowerCase();
    if (Str.HasChar(data))
      for (String value : values)
        if (data.contains(value.toLowerCase()))
        {
          put(key, value);
          return this;
        }
    this.remove(key);
    return this;
  }

  public EPubProps normalizeReals(String... keys)
  {
    for (String key : keys)
    {
      String data = Str.Unnull(get(key, ""));
      if (Str.HasChar(data))
      {
        StringBuilder sb = new StringBuilder(data.length());
        for (char c : data.toCharArray())
          if (c >= '0' && c <= '9' || c == '.' || c == '-')
            sb.append(c);
        data = sb.toString();
        if (Str.HasChar(data))
          put(key, data);
      }
    }
    return this;
  }

  public EPubProps normalize()
  {
    this.normalizeChoice(KEY_OUTPUT, OUTPUT_EPUB3, OUTPUT_MOBI);
    this.normalizeChoice(KEY_VIEWBOX, VIEWBOX_CROP, VIEWBOX_TRIM);
    if (get(KEY_VIEWBOX, "").equals(VIEWBOX_CROP))
      set(KEY_VIEWBOX, "ViewBox");

    this.normalizeReals(KEY_SAMPLING, KEY_ANTIALIAS, KEY_JPEG);

    // // for jiji nestle
    // if (this.bool(KEY_IE9, false))
    // {
    // put(KEY_OUTPUT, OUTPUT_WEBSITE);
    // put(KEY_VEC_GRAPHICS, false);
    // }

    switch (get(KEY_OUTPUT, ""))
    {
    case OUTPUT_EPUB3:
      break;
    case OUTPUT_MOBI:
      // put(KEY_FONT_OTF, true);
      // put(KEY_FONT_SVG, false);
      put(KEY_JAVASCRIPT, false);
      // put(KEY_VEC_GRAPHICS, false);
      put(KEY_DISP_WIDTH, KINDLE_BEST_WIDTH);
      break;
    default:
      break;
    }

    return this;
  }

  public boolean isOutputType(String output)
  {
    return this.get(KEY_OUTPUT, "").equals(output);
  }

  public boolean isEpub()
  {
    return !isMobi();
  }

  public boolean isMobi()
  {
    return isOutputType(OUTPUT_MOBI);
  }
}
