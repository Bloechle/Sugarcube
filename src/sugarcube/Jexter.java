package sugarcube;

import sugarcube.common.system.io.IO;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDDocument;

import java.io.File;

public class Jexter {

    private OCDDocument ocd;

    public Jexter() {

    }

    public void readPDF(String path)
    {
        this.ocd = OCD.Load(path);
    }

    public void writeJson(String path)
    {
        String json = this.ocd.toJson().toJson("  ");
        IO.WriteText(new File(path), json);
    }

    public static void main(String[] args) {

        Jexter pj = new Jexter();

        String filePath = "C:/Users/jean-/OneDrive/Desktop/pdf-zigzag.pdf";

        pj.readPDF(filePath);
        pj.writeJson(filePath.replace(".pdf", ".json"));


    }

}
