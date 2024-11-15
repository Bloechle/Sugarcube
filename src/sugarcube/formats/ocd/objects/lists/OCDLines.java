package sugarcube.formats.ocd.objects.lists;

import sugarcube.formats.ocd.objects.OCDTextLine;

import java.util.Collections;

public class OCDLines extends OCDPaintables<OCDTextLine>
{
  public OCDLines()
  {

  }

  public OCDLines(Iterable<OCDTextLine> iterable)
  {
    for (OCDTextLine t : iterable)
      this.add(t);
  }

  public OCDLines sort(boolean ascendant)
  {
    Collections.sort(this, ascendant ? OCDLines.yComparator() : OCDLines.yComparator());
    return this;
  }

  public OCDLines cxBetween(double minX, double maxX)
  {
    OCDLines lines = new OCDLines();
    for (OCDTextLine line : this)
    {
      double x = line.bounds().cx();
      if (x > minX && x < maxX)
        lines.add(line);
    }
    return lines;
  }
  
  public OCDLines cyBetween(double minY, double maxY)
  {
    OCDLines lines = new OCDLines();
    for (OCDTextLine line : this)
    {
      double y = line.bounds().cy();
      if (y > minY && y < maxY)
        lines.add(line);
    }
    return lines;
  }

  public OCDTextLine[] array()
  {
    return this.toArray(new OCDTextLine[0]);
  }

  public String text(boolean addLF)
  {
    String text = "";
    for (OCDTextLine line : this)
    {
      text += line.uniString();
      text += addLF ? "\n" : " ";
    }
    return text.trim();
  }
}
