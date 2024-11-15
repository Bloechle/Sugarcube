package sugarcube.insight.ribbon.actions.search;

public class SearchScope
{
  public int pageNb = -1;
  public boolean allPages;
  public boolean prevPages;
  public boolean currPage;
  public boolean nextPages;


  public SearchScope(int pageNb, boolean all, boolean prev, boolean current, boolean next)
  {
    this.pageNb = pageNb;
    this.allPages = all;
    this.prevPages = prev;
    this.currPage = current;
    this.nextPages = next;
  }
}
