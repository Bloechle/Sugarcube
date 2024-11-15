package sugarcube.common.system.io;

import sugarcube.common.data.collections.Set3;
import sugarcube.common.data.collections.StringSet;
import sugarcube.common.graphics.geom.Point3;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.util.List;

public class DragAndDrop extends TransferHandler
{

  public static interface Listener
  {
    public void dropped(DragAndDrop dnd);
  }

  private final Set3<Listener> listeners = new Set3<>();
  private final StringSet consumed = new StringSet();
  public TransferSupport support;//if null, drag is fake
  public Object source;
  public Object[] data;//used with fake drag or as complementary drag data

  //standard drag and drop
  public DragAndDrop(Listener listener)
  {
    this.listeners.add(listener);
  }
  
  //fake drag and drop
  public DragAndDrop(Object source, Object... data)
  {
    this.source = source;
    this.data = data;
  }  

  @Override
  public boolean canImport(TransferSupport support)
  {
    if (!support.isDrop())
      return false;
    if ((COPY & support.getSourceDropActions()) == COPY)
    {
      support.setDropAction(COPY);
      return true;
    }
    return false;
  }

  @Override
  public boolean importData(TransferSupport support)
  {
    this.consumed.clear();
    if (!canImport(this.support = support))
      return false;
    for (Listener listener : listeners)
      listener.dropped(this);
    return true;
  }

  public boolean isFake()
  {
    return this.support == null;
  }

  public Point3 dropLocation()
  {
    return support == null ? null : new Point3(support.getDropLocation().getDropPoint());
  }

  public DragAndDrop consume(Object obj)
  {
    this.consumed.add(obj.toString());
    return this;
  }

  public boolean matches(DataFlavor flavor)
  {
    if (support != null)
      for (DataFlavor f : support.getTransferable().getTransferDataFlavors())
        if (flavor.match(f))
          return true;
    if (data != null && flavor.match(DataFlavor.javaFileListFlavor))
      for (Object obj : data)
        if (obj instanceof File)
          return true;
    return false;
  }

  public File3[] files()
  {
    try
    {
      Set3<File> files = new Set3<>();
      if (matches(DataFlavor.javaFileListFlavor))
      {
        if (support != null)
          for (File file : (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor))
          {
            File3 file3 = new File3(file);
            if (consumed.hasnt(file3.toString()))
              files.add(file3);
          }
        if (data != null)
          for (Object obj : data)
            if (obj instanceof File)
            {
              File3 file3 = new File3((File) obj);
              if (consumed.hasnt(file3.toString()))
                files.add(file3);
            }
      }
      return files.toArray(new File3[0]);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return new File3[0];
  }

  public File3 file()
  {
    File3[] files = files();
    return files.length > 0 ? files[0] : null;
  }
}
