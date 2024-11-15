package sugarcube.common.ui.fx.controls;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.control.Slider;
import sugarcube.common.interfaces.Resetable;

public class FxSlider extends Slider implements Resetable
{
    private String textLabel = "";
    private FxLabel label = null;
    private double defaultValue = 0;

    public FxSlider()
    {
    }

    public FxSlider(double min, double max, double value)
    {
        setMin(min);
        setMax(max);
        setValue(value);
        defaultValue = value;
    }

    public FxLabel label(String text)
    {
        this.textLabel = text;
        if (label == null)
            label = new FxLabel();
        refreshLabel();
        return label;
    }

    public String getTextLabel()
    {
        return textLabel;
    }

    public void refreshLabel()
    {
        label.setText(textLabel + "   " + intValue());
    }

    public void reset()
    {
        setValue(defaultValue);
    }

    public FxSlider width(double width)
    {
        this.setPrefWidth(width);
        this.setMaxWidth(width);
        return this;
    }

    public int intValue()
    {
        return (int) Math.round(getValue());
    }

    public FxSlider height(double height)
    {
        this.setPrefHeight(height);
        this.setMaxHeight(height);
        return this;
    }

    public FxSlider vertical()
    {
        this.setOrientation(Orientation.VERTICAL);
        return this;
    }

    public FxSlider min(double min)
    {
        this.setMin(min);
        return this;
    }

    public FxSlider max(double max)
    {
        this.setMax(max);
        return this;
    }

    public FxSlider value(double value)
    {
        this.setValue(value);
        return this;
    }

    public FxSlider hideTickAndValues()
    {
        return showMarks(false).showValues(false);
    }

    public FxSlider hideValues()
    {
        return showValues(false);
    }

    public FxSlider showMarks(boolean doShow)
    {
        this.setShowTickMarks(doShow);
        return this;
    }

    public FxSlider showValues(boolean doShow)
    {
        this.setShowTickLabels(doShow);
        return this;
    }

    public FxSlider ticks(int interval, int unit, boolean showValues)
    {
        this.showMarks(true);
        this.showValues(showValues);
        this.setMinorTickCount(interval);
        this.setMajorTickUnit(unit);
        return this;
    }

    public FxSlider snap()
    {
        this.setSnapToTicks(true);
        return this;
    }

    public FxSlider inc(double increment)
    {
        this.setBlockIncrement(increment);
        return this;
    }

    public FxSlider listen(ChangeListener<Number> listener)
    {
        this.valueProperty().addListener(listener);
        return this;
    }

}
