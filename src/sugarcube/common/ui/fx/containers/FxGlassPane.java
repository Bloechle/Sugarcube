package sugarcube.common.ui.fx.containers;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import sugarcube.common.graphics.Color3;
import sugarcube.common.interfaces.Actable;
import sugarcube.common.interfaces.Progressable;
import sugarcube.common.system.process.Progression;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.controls.FxLabel;
import sugarcube.common.ui.fx.task.FxTimer;
import sugarcube.insight.core.IS;

public class FxGlassPane implements Progression.Listener
{
    private static final Effect FROST_EFFECT = new BoxBlur(8, 8, 3);
    private final FxStackPane rootPane, stackPane;
    protected FxBorderPane contentPane;
    protected ProgressIndicator indicator;
    protected ProgressBar bar;
    protected FxTimer showTimer = new FxTimer(true);
    protected FxTimer hideTimer = new FxTimer(false);
    protected FxLabel msgLabel = new FxLabel();
    protected FxBorderPane msgLabelBox = new FxBorderPane();

    public FxGlassPane(FxStackPane rootPane)
    {
        this.rootPane = rootPane;
        this.stackPane = new FxStackPane();
        this.contentPane = new FxBorderPane().style("sc-modal-progress");
        this.indicator = new ProgressIndicator(-1);
        this.indicator.setMaxSize(200, 200);
        this.indicator.setStyle("-fx-accent: " + IS.GUI_BLUE);
        this.bar = new ProgressBar();
        this.bar.setStyle("-fx-accent: " + IS.GUI_BLUE);
        this.contentPane.widthProperty().addListener((v, o, n) -> bar.setMinWidth(contentPane.getWidth()));
        this.stackPane.setMouseTransparent(true);
        this.contentPane.setMouseTransparent(true);


        msgLabelBox.setStyle("-fx-padding: 150px;");
        msgLabel.setStyle("-fx-font-size: 24px; -fx-padding: 16px; -fx-border-radius: 8px; -fx-text-fill: white;");
        DropShadow shadow = new DropShadow();
        shadow.setRadius(3.0);
        shadow.setOffsetX(0.0);
        shadow.setOffsetY(0.0);
        shadow.setColor(Color3.BLACK.fx());
        msgLabel.setEffect(shadow);
        msgLabelBox.setCenter(msgLabel);
        BorderPane.setAlignment(msgLabelBox, Pos.CENTER);
        BorderPane.setAlignment(msgLabel, Pos.CENTER);
    }

    private StackPane freeze(FxStackPane bg)
    {
        Image frostImage = bg.snapshot(new SnapshotParameters(), null);
        ImageView frost = new ImageView(frostImage);
        Pane frostPane = new Pane(frost);
        frostPane.setEffect(FROST_EFFECT);
        StackPane frostView = new StackPane(frostPane);
        Rectangle clipShape = new Rectangle(0, 0, bg.getWidth(), bg.getHeight());
        frostView.setClip(clipShape);
        // clipShape.yProperty().bind(y);
        return frostView;
    }

    public FxGlassPane show(boolean doShow)
    {
        return doShow ? need(true, null) : hide();
    }

    public FxGlassPane need(boolean showProcessing, Node topNode)
    {
        if (!isPaneActive())
        {
            rootPane.getChildren().add(stackPane);
            stackPane.clear();
            contentPane.setTop(topNode);
            contentPane.setCenter(null);
            contentPane.setBottom(null);

            if (showProcessing)
            {
                contentPane.setStyle("-fx-background-color: rgba(255,255,255,0.5);");
                StackPane freeze = freeze(rootPane);
                stackPane.add(freeze);
                stackPane.add(contentPane);
                FadeTransition ft = new FadeTransition(Duration.millis(200), stackPane);
                ft.setFromValue(0.0);
                ft.setToValue(1.0);
                ft.play();
                indicator.setProgress(-1);
                contentPane.setCenter(indicator);
                contentPane.setBottom(bar);
                bar.setOpacity(0);
                bar.setMinWidth(contentPane.getWidth());

            } else
            {
                stackPane.setOpacity(1.0);
                contentPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
                stackPane.add(contentPane);
            }

            return this;
        }
        return this;
    }

//    public FxGlassPane message(String message, Boolean happy)
//    {
//        return this.message(message, 0, happy, null);
//    }


    public FxGlassPane message(String message, double seconds, Boolean happy, Actable onHidden)
    {
        if (message == null || message.isEmpty())
        {
            hide(0.1, null);
        } else
            showTimer.shot(0.1, e ->
            {
                msgLabel.setText("  " + message + "  ");
                Color3 color = happy == null ? Color3.STEEL_BLUE : happy ? Color3.GREEN_LEAF : Color3.ORANGE;
                msgLabel.setBackground(color.alpha(0.9).fxBackground(8, 8));
                need(false, msgLabelBox);
                if (seconds > 0)
                    hide(seconds, onHidden);
            });
        return this;
    }

    public FxGlassPane progress(double v)
    {
        Fx.Run(() ->
        {
            if (v > 0)
            {
                bar.setOpacity(1.0);
                bar.setProgress(v);
            } else
            {
                bar.setOpacity(0.0);
                bar.setProgress(-1);
            }
        });
        return this;
    }

    @Override
    public void progress(Progressable progression)
    {
        bar.setProgress(progression.progress());
    }

    public void hide(double seconds, Actable onHidden)
    {
        hideTimer.shot(seconds, e ->
        {
            hide();
            if (onHidden != null)
                onHidden.act();
//      FadeTransition fade = new FadeTransition(Duration.seconds(0.25), stack);
//      fade.setFromValue(1);
//      fade.setToValue(0);
//      fade.play();
//      fade.setOnFinished(ee -> {
//        hide();
//        if (onHidden != null)
//          onHidden.act();
//      });
        });

    }

    public FxGlassPane hide()
    {
        if (isPaneActive())
        {
            msgLabel.setText("");
            stackPane.getChildren().clear();
            rootPane.getChildren().remove(stackPane);
            return this;
        }
        return null;
    }

    public boolean isPaneActive()
    {
        for (Node node : rootPane.getChildren())
            if (node == stackPane)
                return true;
        return false;
    }

}
