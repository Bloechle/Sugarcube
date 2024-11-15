package sugarcube.formats.ocd.objects;

import sugarcube.common.data.collections.List3;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.insight.ribbon.toolbox.render.StyleRectangle;
import sugarcube.formats.ocd.objects.lists.OCDBlocks;

import java.util.Collection;
import java.util.Iterator;

public class OCDTableCell extends OCDContent
{
  public enum CellType
  {
    FOOTER,
    HEADER,
    BODY
  }

  private int row;
  private int col;
  private int rowSpan = 1;
  private int colSpan = 1;
  private Rectangle3 box;
  private CellType cellType = CellType.BODY;

  public OCDTableCell()
  {
    super(OCDGroup.CELL, null);
  }

  public OCDTableCell(OCDNode parent)
  {
    super(OCDGroup.CELL, parent);
  }

  public OCDTextBlock firstBlock(boolean insideCell)
  {
    return blocks(insideCell).first();
  }

  public OCDBlocks blocks(boolean insideCell)
  {
    OCDBlocks blocks = new OCDBlocks();
    for (OCDPaintable node : this)
      if (node.isTextBlock() && (!insideCell || isInsideBox(node)))
        blocks.add(node.asTextBlock());
    return blocks;
  }

  public String text(boolean insideCell)
  {
    String text = "";
    for(OCDTextBlock block: blocks(insideCell).xSort())    
      text += block.sortXY().string(" ")+" ";    
    return text.trim();
  }

  public void shift(float dx, float dy)
  {
    super.shift(dx, dy);
    if (box != null)
    {
      box.x += dx;
      box.y += dy;
    }
  }

  public boolean isCellContent()
  {
    return true;
  }

  public void setRowSpan(int rowSpan)
  {
    this.rowSpan = rowSpan;
  }

  public int getRowSpan()
  {
    return rowSpan;
  }

  public int rowSpan()
  {
    return rowSpan;
  }

  public int rowEnd()
  {
    return row + rowSpan - 1;
  }

  public void setColSpan(int colSpan)
  {
    this.colSpan = colSpan;
  }

  public void setColumnSpan(int columnSpan)
  {
    this.colSpan = columnSpan;
  }

  public int getColumnSpan()
  {
    return colSpan;
  }

  public int colSpan()
  {
    return colSpan;
  }

  public int colEnd()
  {
    return col + colSpan - 1;
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    if (rowSpan != 1)
      xml.write("rowspan", rowSpan);
    if (colSpan != 1)
      xml.write("colspan", colSpan);
    xml.write("row", row);
    xml.write("col", col);
    xml.write("box", (box == null ? bounds() : box).toOCD(xml.numberFormat()));
    return super.writeAttributes(xml);
  }

  @Override
  public final void readAttributes(DomNode dom)
  {
    rowSpan = dom.integer("rowspan", rowSpan);
    colSpan = dom.integer("colspan", colSpan);
    row = dom.integer("row", row);
    col = dom.integer("col", dom.integer("column", col));
    box = Rectangle3.fromOCD(dom.value("box"), Rectangle3.fromOCD(dom.value("rbox"), null));
    super.readAttributes(dom);
  }

  @Override
  public String autoID()
  {
    return "";
  }

  public OCDTableCell col(int col)
  {
    this.col = col;
    return this;
  }

  public OCDTableCell row(int row)
  {
    this.row = row;
    return this;
  }

  public OCDTableCell colSpan(int colSpan)
  {
    this.colSpan = colSpan;
    return this;
  }

  public OCDTableCell rowSpan(int rowSpan)
  {
    this.rowSpan = rowSpan;
    return this;
  }

  public OCDTableCell box(Rectangle3 box)
  {
    this.box = box;
    return this;
  }

  public void setCol(int col)
  {
    this.col = col;
  }

  public void setCols(int col, int span)
  {
    this.col = col;
    this.colSpan = span;
  }

  public void setColumn(int column)
  {
    this.col = column;
  }

  public int getColumn()
  {
    return col;
  }

  public int col()
  {
    return col;
  }

  public boolean colIn(int start, int end)
  {
    return col >= start && col <= end;
  }

  public void setRow(int row)
  {
    this.row = row;
  }

  public void setRows(int row, int span)
  {
    this.row = row;
    this.rowSpan = span;
  }

  public int getRow()
  {
    return row;
  }

  public int row()
  {
    return row;
  }

  public boolean isFirstRow()
  {
    return row == 0;
  }

  public boolean isFirstCol()
  {
    return col == 0;
  }

  public boolean isFirstRowOrCol()
  {
    return row == 0 || col == 0;
  }

  public boolean overlapsRow(OCDTableCell cell)
  {
    return row() >= cell.row() && row() <= cell.rowEnd() || cell.row() >= row() && cell.row() <= rowEnd();
  }

  public boolean overlapsCol(OCDTableCell cell)
  {
    return col() >= cell.col() && col() <= cell.colEnd() || cell.col() >= col() && cell.col() <= colEnd();
  }

  public boolean rowIn(int start, int end)
  {
    return row >= start && row <= end;
  }

  public boolean in(int rowStart, int rowEnd, int colStart, int colEnd)
  {
    return rowIn(rowStart, rowEnd) && colIn(colStart, colEnd);
  }

  public boolean isAt(int row, int col)
  {
    return row == this.row && col == this.col;
  }

  public void setRealBounds(Rectangle3 box)
  {
    this.box = box;
  }

  public boolean isInsideBox(OCDPaintable node)
  {
    return node.bounds().overlapThis(box()) >= 0.5;
  }

  public void setBox(Rectangle3 box)
  {
    this.box = box;
  }

  public Rectangle3 getBox()
  {
    return box;
  }

  public Rectangle3 viewRealBounds()
  {
    return this.viewRealBounds(page());
  }

  public Rectangle3 realBounds()
  {
    return box;
  }

  public Rectangle3 textBox()
  {
    return this.blocks().bounds();
  }

  public Rectangle3 styleBox()
  {
    Rectangle3 r = box();
    return new StyleRectangle(classname(), r.x, r.y, r.width, r.height);
  }

  public Rectangle3 box()
  {
    return box == null ? bounds() : box;
  }

  public Rectangle3 viewRealBounds(OCDPage page)
  {
    OCDDocument doc = page == null ? null : page.document();
    Rectangle3 r = box.copy();
    if (r == null)
      return null;
    else
    {
      Point3 o = page == null ? new Point3() : page.viewBox().origin();
      float scale = doc == null ? 1 : doc.viewProps.scale;
      return new Rectangle3(scale * (r.x - o.x), scale * (r.y - o.y), scale * r.width, scale * r.height);
    }
  }

  public OCDTableCell assignContent()
  {
    for (OCDTextBlock block : page().content().blocks())
    {
      if (block.parent() != this && this.box.overlap(block.bounds()) > 0.5)
      {
        block.remove();
        this.add(block);
      }
    }
    return this;
  }

  public boolean hasTextBlockWithClassname(String... styles)
  {
    for (OCDTextBlock block : this.blocks())
      if (block.hasClassname(styles))
        return true;
    return false;
  }

  public CellType getTableCellType()
  {
    return cellType;
  }

  public void setTableCellType(CellType cellType)
  {
    this.cellType = cellType;
  }

  @Override
  public String toString()
  {
    return "Cell[" + row + "," + col + "," + rowSpan + "," + colSpan + "]";
  }

  public static List3<Rectangle3> Split(Rectangle3 box, boolean horiz, Point3... points)
  {
    List3<Point3> pts = new List3<>(points);
    pts.sort((p1, p2) -> horiz ? Double.compare(p1.y, p2.y) : Double.compare(p1.x, p2.x));
    Iterator<Point3> it = pts.iterator();
    while (it.hasNext())
    {
      Point3 p = it.next();
      if (!box.hasOverlap(p, !horiz))
        it.remove();
    }
    if (pts.isEmpty())
      return new List3<Rectangle3>(box);

    pts.add(0, new Point3(box.minX(), box.minY()));
    pts.add(new Point3(box.maxX(), box.maxY()));

    List3<Rectangle3> splits = new List3<>();

    Point3 prev = null;
    for (Point3 p : pts)
    {
      if (prev != null)
        splits.add(horiz ? new StyleRectangle(box.name(), box.x, prev.y, box.width, p.y - prev.y)
            : new StyleRectangle(box.name(), prev.x, box.y, p.x - prev.x, box.height));
      prev = p;
    }
    return splits;
  }

}
