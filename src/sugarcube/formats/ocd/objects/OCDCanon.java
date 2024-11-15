package sugarcube.formats.ocd.objects;

import sugarcube.common.graphics.geom.Coords;
import sugarcube.formats.ocd.objects.font.SVGFont;


public class OCDCanon extends OCDBox
{
  public OCDText text;
  public float radians;
  public Coords coords;
  public float[] tm;

  public OCDCanon(OCDText text)
  {
    this.text = text;
  }

  public OCDText removeLast()
  {
    text.unicodes.removeLast();
    this.coords.removeLast();
    this.refresh();
    return text;
  }

  public OCDText addSpace(float lastX, float lastY)
  {
    this.addChar(OCDText.SPACE, lastX, lastY);
    this.refresh();
    return text;
  }

  public OCDText addChar(int code, float lastX, float lastY)
  {
    text.unicodes().append(code);
    this.coords.add(lastX, lastY);
    this.refresh();
    return text;
  }

  /**
   * Splits this token into two tokens, this one is modified and is the end of
   * the original token, the returned one is a new token containing the
   * beginning of the original token.
   */
  public OCDText split(int index)
  {
    OCDText split = text.copy();
    split.unicodes = text.unicodes.split(index);
    split.canon.coords = this.coords.split(index);
    this.respace();
    this.refresh();
    split.canon.respace();
    split.canon.refresh();
    // the id is transferred to the first text part (ids must be unique)
    split.setID(text.id());
    text.setID(null);
    // Log.debug(this, ".split - [" + text.string() + "][" + string() + "]: "
    // + text.canon.coords.xStrInt() + this.coords.xStrInt());
    return split;
  }

  private void respace()// since spaces may be very big due to retroactive
                        // merging done in a previous canonization process
  {
    int max = coords.size() - 1;
    if (text.startsWithSpace() && max > 0)
      coords.setX(0, coords.x(1) - text.fontsize / 2);
    if (text.endsWithSpace())
      coords.setX(max, coords.get(max - 1).x + text.fontsize / 2);
  }

  public OCDText unspaceEnd()
  {
    while (text.unicodes.length() > 0 && text.unicodes.last() == OCDText.SPACE)
    {
      text.unicodes.removeLast();
      coords.removeLast();
      refresh();
    }
    return text;
  }

  public OCDText unspaceStart()
  {
    while (!text.unicodes.isEmpty() && text.unicodes.first() == OCDText.SPACE)
    {
      text.unicodes.removeFirst();
      coords.removeFirst();
      refresh();
    }
    return text;
  }

  public OCDText trim()
  {
    return this.unspaceEnd().canon.unspaceStart();
  }

  @Override
  public OCDCanon refresh()
  {
    float tx = coords.x();
    float ty = coords.y();
    text.setXY(tx, ty);
    float ascent = SVGFont.DEFAULT_ASCENT;
    float descent = SVGFont.DEFAULT_DESCENT;
    SVGFont font = text.font();
    if (font != null)
    {
      ascent = font.ascent(ascent);
      descent = font.descent(descent);
    }
    this.setPoints(tx, ty - ascent * text.scaledFontsize(), text.lastX(), ty - descent * text.scaledFontsize());
    return this;
  }

  //
  // public Shape3 shape()
  // {
  // return radians == 0.0 ? bounds() :
  // Transform3.rotateInstance(radians).transform(bounds());
  // }

  public void copyTo(OCDCanon canon)
  {
    canon.setRect(this);
    canon.radians = radians;
    canon.coords = coords.copy();
    canon.tm = tm;
  }
}