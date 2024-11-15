package sugarcube.insight.ribbon.toolbox.actions;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Base;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.ui.fx.menus.FxMenu;
import sugarcube.insight.core.FxRibbon;
import sugarcube.insight.core.FxRibbonAction;
import sugarcube.insight.ribbon.toolbox.ToolboxRibbon;
import sugarcube.formats.ocd.objects.*;

public class ImageAct extends FxRibbonAction<FxRibbon>
{
  public static final int RASTER = 0;
  public static final int VIEW = 1;
  public static final int BACKGROUND = 2;
  public static final int SHADING = 3;
  public int method = 0;

  public ImageAct(FxRibbon tab, int method)
  {
    super(tab);

    

      switch (this.method = method)
      {
      case SHADING:
        this.text = "Shading";
        this.setAction(() -> shading());
        break;
      }
    
  }
  
  public void shading()
  {
    OCDPaintable node = tab.pager.interactor.node();
    OCDImage image = node.asImage();
    if(image!=null)
    {
      image.updateImage(image.image3().transparentShading());
      image.modify();
      tab.page().modify();
      Log.debug(this,  ".shading");
    }
    tab.update();
  }

  public static void ViewAct(FxRibbon tab)
  {
    Rectangle3 box = tab.selection();
    String name = Base.x32.random8();
    OCDPage page = tab.page();

    OCDGroup<OCDPaintable> g = page.content();
    for (OCDTable table : page.content().tables())
    {
      if (table.box().overlapThat(box) > 0.5)
      {

        double overlap = -1;
        for (OCDTableCell cell : table)
        {
          double o;
          if ((o = cell.box().overlapThat(box)) > overlap)
          {
            g = cell;
            overlap = o;
          }
        }
        if (overlap > 0)
          break;
      }
    }

    OCDPaintable anchor = null;
    for (OCDPaintable node : g)
    {
      if (node.bounds().minY() > box.minY())
      {
        anchor = node;
        break;
      }
    }

    OCDImage image = new OCDImage(g);
    image.setImageView(box);
    g.add(image, anchor, true);

    tab.update();
  }
  
  public static void Populate(FxMenu menu, ToolboxRibbon tab)
  {
    menu.sepItems(new ImageAct(tab, SHADING));
    menu.messageIfNoItem();
  }

}
