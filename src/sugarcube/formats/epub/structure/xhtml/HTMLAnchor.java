package sugarcube.formats.epub.structure.xhtml;

import sugarcube.common.data.collections.Str;

public class HTMLAnchor extends HTMLNode
{
  public static final String TAG = "a";

  public HTMLAnchor(String href, String... props)
  {
    super(TAG);
    if (!Str.IsVoid(href))
      this.addAttributes("href", href);
    this.addAttributes(props);
  }
}
