package sugarcube.formats.epub.structure;

import sugarcube.common.data.collections.StringMap;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.formats.epub.EPubWriter;
import sugarcube.formats.epub.structure.xhtml.*;
import sugarcube.formats.epub.replica.svg.SVGPage;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.*;

public class EPubEventLayer
{
  public StringMap<HTMLBox> staticVideos = new StringMap<>();
  public StringMap<HTMLBox> staticAudios = new StringMap<>();
  public String onloadJS = "";
  public EPubWriter writer;
  public OCDPage ocdPage;
  public SVGPage svgPage;
  public HTMLDiv rootDiv;
  public OCDAnnotations anchorLinks;
  public float pageScale = 1;
  public Rectangle3 viewBox;

  public EPubEventLayer(EPubWriter writer, OCDPage ocd, SVGPage svg, HTMLDiv rootDiv)
  {
    this.writer = writer;
    this.ocdPage = ocd;
    this.svgPage = svg;
    this.rootDiv = rootDiv;
    this.anchorLinks = ocd.annotations().anchorLinks();
    this.pageScale = writer.pageScale;
    this.viewBox = writer.viewBox;
  }
  
  public void addStaticAudios()
  {
    for (OCDImage image : svgPage.audios)
    {
      HTMLBox audioBox = audioBox(image);
      staticAudios.put(audioBox.id(), audioBox);
      rootDiv.add(audioBox);
    }
  }

  public void addStaticVideos()
  {
    for (OCDImage image : svgPage.videos)
    {
      HTMLBox videoBox = videoBox(image);
      staticVideos.put(videoBox.id(), videoBox);
      rootDiv.add(videoBox);
    }
  }
    
  private HTMLBox audioBox(OCDImage image)
  {
    String imageID = image.needID();
    String audioSrc = EPub.AUDIO_FOLDER + image.filename().replace(' ', '_');
    if (writer.media.notYet(audioSrc))
      writer.writeCopy(EPub.BOOK_DIR + audioSrc, writer.ocd.zipFile().entry(OCD.IMAGES_DIR + image.filename()));
    HTMLAudio video = new HTMLAudio(imageID + "-aud", true, audioSrc);
    HTMLBox videoBox = new HTMLBox("box-" + imageID, image.x(), image.y(), pageScale);
    videoBox.addChild(video);
    return videoBox;
  }  
  
  private HTMLBox videoBox(OCDImage image)
  {
    String imageID = image.needID();
    String videoSrc = EPub.VIDEO_FOLDER + image.filename().replace(' ', '_');
    if (writer.media.notYet(videoSrc))
      writer.writeCopy(EPub.BOOK_DIR + videoSrc, writer.ocd.zipFile().entry(OCD.IMAGES_DIR + image.filename()));
    HTMLVideo video = new HTMLVideo(imageID + "-vid", true, I(image.w() * image.sx()), I(image.h() * image.sy()), videoSrc);
    HTMLBox videoBox = new HTMLBox("box-" + imageID, image.x(), image.y(), pageScale);
    videoBox.addChild(video);
    return videoBox;
  }

  public HTMLBox svgBox(OCDPaintable node)
  {
    HTMLBox box = new HTMLBox("box-" + node.id(), node.bounds());
    box.add(new SVGPage(ocdPage, writer).view(node, ++writer.svgViews).create());
    return box;
  }

  public HTMLImage htmlImage(EPubOnEvent anim)
  {
    return new HTMLImage(anim.sourceID, EPub.IMAGE_FOLDER + anim.imageFilename(), anim.source.bounds(), "");
  }

  public static String R(double d)
  {
    return "" + (Math.round(d * 100) / 100.0);
  }

  public static String S(double d)
  {
    return "" + Math.round(d);
  }

  public static int I(double d)
  {
    return (int) Math.round(d);
  }

}
