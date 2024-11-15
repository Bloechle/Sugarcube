package sugarcube.formats.pdf.reader.pdf.node.image;

import sugarcube.common.data.collections.Unicodes;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Image3;
import sugarcube.formats.pdf.reader.pdf.object.PDF;

import java.awt.image.BufferedImage;

/*
 * Stencil Masking An image mask (an image XObject whose ImageMask entry is true) is a monochrome image in which each
 * sugarcube.app.sample is specified by a single bit. However, instead of being painted in opaque black and white, the image mask is
 * treated as a stencil mask that is partly opaque and partly transparent. Sample values in the image do not represent
 * black and white pixels; rather, they designate places on the page that should either be marked with the current color
 * or masked out (not marked at all). Areas that are masked out retain their former contents. The effect is like
 * applying paint in the current color through a cut-out stencil, which lets the paint reach the page in some places and
 * masks it out in others.
 * 
 * An image mask differs from an ordinary image in the following significant ways: • The image dictionary does not
 * contain a ColorSpace entry because sugarcube.app.sample values represent masking properties (1 bit per sugarcube.app.sample) rather than colors.
 * • The value of the BitsPerComponent entry must be 1. • The Decode entry determines how the source samples are to be
 * interpreted. If the Decode array is [ 0 1 ] (the default for an image mask), a sugarcube.app.sample value of 0 marks the page with
 * the current color, and a 1 leaves the previous contents unchanged. If the Decode array is [ 1 0 ], these meanings are
 * reversed. One of the most important uses of stencil masking is for painting character glyphs represented as bitmaps.
 * Using such a glyph as a stencil mask transfers only its “black” bits to the page, leaving the “white” bits (which are
 * really just background) unchanged. For reasons discussed in Section 5.5.4, “Type 3 Fonts,” an image mask, rather than
 * an image, should almost always be used to paint glyph bitmaps.
 */
public class StencilDecoder
{
  public static BufferedImage decode(PDFImage pdfImage)
  {
    int width = pdfImage.width;
    int height = pdfImage.height;

    Color3 color = Color3.BLACK;
    Color3 alpha = Color3.TRANSPARENT;

    boolean oneIsAlpha = pdfImage.decode[0] < 0.5;
    byte[] stream = pdfImage.bytes();
    boolean doAddByteOffset = stream.length > 0 && stream[0] == Unicodes.ASCII_SP;
    Image3 image = PDF.ImageARGB(width, height);

    //Log.debug(StencilDecoder.class, ".decode - w=" + width + ", h=" + height + ", data=" + Zen.Array.toString(pdfImage.decode));
    ZoubitReader reader = new ZoubitReader(stream, 1);
    if (doAddByteOffset)
      reader.read(8);
    for (int y = 0; y < height; y++)
    {
      reader.byteAlign();
      for (int x = 0; x < width; x++)        
        image.setPixel(x, y, reader.read() > 0 ? (oneIsAlpha ? alpha : color) : (oneIsAlpha ? color : alpha));
    }
    return image;
  }
}
