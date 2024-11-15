package sugarcube.common.numerics;

import sugarcube.common.interfaces.Vectorizable;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public enum DistanceMetric
{
  Manhattan
  {
    @Override
    public double eval(double[] v1, double[] v2)
    {
      double res = 0;
      for (int i = 0; i < v1.length && i < v2.length; i++)
        res += abs(v1[i] - v2[i]);
      
      return (double)res;
    }
    
    @Override
    public double eval(double[] v1, double[] v2, double[] weights)
    {
      double res = 0;
      for (int i = 0; i < v1.length && i < v2.length; i++)
        res += weights[i]*abs(v1[i] - v2[i]);      
      return (double)res;
    }
    
    @Override
    public double eval(int[] v1, int[] v2)
    {
      double res = 0;
      for (int i = 0; i < v1.length && i < v2.length; i++)
        res += abs(v1[i] - v2[i]);      
      return (double)res;
    }
    
    @Override
    public double eval(int[] v1, int[] v2, double[] weights)
    {
      double res = 0;
      for (int i = 0; i < v1.length && i < v2.length; i++)
        res += weights[i]*abs(v1[i] - v2[i]);      
      return (double)res;
    }
  },
  Euclidean
  {
    @Override
    public double eval(double[] v1, double[] v2)
    {
      double res = 0;
      double tmp;
      for (int i = 0; i < v1.length && i < v2.length; i++)
      {
        tmp = v1[i] - v2[i];
        res += tmp * tmp;
      }      
      return (double)sqrt(res);
    }
    
    @Override
    public double eval(double[] v1, double[] v2, double[] weights)
    {
      double res = 0;
      double tmp;
      for (int i = 0; i < v1.length && i < v2.length; i++)
      {
        tmp = (v1[i] - v2[i])*weights[i];
        res += tmp * tmp;
      }      
      return (double)sqrt(res);
    }
    
    @Override
    public double eval(int[] p1, int[] p2)
    {
      double res = 0;
      double tmp;
      for (int i = 0; i < p1.length && i < p2.length; i++)
      {
        tmp = p1[i] - p2[i];
        res += tmp * tmp;
      }      
      return (double)sqrt(res);
    }
    
    @Override
    public double eval(int[] p1, int[] p2, double[] weights)
    {
      double res = 0;
      double tmp;
      for (int i = 0; i < p1.length && i < p2.length; i++)
      {
        tmp = (p1[i] - p2[i])*weights[i];
        res += tmp * tmp;
      }      
      return (double)sqrt(res);
    }
  },
  Euclidean2
  {
    @Override
    public double eval(double[] v1, double[] v2)
    {
      double res = 0;
      double tmp;
      for (int i = 0; i < v1.length && i < v2.length; i++)
      {
        tmp = v1[i] - v2[i];
        res += tmp * tmp;
      }      
      return (double)res;
    }
    
    @Override
    public double eval(double[] v1, double[] v2, double[] weights)
    {
      double res = 0;
      double tmp;
      for (int i = 0; i < v1.length && i < v2.length; i++)
      {
        tmp = (v1[i] - v2[i])*weights[i];
        res += tmp * tmp;
      }      
      return (double)res;
    }
    
    @Override
    public double eval(int[] p1, int[] p2)
    {
      double res = 0;
      double tmp;
      for (int i = 0; i < p1.length && i < p2.length; i++)
      {
        tmp = p1[i] - p2[i];
        res += tmp * tmp;
      }      
      return (double)res;
    }
    
    @Override
    public double eval(int[] p1, int[] p2, double[] weights)
    {
      double res = 0;
      double tmp;
      for (int i = 0; i < p1.length && i < p2.length; i++)
      {
        tmp = (p1[i] - p2[i])*weights[i];
        res += tmp * tmp;
      }      
      return (double)res;
    }
  },
  Maximum
  {
    @Override
    public double eval(double[] v1, double[] v2)
    {
      double dist = Double.MIN_VALUE;
      for (int i = 0; i < v1.length; i++)
      {
        double d = abs(v1[i] - v2[i]);
        if (dist < d)
          dist = d;
      }
      return (double)dist;
    }
    
    @Override
    public double eval(double[] v1, double[] v2, double[] weights)
    {
      double dist = Double.MIN_VALUE;
      for (int i = 0; i < v1.length; i++)
      {
        double d = weights[i]*abs(v1[i] - v2[i]);
        if (dist < d)
          dist = d;
      }
      return (float)dist;
    }
    
    @Override
    public double eval(int[] p1, int[] p2)
    {
      double dist = Double.MIN_VALUE;
      for (int i = 0; i < p1.length; i++)
      {
        double d = abs(p1[i] - p2[i]);
        if (dist < d)
          dist = d;
      }
      return (double)dist;
    }
    
    @Override
    public double eval(int[] p1, int[] p2, double[] weights)
    {
      double dist = Integer.MIN_VALUE;
      for (int i = 0; i < p1.length; i++)
      {
        double d = weights[i]*abs(p1[i] - p2[i]);
        if (dist < d)
          dist = d;
      }
      return (double)dist;
    }
  };
  
  public abstract double eval(double[] p1, double[] p2);
  
  public abstract double eval(int[] p1, int[] p2);
  
  public abstract double eval(double[] p1, double[] p2, double[] weights);
  
  public abstract double eval(int[] p1, int[] p2, double[] weights);
  
  public double eval(Vectorizable v1, Vectorizable v2)
  {
    return this.eval(v1.realValues(),v2.realValues());
  }
  
  public double eval(Vectorizable v1, Vectorizable v2, Vectorizable w)
  {
    return this.eval(v1.realValues(),v2.realValues(),w.realValues());
  }
}