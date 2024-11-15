package sugarcube.formats.epub.replica.svg;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.*;
import sugarcube.common.data.Base64;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Image3;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.Mime;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlDecimalFormat;
import sugarcube.common.data.xml.css.CSSBuilder;
import sugarcube.common.data.xml.svg.SVG;
import sugarcube.common.data.xml.svg.SVGRoot;
import sugarcube.formats.epub.EPubWriter;
import sugarcube.formats.epub.structure.otf.OTF;
import sugarcube.formats.epub.structure.xhtml.XHTML;
import sugarcube.formats.epub.structure.*;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.analysis.BackgroundClipper;
import sugarcube.formats.ocd.objects.*;
import sugarcube.formats.ocd.objects.font.SVGFont;
import sugarcube.formats.ocd.objects.lists.OCDMap;

import java.awt.*;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;

public class SVGPage extends SVGRoot
{
  public static final String CLIP_ID_PAGE = "page-clip";
  public static final String CLIP_ID_BACKGROUND = "background-clip";
  public static final XmlDecimalFormat DF = Xml.decimalFormat(3);
  public static final float EPSILON = 0.01f;

  public EPubWriter writer;
  private OCDPage page;
  private Rectangle3 viewBox;
  private CSSBuilder css = new CSSBuilder();
  protected TextMap cssClasses = new TextMap();
  protected SVGPageClips clips = new SVGPageClips(this);
  protected SVGPageFonts fonts = new SVGPageFonts(this);
  public SVGGroup lastGroup = null;
  public SVGGroup svgGroup = null;
  public boolean lossless = true;
  public boolean addSpanID = true;
  public boolean isLiquid = false;
  public boolean flattenGraphics = false;
  public int spanID = 0;
  public int paragraphID = 0;
  public int viewCounter = 0;
  public List3<OCDImage> videos = new List3<>();
  public List3<OCDImage> audios = new List3<>();
  public StringMapList<OCDImage> slideshows = new StringMapList<>();
  // public EPubAnchors linkNodes = new EPubAnchors();
  public String pageStyle = "";
  public OCDPaintable mediaNode = null;
  public EPubOnEvents onEvents = new EPubOnEvents();
  public EPubLinks urlLinks = new EPubLinks();

  public SVGPage(OCDPage page, EPubWriter writer)
  {
    super(null);
    this.page = page;
    this.writer = writer;
    this.viewBox = page.viewBox();
    this.lossless = writer.props.png();
    this.viewCounter(0);
  }

  public boolean isMediaView()
  {
    return mediaNode != null;
  }


  public SVGPage flatten(boolean doFlatten)
  {
    this.flattenGraphics = doFlatten;
    return this;
  }

  public SVGPage viewCounter(int counter)
  {
    this.viewCounter = counter;
    this.id = "svg-image" + viewIndex();
    return this;
  }

  public SVGPage viewBox(Rectangle3 box)
  {
    this.viewBox = box == null ? page.viewBox() : box;
    return this;
  }

  public SVGPage viewNode(OCDPaintable node)
  {
    this.mediaNode = node == page.content() ? null : node;
    return this;
  }
  
  public SVGPage view(OCDPaintable node, int counter)
  {
    return viewNode(node).viewBox(node.bounds()).viewCounter(counter);
  }

  public SVGPage create()
  {

    Rectangle3 mediaBox = mediaNode == null ? null : mediaNode.bounds();

    if (writer.props.cssInternal())
      this.nodes.add(style);

    this.nodes.add(defs);
    this.nodes.add(svgGroup = new SVGGroup(this, this).scaleTransform(writer.pageScale).clipPath(CLIP_ID_PAGE + viewIndex()));

    clips.add(CLIP_ID_PAGE, new Rectangle3(0, 0, viewBox.width, viewBox.height));
    clips.add(CLIP_ID_BACKGROUND, writer.props.isOCRModeVector() ? new BackgroundClipper().clip(page) : null);

    // before writeContent to check URL detection during content writing
    for (OCDPaintable node : page.links(mediaBox, false))        
      this.addLinkBox(node.link(), node.bounds());    

    OCDMap idMap = page.content().idMap();
    for (OCDPaintable source : page.content().events())
    {      
      EPubOnEvent onEvent = onEvents.addSource(source);

      if (onEvent.hasOnclickTargetOnSamePage())
      {      
        OCDPaintable target = idMap.get(onEvent.targetID);
        if (target == null)
          Log.debug(this, ".create - event target not found: " + onEvent.targetID);
        else
          onEvents.addTargetTo(onEvent, target);
      }
    }

    if (mediaNode != null)
      this.writeNode(mediaNode);
    else if (flattenGraphics)
      this.writeTextContent(page.content());
    else
      this.writeContent(page.content());

    // SVG links are simply superposed to content
    // for (SVGPaintable box : links)
    // this.addPaintable(box);

    if (writer.props.isSpecimen())
      this.addPaintable(new SVGDemoText(this, this));

    if (writer.props.font64())
      for (SVGFont font : fonts)
      {
        font.addGlyphs(writer.fontsRemapped.get(font.fontFamily), ' ');
        css.write("@font-face", "font-family: " + font.fontFamily,
            "src: url(data:" + Mime.OTF + ";charset=utf-8;base64," + Base64.encodeUrl(OTF.Bytes(font)) + ") format(\"opentype\")");
      }

    this.defs.addDefs(this.clips.values());
    // SVGFilter filter=new SVGFilter("softlight", new
    // SVGFilterBlend("soft-light", "BackgroundImage","SourceGraphic"));
    // this.defs.addDefs();

    // for (String font : fonts)//after writeContent since used fonts discovered
    // during content writing
    // writer.fonts.add(font); // css.writeSvgFont(font, EPUB.FONT_FOLDER + font
    // + SVG.EXT);

    if (writer.props.isSpecimen())
      css.write(".c0", "fill:#ff0000;opacity:0.01;font-family:monospace;");

    for (Map.Entry<String, String> entry : cssClasses.entrySet())
      css.write("." + entry.getValue(), entry.getKey());

    String boxClass = "." + SVGLinkBox.CLASS;
    css.write(boxClass, "stroke-width:0;fill-opacity:0;fill:blue;cursor:pointer;");
    css.write(boxClass + ":hover", "stroke-width:0;fill-opacity:0.2;fill:white;");
    // css.write("text, tspan", "text-rendering : optimizeSpeed");
    css.writeComment(EPub.PrintedByReplica());
    if (writer.props.isSpecimen())
      css.writeComment("2UP.CH - DEMO VERSION");

    this.style.css = css.toString();
    return this;
  }

  public String viewIndex()
  {
    return viewCounter > 0 ? "_" + viewCounter : "";
  }

  @Override
  public int pageNb()
  {
    return page == null ? 0 : page.number();
  }

  private void writeTextContent(OCDGroup<OCDPaintable> group)
  {
    Log.debug(this,  ".writeTextContent - page="+group.pageNb()+", id="+group.id()+", tag="+group.tag+", "+group.string(false));
    for (OCDPaintable node : group.zOrderedGraphics())
      if (node.isText())
        this.writeText(node.asText());
      else if (node.isTextBlock())
        this.writeTextBlock(node.asTextBlock());
      else if (node.isGroup())
        this.writeTextContent((OCDGroup<OCDPaintable>)node);
  }

  private void writeContent(OCDGroup<OCDPaintable> group)
  {
    for (OCDPaintable node : group.zOrderedGraphics())
      if (!onEvents.ids.has(node.id()))
        this.writeNode(node);
  }

  private void writeNode(OCDPaintable node)
  {
    if (node.isPath())
      this.writePaths(node.asPath());
    else if (node.isImage())
    {
      OCDImage image = node.asImage();
      if (image.isMP4())
        videos.add(image);
      else if(image.isMP3())
        audios.add(image);
      else
        this.writeImage(image);
    } else if (node.isText())
      this.writeText(node.asText());
    else if (node.isTextBlock())
      this.writeTextBlock(node.asTextBlock());
    else if (node.isFlow())
      this.writeFlow(node.asFlow());
    else if (node.isGroup())
    {
      OCDGroup<OCDPaintable> group = node.asContent();
      if(group==null)
        Log.debug(this,  ".writeNode - null group: "+node);
      else if (group.isType("slideshow"))
      {
        String id = group.needID();
        for (OCDImage image : group.allImages())
          slideshows.add(id, image);
      } else
        this.writeContent(group);
    }
  }

  public String needCssClass(String prefix, String css)
  {
    if (css == null || css.isEmpty())
      return null;
    if (!this.cssClasses.has(css))
    {
      if (Str.IsVoid(prefix))
        prefix = "c";
      String viewIndex = viewIndex();
      String key = "c" + (cssClasses.size() + 1) + viewIndex;
      StringSet keys = new StringSet(cssClasses.values());
      for (int i = 1; i <= cssClasses.size() + 1; i++)
        if (!keys.has(key = prefix + i + viewIndex))
          break;
      this.cssClasses.put(css, key);
    }
    return this.cssClasses.get(css);
  }

  public Rectangle3 viewBox()
  {
    return viewBox;
  }

  public final void writeImage(OCDImage ocdImage)
  {
    if (ocdImage.isView())
      return;

    // Log.debug(this, ".writeImage - page="+ocdImage.page().entryFilename()+",
    // image="+ocdImage);

    boolean lossless = writer.props.png();

    // float sampling = writer.props.sampling();
    float jpeg = writer.props.jpeg();

    if (viewBox.intersects(ocdImage.bounds()) && ocdImage.width() > 0 && ocdImage.height() > 0)
    {
      String oldname = ocdImage.filename();
      if (!writer.ocd.existsZipEntry(OCD.IMAGES_DIR + oldname))
      {
        if (oldname.endsWith(".jpg"))
          oldname = oldname.replace(".jpg", ".png");
        else if (oldname.endsWith(".png"))
          oldname = oldname.replace(".png", ".jpg");
      }

      SVGImage image = new SVGImage(this, this, ocdImage, EPub.IMAGE_FOLDER + oldname, viewBox);

      // if (sampling > 0.5)
      // {
      // double minScale = 1f / sampling;
      // scale = writer.pageScale
      // * Math.min(Math.max(Math.abs(image.tm.sx()), Math.abs(image.tm.hx())),
      // Math.max(Math.abs(image.tm.sy()), Math.abs(image.tm.hy())));
      // scale = scale > 0 && scale < minScale ? scale / minScale : 1;
      // // if (image.width > writer.maxWidth || image.height >
      // writer.maxHeight)
      // // scale = Math.min(scale, Math.min(writer.maxWidth / image.width,
      // // writer.maxHeight / image.height));
      // }

      boolean isPng = lossless || oldname.endsWith(".png");

      String newname = File3.Extense(oldname, isPng ? ".png" : ".jpg");

      double scale = 1;
      int maxArea = writer.props.imageMaxArea(4000000);
      if (image.width * image.height > maxArea)
      {
        scale = Math.sqrt(maxArea / (double) (image.width * image.height));
        Log.debug(this, ".writeImage - maxArea exceeded: scale=" + scale);
      }

      // Log.debug(this, ".writeImage - oldname="+oldname+", newname="+newname);
      if (scale < 1)
      {
        newname = File3.postfix(oldname, "-s" + Math.round(1000 * scale));

        image.tm = image.tm.rescale(1 / scale);
        image.width = Math.max((int) (image.width * scale), 1);
        image.height = Math.max((int) (image.height * scale), 1);

        if (writer.images.hasnt(newname))
        {
          InputStream stream = writer.ocd.zipStream(OCD.IMAGES_DIR + oldname);
          if (stream != null)
            writer.write(EPub.IMAGE_DIR + newname, Image3.read(stream).decimate(image.width, image.height));
          else
            Log.warn(this, ".writeImage: entry not found: " + oldname);
        }
      } else if (writer.images.hasnt(newname))
        if (jpeg >= 0.8 || isPng)
        {
          if (ocdImage.hasData())
            writer.write(EPub.IMAGE_DIR + newname,
                ocdImage.isPNG() && lossless ? ocdImage.data() : Image3.Read(ocdImage.data()).write(isPng ? -1 : jpeg));
          else if (File3.extension(newname).equals(File3.extension(oldname)))
            writer.writeCopy(EPub.IMAGE_DIR + newname, writer.ocd.zipFile().entry(OCD.IMAGES_DIR + oldname));
          else if (!writer.write(EPub.IMAGE_DIR + newname, Image3.read(writer.ocd.zipStream(OCD.IMAGES_DIR + oldname))))
            Log.warn(this, ".writeImage: entry not found: " + oldname);
        } else if (!writer.write(EPub.IMAGE_DIR + newname, Image3.read(writer.ocd.zipStream(OCD.IMAGES_DIR + oldname))))
          Log.warn(this, ".writeImage: entry not found: " + oldname);
      image.filepath = EPub.IMAGE_FOLDER + newname;
      writer.images.add(newname);

      if (writer.props.isOCRModeVector() && ocdImage.isBackground())
        this.addPaintable(image, CLIP_ID_BACKGROUND);
      else
        this.addPaintable(image);
    }
  }

  public final void writePaths(OCDPath... paths)
  {
    if (!writer.props.isOCRModeVector())
      for (OCDPath path : paths)
      {
        // if (Math.abs(path.scaleX()) >= 0.001 && Math.abs(path.scaleY()) >=
        // 0.001)
        // {
        Rectangle3 r = path.bounds();
        if (r.isEmpty())
          r = new Rectangle3(r.x, r.y, 1, 1);
        if (viewBox.intersects(r))
        {
          this.addPaintable(new SVGPath(this, this, path, viewBox));
        }
        // } else
        // Log.debug(this, ".writePaths - removing excentric path: sx=" +
        // path.scaleX() + ", sy=" + path.scaleY() + ", stroke=" +
        // path.strokeWidth());
      }
  }

  public final void writeText(OCDText text)
  {
    // rotated and manually italicised text, better solution yet to implement
    if (viewBox.intersects(text.bounds()))
    {
      if (text.transform().isSheared(EPSILON) || text.transform().isMirrored() || text.isClipped())
        if (writer.props.vecText())
          this.writePaths(text.ocdPaths());
        else
          return;
      else
      {
        SVGText svgText = new SVGText(this, this, detectLinks(this.writeTextSpan(text, true)));

        // if (text.isVerticalMode())
        // svgText.setClasses.add(this.needCssClass("writing-mode:tb;"));
        this.addPaintable(svgText);
      }
    }
  }

  public final void writeTextBlock(OCDTextBlock tb)
  {
    // Log.debug(this,
    // ".writeTextBlock - page="+this.pageNb()+", tb="+tb.toString());
    // rotated and manually italicised text, better solution yet to implement
    boolean vecText = writer.props.vecText();
    List3<SVGTextSpan> spans = new List3<>();
    for (OCDTextLine line : tb)
    {
      List3<SVGTextSpan> lineSpans = new List3<>();
      for (OCDText text : line)
      {
        if (viewBox.intersects(text.bounds()))
          if (text.transform().isSheared(EPSILON) || text.transform().isMirrored() || text.isClipped())
            if (vecText)
            {
              this.writePaths(text.ocdPaths());
              lineSpans.add(this.writeTextSpan(text, false));
            } else
              continue;
          else
            lineSpans.add(this.writeTextSpan(text, true));
      }
      this.detectLinks(lineSpans.toArray(new SVGTextSpan[0]));
      spans.addAll(lineSpans);
    }

    if (!spans.isEmpty())
    {
      if (writer.props.isSpecimen())
        spans.add(new SVGDemoSpan(this));
      this.addPaintable(new SVGText(this, this, spans));
    }
  }

  public SVGTextSpan[] detectLinks(SVGTextSpan... spans)
  {
    String string = "";
    for (SVGTextSpan span : spans)
      string += span.text().string().toLowerCase();

    Matcher matcher = null;
    String target = writer.props.get("link_target", "_blank");
    if (writer.props.detectUrl())
      if ((matcher = writer.emailPattern.matcher(string)).find())
      {
        do
        {
          this.addLinkBox(matcher, "mailto:" + matcher.group(), target, spans);
        } while (matcher.find());
      } else if ((matcher = writer.urlPattern.matcher(string)).find())
      {
        do
        {
          String link = matcher.group();
          if (!link.startsWith("http://") && !link.startsWith("https://") && !link.startsWith("ftp://") && !link.startsWith("ftps://"))
            link = "http://" + link;
          if (link.lastIndexOf(".") > 12)
            this.addLinkBox(matcher, link, target, spans);
        } while (matcher.find());
      } else
        matcher = null;

    return spans;
  }

  public final void writeFlow(OCDFlow flow)
  {
    // Log.debug(this, ".writeFlow - " + page.number());
    for (OCDPaintable p : flow)
      if (p.isTextBlock())
        this.writeTextBlock(p.asTextBlock());
  }

  private SVGTextSpan[] writeTextSpan(OCDText text, boolean visible)
  {
    // String specimen = writer.props.specimen();
    boolean vecText = writer.props.vecText();
    boolean mobi = writer.props.isMobi();
    boolean font64 = writer.props.font64();

    List3<SVGTextSpan> frags = new List3<>();
    SVGTextSpan textSpan = new SVGTextSpan(null, this, text, viewBox, !visible || !vecText);

    // add link highlight for all links, not only matched ones !
    // if (matcher != null && writer.linkColor != null)
    // frags.addAll(textSpan.highlightFragment(matcher.start(), matcher.end(),
    // writer.linkColor));
    // else
    frags.add(textSpan);

    List3<SVGTextSpan> spans = new List3<>();
    for (SVGTextSpan span : frags)
      spans.addAll(span.subfontFragments());

    for (SVGTextSpan span : spans)
    {
      String oldname = text.fontname();
      String newname = XHTML.Fontname(span.fontFamily) + (span.override > 0 ? "-" + span.override : "");

      double scale = 1;
      Transform3 tm = text.transform();
      if (tm.isNonUniformedScaled(EPSILON))
        newname = File3.postfix(newname, "-s" + Math.round(1000 * (scale = tm.sy() / tm.sx())));

      if (writer.fontsRemapped.hasnt(newname))
      {
        SVGFont font = writer.ocd.fontHandler.get(oldname);
        if (font != null)
        {
          font = font.remap(newname, 1, scale, !vecText, !mobi, span.override, mobi);
          // if (Text.HasChar(writer.props.specimen()))
          // font = font.specimen(true, writer.props.specimen());
          if (!font64)
            writer.write(EPub.BOOK_DIR + File3.Extense(font.entryRepath(OCD.FONTS_DIR, EPub.FONT_FOLDER), ".otf"), OTF.Bytes(font));
          writer.fontsRemapped.put(newname, font);
        } else
          Log.warn(this, ".writeTextSpan: font entry not found: " + oldname);
      }

      SVGFont remappedFont = writer.fontsRemapped.get(newname);

      // Log.debug(this,
      // ".writeTextSpan - font="+newname+", uni="+span.unicodes.string()+",
      // remap="+span.unicodes.remap(writer.fonts.get(newname)));
      span.setFontFamily(newname);
      // remapping of odd unicode characters such as arabic ligature
      span.unicodes = span.unicodes.remap(remappedFont.remap);
      // this.fonts.add(newname);

      // page fonts map
      if (fonts.hasnt(newname))
      {
        SVGFont subset = remappedFont.subset();
        // because CSS fontweight should be set to bold when font declares
        // itself bold, same for italic
        subset.fontStyle = SVGFont.STYLE_NORMAL;
        subset.fontWeight = SVGFont.WEIGHT_NORMAL;
        fonts.put(newname, subset);
      }

      // fonts are embedded in each SVG page, hence populate page fonts
      if (font64)
      {
        SVGFont subFont = fonts.get(newname);
        subFont.addGlyphs(remappedFont, span.unicodes.codes());
      }
    }
    return spans.toArray(new SVGTextSpan[0]);
  }

  public final void addPaintable(SVGPaintable node)
  {
    this.addPaintable(node, null);
  }

  public final void addPaintable(SVGPaintable node, String clipID)
  {
    SVGPaintable added = null;

    if (!Str.HasChar(clipID) && node.hasClip())
      clipID = node.clipID;

    // Log.debug(this, ".addPaintable - clipID=" + clipID);

    if (Str.HasChar(clipID))
    {
      if (lastGroup != null && lastGroup.clipID.equals(clipID))
        lastGroup.addAll(node);
      else
        added = lastGroup = new SVGGroup(this, this, clipID, node);
      if (!this.clips.has(clipID))
      {
        OCDClip clip = this.page.definitions().clip(clipID);
        if (clip != null)
          this.clips.put(clipID, new SVGClip(defs, this, clip, viewBox, writer == null ? true : writer.trimClips));
        else
          Log.debug(this, ".addPaintable - clip not found: " + clipID);
      }
    } else
    {
      lastGroup = null;
      node.setParent(this);
      added = node;
    }

    if (added != null)
    {
      added.setParent(parent);
      if (svgGroup == null)
      {
        added.setParent(this);
        nodes.add(added);
      } else
      {
        added.setParent(svgGroup);
        svgGroup.nodes().add(added);
      }
    }
  }

  public final void addLinkBox(String uri, Rectangle3 box)
  {
    boolean isWWW = OCDAnnot.IsWWW(uri);
    boolean isMailto = OCDAnnot.IsMailto(uri);
    if (!isWWW && !isMailto && Str.HasChar(uri))
    {
      if (!(uri.startsWith("@") || uri.startsWith("#")))
        uri = File3.Extense(uri, XHTML.FILE_EXTENSION);
    }
    if (Str.HasChar(uri))
    {
      Rectangle3 rect = new Rectangle3(box.x - viewBox.x, box.y - viewBox.y, box.width, box.height);
      this.urlLinks.addLink(uri, rect, writer.props.get("link_target", isWWW ? "_blank" : ""));
    }
  }

  public final void addLinkBox(Rectangle3 box, String uri, String target)
  {
    Rectangle3 rect = new Rectangle3(box.x - viewBox.x, box.y - viewBox.y, box.width, box.height);
    // checks already defined links
    for (EPubLinkBox link : urlLinks)
      if (link.box.overlap(rect) > 0.5)
        return;
    if (Str.HasChar(uri))
      this.urlLinks.addLink(uri, rect, target);
  }

  public void addLinkBox(Matcher matcher, String link, String target, SVGTextSpan... spans)
  {
    int start = matcher.start();
    int end = matcher.end();

    int pos = 0;

    Transform3 tStart = null;
    Transform3 tEnd = null;

    SVGFont font = null;
    float fontsize = -1;
    for (SVGTextSpan span : spans)
    {
      OCDText text = span.text();

      if (font == null)
        font = text.font();

      if (fontsize < 1)
        fontsize = text.fontsize();

      int size = text.length();

      if (start >= pos && start < pos + size)
        tStart = text.transform().moveTo(text.coordAt(start - pos));

      if (end >= pos && end <= pos + size)
        tEnd = text.transform().moveTo(text.coordAt(end - pos));

      pos += size;
    }

    float ascent = SVGFont.DEFAULT_ASCENT;
    float descent = SVGFont.DEFAULT_DESCENT;
    if (font != null)
    {
      ascent = font.ascent(ascent);
      descent = font.descent(descent);
    }

    Rectangle3 box = new Rectangle3(0, -ascent * fontsize, 0, (ascent - descent) * fontsize);
    Shape shape1 = tStart.transform(box);
    Shape shape2 = tEnd.transform(box);

    box = new Rectangle3(shape1.getBounds2D());
    box.add(shape2.getBounds2D());
    // Zen.debug(this, ".bounds - ascent=" + ascent + ", descent=" + descent +
    // ", width=" + r.width + ", height=" + r.height + ", fontsize=" +
    // fontsize);
    if (box.width <= 0)// may happen with "l", breaking intersection checks
      box.width = 0.001f;
    if (box.height <= 0)// may happen with "-", breaking intersection checks
      box.height = 0.001f;

    // Log.debug(this, ".addLinkAnnot - page "+this.pageNb()+": "+link+" "+box);

    this.addLinkBox(box, link, target);
  }

  public OCDPage ocdPage()
  {
    return page;
  }

  public String filename()
  {
    return "page-" + page.number() + SVG.EXT;
  }

  @Override
  public Collection<? extends OCDNode> children()
  {
    return nodes;
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    xml.write("xmlns", "http://www.w3.org/2000/svg");
    xml.write("xmlns:xlink", "http://www.w3.org/1999/xlink");
    xml.write("id", id);
    xml.write("width", width);
    xml.write("height", height);
    xml.write("version", "1.1");
    // since svg page may be scaled viewbox may not be equal to svg viewbox
    if (this.isMediaView())
      xml.write("viewBox", "0 0 " + viewBox.intWidth() + " " + viewBox.intHeight());
    else
      xml.write("viewBox", "0 0 " + writer.pageWidth + " " + writer.pageHeight);
    xml.write("preserveAspectRatio", "xMidYMid meet");
    if (Str.HasData(pageStyle))
      xml.write("style", pageStyle);
    return this.children();
  }

  public String toString(double d)
  {
    return Xml.toString(d, DF);
  }

  public String toString(float[] data)
  {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < data.length; i++)
      sb.append(toString(data[i])).append((i < data.length - 1 ? " " : ""));
    return sb.toString();
  }
}
