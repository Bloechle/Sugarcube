package sugarcube.common.ui.fx.virtual;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import sugarcube.common.ui.fx.base.Fx;

import java.awt.image.BufferedImage;

public class FxPhong extends PhongMaterial
{
  public FxPhong()
  {
  }
  
  public FxPhong(Color diff)
  {
    super(diff);
  }
  

  public FxPhong(Color diffuseColor, Image diffuseMap, Image specularMap, Image bumpMap, Image selfIlluminationMap)
  {
    super(diffuseColor, diffuseMap, specularMap, bumpMap, selfIlluminationMap);
  }

  public FxPhong diffSpec(Color diff, Color spec)
  {
    if (diff != null)
      this.setDiffuseColor(diff);
    if (spec != null)
      this.setSpecularColor(spec);
    return this;
  }
  
  public FxPhong setDiffuseMap(BufferedImage image)
  {
    super.setDiffuseMap(Fx.toFXImage(image));
    return this;
  }


  public static FxPhong New()
  {
    return new FxPhong();
  }
  
  public static FxPhong Get(Color diff)
  {
    return new FxPhong(diff);
  }

  public static FxPhong Get(Color diff, Color spec)
  {
    return New().diffSpec(diff, spec);
  }

  public static FxPhong Get(Color diff, Image image)
  {
    return new FxPhong(diff, image, null, null, null);
  }
}
