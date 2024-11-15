package sugarcube.formats.epub.structure.xhtml;

public class HTMLCanvas extends HTMLNode
{
  public static final String TAG = "canvas";  
  
  public HTMLCanvas(String canvasID, int width, int height)
  {
    super(TAG, "id", canvasID, "width", ""+width, "height", ""+height, "");       
    //cdata = "" in order to ensure <canvas></canvas> instead of <canvas/>
  } 
}