package sugarcube.formats.epub.structure.xhtml;

import sugarcube.common.system.io.File3;

public class XHTML
{
  public static final String EXT = ".xhtml";
  public static final String FILE_EXTENSION = ".xhtml";
  /**
   * Maurizio
   * public static final String DOCTYPE ="<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">";*/
  
  
  public static String Fontname (String fontname)
  {
    return File3.CleanSymbols(fontname.replaceAll(",", "-comma-"), "_", '-', '_');
  }

}
