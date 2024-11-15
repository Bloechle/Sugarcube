package sugarcube.insight.ribbon.actions;

import sugarcube.insight.core.FxRibbon;
import sugarcube.insight.core.FxRibbonAction;
import sugarcube.formats.ocd.objects.OCDAnnot;
import sugarcube.formats.ocd.resources.icons.shared.IconsDolores;

public class AnnotCopyAct extends FxRibbonAction<FxRibbon>
{  
  public OCDAnnot annot;

  public AnnotCopyAct(FxRibbon tab, OCDAnnot annot)
  {
    super(tab, "Duplicate Annotation");
    this.icon(IconsDolores.class, "process-24.png");
    this.annot = annot;    
  }

  @Override
  public void act()
  {
//    LiveProcess chooser = new LiveProcess("Duplicate Annotation", src, this);
  }

//  @Override
//  public void handlePage(LiveProcess panel, OCDPage page)
//  {
//    if (env.page().number() != page.number())
//      page.annots().addAnnotation(annot.copy());
//  }
//
//  @Override
//  public void handlePageDone(LiveProcess panel)
//  {
//    this.done(true);
//  }
}
