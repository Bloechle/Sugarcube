package sugarcube.insight.side;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Str;
import sugarcube.common.data.collections.StringSet;
import sugarcube.common.system.io.Class3;
import sugarcube.common.system.io.File3;
import sugarcube.insight.core.FxEnvironment;
import sugarcube.insight.core.FxFinalEnvironment;

import java.net.URL;
import java.util.Collection;

public abstract class InsightSide extends FxFinalEnvironment
{  
  protected Pane pane;

  public InsightSide(FxEnvironment env, String name, String fxml)
  {
    super(env, name);
    this.pane = Str.IsVoid(fxml) ? new BorderPane() : fxml(fxml);
    this.pane.setStyle("-fx-margin: 0; -fx-border-width: 0; -fx-border-color:transparent;");
    this.pane.visibleProperty().addListener((obs, old, val) -> visibilityChanged());
  }

  public boolean isVisible()
  {
    return pane != null && pane.visibleProperty().get();
  }

  public void visibilityChanged()
  {

  }

  public Pane fxml(String name)
  {    
    StringSet styles = new StringSet();
    try
    {
      name = File3.Filename(name);
      URL fxml = Class3.Url(this, name + ".fxml");
      URL css = Class3.Url(this, name + ".css");

      if (fxml != null)
      {
        FXMLLoader fxmlLoader = new FXMLLoader(fxml);
        fxmlLoader.setController(this);
        pane = fxmlLoader.load();

        styles.addNotNull(css == null ? null : css.toExternalForm());

        for (String style : styles)
          pane.getStylesheets().add(style);

      }
    } catch (Exception ex)
    {
      Log.debug(this, ".fxml - exception: " + name);
      ex.printStackTrace();
      pane = new BorderPane();
    }
    return pane;
  }

  public void addStyleSheets(Collection<? extends String> css)
  {
    this.pane.getStylesheets().addAll(css);
  }

  public Region pane()
  {
    return pane;
  }

  @Override
  public Node root()
  {
    return pane;
  }

  public void minWidth(int width)
  {
    pane.setMinWidth(width);
  }

  public void prefWidth(int width)
  {
    pane.setPrefWidth(width);
  }

  public void maxWidth(int width)
  {
    pane.setMaxWidth(width);
  }
  
  public BorderPane borderPane()
  {
    return (BorderPane)pane;
  }

  public void setContent(Node content)
  {
    if (pane instanceof BorderPane)
      borderPane().setCenter(content);
    else
      pane.getChildren().setAll(content);
  }

}