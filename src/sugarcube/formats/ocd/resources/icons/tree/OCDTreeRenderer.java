package sugarcube.formats.ocd.resources.icons.tree;

import sugarcube.common.interfaces.Unjammable;
import sugarcube.common.ui.gui.TreeRenderer3;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDGroup;
import sugarcube.formats.ocd.objects.document.OCDItem;
import sugarcube.formats.ocd.objects.nav.OCDNavigation;

public class OCDTreeRenderer extends TreeRenderer3 implements Unjammable
{
  public OCDTreeRenderer()
  {
    super(IconsTree.class, "leaf", "document", "pages", "page", "text", "images", "image", "path", "fonts", "font", "clip", "metadata", "manifest", "navigation", "group", "paragraph", "textline");
  }

  @Override
  public String iconName(Object value)
  {
    XmlINode node = value instanceof XmlINode ? (XmlINode) value : null;

    if (node == null)
      return null;
    if (OCD.isGroup(node))
    {
      OCDGroup group = (OCDGroup) node;
      if (group.isType(OCDGroup.PARAGRAPH))
        return "paragraph";
      else if (group.isType(OCDGroup.TEXTLINE))
        return "textline";
      else
        return "group";
    }
    else if (node instanceof OCDNavigation)
      return "navigation";
    else if (node instanceof OCDItem)
      return ((OCDItem) node).type();
    else
      return node.tag();
  }
}
