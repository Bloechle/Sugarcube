package sugarcube.common.ui.fx.controls;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.xml.Treezable;

import java.util.Collection;

public class FxTreeNode<T extends Treezable> extends TreeItem<T>
{

  private boolean isLeaf;
  private boolean isFirstTimeChildren = true;
  private boolean isFirstTimeLeaf = true;
  
  public FxTreeNode(T root)
  {
    super(root);
  }

  @Override
  public ObservableList<TreeItem<T>> getChildren()
  {
    if (isFirstTimeChildren)
    {
      // builds tree on first call
      isFirstTimeChildren = false;
      super.getChildren().setAll(buildChildren(this));
    }
    return super.getChildren();
  }
  
  public Treezable value()
  {
    return this.getValue();
  }

  @Override
  public boolean isLeaf()
  {
    if (isFirstTimeLeaf)
    {
      isFirstTimeLeaf = false;
      Collection<? extends Treezable> children = value().children();
      isLeaf = children == null || children.isEmpty();
    }
    return isLeaf;
  }

  private ObservableList<TreeItem<T>> buildChildren(TreeItem<T> item)
  {
    T treezable = item.getValue();
    Collection<? extends Treezable> children = treezable == null ? new List3<>() : treezable.children();
    if (children!=null && !children.isEmpty())
    {
      ObservableList<TreeItem<T>> obsChildren = FXCollections.observableArrayList();
      for (Treezable node : children)
        obsChildren.add(new FxTreeNode(node));
      return obsChildren;
    }
    return FXCollections.emptyObservableList();
  }
  
  @Override
  public String toString()
  {
    return value().sticker();
  }
}
