package sugarcube.common.numerics;

public class MoreMath
{
    public static final double PI = java.lang.Math.PI;
    public static final double SQRT_2 = java.lang.Math.sqrt(2.0);
    public static final double SQRT_6 = java.lang.Math.sqrt(6.0);
    public static final double ATAN_SQRT_6 = java.lang.Math.atan(SQRT_6);
    public static final double LOG_2 = java.lang.Math.log(2.0);
    public static final double TO_RADIAN = java.lang.Math.PI / 180.0;
    public static final double TO_DEGREE = 180.0 / java.lang.Math.PI;

    public static double abs(double value)
    {
        return java.lang.Math.abs(value);
    }


    public static double log2(double a)
    {
        return java.lang.Math.log(a) / java.lang.Math.log(2);
    }

    public static int log2(int a)
    {
        return (int) log2((double) a);
    }

    public static int[] primes(int howmany)
    {
        int[] primes = new int[howmany];
        primes[0] = 1;
        primes[1] = 2;
        int count = 2;
        outer:
        for (int i = 3; count < primes.length; i += 2)
        {
            int limit = ((int) java.lang.Math.sqrt(i)) + 1;
            for (int j = 1; j < count && primes[j] <= limit; j++)
                if ((i % primes[j]) == 0)
                    continue outer;
            primes[count++] = i;
        }

        return primes;
    }

    public static int round(double value)
    {
        return (int) java.lang.Math.round(value);
    }

    public static double[] apply(double[] data, Evaluable function)
    {
        if (function == null)
            return data;
        double[] result = new double[data.length];
        for (int i = 0; i < result.length; i++)
            result[i] = function.eval(data[i], i);
        return result;
    }

    public static double[][] apply(double[][] data, Evaluable function)
    {
        if (function == null)
            return data;
        double[][] result = new double[data.length][data[0].length];
        for (int y = 0; y < data.length; y++)
            for (int x = 0; x < data[0].length; x++)
                result[y][x] = function.eval(data[y][x], x, y);
        return result;
    }
}
