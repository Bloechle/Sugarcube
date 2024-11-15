package sugarcube.insight.ribbon.actions.annot;

import javafx.geometry.Pos;
import sugarcube.common.ui.fx.controls.FxListCell;

public class AnnotListCell extends FxListCell<AnnotItem>
{
  private AnnotDialog dialog;


  public AnnotListCell(AnnotDialog dialog)
  {
    super();
    this.dialog = dialog;
    this.setAlignment(Pos.CENTER_LEFT);
  }

  @Override
  public AnnotItem item()
  {
    return this.getItem();
  }

  public void focus(boolean doFocus)
  {

  }

  @Override
  public void updateItem(AnnotItem item, boolean empty)
  {
    super.updateItem(item, empty);
    if (empty)
    {
      this.setText("");
//      this.setGraphic(new FxLabel("").height(30));
    } else if (item != null)
    {
      this.setText(item.toString());
//      setGraphic(box.height(30));
    }
  }

}