package sugarcube.formats.ocd.writer;

import sugarcube.common.system.Prefs;
import sugarcube.common.system.log.Log;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.FileOrFolder;
import sugarcube.common.system.process.Progression;
import sugarcube.formats.ocd.objects.OCDDocument;
import sugarcube.formats.ocd.objects.OCDPage;
import sugarcube.formats.ocd.objects.OCDAddon;
import sugarcube.formats.ocd.objects.font.SVGFont;

public class Image2OCDWriter
{
    private Progression progression;

    static
    {
        Prefs.Need();
    }

    public Image2OCDWriter()
    {
        progression = new Progression("Image2OCDWriter", "Image2OCDWriter is converting files");
    }

    public void addProgressionListener(Progression.Listener listener)
    {
        progression.addListeners(listener);
    }

    public File3 convert(File3 inFile, File3[] allFiles, File3 outFile)
    {
        FileOrFolder folder = new FileOrFolder(inFile, allFiles);

        progression.reset();

        Log.debug(this, ".convert - nb of files="+folder.nbOfFiles());

        File3 ocdFile = (outFile == null ? inFile : outFile).extense(".ocd");
        OCDDocument ocd = new OCDDocument();
        try
        {
            final OCDWriter writer = new OCDWriter(ocd, ocdFile.needDirs(false)).create();
            writer.writeHeader();

            int nbOfPages = folder.nbOfFiles();
            for (int i = 1; i <= nbOfPages; i++)
            {
                progression.update(i / (double) nbOfPages, "Generating OCD page " + i + " of " + nbOfPages);
                // addFonts(page.fonts());
                String filename = folder.name(i - 1);

                if (File3.HasExtension(filename, ".jpg", ".png", "jpeg", ".webp"))
                {
                    OCDPage ocdPage = ocd.addPage(File3.Extense(filename, ".xml"));
                    ocdPage.setBackgroundImage(filename, folder.data(i - 1));
                    ocdPage.setProd(getClass().getSimpleName());
                    writer.writeEntry(ocdPage);
                    ocdPage.freeFromMemory();
                }
            }

            for (SVGFont font : ocd.fontHandler)
                writer.writeEntry(font);
            for (OCDAddon addon : ocd.addonHandler)
                writer.writeEntry(addon);

            writer.writeEntry(ocd.metadata());
            writer.writeEntry(ocd.styles());
            writer.writeEntry(ocd);
            writer.writeEntry(ocd.manifest());

            writer.close();

        } catch (Exception ex)
        {
            Log.warn(Image2OCDWriter.class, ".write: " + ex);
            ex.printStackTrace();
            progression.complete("OCD Document generation failed");
            return null;
        } finally
        {
            if (folder != null)
                folder.close();
        }

        progression.complete(progression.canceled() ? "OCD Document generation interrupted" : "");
        Log.debug(this, ".write - image: " + inFile.path() +" to "+outFile.path());
        return ocdFile;
    }


    public static void main(String... args)
    {
        Image2OCDWriter writer = new Image2OCDWriter();
        writer.convert(File3.Desk("images/"), null, null);
    }
}
