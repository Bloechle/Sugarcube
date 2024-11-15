/*
 * CVS identifier:
 *
 * $Id: ImgWriter.java,v 1.12 2001/09/14 09:13:11 grosbois Exp $
 *
 * Class:                   ImgWriter
 *
 * Description:             Generic interface for all image writer
 *                          classes (to file or other resource)
 *
 *
 *
 * COPYRIGHT:
 * 
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 *  */
package jj2000.j2k.image.output;

import jj2000.j2k.image.BlkImgDataSrc;
import jj2000.j2k.image.Coord;
import jj2000.j2k.image.DataBlk;

import java.io.IOException;

/**
 * This is the generic interface to be implemented by all image file (or other resource) writers for different formats.
 *
 * <p>Each object inheriting from this class should have a source ImgData object associated with it. The image data to write to the file is obtained from the
 * associated ImgData object. In general this object would be specified at construction time.</p>
 *
 * <p>Depending on the actual type of file that is written a call to any write() or writeAll() method will write data from one component, several components or
 * all components. For example, a PGM writer will write data from only one component (defined in the constructor) while a PPM writer will write 3 components
 * (normally R,G,B).</p>
 *
 */
public abstract class ImgWriter
{
  /**
   * The defaukt height used when writing strip by strip in the 'write()' method. It is 64.
   */
  public final static int DEF_STRIP_HEIGHT = 64;
  /**
   * The source ImagaData object, from where to get the image data
   */
  protected BlkImgDataSrc src;
  /**
   * The width of the image
   */
  protected int w;
  /**
   * The height of the image
   */
  protected int h;

  /**
   * Closes the underlying file or netwrok connection to where the data is written. The implementing class must write all buffered data before closing the file
   * or resource. Any call to other methods of the class become illegal after a call to this one.
   *
   * @exception IOException If an I/O error occurs.
     *
   */
  public abstract void close() throws IOException;

  /**
   * Writes all buffered data to the file or resource. If the implementing class does onot use buffering nothing should be done.
   *
   * @exception IOException If an I/O error occurs.
     *
   */
  public abstract void flush() throws IOException;

  /**
   * Flushes the buffered data before the object is garbage collected. If an exception is thrown the object finalization is halted, but is otherwise ignored.
   *
   * @exception IOException If an I/O error occurs. It halts the finalization of the object, but is otherwise ignored.
   *
   * @see Object#finalize
     *
   */
  public void finalize() throws IOException
  {
    flush();
  }

  /**
   * Writes the source's current tile to the output. The requests of data issued by the implementing class to the source ImgData object should be done by blocks
   * or strips, in order to reduce memory usage.
   *
   * <p>The implementing class should only write data that is not "progressive" (in other words that it is final), see DataBlk for details.</p>
   *
   * @exception IOException If an I/O error occurs.
   *
   * @see DataBlk
     *
   */
  public abstract void write() throws IOException;

  /**
   * Writes the entire image or only specified tiles to the output. The implementation in this class calls the write() method for each tile starting with the
   * upper-left one and proceding in standard scanline order. It changes the current tile of the source data.
   *
   * @exception IOException If an I/O error occurs.
   *
   * @see DataBlk
     *
   */
  public void writeAll() throws IOException
  {
    // Find the list of tile to decode.
    Coord nT = src.getNumTiles(null);

    // Loop on vertical tiles
    for (int y = 0; y < nT.y; y++)
      // Loop on horizontal tiles
      for (int x = 0; x < nT.x; x++)
      {
        src.setTile(x, y);
        write();
      } // End loop on horizontal tiles             // End loop on vertical tiles
  }

  /**
   * Writes the data of the specified area to the file, coordinates are relative to the current tile of the source.
   *
   * <p>The implementing class should only write data that is not "progressive" (in other words that is final), see DataBlk for details.</p>
   *
   * @param ulx The horizontal coordinate of the upper-left corner of the area to write, relative to the current tile.
   *
   * @param uly The vertical coordinate of the upper-left corner of the area to write, relative to the current tile.
   *
   * @param width The width of the area to write.
   *
   * @param height The height of the area to write.
   *
   * @exception IOException If an I/O error occurs.
     *
   */
  public abstract void write(int ulx, int uly, int w, int h)
    throws IOException;
}
