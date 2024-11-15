package sugarcube.insight.side.pages;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.StringMap;
import sugarcube.common.graphics.Color3;
import sugarcube.common.interfaces.Widthable;
import sugarcube.common.system.io.IO;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.containers.FxFlowPane;
import sugarcube.common.ui.fx.containers.FxScrollPane;
import sugarcube.common.ui.fx.containers.FxVBox;
import sugarcube.common.ui.fx.event.FxContext;
import sugarcube.common.ui.fx.event.FxHandle;
import sugarcube.common.ui.fx.event.FxScroll;
import sugarcube.common.ui.fx.menus.FxPopup;
import sugarcube.insight.core.FxEnvironment;
import sugarcube.insight.core.IS;
import sugarcube.insight.side.InsightSide;
import sugarcube.formats.ocd.objects.OCDPage;
import sugarcube.formats.pdf.resources.icons.Icon;

import java.io.InputStream;

public class ThumbSide extends InsightSide
{
    private FxPopup popup;
    private FxScrollPane scroll;
    private FxFlowPane flow;
    private int size = 100;
    private int skip = 0;
    private StringMap<Widthable> thumbs = new StringMap<>();

    public ThumbSide(FxEnvironment env)
    {
        super(env, env.insight.config.sidePagesTitle, null);
        this.scroll = new FxScrollPane();
        this.flow = new FxFlowPane();
        this.scroll.setContent(flow);
        this.scroll.setFitToWidth(true);
        this.scroll.setFitToHeight(true);
        this.flow.setAlignment(Pos.TOP_CENTER);
        this.flow.gap(12, 0);
        this.setContent(scroll);

        flow.setStyle(IS.DARK_BG_STYLE + IS.THUMB_PAD_STYLE);
        flow.setOnScroll(scroll(flow, FxScroll.SCROLL));
        flow.setOnScrollStarted(scroll(flow, FxScroll.STARTED));
        flow.setOnScrollFinished(scroll(flow, FxScroll.FINISHED));
    }

    private EventHandler<ScrollEvent> scroll(final Node source, final String state)
    {
        return e -> scrollEvent(new FxScroll(e, state, source));
    }

    public void scrollEvent(FxScroll sc)
    {
        if (sc.event().isControlDown())
        {
            size += sc.event().getDeltaY() > 0 ? 10 : -10;
            size = (size < 50 ? 50 : (size > 200 ? 200 : size));
            Fx.Run(() ->
            {
                for (Widthable thumb : thumbs)
                    thumb.width(size);
            });
        }
    }

    public ThumbSide clear()
    {
        this.flow.getChildren().clear();
        this.thumbs.clear();
        return this;
    }

    @Override
    public void prefWidth(int width)
    {
        super.prefWidth(width);
        this.scroll.setPrefWidth(width);
        this.flow.setPrefWidth(width);
    }

    public ThumbSide skip(int skip)
    {
        this.skip = skip;
        return this;
    }

    public void update()
    {
        Log.debug(this, ".update");

        // this.flow.add(new InterThumb(this, null, size));

        int nbOfPages = env.ocd.nbOfPages();

        if (nbOfPages > 1000000)
            skip = 1000;
        else if (nbOfPages > 100000)
            skip = 250;
        else if (nbOfPages > 50000)
            skip = 100;
        else if (nbOfPages > 25000)
            skip = 50;
        else if (nbOfPages > 10000)
            skip = 25;
        else if (nbOfPages > 5000)
            skip = 10;
        else if (nbOfPages > 1000)
            skip = 5;

        this.clear();

        for (OCDPage page : env.ocd.pageHandler)
        {
            int nb = page.number();
            if (skip == 0 || skip == 1 || nb == 1 || nb % skip == 0)
            {
                Fx.Run(() -> this.add(page, env.ocd.thumbHandler.need(page).stream()));
            }
        }
    }

    private Image add(OCDPage page, InputStream stream)
    {
        String filename = page.entryFilename();
        if (thumbs.has(filename))
            return null;

        Image image = new Image(stream);
        IO.Close(stream);
        FxVBox box = new FxVBox(4);

        final PageThumb pageThumb = new PageThumb(page.number(), filename, image, Color3.ANTHRACITE, size);
        box.add(pageThumb);
        thumbs.put(filename, pageThumb);
        FxHandle handle = FxHandle.Get(pageThumb);
        handle.primary(ms -> env.updatePage(filename));
        handle.popup(ctx -> popup(ctx, filename));
        InterThumb interThumb = new InterThumb(this, filename, size);
        thumbs.put(filename + "-inter", interThumb);
        box.add(interThumb);

        this.flow.add(box);
        return image;
    }

    public void popup(FxContext ctx, String filename)
    {
        if (ctx.isConsumed())
            return;

        if (popup == null)
            this.popup = new FxPopup();

        this.popup.clear();

        OCDPage page = env.ocd.pageHandler.get(filename);
        final int pageNb = page == null ? 0 : page.number();

        this.popup.item(" Insert Before ", Icon.Get(Icon.ARROW_UP, 16)).act(e -> env.insertPage(pageNb));
        this.popup.item(" Insert After ", Icon.Get(Icon.ARROW_DOWN, 16)).act(e -> env.insertPage(pageNb + 1));
        this.popup.separator();
        this.popup.item(" Delete ", Icon.Get(Icon.TRASH, 16)).act(e -> env.deletePage(filename));
//        this.popup.item(" Delete Other ", Icon.Get(Icon.TRASH_ALT, 16)).act(e -> env.deleteAllPagesExcept(filename));

        this.popup.show(env.gui.board.canvas, ctx.screenXY());
    }

    public void move(String filename, String anchor)
    {
        if (Zen.equals(filename, anchor))
            return;
        env.ocd.pageHandler.move(filename, anchor);
        env.updatePage();
        env.updateLeftSide();
    }

}
