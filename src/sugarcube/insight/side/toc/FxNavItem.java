package sugarcube.insight.side.toc;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import sugarcube.common.data.collections.List3;
import sugarcube.formats.ocd.objects.OCDNode;
import sugarcube.formats.ocd.objects.nav.OCDNavItem;

import java.util.Collection;

public class FxNavItem extends TreeItem<OCDNavItem>
{
  private boolean isLeaf;
  private boolean isFirstTimeChildren = true;
  private boolean isFirstTimeLeaf = true;

  public FxNavItem(OCDNavItem item)
  {
    super(item);
    this.setExpanded(item.isExpanded);
  }

  public FxNavItem persistExpansion()
  {
    this.item().isExpanded = this.isExpanded();
    for (TreeItem<OCDNavItem> child : this.getChildren())
      ((FxNavItem) child).persistExpansion();
    return this;
  }

  public OCDNavItem item()
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
  public ObservableList<TreeItem<OCDNavItem>> getChildren()
  {
    if (isFirstTimeChildren)
    {
      // builds tree on first call
      isFirstTimeChildren = false;
      super.getChildren().setAll(build());
    }
    return super.getChildren();
  }

  private ObservableList<TreeItem<OCDNavItem>> build()
  {
    OCDNavItem item = item();
    Collection<? extends OCDNavItem> children = item == null ? new List3<OCDNavItem>() : item.children();
    if (children != null && !children.isEmpty())
    {
      ObservableList<TreeItem<OCDNavItem>> obsChildren = FXCollections.observableArrayList();
      for (OCDNavItem child : children)
      {
        obsChildren.add(new FxNavItem(child));
      }
//      Log.debug(this, ".build() - " + item().tag + " - " + obsChildren.toString());
      return obsChildren;
    }
    return FXCollections.emptyObservableList();
  }

}