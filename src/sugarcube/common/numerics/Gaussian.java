package sugarcube.common.numerics;

public class Gaussian
{
    public static final double PRECISION = 0.00000001;

    public static final double SQRT_2PI = Math.sqrt(2 * Math.PI);

    // return pdf(x) = standard Gaussian pdf
    public static double PDF(double x)
    {
        return Math.exp(-x * x / 2) / SQRT_2PI;
    }

    // return pdf(x, mu, sigma) = Gaussian pdf with mean mu and stddev sigma
    public static double PDF(double x, double mu, double sigma)
    {
        return PDF((x - mu) / sigma) / sigma;
    }

    // return cdf(z) = standard Gaussian cdf using Taylor approximation
    public static double CDF(double z)
    {
        if (z < -8.0) return 0.0;
        if (z > 8.0) return 1.0;
        double sum = 0.0, term = z;
        for (int i = 3; sum + term != sum; i += 2)
        {
            sum = sum + term;
            term = term * z * z / i;
        }
        return 0.5 + sum * PDF(z);
    }

    public static double CDF(double y, int[] muSigma)
    {
        return CDF(y, muSigma[0], muSigma[1]);
    }

    public static double CDF(double y, double[] muSigma)
    {
        return CDF(y, muSigma[0], muSigma[1]);
    }

    // return cdf(z, mu, sigma) = Gaussian cdf with mean mu and stddev sigma
    public static double CDF(double z, double mu, double sigma)
    {
        return CDF((z - mu) / sigma);
    }

    public static double[] CDF(double z, double[] mu, double[] sigma)
    {
        double[] data = new double[mu.length];
        for (int i = 0; i < data.length; i++)
            data[i] = CDF(z, mu[i], sigma[i]);
        return data;
    }

    public static double[][] CDF(int[] z, int[][] muSigmaPairs)
    {
        double[][] data = new double[z.length][];
        for (int i = 0; i < z.length; i++)
            data[i] = CDF(z[i], muSigmaPairs);
        return data;
    }

    public static double[] CDF(double z, int[][] muSigmaPairs)
    {
        double[] data = new double[muSigmaPairs.length];
        for (int i = 0; i < data.length; i++)
            data[i] = CDF(z, muSigmaPairs[i][0], muSigmaPairs[i][1]);
        return data;
    }

    public static double[] CDF(double z, double[][] muSigmaPairs)
    {
        double[] data = new double[muSigmaPairs.length];
        for (int i = 0; i < data.length; i++)
            data[i] = CDF(z, muSigmaPairs[i][0], muSigmaPairs[i][1]);
        return data;
    }

    private static double Range(double sigma)
    {
        double range = 1.0;
        while (CDF(range, 0, sigma) < 0.99)
            range *= 1.5;
        return range;
    }

    // Compute z such that cdf(z) = y via bisection search
    public static double InverseCDF(double y)
    {
        return InverseCDF(y, PRECISION, -8, 8);
    }

    // bisection search
    private static double InverseCDF(double y, double delta, double lo, double hi)
    {
        double mid = lo + (hi - lo) / 2;
        if (hi - lo < delta) return mid;
        if (CDF(mid) > y)
            return InverseCDF(y, delta, lo, mid);
        else
            return InverseCDF(y, delta, mid, hi);
    }

    public static double PPF(double y, int[] muSigma)
    {
        return PPF(y, muSigma[0], muSigma[1]);
    }

    public static double PPF(double y, double[] muSigma)
    {
        return PPF(y, muSigma[0], muSigma[1]);
    }

    public static double PPF(double y, double mu, double sigma)
    {
        return InverseCDF(y, mu, sigma);
    }

    // Compute z such that cdf(z) = y via bisection search
    public static double InverseCDF(double y, double mu, double sigma)
    {
        double range = Range(sigma);
        return InverseCDF(y, mu, sigma, PRECISION, -range + mu, range + mu);
    }

    // bisection search
    private static double InverseCDF(double y, double mu, double sigma, double delta, double lo, double hi)
    {
        double mid = lo + (hi - lo) / 2;
        if (hi - lo < delta)
            return mid;
        if (CDF(mid, mu, sigma) > y)
            return InverseCDF(y, mu, sigma, delta, lo, mid);
        else
            return InverseCDF(y, mu, sigma, delta, mid, hi);
    }

    public static void main(String[] args)
    {
        if (args.length == 0)
            args = new String[]{"2", "3", "4"};

        double z = Double.parseDouble(args[0]);
        double mu = Double.parseDouble(args[1]);
        double sigma = Double.parseDouble(args[2]);
        System.out.println(CDF(z, mu, sigma));
        double y = CDF(z);
        System.out.println(InverseCDF(y));

        mu = 10;
        sigma = 5;
        for (int i = 0; i <= 20; i++)
        {
            double cdf = CDF(i, mu, sigma);
            double icdf = InverseCDF(cdf, mu, sigma);
            System.out.println("x=" + i + ", cdf=" + cdf + ", icdf=" + icdf);
        }
    }

    public static double Random(double mean, double stdDev)
    {
        return Rand.Gaussian() * stdDev + mean;
    }

}
