package sugarcube.common.system.io;

import sugarcube.common.data.collections.Props;
import sugarcube.common.data.collections.StringSet;

public class Mime
{
  public static final Props MAP = new Props();
  public static final String TYPE_IMAGE = "image";
  public static final String TYPE_AUDIO = "audio";
  public static final String TYPE_VIDEO = "video";
  public static final String TYPE_FONT = "font";
  //image
  public static final String PNG = "image/png";
  public static final String JPG = "image/jpeg";
  public static final String SVG = "image/svg+xml";
  //audio
  public static final String AAC = "audio/mp4";  
  public static final String MP3 = "audio/mpeg";
  public static final String WAV = "audio/x-wav";
  public static final String OGG = "application/ogg";
  public static final String WMA = "audio/x-ms-wma";
  //video
  public static final String MP4 = "video/mp4";
  public static final String MPG = "video/mpeg";
  public static final String WEBM = "video/webm";
  public static final String FLV = "video/x-flv";
  public static final String WMV = "video/x-ms-wmv";
  public static final String SWF = "application/x-shockwave-flash";
  //fonts
  public static final String OTF = "application/x-font-opentype";
  public static final String TTF = "application/x-font-truetype";
  public static final String WOFF = "application/x-font-woff";
  //other
  public static final String XHTML = "application/xhtml+xml";
  public static final String XML = "application/xml";
  public static final String ZIP = "application/zip";
  public static final String CSS = "text/css";
  public static final String JS = "text/javascript";
  public static final String NCX = "application/x-dtbncx+xml";
  public static final String PDF = "application/pdf";
  private static final StringSet SET_IMAGE;
  private static final StringSet SET_AUDIO;
  private static final StringSet SET_VIDEO;
  private static final StringSet SET_FONT;

  static
  {

    MAP.put(".png", PNG);
    MAP.put(".jpg", JPG);
    MAP.put(".jpeg", JPG);
    SET_IMAGE = new StringSet(PNG, JPG);

    MAP.put(".aac", AAC); 
    MAP.put(".m4a", AAC);     
    MAP.put(".mp3", MP3);
    MAP.put(".wav", WAV);
    MAP.put(".ogg", OGG);
    MAP.put(".wma", WMA);
    SET_AUDIO = new StringSet(AAC, MP3, WAV, OGG, WMA);
    
    MAP.put(".mp4", MP4);
    MAP.put(".m4v", MP4);
    MAP.put(".mpg", MPG);
    MAP.put(".mpeg", MPG);
    MAP.put(".webm", WEBM);
    MAP.put(".flv", FLV);
    MAP.put(".wmv", WMV);
    MAP.put(".swf", SWF);
    SET_VIDEO = new StringSet(MP4, MPG, WEBM, FLV, WMV, SWF);
    
    MAP.put(".otf", OTF);
    MAP.put(".ttf", TTF);
    MAP.put(".woff", WOFF);
    SET_FONT = new StringSet(OTF, TTF, WOFF);
    
    MAP.put(".svg", SVG);
    MAP.put(".xhtml", XHTML);
    MAP.put(".xml", XML);
    MAP.put(".zip", ZIP);
    MAP.put(".css", CSS);
    MAP.put(".js", JS);
    MAP.put(".ncx", NCX);
    MAP.put(".pdf", PDF);
  }

  public static boolean isType(String type, String mime)
  {
    if (type.equals(TYPE_IMAGE))
      return isImage(mime);
    else if (type.equals(TYPE_AUDIO))
      return isAudio(mime);
    else if (type.equals(TYPE_VIDEO))
      return isVideo(mime);
    else if(type.equals(TYPE_FONT))
      return isFont(mime);
    else
      return false;
  }

  public static boolean isImage(String mime)
  {
    return SET_IMAGE.has(mime);
  }

  public static boolean isAudio(String mime)
  {
    return SET_AUDIO.has(mime);
  }

  public static boolean isVideo(String mime)
  {
    return SET_VIDEO.has(mime);
  }
  
  public static boolean isFont(String mime)
  {
    return SET_FONT.has(mime);
  }  

  public static boolean is(String filepath, String... mimes)
  {
    String mime = get(filepath, "");
    for (String m : mimes)
      if (m.equals(mime))
        return true;
    return false;
  }

  public static String get(String filepath, String recover)
  {
    String ext = File3.extension(filepath).toLowerCase();
    return MAP.get(ext.startsWith(".") ? ext : "." + ext, recover);
  }
}
