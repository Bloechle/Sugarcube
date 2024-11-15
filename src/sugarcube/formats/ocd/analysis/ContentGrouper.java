package sugarcube.formats.ocd.analysis;

import sugarcube.common.data.collections.StringMap;
import sugarcube.formats.ocd.objects.OCDContent;
import sugarcube.formats.ocd.objects.OCDPage;
import sugarcube.formats.ocd.objects.OCDPaintable;
import sugarcube.formats.ocd.objects.lists.OCDList;

import java.util.Map;

public class ContentGrouper
{

  public StringMap<OCDList> groups = new StringMap<>();

  public OCDList add(OCDPaintable node, String label, String contentID)
  {
    String id = label + "-" + contentID;
    OCDList list = groups.get(id);
    if (list == null)
      groups.put(id, list = new OCDList());
    list.add(node);
    return list;
  }

  public void regroup(OCDPage page)
  {
    for (Map.Entry<String, OCDList> entry : groups.entrySet())
    {
      String id = entry.getKey();
      OCDList list = entry.getValue();
      if (list.size() > 1)
      {
        OCDContent content = page.content().newContent(list.first().index());
        content.setLabel(id);
        for (OCDPaintable node : list)
        {
          if (node.remove())
            content.add(node);
        }

      }
    }

  }

}
