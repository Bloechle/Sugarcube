package sugarcube.formats.pdf.reader.pdf.node.actions;

import sugarcube.common.system.log.Log;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.object.PDFArray;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.PDFObject;
import sugarcube.formats.pdf.reader.pdf.object.Reference;

public class PDFDest extends PDFNode
{
  public Reference pageRef = null;
  public String name = "";
  public String action = "";
  public String data = "";
  public float[] xyzwh = new float[0];

  public PDFDest(PDFNode parent, String name, PDFObject dest)
  {
    super("Dest", parent);
    dest = dest.unreference();
    this.reference = dest.reference();
    int size = dest.nbOfChildren();
//    Log.debug(this,  " - name="+name+", dest="+dest);
    
    if(dest.isPDFDictionary())
    {
      PDFDictionary dico = dest.toPDFDictionary();
      if(dico.has("D", "Dest"))
      {
        PDFObject d=dico.get("D", "Dest").unreference();
        if(d.isPDFArray())
          dest = d.toPDFArray();
      }
    }
    
    if (size > 0 && dest.isPDFArray())
    {     
      PDFObject obj = dest.get(0);
      data = obj.stringValue();
      int index=0;
      if (obj.isPDFPointer())
      {
        this.pageRef = obj.toPDFPointer().get();
        index++;
      }
      else if(obj.isPDFArray())
      {
        PDFArray array = obj.toPDFArray();
        for(PDFObject child: array)
        {
          Log.debug(this,  " - child from "+reference()+": "+child);
        }
      }
      if (size > index)
        this.action = dest.get(index++).stringValue();
      if (size > 2)
      {
        xyzwh = new float[size - 2];
        for (int i = 2; i < size; i++)
          xyzwh[i - 2] = dest.get(i).floatValue();
      }
    }
    else
    {
      data = dest.stringValue();
      Log.debug(this, " - unknown dest: "+data);
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
    return type + "[" + data + "]" + reference()+", name="+name;
  }

}
