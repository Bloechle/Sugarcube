package sugarcube.formats.pdf.reader.pdf.codec;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class PredictorTiff extends Predictor
{
  public PredictorTiff()
  {
    super(TIFF);
  }

  @Override
  public byte[] unpredict(byte[] imageData) throws IOException
  {
    //TIFF predictor is the same as PNG subline Predictor (note that first byte is not used as Predictor algorithm type)
    
    List<byte[]> rows = new LinkedList<byte[]>();

    byte[] curLine = null;

    // get the number of bytes per row
    int rowSize = this.columns * this.bpc * this.colors;
    rowSize = (int) Math.ceil(rowSize / 8.0);

    int pos = 0;
    while (imageData.length - pos >= rowSize + 1) // imageData.remaining() >=
                                                  // rowSize + 1
    {
      // the first byte determines the algorithm
      // int pngAlgorithm = (int) (imageData[pos++] & 0xff);
      // read the rest of the line
      curLine = new byte[rowSize];
      System.arraycopy(imageData, pos, curLine, 0, rowSize);
      pos += rowSize;

      doSubLine(curLine);

      rows.add(curLine);
    }

    int outSize = 0;
    for (byte[] row : rows)
      outSize += row.length;

    byte[] unpredicted = new byte[outSize];

    pos = 0;
    for (byte[] row : rows)
    {
      System.arraycopy(row, 0, unpredicted, pos, row.length);
      pos += row.length;
    }
    return unpredicted;

  }

  /**
   * Return the value of the Sub algorithm on the line (compare bytes to the
   * previous byte of the same color on this line).
   */
  protected void doSubLine(byte[] curLine)
  {
    // get the number of bytes per sugarcube.app.sample
    int sub = (int) Math.ceil(this.bpc * this.colors / 8.0);

    for (int i = 0; i < curLine.length; i++)
    {
      int prevIdx = i - sub;
      if (prevIdx >= 0)
        curLine[i] += curLine[prevIdx];
    }
  }

  // @Override
  // public byte[] unpredict(byte[] imageData) throws IOException
  // {
  // if (this.bpc != 8)
  // Log.warn(this,
  // ".unpredict - bpc different from 8 not yet implemented: bpc=" + this.bpc);
  //
  // int col = columns;
  // for (int i = 0; i < imageData.length; i += colors)
  // if (col == columns)
  // {
  // col = 1;
  // } else
  // {
  // col++;
  // for (int j = 0; j < colors; j++)
  // imageData[i + j] = (byte) ((imageData[i + j - colors] & 0xff) -
  // (imageData[i + j] & 0xff));
  //
  // }
  // return imageData;
  // }
}
