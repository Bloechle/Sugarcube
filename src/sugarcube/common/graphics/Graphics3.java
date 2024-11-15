package sugarcube.common.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.transform.Affine;
import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.geom.*;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.gui.Font3;
import sugarcube.common.interfaces.Colorable;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.util.HashMap;
import java.util.Map;

public class Graphics3
{
    private Graphics2D g;
    private GraphicsContext fx;
    private double width;
    private double height;

    public Graphics3(Graphics g)
    {
        this(g, -1, -1);
    }

    public Graphics3(Graphics g, double width, double height)
    {
        this.g = (Graphics2D) g;
        this.width = width;
        this.height = height;
        this.g.addRenderingHints(RenderingHints3.HQ_HINTS);
    }

    public Graphics3(Graphics g, Dimension2D dimension)
    {
        this(g, dimension.getWidth(), dimension.getHeight());
    }

    public Graphics3(BufferedImage image)
    {
        this(image.createGraphics(), image.getWidth(), image.getHeight());
    }

    public Graphics3(VolatileImage image)
    {
        this(image.createGraphics(), image.getWidth(), image.getHeight());
    }

    public Graphics3(javafx.scene.canvas.GraphicsContext fx)
    {
        this(fx, -1, -1);
    }

    public Graphics3(javafx.scene.canvas.GraphicsContext fx, double width, double height)
    {
        this.fx = fx;
        this.width = width;
        this.height = height;
    }

    public Graphics3(javafx.scene.canvas.Canvas c)
    {
        this.fx = c.getGraphicsContext2D();
        this.width = c.getWidth();
        this.height = c.getHeight();
    }

    public Graphics2D context()
    {
        return this.g;
    }

    public Graphics2D graphics()
    {
        return g;
    }

    public GraphicsContext fxContext()
    {
        return this.fx;
    }

    public GraphicsContext fxGraphics()
    {
        return this.fx;
    }

    public void reset()
    {
        if (g != null)
        {
            this.g.setRenderingHints(RenderingHints3.HQ_HINTS);
            AffineTransform at = this.g.getTransform();
            this.g.setTransform(new Transform3());
            if (width > 1 && height > 1)
                this.g.setClip(0, 0, (int) width, (int) height);
            this.g.setTransform(at);
        }
        if (fx != null)
        {
            this.fx.save();
            if (width > 1 && height > 1)
            {
                this.fx.moveTo(0, 0);
                this.fx.lineTo(width, 0);
                this.fx.lineTo(width, height);
                this.fx.lineTo(0, height);
                this.fx.closePath();
                this.fx.clip();
            }
            this.fx.restore();
        }
    }

    public int spaceWidth()
    {
        return (int) (this.width() / this.transform().scaleX());
    }

    public int spaceHeight()
    {
        return (int) (this.height() / this.transform().scaleY());
    }

    public int intWidth()
    {
        return (int) width;
    }

    public int intHeight()
    {
        return (int) height;
    }

    public Dimension3 dimension()
    {
        return new Dimension3(width, height);
    }

    public double width()
    {
        return width;
    }

    public double height()
    {
        return height;
    }

    public double halfWidth()
    {
        return width / 2.0;
    }

    public double halfHeight()
    {
        return height / 2.0;
    }

    public Graphics3 highQuality()
    {
        if (g != null)
            g.setRenderingHints(RenderingHints3.HQ_HINTS);
        return this;
    }

    public Graphics3 lowQuality()
    {
        if (g != null)
            g.setRenderingHints(new HashMap());
        return this;
    }

    public Graphics3 setInterpolation(Boolean bicubic)
    {
        if (g != null)
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, bicubic == null ? RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR : (bicubic ? RenderingHints.VALUE_INTERPOLATION_BICUBIC : RenderingHints.VALUE_INTERPOLATION_BILINEAR));
        return this;
    }

    public void stroke(Shape shape)
    {
        this.draw(shape);
    }

    public void stroke(Shape shape, Paint paint)
    {
        this.draw(shape, paint);
    }

    public void stroke(Shape shape, Paint paint, Stroke stroke)
    {
        this.draw(shape, paint, stroke);
    }

    public void stroke(Shape shape, Paint paint, double stroke)
    {
        this.draw(shape, paint, stroke);
    }

    public void stroke(Shape shape, double stroke)
    {
        this.draw(shape, stroke);
    }

    public void stroke(Shape shape, Stroke stroke)
    {
        this.draw(shape, stroke);
    }

    public void draw(Shape shape, Paint paint)
    {
        if (shape != null && paint != null)
        {
            this.setPaint(paint);
            this.draw(shape);
        }
    }

    public void draw(Shape shape, Paint paint, Stroke stroke)
    {
        if (paint != null)
        {
            this.setPaint(paint);
            this.setStroke(stroke);
            this.draw(shape);
        }
    }

    public void draw(Shape shape, Paint paint, double stroke)
    {
        if (paint != null)
        {
            this.setPaint(paint);
            this.setStroke(stroke);
            this.draw(shape);
        }
    }

    public void draw(Shape shape, double stroke)
    {
        this.setStroke(stroke);
        this.draw(shape);
    }

    public void draw(Shape shape, Stroke stroke)
    {
        this.setStroke(stroke);
        this.draw(shape);
    }

    public void fill(Shape shape, Paint paint)
    {
        if (shape != null && paint != null)
        {
            this.setPaint(paint);
            this.fill(shape);
        }
    }

    public void paint(Shape shape, Paint fill, Paint draw)
    {
        this.fill(shape, fill);
        this.draw(shape, draw);
    }

    public void paint(Shape shape, Paint fill, Paint draw, double stroke)
    {
        this.paint(shape, fill, draw, new Stroke3(stroke));
    }

    public void paint(Shape shape, Paint fill, Paint draw, Stroke stroke)
    {
        this.setStroke(stroke);

        if (fill != null)
        {
            if (g != null)
            {
                this.g.setPaint(fill);
                this.g.fill(shape);
            }
            if (fx != null)
            {
                this.fx.setFill(Fx.fxPaint(fill));
                this.fxShape(shape);
                this.fx.fill();
            }
        }
        if (draw != null)
        {
            if (g != null)
            {
                this.g.setPaint(draw);
                this.g.draw(shape);
            }
            if (fx != null)
            {
                this.fx.setStroke(Fx.fxPaint(draw));
                this.fxShape(shape);
                this.fx.stroke();
            }
        }
    }

    private void fxShape(Shape shape)
    {
        fx.beginPath();
        PathIterator it = shape.getPathIterator(null);
        float[] p = new float[6];
        do
        {
            int op = it.currentSegment(p);
            switch (op)
            {
                case PathIterator.SEG_MOVETO:
                    fx.moveTo(p[0], p[1]);
                    break;
                case PathIterator.SEG_LINETO:
                    fx.lineTo(p[0], p[1]);
                    break;
                case PathIterator.SEG_CUBICTO:
                    fx.bezierCurveTo(p[0], p[1], p[2], p[3], p[4], p[5]);
                    break;
                case PathIterator.SEG_QUADTO:
                    fx.quadraticCurveTo(p[0], p[1], p[2], p[3]);
                    break;
                case PathIterator.SEG_CLOSE:
                    fx.closePath();
                    break;
            }
            it.next();
        } while (!it.isDone());
    }

    public void draw(Shape s)
    {
        if (s != null)
        {
            if (g != null)
                this.g.draw(s);
            if (fx != null)
            {
                this.fxShape(s);
                this.fx.stroke();
            }
        }
    }

    public void fill(Shape s)
    {
        if (s != null)
        {
            if (g != null)
                this.g.fill(s);
            if (fx != null)
            {
                this.fxShape(s);
                this.fx.fill();
            }
        }
    }

    public void line(double x1, double y1, double x2, double y2, double width)
    {
        this.setStroke(width);
        this.draw(new Line3(x1, y1, x2, y2));
    }

    public void line(double x1, double y1, double x2, double y2, Paint color, double width)
    {
        this.setStroke(width);
        this.setPaint(color);
        this.draw(new Line3(x1, y1, x2, y2));
    }

    public void line(double x1, double y1, double x2, double y2, Paint color, Stroke stroke)
    {
        this.setStroke(stroke);
        this.setPaint(color);
        this.draw(new Line3(x1, y1, x2, y2));
    }

    public void rect(double x, double y, double w, double h)
    {
        this.draw(new Rectangle3(x, y, w, h));
    }

    public void rect(double x, double y, double w, double h, Paint fill)
    {
        this.setPaint(fill);
        this.fill(new Rectangle3(x, y, w, h));
    }

    public void rect(double x, double y, double w, double h, Paint fill, Paint draw, Stroke stroke)
    {
        Rectangle3 r = new Rectangle3(x, y, w, h);
        if (fill != null)
        {
            this.setPaint(fill);
            this.fill(r);
        }
        this.setStroke(stroke);
        if (draw != null)
        {
            this.setPaint(draw);
            this.stroke(r);
        }
    }

    public void circle(double x, double y, double r, Paint fill)
    {
        this.circle(x, y, r, fill, null);
    }

    public void circle(double x, double y, double r, Paint fill, Paint stroke)
    {
        Circle3 circle = new Circle3(x, y, r);
        if (fill != null)
            this.fill(circle, fill);
        if (stroke != null)
            this.draw(circle, stroke);
    }

    public void draw(java.awt.Image image)
    {
        this.draw(image, null);
    }

    public void draw(java.awt.Image image, double x, double y)
    {
        this.draw(image, Transform3.translateInstance(x, y));
    }

    public void draw(java.awt.Image image, AffineTransform transform)
    {
        if (g != null)
            this.g.drawImage(image, transform, null);
        if (fx != null)
        {
            this.fx.save();
            Transform3 tm = Transform3.create(this.fx.getTransform());
            if (transform != null)
                tm.concatenate(transform);

            if (tm.isIdentity(0.0001))
                this.fx.setTransform(new Affine());
            else
                this.fx.setTransform(tm.getScaleX(), tm.getShearY(), tm.getShearX(), tm.getScaleY(), tm.getTranslateX(), tm.getTranslateY());
            this.fx.drawImage(Fx.toFXImage(image), 0, 0);
            this.fx.restore();
        }
    }

    public void draw(javafx.scene.image.Image image)
    {
        this.draw(image, null);
    }

    public void draw(javafx.scene.image.Image image, double x, double y)
    {
        this.draw(image, Transform3.translateInstance(x, y));
    }

    public void draw(javafx.scene.image.Image image, AffineTransform transform)
    {
        if (g != null)
            this.g.drawImage(Fx.toBufferedImage(image), transform, null);
        if (fx != null)
        {
            this.fx.save();
            Transform3 tm = Transform3.create(this.fx.getTransform());
            if (transform != null)
                tm.concatenate(transform);
            this.fx.setTransform(tm.getScaleX(), tm.getShearY(), tm.getShearX(), tm.getScaleY(), tm.getTranslateX(), tm.getTranslateY());

//      Log.debug(this,  ".draw - image: "+image+", tm="+tm);           

            this.fx.drawImage(image, 0, 0);
            this.fx.restore();
        }
    }

    public void draw(String text, double x, double y)
    {
        if (g != null)
            this.g.drawString(text, (float) x, (float) y);
        if (fx != null)
            this.fx.fillText(text, x, y);
    }

    public void draw(String text, double x, double y, Font font, Paint paint)
    {
        if (font != null)
            this.setFont(font);
        if (paint != null)
            this.setPaint(paint);
        this.draw(text, x, y);
    }

    public void drawTo(String text, double x, double y, Font font, Paint paint)
    {
        this.draw(text, x - bounds(text, font).width(), y, font, paint);
    }

    public void drawCenterX(String text, double x, double y, Font font, Paint paint)
    {
        Rectangle3 r = bounds(text, font);
        this.draw(text, x - r.halfWidth(), y, font, paint);
    }

    public void textCx(String text, double x, double y, Font font, Paint paint, boolean mirrorX, boolean mirrorY)
    {
        if (!mirrorX && !mirrorY)
        {
            this.drawCenterX(text, x, y, font, paint);
            return;
        }
        Rectangle3 r = bounds(text, font);
        // Log.debug(this, ".drawCenter - r: " + r);
        Image3 canvas = new Image3(r.intWidth(), r.intHeight(), Image3.Type.ARGB);
        Graphics3 g = canvas.graphics();
        // g.clear(Color3.RED);
        g.drawCenterX(text, r.width / 2, -r.y, font, paint);
        canvas = canvas.mirror(mirrorX, mirrorY);
        this.draw(canvas, Math.round(x - r.halfWidth()), Math.round(-r.y));
    }

    public void drawCenter(String text, double x, double y, Font font, Paint paint)
    {
        Rectangle3 r = bounds(text, font);
        this.draw(text, x - r.halfWidth(), y + r.halfHeight() / 2, font, paint);
    }

    public Path3 outline(String text, Font font)
    {
        return new Path3(font.createGlyphVector(this.g.getFontRenderContext(), text).getOutline());
    }

//  public Rectangle3 bounds(String text)
//  {
//    return new Rectangle3(this.getFont().getStringBounds(text, this.g.getFontRenderContext()));
//  }

    public static Rectangle3 bounds(String text, Font font)
    {
        BufferedImage bi = new BufferedImage(BufferedImage.TYPE_INT_RGB, 10, 10);
        return new Rectangle3(font.getStringBounds(text, bi.createGraphics().getFontRenderContext()));
    }

    public Stroke getStroke()
    {
        return g == null ? null : g.getStroke();
    }

    public Color3 getColor()
    {
        return g == null ? null : new Color3(g.getColor());
    }

    public void setColor(int argb)
    {
        this.setPaint(new Color3(argb));
    }

    public void setColor(Colorable c)
    {
        this.setPaint(c.color());
    }

    public void setColor(Color c)
    {
        this.setPaint(c);
    }

    public void setPaint(Paint p)
    {
        if (g != null)
            this.g.setPaint(p);
        if (fx != null)
        {
            this.fx.setFill(Fx.fxPaint(p));
            this.fx.setStroke(Fx.fxPaint(p));
        }
    }

    public void setStroke(double width)
    {
        if (g != null)
            this.g.setStroke(new BasicStroke((float) width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        if (fx != null)
        {
            this.fx.setLineWidth(width);
            this.fx.setLineCap(StrokeLineCap.BUTT);
            this.fx.setLineJoin(StrokeLineJoin.MITER);
        }
    }

    public void setStroke(Stroke s)
    {
        if (s == null)
            s = Stroke3.LINE;
        if (g != null)
            this.g.setStroke(s);
        if (fx != null)
        {
            if (s instanceof BasicStroke)
            {
                Stroke3 s3 = s instanceof Stroke3 ? (Stroke3) s : new Stroke3((BasicStroke) s);
                this.fx.setLineWidth(s3.pen());
                this.fx.setLineCap(s3.fxCap());
                this.fx.setLineJoin(s3.fxJoin());
            } else
                Log.debug(this, ".setStroke - unknown stroke: " + s);
        }
    }

    public void setHints(Map<?, ?> hints)
    {
        this.g.setRenderingHints(hints);
    }

    public void setScale(double scale)
    {
        this.setTransform(scale, 0, 0, scale, 0, 0);
    }

    public void clearTransform()
    {
        this.setTransform();
    }

    public void resetTransform()
    {
        this.setTransform();
    }

    public void setTransform(double... transform)
    {
        this.setTransform(new Transform3(transform));
    }

    public void setTransform(AffineTransform tm)
    {
        if (tm == null)
            tm = new AffineTransform();
        if (g != null)
            this.g.setTransform(tm);
        if (fx != null)
            this.fx.setTransform(tm.getScaleX(), tm.getShearY(), tm.getShearX(), tm.getScaleY(), tm.getTranslateX(), tm.getTranslateY());
    }

    public void concatTransform(AffineTransform transform)
    {
        if (transform != null)
        {
            Transform3 concat = transform();
            if (concat != null)
            {
                concat.concatenate(transform);
                this.setTransform(concat);
            }
        }
    }

    public Transform3 transform()
    {
        if (g != null)
            return Transform3.create(this.g.getTransform());
        if (fx != null)
            return Transform3.create(this.fx.getTransform());
        return null;
    }

    public void translate(double tx, double ty)
    {
        if (g != null)
            this.g.translate(tx, ty);
        if (fx != null)
            this.fx.translate(tx, ty);
    }

    public void scale(double sx, double sy)
    {
        if (g != null)
            this.g.scale(sx, sy);
        if (fx != null)
            this.fx.scale(sx, sy);
    }

    public void scale(double scale)
    {
        this.scale(scale, scale);
    }

    public void setComposite(Composite c)
    {
        if (g != null)
            this.g.setComposite(c);
        if (fx != null)
            Log.debug(this, " - blend mode not yet implemented: " + c);
    }

    public void setClip(Shape s)
    {
        if (g != null)
        {
            if (s == null && width > 0 && height > 0)
            {
                AffineTransform at = this.g.getTransform();
                this.g.setTransform(new Transform3());
                this.g.setClip(0, 0, (int) width, (int) height);
                this.g.setTransform(at);
            } else
                this.g.setClip(s);
        }
        if (fx != null)
        {
            if (s == null && width > 0 && height > 0)
            {
                Affine at = this.fx.getTransform();
                this.fx.setTransform(1, 0, 0, 1, 0, 0);
                this.fxShape(new Rectangle3(0, 0, (int) width, (int) height));
                this.fx.clip();
                this.fx.setTransform(at);
            } else
            {
                this.fxShape(s);
                this.fx.clip();
            }

        }
    }

    public void setFont(Font f)
    {
        if (g != null)
            this.g.setFont(f);
        if (fx != null)
            this.fx.setFont(new javafx.scene.text.Font(f.getName(), f.getSize2D()));
    }

    public Font getFont()
    {
        return this.g.getFont();
    }

    public Font3 font()
    {
        return new Font3(this.g.getFont());
    }

    public Font3 font(float size)
    {
        return new Font3(getFont().deriveFont(size));
    }

    public void clearChessBoard()
    {
        clearChessBoard(16);
    }

    public void clearChessBoard(int size)
    {
        clearChessBoard(Color3.WHITE, Color3.DUST_WHITE, size);
    }

    public void clearChessBoard(Color c1, Color c2, int size)
    {
        fillChessBoard(0, 0, width, height, c1, c2, size);
    }

    public void fillChessBoard(int x, int y, double w, double h)
    {
        fillChessBoard(x, y, w, h, 16);
    }

    public void fillChessBoard(int x, int y, double w, double h, int size)
    {
        fillChessBoard(x, y, w, h, Color3.WHITE, Color3.DUST_WHITE, size);
    }

    public void fillChessBoard(int x, int y, double w, double h, Color c1, Color c2, int size)
    {
        int x0 = x;
        int y0 = y;

        for (y = y0; y < h + y0; y += size)
            for (x = x0; x < w + x0; x += size)
            {
                this.fill(new Rectangle3(x, y, size, size), (x / size + y / size) % 2 == 0 ? c1 : c2);
            }
    }

    public void clearWhite()
    {
        this.clearWhite(width, height);
    }

    public void clearWhite(double w, double h)
    {
        this.clear(Color.WHITE, w, h);
    }

    public void clearBlack()
    {
        this.clearBlack(width, height);
    }

    public void clearBlack(double w, double h)
    {
        this.clear(Color.BLACK, w, h);
    }

    public void clear(Paint paint)
    {
        this.clear(paint, width, height);
    }

    public void clear(Paint paint, double w, double h)
    {
        if (w > 0 && h > 0)
        {
            Transform3 tm = this.transform();
            setPaint(paint);
            fill(new Rectangle3(0, 0, (int) (w * tm.sx()), (int) (h * tm.sy())));
        }
    }

    public void setBounds(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    public void setBounds(Dimension2D dimension)
    {
        this.width = (int) dimension.getWidth();
        this.height = (int) dimension.getHeight();
    }

    public Rectangle3 bounds()
    {
        return width < 0 || height < 0 ? null : new Rectangle3(0, 0, width, height);
    }

    public void dispose()
    {
        if (g != null)
            this.g.dispose();
    }

    public void setComposite(float alpha, String... composites)
    {
        String blend = composites == null || composites.length == 0 || composites.length == 1 && composites[0] == null ? BlendComposite.MODE_NORMAL
                : composites[0].toLowerCase();
        switch (blend)
        {
            case BlendComposite.MODE_NORMAL:
                this.resetComposite(alpha);
                break;
            case BlendComposite.MODE_MULTIPLY:
                this.setComposite(BlendComposite.Multiply.derive(alpha));
                break;
            case BlendComposite.MODE_SOFTLIGHT:
                this.setComposite(BlendComposite.SoftLight.derive(alpha));
                break;
            case BlendComposite.MODE_HARDLIGHT:
                this.setComposite(BlendComposite.HardLight.derive(alpha));
                break;
            case BlendComposite.MODE_OVERLAY:
                this.setComposite(BlendComposite.Overlay.derive(alpha));
                break;
            case BlendComposite.MODE_SCREEN:
                this.setComposite(BlendComposite.Screen.derive(alpha));
                break;
            default:
                Log.debug(this, ".setComposite - blend=" + blend);
                this.resetComposite(alpha);
        }

        if (fx != null)
            Log.debug(this, ".setComposite - FX composite not yet implemented");
    }

    public void resetComposite(float alpha)
    {
        this.setComposite(AlphaComposite.SrcOver.derive(alpha));
    }

    @Override
    public String toString()
    {
        return "Graphics3[" + this.width + " " + this.height + "]";
    }
}
