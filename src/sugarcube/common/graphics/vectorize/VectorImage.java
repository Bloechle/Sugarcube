/*
Created from ImageTracer.java
(Desktop version with javax.imageio. See ImageTracerAndroid.java for the Android version.)
Simple raster image tracer and vectorizer written in Java. This is a port of imagetracer.js.
by András Jankovics 2015, 2016
andras@jankovics.net

*/

package sugarcube.common.graphics.vectorize;

import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.IO;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

public class VectorImage
{
  public static String VERSION = "1.1.2";

  public ImageLayer[] layers;
  public Palette palette;

  public VectorImage(ImageLayer[] layers, Palette palette, DTOptions options)
  {
    this.layers = layers;
    this.palette = palette;

    int pathOmit = options.floor(DTOptions.PATHOMIT);
    float ltRes = options.get(DTOptions.LTRES);
    float qtRes = options.get(DTOptions.QTRES);

    for (ImageLayer layer : layers)
    {
      layer.scanPath(pathOmit);
      layer.interpolateInternodes();
      layer.traceAllPaths(ltRes, qtRes);
    }
  }

//  public static String S(float val, float places)
//  {
//    return places < 0 ? "" + val : "" + (Math.round(val * Math.pow(10, places)) / Math.pow(10, places));
//  }

  // Getting SVG path element string from a traced path
  public static void svgPath(SVGizer sb, String desc, VecSegmentPath path, byte[] rgb, DTOptions options)
  {
    float s = options.get(DTOptions.SCALE);
    float lcpr = options.get(DTOptions.LCPR);
    float qcpr = options.get(DTOptions.QCPR);

    String color = sb.rgb(rgb);

    VecSegment seg = path.get(0);
    sb.openPath(seg.x0 * s, seg.y0 * s, desc, color, color, 1, (rgb[3] + 128f) / 255f);
    for (int i = 0; i < path.size(); i++)
    {
      seg = path.get(i);
      if (seg.type == 1)
        sb.lineTo(seg.x1 * s, seg.y1 * s);
      else
        sb.quadTo(seg.cpx, seg.cpy, seg.x1, seg.y1);
    }
    
    sb.closePath();

    // Rendering control points
    if (lcpr > 0 || qcpr > 0)
      for (int i = 0; i < path.size(); i++)
      {
        seg = path.get(i);

        if (lcpr > 0 && seg.type == 1)
        {
          sb.circle(seg.x1 * s, seg.y1 * s, lcpr, "white", "black", lcpr * 0.2f);
        }

        if (qcpr > 0 && seg.type == 2)
        {
          sb.circle(seg.cpx * s, seg.cpy * s, qcpr, "cyan", "black", qcpr * 0.2f);
          sb.circle(seg.x1 * s, seg.y1 * s, qcpr, "white", "black", qcpr * 0.2f);
          sb.line(seg.x0 * s, seg.y0 * s, seg.cpx * s, seg.cpy * s, "cyan", qcpr * 0.2f);
          sb.line(seg.cpx * s, seg.cpy * s, seg.x1 * s, seg.y1 * s, "cyan", qcpr * 0.2f);
        }
      }
  }

  // Converting tracedata to an SVG string, paths are drawn according to a
  // Z-index
  // the optional lcpr and qcpr are linear and quadratic control point radiuses
  public String svg(DTOptions options)
  {
    float s =  options.get("scale");  
    
    int w = (int)(width()*s);
    int h = (int)(height()*s);
    
    SVGizer svg = new SVGizer(options.floor(DTOptions.ROUNDCOORDS));    
    svg.openSvg(w,h,  options.get(DTOptions.VIEWBOX) != 0, "Created with DeepTracer");

    TreeMap<Double, int[]> zIndex = new TreeMap<Double, int[]>();
    for (int k = 0; k < layers.length; k++)
    {
      ArrayList<VecSegmentPath> tracePaths = layers[k].tracePaths;
      for (int i = 0; i < tracePaths.size(); i++)
      {
        // Label (Z-index key) is the startpoint of the path, linearized
        double label = (tracePaths.get(i).get(0).y0 * w) + tracePaths.get(i).get(0).x0;
        // Creating new list if required
        if (!zIndex.containsKey(label))
          zIndex.put(label, new int[2]);

        // Adding layer and path number to list
        zIndex.get(label)[0] = k;
        zIndex.get(label)[1] = i;
      }
    }

    // Drawing
    // Z-index loop
    for (Entry<Double, int[]> entry : zIndex.entrySet())
    {
      int layer = entry.getValue()[0];
      int index = entry.getValue()[1];
      String desc = options.get(DTOptions.DESC) == 0 ? "" : "l " + layer + " p " + index + " ";
      svgPath(svg, desc, layers[layer].tracePaths.get(index), palette.colors[layer], options);
    }

    
    svg.closeSvg();

    return svg.toString();

  }

  public int width()
  {
    return layers[0].width();
  }

  public int height()
  {
    return layers[0].height();
  }

  public static VectorImage Load(File3 imageFile, DTOptions options)
  {
    return ImageDataRGBA.Load(imageFile).vectorize(options, null);
  }

  public static void main(String[] args)
  {
    try
    {
      String filename = "Key-median.png";

      File3 imgFile = File3.Desk(filename);

      DTOptions options = new DTOptions();

      VectorImage vector = Load(imgFile, options);
      String svg = vector.svg(options);

      // Loading image, tracing, rendering SVG, saving SVG file
      IO.WriteText(imgFile.extense(".svg"), svg);
      System.out.println("DONE");

    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
