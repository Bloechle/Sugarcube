package sugarcube.formats.ocd.objects;

import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Stringer;
import sugarcube.common.data.collections.Unicodes;
import sugarcube.common.graphics.geom.Coords;
import sugarcube.common.graphics.geom.Line3;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.interfaces.Glyph;
import sugarcube.common.data.xml.css.CSS;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.analysis.DexterProps;

import java.util.Iterator;

public class OCDFlow extends OCDGroup<OCDPaintable>
{
  public OCDFlow()
  {
    this(null);
  }

  public OCDFlow(OCDNode parent, OCDPaintable... nodes)
  {
    super(OCDGroup.FLOW, parent);
    if (nodes.length > 0)
    {
      this.addAll(nodes);
      this.extent = this.bounds().extent();
    }
  }

  @Override
  public void paint(Graphics3 g, OCD.ViewProps props)
  {
    for (OCDPaintable node : this.zOrderedLeaves())
      node.paint(g, props);
  }

  public OCDTextBlock newParagraph()
  {
    OCDTextBlock block = new OCDTextBlock(this);
    this.add(block);
    return block;
  }

  public OCDText textAt(int index)
  {
    int i = 0;
    for (OCDTextBlock block : blocks())
      for (OCDTextLine line : block)
        for (OCDText text : line)
          if (i++ == index)
            return text;
    return null;
  }

  public int textIndex(OCDText text)
  {
    int i = 0;
    for (OCDTextBlock block : blocks())
      for (OCDTextLine line : block)
        for (OCDText token : line)
          if (text == token)
            return i;
          else
            i++;
    return -1;
  }

  public OCDText addText(OCDText text, OCDText anchor)
  {
    NodeIt<OCDTextBlock> blockIt = this.blockIt();
    if (!blockIt.hasNext())
    {
      OCDTextBlock block = this.newParagraph();
      block.needFirstLine();
      blockIt = this.blockIt();
    }
    if (anchor != null && addText(anchor.textBlock(), text, anchor))
      return text;
    for (OCDTextBlock block : blockIt)
      if (addText(block, text, anchor))
        return text;

    return text;
  }
    
  private boolean addText(OCDTextBlock block, OCDText text, OCDText anchor)
  {
    if (anchor == null)
    {
      OCDTextLine line = block.needFirstLine();
      OCDText first = line.first();
      if (first == null || !first.startsWithHardReturn())
        line.add(0, text);
      else
        line.add(1, text);
    }
    if (block.contains(anchor))
      for (OCDTextLine line : block)
        if (line.contains(anchor))
        {
          line.add(text, anchor);
          return true;
        }
    return false;
  }  

  public OCDText addTextAt(OCDText text, int index)
  {
    int i = 0;
    int textIndex = 0;
    OCDTextLine textLine = null;
    stop:
    for (OCDTextBlock block : this.blocks())
      for (OCDTextLine line : block)
      {
        textIndex = 0;
        for (OCDText t : line)
        {
          if (i++ == index)
          {
            textLine = line;
            break stop;
          }
          textIndex++;
        }
      }
    if (textLine != null)
    {
      textLine.nodes.add(textIndex, text);
      return text;
    }
    return null;
  }

  public OCDText removeTextAt(int index)
  {
    int i = 0;
    for (OCDTextBlock block : this.blocks())
      for (OCDTextLine line : block)
      {
        Iterator<OCDText> textit = line.iterator();
        while (textit.hasNext())
        {
          OCDText next = textit.next();
          if (i++ == index)
          {
            textit.remove();
            return next;
          }
        }
      }
    return null;
  }

  public String stringValue(boolean removeLineReturn)
  {
    Stringer sb = new Stringer();
    for (OCDTextBlock block : this.blockIt())
      sb.append(block.stringValue(removeLineReturn)).br();
    return sb.toString();
  }

  @Override
  public OCDFlow copy()
  {
    OCDFlow flow = new OCDFlow(parent());
    super.copyTo(flow);
    return flow;
  }
  
  public synchronized void ensureLineReturns()
  {
    String fontname = "Calibri";
    float fontsize = 12;
    OCDText prevText = null;
    for (OCDTextBlock block : this.blocks())
    {
      int lineIndex = 0;
      for (OCDTextLine line : block)
      {
        // ensure hard/soft line returns
        OCDText first = line.first();
        if (first == null || !first.startsWithReturn(lineIndex == 0))
        {
          OCDText text = OCDText.lineReturn(line, first == null ? fontname : first.fontname(), first == null ? fontsize : first.fontsize(),
              lineIndex == 0);
          if (first != null)
            text.setTransform(first.transform());
          line.add(0, text);
        }
        Iterator<OCDText> textit = line.iterator();
        int textIndex = 0;
        while (textit.hasNext())
        {
          OCDText text = textit.next();
          fontname = text.fontname();
          fontsize = text.fontsize();
          // if soft return is not at line start, get rid off it
          if (textIndex > 0 && text.startsWithSoftReturn())
          {
            // if (start == text)
            // start = prevText;
            // if (end == text)
            // end = prevText;

            textit.remove();
          } else
            prevText = text;
          textIndex++;
        }
        lineIndex++;
      }
    }
  }
  
  public synchronized void inflate()
  {

    // split or merge text blocks according to hard returns
    Line3 extent = this.extent();

    // Log.debug(this, ".inflate - extent: " + extent);
    this.reparagraph();
    float maxX = extent().maxX();
    Point3 p = extent.p1();

    int i = 0;
    List3<OCDTextBlock> blocks = this.blocks();
    List3<OCDTextLine> lines = new List3<>();

    // nospace used to easily implement line wrap
    List3<OCDText> run = new List3<>();
    OCDTextLine line;

    // text blocks already exist (inferred by line returns)
    for (OCDTextBlock block : blocks)
    {
      // calculate basic standard left aligned x positions, generate text lines
      line = new OCDTextLine(block);
      run.clear();
      lines.clear();
      p.x = extent.x1;

      float charspace = block.charspace();
      Iterator<OCDText> textit = block.textIt();
      while (textit.hasNext())
      {
        OCDText text = textit.next();

        float w = text.computeWidth(charspace);
        text.setX(p.x);
        run.add(text);

        // if space add run to line and clear run
        boolean isSpace = text.startsWithSpace();
        if (isSpace)
        {
          line.addAll(run);
          run.clear();
        }

        // dynamic line splitting (soft return)
        if (p.x + w > maxX && !line.isEmpty())
        {
          p.x = extent.x1;
          lines.add(line);
          line = new OCDTextLine(block);

          if (run.isPopulated())// shifts run back to the beginning of the new
                                // line
          {
            float d = run.first().x() - p.x;
            for (OCDText c : run)
              c.setX(c.x() - d);
            p.x = run.last().x() + w + block.charspace();
          }
        } else
          p.x += w + block.charspace();
      }
      line.addAll(run);
      lines.add(line);
      block.setAll(lines);

      // update y and heights and adapts x to justification
      String align = block.align();
      boolean right = align.equals(CSS._right);
      boolean center = !right && align.equals(CSS._center);
      boolean justify = !right && !center && align.equals(CSS._justify);
      float lineHeight = block.interline();

      OCDTextLine lastLine = block.isEmpty() ? null : block.last();
      Iterator<OCDTextLine> it = block.iterator();
      line = it.hasNext() ? it.next() : null;
      while (line != null)
      {
        OCDTextLine next = it.hasNext() ? it.next() : null;
        if (!line.isEmpty())
        {
          OCDText firstText = line.first();
          OCDText lastText = line.last();
          boolean startsWS = firstText.startsWithSpace();
          boolean endsWS = lastText.endsWithSpace();
          if (endsWS)
            lastText = line.size() > 1 ? line.penultimate() : line.last();
          int spaces = 0;// nb of inner spaces
          if (justify)
          {
            for (OCDText text : line)
              if (text.startsWithSpace())
                spaces++;
            spaces = spaces + (startsWS ? -1 : 0) + (endsWS ? -1 : 0);
          }
          // be careful, OCDText.cs not yet updated
          float dx = maxX - (lastText == null ? 0 : lastText.x() + lastText.computeWidth());
          if (justify && spaces > 0 && line != lastLine && (next == null || !(next.first() != null && next.first().startsWithHardReturn())))
            dx = dx / spaces;
          else
            dx = right ? dx : center ? dx / 2 : 0;

          // Zen.LOG.debug(this, ".updateCoordsY - line=" + line + ", spaces=" +
          // spaces + ", maxX=" + maxX + ", lastX=" + lastText.lastX() + ", dx="
          // + dx);
          float maxFS = line.maxFontsize();
          p.y += (maxFS > 0 ? maxFS : 12) * (i == 0 ? 1 : lineHeight);

          OCDText prev = null;
          int counter = 0;
          for (OCDText text : line)
          {
            boolean isSpace = text.isSpace();
            if (justify && text != firstText && isSpace)
              counter++;

            text.setX(text.x() + (justify ? dx * (isSpace && counter > 0 ? counter - 1 : counter) : dx));
            text.setY(p.y);

            if (prev != null)
              updateCS(prev, charspace, text.firstPoint());
            prev = text;
          }
          if (prev != null)
            updateCS(prev, charspace, null);
        }
        i++;
        line = next;
      }
    }

    this.ensureLineReturns();
    this.refresh();
  }

  public synchronized void reparagraph()
  {
    OCDText merge;
    OCDText split;
    // Log.debug(this, ".reparagraph - blocks="+flow.nbOfChildren());
    do
    {
      merge = null;
      split = null;
      int blockIndex = 0;
      OCDTextBlock block = null;
      OCDTextBlock prevBlock = null;
      NodeIt<OCDTextBlock> blockIt = this.blockIt();
      root: while (blockIt.hasNext())
      {
        block = blockIt.next();
        // Log.debug(this,
        // ".reparagraph - block["+block.string()+"]"+block.string().trim().isEmpty()+", "+block.graphics().size());
        if (blockIndex > 0 && block.isTextEmpty())// removes empty blocks
                                                  // (except first one)
          blockIt.remove();
        else
        {
          int lineIndex = 0;
          for (OCDTextLine line : block)
          {
            int textIndex = 0;
            for (OCDText text : line)
            {
              if (lineIndex == 0 && textIndex == 0)// this is a block beginning
              {
                // hard return has been removed... merge previous and current
                // blocks
                if (blockIndex > 0 && !text.startsWithHardReturn())
                {
                  merge = text;
                  break root;
                }
              } else if (text.startsWithHardReturn())
              {
                split = text;
                break root;
              }
              textIndex++;
            }
            lineIndex++;
          }
          blockIndex++;
          prevBlock = block;
        }
      }

      if (split != null)
      {
        // Log.debug(this, ".reparagraph - split");
        splitParagraph(split);
      } else if (merge != null)
      {
        // Log.debug(this, ".reparagraph - merge: " + prevBlock.string() + "/" +
        // block.string());
        prevBlock.addAll(block.textlines());
        blockIt.remove();
      }
    } while (merge != null || split != null);
  }
  


  private synchronized void splitParagraph(OCDText hardReturn)
  {
    OCDTextBlock block = hardReturn.textBlock();
    OCDTextBlock newBlock = new OCDTextBlock(this);
    newBlock.set(CSS.TextAlign, block.get(CSS.TextAlign, CSS._left));
    newBlock.set(CSS.LineHeight, block.get(CSS.LineHeight, "1"));
    newBlock.set(CSS.LetterSpacing, block.get(CSS.LetterSpacing, "0"));
    OCDTextLine newLine = newBlock.needFirstLine();

    Iterator<OCDText> textIt = block.textIt();
    boolean doSplit = false;
    while (textIt.hasNext())
    {
      OCDText text = textIt.next();
      if (text == hardReturn)
        doSplit = true;
      else if (text.startsWithSoftReturn())
        newLine = newBlock.newLine();
      if (doSplit)
      {
        textIt.remove();
        newLine.add(text);
      }
    }
    int index = this.childIndex(block);
    this.graphics().add(index > -1 ? index + 1 : this.graphics().size(), newBlock);
  }  
  
  
  public static void updateCS(OCDText text, float charspace, Point3 end)
  {
    float x = text.x();
    float y = text.y();
    Coords coords = new Coords(x, y);
    for (Glyph glyph : text.glyphs())
      coords.add(x += (glyph.width() + charspace) * text.scriptSize(), y);

    if (end != null)
    {
      coords.removeLast();
      coords.add(end);
    }
    text.updateCS(coords);
  }

  public void prepareToEdition()
  {    
    // Log.debug(this, ".prepare - extent=" + this.extent());
    for (OCDTextBlock block : this.blocks())
      for (OCDTextLine line : block)
      {
        // fragments text runs to chars (allowing the selection of individual
        // characters)
        List3<OCDText> chars = new List3<>();
        for (OCDText text : line)
          if (text.groupID<0)
            chars.addAll(text.normalize().characterize());
          else
            chars.add(text.normalize());// fixed text is not fragmented (such as
                                        // database field wrapper)

        // adds white space at line ends
        if (!chars.isEmpty() && !(chars.size() == 1 && chars.first().startsWithHardReturn()) && chars.last().unicodes().last() != Unicodes.ASCII_SP)
        {
          OCDTextLine next = line.next();
          if (next != null)
          {
            OCDText space = chars.last().copy();
            space.setAsWhiteSpace();
            Coords coords = space.coords();
            if (coords.size() > 1)
              space.updateCS(space.coords().pointsAt(1, 1));
            else if (coords.size() > 0)
              space.updateCS(space.coords().pointsAt(0, 0));
            chars.add(space);
          }
        }
        line.setAll(chars);
      }
    this.ensureLineReturns();
  } 

  public void backToNormal(DexterProps props)
  {
    
  if (props == null)
    props = new DexterProps();
  for (OCDTextBlock block : this.blocks())
    for (OCDTextLine line : block)
    {
      // merges back chars to text runs
      line.canonize();
      Iterator<OCDText> iterator = line.iterator();
      OCDText t1 = null;
      // Log.debug(this,
      // ".mergeTokens - "+line.first().first()+": "+line.stringValue());
      while (iterator.hasNext())
      {
        OCDText t2 = iterator.next();
        if (t2 != null && t2.nbOfChars() == 1 && t2.first() == OCDText.HARD_RETURN)
          iterator.remove();
        else if (t1 != null && t1.groupID<0 && t2.groupID<0 && props.baselineOK(t1, t2) && t1.fontname().equals(t2.fontname())
            && props.basicsOK(t1, t2) && props.scriptOK(t1, t2) && props.colorsOK(t1, t2))
        {
          // Log.debug(this, ".spacify - " + pre.string() + "-" + cur.string()
          // + " " + pre.zOrder() + "-" + cur.zOrder());
          t1.unicodes().append(t2.unicodes());
          t1.coords().removeLast();
          t1.coords().add(t2.coords());
          if (t2.zOrder > t1.zOrder)
            t1.zOrder = t2.zOrder;
          iterator.remove();
          t1.canon.refresh();
        } else
          t1 = t2;
      }
      line.canon.refresh();
      line.uncanonize();
      // if (cluster.canon == null)
      // Log.debug(this, ".spacify - cluster.canon is null: " +
      // cluster.stringValue(false));
      // Log.debug(this, ".mergeTokens - out:" + line.stringRemapped());

    }
  }
  
}
