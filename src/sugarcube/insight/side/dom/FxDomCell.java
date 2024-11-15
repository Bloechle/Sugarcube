package sugarcube.insight.side.dom;

import javafx.scene.control.TreeCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.Color3;
import sugarcube.insight.core.IS;
import sugarcube.insight.render.FxOCDNode;
import sugarcube.formats.ocd.objects.OCDGroup;
import sugarcube.formats.ocd.objects.OCDNode;
import sugarcube.formats.pdf.resources.icons.Icon;

public class FxDomCell extends TreeCell<OCDNode>
{
    public static Color3 WHITE = IS.WHITE_DUST;
    private FxDomTree tree;

    public FxDomCell(FxDomTree tree)
    {
        this.tree = tree;
        this.setStyle(IS.DARK_BG_STYLE + IS.DUST_TEXT_STYLE);

        this.setOnMouseEntered(e -> onMouseOver(true));
        this.setOnMouseExited(e -> onMouseOver(false));

        this.setOnDragDetected(e ->
        {
            OCDNode node = getItem();
            if (node == null)
                return;
            Dragboard db = startDragAndDrop(TransferMode.MOVE);
            // db.setDragView(this.getImage());

            ClipboardContent content = new ClipboardContent();
            content.putString(node.needID());
            db.setContent(content);
            db.setDragView(this.snapshot(null, null));
            e.consume();
        });

        this.setOnDragDone(e ->
        {
            if (e.getTransferMode() == TransferMode.MOVE)
            {
                // nope
            }
            e.consume();
        });

        this.setOnDragOver(e ->
        {
            if (e.getGestureSource() != this)
            {

                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });

        this.setOnDragEntered(e ->
        {
            setOpacity(0.5);
            e.consume();
        });

        this.setOnDragExited(e ->
        {
            setOpacity(1);
            e.consume();
        });

        this.setOnDragDropped(e ->
        {
            Dragboard db = e.getDragboard();
            if (db.hasString())
            {
                e.setDropCompleted(true);
                tree.pane.group(node());

                Log.debug(this, ".setOnDragDropped - " + this);

                // side.move(db.getString(), filename);
            } else
                e.setDropCompleted(false);
            e.consume();
        });
    }

    public void onMouseOver(boolean over)
    {
        this.setOpacity(over ? 0.8 : 1);
        this.tree.pane.onMouseOver(this, over);
    }

    public OCDNode node()
    {
        return this.getItem();
    }

    public FxOCDNode nodeFx()
    {
        return tree.pane.env().gui.board.fxNode(getItem());
    }

    private String string()
    {
        OCDNode item = node();
        String id = item.id();
        return item == null ? "" : (id == null ? "" : id + " - ") + item.sticker();
    }

    @Override
    protected void updateItem(OCDNode item, boolean empty)
    {
        super.updateItem(item, empty);
        if (empty)
        {
            this.setText(null);
            this.setGraphic(null);
        } else if (item != null)
        {
            refresh();
        }
    }

    public void refresh()
    {
        OCDNode node = this.node();
        if (node == null)
            return;

        this.setText("  " + string());
        this.setTextFill(this.isSelected() ? Color.YELLOW : Color.WHITE);

        switch (node.tag)
        {
            case "page":
                icon(Icon.FILE_TEXT_ALT, 18);
                break;
            case "properties":
                icon(Icon.COGS, 18);
                break;
            case "annotations":
                icon(Icon.PENCIL_SQUARE_ALT, 18);
                break;
            case "definitions":
                icon(Icon.PAPERCLIP, 18);
                break;
            case "content":
                icon(Icon.CODE, 18);
                break;
            case "image":
                icon(Icon.IMAGE, 14);
                break;
            case "path":
                icon(Icon.PAINT_BRUSH, 14);
                break;
            case "text":
                icon(Icon.FONT, 14);
                break;
            case "g":
                String type = ((OCDGroup) node).type();
                if (type != null)
                    switch (type)
                    {
                        case "paragraph":
                            icon(Icon.BARS, 14);
                            break;
                        case "tline":
                            icon(Icon.MINUS, 14);
                            break;
                        default:
                            icon(Icon.OBJECT_GROUP, 10);
                            break;
                    }
                else
                    icon(Icon.CIRCLE, 10);
                break;
            default:
                icon(Icon.CIRCLE, 10);
                break;
        }

    }

    public void icon(Icon icon, int size)
    {
        icon(icon, size, WHITE);
    }

    public void icon(Icon icon, int size, Color3 color)
    {
        this.setGraphic(Icon.Awesome(icon, size, color));
    }

}
