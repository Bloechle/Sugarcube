package sugarcube.common.ui.fx.controls;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Transform;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Image3;
import sugarcube.common.ui.fx.base.FxCSS;

public class FxImageView extends ImageView
{
  private String filename = "";

  public FxImageView()
  {
  }
  
  public FxImageView(String url)
  {
    this(new Image(url));
  }
  
  
  public FxImageView(Image image)
  {
    setImage(image);
  }
  
  public FxImageView(Image image, boolean preserveRatio, boolean smooth, boolean cache)
  {
    setImage(image);
    setPreserveRatio(preserveRatio);
    setSmooth(smooth);
    setCache(cache);
  }

  public FxImageView(Image3 image)
  {
    setImage(image.fx());
  }

  public void setFilename(String filename)
  {
    this.filename = filename;
  }

  public String getFilename()
  {
    return filename;
  }
  
  public void setImage(Image3 image)
  {
    setImage(image.fx());
  }
  
  public FxImageView style(String style)
  {
    return (FxImageView) FxCSS.Style(this,  style);
  }
  
  public FxImageView addTransform(Transform3 tm)
  {
    return addTransform(tm.fx());
  }
  
  public FxImageView addTransform(Transform tm)
  {
    this.getTransforms().add(tm);
    return this;
  }

}
