package sugarcube.insight.side.dom;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import sugarcube.common.data.collections.Set3;
import sugarcube.formats.ocd.objects.OCDNode;
import sugarcube.formats.ocd.objects.OCDPage;
import sugarcube.formats.ocd.objects.lists.OCDList;

public class FxDomTree extends TreeView<OCDNode>
{
  public DomSide pane;

  public FxDomTree(DomSide pane, OCDPage page)
  {
    super(new FxDomItem(page));
    this.pane = pane;
    this.setEditable(true);
    this.setCellFactory(p -> {
      return new FxDomCell(this);
    });
  }

  public FxDomItem root()
  {
    return (FxDomItem) this.getRoot();
  }

  public FxDomTree persistExpansion()
  {
    root().persistExpansion();
    return this;
  }

  public OCDNode[] selected()
  {
    Set3<OCDNode> items = new Set3<>();
    for (TreeItem<OCDNode> item : getSelectionModel().getSelectedItems())
      items.add(item.getValue());
    return items.toArray(new OCDNode[0]);
  }

  public OCDList selectedList()
  {
    return new OCDList(selected());
  }

  public FxDomItem select(OCDNode item)
  {
    TreeItem<OCDNode> selected = item == null ? null : seek(this.getRoot(), item);
    if (selected != null)
    {
      this.getSelectionModel().select(selected);
      this.scrollTo(this.getSelectionModel().getSelectedIndex());
    }
    return (FxDomItem) selected;
  }

  public TreeItem<OCDNode> seek(TreeItem<OCDNode> item, OCDNode ocdItem)
  {
    if (item.getValue() == ocdItem)
      return item;
    for (TreeItem<OCDNode> child : item.getChildren())
      if ((item = seek(child, ocdItem)) != null)
        return item;
    return null;
  }

}
