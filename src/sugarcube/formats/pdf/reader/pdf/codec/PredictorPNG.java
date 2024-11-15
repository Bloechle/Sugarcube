package sugarcube.formats.pdf.reader.pdf.codec;

/*
 * $Id: PNGPredictor.java,v 1.3 2009/02/12 13:53:58 tomoke Exp $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Undo prediction based on the PNG algorithm.
 */
public class PredictorPNG extends Predictor
{
  /**
   * Creates a new instance of PNGPredictor
   */
  public PredictorPNG()
  {
    super(PNG);
  }

  /**
   * Undo data based on the png algorithm
   */
  @Override
  public byte[] unpredict(byte[] imageData)
    throws IOException
  {
    //XED.LOG.debug(this, ".unpredict - trying to unpredict: bpc=" + this.bpc);
    List<byte[]> rows = new LinkedList<byte[]>();

    byte[] curLine = null;
    byte[] prevLine = null;

    // get the number of bytes per row
    int rowSize = this.columns * this.bpc * this.colors;
    rowSize = (int) Math.ceil(rowSize / 8.0);

    int pos = 0;
    while (imageData.length - pos >= rowSize + 1) //imageData.remaining() >= rowSize + 1
    {
      // the first byte determines the algorithm
      int pngAlgorithm = (int) (imageData[pos++] & 0xff);
      // read the rest of the line
      curLine = new byte[rowSize];
      System.arraycopy(imageData, pos, curLine, 0, rowSize);
      pos += rowSize;

      switch (pngAlgorithm)
      {
        case 0:
          // none
          break;
        case 1:
          doSubLine(curLine);
          break;
        case 2:
          doUpLine(curLine, prevLine);
          break;
        case 3:
          doAverageLine(curLine, prevLine);
          break;
        case 4:
          doPaethLine(curLine, prevLine);
          break;
      }

      rows.add(curLine);
      prevLine = curLine;
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
   * Return the value of the Sub algorithm on the line (compare bytes to the previous byte of the same color on this line).
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

  /**
   * Return the value of the up algorithm on the line (compare bytes to the same byte in the previous line)
   */
  protected void doUpLine(byte[] curLine, byte[] prevLine)
  {
    if (prevLine == null)
      // do nothing if this is the first line
      return;

    for (int i = 0; i < curLine.length; i++)
      curLine[i] += prevLine[i];
  }

  /**
   * Return the value of the average algorithm on the line (compare bytes to the average of the previous byte of the same color and the same byte on the
   * previous line)
   */
  protected void doAverageLine(byte[] curLine, byte[] prevLine)
  {
    // get the number of bytes per sugarcube.app.sample
    int sub = (int) Math.ceil(this.bpc * this.colors / 8.0);

    for (int i = 0; i < curLine.length; i++)
    {
      int raw = 0;
      int prior = 0;

      // get the last value of this color
      int prevIdx = i - sub;
      if (prevIdx >= 0)
        raw = curLine[prevIdx] & 0xff;

      // get the value on the previous line
      if (prevLine != null)
        prior = prevLine[i] & 0xff;

      // add the average
      curLine[i] += (byte) Math.floor((raw + prior) / 2);
    }
  }

  /**
   * Return the value of the average algorithm on the line (compare bytes to the average of the previous byte of the same color and the same byte on the
   * previous line)
   */
  protected void doPaethLine(byte[] curLine, byte[] prevLine)
  {
    // get the number of bytes per sugarcube.app.sample
    int sub = (int) Math.ceil(this.bpc * this.colors / 8.0);

    for (int i = 0; i < curLine.length; i++)
    {
      int left = 0;
      int up = 0;
      int upLeft = 0;

      // get the last value of this color
      int prevIdx = i - sub;
      if (prevIdx >= 0)
        left = curLine[prevIdx] & 0xff;

      // get the value on the previous line
      if (prevLine != null)
        up = prevLine[i] & 0xff;

      if (prevIdx > 0 && prevLine != null)
        upLeft = prevLine[prevIdx] & 0xff;

      // add the average
      curLine[i] += (byte) paeth(left, up, upLeft);
    }
  }

  /**
   * The paeth algorithm
   */
  protected int paeth(int left, int up, int upLeft)
  {
    int p = left + up - upLeft;
    int pa = Math.abs(p - left);
    int pb = Math.abs(p - up);
    int pc = Math.abs(p - upLeft);

    if ((pa <= pb) && (pa <= pc))
      return left;
    else if (pb <= pc)
      return up;
    else
      return upLeft;
  }
}
