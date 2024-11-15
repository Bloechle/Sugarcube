package sugarcube.formats.pdf.reader.pdf.node.nametree;

import sugarcube.common.data.collections.StringMap;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.object.PDFArray;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.PDFObject;

public class PDFNameTree extends PDFNode<PDFNameTree>
{
  public interface Listener
  {
    public void visitLeaf(PDFNameTree node, String name, PDFObject leaf);
  }
  public StringMap<PDFObject> names = new StringMap<>();

  public PDFNameTree(PDFNode parent, PDFDictionary map, Listener listener)
  {
    super("NameTree", parent);
    this.reference = map.reference();
    if (map.has("Kids"))
    {
      PDFArray kids = map.get("Kids").toPDFArray();
      for (PDFObject kid : kids)
      {
        this.add(new PDFNameTree(this, kid.toPDFDictionary(), listener));
      }
    }
    if (map.has("Names"))
    {
      PDFArray array = map.get("Names").toPDFArray();
      PDFObject name = null;
      for (PDFObject leaf : array)
      {
        if (name == null)
          name = leaf;
        else
        {          
          names.put(name.stringValue(), leaf);
          listener.visitLeaf(this, name.stringValue(), leaf);
          name = null;
        }
      }
    }
  }

  @Override
  public String sticker()
  {
    return type + " " + reference();
  }

  @Override
  public String toString()
  {
    return sticker() + "\n" + names;
  }
}
