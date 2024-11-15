package sugarcube.formats.ocd.writer;

import sugarcube.common.system.Prefs;
import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.Image3;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.FileOrFolder;
import sugarcube.formats.ocd.objects.OCDDocument;
import sugarcube.formats.ocd.objects.OCDImage;
import sugarcube.formats.ocd.objects.OCDPage;
import sugarcube.formats.ocd.objects.OCDPageContent;
import sugarcube.formats.ocd.objects.OCDAddon;
import sugarcube.formats.ocd.objects.font.SVGFont;

public class OCDThumbWriter
{
  public File3 zip = null;
  public OCDDocument ocd;
  public FileOrFolder folder;
  public File3 ocdFile;
  public int width;
  public int height;

  static
  {
    Prefs.Need();
  }

  public OCDThumbWriter(int width, int height)
  {
    this.width = width;
    this.height = height;
  }

  public OCDDocument convert(File3 imgFile)
  {
    this.folder = new FileOrFolder(imgFile);
    return write();
  }

  public OCDDocument write()
  {
    if (ocdFile == null)
      ocdFile = this.folder.file().extense(".ocd");
    this.ocd = new OCDDocument();
    try
    {
      final OCDWriter writer = new OCDWriter(ocd, ocdFile.needDirs(false)).create();
      writer.writeHeader();

      int nbOfImages = folder.nbOfFiles();

      int pageNb = 1;
      int imageIndex = 0;

      do
      {
        int x = 0;
        int y = 0;
        String filename = folder.name(imageIndex);
        Log.debug(this, ".write - filename: " + filename + ", path=" + folder.toString());

        OCDPage ocdPage = ocd.addPage(File3.Extense(filename, ".xml"));
        // ensures OCDPage does not try to dynamically load page from file
        ocdPage.setInMemory(true);
        ocdPage.setSize(width, height);
        ocdPage.setSizeViewBoxesAndClip();

        
        Log.info(this,  ".write - page "+pageNb+", imageIndex="+imageIndex);

        while (y+10 < height && imageIndex < nbOfImages)
        {
          byte[] data = folder.data(imageIndex);

          Image3 img = Image3.Read(data);
          int imageWidth = img.width();
          int imageHeight = img.height();

          OCDPageContent ocdContent = ocdPage.content();

          OCDImage ocdImage = new OCDImage(ocdContent);
          ocdImage.setSize(imageWidth, imageHeight);
          ocdImage.setFilename(folder.name(imageIndex));
          ocdImage.setData(data);
          ocdImage.setTransform(1, 0, 0, 1, x, y);
          ocdImage.setName(filename);
          ocdContent.add(ocdImage);

          x += imageWidth;
          if (x+imageWidth > width)
          {
            x = 0;
            y += imageHeight;
          }
          imageIndex++;
        }
        ocdPage.content().zOrderize(0);
        ocdPage.setProd(this.getClass().getSimpleName());
        writer.writeEntry(ocdPage);
        ocdPage.freeFromMemory();
        pageNb++;
      } while (imageIndex < nbOfImages);

      for (SVGFont font : ocd.fontHandler)
        writer.writeEntry(font);
      for (OCDAddon addon : ocd.addonHandler)
        writer.writeEntry(addon);

      writer.writeEntry(ocd.metadata());
      writer.writeEntry(ocd.styles());
      writer.writeEntry(ocd);
      writer.writeEntry(ocd.manifest());

      writer.close();
      Log.info(this,  ".write - ocd written at "+this.ocd.filePath());

    } catch (Exception ex)
    {
      Log.warn(Image2OCDWriter.class, ".write: " + ex);
      ex.printStackTrace();
      return null;
    } finally
    {
      if (folder != null)
        folder.close();
    }

    return ocd;
  }

  public static void main(String... args)
  {
    OCDThumbWriter writer = new OCDThumbWriter(28 * 40, 28 * 30);
    writer.convert(File3.Desk("mnist/"));
  }
}
