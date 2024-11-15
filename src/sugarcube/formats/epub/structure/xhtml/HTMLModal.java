package sugarcube.formats.epub.structure.xhtml;

public class HTMLModal extends HTMLOverlay
{
  

  public HTMLModal(HTMLNode node)
  {
    super("modal-" + node.id(), true);
    add(node);
    addCloseButton();
  }
  
  public void addCloseButton()
  {
    this.add(new HTMLModalClose());
  }

  public static HTMLModal Get(HTMLNode node)
  {
    return new HTMLModal(node);
  }
}
