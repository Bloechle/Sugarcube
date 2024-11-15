package sugarcube.insight.side.toc;

import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.controls.FxTextField;
import sugarcube.insight.core.IS;
import sugarcube.formats.ocd.objects.nav.OCDNavItem;
import sugarcube.formats.pdf.resources.icons.Icon;

public class FxNavCell extends TreeCell<OCDNavItem>
{
  public static Color3 WHITE = IS.WHITE_DUST;
  private FxTextField field;

  public FxNavCell(FxNavTree tree)
  {
    this.setStyle(IS.DARK_BG_STYLE + IS.DUST_TEXT_STYLE);

    this.setOnMouseEntered(e -> onMouseOver(true));
    this.setOnMouseExited(e -> onMouseOver(false));

    this.setOnDragDetected(e -> {
      Dragboard db = startDragAndDrop(TransferMode.MOVE);
      // db.setDragView(this.getImage());
      ClipboardContent content = new ClipboardContent();
      content.putString(this.getItem().link);

      db.setContent(content);
      e.consume();
    });

    this.setOnDragDone(e -> {
      if (e.getTransferMode() == TransferMode.MOVE)
      {
        // nope
      }
      e.consume();
    });

    this.setOnDragOver(e -> {
      if (e.getGestureSource() != this)
        e.acceptTransferModes(TransferMode.MOVE);
      e.consume();
    });

    this.setOnDragEntered(e -> {
      // if (e.getGestureSource() != this && e.getDragboard().hasString())
      // line.stroke(Color3.SC_BLUE);
      e.consume();
    });

    this.setOnDragExited(e -> {

      e.consume();
    });

    this.setOnDragDropped(e -> {
      Dragboard db = e.getDragboard();
      if (db.hasString())
      {
        e.setDropCompleted(true);
        tree.pane.group(item());

        // side.move(db.getString(), filename);
      } else
        e.setDropCompleted(false);
      e.consume();
    });
  }

  public void onMouseOver(boolean over)
  {
    this.setOpacity(over ? 0.8 : 1);
  }

  @Override
  public void startEdit()
  {
    super.startEdit();
    if (field == null)
      newField();
    setText(null);
    setGraphic(field);
    field.selectAll();
  }

  @Override
  public void cancelEdit()
  {
    super.cancelEdit();
    this.refresh();
  }

  public OCDNavItem item()
  {
    return this.getItem();
  }

  private String string()
  {
    OCDNavItem item = item();
    
    if(item==null)
      return "";
    else if(item.isTOCRoot())
      return "Table of Contents";
    else if (item.isBookmarksRoot())
      return "Bookmarks";
    else if(item.isPageListRoot())
      return "Page List";
    else if(item.isNavRoot())
      return "Navigation";
    else 
      return item.text;
  }

  private void newField()
  {
    field = new FxTextField(string());
    field.setStyle("-fx-text-fill:rgb(255,255,255);");
    field.setOnKeyReleased(k -> {
      if (k.getCode() == KeyCode.ENTER)
      {
        OCDNavItem item = item();
        item.text = field.getText();
        commitEdit(item);
      } else if (k.getCode() == KeyCode.ESCAPE)
      {
        cancelEdit();
      }
    });
  }

  @Override
  protected void updateItem(OCDNavItem item, boolean empty)
  {
    super.updateItem(item, empty);
    if (empty)
    {
      this.setText(null);
      this.setGraphic(null);
    } else if (item != null)
    {
      if (isEditing())
      {
        if (field != null)
          field.setText(string());
        this.setText(null);
        this.setGraphic(field);
      } else
        refresh();
    }
  }

  public void refresh()
  {
    OCDNavItem item = this.item();
    if (item == null)
      return;
    this.setText("  " + string());

    Node icon = null;

    if (item.isNavRoot())
      icon = Icon.Awesome(Icon.COMPASS, 18, WHITE);
    else if (item.isBookmarksRoot())
      icon = Icon.Awesome(Icon.BOOKMARK, 18, WHITE);
    else if (item.isTOCRoot())
      icon = Icon.Awesome(Icon.BOOK, 18, WHITE);
    else if (item.isPageListRoot())
      icon = Icon.Awesome(Icon.LIST, 18, WHITE);
    else if (item.isLeaf())
      icon = Icon.Awesome(Icon.CIRCLE, 8, WHITE);

    this.setGraphic(icon);
  }

}
