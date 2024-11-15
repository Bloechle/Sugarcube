package sugarcube.formats.ocd.writer;

import sugarcube.common.system.log.Log;
import sugarcube.common.system.util.Sys;
import sugarcube.common.data.collections.Files3;
import sugarcube.common.data.collections.StringSet;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.Zipper;
import sugarcube.common.system.process.Progression;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDPageProcessor;
import sugarcube.formats.ocd.objects.*;
import sugarcube.formats.ocd.objects.OCDAddon;
import sugarcube.formats.ocd.objects.font.SVGFont;

import java.util.zip.ZipEntry;

public class OCDMergeWriter implements OCDPageProcessor
{
  public static final int BUFFER_SIZE = 8192;
  public StringSet set = new StringSet();// already written entries, to avoid duplicate
  public Zipper zip;
  public File3 tmpFile;
  public File3 file;
  public File3[] ocdFiles;
  public boolean keepImages = true;
  public OCDDocument mergedOCD;
  public Progression progression;
  public boolean manifestWritten = false;

  public OCDMergeWriter()
  {
    // first step metadata, last step writing...
    // this.progression.setNbOfSteps(ocd.pageHandler.nbOfPages() + 2);
  }

  public OCDMergeWriter images(boolean keep)
  {
    this.keepImages = keep;
    return this;
  }

  public OCDMergeWriter files(File3... ocdFiles)
  {
    this.ocdFiles = ocdFiles;
    return this;
  }

  public OCDMergeWriter progressor(Progression.Listener progressor)
  {
    this.progression.addListeners(progressor);
    return this;
  }

  public Progression progression()
  {
    return progression;
  }

  public void setProgression(Progression progression)
  {
    this.progression = progression;
  }

  public void cancel()
  {
    this.progression.cancel();
  }

  public OCDMergeWriter write(File3 file)
  {
    this.progression = new Progression("Writing OCD File - " + file.getName());
    this.mergedOCD = new OCDDocument();
    this.file = file;
    this.file = this.file.extense(OCD.FILE_EXTENSION);
    this.file.ensureRW();
    this.mergedOCD.setFilePath(this.file.getAbsolutePath());
    this.tmpFile = this.file.extense(".ocd_w");

    this.manifestWritten = false;
    mergedOCD.isSaving = true;
    boolean successful = false;
    progression.start("Writing OCD file - " + file.getAbsolutePath());
    mergedOCD.manifest().clear();
    this.writeHeader();
    try
    {
      for (File3 ocdFile : ocdFiles)
      {
        OCDDocument ocd = OCD.Load(ocdFile);

        for (OCDPage page : ocd)
        {

          // mergedOCD.pageHandler.add(page);

          page.ensureInMemory();
          this.writeEntry(page, ocd);
          this.pageWritten(page);
          mergedOCD.pageHandler.add(page);
          progression.stepAchieved("Writing OCD Page " + page.number() + "/" + page.nbOfPages());

          page.forceFreeFromMemory();
        }

        if (keepImages)
          for (OCDImageEntry image : ocd.imageHandler)
            this.writeEntry(image, ocd);
        for (OCDAudioEntry audio : ocd.audioHandler)
          this.writeEntry(audio, ocd);
        for (OCDVideoEntry video : ocd.videoHandler)
          this.writeEntry(video, ocd);
        for (OCDAddon addon : ocd.addonHandler)
          this.writeEntry(addon, ocd);

        for (SVGFont font : ocd.fontHandler)
          mergedOCD.fontHandler.add(font);
      }

      for (SVGFont font : mergedOCD.fontHandler)
        this.writeEntry(font);

      this.writeEntry(mergedOCD.metadata());
      this.writeEntry(mergedOCD.nav().populatePagesTOC());
      this.writeEntry(mergedOCD.styles());
      this.writeEntry(mergedOCD);
      this.writeEntry(mergedOCD.manifest());
      this.close(!progression.canceled());
      progression.stepAchieved(
          progression.canceled() ? "OCD file writing has been interrupted" : "OCD file written successfully - " + this.tmpFile.getAbsolutePath());
      progression.complete();
    } catch (Exception e)
    {
      Log.warn(OCDWriter.class, ".write - Some problem occured during OCD file writing operation: " + e);
      e.printStackTrace();
      successful = false;
    }
    this.ocdWritten(successful);
    return this;
  }

  // public static OCDWriter write(OCDDocument document, File file, boolean
  // doUpdate, Listener listener)
  // {
  // return new OCDWriter(document, file, doUpdate, listener).write();
  // }

  public boolean writeHeader()
  {
    this.tmpFile.needDirs(false);
    this.zip = Zipper.OCD(tmpFile);
    this.mergedOCD.manifest().addMimeTypeItem();
    return zip != null;
  }

  public boolean writeEntry(OCDEntry entry)
  {
    return writeEntry(entry, null);
  }

  public boolean writeEntry(OCDEntry entry, OCDDocument source)
  {
    if (entry == null)
      return true;

    mergedOCD.manifest().updateItem(entry.item());
    String entryPath = entry.entryPath();
    boolean bug = true;

    String checksum = "";

    try
    {
      if (entry instanceof OCDPage)
      {
        OCDPage page = (OCDPage) entry;
        OCDThumbEntry thumb = source.thumbHandler.need(page);
        writeEntry(thumb, source);
        thumb.dispose();
      }

      if (this.set.contains(entryPath))// entry already written
        Log.debug(bug, this, ".writeEntry - already written: " + entryPath);
      else if (source != null)
      {        
        this.zip.addEntry(entryPath, source.zipFile().stream(entryPath));
        this.set.add(entryPath);
      } else
      {
        // ocd entry data, i.e., xml, svg, jpg, png...
        entry.setChecksum(checksum);
        this.zip.putNextEntry(new ZipEntry(entryPath));
        entry.writeEntry(zip);
        this.zip.flush();
        this.zip.closeEntry();// close entry but not zip outputstream itself
        this.set.add(entryPath);
      }

      if (entry == mergedOCD.manifest())
        this.manifestWritten = true;

      return true;
    } catch (Exception e)
    {
      Log.warn(this, ".writeEntry - " + e + ": " + entry);
      e.printStackTrace();
    }
    return false;
  }

  public void complete(boolean succeeded)
  {
    // need to close when overwriting
    this.mergedOCD.close();
    this.zip.dispose();
    this.set.clear();
    if (succeeded)
    {
      if (!file.ensureRW().delete() || !tmpFile.ensureRW().renameTo(file))
        Log.error(this, ".overwrite - unable to write OCD file at " + file.getAbsolutePath() + ". The file must be locked by another process !");
    } else
    {
      tmpFile.delete();
      Log.info(OCDWriter.class, ".write - OCD file writing interrupted");
    }
    this.mergedOCD.isSaving = false;
    if (!this.manifestWritten)
      Log.warn(this, ".complete - manifest not written !");
  }

  public void dispose()
  {
    this.mergedOCD.close();
    this.zip = null;
    this.mergedOCD = null;
  }

  public void close()
  {
    this.close(true);
  }

  public void close(boolean succeeded)
  {
    this.complete(succeeded);
    this.dispose();
  }

  @Override
  public boolean process(OCDPage page)
  {
    return false;
  }

  public void pageWritten(OCDPage page)
  {

  }

  public void ocdWritten(boolean successful)
  {

  }

  public static File3 Merge(File3 folder, boolean abcSort, boolean keepImages)
  {
    if (folder.isDirectory())
    {
      OCDMergeWriter writer = new OCDMergeWriter();
      writer.images(keepImages).files(folder.files(".ocd").sort(abcSort).array());
      writer.write(folder.extense(".ocd"));
      return folder.extense(".ocd");
    } else
    {
      Log.warn(OCDMergeWriter.class, ".MergeOCD - folder not found: " + folder);
      return null;
    }
  }
  
  public static void MergeKerns()
  {
    Files3 files = File3.Get("E:/schelker/kerns/").files(".ocd");
    
    for(File3 file: files)
    {
      File3 addon = file.repath("/kerns/",  "/addons/");
      if(addon.exists())
      {
        OCDMergeWriter writer = new OCDMergeWriter();
        writer.images(false).files(file,addon);
        writer.write(file.repath("/kerns/",  "/merged/"));        
      }
      else
        Sys.Println(addon.exists()+", "+addon.name());
    }
  }


  public static void main(String... args)
  {
    MergeKerns();
  }
}
