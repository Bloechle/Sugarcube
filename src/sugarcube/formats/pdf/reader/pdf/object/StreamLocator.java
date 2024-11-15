package sugarcube.formats.pdf.reader.pdf.object;

public class StreamLocator
{
  public final Reference reference;
  public final int pointer;
  public int length;

  //instantiates an undefined StreamLocator
  public StreamLocator()
  {
    this(0, 0, new Reference());
  }

  public StreamLocator(long pointer, long length, Reference reference)
  {
    this.pointer = (int) pointer;
    this.length = (int) length;
    this.reference = reference;
  }

  public StreamLocator copy()
  {
    return new StreamLocator(pointer, length, reference);
  }

  @Override
  public String toString()
  {
    return "[" + (reference == null ? Reference.UNDEF : reference) + ", pos=" + pointer + ", length=" + length + "]";
  }

  public void setEndPointer(int endPosition)
  {
    this.length = endPosition - this.pointer;
  }

  public int endPointer()
  {
    return this.pointer + this.length;
  }
}
