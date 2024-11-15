package sugarcube.formats.pdf.reader.pdf.node.image;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.Array3;
import sugarcube.common.data.collections.Str;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Image3;
import sugarcube.common.graphics.PNGImage;
import sugarcube.common.system.io.File3;
import sugarcube.formats.pdf.reader.Dexter;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.codec.Codec;
import sugarcube.formats.pdf.reader.pdf.node.*;
import sugarcube.formats.pdf.reader.pdf.node.colorspace.PDFColorSpace;
import sugarcube.formats.pdf.reader.pdf.node.colorspace.PDFPattern;
import sugarcube.formats.pdf.reader.pdf.object.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class PDFImage extends PDFPaintable
{
  public static int DEBUG_COUNTER = 0;
  public static final String INLINE_RESOURCE_ID = "inline";
  public static final int BI_BINARY = BufferedImage.TYPE_BYTE_BINARY;
  public static final int BI_GRAY = BufferedImage.TYPE_BYTE_GRAY;
  public static final int BI_RGB = BufferedImage.TYPE_INT_RGB;
  public static final int BI_ARGB = BufferedImage.TYPE_INT_ARGB;
  private PDFState state = null;
  protected transient PDFInstr instr;
  private PDFMatrix um; // user space matrix
  private PDFClip clip;
  protected PDFStream stream;
  protected PDFColorSpace cs;
  protected PDFColor fillColor;
  protected int width; // image width
  protected int height;
  public int bpc;
  protected String intent;
  protected Codec filter;
  protected float[] decode;
  protected boolean imageMask;
  protected float[] maskRange = null;
  protected PDFImage mask;
  protected PDFImage smask;
  protected float[] smaskMatte;
  public int paramsColors;
  public int paramsColumns;
  public int paramsPredictor;
  protected PDFContent content;
  protected String resourceID;
  protected boolean isSMask = false;
  protected boolean isMask = false;
  protected float baseAlpha = 1f;
  public String[] blendModes = null;
  public PNGImage rasterizedXObject = null;

  private PDFImage(PDFImage pdfImage)
  {
    super(Dexter.IMAGE, pdfImage);
  }

  public PDFImage(PDFNode parent, Image3 image, float x, float y, float scale)
  {
    super(Dexter.IMAGE, parent);
    this.reference = parent.reference();
    this.rasterizedXObject = image.PNGImage();
    this.width = image.width();
    this.height = image.height();
    this.um = new PDFMatrix(1.0 / width, 0, 0, 1.0 / height, 0, 1);
    this.tm = new PDFMatrix(width / scale, 0, 0, height / scale, x, y);
  }

  public PDFImage(PDFNode parent, String resourceID, PDFStream stream)
  {
    super(Dexter.IMAGE, parent);
    this.reference = stream.reference();
    this.resourceID = resourceID;
    if (this.document().content() != null)
      this.state = document().content().state().copy();

    this.width = stream.get("Width", "W").intValue(-1);
    this.height = stream.get("Height", "H").intValue(-1);
    this.um = new PDFMatrix(1.0 / width, 0, 0, 1.0 / height, 0, 1);
    this.tm = new PDFMatrix(width, 0, 0, height, 0, 0);
    this.clip = null;

    this.stream = stream;
    this.bpc = stream.get("BitsPerComponent", "BPC").intValue(8);
    // TODO - improve
    if (this.stream.contains("ColorSpace") || this.stream.contains("CS"))
      this.cs = PDFColorSpace.instance(this, null, this.stream.get("ColorSpace", "CS"));
    else
      this.cs = PDFColorSpace.instance(this, "DeviceRGB");

    int nb = this.cs.nbOfComponents();
    float[] decodeDefault = new float[nb * 2];
    for (int i = 0; i < decodeDefault.length; i++)
      decodeDefault[i] = i % 2;
    this.decode = stream.get("Decode", "D").floatValues(decodeDefault);

    this.filter = stream.lastFilter();
    this.intent = stream.get("Intent").stringValue("RelativeColorimetric");
    this.imageMask = stream.get("ImageMask", "IM").booleanValue(false);

    PDFStream maskStream = null;
    PDFObject maskObj = stream.has("Mask") ? stream.get("Mask").unreference() : null;
    if (maskObj != null)
    {
      if (maskObj.isPDFArray())
      {
        this.maskRange = maskObj.toPDFArray().floatValues(null);
      } else if (maskObj.isPDFStream())
      {
        if ((maskStream = maskObj.toPDFStream()) != null && maskStream.isValid())
        {
          this.mask = new PDFImage(this, "Mask[" + this.resourceID + "]", maskStream);
          this.mask.isMask = true;
          //2020.04.28 - since we resize this image to the mask dimensions, we need to adapt um
          this.um = this.mask.um;
          add(this.mask);

//          Log.debug(this, " - img="+debugString()+" mask="+mask.debugString());
        }
      }
    }

    maskStream = stream.get("SMask").toPDFStream();
    if (maskStream.isValid())
    {
      this.smask = new PDFImage(this, "SMask[" + this.resourceID + "]", maskStream);
      this.smask.isSMask = true;
      this.um = this.smask.um;
      add(this.smask);
    }

    if (stream.has("Matte"))
      this.smaskMatte = stream.get("Matte").toPDFArray().floatValues();

    if (stream.has("DecodeParms"))
    {
      PDFDictionary params = stream.get("DecodeParms").toPDFDictionary();
      this.paramsColors = params.get("Colors").intValue(1);
      this.paramsColumns = params.get("Columns").intValue(0);
      this.paramsPredictor = params.get("Predictor").intValue(0);
      // Log.debug(this, " - DecodeParms: Colors="+paramsColors+",
      // Columns="+paramsColumns+", Predictor="+paramsPredictor);
    }

//    new Image3(decode()).write(File3.desktop("image"+(++DEBUG_COUNTER)+".png"));

    add(this.cs);
  }
  
  public String blendMode()
  {
    return blendModes == null || blendModes.length == 0 ? null : blendModes[0];
  }

  public boolean isInline()
  {
    return this.resourceID.equals(INLINE_RESOURCE_ID);
  }

  public float[] decodeBlack()
  {
    return decode;
  }

  @Override
  public PDFImage instance(PDFContent content, PDFInstr instr, PDFContext context)
  {
    PDFState cState = document().content().state().copy();
    PDFImage image = copy();
    image.state = cState;

    image.tm = cState.ctm().copy();

    // don't ask why !
    if (content.isSubtype(PDFContent.TYPE_TILINGPATTERN))
    {
      Log.debug(this, ".instance - ctm=" + image.tm + ", page=" + this.page().number());
      if (image.tm.isIdentity())
        image.tm = image.tm.concat(new PDFMatrix(width, 0, 0, height, 0, 0));
      else
        image.tm = new PDFMatrix(image.tm.sx(), image.tm.hy(), image.tm.hx(), -image.tm.sy(), image.tm.x(), height - image.tm.y());
    }

    image.fillColor = cState.fillColor();
    image.clip = cState.clip();
    image.content = content;
    image.instr = instr;
    image.baseAlpha = (float) content.baseFillAlpha;
    // Log.debug(this,
    // ".instance - "+this.reference()+":
    // bm="+Zen.A.toString(content.baseBlendModes)+":
    // "+Zen.A.toString(cState.blendModes()));
    image.blendModes = content.blendModes(cState.blendModes());
    image.marks.clear();
    image.marks.setAll(content.marks());// in reverse order... i.e., closest
                                        // nesting first
    return image;
  }

  @Override
  public StreamLocator streamLocator()
  {
    return instr == null ? null : instr.streamLocator();
  }

  public PDFState imageState()
  {
    return this.state;
  }

  public BufferedImage image()
  {
    if (this.rasterizedXObject != null)
      return this.rasterizedXObject.image();
    BufferedImage image = this.decode();
    if (smask != null)
      image = smaskImage(image);
    else if (mask != null)
      image = maskImage(image);
    if (imageMask)
    {
      image = stencilMask(image);
    }

    // Log.debug(CCITTDecoder.class,
    // ".decode - "+stream.reference()+": smask="+(smask!=null)+",
    // mask="+(mask!=null)+", imageMask="+imageMask);

    if (this.state != null && this.state.fillAlpha() < 1.0)
      return new Image3(image, this.state.fillAlpha());
    else if (baseAlpha < 1.0)
      return new Image3(image, baseAlpha);
    else
      return image;
  }

  public static Image3 errorImage(int width, int height)
  {
    Image3 image = PDF.ImageARGB(width, height);
    Graphics3 g = image.graphics();
    g.clearChessBoard(Color3.RED.alpha(0.1), Color3.GREEN.alpha(0.1), 16);
    g.dispose();
    return image;
  }

  public boolean isFilter(String... names)
  {
    return filter != null && filter.is(names);
  }

  public boolean hasMaskRange()
  {
    return this.maskRange != null && this.maskRange.length > 0;
  }

  public boolean isInMaskRange(int... c)
  {
    for (int i = 0; i < c.length && 2 * i + 1 < maskRange.length; i++)
    {
      if (c[i] < maskRange[2 * i] || c[i] > maskRange[2 * i + 1])
        return false;
    }
    return true;
  }

  public BufferedImage decode()
  {
    BufferedImage bi = null;
    try
    {
      if (Dexter.DISABLE_IMAGES)
      {
        Log.debug(this, ".decode - IMAGES DISABLED");
        return PDF.ImageRGB(1, 1);
      }

      if (isFilter("JPXDecode", "JPX"))
        bi = JPXDecoder.Decode(this);
      else if (isFilter("DCTDecode", "DCT"))
        bi = JPEGDecoder.Decode(this);
      else if (isFilter("CCITTFaxDecode", "CCF"))
        bi = CCITTDecoder.Decode(this);
      else if (isFilter("JBIG2Decode"))
        bi = JBIG2.Decode(this);
      else
        bi = this.imageMask ? StencilDecoder.decode(this) : RawDecoder.decode(this);
    } catch (Exception e)
    {
      Log.warn(this, ".decode - image reading hiccup:" + e.getMessage());
      e.printStackTrace();
      bi = errorImage(width, height);
    }

    // if (bi != null)
    // try
    // {
    // String name = (isSMask ? "mask-" : "image-") + (filter == null ?
    // "nofilter" : filter.name) + cs.name();
    // ImageIO.write(bi, "png", File3.Desk("tmp/" + name + "-" +
    // (DEBUG_COUNTER++) + ".png"));
    // } catch (Exception e)
    // {
    // e.printStackTrace();
    // }

    return bi;
  }

  private BufferedImage resizeImage(BufferedImage bi, int w, int h)
  {
    if (bi.getWidth() == w && bi.getHeight() == h)
      return bi;

    Image3 image = PDF.Image(w, h, bi.getTransparency() != BufferedImage.OPAQUE);
    Graphics3 g = image.graphics();
    g.draw(bi, Transform3.scaleInstance(w / (double) bi.getWidth(), h / (double) bi.getHeight()));
    return image;
  }

  public BufferedImage stencilMask(BufferedImage src)
  {
    // Log.debug(this, ".stencilMask");
    Image3 res = PDF.ImageARGB(src);
    try
    {
      // ImageIO.write(src, "png", File3.userDesktop("tmp/" + src.getWidth() +
      // "x" + src.getHeight() + ".png"));

      int color = fillColor == null ? Color3.BLACK.argb() : fillColor.color().argb();
      int alpha = Color3.TRANSPARENT.argb();
      Image3 pattern = null;
      if (fillColor != null && fillColor.isPattern())
      {

        // tm = tm.concat(new PDFMatrix(width, 0, 0, height, 0, 0));
        PDFMatrix ptm = tm;
        pattern = ((PDFPattern) fillColor.colorSpace()).image(new Rectangle3(0, 0, width, height), new PDFDisplayProps(), ptm.transform(), false);
      }

      // Log.debug(this, ".stencilMask - w=" + src.getWidth() + ", h=" +
      // src.getHeight());
      // if (pattern != null)
      // res = new Image3(src.getWidth(), src.getHeight(),
      // BufferedImage.TYPE_INT_ARGB);
      for (int y = 0; y < res.getHeight(); y++)
        for (int x = 0; x < res.getWidth(); x++)
        {
          int argb = src.getRGB(x, y);
          if (((argb >> 24) & 255) < 128)
            res.setRGB(x, y, argb);
          else if (pattern == null)
          {
            res.setRGB(x, y, (argb & 255) > 128 ? alpha : color);
          } else
            res.setRGB(x, y, (argb & 255) > 128 ? alpha : pattern.getRGB(x, y));
        }

      if (false && pattern != null)
        try
        {
          String prefix = src.getWidth() + "x" + src.getHeight();
          ImageIO.write(src, "png", File3.desktop("dexter/" + prefix + "-src.png"));
          ImageIO.write(pattern, "png", File3.desktop("dexter/" + prefix + "-pat.png"));
          ImageIO.write(res, "png", File3.desktop("dexter/" + prefix + "-res.png"));
        } catch (IOException ex)
        {
          ex.printStackTrace();
        }
    } catch (Exception e)
    {
      res = null;      
      e.printStackTrace();
    }
    return res == null ? src : res;
  }

  private BufferedImage smaskImage(BufferedImage src)
  {
    // todo - resize mask instead of image, since image is affected by
    // transform?!?
    // todo - apply matte when defined !

    boolean opInverse = false;

    float[] matte = smask.smaskMatte;

    boolean preblended = matte != null && matte.length > 0;

//    Log.debug(this, ".smaskImage - inverse=" + opInverse + ", " + Arrays.toString(smaskMatte));

    BufferedImage m = this.smask.image();// resizeImage(this.smask.image(),
                                         // src.getWidth(), src.getHeight());
    src = resizeImage(src, m.getWidth(), m.getHeight());
    Image3 res = PDF.ImageARGB(src);

    // if (!this.smask.hasSMaskMatte())
    // {
    for (int y = 0; y < res.getHeight(); y++)
      for (int x = 0; x < res.getWidth(); x++)
      {
        res.setRGB(x, y, src.getRGB(x, y));
        int a = m.getRaster().getSample(x, y, 0);

        double alpha = Math.pow(a / 255.0, 1.5) * 255;
        a = (int) alpha;
        if (a < 0)
          a = 0;
        else if (a > 255)
          a = 255;
        if (preblended && a > 0)
          for (int i = 0; i < 3; i++)
          {
            int c = src.getRaster().getSample(x, y, i);
            c = (int) (((c - matte[0])) / (alpha / 255) + matte[0]);
            res.getRaster().setSample(x, y, i, c);
          }
        // double g = Math.pow(a/255.0, 2.2);
        // a = (int)(g*255+0.5);
        res.getRaster().setSample(x, y, 3, a);
      }
    return res;

    // } else
    // return src;
  }

  private BufferedImage maskImage(BufferedImage src)
  {
    BufferedImage m = this.mask.image();// resizeImage(this.mask.image(),
                                        // src.getWidth(), src.getHeight());
    src = resizeImage(src, m.getWidth(), m.getHeight());

    Image3 res = PDF.ImageARGB(src);

    for (int y = 0; y < res.getHeight(); y++)
      for (int x = 0; x < res.getWidth(); x++)
        if (((m.getRGB(x, y) >> 8) & 0xffffff) > 0)
          res.setRGB(x, y, src.getRGB(x, y));
        else
          res.getRaster().setPixel(x, y, new double[]
          { 0, 0, 0, 0 });
    return res;
  }

  public PDFClip clip()
  {
    return clip;
  }

  public int width()
  {
    return this.width;
  }

  public int height()
  {
    return this.height;
  }

  public int bitsPerComponent()
  {
    return this.bpc;
  }

  public PDFColorSpace colorspace()
  {
    return this.cs;
  }

  public PDFColor getFillColor()
  {
    return fillColor;
  }

  public PDFStream stream()
  {
    return this.stream;
  }

  public boolean hasDecodeParam(String name)
  {
    return filter != null && filter.parms != null && filter.parms.has(name);
  }

  public PDFDictionary decodeParms()
  {
    return filter == null || filter.parms == null ? new PDFDictionary(stream) : filter.parms;
  }

  public byte[] bytes()
  {
    return this.stream.byteValues();
  }

  @Override
  public String sticker()
  {
    return (resourceID == null ? "" : resourceID + " » ") + type + "[" + (filter == null ? "XObject" : filter.name) + "] " + reference;
  }

  public PDFMatrix um()
  {
    return um;
  }

  public PDFMatrix concat()
  {
    return um.concat(tm);
  }

  @Override
  public void paint(Graphics3 g, PDFDisplayProps props)
  {
    g.setComposite(1, blendModes);
    // g.resetComposite(1);

    if (blendModes != null)
    {
      String msg = this.baseAlpha + ", " + Str.Array(blendModes);
      if (!msg.equals("1.0, Normal"))
        Log.debug(this, ".paint - composite: " + msg);
    }

    if (props.displayImages)
    {
      // Log.debug(this, ".paint - tm=" + tm + ", props.minX=" + props.minX() +
      // ", props.maxY=" + props.maxY());
      Transform3 transform = concat().reverse(props.minX(), props.maxY()).transform();
      g.draw(props.enableColors ? image() : PDF.ImageRGB(width, height), transform);
    }

    if (props.highlightImages)
    {
      Transform3 transform = um.concat(tm).reverse(props.minX(), props.maxY()).transform();
      g.setClip(null);
      g.setColor(new Color(0, 150, 0, 150));
      g.fill(transform.transform(new Rectangle3(0, 0, width, height)));
    }
    g.resetComposite(1);
  }

  public String debugString()
  {
    return this.reference()+" ("+width+"x"+height+")"+" "+tm();
  }

  @Override
  public String toString()
  {
    return sticker() + "\nClip[" + clip + "]" + "\nWidth[" + width + "]" + "\nHeight[" + height + "]" + "\nBPC[" + bpc + "]" + "\nImageMask["
        + imageMask + "]" + "\nMask[" + mask + "]" + "\nSMask[" + smask + "]" + "\nSMaskMatte[" + Zen.Array.String(smaskMatte) + "]" + "\nBlendModes["
        + Zen.Array.String(blendModes) + "]" + "\nUM" + um + "\nTM" + tm + "\nDecode[" + Zen.Array.String(this.decode) + "]" + "\nDecodeParams"
        + (filter == null || filter.parms == null ? "[null]" : filter.parms) + "\nColorSpace[" + (cs == null ? "null" : cs.name()) + "]"
        + "\nOperator[\n" + instr + "]" + "\nMarks" + this.marks + "\n" + this.state;
  }

  public PDFImage copy()
  {
    PDFImage image = new PDFImage(this);
    image.state = state;
    image.stream = this.stream;
    image.width = this.width;
    image.height = this.height;
    image.bpc = this.bpc;
    image.cs = this.cs;
    image.decode = this.decode;
    image.filter = this.filter;
    image.intent = this.intent;
    image.imageMask = this.imageMask;
    // image.imageMaskApplied = this.imageMaskApplied;
    image.maskRange = this.maskRange;
    image.mask = this.mask;
    image.smask = this.smask;
    image.smaskMatte = this.smaskMatte;

    image.paramsColors = this.paramsColors;
    image.paramsColumns = this.paramsColumns;
    image.paramsPredictor = this.paramsPredictor;
    // image.isSMask = this.isSMask;
    // image.isMask = this.isMask;
    image.baseAlpha = this.baseAlpha;
    image.blendModes = Array3.copy(this.blendModes);
    image.um = this.um;
    if (state != null)
    {
      image.tm = state.ctm();
      image.fillColor = state.fillColor();
      image.clip = state.clip();
    }
    image.content = this.content;
    image.instr = this.instr;
    image.resourceID = this.resourceID;
    image.marks = this.marks;
    image.reference = this.reference;
    return image;
  }
}
