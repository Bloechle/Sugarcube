package sugarcube.formats.ocd.objects.metadata.powerswitch;

import sugarcube.common.system.reflection.Annot._Xml;
import sugarcube.common.system.log.Log;
import sugarcube.common.system.io.File3;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlNodeReflect;
import sugarcube.formats.ocd.objects.OCDDocument;
import sugarcube.formats.ocd.objects.document.OCDMetadata;
import sugarcube.formats.ocd.objects.metadata.dc.DC;

import java.io.File;

@_Xml(tag = PSMeta.TAG)
public class PSMeta extends XmlNodeReflect
{   
  public static final String TAG = "ValueDescription";
  public static String SPLIT = ",";
  @_Xml
  public String Type = "";
  @_Xml
  public PSRow[] rows = new PSRow[0];    

  public void populate(OCDMetadata meta)
  {
    for(PSRow row: rows)
     {
        String field = row.LocalizedTagName.trim().toLowerCase().replace("english - ",  "");

        String value = row.cdata;
        
        if(value!=null && !value.trim().isEmpty())
        switch(field)
        {
        case "name":
          meta.replace(DC.title,  value);
          break;
        case "description":
          meta.replace(DC.description, value);
          break;
//        case "cover file":
//          meta.replace(DC.subject, value);
//          break;
        case "isbn" :         
          meta.replace(DC.identifier, "ISBN:"+value);
        break;
        case "authors":          
          meta.replace(DC.creator, value);
          break;
        case "publishers":
          meta.replace(DC.publisher, value);          
          break;
        case "categories":
          break;
        case "sell price":
          break;
          default:
           Log.debug(this, ".populateDC - field="+field+", value="+value); 
            break;
        }
     }    
  }
  
  public static void populate(File file, OCDDocument ocd)
  {
    PSMeta meta = new PSMeta();
    Xml.Load(meta, file);
    meta.populate(ocd.metadata());    
  }

  public static void main(String... args)
  {
    PSMeta meta = new PSMeta();

    File3 file = File3.desktop("MyLiveBook_1.xml");

    Xml.Load(meta, file);

    Log.debug(PSMeta.class, " - " + meta);
  }

}
