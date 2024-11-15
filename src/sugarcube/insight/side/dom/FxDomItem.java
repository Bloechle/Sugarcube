package sugarcube.insight.side.dom;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import sugarcube.common.data.collections.List3;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;

public class FxDomItem extends TreeItem<OCDNode>
{
  private boolean isLeaf;
  private boolean isFirstTimeChildren = true;
  private boolean isFirstTimeLeaf = true;

  public FxDomItem(OCDNode node)
  {
    super(node);
    this.setExpanded(node.isTreeViewExpanded);
  }

  public FxDomItem persistExpansion()
  {
    this.item().isTreeViewExpanded = this.isExpanded();
    for (TreeItem<OCDNode> child : this.getChildren())
      ((FxDomItem) child).persistExpansion();
    return this;
  }

  public OCDNode item()
  {
    return this.getValue();
  }

  @Override
  public boolean isLeaf()
  {
    if (isFirstTimeLeaf)
    {
      isFirstTimeLeaf = false;
      Collection<? extends OCDNode> children = item().children();
      isLeaf = children == null || children.isEmpty();
    }
    return isLeaf;
  }

  @Override
  public ObservableList<TreeItem<OCDNode>> getChildren()
  {
    if (isFirstTimeChildren)
    {
      // builds tree on first call
      isFirstTimeChildren = false;
      super.getChildren().setAll(build());
    }
    return super.getChildren();
  }

  private ObservableList<TreeItem<OCDNode>> build()
  {
    OCDNode item = item();
    Collection<? extends OCDNode> children = item == null ? new List3<OCDNode>() : item.children();
    if (children != null && !children.isEmpty())
    {
      ObservableList<TreeItem<OCDNode>> obsChildren = FXCollections.observableArrayList();
      for (OCDNode child : children)
      {
        obsChildren.add(new FxDomItem(child));
      }
//      Log.debug(this, ".build() - " + item().tag + " - " + obsChildren.toString());
      return obsChildren;
    }
    return FXCollections.emptyObservableList();
  }

}