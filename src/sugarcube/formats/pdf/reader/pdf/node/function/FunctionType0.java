package sugarcube.formats.pdf.reader.pdf.node.function;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.object.PDF;
import sugarcube.formats.pdf.reader.pdf.object.PDFStream;

public class FunctionType0 extends PDFFunction
{
  private float[] samples = null;
  private float[] encode = null;
  private float[] decode = null;
  private int[] size = new int[0];
  private int bps = 8;
  private int order = 1;
  private int outSize;

  // Sampled function
  public FunctionType0(PDFNode node, PDFStream stream)
  {
    super(node, stream);
    this.bps = stream.get("BitsPerSample").toPDFNumber().intValue();
    this.size = stream.get("Size").toPDFArray().intValues();
    this.order = stream.get("Order").toPDFNumber().intValue();

    if (stream.contains("Encode"))
      this.encode = stream.get("Encode").toPDFArray().floatValues();
    else
    {
      this.encode = new float[2 * this.size.length];
      for (int i = 1; i < this.encode.length; i += 2)
        this.encode[i] = this.size[i / 2];
    }
    if (stream.contains("Decode"))
      this.decode = stream.get("Decode").toPDFArray().floatValues();
    else
      this.decode = Zen.Array.copy(this.range);
    try
    {
      readSamples(stream.byteValues());
    } catch (Exception e)
    {
      Log.warn(this, " - exception thrown: " + e);
    }
  }

  private void readSamples(byte[] stream)
  {
    if (bps == 8)
    {
      int count = stream.length;
      samples = new float[count];
      for (int i = 0; i < count; i++)
        samples[i] = (stream[i] & 255) / 256f;
    } else if (bps == 4)
    {
      int count = stream.length * 2;
      samples = new float[count];
      int pos = 0;
      for (int i = 0; i < count; i = i + 2)
      {
        samples[i] = ((stream[pos] & 240) >> 4) / 16f;
        samples[i + 1] = (stream[pos] & 15) / 16f;
        pos++;
      }
    } else if (bps == 2)
    {
      int count = stream.length * 4;
      samples = new float[count];
      int pos = 0;
      for (int i = 0; i < count; i = i + 4)
      {
        samples[i] = ((stream[pos] & 192) >> 6) / 4f;
        samples[i + 1] = ((stream[pos] & 48) >> 4) / 4f;
        samples[i + 2] = ((stream[pos] & 12) >> 2) / 4f;
        samples[i + 3] = (stream[pos] & 3) / 4f;
        pos++;
      }
    } else if (bps == 1)
    {
      int count = stream.length * 8;
      samples = new float[count];
      int pos = 0;
      for (int i = 0; i < count; i = i + 8)
      {
        samples[i] = ((stream[pos] & 128) >> 7) / 2f;
        samples[i + 1] = ((stream[pos] & 64) >> 6) / 2f;
        samples[i + 2] = ((stream[pos] & 32) >> 5) / 2f;
        samples[i + 3] = ((stream[pos] & 16) >> 4) / 2f;
        samples[i + 4] = ((stream[pos] & 8) >> 3) / 2f;
        samples[i + 5] = ((stream[pos] & 4) >> 2) / 2f;
        samples[i + 6] = ((stream[pos] & 2) >> 1) / 2f;
        samples[i + 7] = ((stream[pos] & 1)) / 2f;
        pos++;
      }
    } else if (bps == 12)
    {
      int samplesPerByte = 16 / bps;
      int count = stream.length * samplesPerByte * 2;
      samples = new float[count];
      int byteReached = 0, bitsLeft = 0;
      int maxSize = (2 << bps) - 1;
      for (int ii = 0; ii < count; ii++)
      {
        for (int jj = 0; jj < samplesPerByte; jj++)
          samples[ii] = ((((stream[byteReached] << 8) + stream[byteReached]) & (maxSize << (16 - (jj * bps)))) >> (16 - bps))
              / maxSize;
        // rollon
        while (bitsLeft > 16)
        {
          byteReached = byteReached + 2;
          bitsLeft = bitsLeft - 16;
        }
      }
    } else
    { // rest of values 16,24,32
      int bytes = bps / 8;
      int count = stream.length / bytes;
      samples = new float[count];

      // Log.debug(this, ".readSamples - bps=" + bps);

      int pos = 0;
      long max = 0;
      if (bps == 16)
        max = 65536;
      else if (bps == 24)
        max = 16777216;
      else if (bps == 32)
        max = 4294967296L;
      else
        Log.warn(this, ".readSamples - unexpected value: bps=" + bps);
      for (int i = 0; i < count; i++)
      {
        long val = 0;
        for (int off = 0; off < bytes; off++)
          val += ((long) ((stream[pos + off] & 0xff)) << (8 * (bytes - off - 1)));
        samples[i] = (float) (val / (double) max); // sugarcube.app.sample is fraction
        // System.out.print(" " + val);
        pos += bytes;
      }
    }
    outSize = range.length / 2;
  }

  @Override
  public float[] eval(float[] x)
  {
    float[] y = new float[outSize];
    int xSize = domain.length / 2;
    int ySie = range.length / 2;
    // @odd
    if (ySie < xSize)
    {
      // reverse
      int size = x.length;
      float[] reversed = new float[size];
      for (int i = 0; i < size; i++)
        reversed[size - i - 1] = x[i];
      x = reversed;
      // Log.debug(this, ".eval - odd");
    }

    float[] e = new float[xSize];
    for (int i = 0; i < xSize; i++)
    {
      // Encode and clip value to sugarcube.app.sample table
      e[i] = encode(x[i], i);
      if (ySie == xSize)
        y[i] = value(e[i], i, ySie, 0);
      else if (xSize < ySie)
        for (int j = 0; j < ySie; j++)
          y[j] = value(e[i], j, ySie, j);
      else if (ySie < xSize)
        // @odd - see above as well
        // Current issue with hexachromatic colorspaces.
        // Possible need for transparency
        // also must figure out how to turn 6 into n components
        for (int j = 0; j < ySie; j++)
          y[j] = value(e[i], j, ySie, j);
    }
    // Zen.debug(this,
    // ".eval: input="+Zen.Array.toString(input)+", output="+Zen.Array.toString(output));
    return y;
  }

  private float encode(float value, int i)
  {
    value = Math.min(Math.max(value, domain[i * 2]), domain[i * 2 + 1]);
    value = interpolate(value, domain[i * 2], domain[i * 2 + 1], encode[i * 2],
        Math.min(encode[i * 2 + 1], size[i] - 1));// 2014.02.14
    value = Math.min(Math.max(value, 0), size[i] - 1);
    return value;
  }

  private float value(float value, int j, int outSize, int modifier)
  {
    // Convert input value into a sampled value
    int sample = (int) (value);
    if ((value - (int) value) > 0)
      sample = (int) value + 1;
    // Calculate the fraction between this value and value+1
    float frac1 = sample - value, frac0 = 1f - frac1;
    // Calculate the point in the samples array
    int lower = (((int) value * outSize) + modifier);
    int upper = ((sample * outSize) + modifier);
    // Get output value + the fraction between the values
    float output = (frac1 * samples[lower]) + (frac0 * (samples[upper]));
    // uses 1 and not maxSize as we have already factored in
    output = interpolate(output, 0, 1, decode[j * 2], decode[j * 2 + 1]);
    // clip to output range
    output = Math.min(Math.max(output, range[j * 2]), range[j * 2 + 1]);
    // final output result
    return output;
  }

  @Override
  public String toString()
  {
    return super.toString() + "\nBitsPerSample[" + this.bps + "]" + "\nOrder[" + order + "]" + "\nEncode"
        + PDF.toString(encode) + "\nDecode" + PDF.toString(decode) + "\nSize[" + Zen.Array.String(size) + "]"
        + "\nSamples[" + Zen.Array.String(samples) + "]";
  }
}
