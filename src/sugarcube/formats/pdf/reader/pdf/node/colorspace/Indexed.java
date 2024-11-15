package sugarcube.formats.pdf.reader.pdf.node.colorspace;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Image3;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.node.image.PDFImage;
import sugarcube.formats.pdf.reader.pdf.node.image.ZoubitReader;
import sugarcube.formats.pdf.reader.pdf.object.PDF;
import sugarcube.formats.pdf.reader.pdf.object.PDFArray;
import sugarcube.formats.pdf.reader.pdf.object.PDFObject;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;

public class Indexed extends PDFColorSpace
{
  private PDFColorSpace baseCS = null;
  private int[] cmap;
  private int[][] palette;// palette[index]=rgb [0..1]
  private int hival = 255;// maximum possible value is 255 (1 byte)
  private String lookupClass = "null";
  private byte[] lookup;

  // TODO setting the current stroking or non-stroking colorspace to indexed
  // iniatilizes the current state color to the one corresponding to index 0
  public Indexed(PDFNode parent, PDFArray array)
  {
    super(parent, "Indexed", 1);


    this.baseCS = PDFColorSpace.instance(this, null, array.get(1));

    // this.nbOfComponents = this.baseCS.nbOfComponents;
    this.hival = array.get(2).toPDFNumber().intValue(hival);

    PDFObject lookupObject = array.get(3).unreference();
    if (lookupObject.isPDFStream())
      this.lookup = lookupObject.toPDFStream().byteValues();
    else if (lookupObject.isPDFString())
      this.lookup = lookupObject.toPDFString().byteValues();
    this.lookupClass = lookupObject.unreference().getClass().getSimpleName();

    this.colorSpace = this.baseCS.colorSpace;

    if (Math.abs(lookup.length - baseCS.nbOfComponents * (hival + 1)) > 1)
      Log.warn(this, " - lookup table has a strange size: stream size=" + lookup.length + " expected size=" + (baseCS.nbOfComponents * (hival + 1)));

    this.palette = new int[hival + 1][];
    this.cmap = new int[hival + 1];
    int[] xyz = new int[baseCS.nbOfComponents()];
    int pos = 0;
    
    for (int index = 0; index < palette.length; index++)
    {
      for (int i = 0; i < xyz.length; i++)
        xyz[i] = pos < lookup.length ? lookup[pos++] & 0xff : 255;

      palette[index] = this.baseCS.toRGB(xyz);            
      cmap[index] = (0xff000000 | palette[index][0] << 16) | (palette[index][1] << 8) | (palette[index][2]);// argb
    }
   
  }

  public int hival()
  {
    return hival;
  }

  @Override
  public boolean isIndexed()
  {
    return true;
  }

  public int[] rgb(int index)
  {
    return index > -1 && index < palette.length ? palette[index] : null;
  }

  @Override
  public ColorModel createColorModel(int bpc)
  {
    return new IndexColorModel(bpc, hival + 1, cmap, 0, DataBuffer.TYPE_BYTE, null);
  }

  @Override
  public BufferedImage decodeImage(PDFImage pdfImage)
  {
    int width = pdfImage.width();
    int height = pdfImage.height();
    int bpc = pdfImage.bitsPerComponent();
    try
    {
      Image3 image = PDF.ImageARGB(width, height);
      ZoubitReader reader = new ZoubitReader(pdfImage.bytes(), bpc);
      int v = 0;

      boolean rMask = pdfImage.hasMaskRange();
      for (int y = 0; y < height; y++)
      {
        reader.byteAlign();

        for (int x = 0; x < width; x++)
        {
          v = reader.read();
          boolean alpha = rMask && pdfImage.isInMaskRange(v);
          if (!alpha && v < cmap.length)
          {
            image.setRGB(x, y, cmap[v < cmap.length ? v : cmap.length - 1]);
          }
        }
      }
      // image.write(File3.userDesktop("/tmp/"+pdfImage.stream().reference()+".png"));

      return image;
    } catch (Exception e)
    {
      Log.warn(this, ".decodeImage - " + e);
      e.printStackTrace();
      return null;
    }
  }

  public String getName()
  {
    return "Indexed";
  }

  // public class ColorSpaceIndexed extends ColorSpace
  // {
  // public ColorSpaceIndexed()
  // {
  // super(ColorSpace.CS_GRAY, 1);
  // }
  //
  // @Override
  // public float[] toRGB(float[] comp)
  // {
  // return palette[(int) comp[0]];
  // }
  //
  // @Override
  // public float[] fromRGB(float[] rgbvalue)
  // {
  // return new float[1];
  // }
  //
  // @Override
  // public float[] fromCIEXYZ(float[] colorvalue)
  // {
  // return new float[1];
  // }
  //
  // @Override
  // public int getType()
  // {
  // return ColorSpace.CS_GRAY;
  // }
  //
  // @Override
  // public int getNumComponents()
  // {
  // return 1;
  // }
  //
  // @Override
  // public float[] toCIEXYZ(float[] colorvalue)
  // {
  // return new float[3];
  // }
  // }

  @Override
  public String toString()
  {
    String name = "";
    String type = "";
    String comp = "";
    if (baseCS != null)
    {
      name = baseCS.name;
      if (baseCS.colorSpace != null)
        type = baseCS.colorSpace.getType() + "";
      comp = baseCS.nbOfComponents + "";
    }
    return "ColorSpace[" + name + "]" + "\nHival[" + hival + "]" + "\nBaseCS[" + name + "]" + "\nBaseType[" + type + "]" + "\nBaseNbOfComponents["
        + comp + "]" + "\nLookupClass[" + lookupClass + "]" + "\nLookupBytes[" + Zen.Array.String(lookup, true) + "]" + "\nPalette["
        + Zen.Array.String(palette) + "]";
  }

  @Override
  public void paint(Graphics3 g, PDFDisplayProps props)
  {
    int size = (int) (16 * props.displayScaling);
    int x = 0;
    int y = 0;

    for (int i = 0; i < palette.length; i++)
    {
      g.setColor(new Color3(palette[i]));
      g.fill(new Rectangle3(x, y, size, size));

      x += size;
      if (x + size > g.width())
      {
        x = 0;
        y += size;
      }
    }

  }
}
