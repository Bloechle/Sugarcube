package sugarcube.formats.epub.structure.xhtml;

public class HTMLSource extends HTMLNode
{
  public static final String TAG = "source";

  public HTMLSource(String src, String type)
  {
    super(TAG, "src", src, "type", type);
  }
}