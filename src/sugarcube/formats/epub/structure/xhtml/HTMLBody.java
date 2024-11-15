package sugarcube.formats.epub.structure.xhtml;

import sugarcube.formats.ocd.objects.OCDNode;

public class HTMLBody extends HTMLNode
{
  public static final String TAG = "body";

  public HTMLBody(OCDNode... children)
  {
    super(TAG);
    this.addChildren(children);
  }

  public HTMLBody style(String style)
  {
    return (HTMLBody) super.style(style);
  }

  public HTMLBody onload(String js)
  {
    super.onload(js);
    return this;
  }

  public HTMLNode first()
  {
    for (OCDNode node : this.children())
      if (node instanceof HTMLNode)
        return (HTMLNode) node;
    return null;
  }

}
