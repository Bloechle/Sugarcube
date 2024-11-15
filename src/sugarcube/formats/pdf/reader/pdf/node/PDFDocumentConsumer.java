package sugarcube.formats.pdf.reader.pdf.node;

public interface PDFDocumentConsumer extends PDFPageConsumer
{
    
  default void consumeTrailer(PDFDocument pdf)
  {

  }
}
