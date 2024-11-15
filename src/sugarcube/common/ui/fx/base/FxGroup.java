package sugarcube.common.ui.fx.base;

import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import sugarcube.common.data.collections.Set3;
import sugarcube.common.data.collections.Str;
import sugarcube.common.graphics.geom.*;
import sugarcube.common.ui.fx.event.FxEventHandler;
import sugarcube.common.ui.fx.event.FxHandle;
import sugarcube.common.ui.fx.shapes.FxCircle;
import sugarcube.common.ui.fx.shapes.FxLine;
import sugarcube.common.ui.fx.shapes.FxRect;

import java.util.Collection;

public class FxGroup extends Group implements IPoint
{
    protected String name;

    public FxGroup(Node... nodes)
    {
        this.add(nodes);
    }

    public FxGroup(String name)
    {
        this.setName(name);
    }

    public FxGroup style(String style)
    {
        return (FxGroup) FxCSS.Style(this, style, false);
    }

    public String name()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isName(String name)
    {
        return Str.Equal(name, this.name);
    }

    public FxGroup opacity(double alpha)
    {
        this.setOpacity(alpha);
        return this;
    }

    public FxGroup hide()
    {
        return visible(false);
    }

    public FxGroup show()
    {
        return visible(true);
    }

    public FxGroup visible(boolean isVisible)
    {
        if (this.isVisible() != isVisible)
            this.setVisible(isVisible);
        return this;
    }

    public FxGroup parentGroup()
    {
        Parent g = this.parentProperty().get();
        return g instanceof FxGroup ? (FxGroup) g : null;
    }

    public FxGroup clear()
    {
        this.getChildren().clear();
        return this;
    }

    public void fxClear()
    {
        Fx.Run(() -> clear());
    }

    public void refreshFX()
    {

    }

    public FxGroup set(Collection<Node> child)
    {
        return this.set(child.toArray(new Node[0]));
    }

    public FxGroup set(Node... child)
    {
        this.clear();
        return this.add(child);
    }

    public FxGroup fxSet(Node... child)
    {
        Fx.Run(() -> set(child));
        return this;
    }

    public FxGroup add(Node... child)
    {
        this.getChildren().addAll(new Set3<>(child).unnull().removeAll3(getChildren()));
        Node p = this.getParent();
        Node gp = p == null ? null : p.getParent();
//    Log.debug(this, ".add - "+this+", children="+getChildren().size()+", parent="+p+", grand-parent="+gp);
        return this;
    }

    public FxGroup fxAdd(Node... child)
    {
        Fx.Run(() -> add(child));
        return this;
    }

    public FxGroup add(int index, Node... child)
    {
        Node[] nodes = new Set3<>(child).unnull().removeAll3(getChildren()).toArray(new Node[0]);
        for (int i = 0; i < nodes.length; i++)
            this.getChildren().add(index + i, nodes[i]);
        return this;
    }

    public FxGroup addGroup()
    {
        FxGroup group = new FxGroup();
        this.add(group);
        return group;
    }

    public FxCircle addCircle(Circle3 c)
    {
        FxCircle circle = c.fx();
        this.add(circle);
        return circle;
    }

    public FxCircle addCircle(Point3 center, double r)
    {
        FxCircle circle = new FxCircle(center, r);
        this.add(circle);
        return circle;
    }

    public FxCircle addCircle(double cx, double cy, double r)
    {
        FxCircle circle = new FxCircle(cx, cy, r);
        this.add(circle);
        return circle;
    }

    public FxRect addRectangle(FxRect r)
    {
        this.add(r);
        return r;
    }

    public FxRect addRectangle(Rectangle3 r)
    {
        FxRect rect = r.fx();
        this.add(rect);
        return rect;
    }

    public FxRect addRectangle(double x, double y, double w, double h)
    {
        FxRect rect = new FxRect(x, y, w, h);
        this.add(rect);
        return rect;
    }

    public FxLine addLine(Line3 l)
    {
        FxLine line = l.fx();
        this.add(line);
        return line;
    }

    public FxLine addLine(Point3 p0, Point3 p1)
    {
        FxLine line = new FxLine(p0, p1);
        this.add(line);
        return line;
    }

    public FxLine addLine(double x0, double y0, double x1, double y1)
    {
        FxLine line = new FxLine(x0, y0, x1, y1);
        this.add(line);
        return line;
    }

    public FxGroup remove(Node... child)
    {
        if (child.length == 0)
        {
            Parent parent = this.parentProperty().get();
            if (parent instanceof Group)
                ((Group) parent).getChildren().remove(this);
        } else
            this.getChildren().removeAll(child);
        return this;
    }

    public int index(Node child)
    {
        return this.getChildren().indexOf(child);
    }

    public FxGroup addStyleSheet(Collection<? extends String> css)
    {
        this.getStylesheets().addAll(css);
        return this;
    }

    public FxGroup addStyleSheet(String... css)
    {
        for (String style : css)
            this.getStylesheets().add(style);
        return this;
    }

    public void setHandler(FxEventHandler handler)
    {
        new FxHandle(this).events(handler);
    }

    public void setScale(double scale)
    {
        this.setScale(scale, scale);
    }

    public void setScale(double sx, double sy)
    {
        this.setScaleX(sx);
        this.setScaleY(sy);
    }

    public void setXY(double x, double y)
    {
        this.setLayoutX(x);
        this.setLayoutY(y);
    }

    public FxGroup scale(double sx, double sy)
    {
        this.getTransforms().clear();
        if (sx != 1 || sy != 1)
            this.getTransforms().add(new Scale(sx, sy));
        return this;
    }

    public FxGroup translateAndScale(double dx, double dy, double sx, double sy)
    {
        this.getTransforms().clear();
        if (dx != 0 || dy != 0)
            this.getTransforms().add(new Translate(dx, dy));
        if (sx != 1 || sy != 1)
            this.getTransforms().add(new Scale(sx, sy));
        return this;
    }

    public FxGroup addTransform(Transform3 tm)
    {
        return addTransform(tm.fx());
    }

    public FxGroup addTransform(Transform... tm)
    {
        this.getTransforms().addAll(tm);
        return this;
    }

    public double x()
    {
        return getTranslateX();
    }

    public double y()
    {
        return getTranslateY();
    }

    public double z()
    {
        return getTranslateZ();
    }

    public void setX(double x)
    {
        setTranslateX(x);
    }

    public void setY(double y)
    {
        setTranslateY(y);
    }

    public void setZ(double z)
    {
        setTranslateZ(z);
    }

    public void setXYZ(double x, double y, double z)
    {
        setX(x);
        setY(y);
        setZ(z);
    }

    public void setXYZ(IPoint p)
    {
        setXYZ(p.x(), p.y(), p.z());
    }

    public void setXYZ(Point3D p)
    {
        setXYZ(p.getX(), p.getY(), p.getZ());
    }

    public FxGroup translate(FxGroup g)
    {
        return translate(g.x(), g.y(), g.z());
    }

    public FxGroup translate(double x, double y, double z)
    {
        setXYZ(x, y, z);
        return this;
    }

    public FxGroup rotate(Point3D p, double degrees)
    {
        this.setRotationAxis(p);
        this.setRotate(degrees);
        return this;
    }

    public boolean isEmpty()
    {
        return this.getChildren().isEmpty();
    }

    public boolean hasChild(Node node)
    {
        return this.getChildren().contains(node);
    }

    public ObservableList<Node> children()
    {
        return this.getChildren();
    }

    public FxGroup mouseTransparent()
    {
        this.setMouseTransparent(true);
        return this;
    }

    public FxGroup Wrap(Node... nodes)
    {
        return new FxGroup(nodes);
    }


    // public static void setTopLeft(Node node, double top, double left)
    // {
    // AnchorPane.setTopAnchor(node, top);
    // AnchorPane.setLeftAnchor(node, left);
    // }
}
