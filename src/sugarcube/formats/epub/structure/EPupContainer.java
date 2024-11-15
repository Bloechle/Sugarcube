package sugarcube.formats.epub.structure;

import sugarcube.common.data.collections.List3;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.common.data.xml.XmlNode;
import sugarcube.common.data.xml.XmlNodeProps;

import java.util.Collection;

public class EPupContainer extends XmlNodeProps
{
  public static class RootFile extends XmlNodeProps
  {
    public RootFile(RootFiles rootFiles)
    {
      super("rootfile", rootFiles);
    }

    @Override
    public Collection<? extends XmlINode> writeAttributes(Xml xml)
    {
      xml.write("full-path", EPub.OPF_PATH);
      xml.write("media-type", "application/oebps-package+xml");
      return this.children();
    }
  }

  public static class RootFiles extends XmlNodeProps
  {
    public RootFiles(EPupContainer container)
    {
      super("rootfiles", container);
    }

    @Override
    public Collection<? extends XmlINode> writeAttributes(Xml xml)
    {
      return this.children();
    }

    @Override
    public Collection<? extends XmlNode> children()
    {
      return new List3<XmlNode>(new RootFile(this));
    }
  }
  private static final String TAG = "container";

  public EPupContainer()
  {
    super(TAG);
  }

  @Override
  public Collection<? extends XmlINode> writeAttributes(Xml xml)
  {
    xml.write("version", "1.0");
    xml.write("xmlns", "urn:oasis:names:tc:opendocument:xmlns:container");
    return this.children();
  }

  @Override
  public Collection<? extends XmlNode> children()
  {
    return new List3<XmlNode>(new RootFiles(this));
  }
}
