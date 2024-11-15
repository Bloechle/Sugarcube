package sugarcube.insight.ribbon.video;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.geom.Line3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.system.io.File3;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.controls.FxToggles;
import sugarcube.common.ui.fx.dnd.FileDnD;
import sugarcube.common.ui.fx.event.FxInput;
import sugarcube.common.ui.fx.event.FxKeyboard;
import sugarcube.common.ui.fx.event.FxMouse;
import sugarcube.insight.Insight;
import sugarcube.insight.core.FxEnvironment;
import sugarcube.insight.core.FxRibbon;
import sugarcube.insight.interfaces.FxRibbonLoader;
import sugarcube.insight.render.FxOCDNode;
import sugarcube.insight.ribbon.toolbox.actions.DeleteAct;
import sugarcube.insight.ribbon.video.render.VideoImage;
import sugarcube.insight.ribbon.video.render.VideoPager;
import sugarcube.formats.ocd.objects.OCDImage;
import sugarcube.formats.ocd.objects.OCDPage;
import sugarcube.formats.pdf.resources.icons.Icon;

public class VideoRibbon extends FxRibbon
{
    public static FxRibbonLoader LOADER = env -> new VideoRibbon(env);

    public @FXML Button openBt, saveBt, localBt, backBt;
    public @FXML TextField xField, yField, wField, hField;
    public @FXML ToggleButton videoBt;
    public @FXML Button playBt, pauseBt;

    public FxToggles insertToggles;

    public VideoRibbon(final FxEnvironment env)
    {
        super(env, "Video Content");
        this.rightSideSize = 300;
    }

    @Override
    public void init()
    {
        super.init(openBt, saveBt, localBt, backBt);
        super.init(xField, yField, wField, hField);

        insertToggles = FxToggles.Handle(videoBt, bt -> startInsertVideo());

        Icon.FILE_MOVIE_ALT.set(videoBt, iconSize, 100, "", Color3.DUST_WHITE);
        Icon.PLAY.set(playBt, iconSize, 100, "", e -> doPlay(true));
        Icon.PAUSE.set(pauseBt, iconSize, 100, "", e -> doPlay(false));

    }

    public void doPlay(boolean play)
    {
        for (VideoImage fx : pager().videos())
        {
            if (play)
                fx.mediaView.media.play();
            else
                fx.mediaView.media.stop();
        }
    }

    @Override
    public VideoPager pager()
    {
        return pager == null ? new VideoPager(this) : (VideoPager) pager;
    }

    @Override
    public boolean doHandleClick(FxOCDNode node)
    {
        boolean handleClick = !insertToggles.isSelected() && super.doHandleClick(node);
        Log.debug(this, ".doHandleClick - " + handleClick);
        return handleClick;
    }

    public void startInsertVideo()
    {
        this.pager.board.restyle("cursor-crosshair");
        this.pager.stopInteract();
    }

    @Override
    public void fileDropped(FileDnD dnd)
    {
        File3[] images = dnd.files("mp4", "mp3");
        if (images.length > 0)
            insertVideo(null, images);
        this.update();
    }

    public boolean boardKeyEvent(FxKeyboard kb)
    {
        super.boardKeyEvent(kb);

        if (kb.isUp())
        {
            if (!kb.isControlDown())
                switch (kb.getCode())
                {
                    case DELETE:
                        DeleteAct.Delete(this);
                        break;
                    default:
                        break;
                }
        }
        return false;
    }

    public boolean boardMouseEvent(FxMouse ms)
    {
        // catches all mouse event from board by filtering (not handling)
        super.boardMouseEvent(ms);
        if (ms.isConsumed())
            return true;

        if (ms.isUp())
        {
            ToggleButton toggle = insertToggles.selected();
            if (toggle != null)
            {
                if (toggle == videoBt)
                    insertVideo();
                insertToggles.deselect();
                this.pager.board.restyle();
            }
        }

        return false;
    }

    public void insertVideo()
    {
        insertVideo(pager.interactor.activeExtent(), Fx.Chooser("Select Video").dir(ocd().fileDirectory()).open(window()));
    }

    public void insertVideo(Line3 extent, File3... files)
    {
        OCDPage page = pager.page;

        if (page == null || !File3.HasFile(files))
            return;

        if (extent == null)
            extent = pager.interactorExtent(true);

        OCDImage image = null;
        for (File3 file : files)
            if (file.exists())
            {
                image = page.content().newImage();
                image.setFromFile(file, extent);
                page.document().imageHandler.addEntry(image);
            }

        this.update();
        pager.pleaseInteract(image);
    }

    @Override
    public boolean boardPopup(FxInput in)
    {
        Log.debug(this, ".boardPopup - context");
        this.popup.clear();

        popup.sep();

        this.popup.show(env.gui.board.canvas, in.screenXY());
        return true;
    }

    @Override
    public void dispose()
    {

    }

    public static void main(String... args)
    {
        Insight.main();
    }

}
