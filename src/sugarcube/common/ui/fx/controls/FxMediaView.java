package sugarcube.common.ui.fx.controls;

import javafx.scene.media.MediaView;
import javafx.scene.transform.Transform;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.ui.fx.base.FxCSS;
import sugarcube.common.ui.fx.media.FxMedia;

public class FxMediaView extends MediaView
{
  public FxMedia media;
  
  public FxMediaView()
  {
  }
  
  public FxMediaView(String file)
  {
    this(new FxMedia(file, true));
  }
  
  
  public FxMediaView(FxMedia media)
  {
    this.media = media;
    this.setMediaPlayer(media.player);    
  }
  
  public FxMediaView(FxMedia media, boolean preserveRatio, boolean smooth, boolean cache)
  {
     this(media);
    this.setPreserveRatio(preserveRatio);
    this.setSmooth(smooth);
    this.setCache(cache);
  }
  
 public FxMediaView setBounds(Rectangle3 box)
 {
   this.setX(box.x);
   this.setY(box.y);
   this.setFitWidth(box.width);
   this.setFitHeight(box.height);
   return this;
 }
  
  public FxMediaView style(String style)
  {
    return (FxMediaView) FxCSS.Style(this,  style);
  }
  
  public FxMediaView addTransform(Transform3 tm)
  {
    return addTransform(tm.fx());
  }
  
  public FxMediaView addTransform(Transform tm)
  {
    this.getTransforms().add(tm);
    return this;
  }

}
