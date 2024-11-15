package sugarcube.common.ui.fx.media;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import sugarcube.common.system.io.File3;

import java.net.URISyntaxException;
import java.net.URL;

public class FxMedia
{
  public Media media;
  public MediaPlayer player;

  protected FxMedia()
  {

  }

  public FxMedia(String path, boolean isFile)
  {
    if (isFile)
      file(path);
    else
      uri(path);
  }

  public FxMedia file(String path)
  {
    return uri(File3.Get(path).toURI().toString());
  }

  public FxMedia uri(String path)
  {
    try
    {
      this.media = new Media(path);
      this.player = new MediaPlayer(media);
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return this;
  }

  public FxMedia play()
  {
    if (player != null)
      player.play();
    return this;
  }

  public FxMedia dispose()
  {
    if (player != null)    
      try
      {
        player.stop();
        player.dispose();
      } catch (Exception e)
      {
        e.printStackTrace();
      }  
    return this;
  }

  public FxMedia stop()
  {
    if (player != null)
      try
      {
        player.stop();
      } catch (Exception e)
      {
        e.printStackTrace();
      }
    return this;
  }

  public FxMedia pause()
  {
    if (player != null)
      player.pause();
    return this;
  }

  public FxMedia seekStart()
  {
    if (player != null)
      player.seek(Duration.ZERO);
    return this;
  }

  public FxMedia loop()
  {
    if (player != null)
      player.setCycleCount(MediaPlayer.INDEFINITE);
    return this;
  }

  public static FxMedia FromFile(String path)
  {
    return new FxMedia(path, true);
  }

  public static FxMedia FromURI(String path)
  {
    return new FxMedia(path, false);
  }

  public static FxMedia FromURL(URL url)
  {
    try
    {
      return new FxMedia(url.toURI().toString(), false);
    } catch (URISyntaxException e)
    {
      e.printStackTrace();
      return null;
    }
  }

  public static void main(String... args)
  {

  }

}
