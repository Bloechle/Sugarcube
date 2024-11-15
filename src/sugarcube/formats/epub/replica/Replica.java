package sugarcube.formats.epub.replica;

import sugarcube.common.data.collections.Props;
import sugarcube.common.graphics.Image3;
import sugarcube.common.interfaces.Unjammable;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.IO;
import sugarcube.common.system.log.Log;
import sugarcube.common.system.log.Logger;
import sugarcube.common.system.process.Arguments;
import sugarcube.common.system.process.Progression;
import sugarcube.formats.epub.EPubProps;
import sugarcube.formats.epub.EPubWriter;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.analysis.DexterProps;
import sugarcube.formats.ocd.objects.OCDPageProcessor;
import sugarcube.formats.ocd.objects.OCDDocument;
import sugarcube.formats.pdf.reader.Dexter;

public class Replica implements Unjammable {

    protected Props props = new Props();
    protected DexterProps dexterProps = new DexterProps();
    public Progression pdfProgression = new Progression();
    public Progression ocdProgression = new Progression();
    public Progression outProgression = new Progression();
    protected boolean doTrack = true;
    protected OCDPageProcessor processor;

    public Replica() {
    }

    public Replica(Props props) {
        this.setProps(props);
    }


    public void setProps(Props props) {
        if (props != null)
            this.props = props;
    }

    public void setCanonProps(DexterProps props) {
        if (props != null)
            this.dexterProps = props;
    }

    public void setPDFProgression(Progression prog) {
        if (prog != null)
            this.pdfProgression = prog;
    }

    public void setOCDProgression(Progression prog) {
        if (prog != null)
            this.ocdProgression = prog;
    }

    public void setOutProgression(Progression prog) {
        if (prog != null)
            this.outProgression = prog;
    }

    public void setProgressions(Progression pdf, Progression ocd, Progression epub) {
        this.setPDFProgression(pdf);
        this.setOCDProgression(ocd);
        this.setOutProgression(epub);
    }

    public Replica processor(OCDPageProcessor processor) {
        this.processor = processor;
        return this;
    }

    public Props convert(File3 input, File3 output, File3... metaFiles) {
        if (props.has("log_level"))
            Log.setLevel(Logger.Level.instance(props.get("log_level", "info")));


        if (!input.isExtension(".pdf", OCD.EXT)) {
            Log.warn(Replica.class, ".convert requires an OCD/PDF file as input: " + input.getAbsolutePath());
            return new EPubProps();
        }

        File3 pdfFile = null;
        Log.debug(this, ".convert - input=" + input);
        if (input.isExtension(".pdf")) {
            pdfFile = new File3(input);
            Dexter dexter = new Dexter(dexterProps);
            dexter.setProgressions(pdfProgression, ocdProgression);
            dexter.convert(input, input = input.extense(OCD.EXT)).close();
        }

        OCDDocument ocd = new OCDDocument(input);
        if (metaFiles != null && metaFiles.length > 0 && metaFiles[0] != null) {
            for (File3 metaFile : metaFiles) {
                if (metaFile != null && metaFile.exists()) {
                    // PSMeta.populate(metaFile, ocd);
                    ocd.metadata().load(metaFile);
                }
            }
        }

        EPubWriter writer = writer(ocd, output, outProgression).processor(processor);
        writer.doTrack = this.doTrack;
        // before putAll to override ocd meta by new ones
        Log.debug(this, "props=" + props + ", writer.props=" + writer.props());
        writer.props().putAll(props);

        // after putAll since we need to check if dc already present
        ocd.metadata().populateMap(writer.props(), "dc_", false);
        writer.props().put("ocd_pages", ocd.nbOfPages());
        writer.props().put("ocd_filename", ocd.fileName());

        if (props.has("link_target"))
            writer.props().put("link_target", props.get("link_target", null));

        writer.write();

        ocd.close();

        // since file name may have changed (suffix)
        output = writer.file();
        writer.props().put("ocd_filepath", input.path());
        writer.props().put("ocd_filename", input.name());
        writer.props().put("out_filepath", output.path());
        writer.props().put("out_filename", output.name());

        String epubDir = output.dir();

        String unzip = props.get("unzip", null);
        if (unzip != null) {
            unzip = File3.RePath(epubDir, unzip, true);

            File3 epubFolder = new File3(unzip);
            if (epubFolder.exists())
                epubFolder.deleteDirectory();

            IO.Unzip(output, epubFolder);
            File3 opf = new File3(unzip, "OEBPS/content.opf");
            opf.renameTo(opf.extense(".xml"));
        }

        String cover = props.get("cover", null);
        if (cover != null) {
            Image3 image = writer.cover();
            if (image != null) {
                cover = File3.RePath(epubDir, cover, false);

                // String specimen = writer.props.specimen();
                // if (Zen.hasChar(specimen))
                // {
                // Log.info(this, ".convert - specimen: " + specimen);
                // Graphics3 g = image.graphics();
                // int w = image.width();
                // int h = image.height();
                //
                // Image3 demo = Image3.read(RS.stream(RS.IMAGE_DEMO));
                // demo = demo.decimate(0.8 * h / (float) demo.height());
                // g.draw(demo, 8, h / 2 - demo.height() / 2);
                // }

                int height = props.integer("cover_height", -1);
                if (height > 0 && height != image.height())
                    image = image.decimate(height / (double) image.height());
                // Log.debug(this, ".convert - cover: " + cover);
                image.write(cover);
            }
        }

        String epubcheck = props.get("epubcheck", null);
        if (epubcheck != null) {
            epubcheck = File3.RePath(epubDir, epubcheck, false);
            String report = writer.props().get("epubcheck_report", "no epubcheck report found");

            // if (writer.props().isKindleOutput())
            // {
            // String kindleReport = writer.props().get("kindlegen_report",
            // "no kindlegen report found");
            // Log.debug(this, ".convert - kindlegen: " + kindleReport);
            // }

            report = report.replace("ERROR: OEBPS/fonts.css: com.steadystate.css.parser.SACParserCSS21", "");
            report = report.replace("ERROR: OEBPS/replica.css: com.steadystate.css.parser.SACParserCSS21", "");

            IO.WriteText(File3.Get(epubcheck),
                    report.trim().isEmpty() ? "Validating against EPUB version 3.0: check finished without warning or error\n" : report);
        }

        if (pdfFile != null && props.bool("del_pdf", false))
            pdfFile.delete();
        if (input != null && props.bool("del_ocd", false))
            input.delete();
        if (output != null && props.bool("del_epub", false))
            output.delete();

        writer.dispose();

        return writer.props();
    }

    public ReplicaWriter writer(OCDDocument ocd, File3 epub, Progression progression) {
        return new ReplicaWriter(ocd, epub, progression);
    }

    public static Props Convert(File3 ocdFile, File3 epubFile, Props props, File3... meta) {
        Replica replica = new Replica(props);
        return replica.convert(ocdFile, epubFile, meta);
    }

    public static void main(String... arguments) {

        int debug = 0;
        if (debug > 0)
            Log.debug(Replica.class, ".main - running a debug case !");
        switch (debug) {
            case 1:
                arguments = new String[]{"-log_level=debug", "-resize=height", "-ebook_type=web", "-suffix=none", File3.desktop("TrainingCatalog_2015_SoftSkills_V2a.pdf").path()};
                break;
            case 2:
                arguments = new String[]{"-log_level=debug", "-width=0", "-height=0", "-sampling=2", "-antialias=2", "-jpg=0.9", "-detect_url=true",
                        "-svg_graphics=true -svg_text=true -toc=true", "-font_otf=true -font_svg=true -page_fonts=true -javascript=false -suffix=none", File3
                        .desktop("MakeMyParty.pdf").path()};
                break;
        }

        Arguments args = new Arguments(arguments);

        // Log.setLevel(Level.instance(args.options.get("log_level", "info")));

        File3 file = args.firstFile();
        Convert(file, file.extense(".epub"), args.options);

    }


}
