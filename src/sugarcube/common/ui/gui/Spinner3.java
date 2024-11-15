package sugarcube.common.ui.gui;

import sugarcube.common.data.collections.Set3;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;

public class Spinner3 extends Box implements ChangeListener
{
  private Set3<ChangeListener> listeners = new Set3<>();
  private Mute mute = new Mute();
  private Label3 label;
  private String cmd;
  private JSpinner spinner;
  private String text;
  private double step;
  private double init;
  private double min;
  private double max;

  public Spinner3(String text, double value, ChangeListener... listeners)
  {
    this(text, Integer.MIN_VALUE, Integer.MAX_VALUE, value, 1.0, listeners);
  }

  public Spinner3(String text, double value, double step, ChangeListener... listeners)
  {
    this(text, Integer.MIN_VALUE, Integer.MAX_VALUE, value, step, listeners);
  }

  public Spinner3(String text, int min, int max, int value, ChangeListener... listeners)
  {
    this(text, min, max, value, 1.0, listeners);
  }

  public Spinner3(String text, double min, double max, double value, double step, ChangeListener... listeners)
  {
    this(null, text, "", min, max, value, step, listeners);
  }

  public Spinner3(Icon icon, String text, String description, double min, double max, double value, double step, ChangeListener... listeners)
  {
    super(BoxLayout.X_AXIS);
    this.text = text;
    this.min = min;
    this.max = max;
    this.init = value;
    this.step = step;
    this.spinner = new JSpinner(new SpinnerNumberModel(value, min, max, step));
    this.spinner.setPreferredSize(new Dimension(70, this.spinner.getPreferredSize().height));
    // this.spinner.setPreferredSize(new Dimension(200,-1));
    if (icon == null)
      this.label = new Label3(text);
    else
      this.label = new Label3(icon, text);

    if (description != null && !description.isEmpty())
    {
      this.setToolTipText(description);
      this.label.setToolTipText(description);
      this.spinner.setToolTipText(description);
    }

    this.label.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
    this.label.setAlignmentY(JComponent.CENTER_ALIGNMENT);
    this.label.setHorizontalAlignment(JLabel.RIGHT);
    this.label.setHorizontalTextPosition(JLabel.RIGHT);
    this.setBorder(Border3.empty(2, 1));
    this.add(label);
    this.add(spinner);
    this.spinner.addChangeListener(this);
    for (ChangeListener listener : listeners)
      this.addChangeListener(listener);
    this.reset();
    this.refresh();
    this.addMouseWheelListener(new WheelListener());
  }

  public boolean isMuted()
  {
    return mute.isOn();
  }

  public void setActionCommand(String cmd)
  {
    this.cmd = cmd;
  }

  public String getActionCommand()
  {
    return this.cmd;
  }

  @Override
  public void setEnabled(boolean enable)
  {
    super.setEnabled(enable);
    label.setEnabled(enable);
    spinner.setEnabled(enable);
  }

  public Spinner3 labelSize(int w, int h)
  {
    return this.labelSize(new Dimension(w, h));
  }

  public Spinner3 labelSize(Dimension dim)
  {
    this.label.setMinimumSize(dim);
    this.label.setPreferredSize(dim);
    this.label.setMaximumSize(dim);
    return this;
  }

  public Spinner3 spinnerSize(int w, int h)
  {
    return this.spinnerSize(new Dimension(w, h));
  }

  public Spinner3 spinnerSize(Dimension dim)
  {
    this.spinner.setMinimumSize(dim);
    this.spinner.setPreferredSize(dim);
    this.spinner.setMaximumSize(dim);
    return this;
  }

  public Spinner3 size(Dimension label, Dimension spinner)
  {
    this.labelSize(label);
    this.spinnerSize(spinner);
    return this;
  }

  public Spinner3 size(int label, int spinner, int h)
  {
    this.labelSize(label, h);
    this.spinnerSize(spinner, h);
    return this;
  }

  public Spinner3 size(int w, int h)
  {
    return this.size(new Dimension(w, h));
  }

  public Spinner3 size(Dimension dim)
  {
    this.setMinimumSize(dim);
    this.setPreferredSize(dim);
    this.setMaximumSize(dim);
    return this;
  }

  public void setLabel(String text)
  {
    this.label.setText(text);
  }

  public Label3 getLabel()
  {
    return label;
  }

  public class WheelListener extends MouseAdapter
  {
    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
      int ticks = e.getWheelRotation();
      if (e.isShiftDown())
        ticks *= 1000;
      else if (e.isControlDown())
        ticks *= 100;
      else if (e.isAltDown())
        ticks *= 10;

      if (ticks > 0)
        while (ticks-- > 0)
          decValue();
      else
        while (ticks++ < 0)
          incValue();
    }
  }

  @Override
  public void enable()
  {
    this.spinner.setEnabled(true);
    this.mute.setOff();
  }

  @Override
  public void disable()
  {
    this.spinner.setEnabled(false);
    this.mute.setOn();
  }

  public void setEnbabled(boolean enabled)
  {
    this.spinner.setEnabled(enabled);
    this.mute.set(!enabled);

  }

  public JSpinner slider()
  {
    return spinner;
  }

  public String text()
  {
    return text.trim();
  }

  public void setValue(boolean triggerAction, double value)
  {
    if (triggerAction)
      setValue(value);
    else
    {
      boolean wasMuted = this.mute.isOn();
      this.mute.setOn();
      this.setValue(value);
      this.mute.set(wasMuted);
    }
  }

  public void setValue(double value)
  {
    if (value >= min && value <= max)
      this.spinner.setValue(value);
  }

  public void incValue()
  {
    this.setValue(this.value() + step);
  }

  public void decValue()
  {
    this.setValue(this.value() - step);
  }

  public double value()
  {
    return (Double) spinner.getValue();
  }

  public int intValue()
  {
    return (int) Math.round(value());
  }

  public String stringValue()
  {
    return "" + value();
  }

  public void reset()
  {
    this.spinner.setValue(init);
    this.refresh();
  }

  public void refresh()
  {
    this.spinner.repaint();
  }

  public Spinner3 addChangeListener(ChangeListener... listeners)
  {
    this.listeners.addAll(listeners);
    return this;
  }

  @Override
  public void stateChanged(ChangeEvent e)
  {
    this.refresh();
    if (mute.isOff())
      for (ChangeListener listener : listeners)
        listener.stateChanged(e);
  }

  @Override
  public String toString()
  {
    return text.trim();
  }

  public JTextField getTextField()
  {
    return ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
  }
}
