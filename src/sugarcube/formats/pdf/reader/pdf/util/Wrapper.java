package sugarcube.formats.pdf.reader.pdf.util;

import sugarcube.common.data.collections.IntMap;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;

public class Wrapper
{
  public static PDFIntMap Wrap(PDFNode parent, IntMap map)
  {
    return new PDFIntMap(parent, map);
  }

  public static class PDFIntMap extends PDFNode
  {
    private IntMap map;

    public PDFIntMap(PDFNode parent, IntMap map)
    {
      super(map.name(), parent);
      this.map = map;
    }

    @Override
    public String sticker()
    {
      return map.name() + "[" + map.size() + "]";
    }

    @Override
    public String toString()
    {
      return map.toString();
    }
  }
}
