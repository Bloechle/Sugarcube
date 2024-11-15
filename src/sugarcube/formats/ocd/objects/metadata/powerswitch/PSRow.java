package sugarcube.formats.ocd.objects.metadata.powerswitch;

import sugarcube.common.system.reflection.Annot._Xml;
import sugarcube.common.data.xml.XmlNodeReflect;

@_Xml(tag=PSRow.TAG)
public class PSRow extends XmlNodeReflect
{
public static final String TAG = "MF_";//spMF
@_Xml
public String Format = "";
@_Xml
public String LocalizedTagName = "";
@_Xml
public String Subtype = "inline";
@_Xml
public String Default = "";
@_Xml
public String Type = "string";
@_Xml
public String Tooltip = "";
@_Xml
public String ReadOnly = "Yes";
@_Xml
public String Editor = "inline";
@_Xml
public String ValueIsRequired = "Yes";
@_Xml
public String RememberLastValue = "No";
@_Xml
public String cdata = "";

}
