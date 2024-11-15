package sugarcube.formats.pdf.reader.pdf.node;

import sugarcube.formats.pdf.reader.Dexter;

public class PDFGhost extends PDFPaintable
{

  // not yet used, but created for debugging when rasterizing, in order to keep
  // a ghost version of the original nodes in the document tree
  private PDFGhost(PDFNode parent)
  {
    super(Dexter.GHOST, parent);
  }

  @Override
  public String sticker()
  {
    return type;
  }
}
