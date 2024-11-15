package sugarcube.insight.tree;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import sugarcube.common.data.collections.List3;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;

public class FxOCDItem extends TreeItem<OCDNode>
{
  private boolean isLeaf;
  private boolean isFirstTimeChildren = true;
  private boolean isFirstTimeLeaf = true;

  public FxOCDItem(OCDNode node)
  {
    super(node);
  }  
  
  public OCDNode node()
  {
    return this.getValue();
  }
  
  @Override
  public ObservableList<TreeItem<OCDNode>> getChildren()
  {
    if (isFirstTimeChildren)
    {
      // builds tree on first call
      isFirstTimeChildren = false;
      super.getChildren().setAll(buildChildren(this));
    }
    return super.getChildren();
  }


  @Override
  public boolean isLeaf()
  {
    if (isFirstTimeLeaf)
    {
      isFirstTimeLeaf = false;
      Collection<? extends OCDNode> children = node().children();
      isLeaf = children == null || children.isEmpty();
    }
    return isLeaf;
  }

  private ObservableList<TreeItem<OCDNode>> buildChildren(TreeItem<OCDNode> item)
  {
    OCDNode node = item.getValue();
    Collection<? extends OCDNode> children = node == null ? new List3<OCDNode>() : node.children();
    if (children!=null && !children.isEmpty())
    {
      ObservableList<TreeItem<OCDNode>> obsChildren = FXCollections.observableArrayList();
      for (OCDNode child : children)
        obsChildren.add(new FxOCDItem(child));
      return obsChildren;
    }
    return FXCollections.emptyObservableList();
  }
  
  @Override
  public String toString()
  {
    return node().sticker();
  }

}
