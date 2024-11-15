package sugarcube.common.graphics.vectorize;

public class Palette
{
 
  public byte[][] colors; //colors[nbOfColors][4] RGBA color palette

  public Palette()
  {

  }
  
  public Palette(byte[][] colors)
  {
    this.colors = colors;
  }

  public Palette(int nbOfColors)
  {
    this.generatePalette(nbOfColors);
  }

  // Generating a palette with numberofcolors, array[numberofcolors][4] where
  // [i][0] = R ; [i][1] = G ; [i][2] = B ; [i][3] = A
  private void generatePalette(int nbOfColors)
  {
    this.colors = new byte[nbOfColors][4];
    if (nbOfColors < 8)
    {
      // Grayscale
      double grayStep = 255.0 / (double) (nbOfColors - 1);
      for (int index = 0; index < nbOfColors; index++)
      {
        colors[index][0] = (byte) (-128 + Math.round(index * grayStep));
        colors[index][1] = colors[index][0];
        colors[index][2] = colors[index][0];
        colors[index][3] = (byte) 127;
      }
    } else
    {
      // RGB color cube
      // Number of points on each edge on the RGB color cube
      int k = (int) Math.floor(Math.pow(nbOfColors, 1.0 / 3.0));
      // distance between points
      int colorStep = (int) Math.floor(255 / (k - 1));
      int index = 0;
      int[] rgb = new int[3];

      for (rgb[0] = 0; rgb[0] < k; rgb[0]++)
        for (rgb[1] = 0; rgb[1] < k; rgb[1]++)
          for (rgb[2] = 0; rgb[2] < k; rgb[2]++)
          {
            for (int c = 0; c < rgb.length; c++)
              colors[index][c] = (byte) (-128 + (rgb[c] * colorStep));
            colors[index][3] = (byte) 127;
            index++;
          }

      // Rest is random
      for (; index < nbOfColors; index++)
        for (int c = 0; c < colors[0].length; c++)
          colors[index][c] = (byte) (-128 + Math.floor(Math.random() * 255));

    }
  }
  
  public int size()
  {
    return colors.length;
  }
  
  public static Palette BlackAndWhite()
  {
    byte white = (byte) (255-128);    
    byte[][] colors = new byte[2][4];
    colors[0] = new byte[] {0,0,0,white};
    colors[1] = new byte[] {white,white,white,white};
    return new Palette(colors);
   
  }
}
