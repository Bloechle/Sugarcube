/*
 * CVS Identifier:
 *
 * $Id: ImgDataConverter.java,v 1.13 2001/02/27 19:16:03 grosbois Exp $ 
 *
 * Interface:           ImgDataConverter
 *
 * Description:         The abstract class for classes that provide
 *                      Image Data Convertres (int -> float, float->int).
 *
 *
 *
 * COPYRIGHT:
 * 
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 * */
package jj2000.j2k.image;

/**
 * This class is responsible of all data type conversions. It should be used, at encoder side, between Tiler and ForwardWT modules and, at decoder side, between
 * InverseWT/CompDemixer and ImgWriter modules. The conversion is realized when a block of data is requested: if source and destination data type are the same
 * one, it does nothing, else appropriate cast is done. All the methods of the 'ImgData' interface are implemented by the 'ImgDataAdapter' class that is the
 * superclass of this one, so they don't need to be reimplemented by subclasses.
 *
 */
public class ImgDataConverter extends ImgDataAdapter implements BlkImgDataSrc
{
  /**
   * The block used to request data from the source in the case that a conversion seems necessary. It can be either int or float at initialization time. It will
   * be checked (and corrected if necessary) by the source whenever necessary
   */
  private DataBlk srcBlk = new DataBlkInt();
  /**
   * The source of image data
   */
  private BlkImgDataSrc src;
  /**
   * The number of fraction bits in the casted ints
   */
  private int fp;

  /**
   * Constructs a new ImgDataConverter object that operates on the specified source of image data.
   *
   * @param imgSrc The source from where to get the data to be transformed
   *
   * @param fp The number of fraction bits in the casted ints
   *
   * @see BlkImgDataSrc
     *
   */
  public ImgDataConverter(BlkImgDataSrc imgSrc, int fp)
  {
    super(imgSrc);
    src = imgSrc;
    this.fp = fp;
  }

  /**
   * Constructs a new ImgDataConverter object that operates on the specified source of image data.
   *
   * @param imgSrc The source from where to get the data to be transformed
   *
   * @see BlkImgDataSrc
     *
   */
  public ImgDataConverter(BlkImgDataSrc imgSrc)
  {
    super(imgSrc);
    src = imgSrc;
    fp = 0;
  }

  /**
   * Returns the position of the fixed point in the specified component. This is the position of the least significant integral (i.e. non-fractional) bit, which
   * is equivalent to the number of fractional bits. For instance, for fixed-point values with 2 fractional bits, 2 is returned. For floating-point data this
   * value does not apply and 0 should be returned. Position 0 is the position of the least significant bit in the data.
   *
   * @param c The index of the component.
   *
   * @return The position of the fixed-point, which is the same as the number of fractional bits.
     *
   */
  public int getFixedPoint(int c)
  {
    return fp;
  }

  /**
   * Returns, in the blk argument, a block of image data containing the specifed rectangular area, in the specified component, using the 'transfer type'
   * specified in the block given as argument. The data is returned, as a copy of the internal data, therefore the returned data can be modified "in place".
   *
   * <P>The rectangular area to return is specified by the 'ulx', 'uly', 'w' and 'h' members of the 'blk' argument, relative to the current tile. These members
   * are not modified by this method. The 'offset' of the returned data is 0, and the 'scanw' is the same as the block's width. See the 'DataBlk' class.
   *
   * <P>This method, in general, is less efficient than the 'getInternCompData()' method since, in general, it copies the data. However if the array of returned
   * data is to be modified by the caller then this method is preferable.
   *
   * <P>If the data array in 'blk' is 'null', then a new one is created. If the data array is not 'null' then it is reused, and it must be large enough to
   * contain the block's data. Otherwise an 'ArrayStoreException' or an 'IndexOutOfBoundsException' is thrown by the Java system.
   *
   * <P>The returned data may have its 'progressive' attribute set. In this case the returned data is only an approximation of the "final" data.
   *
   * @param blk Its coordinates and dimensions specify the area to return, relative to the current tile. If it contains a non-null data array, then it must be
   * large enough. If it contains a null data array a new one is created. Some fields in this object are modified to return the data.
   *
   * @param c The index of the component from which to get the data.
   *
   * @see #getInternCompData
     *
   */
  public DataBlk getCompData(DataBlk blk, int c)
  {
    return getData(blk, c, false);
  }

  /**
   * Returns, in the blk argument, a block of image data containing the specifed rectangular area, in the specified component, using the 'transfer type' defined
   * in the block given as argument. The data is returned, as a reference to the internal data, if any, instead of as a copy, therefore the returned data should
   * not be modified.
   *
   * <P>The rectangular area to return is specified by the 'ulx', 'uly', 'w' and 'h' members of the 'blk' argument, relative to the current tile. These members
   * are not modified by this method. The 'offset' and 'scanw' of the returned data can be arbitrary. See the 'DataBlk' class.
   *
   * <P> If source data and expected data (blk) are using the same type, block returned without any modification. If not appropriate cast is used.
   *
   * <P>This method, in general, is more efficient than the 'getCompData()' method since it may not copy the data. However if the array of returned data is to
   * be modified by the caller then the other method is probably preferable.
   *
   * <P>If the data array in <tt>blk</tt> is <tt>null</tt>, then a new one is created if necessary. The implementation of this interface may choose to return
   * the same array or a new one, depending on what is more efficient. Therefore, the data array in <tt>blk</tt> prior to the method call should not be
   * considered to contain the returned data, a new array may have been created. Instead, get the array from <tt>blk</tt> after the method has returned.
   *
   * <P>The returned data may have its 'progressive' attribute set. In this case the returned data is only an approximation of the "final" data.
   *
   * @param blk Its coordinates and dimensions specify the area to return, relative to the current tile. Some fields in this object are modified to return the
   * data.
   *
   * @param c The index of the component from which to get the data.
   *
   * @return The requested DataBlk
   *
   * @see #getCompData
     *
   */
  public final DataBlk getInternCompData(DataBlk blk, int c)
  {
    return getData(blk, c, true);
  }

  /**
   * Implements the 'getInternCompData()' and the 'getCompData()' methods. The 'intern' flag signals which of the two methods should run as.
   *
   * @param blk The data block to get.
   *
   * @param c The index of the component from which to get the data.
   *
   * @param intern If true behave as 'getInternCompData(). Otherwise behave as 'getCompData()'
   *
   * @return The requested data block
   *
   * @see #getInternCompData
   *
   * @see #getCompData
     *
   */
  private DataBlk getData(DataBlk blk, int c, boolean intern)
  {
    DataBlk reqBlk;    // Reference to block used in request to source

    // Keep request data type
    int otype = blk.getDataType();

    if (otype == srcBlk.getDataType())
      // Probably requested type is same as source type
      reqBlk = blk;
    else
    {
      // Probably requested type is not the same as source type
      reqBlk = srcBlk;
      // We need to copy requested coordinates and size
      reqBlk.ulx = blk.ulx;
      reqBlk.uly = blk.uly;
      reqBlk.w = blk.w;
      reqBlk.h = blk.h;
    }

    // Get source data block
    if (intern)
      // We can use the intern variant
      srcBlk = src.getInternCompData(reqBlk, c);
    else
      // Do not use the intern variant. Note that this is not optimal
      // since if we are going to convert below then we could have used
      // the intern variant. But there is currently no way to know if we
      // will need to do conversion or not before getting the data.
      srcBlk = src.getCompData(reqBlk, c);

    // Check if casting is needed
    if (srcBlk.getDataType() == otype)
      return srcBlk;

    int i;
    int k, kSrc, kmin;
    float mult;
    int w = srcBlk.w;
    int h = srcBlk.h;

    switch (otype)
    {
      case DataBlk.TYPE_FLOAT: // Cast INT -> FLOAT

        float farr[];
        int srcIArr[];

        // Get data array from resulting blk
        farr = (float[]) blk.getData();
        if (farr == null || farr.length < w * h)
        {
          farr = new float[w * h];
          blk.setData(farr);
        }

        blk.scanw = srcBlk.w;
        blk.offset = 0;
        blk.progressive = srcBlk.progressive;
        srcIArr = (int[]) srcBlk.getData();

        // Cast data from source to blk
        fp = src.getFixedPoint(c);
        if (fp != 0)
        {
          mult = 1.0f / (1 << fp);
          for (i = h - 1, k = w * h - 1, kSrc =
              srcBlk.offset + (h - 1) * srcBlk.scanw + w - 1; i >= 0; i--)
          {
            for (kmin = k - w; k > kmin; k--, kSrc--)
              farr[k] = ((srcIArr[kSrc] * mult));
            // Jump to geggining of next line in source
            kSrc -= srcBlk.scanw - w;
          }
        }
        else
          for (i = h - 1, k = w * h - 1, kSrc =
              srcBlk.offset + (h - 1) * srcBlk.scanw + w - 1; i >= 0; i--)
          {
            for (kmin = k - w; k > kmin; k--, kSrc--)
              farr[k] = ((float) (srcIArr[kSrc]));
            // Jump to geggining of next line in source
            kSrc -= srcBlk.scanw - w;
          }
        break; // End of cast INT-> FLOAT

      case DataBlk.TYPE_INT: // cast FLOAT -> INT
        int iarr[];
        float srcFArr[];

        // Get data array from resulting blk
        iarr = (int[]) blk.getData();
        if (iarr == null || iarr.length < w * h)
        {
          iarr = new int[w * h];
          blk.setData(iarr);
        }
        blk.scanw = srcBlk.w;
        blk.offset = 0;
        blk.progressive = srcBlk.progressive;
        srcFArr = (float[]) srcBlk.getData();

        // Cast data from source to blk
        if (fp != 0)
        {
          mult = (float) (1 << fp);
          for (i = h - 1, k = w * h - 1, kSrc =
              srcBlk.offset + (h - 1) * srcBlk.scanw + w - 1; i >= 0; i--)
          {
            for (kmin = k - w; k > kmin; k--, kSrc--)
              if (srcFArr[kSrc] > 0.0f)
                iarr[k] = (int) (srcFArr[kSrc] * mult + 0.5f);
              else
                iarr[k] = (int) (srcFArr[kSrc] * mult - 0.5f);
            // Jump to geggining of next line in source
            kSrc -= srcBlk.scanw - w;
          }
        }
        else
          for (i = h - 1, k = w * h - 1, kSrc =
              srcBlk.offset + (h - 1) * srcBlk.scanw + w - 1; i >= 0; i--)
          {
            for (kmin = k - w; k > kmin; k--, kSrc--)
              if (srcFArr[kSrc] > 0.0f)
                iarr[k] = (int) (srcFArr[kSrc] + 0.5f);
              else
                iarr[k] = (int) (srcFArr[kSrc] - 0.5f);
            // Jump to geggining of next line in source
            kSrc -= srcBlk.scanw - w;
          }
        break; // End cast FLOAT -> INT
      default:
        throw new IllegalArgumentException("Only integer and float data "
          + "are "
          + "supported by JJ2000");
    }
    return blk;
  }
}
