package sugarcube.insight.side.toc;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import sugarcube.common.data.collections.Set3;
import sugarcube.formats.ocd.objects.nav.OCDNavItem;

public class FxNavTree extends TreeView<OCDNavItem>
{
  public NavSide pane;
  

  public FxNavTree(NavSide pane, OCDNavItem root)
  {
    super(new FxNavItem(root));
    this.pane = pane;
    this.setShowRoot(false);
    this.setEditable(true);
    this.setCellFactory(p -> {
      return new FxNavCell(this);
    });    
  }
  
  
  public FxNavItem root()
  {
    return (FxNavItem) this.getRoot();
  }

  public FxNavTree persistExpansion()
  {
    root().persistExpansion();
    return this;
  }

  public OCDNavItem[] selected()
  {
    Set3<OCDNavItem> items = new Set3<>();
    for (TreeItem<OCDNavItem> item : getSelectionModel().getSelectedItems())
      items.add(item.getValue());
    return items.toArray(new OCDNavItem[0]);
  }

  public FxNavItem select(OCDNavItem item)
  {
    TreeItem<OCDNavItem> selected = item == null ? null : seek(this.getRoot(), item);
    if (selected != null)
    {
      this.getSelectionModel().select(selected);
      this.scrollTo(this.getSelectionModel().getSelectedIndex());
    }
    return (FxNavItem) selected;
  }

  public TreeItem<OCDNavItem> seek(TreeItem<OCDNavItem> item, OCDNavItem ocdItem)
  {
    if (item.getValue() == ocdItem)
      return item;
    for (TreeItem<OCDNavItem> child : item.getChildren())
      if ((item = seek(child, ocdItem)) != null)
        return item;
    return null;
  }

}
