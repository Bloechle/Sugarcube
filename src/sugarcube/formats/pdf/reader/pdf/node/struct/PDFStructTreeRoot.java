package sugarcube.formats.pdf.reader.pdf.node.struct;

import sugarcube.common.data.collections.Array3;
import sugarcube.common.data.collections.Map3;
import sugarcube.common.data.collections.Stringer;
import sugarcube.formats.pdf.reader.pdf.node.PDFDocument;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.Reference;
import sugarcube.formats.pdf.reader.struct.GenericStruct;

import java.util.Map;

public class PDFStructTreeRoot extends PDFStructElem
{
  public String producer = "unknown";
  public Map3<Reference, PDFPageStruct> structs = new Map3<Reference, PDFPageStruct>();//access StructElem by their mcid (which are relative to a page content or xObject)
  public Array3<PDFStructElem> indexes = new Array3<PDFStructElem>();//access StructElem by their auto generated reading order id

  public PDFStructTreeRoot(PDFDocument parent, PDFDictionary map)
  {
    super(parent, map);
    this.root = this;
    this.reference = map.reference();
    this.name = "Structure";

    PDFDictionary info = parent.trailer().getInfo();
    if (info != null)
    {
      String creator = info.get("Creator").stringValue(info.get("Producer").stringValue(producer)).toLowerCase();
      if(creator.contains("pdfmaker") && creator.contains("for word"))
        producer = GenericStruct.PRODUCER_ACROBAT;
        else if (creator.contains("microsoft") && creator.contains("word"))
        producer = GenericStruct.PRODUCER_WORD;
      else if(creator.contains("abbyy"))
        producer = GenericStruct.PRODUCER_ABBYY;
    }
    this.parseTree(map);
  }
  
  public boolean isProducer(String producer)
  {
    return this.producer.equals(producer);
  }
  
  public boolean isProducedByWord()
  {
    return isProducer(GenericStruct.PRODUCER_WORD);
  }
  
  public boolean isProducedByABBYY()
  {
    return isProducer(GenericStruct.PRODUCER_ABBYY);
  }
  
  public boolean isProducedByAcrobat()
  {
    return isProducer(GenericStruct.PRODUCER_ACROBAT);
  }


  public PDFStructElem get(int index)
  {
    return indexes.get(index, null);
  }

  public PDFPageStruct getPageStruct(Reference ref)
  {
    return this.structs.get(ref);
  }

  protected PDFPageStruct needPageStruct(Reference ref)
  {
    PDFPageStruct struct = structs.get(ref);
    if (struct == null)
      structs.put(ref, struct = new PDFPageStruct(this, ref));
    return struct;
  }

  protected void populatePageStruct(PDFStructElem elem)
  {
    if (elem.page != null && !elem.mcids.isEmpty())
    {
      PDFPageStruct struct = needPageStruct(elem.page);
      for (int mcid : elem.mcids)
        struct.add(mcid, elem);
    }
    for (PDFMarkPointer mark : elem.marks)
      if (mark.page != null)
        needPageStruct(mark.page).add(mark.mcid, elem);
    for (PDFObjectPointer obj : elem.refs)
      if (obj.obj != null)
        needPageStruct(obj.obj).add(-1, elem);
  }

  @Override
  public String toString()
  {
    Stringer str = new Stringer();
    for (Map.Entry<Reference, PDFPageStruct> entry : structs.entrySet())
      str.span("\n\n" + entry.getValue());
    str.trimEnd(" ").span("]");
    return this.type + "[" + this.name + "] " + reference()
      + str
      + "";
  }
}
