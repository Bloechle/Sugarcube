/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sugarcube.formats.pdf.reader.pdf.codec;

/*
 * $Id: Predictor.java,v 1.2 2007/12/20 18:33:33 rbair Exp $
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

import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;

import java.io.IOException;

/**
 * The abstract superclass of various predictor objects that undo well-known
 * prediction algorithms.
 */
public abstract class Predictor
{
  public static Predictor NULL_PREDICTOR = new PredictorNull();
  /**
   * well known algorithms
   */
  public static final int NULL = -1;
  public static final int TIFF = 0;
  public static final int PNG = 1;
  /**
   * the algorithm to use
   */
  protected int algorithm = 1;
  /**
   * the number of colors per sugarcube.app.sample
   */
  protected int colors = 1;
  /**
   * the number of bits per color component
   */
  protected int bpc = 8;
  /**
   * the number of columns per row
   */
  protected int columns = 1;

  /**
   * Create an instance of a predictor. Use <code>getPredictor()</code> instead
   * of this.
   */
  protected Predictor(int algorithm)
  {
    this.algorithm = algorithm;
  }

  /**
   * Actually perform this algorithm on decoded image data. Subclasses must
   * implement this method
   */
  public abstract byte[] unpredict(byte[] imageData) throws IOException;

  public static Predictor getPredictor(PDFDictionary map) throws Exception
  {
    int algorithm = map == null ? 1 : map.get("Predictor").intValue(1);

    // create the predictor object
    Predictor predictor = null;
    switch (algorithm)
    {
    case 1:
      return NULL_PREDICTOR;// no predictor
    case 2:
      predictor = new PredictorTiff();
      break;
    case 10:
    case 11:
    case 12:
    case 13:
    case 14:
    case 15:
      predictor = new PredictorPNG();
      break;
    default:
      throw new Exception("Unknown predictor: " + algorithm);
    }
    if (map != null)
    {
      if (map.has("Colors"))
        predictor.colors = map.get("Colors").intValue(1);
      if (map.has("BitsPerComponent"))
        predictor.bpc = map.get("BitsPerComponent").intValue(8);
      if (map.has("Columns"))
        predictor.columns = map.get("Columns").intValue();
    }
    return predictor;
  }

  public static Predictor Get(int algorithm, int colors, int columns, int bpc) throws Exception
  {
    // create the predictor object
    Predictor predictor = null;
    switch (algorithm)
    {
    case 1:
      return NULL_PREDICTOR;// no predictor
    case 2:
      predictor = new PredictorTiff();
      break;
    case 10:
    case 11:
    case 12:
    case 13:
    case 14:
    case 15:
      predictor = new PredictorPNG();
      break;
    default:
      throw new Exception("Unknown predictor: " + algorithm);
    }
    predictor.colors = colors;
    predictor.columns = columns;
    predictor.bpc = bpc;

    return predictor;
  }
}
