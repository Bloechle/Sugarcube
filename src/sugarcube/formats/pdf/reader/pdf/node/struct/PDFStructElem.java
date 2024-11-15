package sugarcube.formats.pdf.reader.pdf.node.struct;

import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Stringer;
import sugarcube.formats.pdf.reader.pdf.node.PDFDocument;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.PDFObject;
import sugarcube.formats.pdf.reader.pdf.object.Reference;

public class PDFStructElem extends PDFNode<PDFStructElem>
{
  public PDFStructTreeRoot root;
  public int index = -1;
  public String treeID;
  public String name;// P, H2, etc.
  public Reference page;
  public List3<Integer> mcids = new List3<Integer>();
  public List3<PDFObjectPointer> refs = new List3<PDFObjectPointer>();
  public List3<PDFMarkPointer> marks = new List3<PDFMarkPointer>();

  public PDFStructElem(PDFDocument parent, PDFDictionary map)
  {
    super("StructTreeRoot", parent);
  }

  public PDFStructElem(PDFStructElem parent, PDFDictionary map)
  {
    super("StructElem", parent);
    this.root = parent.root;
    this.index = ++root.index;
    this.root.indexes.add(this.index, this);
    this.reference = map.reference();
    this.name = map.get("S").stringValue("");
    this.treeID = map.get("ID").stringValue(null);
    if (map.has("Pg"))
      this.page = map.get("Pg").toPDFPointer().get();
    else
      this.page = parent.page;
    this.parseTree(map);

    root.populatePageStruct(this);
  }

  public boolean isName(String... names)
  {
    for (String n : names)
      if (this.name.equalsIgnoreCase(n))
        return true;
    return false;
  }

  protected void parseTree(PDFDictionary map)
  {
    if (map.has("K"))
    {
      PDFObject k = map.get("K").unreference();
      if (k.isPDFDictionary())
        this.addDictionaryRef(k.toPDFDictionary());
      else if (k.isPDFArray())
        for (PDFObject ko : k.toPDFArray())
          if (ko.unreference().isPDFDictionary())
            this.addDictionaryRef(ko.toPDFDictionary());
          else
            mcids.add(ko.intValue(-1));
      else
        mcids.add(k.intValue(-1));
    }
  }

  public PDFStructElem parentElement()
  {
    return root == this ? null : (PDFStructElem) parent;
  }

  private int level(int level)
  {
    return root == this ? level : ((PDFStructElem) parent).level(level + 1);
  }

  public int level()// root==-1, first children==0,...
  {
    return level(-1);
  }

  private void addDictionaryRef(PDFDictionary map)
  {
    if (map.is("Type", "MCR"))
      this.marks.add(new PDFMarkPointer(this, map));
    else if (map.is("Type", "OBJR"))
      this.refs.add(new PDFObjectPointer(this, map));
    else
      this.add(new PDFStructElem(this, map));
  }

  @Override
  public String sticker()
  {
    return this.name + " " + reference();
  }

  @Override
  public String toString()
  {
    Stringer str = new Stringer();
    str.span("\nMCID[");
    for (Integer ref : mcids)
      str.span(ref, " ");
    str.trimEnd(" ").span("]\nObj[");
    for (PDFObjectPointer ref : refs)
      str.span(ref.obj, " ");
    str.trimEnd(" ").span("]\nMarks[");
    for (PDFMarkPointer ref : marks)
      str.span(ref.mcid, " ");
    str.trimEnd(" ").span("]");
    return this.type + "[" + this.name + "] " + reference() + "\nIDTree[" + (treeID == null ? "null" : treeID) + "]" + "\nPage["
        + (page == null ? "null" : page) + "]" + "\nIndex[" + index + "]" + str + "";
  }
}
