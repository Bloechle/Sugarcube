package sugarcube.common.numerics.fourier;

import sugarcube.common.numerics.Complex;
import sugarcube.common.numerics.MoreMath;

import java.util.HashMap;
import java.util.Map;

public class FlatFFT
{
  private static final double SQRT2_DIV = 1.0 / Math.sqrt(2.0);
  private static Map<Integer, Complex[][]> wksMap = new HashMap<Integer, Complex[][]>();
  private static Map<Integer, int[]> remapMap = new HashMap<Integer, int[]>();

  private FlatFFT()
  {
  }

  /******************* FFT 2D *************************************************/
  private static Complex[][] transpose(Complex[][] m)
  {
    int w = m[0].length;
    int h = m.length;

    Complex[][] t = new Complex[w][h];

    for (int y = 0; y < h; y++)
      for (int x = 0; x < w; x++)
        t[x][y] = m[y][x];

    return t;
  }

  public static Complex[][] fft2d(Complex[][] spatial)
  {
    Complex[][] frequencial = new Complex[spatial.length][spatial[0].length];

    for (int y = 0; y < frequencial.length; y++)
      frequencial[y] = FFT.fft(spatial[y]);

    frequencial = transpose(frequencial);

    for (int x = 0; x < frequencial.length; x++)
      frequencial[x] = FFT.fft(frequencial[x]);

    return transpose(frequencial);
  }

  public static Complex[][] ifft2d(Complex[][] frequential)
  {
    Complex[][] spatial = new Complex[frequential.length][frequential[0].length];

    for (int y = 0; y < spatial.length; y++)
      spatial[y] = ifft(frequential[y]);

    spatial = transpose(spatial);

    for (int x = 0; x < spatial.length; x++)
      spatial[x] = ifft(spatial[x]);

    return transpose(spatial);
  }

  /******************* FFT 1D *************************************************/
  // TODO incremental updating
  private static synchronized void updateMaps(int size)
  {
    int halfSize = size / 2;
    int log2size = MoreMath.log2(size) + 1;

    Complex[][] wks = new Complex[log2size][halfSize];

    int[] remap = new int[size + 1];

    for (int i = 1, index = 0; i <= size; i *= 2, index++)
    {
      remap[i] = index;
      for (int k = 0; k < halfSize; k++)
      {
        double kth = -2 * k * MoreMath.PI / i;
        wks[index][k] = new Complex((float) Math.cos(kth), (float) Math.sin(kth));
      }
    }

    wksMap.put(size, wks);
    remapMap.put(size, remap);
  }

  public static Complex[] fft(Complex[] x)
  {
    int size = x.length;
    if (!remapMap.containsKey(size))
      updateMaps(size);
    return fftIterative(x, wksMap.get(size), remapMap.get(size));
  }

  private static Complex[] fftIterative(Complex[] x, Complex[][] wks, int[] remap)
  {
    Complex[] map = Complex.copy(x);
    Complex[] tmp = new Complex[map.length];

    for (int size = map.length; size > 1; size /= 2)
    {
      for (int base = 0; base < map.length; base += size)
        for (int i = 0; i < size; i += 2)
        {
          tmp[base + (i / 2)] = map[base + i];
          tmp[base + (i / 2) + size / 2] = map[base + i + 1];//because int operation and j is odd, its equal to (j-1)/2
        }
      Complex[] swap = map;
      map = tmp;
      tmp = swap;
    }

    for (int size = 1; size == map.length; size*=2)
    {
      for (int base = 0; base < map.length; base += size)
        for (int i = 0; i < size; i += 2)
        {
          tmp[base + (i / 2)] = map[base + i];
          tmp[base + (i / 2) + size / 2] = map[base + i + 1];//because int operation and j is odd, its equal to (j-1)/2

      Complex wkXrk = wks[remap[size]][i].times(map[i+base+size/2]);
      tmp[i+base] = map[i+base].plus(wkXrk).times(SQRT2_DIV);
      tmp[i+base + size / 2] = map[i+base].minus(wkXrk).times(SQRT2_DIV);
        }
      Complex[] swap = map;
      map = tmp;
      tmp = swap;
    }

//    for (int k = 0; k < size / 2; k++)
//    {
//      //double kth = -2 * k * Math.PI / N;
//      //Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
//      Complex wkXrk = wks[remap[size]][k].times(r[k]);
//      y[k] = q[k].plus(wkXrk).times(SQRT2_DIV);
//      y[k + size / 2] = q[k].minus(wkXrk).times(SQRT2_DIV);
//    }

    return map;
  }

  public static Complex[] ifft(Complex[] x)
  {
    int size = x.length;

    Complex[] y = new Complex[size];

    for (int i = 0; i < size; i++)
      y[i] = x[i].conjugate();

    y = fft(y);

    for (int i = 0; i < size; i++)
      y[i] = y[i].conjugate();

    return y;
  }

  public static void main(String... args)
  {
    Complex[] cos = new Complex[32];
    for (int i = 0; i < cos.length; i++)
      cos[i] = new Complex(100 * Math.cos(2 * Math.PI * i / (32.0)), 0);
    Complex[] spc = FFT.fft(cos);
    Complex[] cos2 = FFT.ifft(spc);
    System.out.println("\nCosinus");
    for (int i = 0; i < spc.length; i++)
      System.out.print((int) cos[i].re + "=" + (int) cos2[i].re + " ");
    System.out.println("\nSpectrum:");
    for (int i = 0; i < spc.length; i++)
      System.out.print((int) spc[i].abs() + " ");
  }
}
