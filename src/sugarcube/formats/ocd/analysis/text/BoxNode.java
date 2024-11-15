package sugarcube.formats.ocd.analysis.text;

import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.formats.ocd.objects.OCDPaintable;

public class BoxNode
{
    public OCDPaintable node;
    public float minX, minY, maxX, maxY, delta;

    public BoxNode(OCDPaintable node)
    {
        this.node = node;
        Rectangle3 r = node.bounds();
        minX = r.minX();
        minY = r.minY();
        maxX = r.maxX();
        maxY = r.maxY();
        delta = maxY - minY;
        int nb = node.nbOfChildren();
        if (nb > 0)
            delta /= nb;
    }

    public float cx()
    {
        return (maxX - minX) / 2f + minX;
    }

    public float cy()
    {
        return (maxY - minY) / 2f;
    }

    public boolean isTB()
    {
        return node.isTextBlock();
    }

    public boolean typeFriends(BoxNode box)
    {
        return node.tag.equalsIgnoreCase(box.node.tag);
    }

    public boolean deltaFriends(BoxNode box)
    {
        return delta < box.delta ? box.delta - delta < delta / 2 : delta - box.delta < box.delta / 2;
    }

    public boolean isColNext(BoxNode box)
    {
        if (isTB() && box.isTB() && box.minY >= maxY && box.minY < maxY + 2 * delta && this.overlapRatioX(box) > 0.5 && deltaFriends(box))
        {
//            Sys.Println("\n" + (int) maxY + "," + node.asTextBlock().string() + " \n->\n " + (int) box.minY + ", " + box.node.asTextBlock().string() + "\n");
            return true;
        }
        return false;
    }

    public float overlapRatioX(BoxNode node)
    {
        float min = Math.max(minX, node.minX);
        float max = Math.min(maxX, node.maxX);
        if (max <= min)
            return 0;
        return (max - min) / Math.min(maxX - minX, node.maxX - node.minX);
    }

    public String toString()
    {
        return node.toString();
    }
}

