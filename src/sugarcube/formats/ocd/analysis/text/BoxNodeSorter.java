package sugarcube.formats.ocd.analysis.text;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.List3;
import sugarcube.formats.ocd.objects.OCDPaintable;

import java.util.Iterator;

public class BoxNodeSorter
{
    private BoxNodeGroup content = new BoxNodeGroup();
    private List3<BoxNodeGroup> columns = new List3<>();

    public BoxNodeSorter()
    {
    }

    public BoxNodeSorter add(List3<? extends OCDPaintable> group)
    {
        for (OCDPaintable node : group)
            content.add(node);
        return this;
    }

    public BoxNodeSorter trimColumn(float v, boolean left)
    {
        Iterator<BoxNode> it = content.iterator();
        while(it.hasNext())
        {
            BoxNode node = it.next();
            Log.debug(this, ".trimColumn - "+v+", left="+left+", cx="+node.cx()+", remove="+(left != node.cx()<v));
            if(left != node.cx()<v)
            {
                it.remove();
            }
        }
        return this;
    }

    public BoxNodeSorter sort()
    {
        content.sortMinY();
        while (content.isPopulated())
        {
            BoxNodeGroup col = new BoxNodeGroup(content.removeFirst());
            col.growColumnFromMinYSortedNodes(content);
            columns.add(col);
        }
        columns.sort((a,b)->Float.compare(a.first().minX, b.first().minX));
        return this;
    }

    public BoxNodeGroup sortedGroup()
    {
        BoxNodeGroup group = new BoxNodeGroup();
        for (BoxNodeGroup col : columns)
            for (BoxNode node : col)
                group.add(node.node);
        return group;
    }

    public BoxNodeSorter update(List3<OCDPaintable> group)
    {
        group.clear();
        for (BoxNodeGroup col : columns)
            for (BoxNode node : col)
                group.add(node.node);
        return this;
    }

    public static BoxNodeSorter Get(List3<? extends OCDPaintable> group)
    {
        return new BoxNodeSorter().add(group);
    }

    public static void Sort(List3<OCDPaintable> group)
    {
        BoxNodeSorter sorter = BoxNodeSorter.Get(group);
        sorter.sort();
        sorter.update(group);
    }

}
