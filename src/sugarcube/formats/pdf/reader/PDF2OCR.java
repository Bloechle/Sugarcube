package sugarcube.formats.pdf.reader;


import sugarcube.common.graphics.geom.Coords;
import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Image3;
import sugarcube.common.interfaces.Glyph;
import sugarcube.common.system.io.IO;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDDocument;
import sugarcube.formats.ocd.objects.OCDPage;
import sugarcube.formats.ocd.objects.OCDPageContent;
import sugarcube.formats.ocd.objects.OCDText;

import java.awt.*;
import java.io.File;

public class PDF2OCR

{

    public static void generateOcrFile(String pdfFile)
    {
        String filePath = pdfFile.replace(".pdf", "");


        Image3 image = Image3.Read(new File(filePath + ".png"));
        Image3 boxImage = new Image3(image.width(), image.height());
        Graphics3 g = boxImage.graphics();
        g.draw(image);

        Dexter pdf2ocd = new Dexter(OCD.canonizerProps).disableRestructuring();

        OCDDocument ocd = pdf2ocd.convert(pdfFile, filePath + ".ocd");

        for (OCDPage page : ocd)
        {
            double scale = image.width()/page.width;

            log("#### PAGE " + page.number() + " ################################################################");
            page.ensureInMemory();

            StringBuilder ocr = new StringBuilder();

            OCDPageContent content = page.content();//contains OCD graphics such as images, paths and text

            for (OCDText text : content.allTexts())//an OCDText is a run of text having identical properties: fontname, fontsize, color, etc.
            {

                try
                {
                    Glyph[] glyphs = text.glyphs();
                    Coords coords = text.coords();
                    for (int i = 0; i < glyphs.length; i++)
                    {
                        Glyph glyph = glyphs[i];
                        Path3 path = text.transform().moveTo(coords.pointAt(i)).transform(glyph.path(text.fontScale()));
                        Rectangle3 box = path.bounds().copy().scale(scale);

                        String symbol = glyph.code();
                        ocr.append(symbol).append(" 0 ");

                        for (Point3 p : box.cornerPoints())
                            ocr.append(" ").append(p.getX()).append(" ").append(p.getY());

                        ocr.append("\n");

                        if (g != null)
                        {
                            g.rect(box.minX(), box.minY(), box.width(), box.height(),new Color(0, 1, 0, 0.8f));
                        }
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }

                log("Text[" + text.glyphString() + "]");//uniString does its best to get meaningful unicodes :-)
                log("Font[" + text.fontname() + "]");
                log("Size[" + text.fontsize() + "]");
                log("Bounds" + text.bounds());
                log("");

            }

            page.freeFromMemory();

            IO.WriteText(new File(filePath + ".ocr"), ocr.toString());

            boxImage.write(filePath+".jpeg");

        }

        ocd.close();
    }

    public static void main(String... args)
    {
        String pdfFile = "C:/Users/jean-/Desktop/ZigZag/HotFolder/15_15.pdf";


        generateOcrFile(pdfFile);
    }

    public static void log(String msg) //for concision purpose
    {
        System.out.println(msg);
    }
}
