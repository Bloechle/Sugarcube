package sugarcube.formats.pdf.reader.pdf.node.actions;

import sugarcube.common.system.log.Log;
import sugarcube.formats.pdf.reader.pdf.node.PDFDocument;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.node.nametree.PDFNames;
import sugarcube.formats.pdf.reader.pdf.object.*;

public class PDFGotoAction extends PDFAction
{
    private Reference pageRef = null;
    private int pageNb = -1;
    private String pageName = null;
    //private byte[] data;

    public PDFGotoAction(PDFNode parent, PDFDictionary map)
    {
        super(parent, map);

        PDFObject o = map.get("D").unreference();

        PDFArray a = o.toPDFArray();

        if (a.isValid())
        {
            PDFPointer pointer = a.get(0).toPDFPointer();
            if (pointer.isValid())
            {
                pageRef = pointer.get();
            } else
            {
                pageNb = a.get(0).intValue(-1);
                pageRef = document().refs2PagesMap.reference(pageNb);
                Log.debug(this, " - pageNb=" + pageNb +", "+pageRef);
            }

        } else
        {
            if ((pageName = o.stringValue(null)) != null)
            {
                PDFDocument doc = document();
                PDFNames names = doc == null ? null : doc.names();
                PDFDest pageDest = names == null ? null : names.dest(pageName);
//        Log.debug(this,  " - string Dest:"+value+", "+dest.data);
                if (pageDest == null)
                {
                    Log.debug(this, " - dest not found: " + pageName + " in " + this.reference);
                } else
                {
                    pageRef = pageDest.pageRef;
//          Log.debug(this, " - dest="+value+", gotoPage="+pageRef);
                }
            }

        }

    }

    public Reference gotoRef()
    {
        return pageRef;
    }

    @Override
    public boolean isGotoAction()
    {
        return true;
    }

    @Override
    public String sticker()
    {
        return type + "[" + keyS + "]";
    }

    @Override
    public String toString()
    {
        return type + "[" + keyS + "]"
                + "\nPage[" + pageRef + "]"
                + "\nPageNb[" + pageNb + "]"
                + "\nPageName[" + pageName + "]";

    }
}
