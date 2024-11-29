package sugarcube.formats.pdf.reader.pdf.util;

import sugarcube.common.graphics.Graphics3;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;

import java.util.LinkedList;

public class PDFLinkedList<T extends PDFNode> extends LinkedList<T>
{
  private final String name;

  public PDFLinkedList(String name)
  {
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
      super(PDFLinkedList.this.name, vo);
      for (PDFNode o : PDFLinkedList.this)
        this.add(o);
    }

    @Override
    public String sticker()
    {
      return PDFLinkedList.this.name + "[" + PDFLinkedList.this.size() + "]";
    }

    @Override
    public String toString()
    {
      return PDFLinkedList.this.name + "[" + PDFLinkedList.this.size() + "]";
    }

    @Override
    public void paint(Graphics3 g, PDFDisplayProps props)
    {
      for (PDFNode vo : PDFLinkedList.this)
        vo.paint(g, props);
    }
  }
}
