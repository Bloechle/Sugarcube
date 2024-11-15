package sugarcube.insight.ribbon.insert.render;

import sugarcube.common.data.collections.Set3;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.base.FxGroup;
import sugarcube.common.ui.fx.event.FxHandle;
import sugarcube.common.ui.fx.event.FxMouse;
import sugarcube.common.ui.fx.shapes.FxRect;
import sugarcube.insight.core.IS;
import sugarcube.formats.ocd.objects.OCDTable;
import sugarcube.formats.ocd.objects.OCDTableCell;

public class InsertTable extends InsertNode<OCDTable>
{
  public static Color3 HIGH = IS.INTERACTOR_COLOR.alpha(0.8);
  public static Color3 GLASS = Color3.GLASS;

  public Set3<InsertTableCell> cells = new Set3<>();
  public Set3<InsertTableCell> selection = new Set3<>();
  public InsertTableCell first;
  public InsertTableCell last;
  public FxRect anchor;
  public boolean selected = false;

  public InsertTable(InsertPager pager, OCDTable table)
  {
    super(pager, table);
//    table.pack(4);
    this.refresh();
  }

  public Rectangle3 box()
  {
    return node.box();
  }

  public boolean contains(Point3 p)
  {
    return anchor.contains(p) || box().contains(p);
  }

  @Override
  public InsertTable refresh()
  {
    this.clear();
    for (OCDTableCell cell : node)
      if (cell != null && cell.box() != null)
        this.add(new InsertTableCell(this, cell));

    Rectangle3 box = box();
    this.boxing(box);
    int s = 10;

    this.anchor = new Rectangle3(box.x - s, box.y - s, s, s).fx().paint(HIGH, HIGH, 1);

    FxHandle.Get(anchor).mouse(ms -> {

      if (ms.isPrimaryClick())
      {
        if (!selected)
          pager.pleaseInteract(this, ms);
        this.select(!selected);
      }

      anchor.pen(ms.isOut() ? 1 : 3);
    });

    this.add(anchor);

    return this;
  }

  public void select(boolean select)
  {
    if (select)
      this.updateSelection(null, null);
  }

  @Override
  public FxGroup clear()
  {
    super.clear();
    cells.clear();
    selection.clear();
    first = null;
    last = null;
    this.selected = false;
    return this;
  }

  public InsertTable add(InsertTableCell cell)
  {
    super.add(cell);
    cells.add(cell);
    return this;
  }

  public void start(InsertTableCell cell)
  {
    if (this.selected)
      this.select(false);

    if (cell != null && cell.selected)
      cell = null;
    this.first = cell;
    this.last = cell;
    this.updateSelection();
  }

  public void drag(InsertTableCell cell, FxMouse mouse)
  {
    if (first == null)
      first = cell;
    for (InsertTableCell c : cells)
      if (c.box.contains(mouse.xy()))
      {
        this.last = c;
        this.updateSelection();
        return;
      }
  }

  public void end()
  {
    this.updateSelection();
  }

  public void updateSelection(InsertTableCell first, InsertTableCell last)
  {
    this.first = first;
    this.last = last;
    this.updateSelection();
  }

  public void updateSelection()
  {
    selection.clear();
    if (first != null && last != null)
    {
      if (first == last)
      {
        selection.add(first);
      } else
      {
        int row0 = Math.min(first.row(), last.row());
        int row1 = Math.max(first.row(), last.row());
        int col0 = Math.min(first.col(), last.col());
        int col1 = Math.max(first.col(), last.col());

        selection.addAll(first, last);
        for (InsertTableCell fedCell : cells)
        {
          OCDTableCell cell = fedCell.node;
          if (cell == null)
            continue;
          // adapts span selection
          if (cell.rowEnd() >= row0 && cell.row() <= row1 && cell.colEnd() >= col0 && cell.col() <= col1)
          {
            int index = cell.rowEnd();
            row0 = cell.row() < row0 && index >= row0 ? cell.row() : row0;
            row1 = cell.rowSpan() > 1 && index > row1 && index > row1 ? index : row1;
            index = cell.colEnd();
            col0 = cell.col() < col0 && index >= col0 ? cell.col() : col0;
            col1 = cell.colSpan() > 1 && index > col1 && index > col1 ? index : col1;
          }
          if (cell.in(row0, row1, col0, col1))
            selection.add(fedCell);
        }
      }
    }

    // Log.debug(this,
    // ".updateSelection - first="+first+", last="+last+",
    // selection="+selection.size());

    for (InsertTableCell cell : cells)
      cell.select(selection.has(cell));
  }

  public boolean hasSelection()
  {
    return this.selection.isPopulated();
  }

  public boolean areMultipleColSelected()
  {
    if (selection.isEmpty())
      return false;
    int col = selection.first().col();
    for (InsertTableCell cell : selection)
      if (cell.col() != col)
        return true;
    return false;
  }

  public boolean isOneColSelected()
  {
    return selection.size() > 1 && !areMultipleColSelected();
  }

  public boolean areMultipleRowSelected()
  {
    if (selection.isEmpty())
      return false;
    int row = selection.first().row();
    for (InsertTableCell cell : selection)
      if (cell.row() != row)
        return true;
    return false;
  }

  public boolean isOneRowSelected()
  {
    return selection.size() > 1 && !areMultipleRowSelected();
  }

  @Override
  public void dispose()
  {
    if (this.selected)
      select(this.selected = false);
  }
}
