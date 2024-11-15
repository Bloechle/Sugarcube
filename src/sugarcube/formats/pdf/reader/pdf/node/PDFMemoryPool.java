package sugarcube.formats.pdf.reader.pdf.node;

import sugarcube.common.system.util.Sys;
import sugarcube.common.data.collections.Map3;
import sugarcube.formats.pdf.reader.pdf.object.Reference;

public class PDFMemoryPool
{

    private final Map3<Reference, Object> pool = new Map3<>();
    private long timestamp;
    private long memMillis;

    public PDFMemoryPool(float memSeconds)
    {
        this.memMillis = memSeconds <= 0 ? -1 : (long) (memSeconds * 1000);
        timestamp = Sys.Millis();
    }

    public Object get(Reference ref)
    {
        Object obj = isUndef(ref) ? null : pool.get(ref, null);
        checkCleaner();
        return obj;
    }

    public Object add(Reference ref, Object node)
    {
        //node is added only second time to limit the pool to redundant data
        if (!isUndef(ref))
            pool.put(ref, pool.has(ref) ? node : null);
        return node;
    }

    private void checkCleaner()
    {
        if (memMillis > 0 && Sys.Millis() - timestamp > memMillis)
        {
            timestamp = Sys.Millis();
            pool.clear();
        }
    }

    private boolean isUndef(Reference ref)
    {
        return pool == null || ref == null || ref.isUndef();
    }
}
