package jj2000.j2k.decoder;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Image3;
import jj2000.colorspace.ColorSpace;
import jj2000.colorspace.ColorSpace.CSEnum;
import jj2000.j2k.image.BlkImgDataSrc;
import jj2000.j2k.image.Coord;
import jj2000.j2k.image.DataBlkInt;
import sugarcube.formats.pdf.reader.pdf.node.colorspace.CMYKColorSpace;
import sugarcube.formats.pdf.reader.pdf.node.colorspace.Indexed;
import sugarcube.formats.pdf.reader.pdf.node.image.PDFImage;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageProducer;

/**
 * This class provides an ImageProducer for the BlkImgDataSrc interface. It will
 * request data from the BlkImgDataSrc source and deliver it to the registered
 * image consumers. The data is requested line by line, starting at the top of
 * each tile. The tiles are requested in raster-scan order.
 * 
 * <p>
 * The image data is not rescaled to fit the available dynamic range (not even
 * the alpha values for RGBA data).
 * </p>
 * 
 * <p>
 * BlkImgDataSrc sources with 1, 3 and 4 components are supported. If 1, it is
 * assumed to be gray-level data. If 3 it is assumed to be RGB data, in that
 * order. If 4 it is assumed to be RGBA data (RGB plus alpha plane), in that
 * order. All components must have the same size.
 * </p>
 * 
 * @see ImageProducer
 * @see BlkImgDataSrc
 */
public class BlkImgDataSrcImager
{
  private BlkImgDataSrc src;
  private PDFImage pdfImage;
  private int type;
  private int nbOfComponents;
  private static final int GRAY = 0;
  private static final int RGB = 1;
  private static final int RGBA = 2;// R, G, B and A, in that order.
  private static final int CMYK = 3;
  // default color model (0xAARRGGBB) used in Java
  private static final ColorModel cm = ColorModel.getRGBdefault();

  public BlkImgDataSrcImager(PDFImage pdfImage, BlkImgDataSrc src, CSEnum cs)
  {
    this.nbOfComponents = src.getNumComps();
    this.pdfImage = pdfImage;

    if (cs == ColorSpace.CMYK)
      type = 3;
    else
      switch (nbOfComponents)
      {
      case 1:
        type = GRAY;
        break;
      case 3:
        type = RGB;
        break;
      case 4:
        type = RGBA;
        break;
      default:
        throw new IllegalArgumentException("Only 1, 3, and 4 components supported");
      }
    // Check component sizes and bit depths
    int imh = src.getCompImgHeight(0);
    int imw = src.getCompImgWidth(0);
    for (int i = nbOfComponents - 1; i >= 0; i--)
    {
      if (src.getCompImgHeight(i) != imh || src.getCompImgWidth(i) != imw)
        throw new IllegalArgumentException("All components must have the same dimensions and no subsampling");
      if (src.getNomRangeBits(i) > 8)
        throw new IllegalArgumentException("Depths greater than 8 bits per component is not yet supported");
    }
    this.src = src;
  }

  /**
   * <p>
   * If the data returned by the BlkImgDataSrc source happens to be progressive
   * (see BlkImgDataSrc and DataBlk) then the abort condition is sent to the
   * image consumers and no further data is delivered.
   * </p>
   * <p>
   * To start the BlkImgDataSrc is set to tile (0,0), and the tiles are produced
   * in raster sacn order. Once the last tile is produced, setTile(0,0) is
   * called again, which signals that we are done with the current tile, which
   * might free up resources.
   * </p>
   */
  public Image3 decode()
  {
    DataBlkInt[] db = new DataBlkInt[4]; // data-blocks to request data from src
    db[0] = db[1] = db[2] = db[3] = null;
    int i = 0;
    int l = 0;
    int c = 0;
    int[] k = new int[4];
    int[] tmp = new int[4];
    int[] tmpOut = new int[3];
    int[] max = new int[4];
    int[] shift = new int[4];// level shift for each component
    int[] fb = new int[4];// fractional bits for each component
    int[][] data = new int[4][]; // references to data buffers
    boolean prog; // Flag for progressive data

    int offsetX = -1;
    int offsetY = -1;
    int[] out = null;

    int width = src.getCompImgWidth(0);
    int height = src.getCompImgHeight(0);

    Indexed indexed = pdfImage.colorspace().isIndexed() ? (Indexed) pdfImage.colorspace() : null;
    // if (indexed != null)
    // Log.debug(this, ".decode - indexed JPX, nbOfComponents=" +
    // this.nbOfComponents);

    Image3 res;
    // Log.debug(this, ".decode - type=" + type);
    switch (type)
    {
    case RGBA:
      res = new Image3(width, height, BufferedImage.TYPE_INT_ARGB);
      break;
    // case GRAY:
    // res = new Image3(width, height, BufferedImage.TYPE_BYTE_GRAY);
    // break;
    default:
      res = new Image3(width, height, BufferedImage.TYPE_INT_RGB);
    }

    try
    {
      for (i = nbOfComponents - 1; i >= 0; i--)
      {
        db[i] = new DataBlkInt();
        shift[i] = 1 << (src.getNomRangeBits(i) - 1);
        max[i] = (1 << src.getNomRangeBits(i)) - 1;
        fb[i] = src.getFixedPoint(i);
      }

      Coord nbOfTiles = src.getNumTiles(null);
      // Start the data delivery tile by tile
      int tileIndex = 0; // index of the current tile
      java.awt.color.ColorSpace cmykCS = null;
      if (type == CMYK)
        cmykCS = CMYKColorSpace.GENERIC;

      for (int y = 0; y < nbOfTiles.y; y++)
        for (int x = 0; x < nbOfTiles.x; x++, tileIndex++)
        {
          src.setTile(x, y);
          height = src.getTileCompHeight(tileIndex, 0);
          width = src.getTileCompWidth(tileIndex, 0);
          // The offset of the active tiles is the same for all components,
          // since we don't support different component dimensions.
          offsetX = src.getCompULX(0) - (int) Math.ceil(src.getImgULX() / (double) src.getCompSubsX(0));
          offsetY = src.getCompULY(0) - (int) Math.ceil(src.getImgULY() / (double) src.getCompSubsY(0));
          // Deliver in lines to reduce memory usage
          for (l = 0; l < height; l++)
          {
            // Request line data
            prog = false;
            for (i = nbOfComponents - 1; i >= 0; i--)
            {
              db[i].ulx = 0;
              db[i].uly = l;
              db[i].w = width;
              db[i].h = 1;
              src.getInternCompData(db[i], i);
              prog = prog || db[i].progressive;
            }
            if (prog)
              Log.debug(this, ".decode - progressive data not yet supported");
            // Put pixel data in line buffer

            for (c = 0; c < nbOfComponents; c++)
            {
              data[c] = db[c].data; // cmyk
              k[c] = db[c].offset + width - 1;
            }
            for (i = width - 1; i >= 0; i--)
            {
              for (c = 0; c < nbOfComponents; c++)
              {
                tmp[c] = (data[c][k[c]--] >> fb[c]) + shift[c];
                tmp[c] = (tmp[c] < 0) ? 0 : ((tmp[c] > max[c]) ? max[c] : tmp[c]);
                tmp[c] = tmp[c] * 255 / max[c];
              }

              if (indexed != null)
              {
                int[] rgbIndexed = indexed.rgb(tmp[0]);
                System.arraycopy(rgbIndexed,  0,  tmpOut,  0,  rgbIndexed.length);
                // if (indexed.hival() == 178)
                // Log.debug(this, ".decode - indexed[" + indexed.hival() + "]="
                // + in[0] + "/" + Math.round(in[0] * indexed.hival() / 255f) +
                // ", rgb=" + Zen.A.toString(tmp));
              } else if (this.type == CMYK)
              {
                float[] cmyk = new float[4];
                for (int j = 0; j < cmyk.length; j++)
                {
                  cmyk[j] = tmp[j] / 255f;
                }
                float[] rgb = cmykCS.toRGB(cmyk);
                for (int j = 0; j < rgb.length; j++)
                {
                  tmpOut[j] = (int) (rgb[j] * 255 + 0.5);
                }

              } else if (nbOfComponents == 1)
              {
                //255-tmp[0]
                float[] lum = Color3.CS_GRAY.toRGB(new float[]
                { (float)Math.pow(tmp[0] / 255f, 2.2f) });
                tmpOut[0] = tmpOut[1] = tmpOut[2] = Math.round(lum[0] * 255f);
              }
              else
              {
                System.arraycopy(tmp,  0,  tmpOut,  0,  tmpOut.length);
              }
              res.setPixel(offsetX + i, offsetY + l, Zen.Array.trim(tmpOut, indexed == null ? nbOfComponents : 3));
            }
          }
        }
    } catch (Exception e)
    {
      // Log.debug(this,
      // ".decode - exception thrown: tile-width=" + width + ", tile-height=" +
      // height + ", width=" + res.getWidth() + ", height=" + res.getHeight()
      // + ", offsetX=" + offsetX + ", offsetY=" + offsetY + ", i=" + i + ", l="
      // + l + ", nbOfComponents=" + nbOfComponents + ", out.size="
      // + out.length + ", msg=" + e.getMessage());
      e.printStackTrace();
      // res = new Image3(width, height, BufferedImage.TYPE_INT_ARGB);
    }
    return res;
  }
}
