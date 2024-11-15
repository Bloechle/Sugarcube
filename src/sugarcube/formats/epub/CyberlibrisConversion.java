package sugarcube.formats.epub;

import sugarcube.common.data.collections.Props;
import sugarcube.common.interfaces.Unjammable;
import sugarcube.common.system.io.File3;
import sugarcube.formats.epub.replica.Replica;

public class CyberlibrisConversion implements Unjammable
{
  public static void main(String... args)
  {
    Props props = new Props();
    props.put("log_level","debug");
    props.put("width","0");
    props.put("height","0");
    props.put("sampling","2");
    props.put("antialias","2");
    props.put("jpeg","0.9");
    props.put("detect_url","true");
    props.put("svg_graphics","true");
    props.put("svg_text","true");
    props.put("toc","true");
    props.put("font_otf","true");
    props.put("font_svg","true");
    props.put("page_fonts","true");
    props.put("javascript","false");
    props.put("suffix","none");
    
    File3 file = File3.desktop("c02-beamer.pdf");
    
    Replica.Convert(file, file.extense("epub"), props);
  }
}
