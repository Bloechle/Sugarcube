package sugarcube.insight.ribbon.insert.render;

import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.interfaces.Boxable;
import sugarcube.common.ui.fx.event.FxMouse;
import sugarcube.formats.ocd.objects.OCDPaintable;
import sugarcube.formats.ocd.objects.OCDTableCell;

public class InsertTableCell extends InsertNode<OCDTableCell> implements Boxable
{
  public InsertTable table;
  public boolean selected = false;

  public InsertTableCell(InsertTable table, OCDTableCell cell)
  {
    super(table.pager, cell);
    this.table = table;
    for (OCDPaintable node : cell.zOrderedGraphics())
      this.add(pager.fxNode(node, this));  
    if (cell != null && cell.box() != null)
      this.boxing(cell.box()).glass().stroke(HIGH, 1);
    handle().mouse(ms -> mouseEvent(ms));
  }

  public int row()
  {
    return node.row();
  }

  public int col()
  {
    return node.col();
  }

  public int rowEnd()
  {
    return node.rowEnd();
  }

  public int colEnd()
  {
    return node.colEnd();
  }

  @Override
  public Rectangle3 box()
  {
    return node.box();
  }

  public InsertPager pager()
  {
    return (InsertPager) pager;
  }

  public void select(boolean select)
  {
    box.fill((selected = select) ? HIGH : GLASS).pen(1);
  }

  public void mouseEvent(FxMouse ms)
  {
    
  }

  @Override
  public String toString()
  {
    return node.toString();
  }

}
