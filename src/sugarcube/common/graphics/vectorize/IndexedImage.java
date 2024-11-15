package sugarcube.common.graphics.vectorize;

// Container for the color-indexed image before and tracedata after
// vectorizing
public class IndexedImage
{
  private static final int[] DX =
  { -1, 0, 1, -1, 1, -1, 0, 1 };
  private static final int[] DY =
  { -1, -1, -1, 0, 0, 1, 1, 1 };
  
  public int width, height;
  public int[][] data; // color indexes
  public Palette palette;


  public IndexedImage(int[][] data, Palette palette)
  {
    this.data = data;
    this.palette = palette;
    width = data[0].length - 2;
    height = data.length - 2;// Color quantization adds +2 to the original
                             // width and height
  }
  
  public VectorImage vectorize(DTOptions options)
  {
    return new VectorImage(separateLayers(), palette, options);
  }

  // 2. Layer separation and edge detection
  // Edge node types ( ▓:light or 1; ░:dark or 0 )
  // 12 ░░ ▓░ ░▓ ▓▓ ░░ ▓░ ░▓ ▓▓ ░░ ▓░ ░▓ ▓▓ ░░ ▓░ ░▓ ▓▓
  // 48 ░░ ░░ ░░ ░░ ░▓ ░▓ ░▓ ░▓ ▓░ ▓░ ▓░ ▓░ ▓▓ ▓▓ ▓▓ ▓▓
  // 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15
  //

  private ImageLayer[] separateLayers()
  {
    // Creating layers for each indexed color in arr
    int val = 0;
    int width = data[0].length;
    int height = data.length;
    int[] neighbors = new int[8];
    ImageLayer[] layers = new ImageLayer[palette.size()];

    for (int i = 0; i < layers.length; i++)
      layers[i] = new ImageLayer(width, height);
    // Looping through all pixels and calculating edge node type
    for (int j = 1; j < height - 1; j++)
    {
      for (int i = 1; i < width - 1; i++)
      {
        val = data[j][i];

        // Are neighbor pixel colors the same?
        for (int n = 0; n < neighbors.length; n++)
          neighbors[n] = data[j + DY[n]][i + DX[n]] == val ? 1 : 0;

        // this pixel's type and looking back on previous pixels
        layers[val].data[j + 1][i + 1] = 1 + (neighbors[4] * 2) + (neighbors[7] * 4) + (neighbors[6] * 8);
        if (neighbors[3] == 0)
          layers[val].data[j + 1][i] = 0 + 2 + (neighbors[6] * 4) + (neighbors[5] * 8);

        if (neighbors[1] == 0)
          layers[val].data[j][i + 1] = 0 + (neighbors[2] * 2) + (neighbors[4] * 4) + 8;

        if (neighbors[0] == 0)
          layers[val].data[j][i] = 0 + (neighbors[1] * 2) + 4 + (neighbors[3] * 8);

      }
    }

    return layers;
  }
}