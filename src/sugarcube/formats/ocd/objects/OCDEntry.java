package sugarcube.formats.ocd.objects;

import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.Str;
import sugarcube.common.data.Base;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.IO;
import sugarcube.common.system.io.Zip;
import sugarcube.common.system.io.ZipItem;
import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.objects.document.OCDItem;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class OCDEntry extends OCDNode
{
  // protected Set8 pages = new Set8();
  public transient boolean inMemory = false;
  public transient boolean doDelete = false;
  public String entryPath = "";
  public String checksum = "";

  public OCDEntry(String tag, OCDNode parent)
  {
    this(tag, parent, "");
  }

  public OCDEntry(String tag, OCDNode parent, ZipItem zip)
  {
    this(tag, parent, zip.path());
  }

  public OCDEntry(String tag, OCDNode parent, String path)
  {
    super(tag, parent);
    this.entryPath = path == null || path.isEmpty() ? this.tag + Xml.FILE_EXTENSION : path;
  }

  public OCDItem item()
  {
    return new OCDItem(entryPath, OCDItem.TYPE_RESOURCE);// .addPages(pages);
  }
  
  public boolean existsTmp()
  {
    File3 tmp = needTmp();
    return tmp == null ? false : tmp.exists();
  }

  public File3 needTmp()
  {    
    OCDDocument ocd = doc();
    return ocd == null ? null : ocd.temp(this.entryFilename());    
  }  
  

  public boolean isEntry(String path)
  {
    return this.entryPath.equals(path);
  }
  
  public String entryDocPath()
  {
    return this.doc().filePath+"/"+this.entryPath;
  }

  public String entryPath()
  {
    return entryPath;
  }

  public String entryRepath(String regex, String replace)
  {
    return entryPath.replaceAll(regex, replace);
  }

  public void setEntryPath(String filepath)
  {
    this.entryPath = filepath;
  }

  public void setEntryFilename(String filename)
  {
    this.entryPath = entryDirectory() + filename;
  }

  public String entryFilename()
  {
    return File3.Filename(entryPath);
  }

  public void setEntryDirectory(String directory)
  {
    this.entryPath = directory + entryFilename();
  }

  public String entryDirectory()
  {
    return File3.directory(entryPath);
  }   

  public void writeEntry(OutputStream stream)
  {
    this.writeNode(stream);
  }

  public void readEntry(InputStream stream)
  {
    if (stream != null && readNode(stream))
      this.setInMemory(true);
  }

  public void readEntry(File3 file)
  {
    InputStream stream = file.inputStream();
    this.readEntry(stream);
    IO.Close(stream);
  }
  
  public boolean readEntry(ZipItem zip)
  {
    if (zip != null)
    {
      readEntry(zip.stream());
      return true;
    }
    return false;
  }

  public boolean readEntryTry(ZipItem zip)
  {
    if (zip != null && this.isEntry(zip.path()))
    {
      readEntry(zip.stream());
      // this.setEntryFilepath(zip.path());
      return true;
    }
    return false;
  }

  public InputStream zipInputStream()
  {
    OCDDocument ocd = this.document();
    Zip zip = ocd == null ? null : ocd.zipFile();
    return zip == null ? null : zip.stream(this.entryPath());
  }

  public boolean ensureInMemory()
  {
    return inMemory;
  }

  public void freeFromMemory()
  {

  }

//  public void freeFromMemory(boolean wasInMemory)
//  {
//    if (!wasInMemory)
//      this.freeFromMemory();
//  }

  public boolean isInMemory()
  {
    return inMemory;
  }

  public void setInMemory(boolean inMemory)
  {
    this.inMemory = inMemory;
  }

  @Override
  public boolean modified()
  {
    String check = this.computeChecksum();
    boolean mod = !Zen.equals(check, checksum);
//    Log.trace(this,  ".modified - checksum="+checksum+", computed="+check+", mod="+mod);
    return mod;
  }

  public void setChecksum(String checksum)
  {
    this.checksum = checksum;
  }

  public String computeChecksum()
  {
//    Log.debug(this, ".computeChecksum - inMem=" + this.isInMemory() + ", checksum=" + checksum);
    if (!this.isInMemory())
      return "";
    String tmp = this.checksum;
    this.checksum = "";
    String xml = this.xmlString().replace(" ", "").replace("\n",  "");
    String compute = Base.x32.get(Str.Hash(xml)) + "-" + xml.length();
    this.checksum = tmp;
    return compute;
  }

  public void copyTo(OCDEntry node)
  {
    super.copyTo(node);
    node.entryPath = this.entryPath;
  }
}
