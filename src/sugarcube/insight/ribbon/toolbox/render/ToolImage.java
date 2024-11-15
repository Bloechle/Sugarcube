package sugarcube.insight.ribbon.toolbox.render;

import javafx.scene.image.Image;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.base.FxTransform;
import sugarcube.common.ui.fx.controls.FxImageView;
import sugarcube.common.ui.fx.controls.FxMediaView;
import sugarcube.common.ui.fx.shapes.FxRect;
import sugarcube.formats.ocd.objects.OCDImage;

public class ToolImage extends ToolNode<OCDImage>
{
  public FxImageView view;
  public FxMediaView media;
  public FxRect frame;
  public FxTransform transform = new FxTransform();

  public ToolImage(final ToolPager pager, final OCDImage ocdImage, String... styles)
  {
    super(pager, ocdImage, styles);
    this.init();
  }

  protected void init()
  {
    Image image = node.fxImage();
    if (image != null)
    {
      this.add(view = new FxImageView(image));
      double op = pager.tab.opacitySlider.getValue();
      if (op > 0)
        view.setOpacity((100 - op) / 100.0);
      this.transform.update(node.transform());
      view.getTransforms().add(transform);
    } else
    {
      this.add(this.frame = node.bounds().fx().paint(Color3.GLASS, Color3.ORANGE, 2).mouseTransparent());
    }
  }

  @Override
  public ToolImage refresh()
  {
    this.setOpacity(node.zOrder < 0 ? 0.1 : 1);
    this.clip(node.fxClip());
    transform.update(node.transform());
    boxing();
    return this;
  }

  public int imageWidth()
  {
    return node == null ? 0 : node.width();
  }

  public int imageHeight()
  {
    return node == null ? 0 : node.height();
  }

}
