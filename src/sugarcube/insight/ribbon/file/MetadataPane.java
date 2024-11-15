package sugarcube.insight.ribbon.file;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import sugarcube.common.data.collections.StringList;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Image3;
import sugarcube.common.system.io.File3;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.controls.FxImageScrollView;
import sugarcube.common.ui.fx.dialogs.FxFileChooser;
import sugarcube.common.data.xml.Nb;
import sugarcube.common.data.xml.Xml;
import sugarcube.insight.core.FxEnvironment;
import sugarcube.insight.interfaces.FxPaneLoader;
import sugarcube.formats.ocd.objects.OCDPage;
import sugarcube.formats.ocd.objects.document.OCDMetadata;
import sugarcube.formats.ocd.objects.metadata.dc.DC;
import sugarcube.formats.pdf.resources.icons.Icon;

import java.io.File;

public class MetadataPane extends FxEnvironmentPane
{
  public static FxPaneLoader LOADER = env -> new MetadataPane(env);

  private @FXML BorderPane rootPane;
  private @FXML TextField titel;
  private @FXML TextField author;
  private @FXML TextField subject;
  private @FXML TextArea description;
  private @FXML TextField publisher;
  private @FXML TextField identifier;
  private @FXML TextField date;
  private @FXML ComboBox<String> language;
  private @FXML TextField rights;  
  private @FXML Button imageBt;
  private @FXML ComboBox<String> pageCombo;
  private @FXML Button importBt;
  private @FXML Button exportBt;
  private Image3 image;
  private FxImageScrollView imageView;

  public MetadataPane(FxEnvironment env)
  {
    super(env, "Metadata", "Metadata Editor", Icon.Awesome(Icon.INFO_CIRCLE, 36, Color3.BLUE_BRIGHT));

    Fx.MaximizeWidth(exportBt);

    rootPane.setCenter(imageView = new FxImageScrollView());
    
    this.refreshPane();

    imageBt.setOnAction(e -> {
      FileChooser chooser = new FileChooser();
      chooser.setTitle("Select Image File");

      this.setImage(chooser.showOpenDialog(env.window()));
    });

    Fx.Listen((obs, old, val) -> updateOCD(), titel, author, subject, description, publisher, identifier, date, rights);
    Fx.Listen((obs, old, val) -> updateOCD(), language);

    exportBt.setOnAction(e -> {
      FxFileChooser chooser = new FxFileChooser("Export XML Metadata", env.ocd.file().extense(".meta").path());
      File3 file = chooser.save(env.window());
      if (file != null)
        Xml.write(env.ocd.metadata(), file);
    });

    importBt.setOnAction(e -> {

      FxFileChooser chooser = new FxFileChooser("Import XML Metadata", env.ocd.file().extense(".meta").path());
      this.files(chooser.open(env.window()));
    });

    Fx.Init(language, null, "ar - arabic", "en - english", "es - spanish", "fr - french", "ge - german", "it - italian", "ja - japanese",
        "ru - russian", "zh - chinese");

    StringList pages = new StringList();
    for (OCDPage page : env.ocd.pageHandler)
      pages.add("Page " + page.number());

    Fx.Init((obs, old, val) -> {
      int nb = Nb.Int(true, val, -1);
      if (nb > 0)
        this.setImage(env.ocd.pageHandler.getPage(nb).createImage(), true);
    }, pageCombo, "Page 1", pages.array());

  }

  public void files(File... files)
  {
    for (File file : files)
    {
      if (file != null)
        switch (File3.extension(file, true).toLowerCase())
        {
        case "png":
        case "jpg":
        case "jpeg":
          this.setImage(file);
          break;
        case "xml":
        case "meta":
          env.ocd.metadata().load(file);
          this.refreshPane();
          break;

        }
    }
  }

  @Override
  public void onDragDropped(DragEvent event)
  {
    files(event.getDragboard().getFiles().toArray(new File[0]));
  }

  public void setImage(File file)
  {
    if (file != null)
      imageView.setImage((image = Image3.Read(file)).fx());
  }

  public void setImage(Image3 image, boolean updateOCD)
  {
    if (image == null)
      image = env.ocd.pageHandler.firstPage().createImage();

    if ((this.image = image) != null)
    {
      imageView.setImage(image.fx());
      if (updateOCD)
        env.ocd.setCover(image);
    }
  }

  public void refreshPane()
  {
    if(env.ocd==null)
      return;
    OCDMetadata meta = env.ocd.metadata();
    this.titel.setText(meta.dc(DC.title, ""));
    this.author.setText(meta.dc(DC.creator, ""));
    this.subject.setText(meta.dc(DC.subject, ""));
    this.description.setText(meta.dc(DC.description, ""));
    this.publisher.setText(meta.dc(DC.publisher, ""));
    this.identifier.setText(meta.dc(DC.identifier, ""));
    this.date.setText(meta.dc(DC.date, ""));
    this.language.getSelectionModel().select(meta.dc(DC.language, ""));
    this.rights.setText(meta.dc(DC.rights, ""));
    this.setImage(env.ocd.cover(), false);
  }

  public void updateOCD()
  {
    OCDMetadata meta = env.ocd.metadata();
    meta.set(DC.title, this.titel.getText());
    meta.set(DC.creator, this.author.getText());
    meta.set(DC.subject, this.subject.getText());
    meta.set(DC.description, this.description.getText());
    meta.set(DC.publisher, this.publisher.getText());
    meta.set(DC.identifier, this.identifier.getText());
    meta.set(DC.date, this.date.getText());
    meta.set(DC.language, this.language.getValue());
    meta.set(DC.rights, this.rights.getText());
  }

  @Override
  public void refresh()
  {

  }

}
