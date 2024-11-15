package sugarcube.formats.epub.structure.xhtml;

public class HTMLTextLayer extends HTMLDiv
{
  public HTMLTextLayer(HTMLPage page, String text, float fs, float x, float y)
  {
    super();

//    this.setID(id);
//    if (controls)
//      this.addAttributes("controls", "controls");
    this.addAttributes("class", "layer", "style", "top:" + (y - fs) + "px; left:" + x + "px; font-size: " + fs + "px; ");

    this.addChild(new HTMLCData(text));
  }
}
