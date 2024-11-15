package sugarcube.formats.epub.structure.xhtml;

public class W3Modal extends HTMLDiv
{
  public HTMLDiv content = new HTMLDiv("class", "w3-modal-content sc-modal-content");
  public HTMLDiv container = new HTMLDiv("class", "w3-container sc-modal-container");

  public W3Modal(String id, String text)
  {
    super();
    this.idClass(id, "w3-modal sc-modal sc-transparent sc-none");

    this.addChild(content);
    content.addChild(container);
    String cdata =  "<span onclick=\"classAndDelay3('"+id+"','sc-transparent -sc-opaque','sc-none -sc-block',500)\" class=\"w3-button w3-display-topright sc-modal-close\">&#215;</span>";
    container.setCData(cdata+"\n<p>"+text+"</p>", false);
  }
  
  public static String NoteReference(String id) 
  {
    return "<sup><span onclick=\"classAndDelay3('" +id + "','-sc-none sc-block', '-sc-transparent sc-opaque',100)\" class=\"note-ref\">(note)</span></sup>";
  }

}
