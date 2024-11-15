package sugarcube.formats.ocd.analysis.text;

import sugarcube.common.graphics.geom.Line3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.formats.ocd.objects.OCDText;

public class WallSeg
{
    int x, minY, maxY, counter, fs;
    String str;

    public WallSeg(OCDText text)
    {
        Rectangle3 box = text.canon;
        x = Math.round(text.canon.coords.firstX());
        minY = Math.round(box.minY());
        maxY = Math.round(box.maxY());
        fs = Math.round(text.scaledFontsize());
        counter = 1;
        str = text.string();
    }

    public boolean isFollowedBy(WallSeg next)
    {
//        Log.debug(this, ".isFollowedBy - " + this.str + " (" + minY + "," + maxY + ")" + fs + "->" + next.str + " (" + next.minY + "," + next.maxY + ")" + next.fs + ", " + (x == next.x && Math.abs(fs - next.fs) <= 2 && minY <= next.minY && maxY + fs > next.minY - next.fs));
        return x == next.x && Math.abs(fs - next.fs) <= 2 && minY <= next.minY && maxY + fs > next.minY - next.fs;
    }

    public WallSeg mergeWith(WallSeg next)
    {
        if (next.maxY > maxY)
            maxY = next.maxY;
        counter++;
        return this;
    }

    public Line3 line()
    {
        return new Line3(x, minY, x, maxY);
    }

    public boolean doSeparate(float y, float minX, float maxX)
    {
        return minX < x && maxX > x - 2 && y > minY - fs / 2 && y < maxY + fs / 2;
    }

    public String toString()
    {
        return "x=" + x + ", str=" + str;
    }
}
