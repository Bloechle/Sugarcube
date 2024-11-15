package sugarcube.common.ui.fx.controls;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import sugarcube.common.data.collections.StringMap;
import sugarcube.common.ui.fx.containers.FxVBox;


public class FxToolBar extends ToolBar
{
    private transient FxVBox lastBox = null;
    private transient StringMap<ToggleGroup> toggleMap = new StringMap<>();

    public FxToolBar()
    {
        newColumn();
    }

    public void setHeight(int value)
    {
        setMinHeight(value);
        setPrefHeight(value);
        setMaxHeight(value);
    }

    public void setPadding(int value)
    {
        setPadding(new Insets(value, value, value, value));
    }

    public void clear()
    {
        getItems().clear();
        newColumn();
    }

    public FxToolBar newColumn()
    {
        FxVBox box = new FxVBox();
        box.setPadding(new Insets(0, 20, 0, 10));
        box.setAlignment(Pos.CENTER_LEFT);
        box.setSpacing(5);
        getItems().add(box);
        lastBox = box;
        return this;
    }

    public void addControl(Region region)
    {
        lastBox.add(region);
    }

    public Button addButton(String label, EventHandler<ActionEvent> eventHandler)
    {
        FxButton button = new FxButton(" " + label + " ");
        if (eventHandler != null)
            button.setOnAction(eventHandler);
        button.setMinHeight(40);
        lastBox.add(button);
        return button;
    }

    public Slider addSlider(String label, int min, int max, int value)
    {
        FxSlider slider = new FxSlider(min, max, value).ticks(1, (max - min) / 4, true);
        lastBox.add(slider.label(label));
        lastBox.add(slider);
        return slider;
    }

    public CheckBox addCheckBox(String label, boolean isSelected)
    {
        FxCheckBox checkBox = new FxCheckBox(label, isSelected);
        lastBox.add(checkBox);
        return checkBox;
    }

    public RadioButton addRadioButton(String label, boolean isSelected, String groupName)
    {
        ToggleGroup group = toggleMap.get(groupName, null);
        if (group == null)
            toggleMap.put(groupName, group = new ToggleGroup());
        FxRadioButton radio = new FxRadioButton(label, isSelected);
        radio.setToggleGroup(group);
        lastBox.add(radio);
        return radio;
    }

    public <T> ListView<T> addListView(T... items)
    {
        FxListView<T> list = new FxListView<>();
        list.addItems(items);
        lastBox.add(list);
        return list;
    }
}
