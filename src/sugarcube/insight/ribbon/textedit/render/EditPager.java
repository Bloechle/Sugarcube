package sugarcube.insight.ribbon.textedit.render;

import sugarcube.insight.render.FxPager;
import sugarcube.insight.ribbon.textedit.EditRibbon;
import sugarcube.formats.ocd.objects.OCDPage;

public class EditPager extends FxPager<EditRibbon>
{

  public EditPager(EditRibbon tab)
  {
    super(tab, false);
    this.boardStyles = new String[]{"cursor-text"};
  }

  @Override
  public void init()
  {
    board.metaLayer("selector", tab.selector);
    this.reset();
  }

  public void reset()
  {
    tab.selector.reset();
    super.reset();
  }

  public boolean hasSelector()
  {
    return tab.selector.hasCaret();
  }

  @Override
  public void update(OCDPage page)
  {

    super.update(page);
  }

  @Override
  public void refresh()
  {
    super.refresh();
    
    if (page == null)
      return;

  }

//  @Override
//  public TextEditTextFlow newTextFlow(OCDFlow flow)
//  {
//    return new TextEditTextFlow(this, flow);
//  }
//  



}
