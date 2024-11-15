package sugarcube.formats.pdf.reader.pdf.node.font;

import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.Unicodes;
import sugarcube.common.graphics.geom.Circle3;
import sugarcube.common.graphics.geom.Line3;
import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.node.font.encoding.Encoding;

import java.awt.*;
import java.util.Map;


public class ReaderCFF extends FontReader
{

  private CFF cff;

  public ReaderCFF(FontDescriptor desc, CFF cff)
  {
    super("Type1CFont", desc);
    this.cff = cff;
  }

  @Override
  public String sticker()
  {
    return "ReaderCFF";
  }

  @Override
  public Transform3 transform()
  {
    return new Transform3(cff.fontMatrix());
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("ReaderCFF");
    sb.append("FontMatrix[" + Zen.Array.String(cff.fontMatrix()) + "]");
    sb.append("\nisCID[" + cff.isCID + "]");

    if (cff.glyphs.charStrings != null)
    {
      sb.append("\nT1Glyphs CharStrings:");
      for (Map.Entry<String, byte[]> entry : cff.glyphs.charStrings.entrySet())
        sb.append(" ").append(entry.getKey());
    }

    return sb.toString();
  }

  @Override
  public PDFGlyph outline(String name, Unicodes uni, int code)
  {
    if (name.equals(Encoding.NOTDEF))
      name = this.descriptor.pdfFont.encoding.nameFromCode(code); // Log.debug(this,
                                                                  // ".outline -
                                                                  // notdef:
                                                                  // "+code+",
                                                                  // "+name);
    Path3 path = cff.glyphs.getPath(name, uni == null ? null : uni.string(), code);
    if (path != null)
      path = path.closeSubpaths();// ensures subpaths are closed
    // Log.debug(this, ".outline - " + name + ": uni[" + (uni == null ? "null"
    // : uni.string()) + "], code[" + code + "], path="+(path==null? "null" :
    // ":-)"));
    PDFGlyph outline = (path == null ? null : new PDFGlyph(path.transform(transform())));
    return outline == null ? null : outline.reverseY();
  }

  @Override
  public void paint(Graphics3 g, PDFDisplayProps props)
  {
    float scale = props.displayScaling;
    int size = (int) (48 * scale);
    int d = size / 2;
    int x = d;
    int y = size + d;

    g.setColor(Color.BLACK);
    for (String name : cff.glyphs.charStrings.keySet())
    {
      if (x > g.width() - (size + size / 2))
      {
        x = d;
        y += (d + size);
      }

      PDFGlyph glyph = this.outline(name, new Unicodes(name), -1);
      if (glyph == null)
        continue;
      // System.out.println("font="+this.fontname+" glyph="+code+"
      // path="+glyph.path.stringValue(0));
      // XED.LOG.warn(this,".paint - empty glyph: fontname="+fontname+"
      // code="+code+" path="+glyph.path.stringValue(0.1f));
      g.setColor(Color.BLACK);
      g.fill(new Transform3(size, 0, 0, size, x, y).transform(glyph.path));
      g.setColor(Color.GREEN.darker());
      g.fill(new Circle3(x, y, 2));
      g.draw(new Line3(x, y, x + (int) (glyph.width()), y));
      g.graphics().drawString(name, x, y + size / 2);
      x += (d + 3 * size / 2);
    }
  }
}
