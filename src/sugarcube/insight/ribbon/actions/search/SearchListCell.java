package sugarcube.insight.ribbon.actions.search;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.base.FxText;
import sugarcube.common.ui.fx.containers.FxHBox;
import sugarcube.common.ui.fx.controls.FxLabel;
import sugarcube.common.ui.fx.controls.FxListCell;
import sugarcube.formats.pdf.resources.icons.Icon;

public class SearchListCell extends FxListCell<SearchResult>
{

  // private LignageSide lignage;
  // private FxLabel msg = new FxLabel().style("-fx-text-fill:
  // #000;-fx-margin:0px 8px 0px 8px;");
  private FxHBox box;
  private BorderPane icon = new BorderPane();
  private int iconSize = 18;

  public SearchListCell()
  {
    super();
    // this.lignage = lignage;
    
    this.setAlignment(Pos.CENTER_LEFT);
    this.box = new FxHBox().add(icon, Fx.Space()).align(Pos.CENTER_LEFT);
    this.style("-fx-border-width: 2px 0px 0px 0px; -fx-border-color:transparent;");
  }

  public void focus(boolean doFocus)
  {

  }

  public FxText icon(SearchResult result)
  {
    return icon(Icon.TAG, Color3.ANTHRACITE);
  }

  private FxText icon(Icon icon, Color3 color)
  {
    return Icon.Awesome(icon, iconSize, color);
  }

  @Override
  public void updateItem(SearchResult result, boolean empty)
  {
    super.updateItem(result, empty);
    if (empty)
    {
      this.setText("");
      this.setGraphic(new FxLabel(""));
    } else if (result != null)
    {
      this.setText(result.text);
      this.icon.setCenter(icon(result));
//      setGraphic(box);
      setGraphic(result);
      result.cell = this;
    }
  }
}
