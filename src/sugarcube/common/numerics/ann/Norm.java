package sugarcube.common.numerics.ann;

import sugarcube.common.data.Zen;

public interface Norm
{
  public static final Norm IDENTITY = new Norm()
  {
    @Override
    public String name()
    {
      return "identity";
    }

    @Override
    public float[] normalize(float... values)
    {
      return values;
    }
  };

  public String name();

  public float[] normalize(float... values);

  public static class Gaussian implements Norm
  {
    float[] mean;
    float[] sdev;

    public Gaussian(SampleSet set)
    {
      if (!set.isEmpty())
      {
        int size = 0;
        this.mean = new float[set.nbOfFeatures()];
        this.sdev = new float[set.nbOfFeatures()];
        for (Sample sample : set)
          if (!sample.isLabel(Label.UNDEF))
          {
            size++;
            float[] values = sample.values();
            for (int i = 0; i < values.length; i++)
            {
              mean[i] += values[i];
              sdev[i] += values[i] * values[i];
            }
          }

        for (int i = 0; i < mean.length; i++)
        {
          mean[i] /= size;
          sdev[i] /= size;
          sdev[i] -= mean[i] * mean[i];
          sdev[i] = (float) Math.sqrt(sdev[i]);
        }
      }
    }

    @Override
    public String name()
    {
      return "gaussian";
    }

    @Override
    public float[] normalize(float[] values)
    {
      if (mean == null)
        return Zen.Array.copy(values);
      {
        float[] norm = new float[values.length];
        for (int i = 0; i < norm.length; i++)
          norm[i] = (values[i] - mean[i]) / (sdev[i] > 0f ? sdev[i] : 1f);
        return norm;
      }
    }
  }

  public static class Extrema implements Norm
  {
    float[] subtract;
    float[] factor;
    float scale;
    float shift;

    public Extrema(SampleSet set, double scale, double shift)
    {
      this.scale = (float) scale;
      this.shift = (float) shift;
      if (!set.isEmpty())
      {
        float[] min = null;
        float[] max = null;
        for (Sample sample : set)
          if (!sample.isLabel(Label.UNDEF))
          {
            float[] values = sample.values();
            if (min == null)
            {
              min = Zen.Array.copy(values);
              max = Zen.Array.copy(values);
            }

            for (int i = 0; i < values.length; i++)
              if (values[i] < min[i])
                min[i] = values[i];
              else if (values[i] > max[i])
                max[i] = values[i];
          }

        if (min != null)
        {
          this.subtract = new float[min.length];
          this.factor = new float[min.length];

          for (int i = 0; i < this.subtract.length; i++)
          {
            factor[i] = (max[i] - min[i]) / 2f;
            subtract[i] = min[i] + factor[i];
          }
        }
      }
    }

    @Override
    public String name()
    {
      return "extrema";
    }

    @Override
    public float[] normalize(float[] values)
    {
      if (subtract == null)
        return Zen.Array.copy(values);
      else
      {
        float[] norm = new float[values.length];
        for (int i = 0; i < norm.length; i++)
          norm[i] = scale * (values[i] - subtract[i]) / (factor[i] > 0f ? factor[i] : 1f) + shift;
        return norm;
      }
    }
  }

  public static class ExtremaPos implements Norm
  {
    float[] subtract;
    float[] factor;

    public ExtremaPos(SampleSet set)
    {
      if (!set.isEmpty())
      {
        float[] min = null;
        float[] max = null;
        for (Sample sample : set)
          if (!sample.isLabel(Label.UNDEF))
          {
            float[] values = sample.values();
            if (min == null)
            {
              min = Zen.Array.copy(values);
              max = Zen.Array.copy(values);
            }

            for (int i = 0; i < values.length; i++)
              if (values[i] < min[i])
                min[i] = values[i];
              else if (values[i] > max[i])
                max[i] = values[i];
          }

        if (min != null)
        {
          this.subtract = new float[min.length];
          this.factor = new float[min.length];

          for (int i = 0; i < this.subtract.length; i++)
          {
            factor[i] = (max[i] - min[i]);
            subtract[i] = min[i];
          }
        }
      }
    }

    @Override
    public String name()
    {
      return "extrema-pos";
    }

    @Override
    public float[] normalize(float[] values)
    {
      if (subtract == null)
        return Zen.Array.copy(values);
      else
      {
        float[] norm = new float[values.length];
        for (int i = 0; i < norm.length; i++)
          norm[i] = (values[i] - subtract[i]) / (factor[i] > 0f ? factor[i] : 1f);
        return norm;
      }
    }
  }
}
