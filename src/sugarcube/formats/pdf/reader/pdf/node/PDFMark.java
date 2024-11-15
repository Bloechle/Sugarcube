package sugarcube.formats.pdf.reader.pdf.node;

import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;

public class PDFMark extends PDFNode
{
  public String tag;
  public PDFDictionary props;
  public int mcid = -1;

  public PDFMark(PDFContent parent, PDFInstr instr)
  {
    super("Mark", parent);
    this.tag = instr.params.first().stringValue("null");
    if (instr.params.size() > 1)
    {
      this.props = instr.params.second().toPDFDictionary();
      this.mcid = this.props.get("MCID").intValue(-1);
    }
  }

  public boolean isTag(String tag)
  {
    return this.tag.equals(tag);
  }

  @Override
  public String sticker()
  {
    return tag + "[" + mcid + "]";
  }

  @Override
  public String toString()
  {
    return sticker();
  }
}
