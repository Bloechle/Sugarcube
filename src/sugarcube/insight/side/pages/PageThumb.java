package sugarcube.insight.side.pages;

import javafx.geometry.Bounds;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.containers.FxAnchorPane;
import sugarcube.common.ui.fx.controls.FxImageView;
import sugarcube.common.ui.fx.shapes.FxRect;

public class PageThumb extends FxAnchorPane
{
    private String filename;
    private FxImageView imageView;
    private FxRect rect;

    public PageThumb(int nb, String filename, Image image, Color3 color, int width)
    {
        style("page-thumb");
        this.filename = filename;

        imageView = new FxImageView(image, true, true, true);
        Bounds box = imageView.getLayoutBounds();
        rect = new FxRect(0, 0, box.getWidth(), box.getHeight()).frame().stroke(Color3.BLACK.alpha(0.5), 1.0);
        width(width);
        add(imageView);

        Tooltip.install(imageView, new Tooltip("Page " + nb+" - "+filename.replace(".xml", "")));

        add(rect);

        setMouseOverEffect(new DropShadow(10, Color.WHITE));

        setOnDragDetected(e ->
        {
            Dragboard db = startDragAndDrop(TransferMode.MOVE);
            db.setDragView(imageView.getImage());
            ClipboardContent content = new ClipboardContent();
            content.putString(filename);
            db.setContent(content);
            e.consume();
        });

        setOnDragDone(e ->
        {
            if (e.getTransferMode() == TransferMode.MOVE)
            {
                // nope
            }
            e.consume();
        });
    }

    @Override
    public void width(double width)
    {
        super.width(width);
        imageView.setFitWidth(width);
        Bounds box = imageView.getLayoutBounds();
        rect.setWidth(box.getWidth());
        rect.setHeight(box.getHeight());
    }
}
