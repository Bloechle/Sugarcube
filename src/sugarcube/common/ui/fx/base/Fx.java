package sugarcube.common.ui.fx.base;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.util.Duration;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.IntSet;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Str;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.interfaces.Handable;
import sugarcube.common.interfaces.Refreshable;
import sugarcube.common.interfaces.Unjammable;
import sugarcube.common.system.io.Class3;
import sugarcube.common.system.process.EnumState;
import sugarcube.common.system.reflection.Reflect;
import sugarcube.common.ui.fx.controls.FxLabel;
import sugarcube.common.ui.fx.controls.FxSwitch;
import sugarcube.common.ui.fx.dialogs.FxFileChooser;
import sugarcube.common.ui.fx.fluent.*;
import sugarcube.common.ui.fx.interfaces.FXRefreshable;
import sugarcube.common.ui.fx.interfaces.ValueListener;
import sugarcube.common.ui.fx.task.FxTimer;
import sugarcube.common.data.xml.Nb;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Arrays;

public class Fx implements Unjammable
{

    public static final javafx.scene.paint.Color GLASS = new javafx.scene.paint.Color(1, 1, 1, 0);

    public static double Double(TextField field, double def)
    {
        return Nb.Double(field.getText(), def);
    }

    public static Rectangle3 Rect(TextField x, TextField y, TextField w, TextField h)
    {
        return new Rectangle3(Fx.Real(x), Fx.Real(y), Fx.Real(w), Fx.Real(h));
    }

    public static void Border(Node node, String color, int width)
    {
        node.setStyle("-fx-border-color: " + color + "; -fx-border-width: " + width + "px; -fx-border-style: solid;");
    }

    public static void MaximizeWidth(Control... nodes)
    {
        for (Control node : nodes)
            node.setPrefWidth(10000);
    }

    public static void DisplayNone(Node... nodes)
    {
        for (Node node : nodes)
        {
            node.managedProperty().set(false);
            node.visibleProperty().set(false);
        }
    }

    public static void BindLayoutToVisibility(Node... nodes)
    {
        for (Node node : nodes)
            node.managedProperty().bind(node.visibleProperty());
    }

    public static WritableImage CaptureImage(Node node)
    {
        return node.snapshot(new SnapshotParameters(), null);
    }

    public static BufferedImage CaptureBufferedImage(Node node)
    {
        return Fx.toBufferedImage(CaptureImage(node));
    }

    public static Background Background(javafx.scene.paint.Paint paint)
    {
        return new Background(new BackgroundFill(paint, null, null));
    }

    public static FadeTransition Fade(Node node, double seconds, boolean fadeIn)
    {
        FadeTransition fade = new FadeTransition(Duration.seconds(seconds), node);
        fade.setFromValue(fadeIn ? 0 : 1);
        fade.setToValue(fadeIn ? 1 : 0);
        fade.play();
        return fade;
    }

    public static FadeTransition FadeIn(Node node, double seconds)
    {
        return Fade(node, seconds, true);
    }

    public static FadeTransition FadeOut(Node node, double seconds)
    {
        return Fade(node, seconds, false);
    }

    public static FluentButton Fluent(javafx.scene.control.Button button)
    {
        return new FluentButton(button);
    }

    public static FluentLV Fluent(ListView view)
    {
        return new FluentLV(view);
    }

    public static FluentStringListView FluentString(ListView<String> view)
    {
        return new FluentStringListView(view);
    }

    public static FluentStringComboBox FluentString(ComboBox<String> box)
    {
        return new FluentStringComboBox(box);
    }

    public static FluentComboBox<String> Fluent(ComboBox<String> box)
    {
        return new FluentComboBox<String>(box);
    }

    public static FluentComboBox<String> Set(ComboBox<String> box, String[] items, String def, Handable<String> listener)
    {
        return new FluentComboBox<>(box).items(items).def(def).listen((obs, old, val) -> listener.handle(val));
    }

    public static FluentComboBox<String> Init(ComboBox<String> box, String[] items, String def, ChangeListener<String> listener)
    {
        return new FluentComboBox<>(box).items(items).def(def).listen(listener);
    }

    public static FluentComboBox<Number> FluentNb(ComboBox<Number> box)
    {
        return new FluentComboBox<Number>(box);
    }

    public static FluentTextField Fluent(TextField field)
    {
        return new FluentTextField(field);
    }

    public static FxFileChooser Chooser(String title)
    {
        return new FxFileChooser(title);
    }

    public static FxTimer Run(double seconds, Runnable runnable)
    {
        return FxTimer.Shot(seconds, e -> runnable.run());
    }

    public static FxTimer Repeat(double seconds, Runnable runnable)
    {
        return FxTimer.Repeat(seconds, e -> runnable.run());
    }

    public static void RunLater(Runnable runnable)
    {
        Platform.runLater(runnable);
    }

    public static void Run(Runnable runnable)
    {
        if (FxApp.IsOnFX())
            runnable.run();
        else
            Platform.runLater(runnable);
    }

    public static void RunTry(Runnable runnable)
    {
        try
        {
            Run(runnable);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void Run(boolean condition, Runnable runnable)
    {
        if (condition)
            Run(runnable);
    }

    public static void Run(boolean condition, Runnable runnable, Runnable elseRunnable)
    {
        Run(condition ? runnable : elseRunnable);
    }

    public static void Runs(Runnable... runnables)
    {
        Runs(0, runnables);
    }

    public static void Runs(int index, Runnable... runnables)
    {
        if (index < runnables.length)
            Fx.Run(() ->
            {
                runnables[index].run();
                Runs(index + 1, runnables);
            });
    }

    public static void AddCSS(Parent parent, Object o, String filename)
    {
        URL root = Class3.Url(o, filename);
        if (root != null)
            parent.getStylesheets().add(root.toExternalForm());
        else
            Log.debug(Fx.class, ".AddCSS - no css: " + filename);
    }

    public static boolean AddCSS(Parent parent, Object controller, String name, String ext)
    {
        URL css = Class3.Url(controller, name, ext);
        if (css != null)
        {
            parent.getStylesheets().add(css.toExternalForm());
            return true;
        } else
            Log.debug(Fx.class, ".AddCSS - css not found: " + name);
        return false;
    }

    public static Parent FxmlLoad(URL fxml, Object controller)
    {
        try
        {
            if (fxml != null)
            {
                FXMLLoader loader = new FXMLLoader(fxml);
                loader.setController(controller);
                Parent pane = loader.load();
                return pane;
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    public static Parent Fxml(Object controller)
    {
        return Fxml(controller, true);
    }

    public static Parent Fxml(Object controller, boolean withCSS)
    {
        return Fxml(controller.getClass().getSimpleName(), controller, withCSS);
    }

    public static Parent Fxml(String name, Object controller, boolean withCSS)
    {
        try
        {
            URL fxml;
            for (String prefix : new String[]{"", "fxml/", "../fxml/"})
                if ((fxml = Class3.Url(controller, prefix + name, ".fxml")) != null)
                {
                    Parent pane = FxmlLoad(fxml, controller);
                    if (withCSS)
                        Fx.AddCSS(pane, controller, name, ".css");
                    return pane;
                }
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        Log.debug(Fx.class, ".Fxml - file not found: " + controller.getClass() + ".fxml ");
        return null;
    }

    public static void Tooltip(String text, Control node)
    {
        Tip(text, node);
    }

    public static void Tip(String text, Control node)
    {
        node.setTooltip(new Tooltip(text));
    }

    public static void Listen(javafx.scene.control.Slider slider, ValueListener<Number> listener)
    {
        slider.valueProperty().addListener((obs, old, val) -> listener.valueChanged(val));
    }

    public static void Listen(TextField field, ValueListener<String> listener)
    {
        field.textProperty().addListener((obs, old, val) -> listener.valueChanged(val));
    }

    public static void ListenNorm(ChangeListener<Number> listener, javafx.scene.control.Slider... sliders)
    {
        if (sliders.length == 0)
            Log.warn(Fx.class, ".ListenNorm - sliders are empty !");
        for (javafx.scene.control.Slider slider : sliders)
            slider.valueProperty()
                    .addListener((obs, old, val) -> listener.changed(obs, old.doubleValue() / slider.getMax(), val.doubleValue() / slider.getMax()));
    }

    public static void Listen(ChangeListener<Number> listener, javafx.scene.control.Slider... sliders)
    {
        for (javafx.scene.control.Slider slider : sliders)
            slider.valueProperty().addListener((obs, old, val) -> listener.changed(obs, old, val));
    }

    public static void Listen(ChangeListener<Number> listener, int granularity, javafx.scene.control.Slider... sliders)
    {
        for (javafx.scene.control.Slider slider : sliders)
        {
            slider.valueProperty().addListener((obs, old, val) ->
            {
                if (granularity <= 0 || Math.round(granularity * old.floatValue()) != Math.round(granularity * val.floatValue()))
                    listener.changed(obs, old, val);
            });
        }
    }

    public static void Listen(ChangeListener<String> listener, TextInputControl... fields)
    {
        for (TextInputControl field : fields)
            field.textProperty().addListener(listener);
    }

    public static void Listen(ChangeListener<String> listener, ComboBox<String>... combos)
    {
        for (ComboBox<String> combo : combos)
            combo.getSelectionModel().selectedItemProperty().addListener(listener);
    }

    public static void Listen(ChangeListener<Boolean> listener, CheckMenuItem... boxes)
    {
        for (CheckMenuItem box : boxes)
            box.selectedProperty().addListener(listener);
    }

    public static void Listen(ChangeListener<Boolean> listener, RadioMenuItem... radios)
    {
        for (RadioMenuItem box : radios)
            box.selectedProperty().addListener(listener);
    }

    public static void Listen(ChangeListener<Boolean> listener, javafx.scene.control.CheckBox... boxes)
    {
        for (javafx.scene.control.CheckBox box : boxes)
            box.selectedProperty().addListener(listener);
    }

    public static void Listen(ChangeListener<Boolean> listener, javafx.scene.control.RadioButton... radios)
    {
        for (javafx.scene.control.RadioButton box : radios)
            box.selectedProperty().addListener(listener);
    }

    public static void SetOnAction(EventHandler<ActionEvent> handler, ButtonBase... buttons)
    {
        if (handler != null)
            for (ButtonBase bt : buttons)
                bt.setOnAction(handler);
    }

    public static void UnselectAll(ToggleButton... buttons)
    {
        Fx.Run(() ->
        {
            for (ToggleButton bt : buttons)
                bt.setSelected(false);
        });
    }

    public static int SelectedIndex(ToggleButton... buttons)
    {
        for (int i = 0; i < buttons.length; i++)
            if (buttons[i].isSelected())
                return i;
        return -1;
    }

    public static void SelectIndex(int index, ToggleButton... buttons)
    {
        if (index >= 0 && index < buttons.length)
            Fx.Run(() -> buttons[index].setSelected(true));
    }

    public static Object Value(Object o)
    {
        if (o instanceof ComboBoxBase)
            return ((ComboBoxBase) o).getValue();
        else if (o instanceof ColorPicker)
            return ((ColorPicker) o).getValue();
        else if (o instanceof javafx.scene.control.Slider)
            return ((javafx.scene.control.Slider) o).getValue();
        return o;
    }

    public static ComboBox Init(ComboBox combo, Object def, Object... items)
    {
        return Init(null, combo, def, items);
    }

    public static ComboBox<String> Init(ComboBox<String> combo, String def, String... items)
    {
        return Init(null, combo, def, items);
    }

    public static ComboBox Init(ChangeListener e, ComboBox combo, Object... items)
    {
        return Init(e, combo, items[0], items);
    }

    public static ComboBox Init(ChangeListener e, ComboBox combo, Object def, Object... items)
    {
        combo.getItems().clear();
        if (def != null)
            combo.setValue(def);
        combo.getItems().addAll(items);
        if (e != null)
            combo.getSelectionModel().selectedItemProperty().addListener(e);
        return combo;
    }

    public static ComboBox<String> Init(ChangeListener<String> e, ComboBox<String> combo, String def, String... items)
    {
        combo.getItems().clear();
        if (def != null)
            combo.setValue(def);
        combo.getItems().addAll(items);
        if (e != null)
            combo.getSelectionModel().selectedItemProperty().addListener(e);
        return combo;
    }

    public static ComboBox<Number> Init(ChangeListener<Number> e, ComboBox<Number> combo, Number def, Number... items)
    {
        combo.getItems().clear();
        if (def != null)
            combo.setValue(def);
        combo.getItems().addAll(items);
        if (e != null)
            combo.getSelectionModel().selectedItemProperty().addListener(e);
        return combo;
    }

    public static ChoiceBox Init(ChoiceBox choice, Object def, Object... items)
    {
        return Init(null, choice, def, items);
    }

    public static ChoiceBox Init(ChangeListener e, ChoiceBox choice, Object def, Object... items)
    {
        choice.getItems().clear();
        if (def != null)
            choice.setValue(def);
        choice.getItems().addAll(items);
        if (e != null)
            choice.getSelectionModel().selectedItemProperty().addListener(e);
        return choice;
    }

    public static ListView Init(ListView list, Object def, Object... items)
    {
        return Init(null, list, def, items);
    }

    public static ListView Init(ChangeListener e, ListView list, Object def, Object[] items)
    {
        list.getItems().clear();
        list.getItems().addAll(items);
        list.getSelectionModel().select(def);
        if (e != null)
            list.getSelectionModel().selectedItemProperty().addListener(e);
        return list;
    }

    public static ListView<String> InitList(ChangeListener<String> e, ListView<String> list, String def, String... items)
    {
        list.getItems().clear();
        list.getItems().addAll(items);
        if (Zen.hasData(def))
            list.getSelectionModel().select(def);
        if (e != null)
            list.getSelectionModel().selectedItemProperty().addListener(e);
        return list;
    }

    public static void SetVisible(boolean visible, Node... nodes)
    {
        for (Node node : nodes)
            node.setVisible(visible);
    }

    public static javafx.scene.paint.Color Color(float r, float g, float b, float a)
    {
        return new javafx.scene.paint.Color(r, g, b, a);
    }

    public static String[] Strings(ListView<String> list)
    {
        return list.getSelectionModel().getSelectedItems().toArray(new String[0]);
    }

    public static String String(ComboBox<String> combo)
    {
        return combo.getSelectionModel().getSelectedItem();
    }

    public static void Set(ComboBox<String> combo, String value)
    {
        combo.getSelectionModel().select(value);
    }

    public static int Int(ComboBox<Integer> combo)
    {
        return combo.getSelectionModel().getSelectedItem();
    }

    public static int Int(ComboBox<String> combo, int def)
    {
        return Nb.Int(String(combo), def);
    }

    public static int Int(TextField field, int def)
    {
        return Nb.Int(field.getText(), def);
    }

    public static String String(ChoiceBox<String> combo)
    {
        return combo.getSelectionModel().getSelectedItem();
    }

    public static void Set(ChoiceBox<String> combo, String value)
    {
        combo.getSelectionModel().select(value);
    }

    public static int Int(ChoiceBox<Integer> combo)
    {
        return combo.getSelectionModel().getSelectedItem();
    }

    public static int Int(ChoiceBox<String> combo, int def)
    {
        return Nb.Int(String(combo), def);
    }

    public static float Real(javafx.scene.control.Slider slider, float norm, int granularity)
    {
        return (Math.round(slider.getValue() * granularity) / granularity) / norm;
    }

    public static float Real(TextField field)
    {
        return Real(field, 0);
    }

    public static float Real(TextField field, float def)
    {
        return (float) Nb.toDouble(true, field.getText(), def);
    }

    public static boolean bool(javafx.scene.control.CheckBox check)
    {
        return check.isSelected();
    }

    public static javafx.scene.image.Image toFXImage(java.awt.Image img)
    {
        return SwingFXUtils.toFXImage(castBufferedImage(img), null);
    }

    public static BufferedImage toBufferedImage(javafx.scene.image.Image imgFx)
    {
        return SwingFXUtils.fromFXImage(imgFx, null);
    }


    public static BufferedImage toBufferedImage(javafx.scene.image.Image imgFx, BufferedImage image)
    {
        return SwingFXUtils.fromFXImage(imgFx, image);
    }

    public static javafx.scene.paint.Paint fxPaint(Paint paint)
    {
        if (paint instanceof java.awt.Color)
        {
            return (paint instanceof Color3 ? (Color3) paint : new Color3((java.awt.Color) paint)).fx();
        }
        Log.debug(Fx.class, ".fxPaint - no color paint not yet implemented: " + paint);
        return null;
    }

    public static BufferedImage castBufferedImage(java.awt.Image img)
    {
        if (img instanceof BufferedImage)
            return (BufferedImage) img;
        BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return bi;
    }

    public static void Bind(Label label, javafx.scene.control.Slider slider, String format)
    {
        label.textProperty().bind(Bindings.format(format, slider.valueProperty()));
    }

    public static void Bind(Region node, Property prop)
    {
        if (prop instanceof BooleanProperty)
        {
            BooleanProperty bool = (BooleanProperty) prop;
            if (node instanceof javafx.scene.control.RadioButton)
                ((javafx.scene.control.RadioButton) node).selectedProperty().bindBidirectional(bool);
            else if (node instanceof javafx.scene.control.CheckBox)
                ((javafx.scene.control.CheckBox) node).selectedProperty().bindBidirectional(bool);
            else if (node instanceof ToggleButton)
                ((ToggleButton) node).selectedProperty().bindBidirectional(bool);
            else if (node instanceof FxSwitch)
                ((FxSwitch) node).on().bindBidirectional(bool);
            else
                Log.warn(Fx.class, ".bind - control not implemented: " + node.getClass());
        } else if (prop instanceof StringProperty)
        {
            StringProperty string = (StringProperty) prop;
            if (node instanceof ComboBox)
                ((ComboBox<String>) node).getEditor().textProperty().bindBidirectional(string);
            else if (node instanceof TextField)
                ((TextField) node).textProperty().bindBidirectional(string);
            else if (node instanceof TextArea)
                ((TextArea) node).textProperty().bindBidirectional(string);
            else if (node instanceof Label)
                ((Label) node).textProperty().bindBidirectional(string);
            else
                Log.warn(Fx.class, ".bind - control not implemented: " + node.getClass());
        } else if (prop instanceof DoubleProperty)
        {
            DoubleProperty real = (DoubleProperty) prop;
            if (node instanceof javafx.scene.control.Slider)
                ((javafx.scene.control.Slider) node).valueProperty().bindBidirectional(real);
            else
                Log.warn(Fx.class, ".bind - control not implemented: " + node.getClass());
        } else
            Log.warn(Fx.class, ".bind - property ot implemented: " + prop.getClass());
    }

    public static void BindVisibility(Node node, javafx.scene.control.CheckBox check)
    {
        BindVisibility(node, check.selectedProperty());
    }

    public static void BindVisibility(Node node, CheckMenuItem check)
    {
        BindVisibility(node, check.selectedProperty());
    }

    public static void BindVisibility(Node node, ToggleButton tg)
    {
        BindVisibility(node, tg.selectedProperty());
    }

    public static void BindVisibility(Node node, BooleanProperty prop)
    {
        node.visibleProperty().bind(prop);
    }

    public static void width(double width, Region... regions)
    {
        for (Region region : regions)
        {
            region.setMinWidth(width);
            region.setPrefWidth(width);
            region.setMaxWidth(width);
        }
    }

    public static void height(double height, Region... regions)
    {
        for (Region region : regions)
        {
            region.setMinHeight(height);
            region.setPrefHeight(height);
            region.setMaxHeight(height);
        }
    }

    public static void size(double width, double height, Region... regions)
    {
        width(width, regions);
        height(height, regions);
    }

    public static FxLabel Space()
    {
        return FxLabel.Space(1);
    }

    public static FxLabel Space(int size)
    {
        return FxLabel.Space(size);
    }

    public static void stretch(HBox box, Region node)
    {
        HBox.setHgrow(box, Priority.ALWAYS);
        node.setMaxWidth(Double.MAX_VALUE);
    }

    public static void changed(ChangeListener<Object> listener, Region... controls)
    {
        try
        {
            for (Node o : controls)
                if (o instanceof javafx.scene.control.CheckBox)
                    ((javafx.scene.control.CheckBox) o).selectedProperty().addListener(listener);
                else if (o instanceof javafx.scene.control.RadioButton)
                    ((javafx.scene.control.RadioButton) o).selectedProperty().addListener(listener);
                else if (o instanceof ToggleButton)
                    ((ToggleButton) o).selectedProperty().addListener(listener);
                else if (o instanceof javafx.scene.control.Button)
                    ((javafx.scene.control.Button) o).onActionProperty().addListener(listener);
                else if (o instanceof TextArea)
                    ((TextArea) o).textProperty().addListener(listener);
                else if (o instanceof TextField)
                    ((TextField) o).textProperty().addListener(listener);
                else if (o instanceof javafx.scene.control.Slider)
                    ((javafx.scene.control.Slider) o).valueProperty().addListener(listener);
                else if (o instanceof ComboBox)
                {
                    ((ComboBox<String>) o).getEditor().textProperty().addListener(listener);
                    // ((ComboBox<String>) o).onActionProperty().addListener(listener);
                } else
                    Log.warn(Fx.class, ".changed - control not yet implemented: " + o.getClass());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void disable(Region pane, BooleanExpression bool)
    {
        if (bool != null)
            pane.disableProperty().bind(bool.not());
    }

    public static void hide(Region pane, BooleanExpression bool)
    {
        if (bool != null)
        {
            pane.visibleProperty().bind(bool);
            pane.managedProperty().bind(bool);
        }
    }

    public static IntSet ParseRanges(String ranges)
    {
        return !Str.HasChar(ranges) ? new IntSet() : ParseRanges(ranges.split(","));
    }

    public static IntSet ParseRanges(String[] ranges)
    {
        if (ranges == null || ranges.length == 0)
            return new IntSet();
        IntSet set = new IntSet();
        for (String range : ranges)
            if (range.contains("-"))
            {
                String[] fromTo = range.trim().split("-");
                if (fromTo.length > 1)
                {
                    int from = Nb.Int(fromTo[0]);
                    int to = Nb.Int(fromTo[1]);
                    for (int i = from; i <= to; i++)
                        set.add(i);
                }
            } else if (!range.trim().isEmpty())
            {
                int i = Nb.Int(range);
                set.add(i);
            }
        return set.ints().sort().set();
    }

    public static void RemoveSelected(ListView list)
    {
        Integer[] indexes = (Integer[]) list.getSelectionModel().getSelectedIndices().toArray(new Integer[0]);
        Arrays.sort(indexes, (a, b) -> b.compareTo(a));
        for (Integer index : indexes)
            list.getItems().remove((int) index);
    }

    public static void SelectAll(ListView list)
    {
        list.getSelectionModel().selectAll();
    }

    public static void SelectNone(ListView list)
    {
        list.getSelectionModel().clearSelection();
    }

    public static String Selected(ComboBox<String> combo, String def)
    {
        return Str.Avoid(combo.getSelectionModel().getSelectedItem(), def);
    }

    public static int SelectedRadioIndex(int def, javafx.scene.control.RadioButton... buttons)
    {
        for (int i = 0; i < buttons.length; i++)
            if (buttons[i].isSelected())
                return i;
        return def;
    }

    public static String SelectedRadioValue(int def, String[] values, javafx.scene.control.RadioButton... buttons)
    {
        return values[SelectedRadioIndex(def, buttons)];
    }

    public static FxLabel Gap(int width)
    {
        return new FxLabel("").width(width);
    }

    public static Node Side(BorderPane pane, boolean right)
    {
        return right ? pane.getRight() : pane.getLeft();
    }

    public static void SetSide(BorderPane pane, boolean right, Node node)
    {
        if (right)
            pane.setRight(node);
        else
            pane.setLeft(node);
    }

    public static BufferedImage ScreenShot(int screenIndex)
    {
        GraphicsDevice[] screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        try
        {
            return new Robot()
                    .createScreenCapture(screens[screenIndex < screens.length ? screenIndex : screens.length - 1].getDefaultConfiguration().getBounds());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static javafx.scene.image.Image ScreenShotImage(int screenIndex)
    {
        return Fx.toFXImage(ScreenShot(screenIndex));
    }

    public static void ShowScenicViewIfAvailable(Scene scene)
    {
        Reflect.StaticMethod("org.scenicview.ScenicView", "show", scene, Scene.class);
    }

    public static void SetMaxSize(Region pane, int width, int height)
    {
        pane.setMaxWidth(width);
        pane.setMaxHeight(height);
    }

    public static void Refresh(Refreshable refreshable)
    {
        if (refreshable != null)
            refreshable.refresh();
    }

    public static void RefreshFX(FXRefreshable... fxs)
    {
        Fx.Run(() ->
                {
                    for (FXRefreshable fx : fxs)
                        fx.refreshFX();
                }
        );
    }

    public static void Enable(boolean on, Node... nodes)
    {
        Fx.Run(() ->
        {
            for (Node fx : nodes)
                if (fx.isDisable() == on)
                    fx.setDisable(!on);
        });
    }

    public static void Visible(boolean on, Node... nodes)
    {
        Fx.Run(() ->
        {
            for (Node fx : nodes)
                if (fx.isVisible() != on)
                    fx.setVisible(on);
        });
    }

    public static void EnumStateBt(javafx.scene.control.Button bt, EnumState enumState, int selectedIndex, Runnable runnable)
    {
        bt.setOnAction(e ->
        {
            bt.setText(enumState.advance().label());
            if (runnable != null)
                runnable.run();
        });

        bt.setText(enumState.selectIndex(selectedIndex).label());
    }

    public static String Trim(TextField field)
    {
        return field == null ? "" : field.getText().trim();
    }


    public static Node[] Trim(Node... nodes)
    {
        List3<Node> list = new List3();
        for (Node node : nodes)
            if (node != null)
                list.add(node);
        return list.toArray(new Node[0]);
    }
}
