package sugarcube.formats.epub.structure.xhtml;

import sugarcube.common.system.log.Log;

public class HTMLSlideshow extends HTMLNode
{
  public static final String TAG = "div";

  public HTMLSlideshow(String id)
  {
    super(TAG, "id", id, "class", "sc-slides");
    this.id = id;
  }

  public HTMLSlideshow(String id, String... sources)
  {
    this(id);    
    this.addSlides(sources);
  }
  
  public HTMLSlideshow(String id, float width, float height, String... sources)
  {
    this(id, sources);
    this.style("width:" + S(width) + "px; height:" + S(height) + "px;");
  }

  public HTMLSlideshow addSlides(String... sources)
  {
    HTMLDiv slideShow = new HTMLDiv("class", "slideshow-container");

    for (int i = 0; i < sources.length; i++)
    {
      String src = sources[i];
      HTMLDiv slide = new HTMLDiv("class", id+" fade");
      slide.addImage(src, "style", "width:100%;");
      slideShow.addChild(slide);
    }

    slideShow.addAnchor("", "class", "prev", "onclick", "plusSlides(-1)").setCData("&#10094;", false);
    slideShow.addAnchor("", "class", "next", "onclick", "plusSlides(1)").setCData("&#10095;", false);

    this.addChild(slideShow);

    HTMLDiv navDots = new HTMLDiv("style", "text-align:center;");
    for (int i = 0; i < sources.length; i++)
    {
      navDots.addSpan("class", "dot", "onclick", "currentSlide(" + (i + 1) + ")", "");
    }

    this.addChild(navDots);

    return this;
  }

  public static void main(String... args)
  {
    HTMLSlideshow slides = new HTMLSlideshow("mySlide");

    slides.addSlides("00.jpg", "01.jpg", "02.jpg");

    Log.debug(HTMLSlideshow.class, ".main - " + slides.xmlString());

  }
}