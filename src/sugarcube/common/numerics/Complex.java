package sugarcube.common.numerics;


/**
 * This class represents a complex number. Once a Complex
 * object has been instantiated, its values never change,
 * a Complex object is immutable.
 */
public class Complex
{
  public final float re;   // the real part
  public final float im;   // the imaginary part

  public Complex(double real, double imag)
  {
    this.re = (float)real;
    this.im = (float)imag;
  }

  public float realValue()
  {
    return re;
  }

  public float imaginaryValue()
  {
    return im;
  }

  @Override
  public String toString()
  {
    return re + " + " + im + "i";
  }

  public float abs()
  {
    return (float)java.lang.Math.sqrt(re * re + im * im);
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
    return new Complex((float)value * re, (float)value * im);
  }

  public Complex conjugate()
  {
    return new Complex(re, -im);
  }

  public static Complex[] copy(Complex[] array)
  {
    Complex[] copy=new Complex[array.length];
    System.arraycopy(array, 0, copy, 0, copy.length);
    return copy;
  }

  public static Complex[] complexValues(float[] vector)
  {
    Complex[] c = new Complex[vector.length];
    for (int i = 0; i < c.length; i++)
      c[i] = new Complex(vector[i], 0);
    return c;
  }

  public static Complex[] complexValues(int[] vector)
  {
    Complex[] c = new Complex[vector.length];
    for (int i = 0; i < c.length; i++)
      c[i] = new Complex(vector[i], 0);
    return c;
  }

  public static Complex[][] complexValues(float[][] matrix)
  {
    Complex[][] c = new Complex[matrix.length][];
    for (int i = 0; i < matrix.length; i++)
      c[i] = complexValues(matrix[i]);
    return c;
  }

  public static Complex[][] complexValues(int[][] matrix)
  {
    Complex[][] c = new Complex[matrix.length][];
    for (int i = 0; i < matrix.length; i++)
      c[i] = complexValues(matrix[i]);
    return c;
  }

  public static float[] floats(Complex[] vector)
  {
    float[] d = new float[vector.length];
    for (int i = 0; i < d.length; i++)
      d[i] = vector[i].re;
    return d;
  }

  public static float[][] floats(Complex[][] matrix)
  {
    float[][] d = new float[matrix.length][];
    for (int i = 0; i < d.length; i++)
      d[i] = floats(matrix[i]);
    return d;
  }

  public static int[] intValues(Complex[] vector)
  {
    int[] d = new int[vector.length];
    for (int i = 0; i < d.length; i++)
      d[i] = (int)(0.5+vector[i].re);
    return d;
  }

  public static int[][] intValues(Complex[][] matrix)
  {
    int[][] d = new int[matrix.length][];
    for (int i = 0; i < d.length; i++)
      d[i] = intValues(matrix[i]);
    return d;
  }
}



