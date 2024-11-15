package sugarcube.formats.epub.structure.xhtml;

import sugarcube.formats.epub.structure.EPub;
import sugarcube.formats.ocd.objects.OCDNode;

public class HTMLHead extends HTMLNode
{
  public static final String TAG = "head";

  public HTMLHead(OCDNode... children)
  {
    super(TAG);
    this.addChildren(children);
  }

  public HTMLHead add(String tag, String... props)
  {
    super.add(tag, props);
    return this;
  }

  public HTMLHead title(String title)
  {
    return add("title", title);
  }

  public HTMLHead utf8()
  {
    return add("meta", "charset", "utf-8");
  }

  public HTMLHead viewport(int width, int height)
  {
    return add("meta", "name", "viewport", "content", "width=" + width + ", height=" + height);
  }

  public HTMLHead stylesheet(String href)
  {
    return add("link", "href", href, "rel", "stylesheet", "type", "text/css");
  }

  public HTMLHead stylesheets(String folder, Iterable<String> hrefs)
  {
    if (!folder.isEmpty() && !folder.endsWith("/"))
      folder += "/";
    for (String href : hrefs)
      stylesheet(folder + href);
    return this;
  }

  public HTMLHead javascript(String src)
  {
    return add("script", "src", EPub.JS_FOLDER + src, "");
  }

  public HTMLHead javascripts(String folder, Iterable<String> srcs)
  {
    if (!folder.isEmpty() && !folder.endsWith("/"))
      folder += "/";
    for (String src : srcs)
      javascript(folder + src);
    return this;
  }

  public HTMLStyle style(String css)
  {
    HTMLStyle style = new HTMLStyle("\n" + css);
    this.add(style);
    return style;
  }

}
