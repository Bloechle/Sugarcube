package sugarcube.formats.pdf.reader.pdf.node.font.aff;

import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.geom.Point3;

public class AFFGlyph
{
    float[] stack = new float[100];
    float[] psStack = new float[3];// postscript stack
    int k = 0;
    int psk = 0;
    String name;
    Path3 path = new Path3();
    float x = 0;
    float y = 0;
    float w = 0;
    float h = 0;

    public AFFGlyph(String name, Point3 advance)
    {
        this.name = name;
        this.w = advance.x;
        this.h = advance.y;
    }

    public float pop()
    {
        float val = 0.0F;
        if (k > 0)
            val = stack[--k];
        return val;
    }
}