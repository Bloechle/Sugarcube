
package sugarcube.formats.epub.structure.nav;

import sugarcube.formats.ocd.objects.OCDNode;


public class NavLink extends NavNode
{
  public static final String TAG = "a";  
  
  public NavLink(OCDNode parent)
  {
    super(TAG, parent);
  }
  
  public NavLink(OCDNode parent, String href, String cdata, String... props)
  {
    super(TAG, parent);
    this.addAttribute("href", href);
    this.addAttributes(props);
    this.setCData(cdata);
//    Log.debug(this, " - cdata: "+this.cdata()+", isEscaped="+this.props.isEscaped()+", isXML="+this.props.isXml());    
  }  
  
  public String href()
  {
    return this.props.get("href");
  }
  
  public void setHRef(String href)
  {
    this.addAttribute("href", href);
  } 
  
  @Override
  public String sticker()
  {
    return ">"+cdata();
  }  

  
}
