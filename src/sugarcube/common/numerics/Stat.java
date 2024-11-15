package sugarcube.common.numerics;

import sugarcube.common.data.Zen;
import sugarcube.common.interfaces.Vectorizable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

public class Stat
{
  /**
   * Returns the array index containing the nearest value.
   *
   * @param array an array of double values
   * @param value the value to find
   * @return the array index corresponding to the nearest value
   */
  public static int binaryFind(double[] array, double value)
  {
    int index = Arrays.binarySearch(array, value);
    if (index < 0)
    {
      index = -index - 1;
      if (index == array.length)
        index = array.length - 1;
      else if (index != 0 && (array[index] - value > value - array[index - 1])) //returns the nearest value index instead of the next index
        index--;
    }
    return index;
  }

  public static int binaryFind(int[] array, int value)
  {
    int index = Arrays.binarySearch(array, value);
    if (index < 0)
    {
      index = -index - 1;
      if (index == array.length)
        index = array.length - 1;
      else if (index != 0 && (array[index] - value > value - array[index - 1]))
        index--;
    }
    return index;
  }

  public static double[] uniform(int levels, double min, double max)
  {
    double[] uniform = new double[levels];
    double step = (max - min) / (double) (levels - 1);
    uniform[0] = min;
    for (int i = 1; i < uniform.length; i++)
      uniform[i] = uniform[i - 1] + step;
    return uniform;
  }

  public static double[] max(Iterable<? extends Vectorizable> samples)
  {
    Iterator<? extends Vectorizable> iterator = samples.iterator();
    if (iterator.hasNext())
    {
      double[] max = Zen.Array.copy(iterator.next().realValues());
      while (iterator.hasNext())
      {
        double[] values = iterator.next().realValues();
        for (int i = 0; i < values.length; i++)
          if (values[i] > max[i])
            max[i] = values[i];
      }
      return max;
    }
    else
      return new double[0];
  }

  public static double[] min(Iterable<? extends Vectorizable> samples)
  {
    Iterator<? extends Vectorizable> iterator = samples.iterator();
    if (iterator.hasNext())
    {
      double[] min = Zen.Array.copy(iterator.next().realValues());
      while (iterator.hasNext())
      {
        double[] values = iterator.next().realValues();
        for (int i = 0; i < values.length; i++)
          if (values[i] < min[i])
            min[i] = values[i];
      }
      return min;
    }
    else
      return new double[0];
  }

  public static double[][] minMax(Iterable<? extends Vectorizable> samples)
  {
    Iterator<? extends Vectorizable> iterator = samples.iterator();
    if (iterator.hasNext())
    {
      double[] min = Zen.Array.copy(iterator.next().realValues());
      double[] max = Zen.Array.copy(min);
      while (iterator.hasNext())
      {
        double[] values = iterator.next().realValues();
        for (int i = 0; i < values.length; i++)
          if (values[i] < min[i])
            min[i] = values[i];
          else if (values[i] > max[i])
            max[i] = values[i];
      }
      return new double[][]
        {
          min, max
        };
    }
    else
      return new double[][]
        {
          {
          },
          {
          }
        };
  }

  public static int median(int... data)
  {
    int[] copy = Zen.Array.copy(data);
    Arrays.sort(copy);
    return copy[copy.length / 2];
  }

  public static float median(float... data)
  {
    float[] copy = Zen.Array.copy(data);
    Arrays.sort(copy);
    return copy[copy.length / 2];
  }

  public static double median(double... data)
  {
    double[] copy = Zen.Array.copy(data);
    Arrays.sort(copy);
    return copy[copy.length / 2];
  }

  public static int max(int... data)
  {
    int max = data[0];
    for (int value : data)
      if (value > max)
        max = value;
    return max;
  }

  public static float max(float... data)
  {
    float max = data[0];
    for (float value : data)
      if (value > max)
        max = value;
    return max;
  }

  public static double max(double... data)
  {
    double max = data[0];
    for (double value : data)
      if (value > max)
        max = value;
    return max;
  }

  public static int min(int... data)
  {
    int min = data[0];
    for (int value : data)
      if (value < min)
        min = value;
    return min;
  }

  public static float min(float... data)
  {
    float min = data[0];
    for (float value : data)
      if (value < min)
        min = value;
    return min;
  }

  public static double min(double... data)
  {
    double min = data[0];
    for (double value : data)
      if (value < min)
        min = value;
    return min;
  }

  public static int min(int[][] data)
  {
    int min = data[0][0];
    for (int[] row : data)
      for (int value : row)
        if (value < min)
          min = value;
    return min;
  }

  public static int max(int[][] data)
  {
    int max = data[0][0];
    for (int[] row : data)
      for (int value : row)
        if (value > max)
          max = value;
    return max;
  }

  public static double min(double[][] data)
  {
    double min = data[0][0];
    for (double[] row : data)
      for (double value : row)
        if (value < min)
          min = value;
    return min;
  }

  public static double max(double[][] data)
  {
    double max = data[0][0];
    for (double[] row : data)
      for (double value : row)
        if (value > max)
          max = value;
    return max;
  }

  public static int[] extrema(int... data)
  {
    int max = data[0];
    int min = data[0];
    for (int value : data)
      if (value > max)
        max = value;
      else if (value < min)
        min = value;
    return Zen.Array.Ints(min, max);
  }

  public static float[] extrema(float[] data)
  {
    float max = data[0];
    float min = data[0];
    for (float value : data)
      if (value > max)
        max = value;
      else if (value < min)
        min = value;
    return Zen.Array.Floats(min, max);
  }

  public static double[] extrema(double... data)
  {
    double max = data[0];
    double min = data[0];
    for (double value : data)
      if (value > max)
        max = value;
      else if (value < min)
        min = value;
    return Zen.Array.doubles(min, max);
  }

  public static int[] extrema(int[][] data)
  {
    int max = data[0][0];
    int min = data[0][0];
    for (int[] row : data)
      for (int value : row)
        if (value > max)
          max = value;
        else if (value < min)
          min = value;
    return Zen.Array.Ints(min, max);
  }

  public static float[] extrema(float[][] data)
  {
    float max = data[0][0];
    float min = data[0][0];
    for (float[] row : data)
      for (float value : row)
        if (value > max)
          max = value;
        else if (value < min)
          min = value;
    return Zen.Array.Floats(min, max);
  }

  public static double[] extrema(double[][] data)
  {
    double max = data[0][0];
    double min = data[0][0];
    for (double[] row : data)
      for (double value : row)
        if (value > max)
          max = value;
        else if (value < min)
          min = value;
    return Zen.Array.doubles(min, max);
  }

  public static int delta(int... data)
  {
    int[] extrema = extrema(data);
    return extrema[1] - extrema[0];
  }

  public static double delta(float[] data)
  {
    float[] extrema = extrema(data);
    return extrema[1] - extrema[0];
  }

  public static double delta(double... data)
  {
    double[] extrema = extrema(data);
    return extrema[1] - extrema[0];
  }

  public static int delta(int[][] data)
  {
    int[] extrema = extrema(data);
    return extrema[1] - extrema[0];
  }

  public static float delta(float[][] data)
  {
    float[] extrema = extrema(data);
    return extrema[1] - extrema[0];
  }

  public static double delta(double[][] data)
  {
    double[] extrema = extrema(data);
    return extrema[1] - extrema[0];
  }

  public static double[] meanRows(double[][] data)
  {
    int rows = data.length;
    int cols = data[0].length;
    double[] mean = new double[cols];
    for (int row = 0; row < rows; row++)
      for (int col = 0; col < cols; col++)
        mean[col] += data[row][col];
    for (int col = 0; col < cols; col++)
      mean[col] /= rows;
    return mean;
  }

  public static double[] meanCols(double[][] data)
  {
    int rows = data.length;
    int cols = data[0].length;
    double[] mean = new double[rows];
    for (int row = 0; row < rows; row++)
      for (int col = 0; col < cols; col++)
        mean[row] += data[row][col];
    for (int row = 0; row < rows; row++)
      mean[row] /= cols;
    return mean;
  }

  public static double mean(int[] data)
  {
    int sum = 0;
    for (int i = 0; i < data.length; i++)
      sum += data[i];
    return sum / (double) data.length;
  }

  public static float mean(float[] data)
  {
    float sum = 0;
    for (int i = 0; i < data.length; i++)
      sum += data[i];
    return sum / data.length;
  }

  public static double mean(double[] data)
  {
    double sum = 0;
    for (int i = 0; i < data.length; i++)
      sum += data[i];
    return sum / data.length;
  }

  public static double meanRV(int[] data)
  {
    int sum = 0;
    int nbOfVariables = 0;
    for (int i = 0; i < data.length; i++)
    {
      sum += data[i] * i;
      nbOfVariables += data[i];
    }
    return sum / (double) nbOfVariables;
  }

  public static double meanRV(double[] data)
  {
    double sum = 0;
    double nbOfVariables = 0;
    for (int i = 0; i < data.length; i++)
    {
      sum += data[i] * i;
      nbOfVariables += data[i];
    }
    return sum / nbOfVariables;
  }

  public static double[] mean(Iterable<? extends Vectorizable> samples)
  {
    Iterator<? extends Vectorizable> iterator = samples.iterator();
    if (iterator.hasNext())
    {
      double[] mean = Zen.Array.copy(iterator.next().realValues());

      int size = 1;
      while (iterator.hasNext())
      {
        size++;
        double[] values = iterator.next().realValues();
        for (int i = 0; i < values.length; i++)
          mean[i] += values[i];
      }

      for (int i = 0; i < mean.length; i++)
        mean[i] /= size;

      return mean;
    }
    else
      return new double[0];
  }

  public static double[] mean(double[][] samples)
  {
    double[] mean = new double[samples[0].length];
    for (double[] s : samples)
      for (int i = 0; i < s.length; i++)
        mean[i] += s[i];

    int size = samples.length;
    for (int i = 0; i < mean.length; i++)
      mean[i] /= size;

    return mean;
  }

  public static double[][] meanSDev(Iterable<? extends Vectorizable> samples)
  {
    Iterator<? extends Vectorizable> iterator = samples.iterator();
    if (iterator.hasNext())
    {
      double[] mean = iterator.next().realValues();
      double[] sdev = Zen.Array.square(mean);

      int size = 1;
      while (iterator.hasNext())
      {
        size++;
        double[] values = iterator.next().realValues();
        for (int i = 0; i < values.length; i++)
        {
          mean[i] += values[i];
          sdev[i] += values[i] * values[i];
        }
      }

      for (int i = 0; i < mean.length; i++)
      {
        sdev[i] /= size;
        mean[i] /= size;
        sdev[i] -= mean[i] * mean[i];
        sdev[i] = Math.sqrt(sdev[i]);
      }
      return new double[][]
        {
          mean, sdev
        };
    }
    else
      return new double[][]
        {
          {
          },
          {
          }
        };
  }

  public static double sdev(double[] data)
  {
    if (data.length == 0 || data.length == 1)
      return 0.0;
    else
    {
      double mean = 0;
      double sdev = 0;
      for (int i = 0; i < data.length; i++)
      {
        mean += data[i];
        sdev += data[i] * data[i];
      }
      return  Math.sqrt(Math.abs(sdev / data.length - (mean /= data.length) * mean));
    }
  }

  public static double sdevRV(int[] data)
  {
    double mean = 0;
    double sdev = 0;
    int nbOfVariables = 0;
    for (int i = 0; i < data.length; i++)
    {
      mean += data[i] * i;
      sdev += data[i] * i * i;
      nbOfVariables += data[i];
    }
    return Math.sqrt(sdev / nbOfVariables - (mean /= nbOfVariables) * mean);
  }

  public static double sdevRV(double[] data)
  {
    double mean = 0;
    double sdev = 0;
    double nbOfVariables = 0;
    for (int i = 0; i < data.length; i++)
    {
      mean += data[i] * i;
      sdev += data[i] * i * i;
      nbOfVariables += data[i];
    }
    return Math.sqrt(sdev / nbOfVariables - (mean /= nbOfVariables) * mean);
  }

  public static double[] pdf(double[] data)
  {
    double[] fct = new double[data.length];
    double sum = 0.0;
    for (int i = 0; i < data.length; i++)
      sum += data[i];
    for (int i = 0; i < data.length; i++)
      fct[i] = data[i] / sum;
    return fct;
  }

  public static double[] pdf(int[] data)
  {
    double[] fct = new double[data.length];
    double sum = 0.0;
    for (int i = 0; i < data.length; i++)
      sum += data[i];
    for (int i = 0; i < data.length; i++)
      fct[i] = data[i] / sum;
    return fct;
  }

  public static int[] gaussianNoise(int[] data, double sdev)
  {
    Random random = new Random();
    int[] gaussianNoise = new int[data.length];
    for (int i = 0; i < data.length; i++)
      gaussianNoise[i] = (int) Math.round(data[i] + random.nextGaussian() * sdev);
    return gaussianNoise;
  }

  public static double[] gaussianNoise(double[] data, double sdev)
  {
    Random random = new Random();
    double[] gaussianNoise = new double[data.length];
    for (int i = 0; i < data.length; i++)
      gaussianNoise[i] = data[i] + random.nextGaussian() * sdev;
    return gaussianNoise;
  }

  public static double[] normalize(double[] data, double[] mean, double[] sdev)
  {
    double[] normalized = new double[data.length];
    for (int i = 0; i < data.length; i++)
      normalized[i] = (data[i] - mean[i]) / (sdev[i] > 0 ? sdev[i] : 1.0);
    return normalized;
  }

  public static double[] normalizeIt(double[] data, double[] mean, double[] sdev)
  {
    for (int i = 0; i < data.length; i++)
      data[i] = (data[i] - mean[i]) / (sdev[i] > 0 ? sdev[i] : 1.0);
    return data;
  }

  public static double[] normalize(int[] data, double factor)
  {
    double[] normalized = new double[data.length];
    for (int i = 0; i < data.length; i++)
      normalized[i] = data[i] / factor;
    return normalized;
  }

  public static double[] normalize(double[] data, double factor)
  {
    double[] normalized = new double[data.length];
    for (int i = 0; i < data.length; i++)
      normalized[i] = data[i] / factor;
    return normalized;
  }

  public static int[] derivate(int[] data)
  {
    int[] derivate = new int[data.length - 1];
    for (int i = 0; i < derivate.length; i++)
      derivate[i] = data[i + 1] - data[i];
    return derivate;
  }

  public static double[] derivate(double[] data)
  {
    double[] derivate = new double[data.length - 1];
    for (int i = 0; i < derivate.length; i++)
      derivate[i] = data[i + 1] - data[i];
    return derivate;
  }

  public static int[] derivate2(int[] data)
  {
    return derivate(derivate(data));
  }

  public static double[] derivate2(double[] data)
  {
    return derivate(derivate(data));
  }

  public static int[] derivate3(int[] data)
  {
    return derivate(derivate(derivate(data)));
  }

  public static double[] derivate3(double[] data)
  {
    return derivate(derivate(derivate(data)));
  }

  public static int[] histogram(double[][] m, int levels)
  {
    double[] extrema = extrema(m);
    double min = extrema[0];
    double delta = extrema[1] - min;
    double factor = (levels - 1) / delta;

    int[] map = new int[levels];
    for (int y = 0; y < m.length; y++)
      for (int x = 0; x < m[0].length; x++)
        map[(int) (0.5 + (m[y][x] - min) * factor)]++;

    return map;
  }

  public static double[] projectionH(double[][] m)
  {
    double[] proj = new double[m[0].length];
    for (int y = 0; y < m.length; y++)
      for (int x = 0; x < m[0].length; x++)
        proj[x] += m[y][x];
    return proj;
  }

  public static double[] projectionV(double[][] m)
  {
    double[] proj = new double[m.length];
    for (int y = 0; y < m.length; y++)
      for (int x = 0; x < m[0].length; x++)
        proj[y] += m[y][x];
    return proj;
  }
}
