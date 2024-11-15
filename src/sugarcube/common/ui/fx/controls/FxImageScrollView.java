package sugarcube.common.ui.fx.controls;

import javafx.scene.image.Image;
import sugarcube.common.graphics.Image3;
import sugarcube.common.ui.fx.containers.FxBorderPane;
import sugarcube.common.ui.fx.containers.FxScrollPane;

public class FxImageScrollView extends FxScrollPane
{  
  public FxBorderPane pane = new FxBorderPane();
  public FxImageView canvas = new FxImageView();

  public FxImageScrollView()
  {
    this.setId("board-scroll");
    this.pane.setId("board-pane");
    this.canvas.setId("board-canvas");
    
    this.pane.setCenter(canvas);
    this.setContent(pane);
    this.setFitToWidth(true);
    this.setFitToHeight(true);
    
    this.consumeSpace();
  }

  public void setImage(Image3 image)
  {
    this.canvas.setImage(image);
  }
  
  public void setImage(Image image)
  {
    this.canvas.setImage(image);
  }
}
