package sugarcube.insight.ribbon.insert.render;

import sugarcube.common.data.collections.Commands;
import sugarcube.common.graphics.geom.Line3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.data.xml.css.CSS;
import sugarcube.insight.render.interaction.FxInteractor;
import sugarcube.formats.ocd.objects.OCDPath;

public class InsertPath extends InsertNode<OCDPath>
{
  public InsertPath(InsertPager pager, final OCDPath path)
  {
    super(pager, path);    
    this.isResizable = true;
    this.isMovable = true;
    this.focusOnMouseOver();  
  }


  @Override
  public InsertPath refresh()
  {
    this.clip(node.fxClip());
    this.set(node.fx());   
    this.boxing();
    return this;
  }
  
  
  @Override
  public void interacted(FxInteractor interactor)
  {
    // if (!normalized)
    // {
    // node.normalize();
    // this.normalized = true;
    // }

    // Log.debug(this, ".interactor - " + interactor.extent());
    // Line3 extent = interactor.extent();
    // Rectangle2D box = node.path().getBounds2D();
    // double sx = extent.deltaX() / box.getWidth();
    // double sy = extent.deltaY() / box.getHeight();
    // double nx = sx * box.getX() + (sx < 0 ? -extent.width() : 0);
    // double ny = sy * box.getY() + (sy < 0 ? -extent.height() : 0);
    //
    // // Zen.LOG.debug(this,".setExtent - path.width="+box, sy)
    // Path3 p = new Transform3(sx, 0, 0, sy, extent.x() - nx, extent.y() -
    // ny).transform(node.path());
    // this.node.setPath(p);
    Line3 extent = interactor.extent();
    float dx = extent.deltaX();
    float dy = extent.deltaY();

    if (Math.abs(dx) < 0.001 || Math.abs(dy) < 0.001)
      return;

    Line3 startExtent = interactor.startExtent();

    float sx = dx / startExtent.dx();
    float sy = dy / startExtent.dy();

    Transform3 tm = Transform3.scaleInstance(sx, sy).concat(interactor.startTM());
    node.setTransform(tm);
    Rectangle3 box = node.bounds();

    node.setTransform(tm.sx(), tm.hy(), tm.hx(), tm.sy(), tm.x() + extent.x() - box.x, tm.y() + extent.y() - box.y);

    this.refresh();
  }

  @Override
  public synchronized void commandBack()
  {
    Commands commands = commands();
    commands.back(CSS.Color, node.fillColor());
    commands.back(CSS.BorderColor, node.strokeColor());
    commands.back(CSS.BorderWidth, node.strokeWidth());
  }
}