package sugarcube.insight.ribbon.actions.annot;

import sugarcube.insight.core.FxRibbon;
import sugarcube.insight.core.FxRibbonAction;
import sugarcube.formats.ocd.objects.OCDAnnot;

public class AnnotCopyAct extends FxRibbonAction<FxRibbon>
{  
  public OCDAnnot annot;

  public AnnotCopyAct(FxRibbon tab, OCDAnnot annot)
  {
    super(tab, "Duplicate Annotation");
    this.annot = annot;
  }

//  @Override
//  public void act()
//  {
//    PageChooser chooser = new PageChooser("Duplicate Annotation", src, this);
//  }
//
//  @Override
//  public void handlePage(PageChooser panel, OCDPage page)
//  {
//    if (env.page().number() != page.number())
//      page.annots().addAnnotation(annot.copy());
//  }
//
//  @Override
//  public void handlePageDone(PageChooser panel)
//  {
//    this.done(true);
//  }
}
