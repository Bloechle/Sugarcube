package sugarcube.common.data.collections;

import sugarcube.common.system.io.File3;

public class FileMap extends StringMap<File3>
{
  public FileMap()
  {

  }

  public FileMap copy()
  {
    FileMap copy = new FileMap();
    copy.putAll(this);
    return copy;
  }

  public Files3 files()
  {
    Files3 files = new Files3();
    for (File3 file : this)
      files.add(file);
    return files;
  }
}
