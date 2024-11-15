package sugarcube.formats.ocd.objects.document;

import sugarcube.common.data.collections.Props;
import sugarcube.common.data.collections.Str;
import sugarcube.common.data.collections.StringSet;

public class OCDProps extends Props
{
  
  public static final String EXPORT_IMAGES = "export-images";
  public static final String EXPORT_PARAGRAPH_SPACING = "export-paragraph_spacing";
  public static final String EXPORT_TEXT_COLOR = "export-text_color";
  public static final String EXPORT_FONT_SIZE = "export-font_size";
  public static final String EXPORT_FONT_BOLD = "export-font_bold";
  public static final String EXPORT_FONT_ITALIC = "export-font_italic";
  public static final String EXPORT_PAGE_BREAK = "export-page_break";
  public static final String EXPORT_PAGE_RANGE = "export-page_range";
  public static final String EXPORT_SORT_VERT = "export-sort_vert";
  public static final String EXPORT_IMAGE_VIEWS = "export-image_views";
  public static final String EXPORT_RESOLVE_HYPHENATION = "export-resolve_hyphenation";
  
  public static final String EXPORT_STYLE_SKIP = "export-style_skip";
  
  public static final String EXPORT_PARAGRAPH_ALIGN = "export-paragraph_align";
  public static final String EXPORT_HEADING_ALIGN = "export-heading_align";
  public static final String EXPORT_COLUMN = "export-column";
  
  public OCDProps()
  {
  }


  @Override
  public OCDProps set(String key, Object value)
  {
    return (OCDProps) super.set(key, value);
  }
  
  public StringSet set(String key)
  {
    return new StringSet(Str.Split(get(key)));
  }
 
}
