package sugarcube.common.system.io;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.*;
import sugarcube.common.data.Base;
import sugarcube.common.interfaces.Seeker;
import sugarcube.common.interfaces.Visitor;
import sugarcube.common.numerics.Math3;
import sugarcube.formats.ocd.objects.OCDDocument;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

//TODO File class full implementation (listFiles()...)
public class File3 extends File implements Iterable<File3>
{

    public static final String SLASH = "/";
    public static final String SEPARATOR = SLASH;
    public static final String USER_HOME;
    public static final String USER_WORK;
    public static final String TEMP_DIR;

    static
    {
        USER_HOME = directorize(System.getProperty("user.home"));
        USER_WORK = directorize(System.getProperty("user.dir"));
        TEMP_DIR = directorize(System.getProperty("java.io.tmpdir"));
    }

    public File3(File parent, String child)
    {
        super(parent, child);
    }

    public File3(String pathname)
    {
        super(pathname);
    }

    public File3(String parent, String child)
    {
        super(parent, child);
    }

    public File3(File file)
    {
        super(file == null ? "" : file.getPath());
    }

    public File3 New(String child)
    {
        return new File3(this, child);
    }

    public OCDDocument loadOCD()
    {
        return OCDDocument.Load(this);
    }

    public File3 get(String child)
    {
        return new File3(this, child);
    }

    public boolean isMime(String... mimes)
    {
        return Mime.is(path(), mimes);
    }

    public File3 needDirs()
    {
        return needDirs(exists() ? isDirectory() : !name().contains("."));
    }

    public File3 needFileDirs()
    {
        return this.needDirs(false);
    }

    public File3 needFolderDirs()
    {
        return this.needDirs(true);
    }

    public File3 needDirs(boolean isFolder)
    {
        try
        {
            File3 dir = isFolder ? this : parent();
            if (!dir.exists())
                dir.mkdirs();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return this;
    }

    public final File3 ensureRW()
    {
        this.setRW(true, true);
        return this;
    }

    public boolean setRW(boolean readable, boolean writable)
    {
        return setReadable(readable, false) && setWritable(writable, false);
    }

    // public boolean isLocked()
    // {
    // try
    // {
    // FileChannel channel = new RandomAccessFile(this, "rw").getChannel();
    // try
    // {
    // FileLock lock=channel.tryLock();
    // if(lock==null)
    // return true;
    // else
    // lock.release();
    // }
    // catch (OverlappingFileLockException e)
    // {
    // return true;
    // }
    // }
    // catch (Exception e)
    // {
    // }
    // return false;
    // }

    public BufferedInputStream stream()
    {
        return inputStream();
    }

    public BufferedInputStream inputStream()
    {
        try
        {
            return new BufferedInputStream(new FileInputStream(this));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public InputStreamReader reader()
    {
        try
        {
            return new InputStreamReader(new BufferedInputStream(new FileInputStream(this)), "UTF-8");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static File3 TempFile(String filename)
    {
        File3 file = null;
        try
        {
            file = new File3(TEMP_DIR + filename);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return file;
    }

    public static File3 RandomTempFile(String extension)
    {
        return RandomTempFile(Base.x32.random16(), extension);
    }

    public static File3 RandomTempFile(String filename, String extension)
    {
        File3 tmpFile = null;
        try
        {
            tmpFile = new File3(File.createTempFile(File3.Filename(filename, true), extension));
            tmpFile.deleteOnExit();
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return tmpFile;
    }

    public static File3 TempFile(String filename, InputStream input, boolean deleteOnExit)
    {
        File3 tmpFile = null;
        try
        {
            tmpFile = TempFile(filename);
            if (deleteOnExit)
                tmpFile.deleteOnExit();
            IO.Transfer(input, new FileOutputStream(tmpFile));
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return tmpFile;
    }

    // private synchronized void swapToDisk()
    // {
    // if (this.swapToDisk)
    // {
    // String name = System.currentTimeMillis() + "_";
    // try
    // {
    //
    // this.swapFile = File3.createTempFile(name);
    // BufferedOutputStream os = new BufferedOutputStream(new
    // FileOutputStream(this.swapFile));
    // os.write(stream, 0, stream.length);
    // os.flush();
    // os.close();
    // this.stream = null;
    // }
    // catch (IOException e)
    // {
    // e.printStackTrace();
    // }
    // }
    // }

    public static String Guil(String path)
    {
        return path.replace("'", "\"");
    }

    public String path(String sep)
    {
        return path().replace("/", sep);
    }

    public String path()
    {
        return path(false);
    }

    public File3 refile(String oldString, String newString)
    {
        if (oldString == null || newString == null)
            return this;
        else
            return File3.Get(path().replace(oldString, newString));
    }

    public String path(boolean forceDirectory)
    {
        String path = normalize(this.getAbsolutePath());
        if ((forceDirectory || this.isDirectory()) && !path.endsWith("/"))
            path = path + "/";
        return path;
    }

    public static String Path(String... folders)
    {
        String path = "";
        if (folders != null)
            for (int i = 0; i < folders.length; i++)
            {
                String t = folders[i].replace("\\", "/");
                if (t.trim().isEmpty())
                    continue;
                if (i > 0 && t.startsWith("/"))
                    t = t.substring(1);
                path += t.endsWith("/") || (i == folders.length - 1 && t.indexOf(".") > -1) ? t : t + "/";
            }
        return path;
    }

    public static String upDir(String path, int up)
    {
        path = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
        while (up-- > 0)
        {
            int i = path.lastIndexOf("/");
            if (i > 0)
                path = path.substring(0, i);
            else
                break;
        }
        return path.endsWith("/") ? path : path + "/";
    }

    public static String normalize(String path)
    {
        // first \\ because char sequence escape, \\\\ when regex because char and
        // regex escape (used with replaceAll)
        return path == null ? null : path.trim().replace("\\", "/");
    }

    public static String normalizeDir(String path)
    {
        return (path = normalize(path)) == null ? null : path.endsWith("/") ? path : path + "/";
    }

    public static String subpath(String path)
    {
        path = normalize(path);
        return path.startsWith(SLASH) ? path.substring(1) : path;
    }

    public static String directorize(String path)
    {
        path = normalize(path);
        if (!new File3(path).isDirectory())
            path = path.substring(0, path.lastIndexOf("/") + 1);
        return path.endsWith("/") ? path : path + "/";
    }

    public static String directory(String path)
    {
        path = normalize(path);
        int index = path.lastIndexOf("/");
        return index < 0 ? "" : path.substring(0, index + 1);
    }

    public static String Filename(String path)
    {
        if (path == null)
            return null;
        path = normalize(path);
        int index = path.lastIndexOf("/");
        return index < 0 ? path : path.substring(index + 1);
    }

    public static String Filename(String path, boolean removeExtension)
    {
        if (path == null)
            return null;
        String name = Filename(path);
        if (removeExtension)
        {
            int i = name.lastIndexOf(".");
            name = i > 0 ? name.substring(0, i) : name;
        }
        return name;
    }

    public static String Filename(String path, String ext)
    {
        if (path == null)
            return null;
        String name = Filename(path);
        if (!name.endsWith(ext.startsWith(".") ? ext : (ext = "." + ext)))
        {
            int i = name.lastIndexOf(".");
            name = i > 0 ? name.substring(0, i) : name;
            name += ext;
        }
        return name;
    }

    public String root()
    {
        return root(this.path());
    }

    public String root(String path)
    {
        if (path == null)
            return null;
        path = normalize(path);
        while (path.startsWith("/"))
            path = path.substring(1);
        int index = path.indexOf("/");
        return index < 0 ? path : path.substring(0, index + 1);
    }

    public File3 postfix(String postfix)
    {
        return new File3(postfix(this.path(), postfix));
    }

    public static String postfix(String path, String postfix)
    {
        int dot = path.lastIndexOf(".");
        return dot > 0 ? path.substring(0, dot) + postfix + path.substring(dot) : path + postfix;
    }

    public File3 prefix(String prefix)
    {
        return Str.IsVoid(prefix) ? this : new File3(prefix(this.path(), prefix));
    }

    public static String prefix(String path, String prefix)
    {
        boolean ends = path.endsWith("/");
        if (ends)
            path = path.substring(0, path.length() - 1);
        int slash = path.lastIndexOf("/");
        path = slash > -1 ? path.substring(0, slash + 1) + prefix + path.substring(slash + 1) : prefix + path;
        return ends ? path + "/" : path;
    }

    public static String CleanSymbols(String path, String replace, char... keeps)
    {
        int dot = path.lastIndexOf(".");
        if (dot < 0)
            dot = path.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.length(); i++)
        {
            char c = path.charAt(i);
            boolean keep = false;
            for (char k : keeps)
                if (c == k)
                    keep = true;
            sb.append(i < dot && !(keep || c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z') ? replace : c);
        }
        return sb.toString();
    }

    public static boolean need(String path)
    {
        File3 file = new File3(path);
        if (file.exists())
            return true;
        else
            file.mkdirs();
        return false;
    }

    public static boolean Exists(File3 file)
    {
        return file != null && file.exists();
    }

    public static boolean Exists(String path)
    {
        return path == null || path.isEmpty() ? false : (new File3(path)).exists();
    }

    public static String Existing(String... paths)
    {
        for (String path : paths)
            if (Exists(path))
                return path;
        return "";
    }

    public static String Existing(int def, String... paths)
    {
        for (String path : paths)
            if (Exists(path))
                return path;
        return paths[def];
    }

    public File3 moveToDir(String path)
    {
        return moveToDir(new File3(path));
    }

    public File3 moveToDir(File3 file)
    {
        String path = file.directoryPath();
        if (path.endsWith("/") && !file.exists())
            file.needDirs(true);
        return moveTo(file.directoryPath() + this.name());
    }

    public File3 moveTo(String path)
    {
        return this.moveTo(new File3(path));
    }

    public File3 moveTo(File3 dest)
    {
        Log.debug(this, ".moveTo - dst=" + dest.path() + ", exists()=" + this.exists());
        if (!this.exists())
            return null;
        if (this.root().equals(dest.root()))
            if (this.renameTo(dest))
                return dest;
            else
            {
                try
                {
                    Path path = Files.copy(this.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    if (path != null)
                        return dest;
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        return this;
    }

    public File3 repath(File3 oldPath, File3 newPath)
    {
        return repath(oldPath.path(), newPath.path());
    }

    public File3 repath(String oldPath, String newPath)
    {
        if (oldPath == null || newPath == null)
            return this;
        String path = this.path();
        String repath = path.replace(oldPath, newPath);
        return path.equals(repath) ? this : File3.Get(repath);
    }

    public static File3 Rename(String oldPath, String newPath)
    {
        // Log.debug(File3.class, ".rename - "+oldPath+" to "+newPath);
        return new File3(oldPath).rename(new File3(newPath));
    }

    public File3 rename(File3 file)
    {
        if (!exists())
            return null;
        if (file.exists())
            file.delete();
        return super.renameTo(file) ? file : null;
    }

    public boolean renameToExt(String ext)
    {
        return this.renameTo(File3.Extense(this.path(), ext));
    }

    public boolean renameTo(String path)
    {
        return this.renameTo(File3.Get(path));
    }

    public boolean renameTo(String oldString, String newString)
    {
        if (oldString != null && newString != null && !oldString.equals(newString))
            return renameTo(path().replace(oldString, newString));
        return true;
    }

    public long lengthKB()
    {
        return this.length() / 1024;
    }

    public long lengthMB()
    {
        return this.length() / (1024 * 1024);
    }

    public String nameOrPath(boolean filename)
    {
        return filename ? name() : path();
    }

    public String name()
    {
        return super.getName();
    }

    public boolean isName(String name)
    {
        return super.getName().equals(name);
    }

    public boolean isDirectoryName(String name)
    {
        return this.isDirectory() && super.getName().equals(name);
    }

    public String name(boolean removeExtension)
    {
        return Filename(this.getName(), removeExtension);
    }

    public String folderName()
    {
        String folder = (isDirectory() ? this : parent()).path();
        if (folder.endsWith("/"))
            folder = folder.substring(0, folder.length() - 1);
        folder = folder.substring(folder.lastIndexOf("/") + 1);
        return folder.endsWith("/") ? folder.substring(0, folder.length() - 1) : folder;
    }

    public File3 directory()
    {
        return this.isDirectory() ? this : parent();
    }

    public String dir()
    {
        return directoryPath();
    }

    public String directoryPath()
    {
        return directory().path();
    }

    public void print()
    {
        System.out.println(this.path());
    }

    public File3 parent()
    {
        return new File3(super.getParentFile());
    }

    public boolean isExt(String... extensions)
    {
        return this.isExtension(extensions);
    }

    public boolean isExtension(String... extensions)
    {
        return isExtension(false, extensions);
    }

    public boolean isExtension(boolean caseSensitive, String... extensions)
    {
        String extension = this.extension();
        if (extensions == null || extensions.length == 0)
            return true;
        for (String ext : extensions)
            if (caseSensitive)
            {
                if (extension.equals(ext.startsWith(".") ? ext.substring(1) : ext))
                    return true;
            } else
            {
                if (extension.equalsIgnoreCase(ext.startsWith(".") ? ext.substring(1) : ext))
                    return true;
            }
        return false;
    }

    public boolean nameStarts(String... starts)
    {
        String name = this.name();
        for (String start : starts)
            if (name.startsWith(start))
                return true;
        return false;
    }

    public boolean nameEnds(String... ends)
    {
        String name = this.name();
        for (String end : ends)
            if (name.endsWith(end))
                return true;
        return false;
    }

    public boolean nameContains(String... tokens)
    {
        String name = this.name();
        for (String tk : tokens)
            if (name.contains(tk))
                return true;
        return false;
    }

    public boolean pathContains(String... tokens)
    {
        String path = this.path();
        for (String tk : tokens)
            if (path.contains(tk))
                return true;
        return false;
    }

    public static File3 userWork()
    {
        return new File3(USER_WORK);
    }

    public static File3 userHome()
    {
        return new File3(USER_HOME);
    }

    public static File3 userDesktop()
    {
        File3 file = new File3(USER_HOME + "Desktop" + SLASH);
        if (!file.exists())
            file = new File3(USER_HOME + "Bureau" + SLASH);
        if (file.exists() && file.isDirectory())
            return file;
        else
            return userHome();
    }

    public static String slash()
    {
        return SLASH;
    }

    public static File3 home(String path)
    {
        return new File3(USER_HOME + subpath(path));
    }

    public static File3 work(String path)
    {
        return new File3(USER_WORK + subpath(path));
    }

    public static File3 desktop(String path)
    {
        return new File3(USER_HOME + "Desktop/" + (path == null || path.isEmpty() ? "" : subpath(path)));
    }

    public static File3 Desk()
    {
        return desktop("");
    }

    public static File3 Desk(String path)
    {
        return desktop(path);
    }

    public String extension()
    {
        return extension(this);
    }

    public String ext()
    {
        return extension(this);
    }

    public String extension(boolean removeDot)
    {
        return extension(this, removeDot);
    }

    public String dotExt()
    {
        return extension(this, false);
    }

    public String dotExtLow()
    {
        return dotExt().toLowerCase();
    }

    public static String Ext(File file)
    {
        return extension(file, true);
    }

    public static String extension(File file)
    {
        return extension(file, true);
    }

    public static String Ext(File file, boolean removeDot)
    {
        return file.isDirectory() ? "" : extension(file.getName(), removeDot);
    }

    public static String extension(File file, boolean removeDot)
    {
        return file.isDirectory() ? "" : extension(file.getName(), removeDot);
    }

    public static String Ext(String path)
    {
        return extension(path, true);
    }

    public static String extension(String path)
    {
        return extension(path, true);
    }

    public static String Ext(String path, boolean removeDot)
    {
        path = normalize(path);
        int i = path.lastIndexOf(".");
        int j = path.lastIndexOf("/");
        return i > 0 && i > j + 1 ? path.substring(removeDot ? i + 1 : i) : "";
    }

    public static String extension(String path, boolean removeDot)
    {
        path = normalize(path);
        int i = path.lastIndexOf(".");
        int j = path.lastIndexOf("/");
        return i > 0 && i > j + 1 ? path.substring(removeDot ? i + 1 : i) : "";
    }

    public static boolean HasExt(String path)
    {
        String ext = extension(path);
        return ext != null && !ext.isEmpty();
    }


    public static boolean HasExtension(String path)
    {
        String ext = extension(path);
        return ext != null && !ext.isEmpty();
    }

    public static boolean HasExtension(String path, String... extensions)
    {
        String x = extension(path, false);
        for (String ext : extensions)
            if (x.equals(ext.startsWith(".") ? ext : "." + ext) || x.isEmpty() && ext.isEmpty() || ext.equals("*") || ext.equals(".*"))
                return true;
        return false;
    }

    public static String Extense(String path, String ext)
    {
        if (ext == null || ext.trim().isEmpty())
        {
            ext = "";
        } else
        {
            if (!ext.startsWith("."))
                ext = "." + ext;
            if (path.endsWith(ext))
                return path;
        }
        int i = path.lastIndexOf(".");
        return (i > 1 && i > path.lastIndexOf("\\") && i > path.lastIndexOf("/")) ? path.substring(0, i) + ext : path + ext;
    }

    public static File3 Extense(File file, String ext)
    {
        return file == null ? null : new File3(file).extense(ext);
    }

    public File3 extense(String ext)
    {
        String path = getAbsolutePath();
        return path.endsWith(ext.startsWith(".") ? ext : "." + ext) ? this : new File3(Extense(path, ext));
    }

    public String[] readTokens()
    {
        StringList tokens = new StringList();
        for (String line : this.readLines())
            if (line.length() > 0)
                for (String token : line.split("\\s+"))
                    tokens.add(token);

        return tokens.toArray(new String[0]);
    }

    public String[] readLines()
    {
        return readLines(true);
    }

    public String[] readLines(boolean utf8)
    {
        return readLinesAsList(utf8).toArray(new String[0]);
    }

    public StringList readLinesAsList(boolean utf8)
    {
        return readLinesAsList(utf8, null, false);
    }

    public StringList readLinesAsList(boolean utf8, StringList contents, boolean skipFirstLine)
    {
        if (contents == null)
            contents = new StringList();
        BufferedReader input = null;

        try
        {
            input = new BufferedReader(new InputStreamReader(new FileInputStream(this), utf8 ? IO.UTF8 : "windows-1252"));

            String line;
            while ((line = input.readLine()) != null)
            {
                if (skipFirstLine)
                    skipFirstLine = false;
                else
                    contents.add(line);
            }
        } catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
        } catch (IOException ex)
        {
            ex.printStackTrace();
        } finally
        {
            try
            {
                if (input != null)
                    input.close();
            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
        return contents;
    }

    public String readText()
    {
        return readText(null);
    }

    public String readText(Charset encoding)
    {
        String text = null;
        try
        {
            byte[] encoded = Files.readAllBytes(toPath());
            if (encoded != null)
                text = new String(encoded, encoding == null ? StandardCharsets.UTF_8 : encoding);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return text;
    }

    public byte[] bytes()
    {
        return byteStream();
    }

    public byte[] byteStream()
    {
        byte[] bytes;
        try
        {
            InputStream stream = new FileInputStream(this);
            bytes = new byte[(int) this.length()];

            int offset = 0;
            int bytesRead;

            while (offset < bytes.length && (bytesRead = stream.read(bytes, offset, bytes.length - offset)) >= 0)
                offset += bytesRead;

            stream.close();

            if (offset < bytes.length)
                return null;
        } catch (Exception e)
        {
            return null;
        }

        return bytes;
    }

    public Ints find(String data)
    {
        try
        {
            return find(data.getBytes("ISO-8859-1"));
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return new Ints();
        }
    }

    public Ints find(byte[] data)
    {
        return find(Bytes.toInts(data));
    }

    public Ints find(int... data)
    {
        Ints indexes = new Ints();
        try
        {
            InputStream stream = new BufferedInputStream(new FileInputStream(this));
            int index = 0;
            int b;
            int i = 0;
            while ((b = stream.read()) >= 0)
            {
                if (b == data[i])
                {
                    if (++i >= data.length)
                    {
                        indexes.add(index);
                        i = 0;
                    }
                } else if (i > 0)
                    i = 0;
                index++;
            }
            indexes.add(index);
            stream.close();
        } catch (Exception e)
        {
            return indexes;
        }

        return indexes;
    }

    @Override
    public boolean delete()
    {
        return exists() ? super.delete() : true;
    }

    public boolean deleteDirectory()
    {
        if (this.isDirectory())
            return cleanDirectory(this).delete();
        return false;
    }

    public boolean cleanDirectory()
    {
        if (this.isDirectory())
        {
            cleanDirectory(this);
            return true;
        }
        return false;
    }

    public File3[] children()
    {
        return listFiles();
    }

    @Override
    public File3[] listFiles()
    {
        if (!isDirectory())
            return new File3[0];
        return Cast(super.listFiles());
    }

    public File3[] listFiles(boolean parseSubdirectories)
    {
        return parseSubdirectories ? listAllFiles(this, new Files3()).toArray(new File3[0]) : listFiles();
    }

    public static Files3 listAllFiles(File folder, Files3 files)
    {
        if (files == null)
            files = new Files3();
        for (File file : folder.listFiles())
        {
            files.add(Cast(file));
            if (file.isDirectory())
                listAllFiles(file, files);
        }
        return files;
    }

    public Files3 folders()
    {
        Files3 folders = new Files3();
        for (File file : super.listFiles())
            if (file.isDirectory())
                folders.add(file);
        return folders;
    }

    public Files3 asFiles()
    {
        return new Files3(this);
    }

    public Files3 files(boolean sort, String... exts)
    {
        Files3 files = this.files(exts);
        if (sort)
            Collections.sort(files);
        return files;
    }

    public Files3 files(String... exts)
    {
        return files(-1, exts);
    }

    public Files3 files(int level, String... exts)
    {
        return files(this, null, level, dotExts(exts));
    }

    public Files3 files(FileFilter filter, int level, String... exts)
    {
        return files(this, null, level, dotExts(exts), filter);
    }

    public Files3 files(FileFilter filter, String... exts)
    {
        return files(filter, -1, exts);
    }

    public Files3 children(String... exts)
    {
        return files(0, exts);
    }

    public static StringSet dotExts(String... exts)
    {
        StringSet dotExts = new StringSet();
        if (exts != null)
            for (String ext : exts)
                if (ext != null)
                    dotExts.add((ext.startsWith(".") || ext.isEmpty() ? ext : "." + ext).toLowerCase());
        return dotExts;
    }

    public static Files3 files(File folder, Files3 files, int level, StringSet dotExts)
    {
        return files(folder, files, level, dotExts, null);
    }

    public static Files3 files(File folder, Files3 files, int level, StringSet dotExts, FileFilter filter)
    {
        if (files == null)
            files = new Files3();
        for (File file : (filter == null ? folder.listFiles() : folder.listFiles(filter)))
        {
            if (file.isDirectory())
            {
                // "" or "." mean directory file
                if (dotExts.has(".") || dotExts.has(""))
                    files.add(Cast(file));
                // level<0 is infinity deep, else level-1 at each directory
                if (level != 0)
                    files(file, files, level - 1, dotExts, filter);
            } else if (dotExts.isEmpty() || dotExts.has(File3.extension(file, false).toLowerCase()))
            {
                files.add(Cast(file));
            }
        }
        return files;
    }

    public File3[] listFiles(String... extensions)
    {
        if (!this.isDirectory())
            return new File3[0];
        return Cast(super.listFiles(new FileFilter3(false, extensions)));
    }

    @Override
    public File3[] listFiles(FileFilter filter)
    {
        if (!this.isDirectory())
            return new File3[0];
        return Cast(super.listFiles(filter));
    }

    public boolean hasFileEnds(String... fileEnds)
    {
        return hasFileEnds(true, fileEnds);
    }

    public boolean hasFileEnds(final boolean caseSensitive, final String... fileEnds)
    {
        if (this.isDirectory())
        {
            if (fileEnds == null || fileEnds.length == 0)
                return true;

            Visitor<File3> visitor = new Visitor<File3>()
            {
                @Override
                public boolean visit(File3 o)
                {

                    for (String end : fileEnds)
                        if (caseSensitive)
                        {
                            if (o.getName().endsWith(end))
                                return true;
                        } else if (o.getName().toLowerCase().endsWith(end.toLowerCase()))
                            return true;

                    return false;
                }
            };

            return visitTree(visitor);
        }

        return false;
    }

    public boolean visitTree(Visitor<File3> visitor)
    {
        return visit(this, visitor, -1, null);
    }

    public boolean visitTree(Visitor<File3> visitor, int level, String... exts)
    {
        return visit(this, visitor, level, dotExts(exts));
    }

    public static boolean visit(File dir, Visitor<File3> visitor, int level, StringSet dotExts)
    {
        // returns true to stop the visiting process
        if (dir.isDirectory())
            for (File child : dir.listFiles())
            {
                if (child.isDirectory())
                {
                    // . means directory file
                    if ((dotExts == null || dotExts.isEmpty() || dotExts.has(".")) && visitor.visit(new File3(child)))
                        return true;
                    // level<0 is infinity deep, else level-1 at each directory
                    if (level != 0 && visit(child, visitor, level - 1, dotExts))
                        return true;
                } else if (dotExts == null || dotExts.isEmpty() || dotExts.has(File3.extension(child, false).toLowerCase()))
                {
                    if (visitor.visit(new File3(child)))
                        return true;
                }
            }
        return false;
    }

    public File3 seekTree(Seeker<File3, File3> seeker)
    {
        return seek(this, seeker, -1, null);
    }

    public File3 seekTree(Seeker<File3, File3> seeker, int level, String... exts)
    {
        return seek(this, seeker, level, dotExts(exts));
    }

    public static File3 seek(File dir, Seeker<File3, File3> seeker, int level, StringSet dotExts)
    {
        // returns true to stop the visiting process
        if (dir.isDirectory())
            for (File child : dir.listFiles())
            {
                if (child.isDirectory())
                {
                    // "" or "." means directory file
                    if ((dotExts == null || dotExts.isEmpty() || dotExts.has(".") || dotExts.has("")))
                    {
                        File3 found = seeker.seek(new File3(child));
                        if (found != null)
                            return found;
                    }
                    // level<0 is infinity deep, else level-1 at each directory
                    if (level != 0)
                    {
                        File3 found = seek(child, seeker, level - 1, dotExts);
                        if (found != null)
                            return found;
                    }
                } else if (dotExts == null || dotExts.isEmpty() || dotExts.has(File3.extension(child, false).toLowerCase()))
                {
                    File3 found = seeker.seek(new File3(child));
                    if (found != null)
                        return found;
                }
            }
        return null;
    }

    private static File cleanDirectory(File directory)
    {
        if (directory.isDirectory())
            for (File file : directory.listFiles())
            {
                if (file.isDirectory())
                    cleanDirectory(file);
                file.delete();
            }
        return directory;
    }

    public static String RePath(String root, String file, boolean isDir)
    {
        root = File3.normalizeDir(root);
        // file is relative or absolute

        if (Str.IsVoid(file) || (file = File3.normalize(file)).equals(".") || file.equals("./"))
            return root;

        if (file.indexOf(":") == 1)
            file = file + "";
        else if (file.startsWith("./"))
            file = root + file.substring(2);
        else if (file.startsWith("../"))
        {
            int up = 0;
            while (file.startsWith("../"))
            {
                up++;
                file = file.substring(3);
            }
            file = File3.upDir(root, up) + file;
        }
        return file.endsWith("/") ? file : isDir ? file + "/" : file;
    }

    public static boolean MoveFolderOnSameDisk(String srcPath, String destPath)
    {
        try
        {
            Files.move(new File(srcPath).toPath(), new File(destPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static void DeleteFolder(String path)
    {
        DeleteFolder(new File(path).toPath());
    }

    public static void DeleteFolder(Path path)
    {
        try
        {
            if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS))
                try (DirectoryStream<Path> entries = Files.newDirectoryStream(path))
                {
                    for (Path entry : entries)
                        DeleteFolder(entry);
                }
            Files.delete(path);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public File3 mkdirs(boolean doMake)
    {
        if (doMake)
            try
            {
                (new File3(File3.directory(path()))).mkdirs();
            } catch (Exception e)
            {
                e.printStackTrace();
                Log.warn(this, ".write - " + path() + ": " + e.getMessage());
            }
        return this;
    }

    public static File3 mkdirs(String path)
    {
        try
        {
            File3 dir = new File3(File3.directory(path));
            dir.mkdirs();
            return dir;
        } catch (Exception e)
        {
            e.printStackTrace();
            Log.warn(File3.class, ".write - " + path + ": " + e.getMessage());
        }
        return null;
    }

    public static File3 Write(String filePath, byte[] data)
    {
        File3 file = new File3(filePath);
        file.write(data, true);
        return file;
    }

    public boolean write(byte[] data, boolean mkdirs)
    {
        return mkdirs(mkdirs).write(data);
    }

    public boolean write(byte[] data)
    {
        return IO.WriteBytes(this, data);
    }

    public boolean write(String text)
    {
        return IO.WriteText(this, text);
    }

    public static File3 Write(String filePath, InputStream stream, boolean doClose)
    {
        File3 file = new File3(filePath);
        file.write(stream, true, doClose);
        return file;
    }

    public boolean write(InputStream stream, boolean mkdirs, boolean doClose)
    {
        return mkdirs(mkdirs).write(stream, doClose);
    }

    public boolean write(InputStream stream, boolean doClose)
    {
        try
        {
            if (stream != null && !(stream instanceof BufferedInputStream))
                stream = new BufferedInputStream(stream);

            byte[] buffer = new byte[4096];
            int bytesRead;
            FileOutputStream fos = new FileOutputStream(this);
            while ((bytesRead = stream.read(buffer)) != -1)
                fos.write(buffer, 0, bytesRead);
            fos.close();
            IO.Close(doClose, stream);
            return true;
        } catch (Exception e)
        {
            IO.Close(doClose, stream);
            return false;
        }
    }

    public boolean download(String url)
    {
        return Download(url, this);
    }

    public String relativeTo(File3 root)
    {
        String dir = root.directory().path();
        String path = this.path();
        return path.startsWith(dir) ? path.substring(dir.length() + 1, path.length()) : path;
    }

    public File3 zip()
    {
        boolean isDir = this.isDirectory();
        File3 zipFile = isDir ? new File3(getParent(), getName() + ".zip") : extense(".zip");
        try
        {
            Zipper zip = new Zipper(zipFile);
            zip.zipFile(this);
            zip.dispose();
        } catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
        }
        return zipFile;
    }

    public Path copyTo(File3 file)
    {
        try
        {
            return Files.copy(this.toPath(), file.toPath());
        } catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public File3 copy()
    {
        return new File3(this);
    }

    public File3[] asArray()
    {
        return new File3[]{this};
    }

    @Override
    public Iterator<File3> iterator()
    {
        return new List3<File3>(listFiles()).iterator();
    }

    @Override
    public String toString()
    {
        return this.getPath();
    }

    public String uri()
    {
        return this.toURI().toString();
    }

    public String url()
    {
        try
        {
            return this.toURI().toURL().toString();
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        return toString();
    }

    public File3 previous()
    {
        return next(false, false);
    }

    public File3 next()
    {
        return next(true, false);
    }

    public File3 next(boolean forward, boolean checkSiblingFolders)
    {
        File3 next = directory().files(extension()).sort(forward).next(this);
        if (next.equals(this) && checkSiblingFolders)
            next = directory().parent().folders().sort(forward).next(directory()).files(extension()).sort(forward).first();
        return next;
    }

    public static File3[] Array(String... paths)
    {
        File3[] files = new File3[paths.length];
        for (int i = 0; i < paths.length; i++)
            files[i] = File3.Get(paths[i]);
        return files;
    }

    public static File3[] Array(File3... files)
    {
        return files;
    }

    public static File3 Get(String dir, String path)
    {
        return new File3(dir, path);
    }

    public static File3 Sparty(String path)
    {
        return new File3("//SPARTACUS/", path);
    }

    public static File3 Get(String path)
    {
        return path == null ? null : new File3(path);
    }

    public static File3 Get(File file, String path)
    {
        return path == null ? null : new File3(file, path);
    }

    public static File3 Get(Path path)
    {
        return new File3(path.toFile());
    }

    public static File3 Wrap(File file)
    {
        return file == null ? null : (file instanceof File3 ? (File3) file : new File3(file));
    }

    public static boolean Download(String url, File3 file)
    {
        if (file != null)
            try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream()))
            {
                if (!file.write(in, true))
                    file = null;
            } catch (IOException e)
            {
                e.printStackTrace();
                file = null;
            }
        return file != null;
    }

    public static File3 NeedDirs(File file)
    {
        return File3.Wrap(file).needDirs();
    }

    public static File3[] Concat(File3[]... arrays)
    {
        List3<File3> files = new List3<>();
        for (File3[] array : arrays)
            for (File3 file : array)
                files.add(file);
        return files.toArray(new File3[0]);
    }

    public static File3[] Parents(File3[] files)
    {
        StringSet set = new StringSet();
        List3<File3> list = new List3<>();
        for (File3 file : files)
            if (file.exists() && !file.isDirectory())
            {
                File3 parent = file.parent();
                if (set.hasnt(parent.path()))
                {
                    set.add(parent.path());
                    list.add(parent);
                }
            }
        return list.toArray(new File3[0]);
    }

    public static File3 FirstExisting(File3... files)
    {
        for (File3 file : files)
            if (file != null && file.exists())
                return file;
        return null;
    }

    public static String DotStart(String data)
    {
        return data != null && !data.startsWith(".") ? "." + data : data;
    }

    public static String UnslashStart(String data)
    {
        return data != null && data.startsWith("/") ? data.substring(1) : data;
    }

    public static String UnslashEnd(String data)
    {
        return data != null && data.endsWith("/") ? data.substring(0, data.length() - 1) : data;
    }

    public static String SlashStart(String data)
    {
        return data != null && !data.startsWith("/") ? "/" + data : data;
    }

    public static String SlashEnd(String data)
    {
        return data != null && !data.endsWith("/") ? data + "/" : data;
    }

    public static String SlashOnlyStart(String data)
    {
        return SlashStart(UnslashEnd(data));
    }

    public static String SlashOnlyEnd(String data)
    {
        return SlashEnd(UnslashStart(data));
    }

    public static boolean HasFile(File3[] files)
    {
        return !(files == null || files.length == 0 || files.length == 1 && files[0] == null);
    }

    public static Comparator<File3> SizeComparator(boolean asc)
    {

        return (f0, f1) -> Math3.Sign(f0.length() - f1.length(), !asc);
    }

    public static Comparator<File3> FilenameComparator(boolean asc)
    {
        return (f0, f1) -> (asc ? f0.getName().compareTo(f1.getName()) : f1.getName().compareTo(f0.getName()));
    }

    public static File3[] Cast(File... files)
    {
        if (files == null)
            return new File3[0];
        File3[] files3 = new File3[files.length];
        for (int i = 0; i < files3.length; i++)
            files3[i] = new File3(files[i]);
        return files3;
    }

    public static void main(String... args)
    {
    }

}
