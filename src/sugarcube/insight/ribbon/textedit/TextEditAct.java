package sugarcube.insight.ribbon.textedit;

import sugarcube.common.data.collections.Set3;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.ui.fx.menus.FxMenu;
import sugarcube.insight.core.FxRibbon;
import sugarcube.insight.core.FxRibbonAction;
import sugarcube.formats.ocd.objects.OCDText;
import sugarcube.formats.ocd.objects.OCDTextBlock;
import sugarcube.formats.ocd.objects.OCDTextLine;
import sugarcube.formats.pdf.resources.icons.Icon;

public class TextEditAct extends FxRibbonAction<FxRibbon>
{


  public TextEditAct(EditRibbon tab, int mode)
  {
    super(tab, "Group as Paragraph @Ctrl+P", Icon.PARAGRAPH.get(tab.iconSize));
  }


  @Override
  public void act()
  {

  }
  
  public static void CleanCharspaces(EditRibbon tab, float charspace)
  {
    Set3<OCDTextBlock> blocks = new Set3<>();
    for(OCDText text : tab.selector.selectedText())    
    {
      text.clearCharSpaces(); 
      blocks.add(text.textBlock());
    }
    
    Point3 p = new Point3();
    for(OCDTextBlock block: blocks)
    {
      for(OCDTextLine line: block)
      {
        p.x = line.first().x();
       
        for (OCDText text: line)
        {
          float w = text.computeWidth(charspace)*text.scaleX();
          text.setX(p.x);
          p.x+=w;
     
        }
      }
    }
    tab.update();
  }

 
  public static void Populate(FxMenu menu, EditRibbon tab)
  {
   
    if (tab.selector.hasSelection())
      menu.sep().item("Clean Charspaces").act(e -> CleanCharspaces(tab, 0));


    menu.messageIfNoItem();
  }
  

  
//  public static FxMenuItem DeleteTextItem(ToolboxRibbon tab)
//  {
//    return new FxMenuItem("Delete @Del", Icon.Awesome(Icon.REMOVE, tab.iconSize, Color3.RED)).act(e -> Delete(tab));
//  }

}