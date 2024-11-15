package sugarcube.insight.ribbon.reader.render;

import javafx.scene.image.Image;
import javafx.scene.media.MediaPlayer;
import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.system.io.File3;
import sugarcube.common.ui.fx.base.FxTransform;
import sugarcube.common.ui.fx.controls.FxImageView;
import sugarcube.common.ui.fx.controls.FxMediaView;
import sugarcube.common.ui.fx.shapes.FxRect;
import sugarcube.formats.ocd.objects.OCDImage;

public class ReaderImage extends ReaderNode<OCDImage>
{
  public FxImageView imageView;
  public FxMediaView mediaView;
  public FxRect frame;
  public FxTransform transform = new FxTransform();

  public ReaderImage(ReaderPager pager, final OCDImage ocdImage)
  {
    super(pager, ocdImage);
    this.init();
  }

  protected void init()
  {
    boolean isVideo = node.isMP4();
    Rectangle3 bounds = node.bounds();
    if (isVideo)
    {

      try
      {
        for(MediaPlayer player: env().mediaPlayers.values())
        {
          player.stop();
        }
        File3 file = File3.RandomTempFile(".mp4");
        node.write(file);
        Log.debug(this, ".init - video: " + file + ", exists=" + file.exists() + ", size=" + file.length());
        this.add(mediaView = new FxMediaView(file.path()));
        mediaView.setBounds(bounds);
        mediaView.media.play();
                       
        this.env().mediaPlayers.put(file.path(), mediaView.media.player);
      } catch (Exception e)
      {
        e.printStackTrace();
        isVideo = false;
      }
    } else
    {
      Image image = node.fxImage();
      if (image != null)
      {
        this.add(imageView = new FxImageView(image));
        this.transform.update(node.transform());
        imageView.getTransforms().add(transform);
      } else
      {
        this.add(this.frame = bounds.fx().paint(Color3.GLASS, Color3.ORANGE, 2).mouseTransparent());
      }
    }
  }

  @Override
  public ReaderImage refresh()
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

}
