package sugarcube.common.ui.fx.menus;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import sugarcube.common.graphics.Color3;
import sugarcube.common.interfaces.Creator;
import sugarcube.common.ui.fx.event.FxAction;
import sugarcube.formats.pdf.resources.icons.Icon;

public class FxMenu extends Menu
{
    public FxMenu()
    {
    }

    public FxMenu(String text)
    {
        super(text);
    }

    public FxMenu(String text, Node graphic)
    {
        super(text, graphic);
    }

    public boolean isPopulated()
    {
        return !this.getItems().isEmpty();
    }

    public ObservableList<MenuItem> items()
    {
        return this.getItems();
    }

    public MenuItem lastItem()
    {
        return items().get(items().size() - 1);
    }

    public FxMenuItem item(String text)
    {
        FxMenuItem item = new FxMenuItem(text);
        this.items().add(item);
        return item;
    }

    public FxMenuItem item(String text, Node node)
    {
        FxMenuItem item = new FxMenuItem(text, node);
        this.items().add(item);
        return item;
    }

    public FxMenuItem item(String text, Node node, KeyCombination accelerator)
    {
        FxMenuItem item = new FxMenuItem(text, node, accelerator);
        this.items().add(item);
        return item;
    }

    public MenuItem item(MenuItem item)
    {
        if (item == null)
            this.separator();
        else
            this.items().add(item);
        return item;
    }

    public FxMenuItem item(FxMenuItem item)
    {
        if (item == null)
            this.separator();
        else
            this.items().add(item);
        return item;
    }

    public FxMenuItem item(FxAction action)
    {
        return this.item(action.menuItem());
    }

    public FxMenu sepItems(Object... objects)
    {
        return sep().items(objects);
    }

    public FxMenu items(int index, int size, Creator<Integer, Object> c)
    {
        for (int i = index; i < index + size; i++)
            items(c.create(i));
        return this;
    }

    public FxMenu radios(int selectedIndex, RadioMenuItem... items)
    {
        ToggleGroup tg = new ToggleGroup();
        for (int i = 0; i < items.length; i++)
        {
            if (i == selectedIndex)
                items[i].setSelected(true);
            items[i].setToggleGroup(tg);
            this.item(items[i]);
        }
        return this;
    }

    public FxMenu items(Object... objects)
    {
        for (Object o : objects)
        {
            if (o == null)
                this.separator();
            else if (o instanceof String)
            {
                String text = (String) o;
                if (text.trim().isEmpty())
                    this.separator();
                else
                    this.item(new FxAction(" " + text + " ", Icon.INFO_CIRCLE.get(14, Color3.GRAY)).setAction(null).menuItem());
            } else if (o instanceof FxAction)
                this.item(((FxAction) o).menuItem());
            else if (o instanceof MenuItem)
                this.item((MenuItem) o);
            else
                this.separator();
        }
        return this;
    }

    public FxMenu messageIfNoItem()
    {
        if (!this.isPopulated())
            this.items("No action currently available");
        return this;
    }

    public FxMenu sep()
    {
        return separator();
    }

    public FxMenu separator()
    {
        if (!items().isEmpty() && !(lastItem() instanceof SeparatorMenuItem))
            items().add(new SeparatorMenuItem());
        return this;
    }

    public FxMenu menu(String name, Node icon)
    {
        FxMenu menu = new FxMenu(name, icon);
        this.item(menu);
        return menu;
    }
}
