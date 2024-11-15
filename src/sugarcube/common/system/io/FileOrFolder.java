package sugarcube.common.system.io;

public class FileOrFolder
{
    private File3 file;
    private File3[] files;
    private Zip zip;
    private ZipItem[] items;
    private transient int size = 0;

    public FileOrFolder(File3 file, File3... allFiles)
    {
        this.file = file;
        if (file.isExt(".zip"))
        {
            zip = Zip.Get(file);
            items = zip.array();
            size = items.length;
        } else if (file.isDirectory())
        {
            files = file.files().array();
            size = files.length;
        } else if (allFiles != null && allFiles.length > 0)
        {
            files = allFiles;
            size = files.length;
        } else
        {
            files = file.asArray();
            size = files.length;
        }
    }

    public File3 file()
    {
        return file;
    }

    public String name(int index)
    {
        return zip == null ? files[index].name() : items[index].name();
    }

    public byte[] data(int index)
    {
        if (zip == null)
            return files[index].bytes();
        else
        {
            byte[] data = items[index].bytes();
            if (index == items.length - 1)
                close();
            return data;
        }
    }

    public int nbOfFiles()
    {
        return size;
    }

    public void close()
    {
        if (zip != null)
            zip.dispose();
    }


    @Override
    public String toString()
    {
        return file.path();
    }
}
