/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sugarcube.common.numerics.fourier;

/**
 *
 * @author Zoubi
 */
public class StudentsFFT
{
  public static class Complex
  {
    public final float re; // the real part
    public final float im; // the imaginary part
    public Complex(double real, double imag)
    {
      this.re = (float) real;
      this.im = (float) imag;
    }

    public float abs()
    { //also called modulus or magnitude
      return (float) java.lang.Math.sqrt(re * re + im * im);
    }

    public Complex plus(Complex c)
    {
      return new Complex(re + c.re, im + c.im);
    }

    public Complex minus(Complex c)
    {
      return new Complex(re - c.re, im - c.im);
    }

    public Complex times(Complex c)
    {
      return new Complex(re * c.re - im * c.im, re * c.im + im * c.re);
    }

    public Complex times(double value)
    {
      return new Complex(value * re, value * im);
    }

    public Complex conjugate()
    {
      return new Complex(re, -im);
    }
  }

  public static Complex[] fft(Complex[] input)
  {
    int size = input.length;
    if (size == 1)
      return input;
    if (size % 2 != 0)
      throw new RuntimeException("FFT - signal size is not a power of 2");
    Complex[] even = new Complex[size / 2];
    Complex[] odd = new Complex[size / 2];
    for (int k = 0; k < size / 2; k++)
    {
      even[k] = input[2 * k];
      odd[k] = input[2 * k + 1];
    }
    Complex[] q = fft(even);
    Complex[] r = fft(odd);
    Complex[] output = new Complex[size];
    for (int k = 0; k < size / 2; k++)
    {
      double kth = -2 * k * Math.PI / size;
      Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
      Complex tmp = wk.times(r[k]);
      output[k] = (q[k].plus(tmp)).times(1 / Math.sqrt(2));
      output[k + size / 2] = (q[k].minus(tmp)).times(1 / Math.sqrt(2));
    }
    return output;
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
      cos[i] = new Complex(100 * Math.cos(2 * Math.PI * i / (double) size), 0);
    Complex[] fft = StudentsFFT.fft(cos);
    Complex[] ifft = StudentsFFT.ifft(fft);
    System.out.println("\nCosinus");
    for (int i = 0; i < fft.length; i++)
      System.out.print((int) cos[i].re + "=" + (int) ifft[i].re + " ");
    System.out.println("\nSpectrum:");
    for (int i = 0; i < fft.length; i++)
      System.out.print((int) fft[i].abs() + " ");

//    int size2 = 8;
//    Complex[][] cos2 = new Complex[size2][size2];
//    for (int i = 0; i < cos2.length; i++)
//      for (int j = 0; j < cos2[i].length; j++)
//        cos2[i][j] = new Complex(100 * Math.cos(2 * Math.PI * (i + j) / (double) size), 0);
//    Complex[][] fft2 = FFT.fft2d(cos2);
//    Complex[][] ifft2 = FFT.ifft2d(fft2);
//    System.out.println("\nCosinus");
//    for (int i = 0; i < fft2.length; i++)
//      for (int j = 0; j < fft2[i].length; j++)
//        System.out.print((int) cos2[i][j].re + "=" + (int) ifft2[i][j].re + " ");
//    System.out.println("\nSpectrum:");
//    for (int i = 0; i < fft2.length; i++)
//      for (int j = 0; j < fft2[i].length; j++)
//        System.out.print((int) fft2[i][j].abs() + " ");
  }
}
