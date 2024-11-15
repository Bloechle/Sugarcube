package sugarcube.common.ui.fx.base;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import sugarcube.formats.ocd.objects.OCDPaintable;

public class FxClipboard
{
  public static OCDPaintable OCD_NODE = null;
  
  public static Clipboard put(String text)
  {
    ClipboardContent content = content();
    content.putString(text);
    return set(content);
  }
  
  public static String text()
  {
    ClipboardContent content = content();
    return content.getString();
  }
  
  public static void put(OCDPaintable node)
  {
    OCD_NODE = node;    
  }
  
  public static OCDPaintable ocdNode()
  {
    return OCD_NODE;
  }
  
  public static Clipboard set(ClipboardContent content)
  {
    Clipboard clipboard = get();
    clipboard.setContent(content);
    return clipboard;
  }
  
  public static ClipboardContent content()
  {
    return new ClipboardContent();
  }
  
  public static Clipboard get()
  {
    return Clipboard.getSystemClipboard();
  }
}
