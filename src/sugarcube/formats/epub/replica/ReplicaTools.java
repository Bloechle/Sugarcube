package sugarcube.formats.epub.replica;

import sugarcube.common.graphics.Image3;
import sugarcube.formats.epub.EPubProps;
import sugarcube.formats.epub.structure.EPub;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDAnnot;
import sugarcube.formats.ocd.objects.OCDPage;

public class ReplicaTools
{

  
  public static void Flatten(ReplicaWriter writer, OCDPage ocdPage, int spreads, String[] bgNames)
  {
    EPubProps props = writer.props;
    
    double antialias = props.antialias();
    double sampling = props.sampling(2);
    if (sampling < 1)
      sampling = 2;
    boolean transparentText = !props.vecText();
    OCD.ViewProps bgProps = new OCD.ViewProps();
    bgProps.linkColor = writer.linkColor;
    bgProps.scale = (float) (writer.pageScale * sampling);
    bgProps.paint_text = transparentText;
    bgProps.box = OCDAnnot.ID_VIEWBOX;
    bgProps.scale *= antialias;
    Image3 bg = ocdPage.createImage(bgProps);

    // improves rendering artefacts such as clipping boundaries
    if (Math.abs(antialias - 1) > 0.001)
      bg = bg.decimate(1.0 / antialias);


    try
    {
      for (int i = 0; i < spreads; i++)
      {
        String part = i > 0 ? "_" + i : "";
        writer.images.add(bgNames[i] = "bg-" + ocdPage.number() + part + (props.png() ? ".png" : ".jpg"));
        Image3 subBg = bg;
        if (spreads > 1)
        {
          int subWidth = Math.round(bg.width() / (float) spreads);
          // Log.debug(this, ".writePage - part " + i + ": subWidth=" +
          // subWidth + ", width=" + bg.width());
          subBg = bg.crop(subWidth * i, 0, subWidth, bg.height());
        }
        writer.write(EPub.IMAGE_DIR + bgNames[i], subBg.write(props.png() ? -1 : props.jpeg()));
      }
    } catch (Exception ex)
    {
      ex.printStackTrace();
      bgNames = null;
    }
  }
  
}
