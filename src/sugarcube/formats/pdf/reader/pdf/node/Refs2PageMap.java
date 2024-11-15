package sugarcube.formats.pdf.reader.pdf.node;

import sugarcube.common.data.collections.Map3;
import sugarcube.formats.pdf.reader.pdf.object.Reference;

public class Refs2PageMap
{
    private final Map3<Reference, Integer> ref2Page = new Map3<>();
    private final Map3<Integer, Reference> page2Ref = new Map3<>();

    public void incremement(Reference pageReference)
    {
        int pageNb = ref2Page.size() + 1;
        this.ref2Page.put(pageReference, pageNb);
        this.page2Ref.put(pageNb, pageReference);
    }

    public int pageNb(Reference ref)
    {
        return ref2Page.get(ref, -1);
    }

    public Reference reference(int pageNb)
    {
        // Log.debug(this, ".pageRef - get "+pageNb+": "+page2Ref.get(pageNb,
        // null));
        return page2Ref.get(pageNb, null);
    }


}
