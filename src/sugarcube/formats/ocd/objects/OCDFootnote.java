package sugarcube.formats.ocd.objects;

public class OCDFootnote extends OCDContent
{

  public OCDFootnote()
  {
    super(OCDGroup.FOOTNOTE, null);
  }

  public OCDFootnote(OCDNode parent)
  {
    super(OCDGroup.FOOTNOTE, parent);
  }

}
