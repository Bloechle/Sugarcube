
package sugarcube.common.ui.gui;

import sugarcube.common.data.collections.StringList;

import java.util.Iterator;


public class ComboBox8 extends ComboBox3<String>
{  

  public ComboBox8(Iterator<String> it)
  {
    super(it);
  }

  public ComboBox8(String... objects)
  {
    super(objects);
  }
  
  @Override
  public StringList items()
  {
    StringList items = new StringList();
    for (int i = 0; i < this.getItemCount(); i++)
      items.add(this.getItemAt(i));
    return items;
  }  
  
}
