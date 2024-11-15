package sugarcube.formats.ocd.analysis.text;

import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Map3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Image3;
import sugarcube.common.system.io.File3;
import sugarcube.formats.ocd.objects.OCDPage;
import sugarcube.formats.ocd.objects.OCDText;

public class WallDetection
{
    static int DEBUG_COUNTER = -1; // >=0 means debug mode

    OCDPage page;
    Map3<Integer, WallSegments> map = new Map3<>();

    public WallDetection()
    {

    }

    public void detect(List3<OCDText> texts)
    {
        if (DEBUG_COUNTER > -1)
            ++DEBUG_COUNTER;

        map.clear();
        if (texts.isPopulated())
            page = texts.first().page();

        for (OCDText t : texts)
        {
            WallSeg seg = new WallSeg(t);
            WallSegments segs = map.get(seg.x, null);
            if (segs == null)
                map.put(seg.x, segs = new WallSegments());
            segs.add(seg);
        }

        debug(Color3.GREEN_LEAF, "_before");
        for (Integer key : map.keyList())
        {
            WallSegments segs = map.get(key, null);
            if (segs == null || segs.size() < 3)
                map.remove(key);
            else
                segs.mergeWalls();
        }

        WallSegments allSegs = new WallSegments();

        for (Integer key : map.keyList())
        {
            WallSegments segs = map.get(key, null);
            if (segs != null)
            {
                segs.trimSmallWalls(4);
                allSegs.addAll(segs);
            }
        }

        map.clear();
        map.put(0, allSegs);

        debug(Color3.BLUE_PIGMENT, "_after");
    }

    public boolean canMerge(OCDText t1, OCDText t2)
    {
        return canMerge(t1.canon.coords.lastY(), t1.canon.coords.lastX(), t2.canon.coords.firstX());
    }

    public boolean canMerge(float y, float x0, float x1)
    {
        WallSegments segs = map.get(0, null);
        if (segs != null)
            for (WallSeg seg : segs)
                if (seg.doSeparate(y, x0, x1))
                    return false;
        return true;
    }

    public void debug(Color3 c, String name)
    {
        if(DEBUG_COUNTER<0)
            return;

        if (page == null)
            return;

        Image3 img = new Image3((int) page.width, (int) page.height);
        img.clearWhite();

        Graphics3 g = img.graphics();

        g.setColor(c);
        for (WallSegments segs : map.values())
        {
            for (WallSeg seg : segs)
                g.draw(seg.line());
        }

        File3 ocdFile = page.document().file().extense(".png");

        File3 file = ocdFile.parent().get("wall/"+ocdFile.name()).postfix(name + "_" + (DEBUG_COUNTER));
        img.write(file);
    }

    public static WallDetection Detect(List3<OCDText> texts)
    {
        WallDetection wall = new WallDetection();
        wall.detect(texts);
        return wall;
    }

}
