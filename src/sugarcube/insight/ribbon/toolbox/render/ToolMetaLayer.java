package sugarcube.insight.ribbon.toolbox.render;

import javafx.scene.paint.Color;
import sugarcube.common.data.collections.Set3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.base.FxBoxed;
import sugarcube.common.ui.fx.base.FxGroup;
import sugarcube.common.ui.fx.event.FxMouse;
import sugarcube.common.ui.fx.shapes.FxCircle;
import sugarcube.common.ui.fx.shapes.FxRect;
import sugarcube.common.ui.fx.shapes.FxTrack;
import sugarcube.formats.ocd.objects.OCDAnnot;
import sugarcube.formats.ocd.objects.OCDPage;
import sugarcube.formats.ocd.objects.OCDPaintable;
import sugarcube.formats.ocd.objects.lists.OCDBounds;

public class ToolMetaLayer extends FxBoxed
{
  private ToolPager pager;
  // private List3<FxTrack> tracks = new List3<>();
  private FxTrack track = new FxTrack().capJoinRound().stroke(Color3.GREEN_DARK.alpha(0.5)).pen(5);
  private FxGroup linkGroup = new FxGroup().mouseTransparent();
  private OCDPage page;
  private OCDBounds boundsMap = new OCDBounds();
  private int rOrder = 0;

  public ToolMetaLayer(ToolPager pager)
  {
    this.pager = pager;
    this.handleMouseEvents();
  }

  public ToolMetaLayer refresh()
  {
    // this.clear();
    this.page = pager.page;
    // this.boxing(page.bounds());
    boxing(page.bounds());
    boundsMap.clear();
    linkGroup.clear();
    this.add(linkGroup);

    // for (OCDAnnot annot : page.annots().type(OCDAnnot.TYPE_LAYOUT))
    // annots.add(new StyxAnnot(this, annot));
    for (OCDPaintable node : page.content())
      node.index = Integer.MAX_VALUE;

    this.rOrder = 0;
    this.refreshLinks();
    this.clearTrack();
    this.add(track);

    return this;
  }

  private void clearTrack()
  {
    this.track.getPoints().clear();
  }

  public void refreshLinks()
  {
    boundsMap.clear();
    linkGroup.clear();

    OCDPaintable prev = null;
    Rectangle3 prevBounds = null;

    float hue = 0;
    Color col = Color.hsb(hue, 0.8, 0.8, 0.8);

    Set3<OCDPaintable> standalones = new Set3<>();
    for (OCDPaintable node : page.content())
      if (node.zOrder < 0)
        standalones.add(node);
    
    for(OCDPaintable alone: standalones)
    {
      
    }

    loop: for (OCDPaintable node : page.content())
    {
      if (standalones.has(node) || node.isPath())
        continue;

      Rectangle3 bounds = node.bounds();
      for (OCDAnnot annot : page.annotations().type(OCDAnnot.TYPE_LAYOUT))
      {
        if (annot.overlap(bounds) > 0.5 && annot.hasClassname("Header", "Footer"))
        {
          continue loop;
        }
      }
      boundsMap.put(node, bounds);
      if (prev != null)
      {
        linkGroup.add(prevBounds.p3().lineTo(bounds.p0()).fx().stroke(col).pen(3).capRound());
        linkGroup.add(bounds.p0().lineTo(bounds.p3()).fx().stroke(col).pen(3).capRound());
      } else
      {
        linkGroup.add(bounds.p0().lineTo(bounds.p3()).fx().stroke(col).pen(3).capRound());
        linkGroup.add(new FxCircle(bounds.p0(), 5).fill(col));
      }
      prev = node;
      prevBounds = bounds;

      // if (pager.props.rorderLayer)
      // this.add(new ToolROrder(pager, block));
      col = Color.hsb(hue += 13, 0.8, 0.8, 0.8);
    }

    if (prevBounds != null)
      linkGroup.add(FxRect.FromCircle(prevBounds.p3(), 5).fill(col));

  }

  public void mouseEvent(FxMouse ms)
  {
    // Log.debug(this, ".mouseEvent - " + ms.state());

    if (ms.isDown())
    {
      this.track.getPoints().clear();
    }

    if (ms.isDrag() || ms.isUp())
    {
      track.add(ms.x(), ms.y());
      OCDPaintable node = boundsMap.nodeAt(ms.x(), ms.y());
      if (node != null)
        node.index = ++rOrder;

      if (ms.isUp())
      {
        this.page.modify().content().sortByIndex();
        this.clearTrack();
        this.refreshLinks();
      }
    }

    //
    // if (ms.hasCtrlOrShift() && !ms.isOut())
    // {
    // pager().splitLine(ms.hasAlt() ? pager.page().bounds() : node.bounds(),
    // ms.xy(), !ms.hasShift());
    // if (ms.isClick())
    // {
    // Log.debug(this, ".mouseEvent - split");
    // split(node, ms.xy(), !ms.hasShift(), ms.hasAlt());
    // }
    //
    // } else
    // pager().clearGlass();
  }

}
