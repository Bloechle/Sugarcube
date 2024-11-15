package sugarcube.formats.pdf.reader;

import sugarcube.common.system.io.File3;
import sugarcube.common.system.process.Progression;
import sugarcube.common.numerics.Math3;
import sugarcube.formats.pdf.reader.pdf.node.PDFDocument;

import java.io.File;

public class PDFImager
{
  private File pdfFile;
  private File outFolder;
  private double scale = 3;
  private Progression progression = new Progression();

  public PDFImager(File pdfFile, File outFolder)
  {
    this.pdfFile = pdfFile;
    this.outFolder = outFolder;
  }

  public File3 outFile(int page, String ext)
  {
    return new File3(outFolder, name(page, ext));
  }

  public String name(int page, String ext)
  {
    String name = page + "";
    while (name.length() < 4)
      name = "0" + name;
    return "page-" + name + ext;
  }

  public int dpi()
  {
    return Math3.Round(scale * 72);
  }


  public void convertPng()
  {
    PDFDocument.ConsumePages(pdfFile, progression, page -> page.image(scale).write(outFile(page.number(), ".png"), -1));       
  }

  public void convertJpg(double quality)
  {
    PDFDocument.ConsumePages(pdfFile, progression, page -> page.image(scale).write(outFile(page.number(), ".jpg"), quality));
  }
}
