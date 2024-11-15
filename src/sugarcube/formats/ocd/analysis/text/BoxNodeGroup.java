package sugarcube.formats.ocd.analysis.text;

import sugarcube.common.data.collections.List3;
import sugarcube.formats.ocd.objects.OCDPaintable;

import java.util.Collections;
import java.util.Iterator;

public class BoxNodeGroup extends List3<BoxNode>
{
    public BoxNodeGroup(BoxNode... nodes)
    {
        this.addAll3(nodes);
    }

    public void add(OCDPaintable node)
    {
        this.add(new BoxNode(node));
    }

    public void growColumnFromMinYSortedNodes(BoxNodeGroup content)
    {
        BoxNode last = this.last();

        Iterator<BoxNode> it = content.iterator();
        while (it.hasNext())
        {
            BoxNode node = it.next();
            if (last.isColNext(node))
            {
                add(last = node);
                it.remove();
//                Log.debug(this, ".grow - adding: "+this.size());
            }
        }
    }

    public BoxNodeGroup sortMinY()
    {
        Collections.sort(this, (a, b) -> Float.compare(a.minY, b.minY));
        return this;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for (BoxNode box : this)
        {
            sb.append(box.isTB() ? box.node.asTextBlock().string()  : box.node.tag).append("\n");
        }
        return sb.toString();
    }
}
