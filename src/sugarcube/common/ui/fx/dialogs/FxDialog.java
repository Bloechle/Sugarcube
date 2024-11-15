package sugarcube.common.ui.fx.dialogs;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

import java.util.Optional;

public class FxDialog
{
  public Dialog dialog;

  public FxDialog(Dialog dialog)
  {
    this.dialog = dialog;          
  }
  
  public FxDialog modal(boolean modal)
  {
    this.dialog.initModality(modal ? Modality.APPLICATION_MODAL : Modality.NONE);
    return this;
  }

  public FxDialog title(String title)
  {
    dialog.setTitle(title);
    return this;
  }

  public FxDialog header(String header)
  {
    dialog.setHeaderText(header);
    return this;
  }

  public FxDialog text(String text)
  {
    dialog.setContentText(text);
    return this;
  }

  public static FxDialog Wrap(Dialog dialog)
  {
    return new FxDialog(dialog);
  }

  public static boolean Confirm(String title, String header, String text)
  {
    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.initStyle(StageStyle.UTILITY);
    FxDialog.Wrap(alert).title(title).header(header).text(text);
    return alert.showAndWait().get() == ButtonType.OK;
  }

  public static String Input(String title, String header, String text, String def)
  {
    TextInputDialog input = new TextInputDialog(def);    
    FxDialog fx=FxDialog.Wrap(input).modal(false).title(title).header(header).text(text);
    fx.dialog.getDialogPane().setMinWidth(400);
    Optional<String> result = input.showAndWait();
    return result.isPresent() ? result.get() : null;
  }
}
