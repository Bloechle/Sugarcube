package sugarcube.insight.render;

import javafx.scene.image.Image;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.base.FxTransform;
import sugarcube.common.ui.fx.controls.FxImageView;
import sugarcube.common.ui.fx.shapes.FxRect;
import sugarcube.formats.ocd.objects.OCDImage;

public class FxOCDImage extends FxOCDNode<OCDImage>
{
    public FxImageView view;
//    public FxMediaView media;
    public FxRect frame;
    public FxTransform transform = new FxTransform();

    public FxOCDImage(final FxPager pager, final OCDImage ocdImage, String... styles)
    {
        super(pager, ocdImage, styles);
        this.init();
    }

    protected void init()
    {
        Image image = node.fxImage();
        if (image != null)
        {
            add(view = new FxImageView(image));
            transform.update(node.transform());
            view.getTransforms().add(transform);
            view.setFilename(node.filename());
        } else
        {
            add(frame = node.bounds().fx().paint(Color3.GLASS, Color3.ORANGE, 2).mouseTransparent());
        }
    }

    @Override
    public Rectangle3 bounds()
    {
        return node.bounds();
    }

    public int imageWidth()
    {
        return node == null ? 0 : node.width();
    }

    public int imageHeight()
    {
        return node == null ? 0 : node.height();
    }

    @Override
    public FxOCDImage refresh()
    {

        setOpacity(node.zOrder < 0 ? 0.1 : 1);
        clip(node.fxClip());

        transform.update(node.transform());

        boxing();
        return this;
    }

}
