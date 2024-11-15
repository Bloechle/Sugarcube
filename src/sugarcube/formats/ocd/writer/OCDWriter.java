package sugarcube.formats.ocd.writer;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.Occurrences;
import sugarcube.common.data.collections.Str;
import sugarcube.common.data.collections.StringOccurrences;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.Zipper;
import sugarcube.common.system.process.Progression;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDPageProcessor;
import sugarcube.formats.ocd.objects.*;
import sugarcube.formats.ocd.objects.OCDAddon;
import sugarcube.formats.ocd.objects.font.SVGFont;

import java.io.File;
import java.util.Arrays;
import java.util.zip.ZipEntry;

public class OCDWriter implements OCDPageProcessor
{
    public static final int BUFFER_SIZE = 8192;
    public StringOccurrences writtenEntriesSet = new StringOccurrences(); //already written entries
    public Zipper zip;
    public File3 tmpFile;
    public File3 file;
    public OCDDocument ocd;
    public Progression progression;
    public boolean doCreateFromScratch = false;
    public boolean doDisposePages = false;
    public boolean doCloseDocument = true;
    public OCDPageProcessor processor = this;
    public boolean useTmp = true;
    public boolean manifestWritten = false;
    public StringBuilder debugModified = new StringBuilder();

    public OCDWriter(OCDDocument ocd, File file)
    {
        this.ocd = ocd;
        if (file == null)
            this.file = new File3(ocd.filePath());
        else if (file instanceof File3)
            this.file = (File3) file;
        else
            this.file = new File3(file);
        this.file = this.file.extense(OCD.FILE_EXTENSION);
        this.file.ensureRW();
        this.ocd.setFilePath(this.file.getAbsolutePath());
        this.tmpFile = this.file.extense(".ocd_w");
        this.progression = new Progression("Writing OCD File - " + this.file.getName());
        // first step metadata, last step writing...
        this.progression.setNbOfSteps(ocd.pageHandler.nbOfPages() + 2);
    }

    public OCDWriter(OCDDocument ocd, File file, boolean createFromScratch)
    {
        this(ocd, file);
        this.doCreateFromScratch = createFromScratch;
    }

    public OCDWriter keepOpen()
    {
        this.doCloseDocument = false;
        return this;
    }

    public OCDWriter clean()
    {
        this.doDisposePages = true;
        return this;
    }

    public OCDWriter create()
    {
        this.doCreateFromScratch = true;
        return this;
    }

    public OCDWriter processor(OCDPageProcessor processor)
    {
        this.processor = processor == null ? this : processor;
        return this;
    }

    public OCDWriter progressor(Progression.Listener progressor)
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

    public OCDWriter write()
    {
        this.manifestWritten = false;
        ocd.isSaving = true;
        boolean successful = true;
        progression.start("Writing document - " + file.getAbsolutePath());
        ocd.manifest().clear();
        writeHeader();
        try
        {
            for (OCDPage page : ocd)
            {
                boolean pageInMem = page.isInMemory();
                if (!page.isInMemory() && useTmp && page.existsTmp())
                {
                    page.ensureInMemory(page.tmp);
                    page.tmp.delete();
                }
                if (processor != null)
                    processor.process(page);
                if (processor != this)
                    process(page);
                writeEntry(page);
                pageWritten(page);

                progression.stepAchieved("Writing document page " + page.number() + "/" + page.nbOfPages());
                if (doDisposePages)
                    page.dispose();
                else if (doCreateFromScratch || !pageInMem)
                    page.freeFromMemory();
            }
            for (OCDImageEntry image : ocd.imageHandler)
                writeEntry(image);
            for (OCDAudioEntry audio : ocd.audioHandler)
                writeEntry(audio);
            for (OCDVideoEntry video : ocd.videoHandler)
                writeEntry(video);
            for (SVGFont font : ocd.fontHandler)
                writeEntry(font);
            for (OCDAddon addon : ocd.addonHandler)
                writeEntry(addon);
            writeEntry(ocd.metadata());
            writeEntry(ocd.nav());
            writeEntry(ocd.styles());
            writeEntry(ocd);
            writeEntry(ocd.manifest());
            close(!progression.canceled());
            progression.stepAchieved(
                    progression.canceled() ? "Document writing has been interrupted" : "Document has been saved ");
            progression.complete();
            Log.info(this, progression.canceled() ? "OCD file writing has been interrupted" : "OCD file written successfully - " + this.tmpFile.getAbsolutePath());
        } catch (Exception e)
        {
            Log.warn(OCDWriter.class, ".write - Some problem occured during OCD file writing operation: " + e);
            e.printStackTrace();
            successful = false;
        }

        Log.debug(this, ".write - modified: " + debugModified.toString());

        ocdWritten(successful);
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
        this.ocd.manifest().addMimeTypeItem();
        return zip != null;
    }

    public boolean writeEntry(OCDEntry entry)
    {
        return writeEntry(entry, entry == null ? false : entry.existsTmp());
    }

    private void repairImages(OCDPage page)
    {
        for (OCDImage image : page.images())
        {
            String filename = image.filename();
            boolean isPNG = filename.endsWith(".png");
            String filenameBis = isPNG ? filename.replace(".png", ".jpg") : filename.replace(".jpg", ".png");

            OCDImageEntry entry = ocd.imageHandler.map().get(filename);
            OCDImageEntry entryBis = ocd.imageHandler.map().get(filenameBis);
            if (entry != null && entryBis != null)
            {
                byte[] data = null;
                if (Arrays.equals(data = entry.data(false), entryBis.data(false)))
                {
                    Log.debug(this,
                            ".repairImages - found duplicate: " + filename + ", " + filenameBis + ", " + data[0] + " " + data[1] + " " + data[2] + " " + data[3]);
                    boolean isDataPNG = data.length > 10 && data[0] == -119 && data[1] == 80 && data[2] == 78 && data[3] == 71;
                    if (isDataPNG)
                    {
                        if (isPNG)
                        {
                            entryBis.doDelete = true;
                        } else
                        {
                            entry.doDelete = true;
                            image.setFilename(filenameBis);
                            page.modify();
                        }
                        Log.debug(this, ".repairImages - isDataPNG=" + isDataPNG);

                    } else
                    {
                        if (isPNG)
                        {
                            entry.doDelete = true;
                            image.setFilename(filenameBis);
                            page.modify();
                        } else
                        {
                            entryBis.doDelete = true;
                        }
                    }

                }
            }

        }

    }

    public boolean writeEntry(OCDEntry entry, boolean modified)
    {
        if (entry == null)
            return true;

        if (entry.doDelete)
        {
            ocd.manifest().removeItem(entry.item());
            Log.debug(this, ".writeEntry - removing " + entry.entryPath);
            return true;
        }

        ocd.manifest().updateItem(entry.item());

        String entryPath = entry.entryPath();
        String checksum = "";

        try
        {
            // refresh thumb image
            if (entry instanceof OCDPage)
            {
                OCDPage page = (OCDPage) entry;
                repairImages(page);

                if (modified || (modified = !Str.Equals(checksum = entry.computeChecksum(), entry.checksum)))
                {
                    OCDThumbEntry thumb = ocd.thumbHandler.add(page, true);
                    writeEntry(thumb);
                    thumb.dispose();
                    for (OCDImage image : page.images())
                        if (!image.isView())
                        {
                            OCDImageEntry imageEntry = ocd.imageHandler.addEntry(image);
                            writeEntry(imageEntry);
                            imageEntry.dispose();
                        }

                } else
                {
                    OCDThumbEntry thumb = ocd.thumbHandler.need(page);
                    writeEntry(thumb);
                    thumb.dispose();
                }
            } else if (!modified)
                modified = !Zen.equals(checksum = entry.computeChecksum(), entry.checksum);

            if (this.writtenEntriesSet.contains(entryPath))
                this.writtenEntriesSet.inc(entryPath); //entry already written, just increment counter
            else if (!doCreateFromScratch && !modified && ocd.existsZipEntry(entryPath))
            {
                this.zip.addEntry(entryPath, ocd.zipFile().stream(entryPath));
                this.writtenEntriesSet.inc(entryPath);
            } else
            {
                // ocd entry data, i.e., xml, svg, jpg, png...
                this.debugModified.append(entryPath + ", ");
                entry.setChecksum(checksum);
                this.zip.putNextEntry(new ZipEntry(entryPath));
                entry.writeEntry(zip);
                this.zip.flush();
                this.zip.closeEntry();// close entry but not zip outputstream itself
                this.writtenEntriesSet.inc(entryPath);
            }

            if (entry == ocd.manifest())
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
        Occurrences<String> duplicateEntries = writtenEntriesSet.trimMin(2);
        if (!duplicateEntries.isEmpty())
            Log.debug(this, ".complete - duplicate entries: " + duplicateEntries);
        // need to close when overwriting
        if (doCloseDocument)
            ocd.close();
        zip.dispose();
        writtenEntriesSet.clear();
        if (succeeded)
        {
            if (!file.ensureRW().delete() || !tmpFile.ensureRW().renameTo(file))
                Log.error(this, ".complete - unable to overwrite OCD file at " + file.getAbsolutePath() + ". The file must be locked by another process !");
        } else
        {
            tmpFile.delete();
            Log.info(OCDWriter.class, ".complete - OCD file writing interrupted");
        }
        ocd.isSaving = false;
        if (!manifestWritten)
            Log.warn(this, ".complete - manifest not written !");
    }

    public void dispose()
    {
        if (doCloseDocument)
            ocd.close();
        zip = null;
        ocd = null;
    }

    public void close()
    {
        close(true);
    }

    public void close(boolean succeeded)
    {
        complete(succeeded);
        dispose();
    }

    @Override
    public boolean process(OCDPage page)
    {
        return true;
    }

    public void pageWritten(OCDPage page)
    {

    }

    public void ocdWritten(boolean successful)
    {

    }
}
