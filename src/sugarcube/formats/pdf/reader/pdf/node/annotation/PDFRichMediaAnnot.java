package sugarcube.formats.pdf.reader.pdf.node.annotation;

import sugarcube.common.system.log.Log;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.IO;
import sugarcube.formats.pdf.reader.Dexter;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.PDFObject;

public class PDFRichMediaAnnot extends PDFAnnotation
{
  public String name;
  public byte[] stream;
  
  public PDFRichMediaAnnot(PDFNode parent, PDFDictionary map)
  {
    super(parent, Dexter.RICH_MEDIA, map);
    
    this.reference = map.reference();
    
    Log.debug(this,  " - map="+map.reference());
    if (map.has("RichMediaContent"))
    {
   
      PDFDictionary media = map.get("RichMediaContent").toPDFDictionary();
      if(media.has("Assets"))
      {
        PDFDictionary assets = media.get("Assets").toPDFDictionary();
        if(assets.has("Names"))
        {

          PDFObject[] names = assets.get("Names").unreference().array();
          Log.debug(this,  " - names="+names.length);
          for(int i=0; i<names.length; i+=2)
          {
            String name = names[i].toPDFString().stringValue();       
            Log.debug(this,  " - name="+name);
            if(name.endsWith(".mp4") && i+1<names.length)
            {
              PDFDictionary filespec=names[i+1].toPDFDictionary();
              if(filespec.has("EF"))
              {
                PDFDictionary ef = filespec.get("EF").toPDFDictionary();
                if(ef.has("F"))
                {
                  if(stream!=null)
                    Log.debug(this,  " - stream already defined: "+name);
                  this.name = name;
                  this.stream = ef.get("F").toPDFStream().byteValues();
                  
                  IO.WriteBytes(File3.Desk(name), stream);
                }
              }
            }
          }                   
        }
      }
    }
  }
  
  public boolean isValid()
  {
    return name!=null && stream!=null;
  }
  
  @Override
  public String sticker()
  {
    return "RichMediaAnnot" + reference();
  }

  @Override
  public String toString()
  {
    return "RichMediaAnnot" + reference() + "\nBounds" + bounds + "\nName[" + name + "]" +"\nSize["+(stream==null ? "0" : stream.length+"]");
  }
}
