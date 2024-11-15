package sugarcube.formats.ocd.analysis.text;

import sugarcube.common.data.collections.List3;
import sugarcube.formats.ocd.objects.OCDText;

import java.util.Comparator;
import java.util.Iterator;

public class WallSegments extends List3<WallSeg>
{
    public WallSegments()
    {

    }

    public void add(OCDText text)
    {
        this.add(new WallSeg(text));
    }

    public void sortY()
    {
        sort(Comparator.comparingInt(a -> a.minY));
    }

    public WallSegments mergeWalls()
    {
        sortY();
        Iterator<WallSeg> it = iterator();
        WallSeg seg = null;
//        Log.debug(this, ".mergeWalls - start x="+first().x);
        while (it.hasNext())
        {
            WallSeg next = it.next();
            if (seg != null && seg.isFollowedBy(next))
            {
                seg.mergeWith(next);
                it.remove();
            } else
            {
                seg = next;
            }
        }
        return this;
    }

    public WallSegments trimSmallWalls(int minSize)
    {
        Iterator<WallSeg> it = iterator();
        while (it.hasNext())
        {
            WallSeg next = it.next();
            if(next.counter<minSize)
                it.remove();
        }
        return this;
    }


}
