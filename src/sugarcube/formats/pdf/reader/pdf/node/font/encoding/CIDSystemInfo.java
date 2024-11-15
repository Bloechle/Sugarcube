package sugarcube.formats.pdf.reader.pdf.node.font.encoding;

import sugarcube.common.system.log.Log;
import sugarcube.formats.pdf.reader.pdf.node.font.CMapReader;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.PDFObject;
import sugarcube.resources.pdf.encoding.cmap.RS_CMAP;

public class CIDSystemInfo
{
  public class CIDEntry
  {
    public String registry;
    public String ordering;
    public int supplement;

    public String name()
    {
      return registry + "-" + ordering;
    }

    @Override
    public String toString()
    {
      return "[" + registry + "," + ordering + "," + supplement + "]";
    }
  }

  public CIDEntry[] entries = new CIDEntry[0];

  public CIDSystemInfo(PDFObject obj)
  {
    obj = obj.unreference();
    if (obj.isPDFDictionary())
    {
      this.entries = new CIDEntry[1];
      this.entries[0] = read(obj.toPDFDictionary());
    }
    else if (obj.isPDFArray())
    {
      PDFDictionary[] dicos = obj.toPDFArray().dicoValues();
      this.entries = new CIDEntry[dicos.length];
      for (int i = 0; i < dicos.length; i++)
        this.entries[i] = read(dicos[i]);
    }
    else
      Log.debug(this, " - unknown object: " + obj);
  }

  public CIDEntry read(PDFDictionary dico)
  {
    CIDEntry cid = new CIDEntry();
    cid.registry = dico.get("Registry").stringValue("Adobe");
    cid.ordering = dico.get("Ordering").stringValue("");
    cid.supplement = dico.get("Supplement").intValue(0);
    return cid;
  }

  @Override
  public String toString()
  {
    String sc = "CIDSystemInfo";
    for (int i = 0; i < entries.length; i++)
      sc = sc + entries[i].toString();
    return sc;
  }

  public void populateEncoding(Encoding enc)
  {
    for (CIDEntry entry : entries)
      populateEncoding(enc, entry);
  }

  public void populateEncoding(Encoding enc, CIDEntry entry)
  {    
    boolean horiz = enc.isHorizontal();
    String name = entry.name();
    switch (name)
    {
      case "Adobe-Japan1":
        populate(true, enc, "UniJIS-UTF16-H");
        populate(!horiz, enc, "UniJIS-UTF16-V");
        break;
      case "Adobe-Korea1":
        populate(true, enc, "UniKS-UTF16-H");
        populate(!horiz, enc, "UniKS-UTF16-V");
        break;        
    }

  }

  private void populate(boolean check, Encoding enc, String cmap)
  {
    if (check)
      CMapReader.ReadResourceCMap(RS_CMAP.stream(cmap), enc.codeToUnicode_);
  }
}
