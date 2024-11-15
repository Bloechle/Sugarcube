package sugarcube.formats.pdf.writer;

import sugarcube.common.data.Zen;
import sugarcube.common.system.io.File3;
import sugarcube.common.ui.gui.FileChooser3;
import sugarcube.formats.ocd.objects.OCDDocument;
import sugarcube.formats.pdf.writer.exception.PDFException;

import javax.swing.*;
import java.io.File;

public class PDFConverter
{
	private static File pdfFile;
	
  public static void main(String... args)
  {
    /*try{
     RandomAccessFile ram = new RandomAccessFile(new File("C:\\Documents and Settings\\rigamont\\Desktop\\pdf\\empty.pdf"), "r");
     int b = 0;
     int counter = -1;
     String t = "";
     boolean skip = true;
     while (true){
     char c = (char)ram.read();
     t+= c;
     b++;
     if (t.endsWith("obj") && !t.endsWith("endobj"))
     System.out.println(b - 7);
     if (c == (char)-1)
     break;
     /*if (b < 349)
     System.out.print(c + ".");
     else if (b == 349)
     System.out.println(c + "<-\r\n\r\n\r\n");
				
     else if (t.endsWith("startxref")){
     System.out.println("startxref found");
     System.out.println(">> nb of bytes " + (counter - "startxref".length()));
     //break;
     }else if (t.endsWith("xref")){
     System.out.println("xref found");
     skip = false;
     counter = -1;
     t = "";
     }else if (skip)
     continue;
     else if (c == (char)-1)
     break;
     else counter++;
     //System.out.print(c); * / 
     }
     }catch(Exception z){
			
     }
     System.out.println();
     System.exit(0);
     */
    System.out.println("pdf.PDFConverter " + args.length);
    if (args.length == 1)
    {
      File file = new File(args[0]);
      if (file.exists())
      {
        if (file.isDirectory())
        {
          System.out.println("pdf.PDFConverter >> processing directory " + file.getPath());
          File[] files = file.listFiles();
          for (int f = 0; f < files.length; f++)
            if (files[f].isFile() && files[f].getName().toLowerCase().endsWith(".ocd"))
            {
              System.out.println("pdf.PDFConverter >> processing file " + files[f].getPath());
              //load document
              OCDDocument ocd = new OCDDocument();
              ocd.load(files[f]);
              new PDFWriter(ocd).write();
            }
        }
        else
        {
          System.out.println("pdf.PDFConverter >> processing file " + file.getPath());
          convert(file, PDFWriter.PDF_VERSION_1_7);
        }
        return;
      }
    }

    Zen.LAF();
    SwingUtilities.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        FileChooser3 fileChooser = new FileChooser3(File3.USER_WORK).filter("ocd");
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
          convert(fileChooser.getSelectedFile(), PDFWriter.PDF_VERSION_1_7);
      }
    });
  }

  public static void convert(final File file, int version)
  {
    convert(file, false, true, version);
  }

  public static void convert(final File file, final boolean deleteOCD, int version) throws PDFException
  {
    convert(file, deleteOCD, true, version);
  }

  public static void convert(final File file, final boolean deleteOCD, boolean useThreads, int version)
  {
    OCDDocument ocd = new OCDDocument();
    ocd.load(file);
    convert(ocd, deleteOCD, useThreads, version);
  }

  public static void convert(final OCDDocument ocd, int version)
  {
    convert(ocd, false, true, version);
  }

  public static void convert(final OCDDocument ocd, final boolean deleteOCD, int version)
  {
	  convert(ocd, deleteOCD, version, 1, ocd.nbOfPages());
    /*PDFWriter pdfWriter = new PDFWriter(ocd, version);
    pdfWriter.write();
    pdfFile = pdfWriter.getFile();
    if (deleteOCD)
    {
      File file = new File(ocd.filePath());
      ocd.close();
      file.delete();
    }
    System.out.println("pdf.PDFConverter pdf file created: " + ocd.filePath());*/
  }


  public static void convert(final OCDDocument ocd, final boolean deleteOCD, int version, int firstPage, int lastPage)
  {
    PDFWriter pdfWriter = new PDFWriter(ocd, version);
    pdfFile = pdfWriter.write(firstPage, lastPage);
    if (deleteOCD)
    {
      File file = new File(ocd.filePath());
      ocd.close();
      file.delete();
    }
    System.out.println("pdf.PDFConverter pdf file created: " + ocd.filePath());
  }

  
  public static void convert(final OCDDocument ocd, final boolean deleteOCD, boolean useThreads, final int version)
  {
    if (!useThreads)
    {
      convert(ocd, deleteOCD, version);
      return;
    }
    new Thread(new Runnable()
    {
      public void run()
      {
        convert(ocd, deleteOCD, version);
      }
    }).start();
  }



  public static void convert(final OCDDocument ocd, final boolean deleteOCD, boolean useThreads,
		  final int version, int firstPage, int lastPage)
  {
    if (!useThreads)
    {
      convert(ocd, deleteOCD, version, firstPage, lastPage);
      return;
    }
    new Thread(new Runnable()
    {
      public void run()
      {
        convert(ocd, deleteOCD, version, firstPage, lastPage);
      }
    }).start();
  }

  
  public static File getPDFFile(){
	  return pdfFile;
  }
}
