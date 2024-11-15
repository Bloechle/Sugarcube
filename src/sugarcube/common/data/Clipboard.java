package sugarcube.common.data;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class Clipboard
{
    public static String text()
    {
        try
        {
            return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (Exception ex)
        {
            return null;
        }
    }

    public static Image image()
    {
        try
        {
            return (Image) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.imageFlavor);
        } catch (Exception ex)
        {
            return null;
        }
    }

    public static void clip(String text)
    {
        new ClipTextThread(text);
    }

    public static void clip(Image image)
    {
        new ClipImageThread(image);
    }

    private static class ClipTextThread extends Thread
    {
        private String text;

        public ClipTextThread(String text)
        {
            this.text = text;
            this.start();
        }

        @Override
        public void run()
        {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
        }
    }

    private static class ClipImageThread extends Thread
    {
        private Image image;

        public ClipImageThread(Image image)
        {
            this.image = image;
            this.start();
        }

        @Override
        public void run()
        {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new ImageSelection(image), null);
        }
    }

    private static class ImageSelection implements Transferable
    {
        private Image image;

        public ImageSelection(Image image)
        {
            this.image = image;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors()
        {
            return new DataFlavor[]
                    {DataFlavor.imageFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor)
        {
            return DataFlavor.imageFlavor.equals(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
        {
            if (!DataFlavor.imageFlavor.equals(flavor))
                throw new UnsupportedFlavorException(flavor);
            return image;
        }
    }
}
