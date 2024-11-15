package sugarcube.common.numerics;

import sugarcube.common.data.collections.A;

import java.util.Random;

public class Prob
{

    public static double[] OBS =
            {229, 733, 912, 459, 876, 352, 631, 785, 681, 619, 335, 749, 393, 846, 989, 782, 517, 866, 422, 329, 448, 871, 671, 483, 674};

    public static double[] RES =
            {0, 1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 1};

    public static double[] Log(double[] data)
    {
        return A.Compute(data, x -> Math.log(x));
    }

    public static double Sigmoid(double slope, double mean, double x)
    {
        return 1.0 / (1.0 + Math.exp(-slope * (x - mean)));
    }

    public static double SigmoidLikelihood(double slope, double mean, double x, double y)
    {
        return Sigmoid(slope, mean, x) * y + (1.0 - Sigmoid(slope, mean, x)) * (1.0 - y);
    }

    public static double SigmoidLikelihood(double slope, double mean, double[] x, double[] y)
    {
        double e = 1.0;
        for (int i = 0; i < x.length && i < y.length; i++)
            e *= SigmoidLikelihood(slope, mean, x[i], y[i]);
        return e;
    }

    public static strictfp double[] LinearSpace(double min, double max, int size)
    {
        double[] y = new double[size];
        double dy = (max - min) / (size - 1);
        for (int i = 0; i < size; i++)
            y[i] = min + (dy * i);
        return y;
    }

    public static strictfp double[] LogSpace(double min, double max, int size)
    {
        return LogSpace(min, max, size, 10.0);
    }

    public static strictfp double[] LogSpace(double min, double max, int size, double base)
    {
        double[] y = new double[size];
        double[] linear = LinearSpace(min, max, size);
        for (int i = 0; i < y.length - 1; i++)
            y[i] = Math.pow(base, linear[i]);
        y[y.length - 1] = Math.pow(base, max);
        return y;
    }

    /**
     * Code from method java.util.Collections.shuffle();
     */
    public static <T> void Shuffle(T[] array, Random random)
    {
        if (random == null)
            random = new Random();
        int count = array.length;
        for (int i = count; i > 1; i--)
            Swap(array, i - 1, random.nextInt(i));
    }

    private static <T> void Swap(T[] array, int i, int j)
    {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public static double[] BestSigmoidBetween(double[] x, double[] y, double minX, double maxX)
    {
        double[] slopes = LogSpace(1, 2, 200);
        double[] means = LinearSpace(0.05, 0.95, 900);

        //System.out.println("slopes=" + Arrays.toString(slopes));

        double slope = 0;
        double mean = 0;
        double maxProb = Double.NEGATIVE_INFINITY;
        double prob = 0;

        double[] normY = y;
        double[] normX = new double[x.length];
        double deltaX = maxX - minX;
        for (int i = 0; i < normX.length; i++)
            normX[i] = (x[i] - minX) / deltaX;

        for (int i = 0; i < slopes.length; i++)
            for (int j = 0; j < means.length; j++)
                if ((prob = SigmoidLikelihood(slopes[i], means[j], normX, normY)) > maxProb)
                {
                    maxProb = prob;
                    slope = slopes[i];
                    mean = means[j];
                }

        return new double[]
                {mean, slope};
    }

    public static double GetSigmoidX(double[] xCoords, double[] yCoords, double minX, double maxX, double y)
    {
        return GetSigmoidX(BestSigmoidBetween(xCoords, yCoords, minX, maxX), minX, maxX, y);
    }

    public static double GetSigmoidX(double[] meanAndSlope, double minX, double maxX, double y)
    {
        return (meanAndSlope[0] - Math.log(1.0 / y - 1.0) / meanAndSlope[1]) * (maxX - minX) + minX;
    }

    public static double[] GetSigmoidXCoords(double[] meanAndSlope, double minX, double maxX, int... percents)
    {
        double[] ths = new double[percents.length];
        for (int i = 0; i < ths.length; i++)
            ths[i] = GetSigmoidX(meanAndSlope, minX, maxX, percents[i] / 100.0);
        return ths;
    }

    public static double[] SigmoidThresholdsStep(double[] meanAndSlope, double minX, double maxX, int step)
    {
        double[] thresholds = new double[100 / step - 1];
        int index = 0;
        for (int i = step; i <= 100 - step && index < thresholds.length; i += step)
        {
            System.out.println(i);
            thresholds[index++] = GetSigmoidX(meanAndSlope, minX, maxX, i / 100.0);
        }
        return thresholds;
    }

    public static void PrintSigmoidThresholds(double[] x, double[] y, double minX, double maxX, int deltaPercent)
    {
        for (int i = deltaPercent; i <= 100 - deltaPercent; i += deltaPercent)
        {
            double th = GetSigmoidX(x, y, minX, maxX, i / 100.0);
            System.out.println(i + "=" + th);
        }
    }

    public static double[] Complement(boolean doApply, double[] probs)
    {
        if (doApply)
            return Complement(probs);
        else
            return A.copy(probs);
    }

    public static double[] Complement(double[] probs)
    {
        double[] comp = new double[probs.length];
        for (int i = 0; i < probs.length; i++)
            comp[i] = 1 - probs[i];
        return comp;
    }

    public static double Entropy(double[] probs)
    {
        double entropy = 0;
        for (double p : probs)
            entropy += p * Math.log(p);
        return -entropy;
    }

    public static void main(String... args)
    {
        double[] x = OBS;// reaction time in ms
        double[] y = RES;// 0 (fail) or 1 (success)

        if (x.length != y.length)
            System.out.println("x.length=" + x.length + ", y.length=" + y.length);



            double th = GetSigmoidX(x, y, 250, 750, 0.5);
            System.out.println(th);


    }
}
