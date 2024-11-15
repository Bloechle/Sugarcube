package sugarcube.insight.ribbon.file;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import sugarcube.common.graphics.geom.Dimension3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Metric;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.Paper;
import sugarcube.common.ui.fx.base.FxText;
import sugarcube.common.ui.fx.dialogs.FxFileChooser;
import sugarcube.insight.core.FxEnvironment;
import sugarcube.insight.interfaces.FxPaneLoader;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.pdf.resources.icons.Icon;

public class NewPane extends FxEnvironmentPane
{
  public static FxPaneLoader LOADER = env -> new NewPane(env);

  private @FXML ListView<Paper> paperList;
  private @FXML TextField widthField;
  private @FXML TextField heightField;
  private @FXML RadioButton portraitRadio;
  private @FXML RadioButton landscapeRadio;
  private @FXML ToggleGroup orientationGroup;
  private @FXML AnchorPane previewPane;
  private @FXML ComboBox<Integer> numberCombo;
//  private @FXML Button setBt;
  private @FXML Button createBt;

  public NewPane(FxEnvironment env)
  {
    super(env, "New", "New Document", Icon.Awesome(Icon.FILE_ALT, 36, Color3.LIGHT_GRAY));

    createBt.setOnAction(e -> {

      FxFileChooser chooser = new FxFileChooser();
      File3 file = chooser.save(env.window());
      if (file != null)
      {
        File3 ocd = file.extense(".ocd");
        OCD.createOCDFile(ocd, dim(), nbOfPages());
        env.gui.glassPane.message("OCD Created - " + ocd.path(), 2, true, () -> env.load(ocd));
      } else
        env.gui.glassPane.message("OCD Document Not Created", 2, false, null);
    });
    
//
//    setBt.setOnAction(e -> {
//    });

    for (int i = 1; i <= 300; i++)
      numberCombo.getItems().add(i);
    numberCombo.getSelectionModel().select(0);

    for (int i = 1; i < 7; i++)
      size(Paper.A(i));
    for (int i = 1; i < 7; i++)
      size(Paper.B(i));
    for (int i = 1; i < 7; i++)
      size(Paper.C(i));

    size(Paper.LETTER, Paper.GOV_LETTER, Paper.LEGAL, Paper.TABLOID, Paper.CREDIT_CARD, Paper.BUSINESS_CARD_ISO);

    paperList.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {

      this.portraitRadio.setSelected(val.isPortrait());
      this.landscapeRadio.setSelected(!val.isPortrait());

      refresh();
    });

    this.orientationGroup.selectedToggleProperty().addListener((obs, old, val) -> refresh());

    this.paperList.getSelectionModel().select(3);
  }

  public NewPane size(Paper... papers)
  {
    for (Paper paper : papers)
      paperList.getItems().add(paper);
    return this;
  }

  public Paper paper()
  {
    return paperList.getSelectionModel().getSelectedItem();
  }

  public Dimension3 dim()
  {  
    return paper().portrait(portrait());
  }

  public boolean portrait()
  {
    return portraitRadio.isSelected();
  }

  public int nbOfPages()
  {
    return this.numberCombo.getValue();
  }

  @Override
  public void refresh()
  {
    Metric metric = env.insight.config.metric;
    Dimension3 dim = metric.from(Metric.MM, dim()).round(2);

    widthField.setText(dim.width() + metric.unit);
    heightField.setText(dim.height() + metric.unit);
    this.refreshPreview();
  }

  public void refreshPreview()
  {

    Rectangle3 box = new Rectangle3(0, 0, this.previewPane.getPrefWidth() - 16, this.previewPane.getPrefHeight() - 16);

    Paper a1 = Paper.A(1);
    double scale = Math.max(0.1, Math.min(box.width / a1.width(), box.height / a1.height()));

    this.previewPane.getChildren().clear();
   
    Rectangle3 r = Rectangle3.centered(box, dim().scale(scale));
    this.previewPane.getChildren().add(r.fx().stroke(Color3.ANTHRACITE.alpha(0.5)).fill(Color3.WHITE));

    Font font = new Font(10);
    for (int i = 1; i <= 4; i++)// visual hints giving standard A sizes
    {
      r = Rectangle3.centered(box, Paper.A(i).scale(scale));
      this.previewPane.getChildren().add(r.fx().stroke(Color3.ANTHRACITE.alpha(0.3)).fill(Color3.TRANSPARENT));
      this.previewPane.getChildren().add(new FxText(r.x + 2, r.y + 2 + 10, "A" + i).font(font));
    }

  }

}
