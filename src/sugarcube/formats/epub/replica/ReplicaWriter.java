package sugarcube.formats.epub.replica;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Props;
import sugarcube.common.data.collections.StringSet;
import sugarcube.common.data.collections.Tokens;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.IO;
import sugarcube.common.system.process.Progression;
import sugarcube.formats.epub.EPubProps;
import sugarcube.formats.epub.EPubWriter;
import sugarcube.formats.epub.structure.EPub;
import sugarcube.formats.epub.structure.EPubEventLayer;
import sugarcube.formats.epub.structure.EPubLinkBox;
import sugarcube.formats.epub.structure.js.JS;
import sugarcube.formats.epub.structure.res.RS;
import sugarcube.formats.epub.structure.xhtml.*;
import sugarcube.formats.epub.replica.svg.SVGPage;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDDocument;
import sugarcube.formats.ocd.objects.OCDPage;

import java.io.File;

public class ReplicaWriter extends EPubWriter
{
    public StringSet flattens = new StringSet();

    public ReplicaWriter(OCDDocument ocd, File file, Progression progression)
    {
        super(ocd, file, progression);
        this.cssFiles.add(RS.FONTS_CSS, RS.REPLICA_CSS, RS.EBOOK_CSS);
        this.jsFiles.add(RS.REPLICA_JS);
        this.props.put(EPubProps.KEY_HTML_TEXT, true);
    }

    public void writePage(OCDPage ocdPage)
    {
        pageIDs.clear();// we only need unique ids relative to an html page

        boolean inMemory = this.process(ocdPage);
        int nb = ocdPage.number();
        int nbOfPages = ocdPage.nbOfPages();
        int spreads = nb > 1 && nb < nbOfPages ? splitSpread : 1;

        System.out.print(nb % 10 == 0 ? ("\np" + nb + " ") : ".");

        try
        {
            this.viewBox = ocdPage.viewBox();

            float spreadWidth = viewBox.width / spreads;
            float spreadHeight = viewBox.height;

            int viewportWidth = props.dispWidth();
            int viewportHeight = props.dispHeight();

            this.pageScale = viewportWidth > 0 ? viewportWidth / spreadWidth : viewportHeight > 0 ? viewportHeight / spreadHeight : 1f;
            this.pageWidth = Math.round(spreadWidth * pageScale);
            this.pageHeight = Math.round(spreadHeight * pageScale);

            String[] bgs = new String[spreads];

            if (ocdPage.hasBlending())
                flattens.add("" + nb);

            boolean flatten = !props.vecGraphics() || flattens.has("" + nb) || flattens.has(ocdPage.entryFilename());

            if (flatten)
                ReplicaTools.Flatten(this, ocdPage, spreads, bgs);

            Rectangle3 subViewbox = viewBox;
            for (int i = 0; i < spreads; i++)
            {
                // HTMLPage htmlPage = isSVG ? null : new HTMLPage(page, this);
                if (spreads > 1)
                {
                    int subWidth = Math.round(viewBox.width / spreads);
                    subViewbox = new Rectangle3(viewBox.x + i * subWidth, viewBox.y, subWidth, viewBox.height);
                }

                SVGPage svgPage = new SVGPage(ocdPage, this).viewBox(subViewbox).flatten(flatten).create();
                String pageFilename = File3.Filename(ocdPage.entryFilename(), true);
                String spreadFilename = i > 0 ? pageFilename + "_" + i : pageFilename;

                HTMLDocument html = new HTMLDocument();

                HTMLHead head = html.head();
                head.title(pageFilename).utf8();
                head.viewport((viewportWidth > 0 ? R(viewportWidth) : pageWidth), (viewportHeight > 0 ? R(viewportHeight) : pageHeight));

                head.stylesheets(EPub.CSS_FOLDER, cssFiles);
                if (!props.cssInternal())
                    head.stylesheet(EPub.CSS_FOLDER + spreadFilename + ".css");
                head.javascripts(EPub.JS_FOLDER, jsFiles);

                String style = "w:" + pageWidth + "px; h:" + pageHeight + "px;";

                HTMLBody body = html.body("sugarcube").onload("onBodyLoad()").style(style);
                HTMLScript script = body.addScript();
                HTMLDiv rootDiv = body.addDiv().idClass("root", "root")
                        .style(style + (bgs == null || bgs[i] == null ? ""
                                : (" bg:url(" + EPub.IMAGE_FOLDER + bgs[i] + "); bgsize:" + pageWidth + "px " + pageHeight + "px;"
                                + " bgrep:no; bgpos: center center;")));
                rootDiv.add(svgPage);

                EPubEventLayer layer = new EPubEventLayer(this, ocdPage, svgPage, rootDiv);

                layer.addStaticVideos();

                for (EPubLinkBox link : svgPage.urlLinks)
                    rootDiv.addLinkBox(link.url, link.box);

                layer.addStaticAudios();

                JS js = new JS();
                js.writeln("function onBodyLoad() {");
                js.writeln("  if (document.body.className = 'sugarcube')");
                js.writeln("    document.body.className = 'loaded';\n");
                js.writeln(layer.onloadJS);
                js.writeln("}");
                script.js(js);

                // HTMLBox msgBox = root.addBox("message-box", 0, 0);
                // root.addScript(JS.Get("browserVersion('message-box');\n"));

                write(EPub.XHTML_DIR + spreadFilename + XHTML.EXT, html);

                String thumbName = spreadFilename + ".jpg";
                if (ocd.zipFile().has(OCD.THUMBS_DIR + thumbName))
                {
                    if (images.hasnt(thumbName))
                        writeCopy(EPub.IMAGE_DIR + thumbName, this.ocd.zipFile().entry(OCD.THUMBS_DIR + thumbName));
                } else
                    Log.debug(this, ".writePage - thumb not found: " + thumbName);

                images.add(thumbName);

                if (!props.cssInternal())
                {
                    write(EPub.CSS_DIR + spreadFilename + ".css", svgPage.style().css);
                    manifest(EPub.CSS_FOLDER + spreadFilename + ".css", EPub.CSS_FOLDER + spreadFilename + ".css");
                }

                pages.add(spreadFilename);
                svgPages.add(spreadFilename);
            }
            if (!inMemory)
                ocdPage.freeFromMemory();

        } catch (

                Exception e)
        {
            Log.warn(this, ".writePage - write error: " + e);
            error = e.getMessage();
            e.printStackTrace();
        }
        memory.snap("Page " + nb + "/" + nbOfPages + " written");
    }

    @Override
    public void writePages()
    {
        Log.debug(this, ".write - props: " + props);

        if (props.has("flatten_pages"))
        {
            this.flattens = this.flattens.addAll3(Tokens.Split(props.get("flatten_pages", "")).strings());
            Log.debug(this, ".writePages - flatten pages: " + flattens);
        }

        if (splitSpread < 1)
            splitSpread = 1;

        ocd.viewProps.box = props.get(EPubProps.KEY_VIEWBOX, ocd.viewProps.box);

        for (OCDPage page : epub.ocd)
        {
            if (progression.canceled())
                break;
            if (page.type == null || !page.type.toLowerCase().contains("modal"))
                writePage(page);

            progression.update(page.number() / (float) page.nbOfPages(), "Converting page - " + page.number() + "/" + page.nbOfPages());
        }
    }

    private static int R(float value)
    {
        return Math.round(value);
    }

    // public final List8 addMediaResource(String folder, OCDMediaAnnot annot)
    // {
    // List8 files = new List8();
    // for (String ocdPath : annot.filepaths())
    // {
    // String path = files.add3(folder + File3.Filename(ocdPath).replace(' ',
    // '_'));
    // if (media.notYet(path))
    // writeCopy(EPub.BOOK_DIR + path, ocd.zipFile().entry(ocdPath));
    // }
    // return files;
    // }

    public static void main(String... args)
    {

        File3 ocd = File3.Desk("TrainingCatalog_2019_FR_20190219.ocd");

        if (ocd != null)
        {
            File3 epub = ocd.extense(".epub");
            File3 folder = epub.extense("");
            epub.delete();
            folder.deleteDirectory();
            Replica.Convert(ocd, epub, new Props());
            IO.Unzip(epub, folder);
        }
    }

}
