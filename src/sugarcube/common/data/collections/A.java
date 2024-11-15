package sugarcube.common.data.collections;

import sugarcube.common.data.Zen;
import sugarcube.common.interfaces.Computable;
import sugarcube.common.numerics.MoreMath;

import java.util.*;

public class A
{

    public static int Search(boolean sorted, int value, int... a)
    {
        if (sorted)
        {
            return Arrays.binarySearch(a, value);
        } else
        {
            for (int i = 0; i < a.length; i++)
                if (a[i] == value)
                    return i;
            return -1;
        }
    }

    public static int[] Col(int index, int[][] data)
    {
        int[] col = new int[data.length];
        for (int i = 0; i < col.length; i++)
            col[i] = data[i][index];
        return col;
    }

    public static byte min(byte... data)
    {

        if (data == null || data.length == 0)
            return 0;
        byte min = data[0];
        for (int i = 1; i < data.length; i++)
            if (data[i] < min)
                min = data[i];
        return min;
    }

    public static int min(int... data)
    {
        if (data == null || data.length == 0)
            return 0;
        int min = data[0];
        for (int i = 1; i < data.length; i++)
            if (data[i] < min)
                min = data[i];
        return min;
    }

    public static float min(float... data)
    {
        if (data == null || data.length == 0)
            return 0;
        float min = data[0];
        for (int i = 1; i < data.length; i++)
            if (data[i] < min)
                min = data[i];
        return min;
    }

    public static double min(double... data)
    {
        if (data == null || data.length == 0)
            return 0;
        double min = data[0];
        for (int i = 1; i < data.length; i++)
            if (data[i] < min)
                min = data[i];
        return min;
    }

    public static byte Max(byte... data)
    {
        if (data == null || data.length == 0)
            return 0;
        byte max = data[0];
        for (int i = 1; i < data.length; i++)
            if (data[i] > max)
                max = data[i];
        return max;
    }

    public static int Max(int... data)
    {
        if (data == null || data.length == 0)
            return 0;
        int max = data[0];
        for (int i = 1; i < data.length; i++)
            if (data[i] > max)
                max = data[i];
        return max;
    }

    public static float Max(float... data)
    {
        if (data == null || data.length == 0)
            return 0;
        float max = data[0];
        for (int i = 1; i < data.length; i++)
            if (data[i] > max)
                max = data[i];
        return max;
    }

    public static double Max(double... data)
    {
        if (data == null || data.length == 0)
            return 0;
        double max = data[0];
        for (int i = 1; i < data.length; i++)
            if (data[i] > max)
                max = data[i];
        return max;
    }

    public static int MaxIndex(int... data)
    {
        if (data == null || data.length == 0)
            return -1;
        int max = data[0];
        int index = 0;
        for (int i = 1; i < data.length; i++)
            if (data[i] > max)
                max = data[index = i];
        return index;
    }

    public static int MaxIndex(float... data)
    {
        if (data == null || data.length == 0)
            return -1;
        float max = data[0];
        int index = 0;
        for (int i = 1; i < data.length; i++)
            if (data[i] > max)
                max = data[index = i];
        return index;
    }

    public static int MaxIndex(double... data)
    {
        if (data == null || data.length == 0)
            return -1;
        double max = data[0];
        int index = 0;
        for (int i = 1; i < data.length; i++)
            if (data[i] > max)
                max = data[index = i];
        return index;
    }

    public static int MinIndex(int... data)
    {
        if (data == null || data.length == 0)
            return -1;
        int min = data[0];
        int index = 0;
        for (int i = 1; i < data.length; i++)
            if (data[i] < min)
                min = data[index = i];
        return index;
    }

    public static int MinIndex(float... data)
    {
        if (data == null || data.length == 0)
            return -1;
        float min = data[0];
        int index = 0;
        for (int i = 1; i < data.length; i++)
            if (data[i] < min)
                min = data[index = i];
        return index;
    }

    public static int MinIndex(double... data)
    {
        if (data == null || data.length == 0)
            return -1;
        double min = data[0];
        int index = 0;
        for (int i = 1; i < data.length; i++)
            if (data[i] < min)
                min = data[index = i];
        return index;
    }


    public static boolean containsAll(Object[] container, Object[] items)
    {
        ObjectSet set = new ObjectSet(container);
        for (Object o : items)
            if (!set.contains(o))
                return false;
        return true;
    }

    public static float sum(float... data)
    {
        float sum = 0;
        for (int i = 0; i < data.length; i++)
            sum += data[i];
        return sum;
    }

    public static float module2(float... data)
    {
        float module = 0;
        for (int i = 0; i < data.length; i++)
            module += data[i] * data[i];
        return module;
    }

    public static float module(float... data)
    {
        return (float) java.lang.Math.sqrt(module2(data));
    }

    public static float Mean(float... data)
    {
        float mean = 0;
        for (int i = 0; i < data.length; i++)
            mean += data[i];
        return data.length > 0 ? mean / data.length : 0;
    }

    public static double Mean(double[] data)
    {
        double mean = 0;
        for (int i = 0; i < data.length; i++)
            mean += data[i];
        return data.length > 0 ? mean / data.length : 0;
    }

    public static float Mean(int[] data)
    {
        float mean = 0;
        for (int i = 0; i < data.length; i++)
            mean += data[i];
        return data.length > 0 ? mean / (float) data.length : 0;
    }

    public static double norm(int... data)
    {
        return java.lang.Math.sqrt(normSquare(data));
    }

    public static double norm(double... data)
    {
        return java.lang.Math.sqrt(normSquare(data));
    }

    public static int normSquare(int... data)
    {
        int sum = 0;
        for (int value : data)
            sum += value * value;
        return sum;
    }

    public static double normSquare(double... data)
    {
        double sum = 0;
        for (double value : data)
            sum += value * value;
        return sum;
    }

    public static int[][] instance(int[][] data)
    {
        return new int[data.length][data[0].length];
    }

    public static int[] instance(int length, int value)
    {
        int[] array = new int[length];
        for (int i = 0; i < array.length; i++)
            array[i] = value;
        return array;
    }

    public static float[] instance(int length, float value)
    {
        float[] array = new float[length];
        for (int i = 0; i < array.length; i++)
            array[i] = value;
        return array;
    }

    public static double[] instance(int length, double value)
    {
        double[] array = new double[length];
        for (int i = 0; i < array.length; i++)
            array[i] = value;
        return array;
    }

    public static int[][] instance(int rows, int cols, int value)
    {
        int[][] values = new int[rows][cols];
        for (int y = 0; y < values.length; y++)
            for (int x = 0; x < values[0].length; x++)
                values[y][x] = value;
        return values;
    }

    public static double[][] instance(int rows, int cols, double value)
    {
        double[][] values = new double[rows][cols];
        for (int y = 0; y < values.length; y++)
            for (int x = 0; x < values[0].length; x++)
                values[y][x] = value;
        return values;
    }

    public static Set<String> toSet(String[] data)
    {
        Set<String> set = new HashSet<String>();
        if (data != null)
            for (String s : data)
                set.add(s);
        return set;
    }

    public static Set<Double> toSet(double[] data)
    {
        Set<Double> set = new HashSet<Double>();
        if (data != null)
            for (double s : data)
                set.add(s);
        return set;
    }

    public static Set<Integer> toSet(int[] data)
    {
        Set<Integer> set = new HashSet<Integer>();
        if (data != null)
            for (int s : data)
                set.add(s);
        return set;
    }

    public static double[] roundIt(double[] data, int decimals)
    {
        double factor = java.lang.Math.pow(10, decimals);
        for (int i = 0; i < data.length; i++)
            data[i] = MoreMath.round(data[i] / factor) * factor;
        return data;
    }

    public static double[] round(double[] data, int decimals)
    {
        return roundIt(copy(data), decimals);
    }

    public static boolean isZero(int... data)
    {
        if (data != null)
            for (int d : data)
                if (d != 0)
                    return false;
        return true;
    }

    public static boolean isEpsilonZero(float epsilon, float... data)
    {
        if (data != null)
            for (float d : data)
                if (MoreMath.abs(d) > epsilon)
                    return false;
        return true;
    }

    public static boolean isEpsilonZero(double epsilon, double... data)
    {
        if (data != null)
            for (double d : data)
                if (MoreMath.abs(d) > epsilon)
                    return false;
        return true;
    }

    public static boolean isZero(float... data)
    {
        if (data != null)
            for (float d : data)
                if (d != 0f)
                    return false;
        return true;
    }

    public static boolean isZero(double... data)
    {
        if (data != null)
            for (double d : data)
                if (d != 0.0)
                    return false;
        return true;
    }

    public static byte[] bytes(byte... data)
    {
        return data;
    }

    public static int[] Ints(int... data)
    {
        return data;
    }

    public static float[] Floats(float... data)
    {
        return data;
    }

    public static float[] ToFloats(double... data)
    {
        float[] f = new float[data.length];
        for (int i = 0; i < f.length; i++)
            f[i] = (float) data[i];
        return f;
    }

    public static double[] doubles(double... data)
    {
        return data;
    }

    public static String[] strings(String... data)
    {
        return data;
    }

    public static Object[] objects(Object... data)
    {
        return data;
    }

    public static int[][] Transpose(int[][] data)
    {
        if (data == null)
            return null;
        int[][] transposed = new int[data[0].length][data.length];
        for (int y = 0; y < data.length; y++)
            for (int x = 0; x < data[0].length; x++)
                transposed[x][y] = data[y][x];
        return transposed;
    }

    public static double[][] Transpose(double[][] data)
    {
        if (data == null)
            return null;
        double[][] transposed = new double[data[0].length][data.length];
        for (int y = 0; y < data.length; y++)
            for (int x = 0; x < data[0].length; x++)
                transposed[x][y] = data[y][x];
        return transposed;
    }

    public static int[][] swapHorizontally(int[][] data)
    {
        if (data == null)
            return null;
        int[][] swapped = new int[data.length][data[0].length];
        for (int y = 0; y < data.length; y++)
            for (int x = 0; x < data[0].length; x++)
                swapped[y][data[0].length - x - 1] = data[y][x];
        return swapped;
    }

    public static double[][] swapHorizontally(double[][] data)
    {
        if (data == null)
            return null;
        double[][] swapped = new double[data.length][data[0].length];
        for (int y = 0; y < data.length; y++)
            for (int x = 0; x < data[0].length; x++)
                swapped[y][data[0].length - x - 1] = data[y][x];
        return swapped;
    }

    public static int[][] swapVertically(int[][] data)
    {
        if (data == null)
            return null;
        int[][] swapped = new int[data.length][data[0].length];
        for (int y = 0; y < data.length; y++)
            for (int x = 0; x < data[0].length; x++)
                swapped[data.length - y - 1][x] = data[y][x];
        return swapped;
    }

    public static double[][] swapVertically(double[][] data)
    {
        if (data == null)
            return null;
        double[][] swapped = new double[data.length][data[0].length];
        for (int y = 0; y < data.length; y++)
            for (int x = 0; x < data[0].length; x++)
                swapped[data.length - y - 1][x] = data[y][x];
        return swapped;
    }

    public static int[][] swap(int[][] data)
    {
        if (data == null)
            return null;
        int[][] swapped = new int[data.length][data[0].length];
        for (int y = 0; y < data.length; y++)
            for (int x = 0; x < data[0].length; x++)
                swapped[data.length - y - 1][data[0].length - x - 1] = data[y][x];
        return swapped;
    }

    public static float[][] swap(float[][] data)
    {
        if (data == null)
            return null;
        float[][] swapped = new float[data.length][data[0].length];
        for (int y = 0; y < data.length; y++)
            for (int x = 0; x < data[0].length; x++)
                swapped[data.length - y - 1][data[0].length - x - 1] = data[y][x];
        return swapped;
    }

    public static double[][] swap(double[][] data)
    {
        if (data == null)
            return null;
        double[][] swapped = new double[data.length][data[0].length];
        for (int y = 0; y < data.length; y++)
            for (int x = 0; x < data[0].length; x++)
                swapped[data.length - y - 1][data[0].length - x - 1] = data[y][x];
        return swapped;
    }

    public static int[] swap(int[] data)
    {
        if (data == null)
            return null;
        int[] swapped = new int[data.length];
        for (int i = 0; i < data.length; i++)
            swapped[data.length - i - 1] = data[i];
        return swapped;
    }

    public static float[] swap(float[] data)
    {
        if (data == null)
            return null;
        float[] swapped = new float[data.length];
        for (int i = 0; i < data.length; i++)
            swapped[data.length - i - 1] = data[i];
        return swapped;
    }

    public static double[] swap(double[] data)
    {
        if (data == null)
            return null;
        double[] swapped = new double[data.length];
        for (int i = 0; i < data.length; i++)
            swapped[data.length - i - 1] = data[i];
        return swapped;
    }

    public static String[] swap(String[] data)
    {
        if (data == null)
            return null;
        String[] swapped = new String[data.length];
        for (int i = 0; i < data.length; i++)
            swapped[data.length - i - 1] = data[i];
        return swapped;
    }

    public static float[] expand(float[] data, int newSize)
    {
        return expand(data, data.length > 0 ? data[data.length - 1] : 0f, newSize);
    }

    public static float[] expand(float[] data, float value, int newSize)
    {
        if (data.length == newSize)
            return data;
        float[] expanded = new float[newSize];
        System.arraycopy(data, 0, expanded, 0, data.length < newSize ? data.length : newSize);
        for (int i = data.length; i < newSize; i++)
            expanded[i] = value;
        return expanded;
    }

    public static int[] insert(int[] data, int index, int value)
    {
        int[] inserted = new int[data.length + 1];
        for (int i = 0; i < inserted.length; i++)
            inserted[i] = i < index ? data[i] : i > index ? data[i - 1] : value;
        return inserted;
    }

    public static float[] insert(float[] data, int index, float value)
    {
        float[] inserted = new float[data.length + 1];
        for (int i = 0; i < inserted.length; i++)
            inserted[i] = i < index ? data[i] : i > index ? data[i - 1] : value;
        return inserted;
    }

    public static double[] insert(double[] data, int index, double value)
    {
        double[] inserted = new double[data.length + 1];
        for (int i = 0; i < inserted.length; i++)
            inserted[i] = i < index ? data[i] : i > index ? data[i - 1] : value;
        return inserted;
    }

    public static byte[] concat(byte[]... data)
    {
        int size = 0;
        for (int i = 0; i < data.length; i++)
            size += data[i].length;
        byte[] concat = new byte[size];
        int position = 0;
        for (int i = 0; i < data.length; i++)
        {
            System.arraycopy(data[i], 0, concat, position, data[i].length);
            position += data[i].length;
        }
        return concat;
    }

    public static int[] concat(int[]... data)
    {
        int size = 0;
        for (int i = 0; i < data.length; i++)
            size += data[i].length;
        int[] concat = new int[size];
        int position = 0;
        for (int i = 0; i < data.length; i++)
        {
            System.arraycopy(data[i], 0, concat, position, data[i].length);
            position += data[i].length;
        }
        return concat;
    }

    public static float[] concat(float[]... data)
    {
        int size = 0;
        for (int i = 0; i < data.length; i++)
            size += data[i].length;
        float[] concat = new float[size];
        int position = 0;
        for (int i = 0; i < data.length; i++)
        {
            System.arraycopy(data[i], 0, concat, position, data[i].length);
            position += data[i].length;
        }
        return concat;
    }

    public static double[] concat(double[]... data)
    {
        int size = 0;
        for (int i = 0; i < data.length; i++)
            size += data[i].length;
        double[] concat = new double[size];
        int position = 0;
        for (int i = 0; i < data.length; i++)
        {
            System.arraycopy(data[i], 0, concat, position, data[i].length);
            position += data[i].length;
        }
        return concat;
    }

    public static String[] concat(String[]... data)
    {
        int size = 0;
        for (int i = 0; i < data.length; i++)
            size += data[i].length;
        String[] concat = new String[size];
        int position = 0;
        for (int i = 0; i < data.length; i++)
        {
            System.arraycopy(data[i], 0, concat, position, data[i].length);
            position += data[i].length;
        }
        return concat;
    }

    public static String[] append(String[] data, String add)
    {
        if (data == null || data.length == 0)
            return add == null ? new String[0] : strings(add);
        if (add == null)
            return data;
        String[] concat = new String[data.length + 1];
        System.arraycopy(data, 0, concat, 0, data.length);
        concat[concat.length - 1] = add;
        return concat;
    }

    public static boolean contains(String[] data, String value)
    {
        for (String d : data)
            if (d.equals(value))
                return true;
        return false;
    }

    public static byte[] trim(byte[] data, int begin, int end)
    {
        if (data == null)
            return new byte[0];
        if (begin == 0 && end == data.length)
            return data;
        if (begin < 0)
            begin = 0;
        if (end > data.length || end < 0)
            end = data.length;
        byte[] trimmed = new byte[end - begin];
        System.arraycopy(data, begin, trimmed, 0, trimmed.length);
        return trimmed;
    }

    public static int[] trim(int[] data, int begin, int end)
    {
        if (begin == 0 && end == data.length)
            return data;
        if (begin < 0)
            begin = 0;
        if (end > data.length || end < 0)
            end = data.length;
        int[] trimmed = new int[end - begin];
        System.arraycopy(data, begin, trimmed, 0, trimmed.length);
        return trimmed;
    }

    public static float[] trim(float[] data, int begin, int end)
    {
        if (data == null)
            return new float[0];
        if (begin == 0 && end == data.length)
            return data;
        if (begin < 0)
            begin = 0;
        if (end > data.length || end < 0)
            end = data.length;
        float[] trimmed = new float[end - begin];
        System.arraycopy(data, begin, trimmed, 0, trimmed.length);
        return trimmed;
    }

    public static double[] trim(double[] data, int begin, int end)
    {
        if (begin == 0 && end == data.length)
            return data;
        if (begin < 0)
            begin = 0;
        if (end > data.length || end < 0)
            end = data.length;
        double[] trimmed = new double[end - begin];
        System.arraycopy(data, begin, trimmed, 0, trimmed.length);
        return trimmed;
    }

    public static char[] trim(char[] data, int begin, int end)
    {
        if (begin == 0 && end == data.length)
            return data;
        if (begin < 0)
            begin = 0;
        if (end > data.length || end < 0)
            end = data.length;
        char[] trimmed = new char[end - begin];
        System.arraycopy(data, begin, trimmed, 0, trimmed.length);
        return trimmed;
    }

    public static String[] trim(String[] data, int begin, int end)
    {
        if (begin == 0 && end == data.length)
            return data;
        if (begin < 0)
            begin = 0;
        if (end > data.length || end < 0)
            end = data.length;
        String[] trimmed = new String[end - begin];
        System.arraycopy(data, begin, trimmed, 0, trimmed.length);
        return trimmed;
    }

    public static byte[] trim(byte[] data, int end)
    {
        return trim(data, 0, end);
    }

    public static int[] trim(int[] data, int end)
    {
        return trim(data, 0, end);
    }

    public static float[] trim(float[] data, int end)
    {
        return trim(data, 0, end);
    }

    public static double[] Trim(double[] data, int end)
    {
        return trim(data, 0, end);
    }

    public static String[] trim(String[] data, int end)
    {
        return trim(data, 0, end);
    }

    public static double[][] trimRows(double[][] data, int begin, int end)
    {
        double[][] tmp = new double[end - begin][data.length];
        for (int y = begin; y < end; y++)
            for (int x = 0; x < data[0].length; x++)
                tmp[y - begin][x] = data[y][x];
        return tmp;
    }

    public static int[][] trimRows(int[][] data, int begin, int end)
    {
        int[][] tmp = new int[end - begin][data.length];
        for (int y = begin; y < end; y++)
            for (int x = 0; x < data[0].length; x++)
                tmp[y - begin][x] = data[y][x];
        return tmp;
    }

    public static double[][] trimCols(double[][] data, int begin, int end)
    {
        double[][] tmp = new double[data[0].length][end - begin];
        for (int y = 0; y < data.length; y++)
            for (int x = begin; x < end; x++)
                tmp[y][x - begin] = data[y][x];
        return tmp;
    }

    public static int[][] trimCols(int[][] data, int begin, int end)
    {
        int[][] tmp = new int[data[0].length][end - begin];
        for (int y = 0; y < data.length; y++)
            for (int x = begin; x < end; x++)
                tmp[y][x - begin] = data[y][x];
        return tmp;
    }

    public static int[][] crop(int[][] data, int beginY, int endY, int beginX, int endX)
    {
        int[][] trimmed = new int[endY - beginY][endX - beginX];
        for (int y = beginY; y < endY; y++)
            for (int x = beginX; x < endX; x++)
                trimmed[y - beginY][x - beginX] = data[y][x];
        return trimmed;
    }

    public static double[][] crop(double[][] data, int beginY, int endY, int beginX, int endX)
    {
        double[][] trimmed = new double[endY - beginY][endX - beginX];
        for (int y = beginY; y < endY; y++)
            for (int x = beginX; x < endX; x++)
                trimmed[y - beginY][x - beginX] = data[y][x];
        return trimmed;
    }

    public static double[][] subtractRows(double[][] data, double[] row)
    {
        double[][] result = new double[data.length][data[0].length];
        for (int y = 0; y < data.length; y++)
            for (int x = 0; x < data[0].length; x++)
                result[y][x] = data[y][x] - row[x];
        return result;
    }

    public static double[][] subtractCols(double[][] data, double[] col)
    {
        double[][] result = new double[data.length][data[0].length];
        for (int y = 0; y < data.length; y++)
            for (int x = 0; x < data[0].length; x++)
                result[y][x] = data[y][x] - col[y];
        return result;
    }

    public static double[][] addRows(double[][] data, double[] row)
    {
        double[][] result = new double[data.length][data[0].length];
        for (int y = 0; y < data.length; y++)
            for (int x = 0; x < data[0].length; x++)
                result[y][x] = data[y][x] + row[x];
        return result;
    }

    public static float[] Col(float[][] data, int colIndex)
    {
        float[] col = new float[data.length];
        for (int rowIndex = 0; rowIndex < col.length; rowIndex++)
            col[rowIndex] = data[rowIndex][colIndex];
        return col;
    }

    public static double[][] addCols(double[][] data, double[] col)
    {
        double[][] result = new double[data.length][data[0].length];
        for (int y = 0; y < data.length; y++)
            for (int x = 0; x < data[0].length; x++)
                result[y][x] = data[y][x] + col[y];
        return result;
    }

    public static double[][] add(double[][] data, double value)
    {
        double[][] result = new double[data.length][data[0].length];
        for (int y = 0; y < data.length; y++)
            for (int x = 0; x < data[0].length; x++)
                result[y][x] = data[y][x] + value;
        return result;
    }

    public static double[][] AddSelf(double[][] data, double value)
    {
        for (int y = 0; y < data.length; y++)
            for (int x = 0; x < data[0].length; x++)
                data[y][x] += value;
        return data;
    }

    public static int[] Range(int[] data, int min, int max)
    {
        int[] ranged = new int[data.length];
        for (int i = 0; i < data.length; i++)
            if (data[i] < min)
                ranged[i] = min;
            else if (data[i] > max)
                ranged[i] = max;
            else
                ranged[i] = data[i];
        return ranged;
    }

    public static int[] rangeIt(int[] data, int min, int max)
    {
        for (int i = 0; i < data.length; i++)
            if (data[i] < min)
                data[i] = min;
            else if (data[i] > max)
                data[i] = max;
        return data;
    }

    public static double[] Range(double[] data, double min, double max)
    {
        double[] ranged = new double[data.length];
        for (int i = 0; i < data.length; i++)
            if (data[i] < min)
                ranged[i] = min;
            else if (data[i] > max)
                ranged[i] = max;
            else
                ranged[i] = data[i];
        return ranged;
    }

    public static double[] rangeIt(double[] data, double min, double max)
    {
        for (int i = 0; i < data.length; i++)
            if (data[i] < min)
                data[i] = min;
            else if (data[i] > max)
                data[i] = max;
        return data;
    }

    public static int[][] Range(int[][] data, int min, int max)
    {
        int[][] ranged = new int[data.length][data[0].length];
        for (int y = 0; y < data.length; y++)
            for (int x = 0; x < data[0].length; x++)
                if (data[y][x] < min)
                    ranged[y][x] = min;
                else if (data[y][x] > max)
                    ranged[y][x] = max;
                else
                    ranged[y][x] = data[y][x];
        return ranged;
    }

    public static int[][] rangeIt(int[][] data, int min, int max)
    {
        for (int y = 0; y < data.length; y++)
            for (int x = 0; x < data[0].length; x++)
                if (data[y][x] < min)
                    data[y][x] = min;
                else if (data[y][x] > max)
                    data[y][x] = max;
        return data;
    }

    public static double[] add(double[] a, double... b)
    {
        double[] result = new double[a.length];
        if (b.length == 1)
            for (int i = 0; i < a.length; i++)
                result[i] = a[i] + b[0];
        else
            for (int i = 0; i < a.length; i++)
                result[i] = a[i] + b[i];
        return result;
    }

    public static double[] AddSelf(double[] a, double... b)
    {
        if (b.length == 1)
            for (int i = 0; i < a.length; i++)
                a[i] += b[0];
        else
            for (int i = 0; i < a.length; i++)
                a[i] += b[i];
        return a;
    }

    public static float[] add(float[] a, float... b)
    {
        float[] result = new float[a.length];
        if (b.length == 1)
            for (int i = 0; i < a.length; i++)
                result[i] = a[i] + b[0];
        else
            for (int i = 0; i < a.length; i++)
                result[i] = a[i] + b[i];
        return result;
    }

    public static float[] AddTo(float[] a, float... b)
    {
        if (b.length == 1)
            for (int i = 0; i < a.length; i++)
                a[i] += b[0];
        else
            for (int i = 0; i < a.length; i++)
                a[i] += b[i];
        return a;
    }

    public static float[] subtract(float[] a, float... b)
    {
        float[] result = new float[a.length];
        if (b.length == 1)
            for (int i = 0; i < a.length; i++)
                result[i] = a[i] - b[0];
        else
            for (int i = 0; i < a.length; i++)
                result[i] = a[i] - b[i];
        return result;
    }

    public static double[] subtract(double[] a, double... b)
    {
        double[] result = new double[a.length];
        if (b.length == 1)
            for (int i = 0; i < a.length; i++)
                result[i] = a[i] - b[0];
        else
            for (int i = 0; i < a.length; i++)
                result[i] = a[i] - b[i];
        return result;
    }

    public static double[] subtractIt(double[] a, double... b)
    {
        if (b.length == 1)
            for (int i = 0; i < a.length; i++)
                a[i] -= b[0];
        else
            for (int i = 0; i < a.length; i++)
                a[i] -= b[i];
        return a;
    }

    public static double[] Divide(double[] a, double... b)
    {
        double[] result = new double[a.length];
        if (b.length == 1)
            for (int i = 0; i < a.length; i++)
                result[i] = a[i] / b[0];
        else
            for (int i = 0; i < a.length; i++)
                result[i] = a[i] / b[i];
        return result;
    }

    public static double[] DivideSelf(double[] a, double... b)
    {
        if (b.length == 1)
            for (int i = 0; i < a.length; i++)
                a[i] /= b[0];
        else
            for (int i = 0; i < a.length; i++)
                a[i] /= b[i];
        return a;
    }

    public static double[] DivWithSum(double[] a)
    {
        return A.Divide(a, A.Sum(a));
    }

    public static float[] DivideSelf(float[] a, double... b)
    {
        if (b.length == 1)
            for (int i = 0; i < a.length; i++)
                a[i] /= b[0];
        else
            for (int i = 0; i < a.length; i++)
                a[i] /= b[i];
        return a;
    }


    public static float[] Divide(float[] a, float... b)
    {
        float[] result = new float[a.length];
        if (b.length == 1)
            for (int i = 0; i < a.length; i++)
                result[i] = a[i] / b[0];
        else
            for (int i = 0; i < a.length; i++)
                result[i] = a[i] / b[i];
        return result;
    }


    public static double[] Mult(boolean transpose, double[][] abc)
    {
        if (transpose)
        {
            double[] result = new double[abc.length];
            for (int i = 0; i < result.length; i++)
            {
                result[i] = abc[i][0];
                for (int j = 1; j < abc[i].length; j++)
                    result[i] *= abc[i][j];
            }
            return result;
        } else
            return Mult(abc);
    }

    public static double[] Mult(double[][] abc)
    {
        double[] result = new double[abc[0].length];
        for (int j = 0; j < result.length; j++)
        {
            result[j] = abc[0][j];
            for (int i = 1; i < abc.length; i++)
                result[j] *= abc[i][j];
        }
        return result;
    }

    public static double[] Mult(double[] a, double... b)
    {
        double[] result = new double[a.length];
        if (b.length == 1)
            for (int i = 0; i < a.length; i++)
                result[i] = a[i] * b[0];
        else
            for (int i = 0; i < a.length; i++)
                result[i] = a[i] * b[i];
        return result;
    }

    public static double[] MultSelf(double[] a, double... b)
    {
        if (b.length == 1)
            for (int i = 0; i < a.length; i++)
                a[i] *= b[0];
        else
            for (int i = 0; i < a.length; i++)
                a[i] *= b[i];
        return a;
    }

    public static float[] Mult(float[] a, float... b)
    {
        float[] result = new float[a.length];
        if (b.length == 1)
            for (int i = 0; i < a.length; i++)
                result[i] = a[i] * b[0];
        else
            for (int i = 0; i < a.length; i++)
                result[i] = a[i] * b[i];
        return result;
    }

    public static float[] MultSelf(float[] a, float... b)
    {
        if (b.length == 1)
            for (int i = 0; i < a.length; i++)
                a[i] *= b[0];
        else
            for (int i = 0; i < a.length; i++)
                a[i] *= b[i];
        return a;
    }

    public static int[] abs(int... data)
    {
        int[] result = new int[data.length];
        for (int i = 0; i < data.length; i++)
            result[i] = java.lang.Math.abs(data[i]);
        return result;
    }

    public static double[] abs(double... data)
    {
        double[] result = new double[data.length];
        for (int i = 0; i < data.length; i++)
            result[i] = java.lang.Math.abs(data[i]);
        return result;
    }

    public static int[][] abs(int[][] data)
    {
        int[][] result = new int[data.length][data[0].length];
        for (int y = 0; y < data.length; y++)
            for (int x = 0; x < data[0].length; x++)
                result[y][x] = java.lang.Math.abs(data[y][x]);
        return result;
    }

    public static double[][] abs(double[][] data)
    {
        double[][] result = new double[data.length][data[0].length];
        for (int y = 0; y < data.length; y++)
            for (int x = 0; x < data[0].length; x++)
                result[y][x] = java.lang.Math.abs(data[y][x]);
        return result;
    }

    public static boolean equals(String[] a, String[] b)
    {
        if (a == b)
            return true;
        else if (a == null || b == null || a.length != b.length)
            return false;
        else
            for (int i = 0; i < a.length; i++)
                if (!a[i].equals(b[i]))
                    return false;
        return true;
    }

    public static boolean equals(int[] a, int[] b)
    {
        if (a == b)
            return true;
        else if (a == null || b == null || a.length != b.length)
            return false;
        else
            for (int i = 0; i < a.length; i++)
                if (a[i] != b[i])
                    return false;
        return true;
    }

    public static boolean equals(double[] a, double[] b)
    {
        if (a == b)
            return true;
        else if (a == null || b == null || a.length != b.length)
            return false;
        else
            for (int i = 0; i < a.length; i++)
                if (a[i] != b[i])
                    return false;
        return true;
    }

    public static boolean equals(double[] a, double[] b, double precision)
    {
        if (a == b)
            return true;
        else if (a == null || b == null || a.length != b.length)
            return false;
        else
            for (int i = 0; i < a.length; i++)
                if (MoreMath.abs(a[i] - b[i]) > precision)
                    return false;
        return true;
    }

    public static boolean equals(float[] a, float[] b)
    {
        if (a == b)
            return true;
        else if (a == null || b == null || a.length != b.length)
            return false;
        else
            for (int i = 0; i < a.length; i++)
                if (a[i] != b[i])
                    return false;
        return true;
    }

    public static boolean equals(float[] a, float[] b, double precision)
    {
        if (a == b)
            return true;
        else if (a == null || b == null || a.length != b.length)
            return false;
        else
            for (int i = 0; i < a.length; i++)
                if (MoreMath.abs(a[i] - b[i]) > precision)
                    return false;
        return true;
    }

    public static String[] Copy(String... data)
    {
        if (data == null)
            return null;
        String[] copy = new String[data.length];
        System.arraycopy(data, 0, copy, 0, data.length);
        return copy;
    }

    public static byte[] copy(byte... data)
    {
        if (data == null)
            return null;
        byte[] copy = new byte[data.length];
        System.arraycopy(data, 0, copy, 0, data.length);
        return copy;
    }

    public static boolean[] copy(boolean[] data)
    {
        if (data == null)
            return null;
        boolean[] copy = new boolean[data.length];
        System.arraycopy(data, 0, copy, 0, data.length);
        return copy;
    }

    public static int[] copy(int[] data)
    {
        if (data == null)
            return null;
        int[] copy = new int[data.length];
        System.arraycopy(data, 0, copy, 0, data.length);
        return copy;
    }

    public static void CopyTo(int[] data, int[] copy)
    {
        if (data != null && copy != null)
            System.arraycopy(data, 0, copy, 0, data.length < copy.length ? data.length : copy.length);
    }

    public static float[] copy(float[] data)
    {
        if (data == null)
            return null;
        float[] copy = new float[data.length];
        System.arraycopy(data, 0, copy, 0, data.length);
        return copy;
    }

    public static float[] copy(float[] src, float[] dst)
    {
        if (src == null)
            return null;
        if (dst.length != src.length)
            dst = new float[src.length];
        System.arraycopy(src, 0, dst, 0, src.length);
        return dst;
    }

    public static int[] Copy(int[] src, int[] dst)
    {
        if (src == null)
            return null;
        if (dst.length != src.length)
            dst = new int[src.length];
        System.arraycopy(src, 0, dst, 0, src.length);
        return dst;
    }

    public static double[] copy(double... data)
    {
        if (data == null)
            return null;
        double[] copy = new double[data.length];
        System.arraycopy(data, 0, copy, 0, data.length);
        return copy;
    }

    public static byte[][] copy(byte[][] data)
    {
        if (data == null)
            return null;
        byte[][] copy = new byte[data.length][];
        for (int y = 0; y < copy.length; y++)
            copy[y] = copy(data[y]);
        return copy;
    }

    public static int[][] copy(int[][] data)
    {
        if (data == null)
            return null;
        int[][] copy = new int[data.length][];
        for (int y = 0; y < copy.length; y++)
            copy[y] = copy(data[y]);
        return copy;
    }

    public static float[][] copy(float[][] data)
    {
        if (data == null)
            return null;
        float[][] copy = new float[data.length][];
        for (int y = 0; y < copy.length; y++)
            copy[y] = copy(data[y]);
        return copy;
    }

    public static double[][] copy(double[][] data)
    {
        if (data == null)
            return null;
        double[][] copy = new double[data.length][];
        for (int y = 0; y < copy.length; y++)
            copy[y] = copy(data[y]);
        return copy;
    }

    public static String[] stringValues(Iterable<String> data)
    {
        if (data == null)
            return null;
        List<String> list = new LinkedList<String>();
        for (String d : data)
            list.add(d);
        return list.toArray(new String[0]);
    }

    public static int[] Set(int value, int size)
    {
        int[] data = new int[size];
        for (int i = 0; i < data.length; i++)
            data[i] = value;
        return data;
    }


    public static double[] Set(double value, int size)
    {
        double[] data = new double[size];
        for (int i = 0; i < data.length; i++)
            data[i] = value;
        return data;
    }

    public static double[] toDoubles(Iterable<Double> data)
    {
        if (data == null)
            return null;
        List<Double> list = new LinkedList<Double>();
        for (Double d : data)
            list.add(d);
        double[] out = new double[list.size()];
        int index = 0;
        for (Double d : list)
            out[index++] = d;
        return out;
    }

    public static double[] toDoubles(int... data)
    {
        if (data == null)
            return null;
        double[] out = new double[data.length];
        for (int x = 0; x < data.length; x++)
            out[x] = data[x];
        return out;
    }

    public static double[][] toDoubles(int[][] data)
    {
        if (data == null)
            return null;
        double[][] out = new double[data.length][data[0].length];
        for (int y = 0; y < data.length; y++)
            out[y] = toDoubles(data[y]);
        return out;
    }

    public static double[][][] toDoubles(int[][][] data)
    {
        if (data == null)
            return null;
        double[][][] out = new double[data.length][data[0].length][data[0][0].length];
        for (int z = 0; z < data.length; z++)
            out[z] = toDoubles(data[z]);
        return out;
    }

    public static double[][] toDoubles(float[][] data)
    {
        if (data == null)
            return null;
        double[][] out = new double[data.length][data[0].length];
        for (int y = 0; y < data.length; y++)
            out[y] = toDoubles(data[y]);
        return out;
    }

    public static double[][][] toDoubles(float[][][] data)
    {
        if (data == null)
            return null;
        double[][][] out = new double[data.length][data[0].length][data[0][0].length];
        for (int z = 0; z < data.length; z++)
            out[z] = toDoubles(data[z]);
        return out;
    }

    public static float[] toFloats(int[] data)
    {
        if (data == null)
            return null;
        float[] out = new float[data.length];
        for (int x = 0; x < data.length; x++)
            out[x] = data[x];
        return out;
    }

    public static float[] toFloats(long[] data)
    {
        if (data == null)
            return null;
        float[] out = new float[data.length];
        for (int x = 0; x < data.length; x++)
            out[x] = data[x];
        return out;
    }

    public static float[][] toFloats(int[][] data)
    {
        if (data == null)
            return null;
        float[][] out = new float[data.length][data[0].length];
        for (int y = 0; y < data.length; y++)
            out[y] = toFloats(data[y]);
        return out;
    }

    public static float[][][] toFloats(int[][][] data)
    {
        if (data == null)
            return null;
        float[][][] out = new float[data.length][data[0].length][data[0][0].length];
        for (int z = 0; z < data.length; z++)
            out[z] = toFloats(data[z]);
        return out;
    }

    public static float[][] toFloats(double[][] data)
    {
        if (data == null)
            return null;
        float[][] out = new float[data.length][data[0].length];
        for (int y = 0; y < data.length; y++)
            out[y] = toFloats(data[y]);
        return out;
    }

    public static float[][][] toFloats(double[][][] data)
    {
        if (data == null)
            return null;
        float[][][] out = new float[data.length][data[0].length][data[0][0].length];
        for (int z = 0; z < data.length; z++)
            out[z] = toFloats(data[z]);
        return out;
    }

    public static int[] Ints(float... data)
    {
        if (data == null)
            return null;
        int[] out = new int[data.length];
        for (int y = 0; y < data.length; y++)
            out[y] = (int) data[y];
        return out;
    }

    public static int[] Ints(double... data)
    {
        if (data == null)
            return null;
        int[] out = new int[data.length];
        for (int y = 0; y < data.length; y++)
            out[y] = (int) data[y];
        return out;
    }

    public static float[] toFloats(double... data)
    {
        if (data == null)
            return null;
        float[] out = new float[data.length];
        for (int y = 0; y < data.length; y++)
            out[y] = (float) data[y];
        return out;
    }

    public static double[] toDoubles(float... data)
    {
        if (data == null)
            return null;
        double[] out = new double[data.length];
        for (int y = 0; y < data.length; y++)
            out[y] = data[y];
        return out;
    }

    public static int[][] Ints(float[][] data)
    {
        if (data == null)
            return null;
        int[][] out = new int[data.length][data[0].length];
        for (int y = 0; y < data.length; y++)
            out[y] = Ints(data[y]);
        return out;
    }

    public static int[][][] Ints(float[][][] data)
    {
        if (data == null)
            return null;
        int[][][] out = new int[data.length][data[0].length][data[0][0].length];
        for (int z = 0; z < data.length; z++)
            out[z] = Ints(data[z]);
        return out;
    }

    public static int[][] Ints(double[][] data)
    {
        if (data == null)
            return null;
        int[][] out = new int[data.length][data[0].length];
        for (int y = 0; y < data.length; y++)
            out[y] = Ints(data[y]);
        return out;
    }

    public static int[][][] Ints(double[][][] data)
    {
        if (data == null)
            return null;
        int[][][] out = new int[data.length][data[0].length][data[0][0].length];
        for (int z = 0; z < data.length; z++)
            out[z] = Ints(data[z]);
        return out;
    }

    public static float[] square(float[] data)
    {
        float[] result = new float[data.length];
        for (int i = 0; i < data.length; i++)
            result[i] = data[i] * data[i];
        return result;
    }

    public static double[] square(double[] data)
    {
        double[] result = new double[data.length];
        for (int i = 0; i < data.length; i++)
            result[i] = data[i] * data[i];
        return result;
    }

    public static double[] squareIt(double[] data)
    {
        for (int i = 0; i < data.length; i++)
            data[i] *= data[i];
        return data;
    }

    public static double[] sqrt(double[] data)
    {
        double[] result = new double[data.length];
        for (int i = 0; i < data.length; i++)
            result[i] = java.lang.Math.sqrt(data[i]);
        return result;
    }

    public static double[] sqrtIt(double[] data)
    {
        for (int i = 0; i < data.length; i++)
            data[i] = java.lang.Math.sqrt(data[i]);
        return data;
    }

    public static double[] symmetrize(double... data)
    {
        double[] result = new double[data.length];
        for (int i = 0; i < data.length; i++)
            result[i] = -data[i];
        return result;
    }

    public static double[] symmetrizeIt(double... data)
    {
        for (int i = 0; i < data.length; i++)
            data[i] = -data[i];
        return data;
    }

    public static int[] symmetrize(int... data)
    {
        int[] result = new int[data.length];
        for (int i = 0; i < data.length; i++)
            result[i] = -data[i];
        return result;
    }

    public static int[] symmetrizeIt(int... data)
    {
        for (int i = 0; i < data.length; i++)
            data[i] = -data[i];
        return data;
    }

    public static int[] add(int[] data1, int... data2)
    {
        int[] result = new int[data1.length];
        if (data2.length == 1)
            for (int i = 0; i < data1.length; i++)
                result[i] = data1[i] + data2[0];
        else
            for (int i = 0; i < data1.length; i++)
                result[i] = data1[i] + data2[i];
        return result;
    }

    public static int[] AddSelf(int[] data1, int... data2)
    {
        if (data2.length == 1)
            for (int i = 0; i < data1.length; i++)
                data1[i] += data2[0];
        else
            for (int i = 0; i < data1.length; i++)
                data1[i] += data2[i];
        return data1;
    }

    public static int[] subtract(int[] data1, int... data2)
    {
        int[] result = new int[data1.length];
        if (data2.length == 1)
            for (int i = 0; i < data1.length; i++)
                result[i] = data1[i] - data2[0];
        else
            for (int i = 0; i < data1.length; i++)
                result[i] = data1[i] - data2[i];
        return result;
    }

    public static int[] subtractIt(int[] data1, int... data2)
    {
        if (data2.length == 1)
            for (int i = 0; i < data1.length; i++)
                data1[i] -= data2[0];
        else
            for (int i = 0; i < data1.length; i++)
                data1[i] -= data2[i];
        return data1;
    }

    public static int[] Divide(int[] data1, int... data2)
    {
        int[] result = new int[data1.length];
        if (data2.length == 1)
            for (int i = 0; i < data1.length; i++)
                result[i] = data1[i] / data2[0];
        else
            for (int i = 0; i < data1.length; i++)
                result[i] = data1[i] / data2[i];
        return result;
    }

    public static int[] DivideSelf(int[] data1, int... data2)
    {
        if (data2.length == 1)
            for (int i = 0; i < data1.length; i++)
                data1[i] /= data2[0];
        else
            for (int i = 0; i < data1.length; i++)
                data1[i] /= data2[i];
        return data1;
    }

    public static double[] Mult(int[] a, double[] b)
    {
        double[] result = new double[a.length];
        if (b.length == 1)
            for (int i = 0; i < a.length; i++)
                result[i] = a[i] * b[0];
        else
            for (int i = 0; i < a.length; i++)
                result[i] = a[i] * b[i];
        return result;
    }

    public static int[] Mult(int[] a, int... b)
    {
        int[] result = new int[a.length];
        if (b.length == 1)
            for (int i = 0; i < a.length; i++)
                result[i] = a[i] * b[0];
        else
            for (int i = 0; i < a.length; i++)
                result[i] = a[i] * b[i];
        return result;
    }

    public static int[] MultSelf(int[] data1, int... data2)
    {
        if (data2.length == 1)
            for (int i = 0; i < data1.length; i++)
                data1[i] *= data2[0];
        else
            for (int i = 0; i < data1.length; i++)
                data1[i] *= data2[i];
        return data1;
    }

    public static int[][] Mult(int[][] data, double value)
    {
        int[][] result = new int[data.length][data[0].length];
        for (int y = 0; y < data.length; y++)
            for (int x = 0; x < data[0].length; x++)
                result[y][x] = MoreMath.round(data[y][x] * value);
        return result;
    }

    public static int[][] MultSelf(int[][] data, double value)
    {
        for (int y = 0; y < data.length; y++)
            for (int x = 0; x < data[0].length; x++)
                data[y][x] = MoreMath.round(data[y][x] * value);
        return data;
    }

    public static double[][] Mult(double[][] data, double value)
    {
        double[][] result = new double[data.length][data[0].length];
        for (int y = 0; y < data.length; y++)
            for (int x = 0; x < data[0].length; x++)
                result[y][x] = data[y][x] * value;
        return result;
    }

    public static double[][] MultSelf(double[][] data, double value)
    {
        for (int y = 0; y < data.length; y++)
            for (int x = 0; x < data[0].length; x++)
                data[y][x] *= value;
        return data;
    }

    public static double[][] Divide(double[][] data, double value)
    {
        double[][] result = new double[data.length][data[0].length];
        for (int y = 0; y < data.length; y++)
            for (int x = 0; x < data[0].length; x++)
                result[y][x] = data[y][x] / value;
        return result;
    }

    public static double[][] DivideSelf(double[][] data, double value)
    {
        for (int y = 0; y < data.length; y++)
            for (int x = 0; x < data[0].length; x++)
                data[y][x] /= value;
        return data;
    }

    public static double[][] subtract(double[][] data, double value)
    {
        double[][] result = new double[data.length][data[0].length];
        for (int y = 0; y < data.length; y++)
            for (int x = 0; x < data[0].length; x++)
                result[y][x] = data[y][x] - value;
        return result;
    }

    public static double[][] subtractIt(double[][] data, double value)
    {
        for (int y = 0; y < data.length; y++)
            for (int x = 0; x < data[0].length; x++)
                data[y][x] -= value;
        return data;
    }

    /*
     * public static float[] toFloats(float... data) { if (data == null) return
     * null; float[] out = new float[data.length]; for (int y = 0; y <
     * data.length; y++) out[y] = (float) data[y]; return out; }
     */

    public static String String(double[] data)
    {
        if (data == null)
            return "null";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++)
            sb.append(Double.isNaN(data[i]) ? "NaN" : Zen.toString(data[i], 2)).append(i < data.length - 1 ? " " : "");
        return sb.toString();
    }

    public static String String(float[] data)
    {
        if (data == null)
            return "null";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++)
            sb.append(Float.isNaN(data[i]) ? "NaN" : Zen.toString(data[i], 2)).append(i < data.length - 1 ? " " : "");
        return sb.toString();
    }

    public static String String(float[][] data)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++)
        {
            for (int j = 0; j < data[i].length; j++)
                sb.append(Zen.toString(data[i][j], 2)).append(j < data[i].length - 1 ? " " : "");
            sb.append(i < data.length - 1 ? "\n" : "");
        }
        return sb.toString();
    }

    public static String String(double[][] data)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++)
        {
            for (int j = 0; j < data[i].length; j++)
                sb.append(Zen.toString(data[i][j], 2)).append(j < data[i].length - 1 ? " " : "");
            sb.append(i < data.length - 1 ? "\n" : "");
        }
        return sb.toString();
    }

    public static String String(int[] data)
    {
        StringBuilder sb = new StringBuilder();
        if (data != null)
            for (int i = 0; i < data.length; i++)
                sb.append(data[i]).append(i < data.length - 1 ? " " : "");
        return sb.toString();
    }

    public static String String(int[][] data)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++)
        {
            for (int j = 0; j < data[i].length; j++)
                sb.append(data[i][j]).append(j < data[i].length - 1 ? " " : "");
            sb.append(i < data.length - 1 ? "\n" : "");
        }
        return sb.toString();
    }

    public static String String(byte[] data)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++)
            sb.append(data[i]).append(i < data.length - 1 ? " " : "");
        return sb.toString();
    }

    public static String String(byte[] data, boolean unsigned)
    {
        if (!unsigned)
            return String(data);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++)
            sb.append(data[i] & 0xff).append(i < data.length - 1 ? " " : "");
        return sb.toString();
    }

    public static String[] toStrings(Object[] data)
    {
        String[] array = new String[data.length];
        for (int i = 0; i < data.length; i++)
            array[i] = data[i] == null ? "null" : data[i].toString();
        return array;
    }

    public static String String(Object[] data)
    {
        return String(" ", data);
    }

    public static String String(String separator, Object[] data)
    {
        StringBuilder sb = new StringBuilder();
        if (data == null)
            return "null";
        for (int i = 0; i < data.length; i++)
            sb.append(data[i] == null ? "null" : data[i].toString()).append(i < data.length - 1 ? separator : "");
        return sb.toString();
    }

    public static boolean isNaN(float[] data)
    {
        if (data == null || data.length == 0)
            return false;
        for (float d : data)
            if (!Float.isNaN(d))
                return false;
        return true;
    }

    public static boolean isNaN(double... data)
    {
        if (data == null || data.length == 0)
            return false;
        for (double d : data)
            if (!Double.isNaN(d))
                return false;
        return true;
    }

    public static float InRange(int index, float... values)
    {
        return values.length == 0 ? Float.NaN : values[index < 0 ? 0 : (index < values.length ? index : values.length - 1)];
    }

    public static int Range(int value, int... fromTo)
    {
        return value < fromTo[0] ? fromTo[0] : (value > fromTo[fromTo.length - 1] ? fromTo[fromTo.length - 1] : value);
    }

    public static double Range(double value, double... fromTo)
    {
        return value < fromTo[0] ? fromTo[0] : (value > fromTo[fromTo.length - 1] ? fromTo[fromTo.length - 1] : value);
    }

    public static double[] ReallocIf(double[] data, boolean condition)
    {
        return condition ? Realloc(data, 2.0) : data;
    }

    public static double[] ReallocDouble(double[] data)
    {
        return Realloc(data, 2.0);
    }

    public static double[] Realloc(double[] data, double sizeFactor)
    {
        if (data == null || data.length == 0)
            return new double[1];
        if (sizeFactor <= 1)
            return data;
        double[] alloc = new double[MoreMath.round(data.length * sizeFactor)];
        System.arraycopy(data, 0, alloc, 0, data.length);
        return alloc;
    }

    public static double[] Compute(double[] data, Computable fct)
    {
        double[] computed = new double[data.length];
        for (int i = 0; i < data.length; i++)
            computed[i] = fct.compute(data[i]);
        return computed;
    }

    public static double Sum(double... data)
    {
        double sum = 0.0;
        for (int i = 0; i < data.length; i++)
            sum += data[i];
        return sum;
    }

    public static int Sum(int... data)
    {
        int sum = 0;
        for (int i = 0; i < data.length; i++)
            sum += data[i];
        return sum;
    }

    public static double[] Range(double start, double stop)
    {
        return Arange(start, stop, 1);
    }

    public static double[] Arange(double start, double stop, double step)
    {
        int i = 0;
        double v = start;
        while (stop > v)
        {
            i++;
            v += step;
        }
        double[] a = new double[i];
        i = 0;
        v = start;
        while (stop > v)
        {
            a[i++] = v;
            v += step;

        }
        return a;
    }

    public static int[] Range(int start, int stop)
    {
        return Range(start, stop, 1);
    }

    public static int[] Range(int start, int stop, int step)
    {
        int i = 0;
        int v = start;
        while (stop > v)
        {
            i++;
            v += step;
        }
        int[] a = new int[i];
        i = 0;
        v = start;
        while (stop > v)
        {
            a[i++] = v;
            v += step;

        }
        return a;
    }

    public static int[][] Combine(int[] a, int[] b)
    {
        int[][] m = new int[a.length * b.length][2];
        int k = 0;
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < b.length; j++)
            {
                m[k][0] = a[i];
                m[k][1] = b[j];
                k++;
            }
        return m;
    }

    public static double[][] Combine(double[] a, int[] b)
    {
        double[][] m = new double[a.length * b.length][2];
        int k = 0;
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < b.length; j++)
            {
                m[k][0] = a[i];
                m[k][1] = b[j];
                k++;
            }
        return m;
    }

    public static double[][] Combine(int[] a, double[] b)
    {
        double[][] m = new double[a.length * b.length][2];
        int k = 0;
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < b.length; j++)
            {
                m[k][0] = a[i];
                m[k][1] = b[j];
                k++;
            }
        return m;
    }

    public static double[][] Combine(double[] a, double[] b)
    {
        double[][] m = new double[a.length * b.length][2];
        int k = 0;
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < b.length; j++)
            {
                m[k][0] = a[i];
                m[k][1] = b[j];
                k++;
            }
        return m;
    }

    public static boolean IsSize(int[] data, int size)
    {
        return data != null && data.length == size;
    }

    public static void Shuffle(Object[] data)
    {
        Random rand = new Random();
        for (int i = 0; i < data.length; i++)
        {
            int j = rand.nextInt(data.length);
            Object tmp = data[j];
            data[j] = data[i];
            data[i] = tmp;
        }
    }

    public static void Reverse(Object[] data)
    {
        for (int i = 0; i < data.length / 2; i++)
        {
            Object tmp = data[i];
            data[i] = data[data.length - 1 - i];
            data[data.length - 1 - i] = tmp;
        }
    }

    public static void Reverse(int[] data)
    {
        for (int i = 0; i < data.length / 2; i++)
        {
            int tmp = data[i];
            data[i] = data[data.length - 1 - i];
            data[data.length - 1 - i] = tmp;
        }
    }

    public static <T> T[] Swap(T[] data, int i, int j)
    {
        T tmp = data[i];
        data[i] = data[j];
        data[j] = tmp;
        return data;
    }

    public static <T> T[] Generate(T[] data, Generable<T> generable)
    {
        for (int i = 0; i < data.length; i++)
            data[i] = generable.generate(data, i);
        return data;
    }

    public static <T> T[] Remove(T[] data, int index)
    {
        if (index < 0 || index >= data.length)
            return data;

        T[] newData = (T[]) java.lang.reflect.Array.newInstance(data.getClass().getComponentType(), data.length - 1);

        for (int i = 0; i < index; i++)
            newData[i] = data[i];

        for (int i = index + 1; i < data.length; i++)
            newData[i - 1] = data[i];

        return newData;
    }

    public static <T> T[] Join(T[] data, T value)
    {
        T[] newData = (T[]) java.lang.reflect.Array.newInstance(data.getClass().getComponentType(), data.length + 1);
        System.arraycopy(data, 0, newData, 0, data.length);
        newData[data.length] = value;
        return newData;
    }

    public static <T> T[] Copy(T[] data)
    {
        T[] newData = (T[]) java.lang.reflect.Array.newInstance(data.getClass().getComponentType(), data.length);
        System.arraycopy(data, 0, newData, 0, data.length);
        return newData;
    }

    public interface Generable<T>
    {
        T generate(T[] data, int index);
    }


}