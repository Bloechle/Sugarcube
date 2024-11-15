package sugarcube.formats.ocd.objects;

import sugarcube.common.graphics.Image3;

public class ImageThumberCache
{
  public Image3 in;
  public Image3 out;
  public int[][] src;
  public int[][] res;

  public Image3 needIn(int w, int h, int type, boolean clear)
  {
    if (in == null || in.width() != w || in.height() != h || in.getType() != type)
      in = new Image3(w, h, type);
    else if (clear)
      in.clear();
    return in;
  }

  public Image3 needOut(int w, int h, int type)
  {
    if (out == null || out.width() != w || out.height() != h || out.getType() != type)
      out = new Image3(w, h, type);
    return out;
  }

  public int[][] needSrc(int w, int h)
  {
    if (src == null || src.length != h || src[0].length != w)
      src = new int[h][w];
    return src;
  }

  public int[][] needRes(int w, int h)
  {
    if (res == null || res.length != h || res[0].length != w)
      res = new int[h][w];
    else
      for (int y = 0; y < res.length; y++)
        for (int x = 0; x < res[0].length; x++)
          res[y][x] = 0;
    return res;
  }

}
