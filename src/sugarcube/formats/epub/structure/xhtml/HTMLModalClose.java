package sugarcube.formats.epub.structure.xhtml;

public class HTMLModalClose extends HTMLDiv
{
  public HTMLModalClose()
  {
    this.classname("sc-pointer sc-modal-close");    
    this.addSpan("&#215;").style("fs:48px; c:white; text-shadow: 0px 0px 2px #000000;");
  }
}
