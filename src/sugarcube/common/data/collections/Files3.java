package sugarcube.common.data.collections;

import sugarcube.common.system.io.File3;

import java.io.File;
import java.io.FileFilter;
import java.util.Iterator;
import java.util.regex.Pattern;

public class Files3 extends List3<File3>
{

  public Files3(File3... files)
  {
    this.addAll3(files);
  }
  
  public Files3 regex(FileFilter filter)
  {
    if(filter==null)
      return this;
    
    Files3 files = new Files3();
    for (File3 file :this)    
     if(filter.accept(file))
       files.add(file);    
    return files;
  }
  
  public Files3 changeExtension(String ext)
  {
    Files3 files = new Files3();
    for(File3 file: this)
      files.add(file.extense(ext));
    return files;
  }
  
  public boolean deleteAll()
  {
    boolean ok = true;
    for(File3 file: this)
      if(file.exists() && !file.delete())
        ok = false;
    return ok;
  }
  
  public File3 firstExistant()
  {
    for (File3 file : this)
      if (file.exists())
        return file;
    return null;
  }

  public File3 random(String... exts)
  {
    Files3 files = this.ext(exts);
    return files.isPopulated() ? files.get((int) (files.size() * Math.random())) : null;
  }

  public Files3 add(String path)
  {
    this.add(File3.Get(path));
    return this;
  }

  public Files3 addAll(String... paths)
  {
    for (String path : paths)
      if (path != null)
        this.add(path);
    return this;
  }

  public boolean add(String path, boolean addIfExists)
  {
    return add(path == null ? null : File3.Get(path), addIfExists);
  }

  public boolean add(File3 file, boolean addIfExists)
  {
    if (addIfExists && !file.exists())
      file = null;
    return file == null ? false : this.add(file);
  }

  public Files3 add(File file)
  {
    super.add(File3.Wrap(file));
    return this;
  }

  public Files3 filenames(String... names)
  {
    Files3 files = new Files3();
    for (String name : names)
      for (File3 file : this)
        if (file.isName(name))
          files.add(file);
    return files;
  }

  public Files3 ext(String... exts)
  {
    if (exts == null || exts.length == 0)
      return this;
    Files3 files = new Files3();
    for (File3 file : this)
      if (file.isExt(exts))
        files.add(file);
    return files;
  }

  public Files3 regex(String regex)
  {
    return regex(regex, true);
  }

  public Files3 regex(String regex, boolean filename)
  {
    Pattern pat = Pattern.compile(regex);
    Iterator<File3> it = this.iterator();
    while (it.hasNext())
      if (!pat.matcher(filename ? it.next().name() : it.next().path()).find())
        it.remove();
    return this;
  }

  public Files3 regexNot(String regex)
  {
    return regexNot(regex, true);
  }

  public Files3 regexNot(String regex, boolean filename)
  {
    Pattern pat = Pattern.compile(regex);
    Iterator<File3> it = this.iterator();
    while (it.hasNext())
      if (pat.matcher(filename ? it.next().name() : it.next().path()).find())
        it.remove();
    return this;
  }

  public Files3 parents()
  {
    StringSet set = new StringSet();
    Files3 files = new Files3();
    for (File3 file : this)
      if (file.exists() && !file.isDirectory())
      {
        File3 parent = file.parent();
        if (set.hasnt(parent.path()))
        {
          set.add(parent.path());
          files.add(parent);
        }
      }
    return files;
  }

  public Files3 differences(Files3 files, boolean filename, boolean keepExt)
  {

    Files3 diff = new Files3();

    StringMap<File3> map = new StringMap<>();
    for (File3 f : this)
      map.put(filename ? f.name(!keepExt) : f.path(), f);

    for (File3 f : files)
    {
      String key = filename ? f.name(!keepExt) : f.path();
      if (map.has(key))
        map.remove(key);
      else
        diff.add(f);
    }

    diff.addAll(map.values());
    return diff;
  }

  public File3[] array()
  {
    return this.toArray(new File3[0]);
  }

  public String toString(boolean lineReturn)
  {
    return lineReturn ? toString().replace(", ", "\n") : toString();
  }

  public Files3 sort()
  {
    return sort(true);
  }

  public Files3 sort(boolean asc)
  {
    this.sort(File3.FilenameComparator(asc));
    return this;
  }

  public Files3 sortInvert()
  {
    return sort(false);
  }

  public File3 next(File3 file)
  {
    boolean found = false;
    for (File3 f : this)
    {
      if (f.equals(file))
        found = true;
      else if (found)
        return f;
    }
    return file;
  }

  public Files3 sortBySize(boolean asc)
  {
    this.sort(File3.SizeComparator(asc));
    return this;
  }

  public FileMap map(boolean filenames)
  {
    return map(filenames, null, true);
  }

  public FileMap map(boolean filenames, Boolean upperLowercase, boolean keepExtension)
  {
    FileMap map = new FileMap();
    for (File3 file : this)
    {
      String name = filenames ? file.name(!keepExtension) : file.path();
      map.put(upperLowercase == null ? name : upperLowercase ? name.toUpperCase() : name.toLowerCase(), file);
    }
    return map;
  }

  public void printOut()
  {
    for (File3 file : this)
      System.out.println(file.path());
  }

  public static Files3 Get(String... paths)
  {
    return new Files3().addAll(paths);
  }
}
