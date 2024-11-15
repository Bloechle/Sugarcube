package sugarcube.formats.ocd.objects;

import sugarcube.common.data.collections.IntOccurrences;
import sugarcube.common.data.collections.Occurrences;
import sugarcube.common.data.collections.Str;
import sugarcube.common.graphics.geom.Coords;
import sugarcube.common.graphics.geom.Line3;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.numerics.Math3;
import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.OCD;

import java.util.Arrays;
import java.util.Collections;

public class OCDTextLine extends OCDGroup<OCDText>
{
  public class Canon extends OCDBox
  {
    public Canon()
    {
    }

    public double dFirstX(OCDTextLine line)
    {
      return OCDTextLine.this.x() - line.x();
    }

    public double dLastX(OCDTextLine line)
    {
      return lastX() - line.lastX();
    }

    @Override
    public Canon refresh()
    {
      OCDText first = first();
      if (first != null)
        this.setRect(first.canon.refresh());
      for (OCDText text : children())
        this.include(text.canon.refresh());
      return this;
    }

    public Rectangle3 refresh(double blockT, double lineT)
    {
      return refresh().inflate(height() * lineT, height() * blockT);
    }

    public boolean ends()
    {
      int code = nodes.last().last();
      if (Character.isDigit(code))
        return false;
      else if (Character.isLetter(code))
        return Character.isUpperCase(code);
      else
        return true;
    }

    public boolean begins()
    {
      int code = nodes.first().first();
      return Character.isDigit(code) || !(Character.isLetter(code) && Character.isLowerCase(code));
    }

    public boolean isUppercase()
    {
      int caps = 0;
      int count = 0;
      for (OCDText text : nodes)
        for (int i = 0; i < text.length(); i++)
        {
          int c = text.codeAt(i);
          if (Character.isLetter(c))
          {
            if (Character.isUpperCase(c))
              caps++;
            if (c != ' ')
              count++;
          }
        }
      return count != 0 && caps > count / 2;
    }

    public boolean isLettrine()
    {
      return nbOfChars() == 1 && Character.isLetter(first().first());
    }

    @Override
    public Canon copy()
    {
      Canon canon = new Canon();
      canon.setRect(this);
      return canon;
    }
  }

  public Canon canon = null;

  public OCDTextLine(OCDNode parent)
  {
    super(OCDGroup.TEXTLINE, parent);
  }

  public OCDTextLine(OCDNode parent, OCDText text)
  {
    super(OCDGroup.TEXTLINE, parent);
    this.add(text);
    this.zOrder = text.zOrder;
    this.groupID = text.groupID;
    this.setClassname(text.classname());
  }

  public OCDText newText()
  {
    OCDText text = new OCDText(this);
    this.add(text);
    return text;
  }

  public OCDText newText(String content, double fontsize, String fontname, double x, double y)
  {
    OCDText text = newText();
    text.setFontname(fontname);
    text.setUnicodes(content);
    text.setFontsize(fontsize);
    text.setXY(x, y);
    return text;
  }

  public OCDText[] texts()
  {
    return this.children().toArray(new OCDText[0]);
  }

  public OCDText[] zTexts()
  {
    OCDText[] texts = texts();
    Arrays.sort(texts, (t1, t2) -> Float.compare(t1.zOrder, t2.zOrder));
    return texts;
  }

  @Override
  public OCDTextLine canonize()
  {
    for (OCDText text : this)
      text.canonize();
    if (this.canon == null)
      this.canon = new Canon();
    this.canon.refresh();
    return this;
  }

  public OCDTextLine retext(String text)
  {
    return retext(text, 6, 16);
  }

  public OCDTextLine retext(String text, int minFS, int maxFS)
  {

    OCDText first = this.first();

    String fontname = first.fontname;
    float fontsize = first.fontsize;
    if (fontsize < minFS)
      fontsize = minFS;
    if (fontsize > maxFS)
      fontsize = maxFS;
    float x = first.x();
    float y = first.y();

    this.children().clear();

    for (String tk : text.split(" "))
    {
      OCDText t = new OCDText(this, tk + " ", fontname, fontsize);
      t.setXY(x, y);
      x += t.bounds().width;
      this.add(t);
    }

    this.modifyPage();

    return this;
  }

  @Override
  public OCDTextLine uncanonize()
  {
    this.canon = null;
    for (OCDText text : this)
      text.uncanonize();
    return this;
  }

  public float italics()
  {
    int sum = 0;
    int italics = 0;
    for (OCDText text : nodes)
    {
      String font = text.fontname("").toLowerCase();
      int chars = text.nbOfChars();
      if (font.contains("italic") || font.contains("oblique"))
        italics += chars;
      sum += chars;
    }
    return sum == 0 ? -1 : italics / (float) sum;
  }

  public float x()
  {
    OCDText text = nodes.first();
    return text == null ? 0 : text.x();
  }

  public float y()
  {
    OCDText text = nodes.first();
    return text == null ? 0 : text.y();
  }

  public float lastX()
  {
    OCDText text = nodes.last();
    return text == null ? 0 : text.lastX();
  }

  public float lastY()
  {
    OCDText text = nodes.last();
    return text == null ? 0 : text.y();
  }

  public float meanX()
  {
    float mean = 0;
    for (OCDText text : nodes)
      mean += text.x();
    return mean / size();
  }

  public float meanY()
  {
    float mean = 0;
    for (OCDText text : nodes)
      mean += text.y();
    return mean / size();
  }

  public float maxFontsize()
  {
    float max = -1;
    for (OCDText text : this)
      if (text.fontsize > max)
        max = text.fontsize;
    return max;
  }

  public float maxScaledFontsize()
  {
    float max = -1;
    for (OCDText text : this)
      if (text.scaledFontsize() > max)
        max = text.scaledFontsize();
    return max;
  }

  public float minFontsize()
  {
    float min = -1;
    for (OCDText text : this)
      if (text.fontsize < min)
        min = text.fontsize;
    return min;
  }

  public int nbOfSpaces()
  {
    int counter = 0;
    for (OCDText text : this)
      counter += text.unicodes.nbOfSpaces();
    return counter;
  }

  public boolean hasSpace()
  {
    for (OCDText text : this)
      if (text.hasSpace())
        return true;
    return false;
  }

  public boolean hasUnicodes(int... uni)
  {
    for (OCDText text : this)
      for (int i = 0; i < text.unicodes.length(); i++)
        for (int c : uni)
          if (c == text.unicodes.codeAt(i))
            return true;
    return false;
  }

  public int nbOfChars()
  {
    int chars = 0;
    for (OCDText text : this)
      chars += text.unicodes().length();
    return chars;
  }

  public int nbOfTexts()
  {
    return this.nodes.size();
  }

  public int roundFontsize()
  {
    return Math.round(fontSize());
  }

  public float fontSize()
  {
    int sumLength = 0;
    float sumFontsize = 0;
    for (OCDText text : this)
    {
      int size = text.length();
      sumFontsize += text.scaledFontsize() * size;
      sumLength += size;
    }
    return nodes.isEmpty() ? 0 : sumFontsize / sumLength;
  }

  public boolean isFontBold()
  {
    return fontName().toLowerCase().contains("bold");
  }

  public String fontName()
  {
    Occurrences<String> names = new Occurrences<String>();
    for (OCDText text : nodes)
      names.inc(text.fontname());
    return names.isEmpty() ? "" : names.max();
  }

  public OCDTextBlock textBlock()
  {
    return OCD.isTextBlock((OCDNode) parent) ? (OCDTextBlock) parent : null;
  }

  public OCDTextLine previous()
  {
    OCDTextLine prev = null;
    OCDTextBlock block = textBlock();
    if (block != null)
      for (OCDTextLine line : block)
      {
        if (line == this)
          return prev;
        prev = line;
      }
    return prev;
  }

  public OCDTextLine next()
  {
    return next(false);
  }

  public OCDTextLine next(boolean crossBlocks)
  {
    boolean next = false;
    OCDTextBlock block = textBlock();
    if (block != null)
    {
      for (OCDTextLine line : block)
      {
        if (next)
          return line;
        if (line == this)
          next = true;
      }
      if (crossBlocks)
      {
        block = block.next(true);
        if (block != null)
          return block.first();
      }
    }
    return null;
  }

  @Override
  public String sticker()
  {
    return (hasLabel() ? label() : Str.ToString(stringValue(), 35));
  }

  public String stringValue()
  {
    StringBuilder sb = new StringBuilder();
    for (OCDText text : this)
      sb.append(text.unicodes);
    return sb.toString();
  }

  public String uniString()
  {
    return glyphString();
  }

  // public String uniString(boolean respace)
  // {
  // StringBuilder sb = new StringBuilder();
  // OCDText last = null;
  // for (OCDText text : this)
  // {
  // if (last != null && !last.endsWithSpace() && !text.startsWithSpace())
  // {
  // Rectangle3 boxLast = last.bounds();
  // Rectangle3 box = text.bounds();
  // if (box.minX() - boxLast.maxX() > (box.height + boxLast.height) / 3)
  // sb.append(" ");
  // }
  // sb.append(text.glyphString());
  // last = text;
  // }
  // return sb.toString();
  // }

  public String glyphString()
  {
    StringBuilder sb = new StringBuilder();
    for (OCDText text : this)
      sb.append(text.glyphString());
    return sb.toString();
  }

  public String htmlString()
  {
    return htmlString(null).toString();
  }

  public StringBuilder htmlString(StringBuilder sb)
  {
    if (sb == null)
      sb = new StringBuilder();
    for (OCDText text : this)
      text.htmlString(sb);
    return sb;
  }

  public String hexaValue()
  {
    StringBuilder sb = new StringBuilder();
    for (OCDText text : this)
    {
      sb.append(text.unicodes.toHexaXML());
      sb.append(" ");
    }
    return sb.toString();
  }

  public double distance(OCDTextLine line, boolean normalize)
  {
    Rectangle3 a = this.bounds();
    Rectangle3 b = line.bounds();
    return normalize ? a.distance(b) / ((a.height + b.height) / 2) : a.distance(b);
  }

  @Override
  public OCDTextLine copy()
  {
    OCDTextLine line = new OCDTextLine(parent());
    super.copyTo(line);
    if (canon != null)
      line.canon = this.canon.copy();
    return line;
  }

  @Override
  public Rectangle3 bounds()
  {
    return canon != null ? canon : super.bounds();
  }

  public Point3 position()
  {
    return new Point3(x(), y());
  }

  public Line3 baseline()
  {
    return new Line3(x(), y(), lastX(), lastY());
  }

  public int guessBaseline()
  {
    IntOccurrences yPos = new IntOccurrences();
    for (OCDText text : this)
      yPos.inc(Math3.Round(text.yCoord), text.length());
    return yPos.max();
  }

  public int guessFontsize()
  {
    IntOccurrences fontsizes = new IntOccurrences();
    for (OCDText text : this)
      fontsizes.inc(Math3.Round(text.scaledFontsize()), text.length());
    return fontsizes.max();
  }

  public void sortTexts(boolean leftRight)
  {
    Collections.sort(nodes, (t1, t2) -> (Math3.Sign(t1.x() - t2.x(), leftRight)));
  }

  public OCDTextLine needEndSpace()
  {
    OCDText last = this.last();
    if (last != null)
      last.needEndSpace();
    return this;
  }

  public OCDTextLine segmentBySpace()
  {

    // if (text.endsWithSpace() && index==text.length())
    // {
    // OCDTextLine line = text.textLine();
    // OCDText newText = new OCDText(line);
    // newText.setUnicodes(c);
    // newText.setX(text.lastX());
    // newText.setY(text.y());
    // newText.setFontsize(text.fontsize());
    // newText.setFont(text.fontname());
    // line.add(newText);
    // tab.pager.refresh(line);
    // tab.pager.selector.select(newText, 0);
    // } else
    return this;

  }

  public boolean startsWith(String start)
  {
    String text = string();
    return text.length() > 0 && text.startsWith(start);
  }

  public boolean startsWithUppercase()
  {
    String text = string().trim();
    return text.length() > 0 && Character.isUpperCase(text.charAt(0));
  }

  public boolean endsWithPunctuation()
  {
    String text = string().trim();
    return text.length() > 0 && !Character.isLetterOrDigit(text.charAt(text.length() - 1));
  }

  public Point3 firstPos(boolean skipSymbols)
  {
    for (OCDText text : this)
    {
      String uni = text.glyphString();
      Coords coords = text.coords();
      for (int i = 0; i < uni.length(); i++)
      {
        char c = uni.charAt(i);
        if (!Character.isSpaceChar(c))
        {
          if (!skipSymbols || skipSymbols && Character.isLetterOrDigit(c))
          {
            if (i < coords.size())
              return coords.get(i);
          }
        }
      }
    }
    return null;
  }

  public boolean isTextEmpty()
  {
    return isEmpty() || nodes.size() == 1 && nodes.first().unicodes.isEmpty();
  }

  public boolean isCharEmpty()
  {
    return isEmpty() || uniString().trim().isEmpty();
  }


  public OCDTextLine alignRight(float maxX)
  {
    this.shift(maxX - bounds().maxX(), 0);
    return this;
  }

  @Override
  public String toString()
  {
    return "OCDTextLine[" + this.stringValue() + "]" + "\nID[" + id() + "]" + "\nZOrder[" + this.zOrder + "]" + "\nClassID[" + this.groupID + "]"
        + "\nClassname[" + this.classname() + "]" + "\nParent[" + (parent == null ? "null" : parent.sticker()) + "]" + "\nBounds" + this.bounds()
        + "\nOCD[\n" + Xml.toString(this) + "]\n";
  }
}
