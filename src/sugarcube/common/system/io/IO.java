package sugarcube.common.system.io;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.StringList;
import sugarcube.common.ui.gui.FileChooser3;
import sugarcube.common.interfaces.Closable;
import sugarcube.common.system.process.Thread3;
import sugarcube.common.system.process.ThreadPool;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class IO
{
    public interface StringReadListener
    {
        void stringRead(String data);
    }

    private static ThreadPool WRITING_THREAD = null;

    public static final String UTF8 = "UTF-8";
    // 64KB to ensure stream reading efficiency
    public static final int MIN_BUFFER_SIZE = 65536;
    public static final String LINE_SEPARATOR = "\n";

    public static ThreadPool WritingThreadPool()
    {
        return WRITING_THREAD == null ? WRITING_THREAD = new ThreadPool(1) : WRITING_THREAD;
    }

    public static void WritingThread(Runnable runnable)
    {
        ThreadPool pool = WritingThreadPool();
        if (pool == null || pool.fedUp())
            Thread3.Run(() -> runnable.run());
        else
            pool.execute(() -> runnable.run());
    }

    public static boolean Unzip(String zip, String dir)
    {
        return Unzip(new File3(zip), new File3(dir));
    }

    public static boolean Unzip(File3 zip)
    {
        return Unzip(zip, new File3(zip.directory(), zip.name(true)));
    }

    public static boolean Unzip(File3 zip, File3 dir)
    {
        // Log.debug(IO.class, ".unzip - "+zip.path());
        byte[] buffer = new byte[4096];
        try
        {
            dir.mkdirs();
            ZipInputStream stream = new ZipInputStream(new FileInputStream(zip));
            ZipEntry entry = stream.getNextEntry();
            while (entry != null)
            {
                String filepath = entry.getName();
                File3 file = new File3(dir, filepath);
                // Log.debug(IO.class, ".unzip - " + file.path());

                new File3(file.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(file);
                int len;
                while ((len = stream.read(buffer)) > 0)
                {
                    fos.write(buffer, 0, len);
                }
                fos.close();

                stream.closeEntry();
                entry = stream.getNextEntry();
            }
            stream.closeEntry();
            stream.close();

        } catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void cleanDirectory(File directory, FileFilter filter)
    {
        for (File file : directory.listFiles())
            if (filter.accept(file))
            {
                if (file.isFile())
                    file.delete();
                else if (file.isDirectory())
                    deleteDirectory(file);
            } else if (file.isDirectory())
                cleanDirectory(directory, filter);
    }

    public static boolean deleteDirectory(File directory)
    {
        if (directory.isDirectory())
        {
            String[] children = directory.list();
            for (int i = 0; i < children.length; i++)
            {
                boolean success = deleteDirectory(new File(directory, children[i]));
                if (!success)
                    return false;
            }
        }
        return directory.delete();
    }

    public static boolean writeText(String text, OutputStream stream, boolean close)
    {
        boolean ok = true;
        try
        {
            Writer writer = new BufferedWriter(new OutputStreamWriter(stream, UTF8));
            writer.write(text);
            writer.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
            ok = false;
        }
        if (close)
            IO.Close(stream);
        return ok;
    }

    public static String[] ReadLines(InputStream stream)
    {
        try
        {
            return ReadLines(new InputStreamReader(stream, UTF8));
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return new String[0];
    }

    public static String[] ReadLines(File file)
    {
        return ReadLines(file, false);
    }

    public static String[] ReadLines(File file, boolean doTrim)
    {
        try
        {
            return ReadLines(new FileReader(file), doTrim);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return new String[0];
    }

    public static String[] ReadLines(Reader reader)
    {
        return ReadLines(reader, false);
    }

    public static String[] ReadLines(Reader reader, boolean doTrim)
    {
        StringList contents = new StringList();
        BufferedReader input = null;
        try
        {
            input = new BufferedReader(reader);
            String line;
            while ((line = input.readLine()) != null)
                if (!doTrim || !(line = line.trim()).isEmpty())
                    contents.add(line);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        } finally
        {
            IO.Close(input);
        }
        return contents.array();
    }

    public static void ReadLines(File file, StringReadListener listener)
    {
        BufferedReader input = null;
        try
        {
            input = new BufferedReader(new FileReader(file));
            String line;
            while ((line = input.readLine()) != null)
                listener.stringRead(line);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        } finally
        {
            IO.Close(input);
        }
    }

    public static String ReadText(String path)
    {
        return readText(new File3(path));
    }

    public static String ReadText(InputStream stream)
    {
        StringBuilder contents = new StringBuilder();
        BufferedReader input = null;
        try
        {
            input = new BufferedReader(new InputStreamReader(stream, UTF8));
            String line;
            while ((line = input.readLine()) != null)
            {
                contents.append(line);
                contents.append(LINE_SEPARATOR);
            }
        } catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
        } catch (IOException ex)
        {
            ex.printStackTrace();
        } finally
        {
            IO.Close(input);
        }
        return contents.toString();
    }

    public static String ReadString(InputStream is, int bufferSize, int minBufferSize, boolean doUnzip)
    {
        return ReadString(is, bufferSize, minBufferSize, doUnzip, true);
    }

    public static String ReadString(InputStream is, int bufferSize, int minBufferSize, boolean doUnzip, boolean doClose)
    {
        InputStreamReader reader = null;
        if (is != null)
        {
            try
            {
                int size = IO.BufferSize(is, bufferSize, minBufferSize);
                reader = new InputStreamReader(doUnzip ? new GZIPInputStream(is) : is, UTF8);
                StringBuilder data = new StringBuilder(size);
                char[] buffer = new char[size];
                while ((size = reader.read(buffer)) != -1)
                    data.append(buffer, 0, size);
                if (doClose)
                    IO.Close(reader);
                return data.toString();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            if (doClose)
                IO.Close(reader);
        }
        return "";
    }

    public static boolean hasText(File file, String text, int maxLines)
    {
        BufferedReader input = null;
        boolean contains = false;
        try
        {
            int i = 0;
            input = new BufferedReader(new FileReader(file));
            String line;
            while ((line = input.readLine()) != null && (maxLines <= 0 || i++ < maxLines))
                if (line.contains(text) && (contains = true))
                    break;
        } catch (Exception ex)
        {
            ex.printStackTrace();
        } finally
        {
            IO.Close(input);
        }
        return contains;
    }

    public static String readText(File file)
    {
        StringBuilder contents = new StringBuilder();
        BufferedReader input = null;

        try
        {
            input = new BufferedReader(new FileReader(file));
            String line;
            while ((line = input.readLine()) != null)
            {
                contents.append(line);
                contents.append(LINE_SEPARATOR);
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
        } finally
        {
            IO.Close(input);
        }
        return contents.toString();
    }

    public static File3 EnsureFileDirectories(File file)
    {
        return File3.Wrap(file).needDirs(false);
    }

    public static void WriteTextInThread(File file, String content)
    {
        if (content != null)
            IO.WritingThread(() -> IO.WriteText(file, content));
    }

    public static boolean WriteText(File file, String content)
    {
        return WriteText(file, content, true);
    }

    public static boolean WriteText(File file, String content, boolean addBom)
    {
        Writer output = null;
        boolean ok = true;
        try
        {
            // Log.debug(IO.class, ".writeText - " + file);
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            if (addBom)
                output.write('\ufeff');
            output.write(content);
            output.flush();
        } catch (IOException e)
        {
            ok = false;
            e.printStackTrace();
        } finally
        {
            IO.Close(output);
        }
        return ok;
    }

    public static void WriteBytesInThread(File file, byte[] bytes)
    {
        IO.WritingThread(() -> IO.WriteBytes(file, bytes));
    }

    public static boolean WriteBytes(File file, byte[] bytes)
    {
        OutputStream stream = null;
        try
        {
            EnsureFileDirectories(file);
            stream = new BufferedOutputStream(new FileOutputStream(file));
            stream.write(bytes);
            stream.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
            return false;
        } finally
        {
            IO.Close(stream);
        }
        return true;
    }

    public static File3 WriteFileFromURL(String url, File3 folder)
    {
        try
        {
            url = url == null ? "" : url.trim();
            if (url.endsWith("/"))
                url.substring(0, url.length() - 1);
            int i = url.lastIndexOf('/');
            String filename = url.substring(i + 1);
            folder = folder.get(filename);
            Log.debug(IO.class, ".WriteFileFromURL - url=" + url + ", folder=" + folder);
            IO.WriteStream(folder, new URL(url).openStream());
        } catch (Exception e)
        {
            e.printStackTrace();
            Log.debug(IO.class, ".WriteFileFromURL - exception: url=" + url + ", fodler=" + folder + ", message=" + e.getMessage());
            return null;
        }

        return folder;
    }

    public static boolean WriteStream(File file, InputStream inputStream)
    {
        OutputStream stream = null;
        try
        {
            EnsureFileDirectories(file);
            stream = new BufferedOutputStream(new FileOutputStream(file));
            Transfer(inputStream, stream);
            stream.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
            Log.debug(IO.class, ".WriteStream - exception: " + e.getMessage());
            return false;
        } finally
        {
            IO.Close(stream);
        }
        return true;
    }

    public static byte[] ReadBytes(String path)
    {
        return ReadBytes(File3.Get(path));
    }

    public static byte[] ReadBytes(File file)
    {
        byte[] bytes;
        try
        {
            InputStream stream = new FileInputStream(file);
            bytes = new byte[(int) file.length()];
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

    public static int BufferSize(InputStream stream)
    {
        return BufferSize(stream, -1);
    }

    public static int BufferSize(InputStream stream, int size)
    {
        if (size <= 0)
            try
            {
                size = stream.available();
            } catch (Exception ex)
            {
            }
        return BufferSize(stream, size, IO.MIN_BUFFER_SIZE);
    }

    public static int BufferSize(InputStream stream, int size, int minSize)
    {
        if (size <= 0)
            try
            {
                size = stream.available();
            } catch (Exception ex)
            {
            }
        return Math.max(size, minSize);
    }

    public static byte[] ByteBuffer(InputStream stream, int size)
    {
        return new byte[BufferSize(stream, size)];
    }

    public static byte[] ReadBytes(InputStream stream)
    {
        return ReadBytes(stream, -1);
    }

    public static byte[] ReadBytes(InputStream stream, int size)
    {
        if (stream != null)
        {
            byte[] bytes;
            size = BufferSize(stream, size);
            InputStream is = stream instanceof BufferedInputStream ? stream : new BufferedInputStream(stream);
            ByteArrayOutputStream os = new ByteArrayOutputStream(size);
            try
            {
                byte[] buffer = new byte[size];
                while ((size = is.read(buffer)) != -1)
                    os.write(buffer, 0, size);
                bytes = os.toByteArray();
                is.close();
                os.close();
                return bytes;
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean Transfer(InputStream in, OutputStream out)
    {
        int size = BufferSize(in, -1);
        in = in instanceof BufferedInputStream ? in : new BufferedInputStream(in);
        out = out instanceof BufferedOutputStream ? out : new BufferedOutputStream(out);
        try
        {
            byte[] buffer = new byte[size];
            while ((size = in.read(buffer)) != -1)
                out.write(buffer, 0, size);
            in.close();
            out.close();
            return true;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static synchronized boolean storeXML(Serializable o, File f)
    {
        try
        {
            XMLEncoder xenc = new XMLEncoder(new FileOutputStream(f));
            xenc.writeObject(o);
            xenc.close();
        } catch (IOException e)
        {
            Log.warn(IO.class, ".storeXML failed : " + e.getMessage());
            return false;
        }
        return true;
    }

    public static synchronized Object loadXML(File f)
    {
        XMLDecoder xdec;
        try
        {
            xdec = new XMLDecoder(new FileInputStream(f));
        } catch (IOException e)
        {
            Log.warn(IO.class, ".loadXML failed : " + e.getMessage());
            return null;
        }
        return xdec.readObject();
    }

    public static synchronized boolean store(Serializable o, File f)
    {
        try
        {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
            out.writeObject(o);
            out.close();
        } catch (IOException e)
        {
            Log.warn(IO.class, ".store failed : " + e.getMessage());
            return false;
        }
        return true;
    }

    public static synchronized Object load(File f)
    {
        try
        {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
            Object object = in.readObject();
            in.close();
            return object;
        } catch (IOException e)
        {
            Log.warn(IO.class, ".load failed : " + e.getMessage());
            return null;
        } catch (ClassNotFoundException e)
        {
            Log.warn(IO.class, ".load failed : " + e.getMessage());
            return null;
        }
    }

    public static void writeFile(String text)
    {
        FileChooser3 chooser = new FileChooser3();
        if (chooser.acceptSaveDialog())
            new WriteTextThread(text, chooser.file());
    }

    public static void writeFile(BufferedImage image)
    {
        FileChooser3 chooser = new FileChooser3();
        if (chooser.acceptSaveDialog())
            new WriteImageThread(image, chooser.file());
    }

    private static class WriteTextThread extends Thread
    {
        private String text;
        private File file;

        public WriteTextThread(String text, File file)
        {
            this.text = text;
            this.file = file;
            this.start();
        }

        @Override
        public void run()
        {
            try
            {
                PrintWriter writer = new PrintWriter(new FileWriter(file));
                writer.append(text);
                writer.close();
            } catch (IOException ex)
            {
                Log.warn(this, ex);
            }
        }
    }

    private static class WriteImageThread extends Thread
    {
        private BufferedImage image;
        private File file;

        public WriteImageThread(BufferedImage image, File file)
        {
            this.image = image;
            this.file = file;
            this.start();
        }

        @Override
        public void run()
        {
            try
            {
                ImageIO.write(image, "png", file);
            } catch (IOException ex)
            {
                Log.warn(this, ex);
            }
        }
    }

    public static boolean isFile(Object file, String... extensions)
    {
        if (file instanceof File)
        {
            String name = ((File) file).getName().trim().toLowerCase();
            for (String extension : extensions)
                if (name.endsWith(extension.startsWith(".") ? extension
                        : extension.lastIndexOf(".") == -1 ? "." + extension : extension.substring(extension.lastIndexOf("."), extension.length())))
                    return true;
        }
        return false;
    }

    public static InputStream load(String path)
    {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if (stream == null)
            Log.warn(IO.class, ".load - loading file failed: " + path);
        return stream;
    }

    public static BufferedReader loadReader(String path)
    {
        return new BufferedReader(new InputStreamReader(load(path)));
    }

    public static boolean bytesToOutputStream(byte[] data, OutputStream out)
    {
        try
        {
            out = new BufferedOutputStream(out);
            out.write(data);
            out.flush();
            return true;
        } catch (IOException ex)
        {
            Log.warn(IO.class, ".bytesToOutputStream - exception: " + ex.getMessage());
        }
        return false;
    }

    public static BufferedInputStream Buffered(InputStream in)
    {
        return in == null ? null : (in instanceof BufferedInputStream ? (BufferedInputStream) in : new BufferedInputStream(in));
    }

    public static void Close(boolean doIt, InputStream stream)
    {
        if (doIt)
            Close(stream);
    }

    public static void Close(InputStream stream)
    {
        if (stream != null)
            try
            {
                stream.close();
            } catch (Exception e)
            {
                Log.warn(IO.class, ".close - data closing exception: " + e.getMessage());
                e.printStackTrace();
            }
    }


    public static void Close(Closable... datas)
    {
        for (Closable data : datas)
            if (data != null)
                try
                {
                    data.close();
                } catch (Exception e)
                {
                    Log.warn(IO.class, ".close - data closing exception: " + e.getMessage());
                    e.printStackTrace();
                }
    }

    public static void CloseIf(boolean cond, Closeable... datas)
    {
        if (cond)
            Close(datas);
    }

    public static void Close(Closeable... datas)
    {
        for (Closeable data : datas)
            if (data != null)
                try
                {
                    data.close();
                } catch (Exception e)
                {
                    Log.warn(IO.class, ".close - data closing exception: " + e.getMessage());
                    e.printStackTrace();
                }
    }

    public static void Flush(Flushable data)
    {
        if (data != null)
            try
            {
                data.flush();
            } catch (IOException e)
            {
                Log.warn(IO.class, ".flush - data flushing exception: " + e.getMessage());
                e.printStackTrace();
            }
    }

    public static Object DeepClone(final Serializable o)
    {
        final PipedOutputStream pipeout = new PipedOutputStream();
        PipedInputStream pipein = null;
        try
        {
            pipein = new PipedInputStream(pipeout);
        } catch (IOException e)
        {
            return null;
        }

        Thread writer = new Thread()
        {
            @Override
            public void run()
            {
                ObjectOutputStream out = null;
                try
                {
                    out = new ObjectOutputStream(pipeout);
                    out.writeObject(o);
                } catch (IOException e)
                {
                } finally
                {
                    try
                    {
                        out.close();
                    } catch (Exception e)
                    {
                    }
                }
            }
        };
        writer.start();

        ObjectInputStream in;
        try
        {
            in = new ObjectInputStream(pipein);
            return in.readObject();
        } catch (IOException e)
        {
            return null;
        } catch (ClassNotFoundException e)
        {
            return null;
        }
    }

    public static FileInputStream InputStream(File file)
    {
        try
        {
            return new FileInputStream(file);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] GZip(byte[] data)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try
        {
            GZIPOutputStream zip = new GZIPOutputStream(bos);
            zip.write(data);
            zip.flush();
            zip.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }


    public static Thread Async(Runnable process)
    {
        return Async(true, process, null);
    }

    public static Thread Async(Runnable process, Runnable callback)
    {
        return Async(true, process, callback);
    }

    public static Thread Async(boolean async, Runnable process, Runnable callback)
    {
        Runnable processWithCallback = process;

        if (callback != null)
        {
            processWithCallback = () ->
            {
                process.run();
                callback.run();
            };
        }

        if(async)
        {
            Thread thread = new Thread(processWithCallback);
            thread.setDaemon(false);
            thread.start();
            return thread;
        }
        else
            processWithCallback.run();

        return null;
    }

    public static void Dispose()
    {
        if (WRITING_THREAD != null)
            WRITING_THREAD.shutdown();
    }

}
