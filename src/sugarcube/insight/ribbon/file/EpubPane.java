package sugarcube.insight.ribbon.file;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.Color3;
import sugarcube.common.interfaces.Progressable;
import sugarcube.common.system.process.Progression;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.formats.epub.EPubProps;
import sugarcube.formats.epub.replica.ReplicaFx;
import sugarcube.insight.core.FxEnvironment;
import sugarcube.insight.interfaces.FxPaneLoader;
import sugarcube.formats.pdf.resources.icons.Icon;

public class EpubPane extends FxEnvironmentPane implements Progression.Listener {
    public static FxPaneLoader LOADER = env -> new EpubPane(env);

    private @FXML ToggleGroup layoutGroup;
    private @FXML RadioButton fixedRadio;
    private @FXML RadioButton liquidRadio;

    private @FXML AnchorPane fixedPane;
    private @FXML AnchorPane liquidPane;
    private @FXML CheckBox rasterizeCheck;
    private @FXML Slider jpegSlider;
    private @FXML Label jpegLabel;

    private @FXML CheckBox ocrVectorCheck;
    private @FXML CheckBox embedFontCheck;

    private @FXML Slider fontsizeSlider;
    private @FXML Label fontsizeLabel;
    private @FXML TextField splitField;
    private @FXML TextField skipField;
    private @FXML CheckBox justifyCheck;
    private @FXML Slider depthSlider;
    private @FXML CheckBox seqNavCheck;

    private @FXML Button convertBt;

    public EpubPane(FxEnvironment env) {
        super(env, "ePub", "ePub Export", Icon.Awesome(Icon.FILE_IMAGE_ALT, 36, Color3.ANTHRACITE));

        fontsizeLabel.textProperty().bind(Bindings.format("%.1f pt", fontsizeSlider.valueProperty()));
        jpegLabel.textProperty().bind(Bindings.format("%.1f%% quality", jpegSlider.valueProperty()));

        Fx.MaximizeWidth(convertBt);

        convertBt.setOnAction(e -> {
            EPubProps props = new EPubProps();
            props.set(EPubProps.KEY_VEC_GRAPHICS, !rasterizeCheck.isSelected());
            props.set(EPubProps.KEY_OUTPUT, EPubProps.OUTPUT_EPUB3);
            props.jpegPercent(jpegSlider.getValue());

            if (ocrVectorCheck.isSelected())
                props.set(EPubProps.KEY_OCR_MODE, EPubProps.OCR_MODE_VECTOR);
            if (this.embedFontCheck.isSelected())
                props.set(EPubProps.KEY_FONT_64, true);
            new ReplicaFx(env, props, false, null, this);

            Log.debug(this, " - epubProps: " + props);
        });

        layoutGroup.selectedToggleProperty().addListener((obs, old, val) -> {
            boolean fixed = fixedLayout();
            fixedPane.setVisible(fixed);
            liquidPane.setVisible(!fixed);
        });

        jpegLabel.textProperty().bind(Bindings.format("JPEG Quality (%.1f%%)", jpegSlider.valueProperty()));

        Fx.BindLayoutToVisibility(fixedPane, liquidPane);
        fixedPane.setVisible(true);
        liquidPane.setVisible(false);
    }

    public boolean fixedLayout() {
        return fixedRadio.isSelected();
    }

    public void select(ListView<String> list, String... values) {
        for (String v : values)
            list.getSelectionModel().select(v);
    }

    public void remap(EPubProps props, String html, ListView<String> list) {
        for (String style : list.getSelectionModel().getSelectedItems())
            props.setLiquidRemap(style.replace(' ', '_'), html);
    }

    @Override
    public void progress(Progressable progression) {
        env.gui.glassPane.progress(progression.progress());
        env.message(progression.progressDescription(), 0, null);
        if (progression.isProgressComplete())
            message("ePub File Written", 2, true, null);
    }
}
