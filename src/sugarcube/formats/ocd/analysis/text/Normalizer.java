package sugarcube.formats.ocd.analysis.text;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Props;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.formats.ocd.objects.OCDPage;
import sugarcube.formats.ocd.objects.OCDText;
import sugarcube.formats.ocd.objects.OCDTextBlock;
import sugarcube.formats.ocd.objects.OCDTextLine;
import sugarcube.formats.ocd.objects.lists.OCDBlocks;

public class Normalizer
{
  private Props props = new Props();

  public Normalizer(Props props)
  {
    if (props != null)
      this.props.putAll(props);
  }

  public void apply(OCDPage page)
  {
    mergeLines(page);
    for (OCDTextBlock block : page.content().blocks())
      splitBlocks(block, 0);
    for (OCDTextBlock block : page.content().blocks())
      splitTabSpace(block, 0);
  }

  public static boolean splitBlocks(OCDTextBlock block, int counter)
  {
    if (counter > 1000)
    {
      Log.warn(Normalizer.class, ".splitLines - neverending loop !");
      return true;
    }
    OCDTextLine[] lines = block.lines();
    if (lines.length < 2)
      return false;

    OCDTextLine prev = lines[0];
    Rectangle3 prevBox = prev.bounds();
    for (int i = 1; i < lines.length; i++)
    {
      OCDTextLine line = lines[i];
      Rectangle3 box = lines[i].bounds();
//      Log.debug(Normalizer.class, ".splitBlocks : "+prev.italics()+", "+line.italics()+", "+prev.stringValue());
      if (Distance(prevBox, box, -1) > 0.8 || prev.italics() > 0.9 != line.italics() > 0.9)
      {
        OCDTextBlock oldBlock = block.split(line, true);
        if (oldBlock.isTextEmpty())
          oldBlock.remove();
      }
      prev = line;
      prevBox = box;
    }
    if (block.isTextEmpty())
      block.remove();
    return counter > 0;
  }

  public static double Distance(Rectangle3 a, Rectangle3 b, double height)
  {
    return a.distance(b) / (height > 0 ? height : (a.height + b.height) / 2);
  }

  public static void mergeLines(OCDPage page)
  {
    OCDBlocks blocks = page.content().blocks();
    OCDTextBlock prevBlock = null;
    for (OCDTextBlock block : blocks)
    {
      Rectangle3 prev = prevBlock == null ? null : prevBlock.last().bounds();
      Rectangle3 curr = block.first().bounds();
      if (prev != null && prev.cy() < curr.cy() && prev.overlapX(curr) > 0.5 && Distance(prev, curr, -1) < 0.6)
      {
        OCDText last = prevBlock.lastText();
        OCDText first = block.firstText();
        // Log.debug(Normalizer.class, ".mergeLines - " + last.string() + "\n" +
        // first.string());
        if (last != null && first != null && Math.abs(last.scaledFontsize() - first.scaledFontsize()) < 3)
        {
          String text = last.glyphString().trim();
          int c = text.charAt(text.length() - 1);
          if (c != '.' && c != ';' && c != ':')
          {
            // Log.debug(Normalizer.class, ".mergeLines - yeaaaaaaaaaaaah");
            prevBlock.merge(block);
            continue;
          }
        }
      }
      prevBlock = block;
    }
  }

  public static boolean splitTabSpace(OCDTextBlock block, int counter)
  {
    if (counter > 1000)
    {
      Log.warn(Normalizer.class, ".splitTabSpace - neverending loop !");
      return true;
    }
    OCDTextLine[] lines = block.lines();
    if (lines.length != 1 || lines[0].nbOfChildren() < 2)
      return false;

    OCDText[] texts = lines[0].texts();
    OCDText prevText = texts[0];
    Rectangle3 prevBox = prevText.bounds();
    for (int i = 1; i < texts.length; i++)
    {
      Rectangle3 box = texts[i].bounds();
      if (box.x > prevBox.x && box.minX() - prevBox.maxX() > (box.height + prevBox.height) / 2 * 3)
      {
        // Log.debug(this, ".applyPage - orphan: " + texts[i].uniString());
        if (block.splitX((box.minX() + prevBox.maxX()) / 2) != null)
          return splitTabSpace(block, ++counter);
      }
      prevText = texts[i];
      prevBox = box;
    }
    return counter > 0;
  }

  public static Normalizer New(Props props)
  {
    return new Normalizer(props);
  }
}
