package sugarcube.formats.pdf.reader.pdf.object;

public class PDFPagesRecover extends PDFDictionary
{
  public PDFArray array = new PDFArray(this);

  public PDFPagesRecover(PDFTrailer trailer)
  {
    super(trailer);

    map.put("Kids", array);
    map.put("Type", new PDFName(this, "Pages"));
    map.put("Recover",  new PDFName(this, "True"));
    this.add(array);
    this.debug+=" Recover";
  }

  public void addPage(PDFDictionary page)
  {    
    array.add(page);    
  }

}
