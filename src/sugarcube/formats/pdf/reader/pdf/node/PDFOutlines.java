package sugarcube.formats.pdf.reader.pdf.node;

import sugarcube.common.system.log.Log;
import sugarcube.formats.pdf.reader.pdf.node.actions.PDFAction;
import sugarcube.formats.pdf.reader.pdf.node.actions.PDFDest;
import sugarcube.formats.pdf.reader.pdf.node.nametree.PDFNames;
import sugarcube.formats.pdf.reader.pdf.object.PDFArray;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.PDFObject;
import sugarcube.formats.pdf.reader.pdf.object.Reference;

import java.util.Collections;

public class PDFOutlines extends PDFNode<PDFOutlines>
{
    private int count = 0;
    private String title = "Root";
    private PDFAction action = null;
    private Reference gotoPage = null;
    private String data = "";

    public PDFOutlines(PDFNode parent, PDFDictionary map)
    {
        super("Outlines", parent);
        this.reference = map.reference();
        this.count = map.get("Count").intValue(count);

        PDFObject obj = map.get("Title").unreference();
        // if (obj.isPDFString())
        // Log.debug(this, " - title=" + obj.stringValue() + ", bytes=" +
        // Array.toString(obj.toPDFString().getOriginalInts()));

        this.title = Norm(obj.stringValue(title));

        if (map.has("First"))
            this.add(new PDFOutlines(this, map.get("First").toPDFDictionary()));
        if (map.has("Next"))
            parent.add(new PDFOutlines(parent, map.get("Next").toPDFDictionary()));
        Collections.reverse(children);

        if (map.has("A"))
        {
            action = PDFAction.Get(this, map.get("A").toPDFDictionary());
            if (action.isGotoAction())
                gotoPage = action.asGotoAction().gotoRef();
            else if (action.isJavascriptAction())
            {
                gotoPage = action.asJavascriptAction().gotoRef();
                // Log.debug(this, " - gotoPage: "+action.asJavascriptAction().pageNum()
                // +", "+gotoPage);
            }
        }
        if (map.has("Dest"))
        {
            PDFObject destObj = map.get("Dest").unreference();
            data = destObj.toString();
            if (destObj.isPDFString())
            {
                PDFDocument doc = this.document();
                PDFNames names = doc == null ? null : doc.names();
                PDFDest dest = names == null ? null : names.dest(destObj.stringValue());
                // Log.debug(this, " - string Dest: "+data+",
                // keys="+names.dests.keys());
                if (dest == null)
                {
                    Log.debug(this, " - dest not found: " + destObj.stringValue() + " in " + this.reference);
                } else
                {
                    gotoPage = dest.pageRef;
                    // Log.debug(this, " - dest="+destObj.stringValue()+",
                    // gotoPage="+gotoPage+", title="+title);
                }
            } else
            {
                // MsWord uses this way
                PDFArray dest = map.get("Dest").toPDFArray();
                if (dest.isValid())
                    gotoPage = dest.get(0).toPDFPointer().get();
            }
        }
    }

    public static String Norm(String title)
    {
        //Log.debug(PDFOutlines.class, ".Norm - " + title+", codes="+ Str.ToCodePointString(title));

        for (String norm : new String[]{"\r\n", "\n", "\r", "\t", "\u000B"})
            title = title.replace(norm, " ");

        title = title.replace("\u0084", "—");
        title = title.replace("\u0085", "-");
        title = title.replace("\u008D", "“");
        title = title.replace("\u008E", "“");
        title = title.replace("\u008F", "‘");
        title = title.replace("\u0090", "'");

        return title.replaceAll("\\s+", " ").trim();
    }

    public Reference gotoPage()
    {
        return gotoPage;
    }

    public boolean isRoot()
    {
        return this.parent.isDocument();
    }

    public String title()
    {
        return title;
    }

    public PDFAction action()
    {
        return action;
    }

    @Override
    public String sticker()
    {
        return title.equals("Root") ? type + " " + reference() : title;
    }

    @Override
    public String toString()
    {
        return type + reference() + "\nCount[" + count + "]" + "\nTitle[" + title + "]" + "\nAction[" + action + "]" + "\nGotoPage[" + gotoPage + "]"
                + "\nDest[" + data + "]";
    }
}
