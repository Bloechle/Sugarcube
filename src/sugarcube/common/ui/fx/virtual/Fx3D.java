package sugarcube.common.ui.fx.virtual;

import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape3D;
import sugarcube.common.data.collections.Map3;

public class Fx3D
{
    public static final Point3D X = new Point3D(1, 0, 0);
    public static final Point3D Y = new Point3D(0, 1, 0);
    public static final Point3D Z = new Point3D(0, 0, 1);

    public static Map3<Color, FxPhong> MATERIALS = new Map3<>();

    public interface FxNode3D
    {
        Shape3D node();

        default FxNode3D color(Color color)
        {
            Color(node(), color);
            return this;
        }

        default FxNode3D setXYZ(float[] xyz)
        {
            if (xyz != null)
            {
                if (xyz.length > 0)
                    node().setTranslateX(xyz[0]);
                if (xyz.length > 1)
                    node().setTranslateY(xyz[1]);
                if (xyz.length > 2)
                    node().setTranslateZ(xyz[2]);
            }
            return this;
        }

        default FxNode3D setXYZ(double[] xyz)
        {
            if (xyz != null)
            {
                if (xyz.length > 0)
                    node().setTranslateX(xyz[0]);
                if (xyz.length > 1)
                    node().setTranslateY(xyz[1]);
                if (xyz.length > 2)
                    node().setTranslateZ(xyz[2]);
            }
            return this;
        }

        default FxNode3D setXYZ(double x, double y, double z)
        {
            node().setTranslateX(x);
            node().setTranslateY(y);
            node().setTranslateZ(z);
            return this;
        }
    }

    public interface FxNodable3D
    {
        FxNode3D fx();
    }

    public static Node SetXYZ(Node node, double x, double y, double z)
    {
        node.setTranslateX(x);
        node.setTranslateY(y);
        node.setTranslateZ(z);
        return node;
    }

    public static Shape3D Color(Node node, Color color)
    {
        if (node instanceof Shape3D)
            return Color((Shape3D) node, color);
        return null;
    }

    public static Shape3D Color(Shape3D node, Color color)
    {
        node.setMaterial(Material(color));
        return node;
    }

    public static FxPhong Material(Color c)
    {
        FxPhong mat = MATERIALS.get(c);
//        if (mat == null)
//            MATERIALS.put(c, mat = FxPhong.Get(c == null ? OptiColors.WHITE : c));
        return mat;
    }


    // public static FxGroup LoadDAE(String path)
    // {
    // final DaeImporter importer = new DaeImporter();
    // try
    // {
    // importer.load(path);
    // } catch (Exception e)
    // {
    // e.printStackTrace();
    // }
    // return importer.getRoot();
    //
    // }
}
