package sugarcube.formats.pdf.reader.pdf.codec;

import java.io.IOException;

public class PredictorNull extends Predictor
{
  public PredictorNull()
  {
    super(NULL);
  }

  @Override
  public byte[] unpredict(byte[] imageData)
    throws IOException
  {
    return imageData;
  }
}
