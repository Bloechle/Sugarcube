package sugarcube.common.numerics.fourier;

import sugarcube.common.numerics.Complex;
import sugarcube.common.numerics.MoreMath;

import java.util.HashMap;
import java.util.Map;

public class FFT
{
    private static final double SQRT2_DIV = 1.0 / Math.sqrt(2.0);
    private static Map<Integer, Complex[][]> wksMap = new HashMap<>();
    private static Map<Integer, int[]> remapMap = new HashMap<>();

    private FFT()
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
        long time = System.currentTimeMillis();
        Complex[][] frequencial = new Complex[spatial.length][spatial[0].length];

        for (int y = 0; y < frequencial.length; y++)
            frequencial[y] = FFT.fft(spatial[y]);

        frequencial = transpose(frequencial);

        for (int x = 0; x < frequencial.length; x++)
            frequencial[x] = FFT.fft(frequencial[x]);

        System.out.println("FFT computation time: " + (System.currentTimeMillis() - time));
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
                double kth = -2 * k * Math.PI / i;
                wks[index][k] = new Complex((float) Math.cos(kth), (float) Math.sin(kth));
            }
        }

        wksMap.put(size, wks);
        remapMap.put(size, remap);
    }

    public static Complex[] fft(Complex[] input)
    {
        int exp = (int) Math.ceil(Math.log(input.length) / Math.log(2));
        Complex[] frequency = new Complex[(int) Math.pow(2, exp)];

        System.arraycopy(input, 0, frequency, 0, input.length);
        if (frequency.length != input.length)
            for (int i = input.length; i < frequency.length; i++)
                frequency[i] = input[input.length - (i - input.length) - 2];

        if (!remapMap.containsKey(frequency.length))
            updateMaps(frequency.length);

        frequency = fftIterative(frequency, wksMap.get(frequency.length), remapMap.get(frequency.length));


        if (frequency.length != input.length)
        {
            Complex[] trimmedFrequency = new Complex[input.length];
            System.arraycopy(frequency, 0, trimmedFrequency, 0, trimmedFrequency.length);
            return trimmedFrequency;
        }
        return frequency;
    }

    private static Complex[] fftIterative(Complex[] input, Complex[][] wks, int[] remap)
    {
        Complex[] map = input;
        Complex[] tmp = new Complex[input.length];

        for (int size = input.length; size > 2; size /= 2)
        {
//      System.out.println("\ndecsize="+size);
//      for(int i=0; i<map.length; i++)
//        System.out.print((int)map[i].re+" ");
            for (int base = 0; base < map.length; base += size)
                for (int i = 0; i < size; i += 2)
                {
                    tmp[base + i / 2] = map[base + i];
                    tmp[base + i / 2 + size / 2] = map[base + i + 1];//because int operation and j is odd, its equal to (j-1)/2
                }
            Complex[] swap = map;
            map = tmp;
            tmp = swap;
        }

        for (int size = 2; size <= map.length; size *= 2)
        {
//      System.out.println("\nincsize="+size);
//      for(int i=0; i<map.length; i++)
//        System.out.print((int)map[i].re+" ");
            for (int base = 0; base < map.length; base += size)
                for (int i = 0; i < size; i += 2)
                {
                    Complex wkXrk = wks[remap[size]][i / 2].times(map[base + i / 2 + size / 2]);
                    tmp[base + i / 2] = map[base + i / 2].plus(wkXrk).times(SQRT2_DIV);
                    tmp[base + i / 2 + size / 2] = map[base + i / 2].minus(wkXrk).times(SQRT2_DIV);
                }
            Complex[] swap = map;
            map = tmp;
            tmp = swap;
        }

        return map;
    }

    private static Complex[] fftRecursive(Complex[] input, Complex[][] wks, int[] remap)
    {
        int size = input.length;
        if (size == 1)
            return input;
        if (size % 2 != 0)
            throw new RuntimeException("FFT - N is not a power of 2");

        Complex[] even = new Complex[size / 2];
        Complex[] odd = new Complex[size / 2];

        for (int k = 0; k < size / 2; k++)
        {
            even[k] = input[2 * k];
            odd[k] = input[2 * k + 1];
        }

        Complex[] q = fftRecursive(even, wks, remap);
        Complex[] r = fftRecursive(odd, wks, remap);

        Complex[] y = new Complex[size];
        for (int k = 0; k < size / 2; k++)
        {
            //double kth = -2 * k * Math.PI / N;
            //Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            Complex wkXrk = wks[remap[size]][k].times(r[k]);
            y[k] = q[k].plus(wkXrk).times(SQRT2_DIV);
            y[k + size / 2] = q[k].minus(wkXrk).times(SQRT2_DIV);
        }

        return y;
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
        int size = 8;
        Complex[] cos = new Complex[size];
        for (int i = 0; i < cos.length; i++)
            cos[i] = new Complex(100 * Math.cos(2 * MoreMath.PI * i / (double) size), 0);
        Complex[] fft = FFT.fft(cos);
        Complex[] ifft = FFT.ifft(fft);
        System.out.println("\nCosinus");
        for (int i = 0; i < fft.length; i++)
            System.out.print((int) cos[i].re + "=" + (int) ifft[i].re + " ");
        System.out.println("\nSpectrum:");
        for (int i = 0; i < fft.length; i++)
            System.out.print((int) fft[i].abs() + " ");

        int size2 = 8;
        Complex[][] cos2 = new Complex[size2][size2];
        for (int i = 0; i < cos2.length; i++)
            for (int j = 0; j < cos2[i].length; j++)
                cos2[i][j] = new Complex(100 * Math.cos(2 * MoreMath.PI * (i + j) / (double) size), 0);
        Complex[][] fft2 = FFT.fft2d(cos2);
        Complex[][] ifft2 = FFT.ifft2d(fft2);
        System.out.println("\nCosinus");
        for (int i = 0; i < fft2.length; i++)
            for (int j = 0; j < fft2[i].length; j++)
                System.out.print((int) cos2[i][j].re + "=" + (int) ifft2[i][j].re + " ");
        System.out.println("\nSpectrum:");
        for (int i = 0; i < fft2.length; i++)
            for (int j = 0; j < fft2[i].length; j++)
                System.out.print((int) fft2[i][j].abs() + " ");
    }
}
