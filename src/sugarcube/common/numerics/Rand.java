package sugarcube.common.numerics;

import java.util.Random;

public class Rand
{
    public static Random RAND = new Random();

    public static Random Reset(long seed)
    {
        return RAND = new Random(seed);
    }

    public static double Get()
    {
        return RAND.nextDouble();
    }

    public static double Get(double min, double max)
    {
        return Get() * (max - min) + min;
    }

    public static int Int(double min, double max)
    {
        return Int(Math.round(min), Math.round(max));
    }

    public static int Int(int min, int max)
    {
        return RAND.nextInt(max - min + 1) + min;
    }

    public static double Gaussian()
    {
        return RAND.nextGaussian();
    }

    public static double Gaussian(double mean, double stdDev)
    {
        return Gaussian() * stdDev + mean;
    }

    public static boolean Bool()
    {
        return RAND.nextBoolean();
    }


}
