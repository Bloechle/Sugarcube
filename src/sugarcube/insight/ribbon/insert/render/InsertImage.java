package sugarcube.insight.ribbon.insert.render;

import javafx.scene.image.Image;
import sugarcube.common.graphics.geom.Line3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.base.FxTransform;
import sugarcube.common.ui.fx.controls.FxImageView;
import sugarcube.common.ui.fx.controls.FxMediaView;
import sugarcube.common.ui.fx.shapes.FxRect;
import sugarcube.insight.render.interaction.FxInteractor;
import sugarcube.formats.ocd.objects.OCDImage;

public class InsertImage extends InsertNode<OCDImage>
{
  public FxImageView imageView;
  public FxMediaView mediaView;
  public FxRect frame;
  public FxTransform transform = new FxTransform();

  public InsertImage(InsertPager pager, final OCDImage ocdImage)
  {
    super(pager, ocdImage);
    this.isResizable = true;
    this.isMovable = true;
    this.focusOnMouseOver();
    this.init();
  }

  protected void init()
  {
    Image image = node.fxImage();
    if (image != null)
    {
      this.add(imageView = new FxImageView(image));
      this.transform.update(node.transform());
      imageView.getTransforms().add(transform);
    } else
    {
      this.add(this.frame = node.bounds().fx().paint(Color3.GLASS, Color3.ORANGE, 2).mouseTransparent());
    }
  }

  @Override
  public InsertImage refresh()
  {
    this.setOpacity(node.zOrder < 0 ? 0.1 : 1);
    this.clip(node.fxClip());
    transform.update(node.transform());
    if (mediaView != null)
      mediaView.setBounds(node.bounds());
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

  public boolean isInteractable()
  {
    return !node.isBackground();
  }

  @Override
  public void interacted(FxInteractor interactor)
  {
    Line3 extent = interactor.extent();
    if (node.isView())
    {
      node.setDimension((int) extent.deltaX(), (int) extent.deltaY());
      node.setTransform(1, 0, 0, 1, extent.minX(), extent.minY());
      if (frame != null)
        frame.set(extent.bounds());
    } else
    {
      float dx = extent.deltaX();
      float dy = extent.deltaY();

      if (Math.abs(dx) < 0.001 || Math.abs(dy) < 0.001)
        return;

      Line3 startExtent = interactor.startExtent();

      float sx = dx / startExtent.dx();
      float sy = dy / startExtent.dy();

      if (interactor.hasShift())
        sx = sy = Math.min(sx, sy);

      Transform3 tm = Transform3.scaleInstance(sx, sy).concat(interactor.startTM());
      node.setTransform(tm);
      Rectangle3 box = node.bounds();

      node.setTransform(tm.sx(), tm.hy(), tm.hx(), tm.sy(), tm.x() + extent.x() - box.x, tm.y() + extent.y() - box.y);
      node.modify();

      if (interactor.hasShift())
        interactor.updateSilently(node.bounds().extent());

      this.refresh();
    }
  }

}
