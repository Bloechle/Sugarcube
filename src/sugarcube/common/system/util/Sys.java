package sugarcube.common.system.util;

import sugarcube.common.ui.gui.Font3;
import sugarcube.common.interfaces.Tester;

import javax.swing.*;
import java.awt.*;

public class Sys
{
    public static Void Void()
    {
        return null;
    }

    public static void UTF8()
    {
        System.setProperty("file.encoding", "UTF-8");
    }

    public static String Name(Class cls)
    {
        return cls.getSimpleName();
    }

    public static String[] Fonts()
    {
        return Font3.ListOSFonts();
    }

    public static Thread Run(Runnable runnable)
    {
        Thread thread = new Thread(runnable);
        thread.start();
        return thread;
    }

    public static void SleepWhile(long millis, Tester test)
    {
        while (test.condition())
            Sleep(millis);
    }

    public static void Sleep(long millis)
    {
        if (millis > 0)
            try
            {
                Thread.sleep(millis);
            } catch (InterruptedException ex)
            {
                ex.printStackTrace();
            }
    }

    public static long Millis()
    {
        return System.currentTimeMillis();
    }

    public static long Elapsed(long millis)
    {
        return Millis() - millis;
    }

    public static void Print(Object o)
    {
        System.out.print(o == null ? "null" : o.toString());
    }

    public static void Println(Object o)
    {
        System.out.print(o == null ? "null\n" : o.toString() + "\n");
    }

    public static void Copy(Object arraySrc, Object arrayDst, int length)
    {
        System.arraycopy(arraySrc, 0, arrayDst, 0, length);
    }

    public static void LAF()
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            Toolkit.getDefaultToolkit().setDynamicLayout(true);
            System.setProperty("sun.awt.noerasebackground", "true");
        } catch (Exception e)
        {
        }
    }



}
