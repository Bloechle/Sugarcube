package sugarcube.common.numerics;

import sugarcube.common.data.Zen;
import sugarcube.common.interfaces.Unjammable;

public class Matrix3D implements Unjammable
{
    private final double[][][] cube;

    public Matrix3D(int[][][] cube)
    {
        this.cube = Zen.Array.toDoubles(cube);
    }

    public Matrix3D(float[][][] cube)
    {
        this.cube = Zen.Array.toDoubles(cube);
    }

    public Matrix3D(double[][][] cube)
    {
        this.cube = cube;
    }

    public Matrix3D(int planes, int rows, int cols)
    {
        this.cube = new double[planes][rows][cols];
    }

    public double getValue(int i, int row, int col)
    {
        return cube[i][row][col];
    }

    public void setValue(int i, int row, int col, double value)
    {
        cube[i][row][col] = value;
    }
    /**
     * #### #### ####
     * #### #### ####
     * #### #### ####
     */
    public Matrix getMatrix()
    {
        double[][] matrix = new double[rows()][cols() * dims()];
        for (int row = 0; row < rows(); row++)
            for (int col = 0; col < cols(); col++)
                for (int plane = 0; plane < dims(); plane++)
                    matrix[row][col + plane * cols()] = cube[plane][row][col];
        return new Matrix(matrix);
    }

    public double[][][] doubleValues()
    {
        return cube;
    }

    public float[][][] floatValues()
    {
        return Zen.Array.toFloats(cube);
    }

    public int[][][] intValues()
    {
        return Zen.Array.Ints(cube);
    }

    public Matrix toBlockMatrix(int blockWidth, int blockHeight)
    {
        int blockArea = blockWidth * blockHeight;
        int blocksPerLine = cols() / blockWidth;
        double[][] blocks = new double[blockArea * dims()][area() / blockArea];
        for (int row = 0; row < blockArea; row++)
            for (int col = 0; col < blocks[0].length; col++)
                for (int plane = 0; plane < dims(); plane++)
                    blocks[row + plane * blockArea][col] = cube[plane][(row / blockWidth) % blockHeight + col / blocksPerLine * blockHeight][(row % blockWidth + col * blockWidth) % cols()];
        return new Matrix(blocks);
    }

    public int dims()
    {
        return cube.length;
    }

    public int rows()
    {
        return cube[0].length;
    }

    public int cols()
    {
        return cube[1].length;
    }

    public int area()
    {
        return rows() * cols();
    }

    public int volume()
    {
        return area() * dims();
    }
}
