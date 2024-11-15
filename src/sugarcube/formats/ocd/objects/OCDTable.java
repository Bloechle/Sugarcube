package sugarcube.formats.ocd.objects;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.IntSet;
import sugarcube.common.data.collections.Ints;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.StringList;
import sugarcube.common.data.Base;
import sugarcube.common.graphics.geom.*;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.objects.lists.OCDList;

import java.util.Collection;
import java.util.Iterator;

public class OCDTable extends OCDGroup<OCDTableCell>
{
  private int cols = 0;
  private int rows = 0;

  public OCDTable()
  {
    this(null);
  }

  public OCDTable(OCDNode parent)
  {
    super(OCDGroup.TABLE, parent);
  }

  public String textAt(int row, int col)
  {
    OCDTableCell cell = cellAt(row, col);
    if (cell == null)
      return "";
    return cell.string(false);
  }

  public String[] texts(int row, boolean withSpan)
  {
    int cols = this.cols();
    StringList texts = new StringList();
    for (int col = 0; col < cols; col++)
    {
      OCDTableCell cell = cellAt(row, col);
      if (cell != null)
      {
        texts.add(cell.string(false));
        if (withSpan)
          col += cell.colSpan() - 1;
      } else if (withSpan)
        Log.debug(this, ".texts - null cell: row=" + row + ", col=" + col);
    }
    return texts.array();
  }

  public OCDTableCell cellAt(int row, int col)
  {
    for (OCDTableCell cell : this)
      if (cell.isAt(row, col))
        return cell;
    return null;
  }

  public OCDTableCell[] rowAt(int row)
  {
    return rowAt(row, this.cols);
  }

  public OCDTableCell[] rowAt(int row, int size)
  {
    OCDTableCell[] cells = new OCDTableCell[size];
    int i;
    for (OCDTableCell cell : this)
      if (cell.row() == row && (i=cell.col()) < size)
        cells[i] = cell;
    return cells;
  }

  public boolean hasCellAt(int row, int col)
  {
    return cellAt(row, col) != null;
  }

  public boolean hasOneCell()
  {
    return this.nbOfChildren() == 1;
  }

  public void setRows(int rows)
  {
    this.rows = rows;
  }

  public void setCols(int cols)
  {
    this.cols = cols;
  }

  public void setNumberOfColumns(int cols)
  {
    if (this.cols < cols)
      this.cols = cols;
  }

  public void increaseNumberOfRows(int rows)
  {
    this.rows += rows;
  }

  public OCDTableCell addCell(int row, int col)
  {
    OCDTableCell cell = new OCDTableCell(this).row(row).col(col);
    this.add(cell);
    return cell;
  }

  public OCDTableCell addCell(int row, int col, int colSpan, Rectangle3 box)
  {
    OCDTableCell cell = addCell(row, col);
    cell.setColSpan(colSpan);
    cell.setBox(box);
    return cell;
  }

  public void addRow()
  {
    int row = this.rows() - 1;
    int cols = this.cols();

    for (int col = 0; col < cols; col++)
    {
      OCDTableCell cell = cellAt(row, col);
      if (cell != null)
      {
        OCDTableCell add = new OCDTableCell(this).row(row + 1).col(col).box(cell.box().shift(0, cell.box().height()));
        this.add(add);
        add.assignContent();
      }
    }
    this.rows++;
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    xml.write("id", id);
    xml.write("cols", cols);
    xml.write("rows", rows);
    return super.writeAttributes(xml);
  }

  @Override
  public final void readAttributes(DomNode dom)
  {
    cols = dom.integer("cols", dom.integer("columns", cols));
    rows = dom.integer("rows", rows);
    id = dom.value("id", dom.value("table-id", id));
    super.readAttributes(dom);
  }

  @Override
  public String autoID()
  {
    int index = 0;
    OCDTableCell cell = get(index);
    while (cell.first() == null && index < nodes.size())
      cell = get(index++);
    String firstID = cell == null ? Base.x32.random8() : cell.first().autoID();
    int i = firstID.indexOf("-");
    return firstID.substring(0, i < 0 ? 0 : i) + "-s" + type + firstID.substring(i < 0 ? 0 : i);
  }

  public String getTableID()
  {
    return id;
  }

  public void setTableID(String tableID)
  {
    this.id = tableID;
  }

  public int getNumberOfRows()
  {
    return rows;
  }

  public int getNumberOfColumns()
  {
    return cols;
  }
  
  public int nbOfCells()
  {
    return this.nodes.size();
  }

  public int rows()
  {
    int max = -1;
    for (OCDTableCell cell : this)
      if (cell.row() > max)
        max = cell.row();
    return max + 1;
  }

  public int cols()
  {
    int max = -1;
    for (OCDTableCell cell : this)
      if (cell.col() > max)
        max = cell.col();
    return max + 1;
  }

  public Rects boxes()
  {
    Rects boxes = new Rects();
    for (OCDTableCell cell : this)
      boxes.add(cell.box());
    return boxes;
  }

  public float[] colWidths(double scale)
  {
    float[] cols = new float[cols()];
    float max = Integer.MIN_VALUE;
    for (OCDTableCell cell : this)
    {
      Rectangle3 box = cell.box();
      cols[cell.col()] = box.minX();
      if (box.maxX() > max)
        max = box.maxX();
    }
    for (int i = 0; i < cols.length - 1; i++)
      cols[i] = cols[i + 1] - cols[i];
    cols[cols.length - 1] = max - cols[cols.length - 1];

    for (int i = 0; i < cols.length; i++)
      cols[i] = cols[i] * (float) scale;

    return cols;
  }

  @Override
  public Rectangle3 bounds()
  {
    Rectangle3 box = null;
    for (OCDTableCell node : children())
    {
      Rectangle3 bounds = node.bounds();
      if (bounds == null)
        bounds = node.box();
      // skip empty cells
      if (bounds == null || bounds.height == 0 && bounds.width == 0)
        continue;
      box = box == null ? bounds.copy() : box.include(bounds);
    }
    return box == null ? new Rectangle3() : box;
  }

  public Rectangle3 box()
  {
    Rectangle3 box = null;
    for (OCDTableCell node : children())
      box = box == null ? node.box().copy() : box.include(node.box());
    return box;
  }

  public OCDTable blockize()
  {
    for (OCDTableCell cell : this)
      cell.blockize();
    return this;
  }

  public OCDTable clearCellClassnames()
  {
    for (OCDTableCell cell : this)
      cell.removeClassname();
    return this;
  }

  public OCDTable setCellClassnames(Points namedPoints)
  {
    for (OCDTableCell cell : this)
      for (Point3 p : namedPoints)
        if (cell.box().contains(p))
          cell.setClassname(p.name());
    return this;
  }

  public OCDTable pack(int precision)
  {
    float s = precision;
    IntSet colSet = new IntSet();
    IntSet rowSet = new IntSet();

    for (OCDTableCell cell : this)
    {
      Rectangle3 box = cell.box();
      colSet.addRound(box.minX() / s);
      colSet.addRound(box.maxX() / s);
      rowSet.addRound(box.minY() / s);
      rowSet.addRound(box.maxY() / s);
    }

    Ints cols = colSet.ints().sort();
    Ints rows = rowSet.ints().sort();

    // Log.debug(this, ".pack - "+cols);
    // Log.debug(this, ".pack - "+rows);

    List3<OCDTableCell> dummies = new List3<>();
    for (OCDTableCell cell : this)
    {
      Rectangle3 box = cell.box();
      int col = cols.nearestIndex(box.minX() / s);
      int row = rows.nearestIndex(box.minY() / s);
      int colSpan = cols.nearestIndex(box.maxX() / s) - col;
      int rowSpan = rows.nearestIndex(box.maxY() / s) - row;

      if (colSpan == 0 || rowSpan == 0)
        dummies.add(cell);

      cell.col(col);
      cell.row(row);
      cell.colSpan(colSpan);
      cell.rowSpan(rowSpan);

      // Log.debug(this, ".pack - cell: col="+col+", row="+row+",
      // colSpan="+colSpan+", rowSpan="+rowSpan+", box="+box);
    }

    for (OCDTableCell dummy : dummies)
    {
      // Log.debug(this, ".pack - deleting " + dummy + " at page " +
      // this.pageNb());
      this.delete(dummy);
    }

    return this;
  }

  public OCDTable mergeWithRight(OCDTableCell cell)
  {
    int row = cell.row();
    int col = cell.col();
    OCDTableCell right = this.cellAt(row, col + 1);
    if (right != null && right != cell)
    {

      cell.setColSpan(cell.colSpan() + right.colSpan());
      if (cell.getBox() != null && right.getBox() != null)
        cell.setBox(cell.getBox().include(right.getBox()));
      cell.assignContent();
      right.remove();
    }

    return this;
  }

  public OCDTable splitNewTableAfterRow(int splitRowIndex)
  {
    splitRowIndex++;
    OCDTable newTable = new OCDTable(parent());
    newTable.setCols(cols);
    newTable.setRows(rows - splitRowIndex);
    Iterator<OCDTableCell> cellIt = iterator();
    while (cellIt.hasNext())
    {
      OCDTableCell cell = cellIt.next();
      if (cell.row() >= splitRowIndex)
      {
        cellIt.remove();
        cell.setRow(cell.row() - splitRowIndex);
        newTable.add(cell);
      }
    }
    this.setRows(splitRowIndex);
    return newTable;
  }

  public OCDTable split(boolean horiz, List3<Double> positions)
  {

    // reverse table
    Rects boxes = new Rects();
    OCDList contents = new OCDList();
    for (OCDTableCell cell : this)
    {
      for (OCDPaintable node : cell.children())
        contents.add(node);

      Rectangle3 box = cell.box();
      List3<Point3> splits = new List3<>();
      for (double pos : positions)
      {
        if (horiz)
        {
          if (pos > box.minY() && pos < box.maxY())
            splits.add(new Point3(box.cx(), pos));
        } else
        {
          if (pos > box.minX() && pos < box.maxX())
            splits.add(new Point3(pos, box.cy()));
        }
      }

      if (splits.size() > 0)
        boxes.addAll(OCDTableCell.Split(cell.styleBox(), horiz, splits.toArray(new Point3[0])));
      else
        boxes.add(cell.styleBox());
    }
    return this;
  }

  public int maxRow(double maxY)
  {
    int rowIndex = -1;
    for (OCDTableCell cell : this)
      if (cell.box().maxY() < maxY && rowIndex < cell.row())
        rowIndex = cell.row();
    return rowIndex;
  }

  public int maxColIndex()
  {
    int max = -1;
    for (OCDTableCell cell : this)
      if (cell.col() > max)
        max = cell.col();
    return max;
  }

  @Override
  public Line3 extent()
  {
    return this.box().extent();
  }
}
