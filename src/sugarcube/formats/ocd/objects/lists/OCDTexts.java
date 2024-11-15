package sugarcube.formats.ocd.objects.lists;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Map3;
import sugarcube.common.data.collections.Set3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.formats.ocd.objects.*;

import java.util.Collections;

public class OCDTexts extends OCDPaintables<OCDText>
{
  public OCDTexts()
  {

  }

  public OCDTexts(Iterable<OCDText> iterable)
  {
    for (OCDText t : iterable)
      this.add(t);
  }

  public OCDTexts verticalRange(double minY, double maxY)
  {
    float cy = 0;
    OCDTexts texts = new OCDTexts();
    for (OCDText text : this)
      if ((cy = text.bounds().cy()) > minY && cy < maxY)
        texts.add(text);
    return texts;
  }

  public OCDTexts sort(boolean ascendant)
  {
    Collections.sort(this, ascendant ? OCDText.yComparator() : OCDText.yComparator_());
    return this;
  }

  public OCDTexts ySort()
  {
    this.sortY();
    return this;
  }

  public OCDTexts xSort()
  {
    this.sortX();
    return this;
  }

  @Override
  public OCDPaintables<OCDText> sortY()
  {
    Collections.sort(this, OCDText.yComparator());
    return this;
  }

  @Override
  public OCDPaintables<OCDText> sortX()
  {
    Collections.sort(this, OCDText.xComparator());
    return this;
  }

  public OCDTexts add(OCDTextLine line)
  {
    for (OCDText text : line)
      this.add(text);
    return this;
  }

  public OCDTexts add(OCDTextBlock block)
  {
    for (OCDTextLine line : block)
      for (OCDText text : line)
        this.add(text);
    return this;
  }

  public Map3<OCDText, Rectangle3> boxMap()
  {
    Map3<OCDText, Rectangle3> map = new Map3<>();
    for (OCDText text : this)
      map.put(text, text.bounds());
    return map;
  }

  public OCDTextBlock blockize(OCDNode parent, boolean removeFromOCD)
  {
    return blockize(new OCDTextBlock(parent), removeFromOCD);
  }

  public OCDTextBlock blockize(OCDTextBlock block, boolean removeFromOCD)
  {
    if (block == null)
    {
      Log.warn(this, ".blockize - block==null");
      return null;
    }
    this.sortY();
    Map3<OCDTextLine, Rectangle3> blocks = new Map3<>();
    while (this.isPopulated())
    {
      OCDText text = this.removeFirst();
      if (removeFromOCD)
      {
        OCDTextLine oLine = text.textLine();
        if (oLine != null && oLine.remove(text) && oLine.isCharEmpty())
        {
          OCDTextBlock oBlock = oLine.textBlock();
          if (oBlock != null && oBlock.remove(oLine) && oBlock.isCharEmpty())
          {
            OCDGroup<OCDPaintable> oGroup = (OCDGroup<OCDPaintable>) oBlock.parent();
            if (oGroup != null)
              oGroup.remove(oBlock);
          }
        }
      }

      text.setParent(block);
      Rectangle3 tBox = text.bounds();
      Set3<OCDTextLine> sameLines = new Set3<>();
      for (OCDTextLine line : blocks.keySet())
        if (tBox.overlapY(blocks.get(line)) > 0.5)
          sameLines.add(line);

      OCDTextLine line = sameLines.isEmpty() ? new OCDTextLine(block) : sameLines.removeFirst();
      line.add(text);

      while (sameLines.isPopulated())
      {
        OCDTextLine moreLine = sameLines.removeFirst();
        line.addAll(moreLine.allTexts());
        blocks.remove(moreLine);
      }
      blocks.put(line, line.bounds());
    }

    block.clear();
    block.addAll(blocks.keyList());
    block.sort(true, true);

    return block;
  }

  public Rectangle3 textBox()
  {
    if (this.isEmpty())
      return null;
    Rectangle3 box = this.first().textBounds();
    float minX = box.minX();
    float minY = box.minY();
    float maxX = box.maxX();
    float maxY = box.maxY();
    for (OCDText text : this)
    {
      box = text.textBounds();
      if (box.minX() < minX)
        minX = box.minX();
      if (box.maxX() > maxX)
        maxX = box.maxX();
      if (box.minY() < minY)
        minY = box.minY();
      if (box.maxY() > maxY)
        maxY = box.maxY();
    }
    return Rectangle3.Points(minX,  minY,  maxX,  maxY);
  }

  public String string()
  {
    StringBuilder sb = new StringBuilder();
    for (OCDText text : this)
      sb.append(text.glyphString());
    return sb.toString();
  }
}
