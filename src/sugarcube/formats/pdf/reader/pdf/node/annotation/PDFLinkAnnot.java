package sugarcube.formats.pdf.reader.pdf.node.annotation;

import sugarcube.formats.pdf.reader.Dexter;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.node.actions.PDFAction;
import sugarcube.formats.pdf.reader.pdf.object.PDFArray;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.Reference;

public class PDFLinkAnnot extends PDFAnnotation
{
  protected PDFAction action = null;
  protected Reference gotoRef = null;
  protected String gotoURI = null;
  protected String gotoAction = null;
  protected String nameAction = null;
  
  public PDFLinkAnnot(PDFNode parent, PDFDictionary map)
  {
    this(parent, map, Dexter.LINK);
  }

  protected PDFLinkAnnot(PDFNode parent, PDFDictionary map, String subType)
  {
    super(parent, subType, map);

    this.reference = map.reference();

    if (map.has("A"))
    {
      action = PDFAction.Get(this, map.get("A").toPDFDictionary());
      if (action.isGotoAction())
        gotoRef = action.asGotoAction().gotoRef();
      else if (action.isURIAction())
        gotoURI = action.asURIAction().gotoURI();
      else if (action.isGotoRAction())
        gotoAction = action.asGotoRAction().gotoAction();
      else if (action.isJavascriptAction())      
        gotoRef = action.asJavascriptAction().gotoRef();
      else if(action.isNamedAction())
        nameAction = action.asNamedAction().name();      
    }
    if (map.has("Dest")) 
    {
      PDFArray dest = map.get("Dest").toPDFArray();
      if (dest.isValid())
        gotoRef = dest.get(0).toPDFPointer().get();
    }
  }

  public Reference gotoRef()
  {
    return gotoRef;
  }

  public boolean hasGotoRef()
  {
    return gotoRef != null;
  }

  public boolean hasGotoURI()
  {
    return gotoURI != null && !gotoURI.isEmpty();
  }

  public String gotoURI()
  {
    return this.gotoURI;
  }
  
  public String nameAction()
  {
    return this.nameAction;
  }

  @Override
  public String sticker()
  {
    return "LinkAnnot" + reference();
  }

  @Override
  public String toString()
  {
    return "LinkAnnot" + reference() + "\nBounds" + bounds + "\nGotoRef[" + gotoRef + "]" + "\nGotoURI[" + gotoURI + "]" + "\nGotoAction["
        + gotoAction + "]" + "\nContents[" + contents + "]";
  }
}
