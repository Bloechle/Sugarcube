
package sugarcube.formats.epub.structure.nav;

import sugarcube.formats.ocd.objects.OCDNode;


public class NavSpan extends NavNode
{
  public static final String TAG = "span";
  
  public NavSpan(OCDNode parent)
  {
    super(TAG, parent);
  }
  
  public NavSpan(OCDNode parent, String cdata, String... props)
  {
    super(TAG, parent);
    this.addAttributes(props);
    this.setCData(cdata);
//    Log.debug(this, " - cdata: "+this.cdata()+", isEscaped="+this.props.isEscaped()+", isXML="+this.props.isXml());    
  } 
  
}
