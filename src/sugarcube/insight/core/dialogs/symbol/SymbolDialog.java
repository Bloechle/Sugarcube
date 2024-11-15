package sugarcube.insight.core.dialogs.symbol;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import sugarcube.common.ui.fx.dialogs.FxWindow;
import sugarcube.formats.pdf.resources.fontmapping.CharMap;


public class SymbolDialog extends FxWindow implements SymbolPane.Listener
{
  private @FXML ListView<String> fontList;
  private @FXML FlowPane glyphsPane;
  private @FXML Button acceptBt, cancelBt;  
  private CharMap[] charMaps = CharMap.loadAll();
  public Font font = new Font(40);
  public CharMap.Listener listener;
  public SymbolPane symbol = null;

  public SymbolDialog(Stage owner, CharMap.Listener listener)
  {
    super("Insight - Symbol Chooser Dialog", true, owner);
    this.listener = listener;
    fontList.getSelectionModel().selectedIndexProperty().addListener((obs, old, val) -> refresh(val));
    fontList.getItems().setAll(CharMap.NAMES);
    fontList.getSelectionModel().select(0);

    cancelBt.setOnAction(e -> doAccept(false));
    acceptBt.setOnAction(e -> doAccept(true));
    
    this.minSize(600,400);
    this.show();
  }
  
  
  public void doAccept(boolean accept)
  {
    if (accept && symbol != null)
      listener.selectCodes(symbol.codes);
    this.close();
  }

  @Override
  public void refresh()
  {

  }

  public void refresh(Number index)
  {
    int i = index == null ? 0 : index.intValue();

    symbol = null;
    glyphsPane.getChildren().clear();
    CharMap map = charMaps[i = i < 0 || i > charMaps.length - 1 ? 0 : i];
    for (CharMap.Codes codes : map.codes())
    {
      SymbolPane pane = new SymbolPane(font, codes, this);
      glyphsPane.getChildren().add(pane);
      if (symbol == null)
        symbol = pane;
    }
    this.selectSymbol(symbol);
  }

  @Override
  public void selectSymbol(SymbolPane Symbol)
  {
    if (symbol != null)
      symbol.highlight(false);
    this.symbol = Symbol;
    if (symbol != null)
    {
      symbol.highlight(true);

    } else
    {

    }

  }

}
