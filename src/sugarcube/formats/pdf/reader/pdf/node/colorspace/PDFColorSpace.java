package sugarcube.formats.pdf.reader.pdf.node.colorspace;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.Map3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Image3;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.node.image.PDFImage;
import sugarcube.formats.pdf.reader.pdf.node.image.ZoubitReader;
import sugarcube.formats.pdf.reader.pdf.object.PDF;
import sugarcube.formats.pdf.reader.pdf.object.PDFObject;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;

public abstract class PDFColorSpace extends PDFNode
{
    protected ColorSpace colorSpace = null;
    protected String name;
    protected int nbOfComponents = 1;
    protected String resourceID = null;
    private float[] lastXYZ = new float[0];
    private float[] lastRGB = new float[0];
    private Map3<Integer, float[]> cmykMap = new Map3<>();

    protected PDFColorSpace(PDFNode parent, String name, int nbOfComponents)
    {
        super("ColorSpace", parent);
        this.name = name;
        this.nbOfComponents = nbOfComponents;
    }

    protected PDFColorSpace(PDFNode parent, String name)
    {
        super("ColorSpace", parent);
        this.name = name;
        // endsWith Gray or G
        this.nbOfComponents = name.endsWith("CMYK") ? 4 : name.endsWith("RGB") ? 3 : 1;
    }

    public boolean isCMYK()
    {
        return false;
    }

    public PDFPattern toPattern()
    {
        return this.isPattern() ? (PDFPattern) this : null;
    }

    public boolean isPattern()
    {
        return false;
    }

    public boolean isIndexed()
    {
        return false;
    }

    public String name()
    {
        return name;
    }

    public Color3 defaultColor()
    {
        return nbOfComponents == 4 ? Color3.WHITE : Color3.BLACK;
    }

    public ColorSpace colorSpace()
    {
        return colorSpace;
    }

    public BufferedImage decodeImage(PDFImage pdfImage)
    {
        int width = pdfImage.width();
        int height = pdfImage.height();
        int bpc = pdfImage.bitsPerComponent();
        // Log.debug(this, ".decodeImage - bpc=" + bpc + ", nbOfComponents=" +
        // this.nbOfComponents + ", cs=" + colorSpace.getType() + ", predictor="+
        // pdfImage.paramsPredictor);

        float[] blacks = pdfImage.decodeBlack();
        Image3 image = null;
        boolean rMask = pdfImage.hasMaskRange();

        byte[] data = pdfImage.bytes();
        // if(pdfImage.paramsPredictor>0)
        // {
        // try
        // {
        // data =
        // Predictor.Get(pdfImage.paramsPredictor, pdfImage.paramsColors,
        // pdfImage.paramsColumns, pdfImage.bpc).unpredict(data);
        // } catch (Exception e)
        // {
        // e.printStackTrace();
        // }
        // }

        if (bpc == 1 /* && this.nbOfComponents == 1 */)
            try
            {
                float[] black = new float[]
                        {0, 0, 0};
                float[] white = new float[]
                        {1, 1, 1};

                float[] black4 = new float[]
                        {0, 0, 0, 1};
                float[] white4 = new float[]
                        {1, 1, 1, 1};

                if (blacks.length > 0 && blacks[0] > 0.5)
                {
                    float[] tmp = black;
                    black = white;
                    white = tmp;
                }

                image = PDF.Image(width, height, rMask);

                ZoubitReader reader = new ZoubitReader(data, bpc);
                for (int y = 0; y < height; y++)
                {
                    reader.byteAlign();
                    for (int x = 0; x < width; x++)
                    {
                        int p = reader.read();
                        if (rMask)
                        {
                            if (!pdfImage.isInMaskRange(p))
                                image.setRGB(x, y, p == 0 ? black4 : white4);
                        } else
                        {
                            image.setRGB(x, y, p == 0 ? black : white);
                        }
                    }
                }
            } catch (Exception e)
            {
                Log.info(this, ".decodeImage - error: " + e);
                e.printStackTrace();
            }
        else if (bpc == 8 || bpc == 4)
            try
            {
                image = PDF.Image(width, height, rMask);
                ZoubitReader reader = new ZoubitReader(data, bpc);
                // Log.debug(this, ".decodeImage - bpc=" + bpc);
                for (int y = 0; y < height; y++)
                    // \nreader.byteAlign();
                    for (int x = 0; x < width; x++)
                    {
                        int[] p = reader.read(this.nbOfComponents, bpc == 4 ? 16 : 1);

                        boolean alpha = rMask && pdfImage.isInMaskRange(p);

                        for (int i = 0; i < p.length; i++)
                            if (2 * i < blacks.length && blacks[2 * i] > 0.5)
                                p[i] = 255 - p[i];

                        if (!alpha)
                            image.setPixel(x, y, this.toRGB(p));
                    }
            } catch (Exception e)
            {
                Log.info(this, ".decodeImage - error: " + e);
                e.printStackTrace();
            }

        return image;
    }

    public ColorModel createColorModel(int bpc)
    {
        // Log.debug(this, ".createColorModel - bpc=" + bpc + ", colorSpace=" +
        // this.name+", nbOfComponents="+this.nbOfComponents);
        if (nbOfComponents == 1)
            return new ComponentColorModel(colorSpace(), Zen.Array.Ints(bpc), false, false, 1, 0);
        else if (nbOfComponents == 4)
            return new ComponentColorModel(colorSpace(), Zen.Array.Ints(bpc, bpc, bpc, bpc), false, false, 1, 0);
        else if (nbOfComponents == 3)
            return new ComponentColorModel(colorSpace(), Zen.Array.Ints(bpc, bpc, bpc), false, false, 1, 0);

        Log.warn(this, ".createColorModel - wrong number of components: " + nbOfComponents);
        return null;
    }

    public static PDFColorSpace defaultCS(PDFNode parent)
    {
        return new DeviceCS(parent, "DeviceRGB");
    }

    public static PDFColorSpace instance(PDFNode parent, String name)
    {
        try
        {
            if (name.endsWith("Gray") || name.endsWith("RGB") || name.endsWith("CMYK") || name.equals("G"))
                return new DeviceCS(parent, name);
            else if (name.equals("Pattern"))
                return new PDFPattern(parent);// Pattern is set to an empty pattern
                // flag, still it has to be really
                // instanciated !
            else if (parent.page().resources().colorSpaces.containsKey(name))
                return parent.page().resources().colorSpaces.get(name);
        } catch (Exception e)
        {
        }

        Log.info(PDFColorSpace.class, ".instance - new color space: " + name);
        return new DeviceCS(parent, "DeviceRGB");
    }

    public static PDFColorSpace instance(PDFNode parent, String resourceID, PDFObject csObject)
    {
        PDFColorSpace cs = null;
        csObject = csObject.unreference();
        if (csObject.isPDFName())
            cs = instance(parent, csObject.toPDFName().stringValue());
        else if (csObject.isPDFArray())
        {
            // CalCMYK has never been released

            String space = csObject.get(0).toPDFName().stringValue();
            if (space.equals("ICCBased"))
                cs = new ICCBased(parent, csObject.get(1).toPDFStream());
            else if (space.equals("CalCMYK") || space.equals("DeviceCMYK"))
                cs = new DeviceCS(parent, "DeviceCMYK");
            else if (space.equals("CalRGB"))
                cs = new CalRGB(parent, csObject.get(1).toPDFDictionary());
            else if (space.equals("CalGray"))
                cs = new CalGray(parent, csObject.get(1).toPDFDictionary());
            else if (space.equals("Lab"))
                cs = new Lab(parent, csObject.get(1).toPDFDictionary());
            else if (space.equals("Indexed") || space.equals("I"))
                cs = new Indexed(parent, csObject.toPDFArray());
            else if (space.equals("Separation"))
                cs = new Separation(parent, csObject.toPDFArray());
            else if (space.equals("DeviceN"))
                cs = new DeviceN(parent, csObject.toPDFArray());
            else if (space.equals("Pattern"))
                cs = new PDFPattern(parent);
            else
            {
                cs = new DeviceCS(parent, "DeviceRGB");
                Log.warn(PDFColorSpace.class, ".instance() - " + space + " not yet implemented");
            }
        } else
        {
            Log.warn(PDFColorSpace.class, ".instance - colorspace parsing error: " + csObject);
            cs = new DeviceCS(parent, "DeviceRGB");
        }

        if (cs.resourceID == null)
            cs.resourceID = resourceID;
        if (cs.reference == null)
            cs.reference = csObject.reference();

        return cs;
    }

    public int nbOfComponents()
    {
        return nbOfComponents;
    }

    public boolean isStandardCS()
    {
        return false;
    }

    protected float[] toMappedRGB(float... c)
    {
        try
        {
            if (colorSpace != null)
            {
                return colorSpace.toRGB(c);
            }
        } catch (Exception e)
        {
            Log.warn(this, ".toRGB - conversion problem: name=" + name + " input=" + Zen.Array.String(c) + " colorspace=" + colorSpace.getType());
            e.printStackTrace();
        }
        System.out.print(".");
        float[] rgb = new float[3];
        for (int i = 0; i < rgb.length && i < c.length; i++)
            rgb[i] = c[i < c.length ? i : 0];
        return rgb;
    }

    public int[] toRGB(int[] c)
    {
        float[] f = new float[c.length];
        for (int i = 0; i < f.length; i++)
            f[i] = c[i] / 255f;
        f = toRGB(f);
        c = new int[f.length];
        for (int i = 0; i < f.length; i++)
            c[i] = (int) (0.5 + f[i] * 255f);
        return c;
    }

    public float[] toRGB(float... c)
    {
        // very slow generic Adobe CMYK colorspace
//    int xyz = ((int) (255 * c[0] + 0.5)) | (c.length > 1 ? (int) (255 * c[1] + 0.5) << 8 : 0) | (c.length > 2 ? (int) (255 * c[2] + 0.5) << 16 : 0)
//        | (c.length > 3 ? (int) (255 * c[3] + 0.5) << 24 : 0);

//    float[] rgb = this.cmykMap.get(xyz);
//    if (rgb != null)
//    {
//      System.out.println(".toRGB - map: " + Arrays.toString(rgb));
//      return rgb;
//    }

        if (lastXYZ.length == c.length)
        {
            boolean last = true;
            for (int i = 0; i < c.length; i++)
                if (c[i] != lastXYZ[i])
                {
                    last = false;
                    break;
                }
            if (last)
                return lastRGB;
        }

        this.lastRGB = this.toMappedRGB(this.lastXYZ = c);
        for (int i = 0; i < lastRGB.length; i++)
        {
            if (lastRGB[i] < 0)
                lastRGB[i] = 0;
            else if (lastRGB[i] > 1f)
                lastRGB[i] = 1f;
        }
//
//  
//      System.out.println(".toRGB - saving: " + Arrays.toString(c));
//      if(cmykMap.size()>100000)
//        cmykMap.removeFirst();
//      cmykMap.put(xyz, lastRGB);


        return lastRGB;
    }

    public Color3 toColor(float... c)
    {
        return new Color3(toRGB(c));
    }

    @Override
    public String sticker()
    {
        return (resourceID == null ? "" : resourceID + " » ") + type + "[" + name + "] " + (this.reference == null ? "" : this.reference);
    }

    @Override
    public String toString()
    {
        return type + "[" + name + "]";
    }
}
