package sugarcube.insight.tree;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import sugarcube.formats.ocd.objects.OCDNode;

public class FxOCDTree extends TreeView<OCDNode>
{
  public FxOCDTree(OCDNode root)
  {
    super(new FxOCDItem(root));
  }

  public TreeItem<OCDNode> select(OCDNode value)
  {
    TreeItem<OCDNode> selected = seek(this.getRoot(), value);
    if (selected != null)
    {
      this.getSelectionModel().select(selected);
      this.scrollTo(this.getSelectionModel().getSelectedIndex());
    }
    return selected;
  }

  public TreeItem<OCDNode> seek(TreeItem<OCDNode> node, OCDNode value)
  {
    if (node.getValue() == value)
      return node;
    for (TreeItem<OCDNode> child : node.getChildren())
      if ((node = seek(child, value)) != null)
        return node;

    return null;
  }

}
