package sugarcube.insight.render.interaction;

import sugarcube.common.data.collections.Set3;
import sugarcube.common.ui.fx.base.FxGroup;
import sugarcube.common.ui.fx.shapes.FxRect;
import sugarcube.insight.render.FxOCDNode;
import sugarcube.insight.render.FxPager;
import sugarcube.formats.ocd.objects.OCDPaintable;

public class FxFocus extends FxGroup
{
  protected FxPager pager;
  public FxOCDNode over = null;
  public Set3<FxOCDNode> selected = new Set3<>();
  public FxGroup selectedGroup = new FxGroup();
  public FxRect overBox = new FxRect().glass();

  public FxFocus(FxPager pager)
  {
    this.pager = pager;
    this.add(selectedGroup);
    this.add(overBox);
    this.setMouseTransparent(true);
    this.overBox.setMouseTransparent(true);
    this.selectedGroup.setMouseTransparent(true);
  }

  public OCDPaintable overNode()
  {
    return over == null ? null : over.node;
  }

  public boolean updateOver(FxOCDNode over)
  {
    if (pager.preventFocusOver())
      over = null;

    if (this.over != over)
    {
//      Log.debug(this, ".updateOver - " + over);
      this.over = over;
      this.refresh();
    }
    return true;
  }

  public void updateSelection(FxOCDNode node, boolean isCtrlDown)
  {
    if (isCtrlDown)
    {
      selected.add(node);
    } else
      selected.setAll(node);
  }

  public Set3<OCDPaintable> selectedNodes()
  {
    Set3<OCDPaintable> nodes = new Set3<>();
    for (FxOCDNode node : selected)
      nodes.add(node.node);
    return nodes;
  }

  public FxFocus clear()
  {
    this.over = null;
    this.selected.clear();
    this.selectedGroup.clear();
    refresh();
    return this;
  }

  public void refresh()
  {
    if (over == null)
    {
      overBox.set(0, 0, 0, 0);
      overBox.setStrokeWidth(-1);
    } else if (over.focusColor != null)
    {
      overBox.stroke(over.focusColor, 2);
      overBox.set(over.bounds());
    }
  }

}
