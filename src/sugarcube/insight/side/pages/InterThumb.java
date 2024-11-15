package sugarcube.insight.side.pages;

import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import sugarcube.common.graphics.Color3;
import sugarcube.common.interfaces.Widthable;
import sugarcube.common.ui.fx.base.FxBoxed;
import sugarcube.common.ui.fx.shapes.FxLine;

public class InterThumb extends FxBoxed implements Widthable
{
  private FxLine line = new FxLine();

  public InterThumb(ThumbSide side, String filename, int width)
  {
    this.width(width);;
    
    this.add(box, line);

    this.setOnDragOver(e -> {
      if (e.getGestureSource() != this)
        e.acceptTransferModes(TransferMode.MOVE);
      e.consume();
    });

    this.setOnDragEntered(e -> {
      if (e.getGestureSource() != this && e.getDragboard().hasString())
        line.stroke(Color3.SC_BLUE);
      e.consume();
    });

    this.setOnDragExited(e -> {
      reset();
      e.consume();
    });

    this.setOnDragDropped(e -> {
      Dragboard db = e.getDragboard();
      if (db.hasString())
      {
        e.setDropCompleted(true);
        side.move(db.getString(), filename);
      } else
        e.setDropCompleted(false);
      e.consume();
    });

    this.reset();
  }
  
  @Override
  public void width(double width)
  {    
    double height = 5;
    box.set(0, 0, width, height);
    line.set(0, 0, width, 0);
  }


  public void reset()
  {
    line.fill(Color3.TRANSPARENT);
    line.stroke(Color3.TRANSPARENT);
  }


}
