package sugarcube.common.ui.fx.controls;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.util.Callback;
import sugarcube.common.interfaces.Resetable;

import java.util.Collection;
import java.util.Iterator;

public class FxListView<T> extends ListView<T> implements Iterable<T>, Resetable
{
    public FxListView()
    {
    }

    public void reset()
    {
        model().select(0);
    }


    public MultipleSelectionModel<T> model()
    {
        return getSelectionModel();
    }

    public FxListView<T> mulitple()
    {
        model().setSelectionMode(SelectionMode.MULTIPLE);
        return this;
    }

    public FxListView<T> cell(Callback<ListView<T>, ListCell<T>> callback)
    {
        setCellFactory(callback);
        return this;
    }

    public FxListView<T> addChangeListener(ChangeListener<T> listener)
    {
        model().selectedItemProperty().addListener(listener);
        return this;
    }

    public FxListView<T> scrollToBottom()
    {
        scrollTo(size() - 1);
        return this;
    }

    public T first()
    {
        return itemAt(0);
    }

    public T last()
    {
        return itemAt(size() - 1);
    }

    public T selectedItem()
    {
        return model().getSelectedItem();
    }

    public ObservableList<T> selectedItems()
    {
        return model().getSelectedItems();
    }

    public int size()
    {
        return items().size();
    }

    public int selectedIndex()
    {
        return model().getSelectedIndex();
    }

    public ObservableList<T> items()
    {
        return getItems();
    }

    public T itemAt(int index)
    {
        return index >= 0 && index < size() ? items().get(index) : null;
    }

    public FxListView<T> clearItems()
    {
        items().clear();
        return this;
    }

    public FxListView<T> setItems(T... items)
    {
        items().setAll(items);
        return this;
    }

    public FxListView<T> setItems(Collection<? extends T> items)
    {
        items().setAll(items);
        return this;
    }

    public FxListView<T> addItems(T... items)
    {
        items().addAll(items);
        return this;
    }

    public FxListView<T> addItem(T item)
    {
        items().add(item);
        return this;
    }

    public void setSelectedIndex(int index)
    {
        model().select(index);
    }

    public boolean isAllSelected()
    {
        return selectedItems().size() == size();
    }

    @Override
    public Iterator<T> iterator()
    {
        return items().iterator();
    }
}
