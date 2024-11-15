package sugarcube.formats.epub.structure.js;

import sugarcube.formats.ocd.objects.OCDPage;
import sugarcube.formats.ocd.objects.OCDPaintable;
import sugarcube.formats.ocd.objects.OCDPath;

public class JSOCDPage extends JS
{
  private OCDPage page;
  private String canvasID;

  public JSOCDPage(OCDPage page, String canvasID, int nbOfDecimals)
  {
    this.page = page;
    this.canvasID = canvasID;
    this.setNumberOfDecimals(nbOfDecimals);
    this.writeOCDPage(page);
  }

  public final JS writeOCDPage(OCDPage page)
  {
    this.writeOpeningFct("draw");
    this.writeContext(canvasID);
    for (OCDPaintable node : page.content().zOrderedGraphics())
      if (node.isPath())
        this.writeOCDPath((OCDPath)node);
//      else if (node.isImage())
//        this.addDrawable(new SVGImage(this, node.toImage()));
//      else if (node.isText())
//        this.addDrawable(new SVGText(this, node.toText()));
//      else if (node.isTextBlock())
//        this.addDrawable(new SVGTextBlock(this, node.toTextBlock())); 
    this.writeClosingBracket();
    return this;
  }
}
