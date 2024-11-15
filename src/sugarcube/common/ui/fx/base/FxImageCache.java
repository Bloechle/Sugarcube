package sugarcube.common.ui.fx.base;

import javafx.scene.image.Image;
import sugarcube.common.data.collections.Cache;
import sugarcube.common.system.io.File3;

import java.util.Iterator;

public class FxImageCache implements Iterable<Image>
{
  private Cache<String, Image> images = new Cache<>();
  private long maxBytes;

  public FxImageCache(int maxNbOfImages, long maxNbOfMB)
  {
    images.setMaxSize(maxNbOfImages);
    this.maxBytes = maxNbOfMB > 0 ? maxNbOfMB * 1000000 : 0;
  }

  public long bytesAllocated()
  {
    long bytes = 0;
    for (Image image : this)
    {
      bytes += (image.getWidth() * image.getHeight() * 4);
    }
    return bytes;
  }

  public Image image(String key)
  {
    if (images.has(key))
      return images.get(key);
    if (images.has(key = File3.Filename(key, true)))
      return images.get(key);
    if (images.has(key + ".png"))
      return images.get(key + ".png");
    return images.get(key + ".jpg");
  }

  public void put(String key, Object value)
  {
    if (value instanceof Image)
    {
      while (maxBytes > 0 && bytesAllocated() > maxBytes)
        images.removeFirst();
      images.put(key, (Image) value);
    }
  }

  public int size()
  {
    return images.size();
  }

  public void clear()
  {
    images.clear();
  }

  @Override
  public Iterator<Image> iterator()
  {
    return images.values().iterator();
  }
}
