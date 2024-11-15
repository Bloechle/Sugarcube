package sugarcube.common.numerics;

import sugarcube.common.data.Zen;
import sugarcube.common.interfaces.Unjammable;
import sugarcube.common.interfaces.Vectorizable;

import java.util.*;

public abstract class Vector implements Vectorizable, Unjammable
{

  protected final boolean isRowVector;

  public Vector(boolean isRowVector)
  {
    this.isRowVector = isRowVector;
  }

  public static Vector ints(int... values)
  {
    return new Vector.Int(values);
  }

  public static Vector doubles(double... values)
  {
    return new Vector.Double(values);
  }

  public Vector getVector()
  {
    return this;
  }

  public int dimension()
  {
    return size();
  }

  public abstract Number[] numberValues();

  @Override
  public abstract double[] realValues();

  public abstract int[] intValues();

  public abstract int size();

  public abstract Vector transpose();

  public abstract Vector add(Vectorizable v);

  public abstract Vector subtract(Vectorizable v);

  public abstract Vector tcartbus(Vectorizable v);

  public abstract Vector multiply(Vectorizable v);

  public abstract Vector scale(double factor);

  public abstract Vector copy();

  public abstract Vector concat(Vector v);

  public abstract Vector trim(int begin, int end);

  public double doubleValue()
  {
    return doubleValue(0);
  }

  public double doubleValue(int index)
  {
    return realValues()[index];
  }

  public int intValue()
  {
    return intValue(0);
  }

  public int intValue(int index)
  {
    return intValues()[index];
  }

  public boolean isRowVector()
  {
    return isRowVector;
  }

  public Vector.Double extrema()
  {
    return new Vector.Double(isRowVector, Stat.extrema(realValues()));
  }

  public double delta()
  {
    return Stat.delta(realValues());
  }

  public double max()
  {
    return Stat.max(realValues());
  }

  public double min()
  {
    return Stat.min(realValues());
  }

  public double mean()
  {
    return Stat.mean(realValues());
  }

  public double sdev()
  {
    return Stat.sdev(realValues());
  }

  public double meanRV()
  {
    return Stat.meanRV(realValues());
  }

  public double sdevRV()
  {
    return Stat.sdevRV(realValues());
  }

  public Vector derivate()
  {
    return new Vector.Double(isRowVector, Stat.derivate(realValues()));
  }

  public Vector derivate2()
  {
    return new Vector.Double(isRowVector, Stat.derivate2(realValues()));
  }

  public Vector derivate3()
  {
    return new Vector.Double(isRowVector, Stat.derivate3(realValues()));
  }

  public Vector apply(Evaluable function)
  {
    return new Vector.Double(isRowVector, MoreMath.apply(realValues(), function));
  }

  public Map<Number, java.lang.Double> pdf()
  {
    HashMap<Number, java.lang.Double> pdf = new HashMap<Number, java.lang.Double>();
    for (Number key : numberValues())
      if (pdf.containsKey(key))
        pdf.put(key, pdf.get(key) + 1);
      else
        pdf.put(key, 1.0);

    for (Number key : pdf.keySet())
      pdf.put(key, pdf.get(key) / size());

    return pdf;
  }

  public Matrix profile()
  {
    return profile(0);
  }

  /**
   * Transforms a projection profile or histogram to a binary matrix, that is the image corresponding to the histogram.
   *
   * @return a binary matrix.
   */
  public Matrix profile(int size)
  {
    Vector extrema = extrema();

    int min = (int) extrema.intValue(0);
    int max = (int) extrema.intValue(1);
    if (min > 0)
      min = 0;
    if (max < 0)
      max = 0;

    double f = size <= 0 ? 1.0 : size / (double) (max - min);

    min = (int) (f * min);
    max = (int) (f * max);

    int delta = size <= 0 ? max - min : size;

    if (delta <= 0)
      delta = 1;

    double[] values = realValues();

    double[][] matrix;
    if (isRowVector)
    {
      matrix = new double[delta][values.length];
      for (int col = 0; col < matrix[0].length; col++)
        if (values[col] > 0)
          for (int row = -min; row < values[col] * f - min && row < matrix.length; row++)
            matrix[matrix.length - row - 1][col] = 1.0;
        else
          for (int row = -min; row > values[col] * f - min && row >= 0; row--)
            matrix[matrix.length - row - 1][col] = 1.0;
    }
    else
    {
      matrix = new double[values.length][delta];
      for (int row = 0; row < matrix.length; row++)
        if (values[row] > 0)
          for (int col = -min; col < values[row] * f - min && col < matrix[0].length; col++)
            matrix[row][col] = 1.0;
        else
          for (int col = -min; col > values[row] * f - min && col >= 0; col--)
            matrix[row][col] = 1.0;
    }
    return new Matrix(matrix);
  }

  public Matrix matrixValue(int nbOfVectors)
  {
    double[] vector = this.realValues();
    double[][] matrix;
    int counter = 0;
    if (this.isRowVector)
    {
      matrix = new double[nbOfVectors][size() / nbOfVectors];
      for (int row = 0; row < matrix.length; row++)
        for (int col = 0; col < matrix[0].length; col++)
          matrix[row][col] = vector[counter++];
    }
    else
    {
      matrix = new double[size() / nbOfVectors][nbOfVectors];
      for (int col = 0; col < matrix[0].length; col++)
        for (int row = 0; row < matrix.length; row++)
          matrix[row][col] = vector[counter++];
    }
    return new Matrix(matrix);
  }

  @Override
  public String toString()
  {
    return this.getClass().getSimpleName() + "[" + size() + "]"
      + "\nExtrema" + Zen.Array.String(this.extrema().realValues());
  }

  public static class Double extends Vector
  {
    private double[] values;

    public Double(List<Number> vector)
    {
      this(vector.toArray(new Number[0]));
    }

    public Double(boolean isRowVector, List<Number> vector)
    {
      this(isRowVector, vector.toArray(new Number[0]));
    }

    public Double(Number... vector)
    {
      this(false, vector);
    }

    public Double(boolean isRowVector, Number[] vector)
    {
      super(isRowVector);
      this.values = new double[vector.length];
      for (int i = 0; i < vector.length; i++)
        this.values[i] = vector[i].doubleValue();
    }

    public Double()
    {
      this(false);
    }

    public Double(double... vector)
    {
      this(false, vector);
    }

    public Double(boolean isRowVector, double... vector)
    {
      super(isRowVector);
      this.values = vector;
    }

    @Override
    public Vector.Double trim(int begin, int end)
    {
      return new Vector.Double(isRowVector, Zen.Array.trim(values, begin, end));
    }

    @Override
    public Vector.Double transpose()
    {
      return new Vector.Double(!isRowVector, values);
    }

    @Override
    public Vector.Double add(Vectorizable v)
    {
      return new Vector.Double(isRowVector, Zen.Array.add(values, v.realValues()));
    }

    @Override
    public Vector.Double subtract(Vectorizable v)
    {
      return new Vector.Double(isRowVector, Zen.Array.subtract(values, v.realValues()));
    }

    @Override
    public Vector.Double tcartbus(Vectorizable v)
    {
      return new Vector.Double(isRowVector, Zen.Array.subtract(v.realValues(), values));
    }

    @Override
    public Vector.Double multiply(Vectorizable v)
    {
      return new Vector.Double(isRowVector, Zen.Array.Mult(values, v.realValues()));
    }

    @Override
    public Vector.Double scale(double factor)
    {
      return new Vector.Double(isRowVector, Zen.Array.Mult(values, factor));
    }

    @Override
    public Vector.Double concat(Vector v)
    {
      double[] merge = new double[this.size() + v.size()];
      System.arraycopy(values, 0, merge, 0, size());
      System.arraycopy(v.realValues(), 0, merge, size(), v.size());
      return new Vector.Double(isRowVector, merge);
    }

    @Override
    public Vector.Double copy()
    {
      return new Vector.Double(isRowVector, Zen.Array.copy(values));
    }

    @Override
    public double[] realValues()
    {
      return values;
    }

    @Override
    public int[] intValues()
    {
      return Zen.Array.Ints(values);
    }

    @Override
    public Number[] numberValues()
    {
      Number[] numbers = new Number[values.length];
      for (int i = 0; i < numbers.length; i++)
        numbers[i] = new java.lang.Double(values[i]);
      return numbers;
    }

    @Override
    public int size()
    {
      return values.length;
    }
  }

  public static class Int extends Vector
  {
    private int[] values;

    public Int(List<Number> vector)
    {
      this(vector.toArray(new Number[0]));
    }

    public Int(boolean isRowVector, List<Number> vector)
    {
      this(isRowVector, vector.toArray(new Number[0]));
    }

    public Int(Number... vector)
    {
      this(false, vector);
    }

    public Int(boolean isRowVector, Number[] vector)
    {
      super(isRowVector);
      this.values = new int[vector.length];
      for (int i = 0; i < vector.length; i++)
        this.values[i] = vector[i].intValue();
    }

    public Int()
    {
      this(false, new LinkedList<Number>());
    }

    public Int(int... vector)
    {
      this(false, vector);
    }

    public Int(boolean isRowVector, int... vector)
    {
      super(isRowVector);
      this.values = vector;
    }

    @Override
    public Vector.Int trim(int begin, int end)
    {
      return new Vector.Int(isRowVector, Zen.Array.trim(values, begin, end));
    }

    @Override
    public Vector.Int transpose()
    {
      return new Vector.Int(!isRowVector, values);
    }

    @Override
    public Vector.Int add(Vectorizable v)
    {
      return new Vector.Int(isRowVector, Zen.Array.add(values, Zen.Array.Ints(v.realValues())));
    }

    @Override
    public Vector.Int subtract(Vectorizable v)
    {
      return new Vector.Int(isRowVector, Zen.Array.subtract(values, Zen.Array.Ints(v.realValues())));
    }

    @Override
    public Vector.Int tcartbus(Vectorizable v)
    {
      return new Vector.Int(isRowVector, Zen.Array.subtract(Zen.Array.Ints(v.realValues()), values));
    }

    @Override
    public Vector.Int multiply(Vectorizable v)
    {
      return new Vector.Int(isRowVector, Zen.Array.Mult(values, Zen.Array.Ints(v.realValues())));
    }

    @Override
    public Vector.Int scale(double factor)
    {
      return new Vector.Int(isRowVector, Zen.Array.Mult(values, (int) factor));
    }

    @Override
    public Vector.Int concat(Vector v)
    {
      int[] merge = new int[this.size() + v.size()];
      System.arraycopy(values, 0, merge, 0, size());
      System.arraycopy(v.intValues(), 0, merge, size(), v.size());
      return new Vector.Int(isRowVector, merge);
    }

    @Override
    public Vector.Int copy()
    {
      return new Vector.Int(isRowVector, Zen.Array.copy(values));
    }

    @Override
    public double[] realValues()
    {
      return Zen.Array.toDoubles(values);
    }

    @Override
    public int[] intValues()
    {
      return values;
    }

    @Override
    public Number[] numberValues()
    {
      Number[] numbers = new Number[values.length];
      for (int i = 0; i < numbers.length; i++)
        numbers[i] = new java.lang.Integer(values[i]);
      return numbers;
    }

    @Override
    public int size()
    {
      return values.length;
    }
  }

  @Override
  public int hashCode()
  {
    return Arrays.hashCode(realValues());
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    else if (o == null || o.getClass() != this.getClass())
      return false;
    else
      return Arrays.equals(((Vector) o).realValues(), realValues());
  }
}
