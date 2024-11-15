package sugarcube.formats.epub.structure;

import sugarcube.common.data.xml.XmlNodeProps;
import sugarcube.formats.epub.EPubProps;

public class IBookDisplayOptions extends XmlNodeProps
{
  public class IBookPlatform extends XmlNodeProps
  {
    public IBookPlatform(EPubProps props)
    {
      super("platform");
      this.addAttribute("name", "*");
      this.addChild("option", "name", "specified-fonts", "true");
      if (props.isFixedLayout())
      {
        this.addChild("option", "name", "fixed-layout", "true");
        this.addChild("option", "name", "interactive", "true");
      }
    }
  }

  public IBookDisplayOptions(EPubProps props)
  {
    super("display_options");
    this.addChild(new IBookPlatform(props));
  }
}
