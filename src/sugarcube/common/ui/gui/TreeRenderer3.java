
package sugarcube.common.ui.gui;

import sugarcube.common.data.collections.Map3;
import sugarcube.common.ui.gui.icons.ImageIcon3;
import sugarcube.common.system.io.Class3;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public abstract class TreeRenderer3 extends DefaultTreeCellRenderer
{
  protected Map3<String, ImageIcon3> icons = new Map3<String, ImageIcon3>();
  protected Class3 root;

  public TreeRenderer3(Class root, String... types)
  {
    this.root = new Class3(root);
    this.add(types);
    try
    {
      this.setLeafIcon(icons.get("leaf"));
    }
    catch (Exception e)
    {
    }
  }

  public void add(String... names)
  {
    for (String name : names)
      icons.put(name, root.icon(name + ".png"));
  }

  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
  {
    super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    String name = iconName(value);
    if (name != null && icons.has(name))
      setIcon(icons.get(name));
    return this;
  }
  
  public abstract String iconName(Object value); 
}
