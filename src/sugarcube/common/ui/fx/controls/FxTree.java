package sugarcube.common.ui.fx.controls;

import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import sugarcube.common.data.xml.Treezable;

public class FxTree<T extends Treezable> extends TreeView<T>
{

    public FxTree()
    {

    }

    public FxTree(boolean multipleSelection)
    {
        this.setMultipleSectionMode(multipleSelection);
    }

    public FxTree(T root)
    {
        super(new FxTreeNode(root));
    }

    public ObservableList<TreeItem<T>> selectedItems()
    {
        return getSelectionModel().getSelectedItems();
    }

    public FxTree update(Treezable root)
    {
        this.setRoot(root == null ? null : new FxTreeNode(root));
        return this;
    }

    public void listen(Runnable run)
    {
        getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> run.run());
    }
    public FxTree enableMultipleSelection()
    {
        return this.setMultipleSectionMode(true);
    }

    public FxTree setMultipleSectionMode(boolean multiple)
    {
        getSelectionModel().setSelectionMode(multiple ? SelectionMode.MULTIPLE : SelectionMode.SINGLE);
        return this;
    }

    public TreeItem<T> select(T value)
    {
        TreeItem<T> selected = seek(this.getRoot(), value);
        if (selected != null)
        {
            this.getSelectionModel().select(selected);
            this.scrollTo(this.getSelectionModel().getSelectedIndex());
        }
        return selected;
    }

    public TreeItem<T> seek(TreeItem<T> node, T value)
    {
        if (node == null || node.getValue() == value)
            return node;
        for (TreeItem<T> child : node.getChildren())
            if ((node = seek(child, value)) != null)
                return node;

        return null;
    }

    public FxTree expand()
    {
        ExpandTreeView(getRoot());
        return this;
    }

    public FxTree collapse()
    {
        ExpandTreeView(getRoot());
        return this;
    }

    public static void ExpandTreeView(TreeItem<?> item)
    {
        if (item == null || item.isLeaf())
            return;
        item.setExpanded(true);
        for (TreeItem<?> child : item.getChildren())
            ExpandTreeView(child);

    }

    public static void CollapseTreeView(TreeItem<?> item)
    {
        if (item == null || item.isLeaf())
            return;
        item.setExpanded(false);
        for (TreeItem<?> child : item.getChildren())
            CollapseTreeView(child);
    }

}
