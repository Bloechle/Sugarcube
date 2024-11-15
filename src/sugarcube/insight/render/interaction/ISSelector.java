package sugarcube.insight.render.interaction;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Commands;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Set3;
import sugarcube.common.data.collections.Stringer;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.base.FxClipboard;
import sugarcube.common.ui.fx.base.FxGroup;
import sugarcube.common.ui.fx.event.FxKeyboard;
import sugarcube.common.ui.fx.event.FxMouse;
import sugarcube.common.ui.fx.shapes.FxLine;
import sugarcube.common.data.xml.css.CSS;
import sugarcube.insight.core.FxRibbon;
import sugarcube.formats.ocd.objects.*;
import sugarcube.formats.ocd.objects.font.SVGFont;
import sugarcube.formats.ocd.objects.lists.OCDLines;

public class ISSelector extends FxGroup
{
  public OCDText start = null;
  public OCDText end = null;
  public int startIndex = 0; // inclusive
  public int endIndex = 0; // exclusive
  private Timeline caretTimer = new Timeline();
  private FxLine caret = new FxLine();
  private FxRibbon tab;

  public ISSelector(FxRibbon tab)
  {
    this.tab = tab;
    init();
  }

  public void init()
  {
    caret.setStroke(Color.TRANSPARENT);
    caret.setStrokeWidth(1);
    caretTimer.setCycleCount(Timeline.INDEFINITE);
    caretTimer.getKeyFrames().addAll(new KeyFrame(Duration.ZERO, e -> {
      if (isSelecting())
        caret.setStroke(Color.TRANSPARENT);
    }), new KeyFrame(Duration.seconds(.5), e -> {
      if (isSelecting())
        caret.setStroke(Color.BLACK);
    }), new KeyFrame(Duration.seconds(1)));
  }

  public void reset()
  {
    this.start = null;
    this.end = null;
    this.startIndex = 0;
    this.endIndex = 0;
    this.caretTimer.stop();
    this.clear();
  }

  public OCDPageContent content()
  {
    return this.tab.page().content();
  }

  public NodeIt<OCDText> contentTextIt()
  {
    return content().textIt();
  }

  public NodeIt<OCDText> selectedTextIt()
  {
    return content().textIt(start, end);
  }

  public List3<OCDText> selectedText()
  {
    return new List3<OCDText>((Iterable<OCDText>) selectedTextIt());
  }

  public Set3<OCDTextBlock> selectedBlocks()
  {
    Set3<OCDTextBlock> blocks = new Set3<>();
    if (isSelecting())
    {
      if (start == end)
        blocks.add(end.textBlock());
      for (OCDText text : selectedTextIt())
        blocks.add(text.textBlock());
    }
    return blocks;
  }

  public NodeIt<OCDText> textIt(boolean selected)
  {
    return selected ? selectedTextIt() : contentTextIt();
  }

  public NodeIt<OCDTextLine> lineIt()
  {
    return content().lineIt();
  }

  public boolean isPopulated()
  {
    return !isEmpty();
  }

  public boolean refreshEnd(OCDText end, int index)
  {
    if (this.end != end || this.endIndex != index)
    {
      this.end = end;
      this.endIndex = index;
      this.refresh();
      return true;
    }
    return false;
  }

  public boolean refreshStart(OCDText start, int index)
  {
    if (this.start != start || this.startIndex != index)
    {
      this.start = start;
      this.startIndex = index;
      this.refresh();
      return true;
    }
    return false;
  }

  @Override
  public boolean isEmpty()
  {
    return start == end && startIndex == endIndex;
  }

  public OCDFlow flow(boolean starting)
  {
    return (starting ? start : end) == null ? null : (OCDFlow) (starting ? start : end).parentGroup(OCDGroup.FLOW);
  }

  public OCDTextBlock block(boolean starting)
  {
    return (starting ? start : end) == null ? null : (starting ? start : end).textBlock();
  }

  public FxLine caret()
  {
    return caret;
  }

  public boolean isSelecting()
  {
    return start != null && end != null;
  }

  public boolean hasSelection()
  {
    return isSelecting() && (start != end || startIndex != endIndex);
  }

  public boolean hasCaret()
  {
    return isSelecting();
  }

  private void checkShift(boolean shift)
  {
    this.start = shift ? start : end;
    this.startIndex = shift ? startIndex : endIndex;
  }

  public void next(boolean forward, boolean shift)
  {
    if (forward)
    {
      Log.debug(this, ".next - end=" + (end == null ? "null" : end.glyphString()) + ", index=" + endIndex);
      if (end != null && endIndex < end.length())
      {
        endIndex++;
        Log.debug(this, ".next - forward: end=" + (end == null ? "null" : end.glyphString()) + ", index=" + endIndex);
      } else
      {
        OCDText oldEnd = end;
        end = contentTextIt().next(end, end);
        endIndex = end == null ? -1 : oldEnd == end ? end.length() - 1 : 0;
      }
      checkShift(shift);
    } else
    {
      if (endIndex > 0)
        endIndex--;
      else
      {
        OCDText oldEnd = end;
        end = contentTextIt().previous(end, end);
        endIndex = end == null || end == oldEnd ? 0 : end.length() - 1;
      }
      checkShift(shift);
    }
  }

  public void keyDown(FxKeyboard e)
  {
    boolean hasSelection = isPopulated();
    boolean shiftDown = e.isShiftDown();
    boolean ctrlDown = e.isControlDown();
    OCDTextLine line;
    switch (e.getCode())
    {
    case UP:
      if ((line = lineIt().previous(end == null ? null : end.textLine(), null)) != null)
        for (OCDText text : line)
          if (text.x() > end.x())
          {
            this.end = text;
            break;
          }
      checkShift(shiftDown);
      break;
    case DOWN:
      if ((line = lineIt().next(end == null ? null : end.textLine(), null)) != null)
        for (OCDText text : line)
          if (text.x() > end.x())
          {
            this.end = text;
            break;
          }
      checkShift(shiftDown);
      break;
    case END:
      this.end = end == null ? null : end.textLine().last();
      this.endIndex = end == null ? 0 : end.length();
      checkShift(shiftDown);
      break;
    case HOME:
      this.end = end == null ? null : end.textLine().first();
      this.endIndex = 0;
      checkShift(shiftDown);
      break;
    case RIGHT:
      this.next(true, shiftDown);
      break;
    case LEFT:
      this.next(false, shiftDown);
      break;
    case C:
    {
      if (ctrlDown && hasSelection)
      {
        Log.debug(this, ".keyDown - copy: " + text());
        FxClipboard.put(text());
      }
      break;
    }
    // case X:
    // if (ctrlDown && hasSelection)
    // {
    // FxClipboard.put(text());
    // delete();
    // }
    // break;
    case A:
      if (ctrlDown)
      {
        NodeIt<OCDText> it = this.contentTextIt();
        OCDText first = null;
        OCDText last = null;
        while (it.hasNext())
        {
          last = it.next();
          if (first == null)
            first = last;
        }
        this.select(first, last);
        start = first;
        end = last;
      }
      break;
    default:
      break;
    }
    this.refresh();
    this.commandBack();
  }

  public synchronized void commandBack()
  {
    if (end != null && end.fontname() != null && !end.fontname().isEmpty())
    {
      OCDTextBlock block = end.textBlock();
      if (block == null)
      {
        Log.debug(this, ".commandBack - end: block=" + end.textBlock());
        return;
      }
      String fontname = end.fontname();
      Commands commands = tab.commands;

      commands.back(CSS.FontFamily, SVGFont.FontFamily(fontname));
      commands.back(CSS.FontWeight, SVGFont.isBold(fontname) ? CSS._bold : CSS._normal);
      commands.back(CSS.FontStyle, SVGFont.isItalic(fontname) ? CSS._italic : CSS._normal);
      commands.back(CSS.FontSize, end.fontsize());
      commands.back(CSS.TextDecoration, end.decoration());
      commands.back(CSS.TextScript, end.isSuperscript() ? CSS._superscript : end.isSubscript() ? CSS._subscript : CSS._normal);
      commands.back(CSS.TextAlign, block.align());
      commands.back(CSS.LineHeight, block.interline());
      commands.back(CSS.LetterSpacing, block.charspace());
      commands.back(CSS.Color, end.fillColor());
      commands.back(CSS.BorderColor, end.strokeColor());
      commands.back(CSS.BorderWidth, end.strokeWidth());
      // Log.debug(this, ".recommand - end=" + (end == null ? "null" :
      // end.string()) + ", align=" + end.textBlock().align());
    }
  }

  public void selectTo(OCDText node, int index)
  {
    if (node != null)
    {
      if (start == null)
      {
        start = node;
        startIndex = index;
      }
      end = node;
      endIndex = index;
    }
    // if (end != null)
    // Log.debug(this, ".select - " + ((OCDText) end).string() + ", index=" +
    // index);
  }

  // public void selectNode(OCDText node, int index)
  // {
  // this.reset();
  // this.selectTo(node, index);
  // OCDText text = end != null && end instanceof OCDText ? (OCDText) end :
  // null;
  // if (text != null)
  // {
  // this.refresh();
  // }
  // }

  public void select(OCDText cursor, int index)
  {
    this.start = this.end = cursor;
    this.startIndex = this.endIndex = index;
  }

  public void select(OCDText start, OCDText end)
  {
    this.start = start;
    this.end = end;
    this.startIndex = 0;
    this.endIndex = end.length();
    // Log.trace(this, ".select - end="+(end==null? "null" :
    // end.uniString())+", index="+endIndex);
    this.refresh();
  }

  public ISSelector select(FxMouse ms)
  {
    // Log.debug(this, ".select - state=" + ms.state() + ", clicks=" +
    // ms.clicks() + "");
    if (!ms.isPrimaryBt())
      return this;

    if (ms.clicks() < 2 && ms.isDown())
    {
      // Log.debug(this, ".select - reset");
      this.reset();
    }

    Log.debug(this, ".select - end=" + end);

    OCDText text = end != null && end instanceof OCDText ? (OCDText) end : null;
    switch (ms.clicks())
    {
    case 1:// growing selection
      selectTo(ms);
      break;
    case 2:// word selection
      if (text != null)
      {
        this.start = end = text;
        this.startIndex = 0;
        this.endIndex = text.nbOfChars();
      }
      break;
    case 3:// line selection
      if (text != null)
      {
        OCDTextLine line = text.textLine();
        if (line != null)
        {
          OCDText last = line.last();
          this.start = line.first();
          this.end = last;
          this.startIndex = 0;
          this.endIndex = last.nbOfChars();
        }
      }
      break;
    case 4:// block selection
      if (text != null)
      {
        OCDTextBlock block = text.textBlock();
        if (block != null)
        {
          OCDText last = block.lastText();
          this.start = block.firstText();
          this.end = last;
          this.startIndex = 0;
          this.endIndex = last.nbOfChars();
        }
      }
      break;
    }
    this.refresh();
    return this;
  }

  private void selectTo(FxMouse ms)
  {
    Point3 p = ms.xy();
    OCDText hover = null;
    int index = 0;

    for (OCDText text : this.contentTextIt())
    {
      if (text.bounds().contains(p))
      {
        float[] pos = text.coords().array();
        float dist = Math.abs(pos[0] - p.x);
        float d;
        for (int i = 2; i < pos.length; i += 2)
        {
          if ((d = Math.abs(pos[i] - p.x)) < dist)
          {
            dist = d;
            index = i / 2;
          }
        }
        hover = text;
        break;
      }
    }
    // Log.debug(this, ".selectHover - ms=" + ms.xy() + ", ocd=" + p + ", text="
    // + (hover == null ? "null" : hover.string()));

    if (hover != null)
      selectTo(hover, index);
  }

  public ISSelector checkIndexes()
  {
    if (start == null || startIndex < 0)
      startIndex = 0;
    else if (startIndex > start.length())
      startIndex = start.length();

    if (end == null || endIndex < 0)
      endIndex = 0;
    else if (endIndex > end.length())
      endIndex = end.length();

    return this;
  }

  public OCDText previous(OCDText text)
  {
    NodeIt<OCDText> it = textIt(false);
    OCDText prev = null;
    while (it.hasNext())
    {
      OCDText curr = it.next();
      if (curr == text)
        return prev;
      prev = curr;
    }
    return null;
  }

  public OCDText next(OCDText text)
  {
    NodeIt<OCDText> it = textIt(false);
    while (it.hasNext())
      if (it.next() == text)
        return it.next();
    return null;
  }   

  public void delete(boolean forward)
  {
    // if no selected char, we select first char after caret
    if (this.isEmpty() && end != null)
    {
      // select char
      if (forward && endIndex < end.length() || !forward && endIndex > 0)
        endIndex = forward ? endIndex + 1 : endIndex - 1;
      else
      {
        // select next text char if caret is at text end
        start = end = forward ? next(end) : previous(end);
        if (start != null)
        {
          endIndex = forward ? 1 : end.length();
          startIndex = endIndex - 1;
        } else
          startIndex = endIndex = 0;
      }
    }
    this.delete();
  }

  public synchronized void delete()
  {
    checkIndexes();

    Log.debug(this, ".delete : start==end=" + (start == end) + ", startIndex=" + startIndex + ", endIndex=" + endIndex);

    // if only caret with no selection
    if (!hasSelection())
      return;

    // since we delete text, start and end will at the finally be the caret !
    this.swap();

    OCDText next = next(end);
    if (next == null)
      next = previous(start);

    if (start == end)
    {
      if (start.delete(startIndex, endIndex) == null)
      {
        // start has been completely deleted
        start = end = next;
        startIndex = endIndex = 0;
        removeEmptyLinesAndParagraphs();
      } else
        endIndex = startIndex;

    } else
    {

      NodeIt<OCDText> it = textIt(true);
      while (it.hasNext())
      {
        OCDText text = it.next();
        if (text != start && text != end || text.isEmpty())
          it.remove();
      }

      start.delete(startIndex);
      end.delete(0, endIndex);

      if (start == null)
      {
        start = next;
        startIndex = 0;
      }

      end = start;
      endIndex = startIndex;

      removeEmptyLinesAndParagraphs();
    }
  }

  private void swap()
  {
    if (start == end)
    {
      if (startIndex > endIndex)
        swapIndexes();
      return;
    }

    NodeIt<OCDText> it = textIt(true);
    while (it.hasNext())
    {
      OCDText text = it.next();
      if (text == start)
        return;
      if (text == end)
      {
        swapIndexes();
        end = start;
        start = text;
        return;
      }
    }
  }

  private void swapIndexes()
  {
    int tmp = startIndex;
    startIndex = endIndex;
    endIndex = tmp;
  }

  private void removeEmptyLinesAndParagraphs()
  {
    NodeIt<OCDTextLine> it = lineIt();
    OCDLines lines = new OCDLines();
    OCDTextLine line = null;

    while (it.hasNext())
      if ((line = it.next()).isEmpty())
        lines.add(line);

    for (OCDTextLine l : lines)
    {
      OCDTextBlock block = l.textBlock();
      l.remove();
      if (block != null && block.isEmpty())
        block.remove();
    }

  }

  public String text()
  {
    Stringer sb = new Stringer();

    boolean started = false;
    boolean reversed = false;

    if (start == end)
      return startIndex == endIndex ? "" : start.string(startIndex, endIndex);

    for (OCDText text : this.textIt(true))
    {
      if (text == start || text == end)
      {
        if (!started)
        {
          started = true;
          reversed = text == end;
          sb.append(reversed ? end.string(endIndex) : start.string(startIndex));
          if (text.isTextLineLast())
            sb.normalizeEndOfLine();
        } else
        {
          sb.append(reversed ? start.string(0, startIndex) : end.string(0, endIndex));
        }

      } else if (started)
      {
        sb.append(text.glyphString());
        if (text.isTextLineLast())
          sb.normalizeEndOfLine();
      }
    }

    return sb.toString();
  }

  public void refresh()
  {
    this.clear();

    // Log.debug(this,
    // ".refresh - isSelecting="+this.isSelecting()+", text="+this.text());
    if (!isSelecting())
      return;

    // Log.debug(this, ".select - start=" + this.start.string() + ", end=" +
    // this.end.string());

    Color highlight = Color3.SC_BLUE.alpha(0.2).fx();
    this.caretTimer.play();

    boolean reversed = false;
    boolean started = false;
    for (OCDText text : this.textIt(true))
    {
      if (text == start || text == end)
      {
        if (!started && (started = true) && text == end)
          reversed = true;
      } else
        this.add(text.bounds().fx().fill(highlight));

    }

    float x = end.coordAt(endIndex).x;
    Rectangle3 r = end.bounds();

    // handle start and end text which may be partially selected
    if (start == end)
    {
      if (startIndex != endIndex)
      {
        float x0 = start.coordAt(startIndex).x;
        x = start.coordAt(endIndex).x;
        r = end.bounds();
        r = x > x0 ? new Rectangle3(x0, r.y, x - x0, r.height) : new Rectangle3(x, r.y, x0 - x, r.height);
        this.add(r.fx().fill(highlight));
      }
    } else if (reversed)
    {
      x = start.coordAt(startIndex).x;
      r = start.bounds();
      this.add(new Rectangle3(r.x, r.y, x - r.x, r.height).fx().fill(highlight));
      x = end.coordAt(endIndex).x;
      r = end.bounds();
      this.add(new Rectangle3(x, r.y, r.maxX() - x, r.height).fx().fill(highlight));
    } else
    {
      x = start.coordAt(startIndex).x;
      r = start.bounds();
      this.add(new Rectangle3(x, r.y, r.maxX() - x, r.height).fx().fill(highlight));
      x = end.coordAt(endIndex).x;
      r = end.bounds();
      this.add(new Rectangle3(r.x, r.y, x - r.x, r.height).fx().fill(highlight));
    }

    // Log.debug(this,
    // ".refresh - start="+start.string()+", end: "+end.string());

    caret.set(x, r.minY(), x, r.maxY());
    this.add(caret);
  }

}
