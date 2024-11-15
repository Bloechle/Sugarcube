package sugarcube.common.numerics;

import sugarcube.common.data.Zen;
import sugarcube.common.interfaces.Unjammable;
import sugarcube.common.interfaces.Vectorizable;
import sugarcube.common.numerics.jama.EigenvalueDecomposition;

import java.awt.*;
import java.util.Map;

/**
 * A Matrix class based upon Jama (Java Matrix)... Be carefull, a matrix may be a list of vectors, in this case, vectors are supposed to be column vectors.
 */
public class Matrix extends sugarcube.common.numerics.jama.Matrix implements Unjammable
{
  private final double[][] matrix;

  private Matrix(sugarcube.common.numerics.jama.Matrix matrix)
  {
    this(matrix.getArray());
  }

  public Matrix(double[][] matrix)
  {
    super(matrix);
    this.matrix = matrix;
  }

  public Matrix(float[][] matrix)
  {
    this(Zen.Array.toDoubles(matrix));
  }

  public Matrix(int[][] matrix)
  {
    this(Zen.Array.toDoubles(matrix));
  }

  public Matrix(int rows, int cols)
  {
    super(rows, cols);
    this.matrix = getArray();
  }

  public Vector[] vectorValues()
  {
    return vectorValues(false);
  }

  public Matrix applyLog(final double innerFactor, final double innerShift, final double outerFactor, final double outerShift)
  {
    return this.apply(new Evaluable()
    {
      @Override
      public double eval(double value, int... coords)
      {
        return Math.log(value * innerFactor + innerShift) * outerFactor + outerShift;
      }
    });
  }

  public Matrix applyArcTan(final double innerFactor, final double innerShift, final double outerFactor, final double outerShift)
  {
    return this.apply(new Evaluable()
    {
      @Override
      public double eval(double value, int... coords)
      {
        return Math.atan(value * innerFactor + innerShift) * outerFactor + outerShift;
      }
    });
  }

  public Vector[] vectorValues(boolean isRowVector)
  {
    Vector[] vectors;
    if (isRowVector)
      vectors = new Vector[matrix.length];
    else
      vectors = new Vector[matrix[0].length];
    for (int index = 0; index < vectors.length; index++)
      vectors[index] = vectorValue(index, isRowVector);
    return vectors;
  }

  public Vector vectorValue()
  {
    return vectorValue(false);
  }

  public Vector vectorValue(boolean isRowVector)
  {
    double[] vector = new double[area()];
    int index = 0;
    if (isRowVector)
      for (int row = 0; row < rows(); row++)
        for (int col = 0; col < cols(); col++)
          vector[index++] = matrix[row][col];
    else
      for (int col = 0; col < cols(); col++)
        for (int row = 0; row < rows(); row++)
          vector[index++] = matrix[row][col];
    return new Vector.Double(isRowVector, vector);
  }

  public Vector vectorValue(int col)
  {
    return vectorValue(col, false);
  }

  public Vector vectorValue(int index, boolean isRowVector)
  {
    double[] vector;
    if (isRowVector)
    {
      vector = new double[cols()];
      for (int col = 0; col < vector.length; col++)
        vector[col] = matrix[index][col];
    }
    else
    {
      vector = new double[rows()];
      for (int row = 0; row < vector.length; row++)
        vector[row] = matrix[row][index];
    }

    return new Vector.Double(isRowVector, vector);
  }

  public double[][] doubleValues()
  {
    return matrix;
  }

  public float[][] floatValues()
  {
    return Zen.Array.toFloats(matrix);
  }

  public int[][] intValues()
  {
    return Zen.Array.Ints(matrix);
  }

  public Vector projectionH()
  {
    return new Vector.Double(true, Stat.projectionH(matrix));
  }

  public Vector projectionV()
  {
    return new Vector.Double(false, Stat.projectionV(matrix));
  }

  public Vector meanVector()
  {
    return new Vector.Double(Stat.meanCols(matrix));
  }

  public Matrix abs()
  {
    return new Matrix(Zen.Array.abs(matrix));
  }

  public Matrix convolve(int[][] mask, int... factorAndShift)
  {
    return convolve(Zen.Array.toDoubles(mask), Zen.Array.toDoubles(factorAndShift));
  }

  public Matrix convolve(double[][] mask, double... factorAndShift)
  {
    return new Matrix(convolve(matrix, mask, factorAndShift));
  }

  public static double[][] convolve(double[][] m, double[][] n, double[] fs)
  {
    int h = m.length;
    int w = m[0].length;

    double factor = fs != null && fs.length > 0 ? fs[0] : 1.0;
    double shift = fs != null && fs.length > 1 ? fs[1] : 0.0;

    int dv = n.length / 2;
    int du = n[0].length / 2;

    double[][] convolved = new double[h][w];
    for (int y = 0; y < h; y++)
      for (int x = 0; x < w; x++)
      {
        double sum = 0;
        for (int v = 0; v < n.length; v++)
          for (int u = 0; u < n[0].length; u++)
          {
            int nx = x - u + du;
            int ny = y - v + dv;
            sum += m[ny >= 0 && ny < m.length ? ny : ny < 0 ? 0 : m.length - 1][nx >= 0 && nx < m[0].length ? nx : nx < 0 ? 0 : m[0].length - 1] * n[v][u];
          }
        convolved[y][x] = sum / factor + shift;
      }

    return convolved;
  }

  public Matrix add(double value)
  {
    return new Matrix(Zen.Array.add(matrix, value));
  }

  public Matrix addVector(Vectorizable vector)
  {
    return new Matrix(Zen.Array.addCols(matrix, vector.realValues()));
  }

  public Matrix subtractVector(Vectorizable vector)
  {
    return new Matrix(Zen.Array.subtractCols(matrix, vector.realValues()));
  }

  public Matrix divide(double factor)
  {
    return new Matrix(Zen.Array.Divide(matrix, factor));
  }

  public Matrix multiply(double factor)
  {
    return new Matrix(Zen.Array.Mult(matrix, factor));
  }

  public Matrix trimVectors(int begin, int end)
  {
    return new Matrix(Zen.Array.trimCols(matrix, begin, end));
  }

  public Matrix reduce(int dimension)
  {
    if (dimension > matrix[0].length)
      dimension = matrix[0].length;
    return trimVectors(matrix[0].length - dimension, matrix[0].length);
  }

  public double dotDot(Vectorizable rowVector, Vectorizable colVector)
  {
    return dotDot(rowVector.realValues(), colVector.realValues());
  }

  public double dotDot(double[] row, double[] col)
  {
    double sum = 0;
    double res = 0;
    for (int x = 0; x < matrix[0].length; x++)
    {
      sum = 0;
      for (int y = 0; y < matrix.length; y++)
        sum += matrix[y][x] * row[y];

      res += sum * col[x];
    }
    return res;
  }

  @Override
  public Matrix inverse()
  {
    return new Matrix(super.inverse());
  }

  public Vector mean()
  {
    double[] mean = new double[matrix.length];
    for (int row = 0; row < matrix.length; row++)
      for (int col = 0; col < matrix[0].length; col++)
        mean[row] += matrix[row][col];
    int size = matrix[0].length;
    for (int i = 0; i < mean.length; i++)
      mean[i] /= size;
    return new Vector.Double(mean);
  }

  public Matrix covariance()
  {
    return covariance(cols());
  }

  public Matrix covariance_1()
  {
    return covariance(cols() - 1);
  }

  /**
   * Computes the covariance matrix of a set of vectors given a normalization factor. The set of vectors encapsulated in this Matrix object must be represented
   * as column vectors.
   *
   * @param normalize the normalization factor
   * @return the covariance matrix
   */
  private Matrix covariance(double normalize)
  {
    int rows = rows();
    int cols = cols();

    double[][] data = doubleValues();
    double[] mean = Stat.meanCols(data);
    double[][] covariance = new double[rows][rows];

    for (int col = 0; col < cols; col++)
    {
      double[] d = new double[rows]; //d stands for delta or difference
      for (int row = 0; row < rows; row++)
        d[row] = data[row][col] - mean[row];

      for (int covRow = 0; covRow < rows; covRow++)
      {
        covariance[covRow][covRow] += d[covRow] * d[covRow];
        for (int covCol = covRow + 1; covCol < rows; covCol++)
          covariance[covRow][covCol] += d[covRow] * d[covCol];
      }
    }

    // normalization and symetric copy of the half covariance matrix
    for (int row = 0; row < rows; row++)
    {
      covariance[row][row] /= normalize;
      for (int col = row + 1; col < rows; col++)
        covariance[col][row] = covariance[row][col] /= normalize;
    }

    return new Matrix(covariance);
  }

  public Matrix eigenVectors()
  {
    return new Matrix(new EigenvalueDecomposition(this).getV());
  }

  public Matrix times(Matrix data)
  {
    return new Matrix(super.times(data).getArray());
  }

  @Override
  public Matrix transpose()
  {
    return new Matrix(Zen.Array.Transpose(matrix));
  }

  public Matrix toBlockMatrix(int blockWidth, int blockHeight)
  {
    int blockArea = blockWidth * blockHeight;
    int blocksPerLine = matrix[0].length / blockWidth;
    double[][] blocks = new double[blockArea][area() / blockArea];
    for (int row = 0; row < blocks.length; row++)
      for (int col = 0; col < blocks[0].length; col++)
        blocks[row][col] = matrix[(row / blockWidth) % blockHeight + col / blocksPerLine * blockHeight][(row % blockWidth + col * blockWidth) % matrix[0].length];
    return new Matrix(blocks);
  }

  public Matrix toFlatMatrix(int blockWidth, int blockHeight, int matrixCols)
  {
    return toFlatMatrix(blockWidth, blockHeight, matrixCols, area() / matrixCols);
  }

  public Matrix toFlatMatrix(int blockWidth, int blockHeight, int matrixCols, int matrixRows)
  {
    int blocksPerLine = matrixCols / blockWidth;
    double[][] flat = new double[matrixRows][matrixCols];
    for (int row = 0; row < matrix.length; row++)
      for (int col = 0; col < matrix[0].length; col++)
        flat[(row / blockWidth) % blockHeight + col / blocksPerLine * blockHeight][(row % blockWidth + col * blockWidth) % matrixCols] = matrix[row][col];
    return new Matrix(flat);
  }

  public Matrix3D toFlatMatrix3D(int blockWidth, int blockHeight, int matrixCols, int matrixRows)
  {
    int blockArea = blockWidth * blockHeight;
    int planes = rows() / blockArea;
    int blocksPerLine = matrixCols / blockWidth;
    double[][][] flat = new double[planes][matrixRows][matrixCols];
    for (int row = 0; row < matrix.length; row++)
      for (int col = 0; col < matrix[0].length; col++)
        flat[row / blockArea][(row / blockWidth) % blockHeight + col / blocksPerLine * blockHeight][(row % blockWidth + col * blockWidth) % matrixCols] = matrix[row][col];
    return new Matrix3D(flat);
  }

  public Matrix quantize(int levels)
  {
    return quantize(levels, null);
  }

  public Matrix quantize(int levels, Vector outExtrema)
  {
    Vector matExtrema = extrema();
    if (outExtrema == null)
      outExtrema = matExtrema;
    double matDelta = matExtrema.delta();
    double outDelta = outExtrema.delta();
    double matFactor = (levels - 1) / matDelta;
    double outFactor = outDelta / (levels - 1);
    double matMin = matExtrema.doubleValue(0);
    double outMin = outExtrema.doubleValue(0);

    double[][] quantized = new double[matrix.length][matrix[0].length];
    for (int row = 0; row < quantized.length; row++)
      for (int col = 0; col < quantized[0].length; col++)
        quantized[row][col] = MoreMath.round((matrix[row][col] - matMin) * matFactor) * outFactor + outMin;
    return new Matrix(quantized);
  }

  public Matrix quantizePDF(int levels, int levelsGranularity)
  {
    return quantizePDF(levels, levelsGranularity, null, null);
  }

  public Matrix quantizePDF(int levels, int levelsGranularity, Evaluable f)
  {
    return quantizePDF(levels, levelsGranularity, f, null);
  }

  public Matrix quantizePDF(int levels, int levelsGranularity, Evaluable f, Vector outExtrema)
  {
    // levels=6, levelsGranularity=256
    // equalize function over 256 levels 0..255
    double[] map = equalize(levelsGranularity, f).realValues();

    Vector matExtrema = extrema();
    double matDelta = matExtrema.delta();
    double matMin = matExtrema.doubleValue(0);

    double[] uniform = Stat.uniform(levels, 0, levelsGranularity - 1);

    double[] pdfLevels = new double[uniform.length];
    for (int i = 0; i < pdfLevels.length; i++)
      pdfLevels[i] = Stat.binaryFind(map, uniform[i]) / (levelsGranularity - 1.0) * matDelta + matMin;

    double[][] quantized = new double[rows()][cols()];
    for (int row = 0; row < rows(); row++)
      for (int col = 0; col < cols(); col++)
        quantized[row][col] = pdfLevels[Stat.binaryFind(pdfLevels, matrix[row][col])];

    if (outExtrema != null)
    {
      double outFactor = outExtrema.delta() / matDelta;
      double outMin = outExtrema.min();
      for (int row = 0; row < rows(); row++)
        for (int col = 0; col < cols(); col++)
          quantized[row][col] = (quantized[row][col] - matMin) * outFactor + outMin;
    }

    return new Matrix(quantized);
  }

  public Vector equalize(int levels)
  {
    return equalize(levels, null);
  }

  public Vector equalize(int levels, Evaluable function)
  {
    double[] equalize = histogram(levels).apply(function).realValues();
    for (int i = 1; i < equalize.length; i++)
      equalize[i] += equalize[i - 1];

    double factor = (levels - 1) / equalize[equalize.length - 1];
    for (int i = 0; i < equalize.length; i++)
      equalize[i] *= factor;
    return new Vector.Double(equalize);
  }

  /**
   * Returns the occurence of each level in this matrix. The matrix values are rounded to their nearest levels (linear levels).
   *
   * @return a vector containing the occurence of each level.
   */
  public Vector histogram(int levels)
  {
    return new Vector.Int(Stat.histogram(matrix, levels));
  }

  /**
   * Returns a map which size is equal to the number of different values in the matrix. To each value is associated a probability. This method may be used after
   * a quantization.
   *
   * @return a map containing the probabilities of each value.
   */
  public Map<Number, Double> pdf()
  {
    return this.vectorValue().pdf();
  }

  public double entropy()
  {
    double entropy = 0.0;
    for (double probability : pdf().values())
      entropy += (probability != 0.0 ? probability * MoreMath.log2(probability) : 0);
    return -entropy;
  }

  public Matrix apply(Evaluable function)
  {
    if (function == null)
      return this;
    double[][] result = new double[rows()][cols()];
    for (int row = 0; row < result.length; row++)
      for (int col = 0; col < result[0].length; col++)
        result[row][col] = function.eval(matrix[row][col], col, row);
    return new Matrix(result);
  }

  public Vector extrema()
  {
    return new Vector.Double(Stat.extrema(matrix));
  }

  public double max()
  {
    return Stat.max(matrix);
  }

  public double min()
  {
    return Stat.min(matrix);
  }

  public int rows()
  {
    return matrix.length;
  }

  public int cols()
  {
    return matrix[0].length;
  }

  public int height()
  {
    return matrix.length;
  }

  public int width()
  {
    return matrix[0].length;
  }

  public Dimension dimension()
  {
    return new Dimension(cols(), rows());
  }

  public int area()
  {
    return matrix.length * matrix[0].length;
  }

  @Override
  public Matrix copy()
  {
    return new Matrix(Zen.Array.copy(matrix));
  }

  @Override
  public Matrix clone()
  {
    return this.copy();
  }
}
