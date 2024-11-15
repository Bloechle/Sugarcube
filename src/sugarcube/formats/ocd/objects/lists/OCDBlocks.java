package sugarcube.formats.ocd.objects.lists;

import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.formats.ocd.objects.OCDTextBlock;
import sugarcube.formats.ocd.objects.OCDTextLine;

import java.util.Collections;
import java.util.regex.Pattern;

public class OCDBlocks extends OCDPaintables<OCDTextBlock>
{
  public OCDBlocks()
  {

  }

  public OCDBlocks minChars(int min)
  {
    OCDBlocks blocks = new OCDBlocks();
    for (OCDTextBlock block : this)
      if (block.nbOfChars() >= min)
        blocks.add(block);
    return blocks;
  }

  public OCDBlocks maxChars(int max)
  {
    OCDBlocks blocks = new OCDBlocks();
    for (OCDTextBlock block : this)
      if (block.nbOfChars() <= max)
        blocks.add(block);
    return blocks;
  }

  public OCDBlocks regex(String regex)
  {
    OCDBlocks blocks = new OCDBlocks();
    try
    {
      Pattern pat = Pattern.compile(regex);
      for (OCDTextBlock block : this)
        if (pat.matcher(block.string()).find())
          blocks.add(block);
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return blocks;
  }

  public OCDBlocks notRegex(String regex)
  {
    OCDBlocks blocks = new OCDBlocks();
    try
    {
      Pattern pat = Pattern.compile(regex);
      for (OCDTextBlock block : this)
        if (!pat.matcher(block.string()).find())
          blocks.add(block);
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return blocks;
  }

  public OCDBlocks uppercases()
  {
    OCDBlocks blocks = new OCDBlocks();
    String text = null;
    for (OCDTextBlock block : this)
      if ((text = block.string()).toUpperCase().equals(text))
        blocks.add(block);
    return blocks;
  }

  public OCDBlocks lowercases()
  {
    OCDBlocks blocks = new OCDBlocks();
    String text = null;
    for (OCDTextBlock block : this)
      if ((text = block.string()).toLowerCase().equals(text))
        blocks.add(block);
    return blocks;
  }

  public OCDBlocks minHeight(double min)
  {
    OCDBlocks blocks = new OCDBlocks();
    for (OCDTextBlock block : this)
      if (block.bounds().height >= min)
        blocks.add(block);
    return blocks;
  }

  public OCDBlocks minWidth(double min)
  {
    OCDBlocks blocks = new OCDBlocks();
    for (OCDTextBlock block : this)
      if (block.bounds().width >= min)
        blocks.add(block);
    return blocks;
  }

  public OCDBlocks minSize(double minWidth, double minHeight)
  {
    OCDBlocks blocks = new OCDBlocks();
    for (OCDTextBlock block : this)
    {
      Rectangle3 box = block.bounds();
      if (box.width >= minWidth && box.height >= minHeight)
        blocks.add(block);
    }
    return blocks;
  }

  public OCDBlocks minFontsize(double min)
  {
    OCDBlocks blocks = new OCDBlocks();
    for (OCDTextBlock block : this)
      if (block.fontsize() >= min)
        blocks.add(block);
    return blocks;
  }

  public OCDBlocks maxFontsize(double max)
  {
    OCDBlocks blocks = new OCDBlocks();
    for (OCDTextBlock block : this)
      if (block.fontsize() <= max)
        blocks.add(block);
    return blocks;
  }

  public OCDBlocks find(Pattern pattern)
  {
    OCDBlocks blocks = new OCDBlocks();
    for (OCDTextBlock block : this)
      if (pattern.matcher(block.string()).find())
        blocks.add(block);
    return blocks;
  }

  @Override
  public OCDBlocks trimSize(int size)
  {
    super.trimSize(size);
    return this;
  }

  public OCDBlocks above(OCDTextBlock block)
  {
    double minY = block.bounds().minY();
    OCDBlocks blocks = new OCDBlocks();
    for (OCDTextBlock b : this)
      if (b.bounds().maxY() < minY)
        blocks.add(b);
    blocks.ySortBack();
    return blocks;
  }

  public OCDBlocks below(OCDTextBlock block)
  {
    double maxY = block.bounds().maxY();
    OCDBlocks blocks = new OCDBlocks();
    for (OCDTextBlock b : this)
      if (b.bounds().minY() > maxY)
        blocks.add(b);
    blocks.ySort();
    return blocks;
  }


  public double textMinX(double def)
  {
    double min = Double.MAX_VALUE;
    for (OCDTextBlock block : this)
      for (OCDTextLine line : block)
        if (line.x() < min)
          min = line.x();
    return min == Double.MAX_VALUE ? def : min;
  }

  public OCDBlocks setClassname(String cls)
  {
    for (OCDTextBlock block : this)
      block.setClassname(cls);
    return this;
  }

  public OCDBlocks fontsized(double min, double max)
  {
    OCDBlocks blocks = new OCDBlocks();
    for (OCDTextBlock block : this)
    {
      float fs = block.isEmpty() ? 0 : block.first().fontSize();
      if (fs >= min && fs <= max)
        blocks.add(block);
    }
    return blocks;
  }

  public OCDBlocks(Iterable<OCDTextBlock> iterable)
  {
    for (OCDTextBlock t : iterable)
      this.add(t);
  }

  public OCDTextBlock[] array()
  {
    return this.toArray(new OCDTextBlock[0]);
  }

  public OCDTextBlock mergeInto(OCDTextBlock block)
  {
    return texts().blockize(block, false);
  }

  public OCDTexts texts()
  {
    OCDTexts texts = new OCDTexts();
    for (OCDTextBlock block : this)
      texts.addAll(block.allTexts());
    return texts;
  }

  public String string(String sep, boolean noReturn)
  {
    StringBuilder sb = new StringBuilder();
    int size = this.size();
    int index = 0;
    for (OCDTextBlock block : this)
      sb.append(block.uniString(noReturn) + (++index < size ? sep : ""));
    return sb.toString();
  }

  @Override
  public OCDBlocks addAll3(OCDTextBlock... blocks)
  {
    return (OCDBlocks) super.addAll3(blocks);
  }

  public OCDBlocks copy()
  {
    return new OCDBlocks(this);
  }

  public OCDBlocks xSort()
  {
    Collections.sort(this, (t1, t2) -> Float.compare(t1.firstX(), t2.firstX()));
    return this;
  }

  public OCDBlocks xSortBack()
  {
    Collections.sort(this, (t1, t2) -> Float.compare(t2.firstX(), t1.firstX()));
    return this;
  }

  public OCDBlocks sortX(boolean leftRight)
  {
    return leftRight ? xSort() : xSortBack();
  }

  public OCDBlocks ySort()
  {
    Collections.sort(this, (t1, t2) -> Float.compare(t1.firstY(), t2.firstY()));
    return this;
  }

  public OCDBlocks ySortBack()
  {
    Collections.sort(this, (t1, t2) -> Float.compare(t2.firstY(), t1.firstY()));
    return this;
  }

  public OCDBlocks sortY(boolean topDown)
  {
    return topDown ? ySort() : ySortBack();
  }

  public boolean mergeDown(boolean checkFonts, double maxRatioDist)
  {
    boolean hasMerged = false;
    this.sortY();
    OCDBlocks mergedBlocks = new OCDBlocks();
    while (this.isPopulated())
    {
      OCDTextBlock first = removeFirst();
      OCDTextBlock merged = null;
      do
      {
        merged = mergeDown(first, checkFonts, maxRatioDist);
        if (merged != null)
          hasMerged = true;
      } while (merged != null);

      mergedBlocks.add(first);
    }

    this.addAll(mergedBlocks);
    return hasMerged;
  }

  public OCDTextBlock mergeDown(OCDTextBlock block, boolean checkFonts, double maxRatioDist)
  {
    OCDTextBlock merged = null;
    for (OCDTextBlock merge : this)
      if (merge != block && merge.firstY() > block.firstY())
      {
        OCDTextLine blockLine = block.last();
        OCDTextLine mergeLine = merge.first();

        Rectangle3 blockBox = blockLine.bounds();
        Rectangle3 mergeBox = mergeLine.bounds();
        if (blockBox.overlapX(mergeBox) > 0.5 && blockBox.distance(mergeBox) < (blockBox.height() + mergeBox.height()) / 2.0 * maxRatioDist)
        {
          if (checkFonts)
          {
            if (blockLine.roundFontsize() != mergeLine.roundFontsize())
              continue;
            if (!blockLine.fontName().equals(mergeLine.fontName()))
              continue;
          }
          block.merge(merge).reblock();
          merged = merge;
          break;
        }

      }

    if (merged != null)
      this.remove(merged);

    return merged;
  }

  public boolean mergeOverlap(double overlap)
  {
    boolean hasMerged = false;
    this.sortBigger();
    OCDBlocks mergedBlocks = new OCDBlocks();
    while (this.isPopulated())
    {
      OCDTextBlock first = removeFirst();
      OCDTextBlock merged = null;
      do
      {
        merged = mergeOverlap(first, overlap);
        if (merged != null)
          hasMerged = true;
      } while (merged != null);

      mergedBlocks.add(first);
    }

    this.addAll(mergedBlocks);
    return hasMerged;
  }

  public OCDTextBlock mergeOverlap(OCDTextBlock block, double overlap)
  {
    OCDTextBlock merged = null;
    Rectangle3 box = block.bounds();
    for (OCDTextBlock merge : this)
      if (merge != block)
      {
        Rectangle3 miamBox = merge.bounds();
        if (box.area() > miamBox.area() && box.overlapThat(miamBox) >= overlap)
        {
          block.merge(merge).reblock();
          merged = merge;
          break;
        }
      }

    if (merged != null)
      this.remove(merged);

    return merged;
  }

}
