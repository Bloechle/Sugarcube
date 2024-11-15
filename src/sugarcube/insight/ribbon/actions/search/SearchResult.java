package sugarcube.insight.ribbon.actions.search;

import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Image3;
import sugarcube.common.ui.fx.containers.FxAnchorPane;
import sugarcube.common.ui.fx.controls.FxImageView;
import sugarcube.formats.ocd.objects.OCDPage;

public class SearchResult extends FxAnchorPane
{  
  
  public SearchListCell cell;
  public String nodeID;
  public String text = "";
  public int pageNb;
  public Rectangle3 box;
  private FxImageView view;
  public double imageWidth;


  public SearchResult(OCDPage page, Image3 image, Rectangle3 box)
  {    
    this.style("search-result");
    
    this.pageNb = page.number();
    this.box = box;
           
    this.view = new FxImageView(image.fx(), true, true, true);    
    
    this.imageWidth = image.width();
    this.width(imageWidth);  
    this.add(view);

    this.setMouseOverEffect(new DropShadow(2, Color.GRAY));

//    this.setOnDragDetected(e -> {
//      Dragboard db = startDragAndDrop(TransferMode.MOVE);
//      db.setDragView(view.getImage());
//      ClipboardContent content = new ClipboardContent();
//      content.putString(filename);
//      db.setContent(content);
//      e.consume();
//    });
//
//    this.setOnDragDone(e -> {
//      if (e.getTransferMode() == TransferMode.MOVE)
//      {
//        // nope
//      }
//      e.consume();
//    });

  }
  
  
  
  @Override
  public void width(double width)  
  {    
    super.width(width);
    view.setFitWidth(width);
  }
}
