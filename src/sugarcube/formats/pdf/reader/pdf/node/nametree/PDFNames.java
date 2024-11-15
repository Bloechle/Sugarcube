package sugarcube.formats.pdf.reader.pdf.node.nametree;

import sugarcube.common.data.collections.StringMap;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.node.actions.PDFDest;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.PDFObject;

public class PDFNames extends PDFNode implements PDFNameTree.Listener
{
  public PDFNameTree tree;
  public StringMap<PDFDest> dests = new StringMap<>();

  public PDFNames(PDFNode parent, PDFDictionary map)
  {
    super("Names", parent);    
    this.reference = map.reference();
    if (map.has("Dests"))
    {      
      this.add(this.tree = new PDFNameTree(this, map.get("Dests").toPDFDictionary(), this));
    }    
  }
  
  public PDFDest dest(String name)
  {
    return dests.get(name);
  }

  @Override
  public void visitLeaf(PDFNameTree node, String name, PDFObject leaf)
  {
    this.dests.put(name,  new PDFDest(this, name, leaf));    
  }

  @Override
  public String sticker()
  {
    return type + " " + reference();
  }

  @Override
  public String toString()
  {
    return type + reference() +"\n"+dests;
  }



}