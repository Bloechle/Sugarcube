package sugarcube.common.graphics.vectorize;

import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.Image3;
import sugarcube.common.system.io.File3;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

// https://developer.mozilla.org/en-US/docs/Web/API/ImageData
public class ImageDataRGBA
{
  public int width, height;
  public byte[] data; // raw byte data: R G B A R G B A ...

  public ImageDataRGBA(int width, int height, byte[] data)
  {
    this.width = width;
    this.height = height;
    this.data = data;
  }

  public ImageDataRGBA(BufferedImage image)
  {
    this.width = image.getWidth();
    this.height = image.getHeight();
    int[] rawdata = image.getRGB(0, 0, width, height, null, 0, width);
    this.data = new byte[rawdata.length * 4];
    for (int i = 0; i < rawdata.length; i++)
    {
      data[(i * 4) + 3] = ByteTrans((byte) (rawdata[i] >>> 24));
      data[i * 4] = ByteTrans((byte) (rawdata[i] >>> 16));
      data[(i * 4) + 1] = ByteTrans((byte) (rawdata[i] >>> 8));
      data[(i * 4) + 2] = ByteTrans((byte) (rawdata[i]));
    }
  }

  public ImageDataRGBA(Image3 image)
  {
    this.width = image.width();
    this.height = image.height();

    if (image.getColorModel().hasAlpha())
    {
      Log.warn(this, ".ImageDataRGB - uncomment");
//      float[] raster = image.raster();
//      this.data = new byte[raster.length];
//
//      for (int i = 0; i < raster.length; i += 4)
//        for (int c = 0; c < 4; c++)
//          data[i + c] = FloatAsByte(raster[i + c]);
    } else
      Log.debug(this, " - color space not implemented: " + image.getColorModel());
  }

  private byte FloatAsByte(float f)
  {
    int v = Math.round(f * 255f);
    return ByteTrans((byte) (v > 255 ? 255 : (v < 0 ? 0 : v)));
  }

  public VectorImage vectorize(DTOptions options, Palette palette)
  {
    return quantizeColors(palette, options).vectorize(options);
  }

  public Palette palette(int nbOfColors)
  {
    int idx = 0;
    byte[][] colors = new byte[nbOfColors][4];
    for (int i = 0; i < nbOfColors; i++)
    {
      idx = (int) (Math.floor((Math.random() * data.length) / 4) * 4);
      colors[i][0] = data[idx];
      colors[i][1] = data[idx + 1];
      colors[i][2] = data[idx + 2];
      colors[i][3] = data[idx + 3];
    }
    return new Palette(colors);
  }

  public static ImageDataRGBA Load(String imagePath)
  {
    return Load(File3.Get(imagePath));
  }

  // Loading a file to ImageData, ARGB byte order
  public static ImageDataRGBA Load(File3 imageFile)
  {
    try
    {
      BufferedImage image = ImageIO.read(imageFile);
      return new ImageDataRGBA(image);
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }

  // The bitshift method in loadImageData creates signed bytes where -1 -> 255
  // unsigned ; -128 -> 128 unsigned ;
  // 127 -> 127 unsigned ; 0 -> 0 unsigned ; These will be converted to -128
  // (representing 0 unsigned) ...
  // 127 (representing 255 unsigned) and tosvgcolorstr will add +128 to create
  // RGB values 0..255
  public static byte ByteTrans(byte b)
  {
    return b < 0 ? (byte) (b + 128) : (byte) (b - 128);
  }

  // 1. Color quantization repeated "cycles" times, based on K-means clustering
  // https://en.wikipedia.org/wiki/Color_quantization
  // https://en.wikipedia.org/wiki/K-means_clustering
  public IndexedImage quantizeColors(Palette palette, DTOptions options)
  {
    int nbOfColors = (int) Math.floor(options.get(DTOptions.NB_OF_COLORS));
    float minratio = options.get(DTOptions.MINCOLORRATION);
    int cycles = (int) Math.floor(options.get(DTOptions.COLORQUANTCYCLES));
    // Creating indexed color array arr which has a boundary filled with -1 in
    // every direction
    int[][] indexed = new int[height + 2][width + 2];
    for (int j = 0; j < (height + 2); j++)
    {
      indexed[j][0] = -1;
      indexed[j][width + 1] = -1;
    }
    for (int i = 0; i < (width + 2); i++)
    {
      indexed[0][i] = -1;
      indexed[height + 1][i] = -1;
    }

    int idx = 0;
    int dist;
    int minDist;
    int minColor;
    int[] rgba = new int[4];

    // Use custom palette if pal is defined or sugarcube.app.sample or generate custom length
    // palette
    if (palette == null)
      palette = options.get(DTOptions.COLORSAMPLING) != 0 ? palette(nbOfColors) : new Palette(nbOfColors);

    ImageDataRGBA image = this;
    long[][] acc = new long[palette.size()][5];

    // Repeat clustering step "cycles" times
    for (int cnt = 0; cnt < cycles; cnt++)
    {

      // Average colors from the second iteration
      if (cnt > 0)
      {
        // averaging paletteacc for palette
        float ratio;
        for (int k = 0; k < palette.size(); k++)
        {
          // averaging
          if (acc[k][3] > 0)
            for (int c = 0; c < 4; c++)
              palette.colors[k][c] = (byte) (-128 + (acc[k][c] / acc[k][4]));

          ratio = (float) ((double) (acc[k][4]) / (double) (image.width * image.height));

          // Randomizing a color, if there are too few pixels and there will be
          // a new cycle
          if ((ratio < minratio) && (cnt < (cycles - 1)))
            for (int c = 0; c < 4; c++)
              palette.colors[k][c] = (byte) (-128 + Math.floor(Math.random() * 255));

        }
      }

      // Reseting palette accumulator for averaging
      for (int i = 0; i < palette.size(); i++)
        for (int c = 0; c < 5; c++)
          acc[i][c] = 0;

      // loop through all pixels
      for (int j = 0; j < image.height; j++)
      {
        for (int i = 0; i < image.width; i++)
        {

          idx = ((j * image.width) + i) * 4;

          // find closest color from palette by measuring (rectilinear) color
          // distance between this pixel and all palette colors
          minDist = 256 + 256 + 256 + 256;
          minColor = 0;
          for (int k = 0; k < palette.size(); k++)
          {

            // In my experience,
            // https://en.wikipedia.org/wiki/Rectilinear_distance works better
            // than https://en.wikipedia.org/wiki/Euclidean_distance

            for (int c = 0; c < 4; c++)
              rgba[c] = Math.abs(palette.colors[k][c] - image.data[idx + c]);

            // weighted alpha seems to help
            dist = rgba[0] + rgba[1] + rgba[2] + (rgba[3] * 4);
            // Remember this color if this is the closest yet
            if (dist < minDist)
            {
              minDist = dist;
              minColor = k;
            }
          }

          for (int c = 0; c < 4; c++)
            acc[minColor][c] += 128 + image.data[idx + c];

          acc[minColor][4]++;

          indexed[j + 1][i + 1] = minColor;
        }
      }
    }

    return new IndexedImage(indexed, palette);
  }

}