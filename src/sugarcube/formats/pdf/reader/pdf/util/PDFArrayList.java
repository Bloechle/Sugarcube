package sugarcube.formats.pdf.reader.pdf.util;

import sugarcube.common.graphics.Graphics3;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;

import java.util.ArrayList;

public class PDFArrayList<T extends PDFNode> extends ArrayList<T>
{
  private final String name;

  public PDFArrayList(String name)
  {
    this.name = name;
  }

  public PDFArrayList(String name, int capacity)
  {
    super(capacity);
    this.name = name;
  }

  public Wrapper wrap(PDFNode vo)
  {
    return new Wrapper(vo);
  }

  protected class Wrapper extends PDFNode
  {
    public Wrapper(PDFNode vo)
    {
      super(PDFArrayList.this.name, vo);
      for (PDFNode o : PDFArrayList.this)
        this.add(o);
    }

    @Override
    public String sticker()
    {
      return PDFArrayList.this.name + "[" + PDFArrayList.this.size() + "]";
    }

    @Override
    public String toString()
    {
      return PDFArrayList.this.name + "[" + PDFArrayList.this.size() + "]";
    }

    @Override
    public void paint(Graphics3 g, PDFDisplayProps props)
    {
      for (PDFNode vo : PDFArrayList.this)
        vo.paint(g, props);
    }
  }
}