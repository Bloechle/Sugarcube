package sugarcube.formats.epub.structure.xhtml;

import sugarcube.formats.ocd.objects.OCDNode;

public class HTMLDocument extends HTMLNode
{
  public static final String TAG = "html";
  public HTMLHead head;
  public HTMLBody body;
  public String filename;
  public String title;

  public HTMLDocument(OCDNode... nodes)
  {
    super(TAG);
    this.head = new HTMLHead();
    this.body = new HTMLBody(nodes);
    this.addAttributes("xmlns", "http://www.w3.org/1999/xhtml");
    this.addChildren(head, body);
  }

  public HTMLDocument filename(String filename)
  {
    this.filename = filename;
    return this;
  }

  public HTMLDocument title(String title)
  {
    this.title = title;
    return this;
  }

  public HTMLHead head()
  {
    return head;
  }

  public HTMLBody body(String... classnames)
  {
    body.classname(classnames);
    return body;
  }

  public HTMLNode get(String id)
  {
    return body.get(id);
  }

}
