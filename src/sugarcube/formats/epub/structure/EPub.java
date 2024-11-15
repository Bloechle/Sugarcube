package sugarcube.formats.epub.structure;

import sugarcube.common.system.io.File3;
import sugarcube.common.system.time.Date3;
import sugarcube.formats.epub.structure.xhtml.XHTML;

import java.nio.charset.Charset;

public class EPub
{
  public static final Charset UTF8 = Charset.forName("UTF-8");  
  
  public static final String EXT = ".epub";
  public static final String FILE_EXTENSION = EXT;
  public static final String FONT_PREFIX = "font-";  
  public static final String IMAGE_PREFIX = "image-";
  public static final String MIMETYPE_FILE = "mimetype";
  public static final String BOOK_DIR = "OEBPS/";
  public static final String METAINF_DIR = "META-INF/";
  public static final String CONTAINER_FILE = "container.xml";
  public static final String IBOOK_DISPLAY_FILE = "com.apple.ibooks.display-options.xml";
  public static final String NCX_FILE = "toc.ncx";
  public static final String NAV_FILE = "nav.xhtml";
  public static final String OPF_FILE = "content.opf";
  public static final String COVER_FILE = "cover.xhtml";  
  public static final String COVER_IMG_FILE = "cover.jpg";
  public static final String CONTAINER_PATH = METAINF_DIR + CONTAINER_FILE;
  public static final String IBOOK_DISPLAY_PATH = METAINF_DIR + IBOOK_DISPLAY_FILE;
  public static final String NCX_PATH = BOOK_DIR + NCX_FILE;
  public static final String NAV_PATH = BOOK_DIR + NAV_FILE;
  public static final String OPF_PATH = BOOK_DIR + OPF_FILE;
  public static final String COVER_PATH = BOOK_DIR + COVER_FILE;
  public static final String FONT_FOLDER = "fonts/";
  public static final String SVG_FONT_FOLDER = "images/";  
  public static final String IMAGE_FOLDER = "images/";
  public static final String AUDIO_FOLDER = "audio/";  
  public static final String VIDEO_FOLDER = "video/";  
  public static final String XHTML_FOLDER = "";//actual epub pages...
  public static final String PDF_FOLDER = "pdf/";
  public static final String CSS_FOLDER = "";
  public static final String JS_FOLDER = "";
  public static final String FONT_DIR = BOOK_DIR + FONT_FOLDER;
  public static final String SVG_FONT_DIR = BOOK_DIR + SVG_FONT_FOLDER;  
  public static final String IMAGE_DIR = BOOK_DIR + IMAGE_FOLDER;
  public static final String AUDIO_DIR = BOOK_DIR + AUDIO_FOLDER;
  public static final String VIDEO_DIR = BOOK_DIR + VIDEO_FOLDER;  
  public static final String PDF_DIR = BOOK_DIR + PDF_FOLDER;  
  public static final String CSS_DIR = BOOK_DIR + CSS_FOLDER;
  public static final String JS_DIR = BOOK_DIR + JS_FOLDER;
  public static final String XHTML_DIR = BOOK_DIR + XHTML_FOLDER;

  public static String css()
  {
    return "body {\n"
      + "position:absolute;\n"
      + "top:0px;\n"
      + "left:0px;\n"
      + "margin:0px;\n"
      + "padding:0px;\n"
      + "}\n"
      + ".page {\n"
      + "position:absolute;\n"
      + "top:0px;\n"
      + "left:0px;\n"
      + "margin:0px;\n"
      + "padding:0px;\n"
      + "}\n"
      + "svg {\n"
      + "width:100%;\n"
      + "height:100%;\n"
      + "position:absolute;\n"
      + "top:0;\n"
      + "left:0;\n"
      + "margin:0;\n"
      + "padding:0;\n"
      + "image-rendering:optimizeSpeed;\n"
      + "shape-rendering:optimizeSpeed;\n"
      + "color-rendering:optimizeSpeed;\n"
      + "text-rendering:optimizeSpeed;\n"
      + "}\n"
      + "a {\n"
      + "color: black !important;\n"
      + "}\n"
      + "#guide {\n"
      + "display:none;\n"
      + "}\n";
  }
  
  public static String pageFilepath(String filename)
  {
    return EPub.XHTML_FOLDER + File3.Extense(filename, XHTML.FILE_EXTENSION);
  }

  public static boolean isImage(String filePath)
  {
    return filePath.toLowerCase().endsWith(".png") || filePath.toLowerCase().endsWith(".jpg");
  }
  
  public static String PrintedByReplica()
  {
    return "Printed on "+Date3.Calendar()+" with ePUB Replica - sugarcube.ch";
  }
  
  public static String PrintedByLiquid()
  {
    return "Printed on "+Date3.Calendar()+" with ePUB LiQuid - sugarcube.ch";
  }
  

  public static String ID(String id)
  {
    id = id.replace(" ", "_").replace("/", "_").replace("\\", "_");
    if (!Character.isLetter(id.charAt(0)))
      id = "A" + id;
    return id;
  }
  
}
