package sugarcube.common.ui.fx;

import javafx.scene.image.WritableImage;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import sugarcube.common.system.Prefs;
import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.base.FxApp;
import sugarcube.common.ui.fx.base.FxScene;
import sugarcube.common.ui.fx.containers.FxStackPane;
import sugarcube.common.ui.fx.dialogs.FxWindow;
import sugarcube.common.ui.fx.dnd.FileDnD;
import sugarcube.common.ui.fx.dnd.FileDroppable;
import sugarcube.common.ui.fx.event.*;
import sugarcube.insight.core.IS;

public class FxRootPane extends FxStackPane implements FxInterface.Resizable, FileDroppable, FxEventHandler
{
    protected FxApp app;
    protected FxWindow wnd;
    protected String title;
    protected int minWidth = 50;
    protected int minHeight = 50;
    public Prefs prefs;

    public FxRootPane(String title)
    {
        this.title = title;
        this.prefs = Prefs.Get(this);
    }

    public FxRootPane(String title, int minWidth, int minHeight)
    {
        this.title = title;
        this.prefs = Prefs.Get(this);
        this.minWidth = minWidth;
        this.minHeight = minHeight;
    }


    public FxRootPane darky(boolean insight)
    {
        IS.DarkPane(this, insight);
        return this;
    }

    public void loadFX()
    {

    }

    public void loadFX(FxScene scene)
    {

    }



    public FxApp app()
    {
        return app;
    }

    @Override
    public void fileDropped(FileDnD dnd)
    {
    }

    public void dnd()
    {
        FileDnD.Handle(scene(), this);
    }

    public FxScene scene()
    {
        return app != null ? app.scene() : (wnd != null ? wnd.scene() : null);
    }

    public Stage window()
    {
        return app != null ? app.window() : (wnd != null ? wnd.stage() : null);
    }

    public FxRootPane title(String title)
    {
        this.title = title;
        this.window().setTitle(title);
        return this;
    }

    public FxRootPane toggleFullscreen()
    {
        window().setFullScreen(!window().isFullScreen());
        return this;
    }

    public void refresh()
    {

    }

    public FxRootPane setWindowPos(double x, double y)
    {
        return setWindowPos(new Point3(x, y));
    }

    public FxRootPane setWindowPos(Point3 p)
    {
        if (app != null)
            app.position(p);
        else if (wnd != null)
            wnd.position(p);
        return this;
    }

    public FxWindow openWindow(Window owner, boolean modal)
    {
        return openWindow(owner, modal, true);
    }

    public FxWindow openWindow(Window owner, boolean modal, boolean doShow)
    {
        return openWindow(owner, modal, doShow, -1, -1, true);
    }

    public FxWindow openWindow(Window owner, boolean modal, boolean doShow, int width, int height, boolean resizable)
    {
        if (wnd == null)
        {
            wnd = FxWindow.Get(false);
            Rectangle3 box = width > 0 && height > 0 ? new Rectangle3(-1, -1, width, height) : prefs.windowBox(minWidth, minHeight);
            FxScene scene = wnd.root(this, title, true, box.width, box.height, owner, true, true);
            wnd.minSize(minWidth, minHeight).setOnClose(() -> onClose());

            boolean isPosValid = false;
            for (Screen screen : Screen.getScreens())
                if (screen.getBounds().contains(box.x, box.y))
                    isPosValid = true;

            if (isPosValid && (box.x != -1 || box.y != -1))
                wnd.position(box.x, box.y);

            loadFX(scene);

            scene.addResizeListener(this);
            refresh();

            wnd.sizeToScene();
            wnd.setResizable(resizable);
            if (doShow)
            {
                wnd.show();
                wnd.checkOversize();
            }
        }
        return wnd;
    }

    public FxApp launchFX(String... args)
    {
        return launchFX(null, args);
    }

    public FxApp launchFX(Runnable fxRunnable, String... args)
    {
        return launchFX(null, fxRunnable, args);
    }

    public FxApp launchFX(FxApp fxApp, Runnable fxRunnable, String... args)
    {
        this.app = fxApp == null ? FxApp.Launch(args) : fxApp;
        Fx.Run(() ->
        {
            Rectangle3 box = prefs.windowBox(minWidth, minHeight);
            FxScene scene = app.root(this, title, true, box.width, box.height);
            app.minSize(minWidth, minHeight).setOnClose(() -> onClose());

            if (box.x != -1 || box.y != -1)
                app.position(box.x, box.y);

            loadFX(scene);

            scene.addResizeListener(this);

            refresh();
            app.sizeToScene();
            app.show();
            app.checkWindowOversize();
            if (fxRunnable != null)
                fxRunnable.run();
        });
        return app;
    }

    public void closeApp()
    {
        if(app!=null)
            app.close();
    }

    public FxRootPane showWindow(boolean doShow)
    {
        if (wnd != null)
            wnd.show(doShow);
        else if (app != null)
            app.showWindow(doShow);
        return this;
    }

    public boolean checkWindowOversize()
    {
        if (wnd != null)
            return wnd.checkOversize();
        else if (app != null)
            return app.checkWindowOversize();
        return false;
    }

    public boolean isShowingInWindow()
    {
        return wnd != null && wnd.isShowing() || app != null && app.isWindowShowing();
    }

    public void onClose()
    {
        Rectangle3 box = new Rectangle3();
        box.setXY(app == null ? wnd.position() : app.windowPosition());
        box.setDimension(scene().getWidth(), scene().getHeight());
        prefs.putWindowBox(box);
        Log.debug(this, ".onClose");
    }

    public void fullscreen(boolean enable)
    {
        Stage stage = window();
        if (stage != null)
            stage.setFullScreen(enable);
    }

    public WritableImage capture()
    {
        return Fx.CaptureImage(this);
    }


    public void keyEvent(FxKeyboard kb)
    {
//    Log.debug(this, ".keyEvent - " + kb);
        if (kb.isUp())
        {
            switch (kb.getCode())
            {
                case F10:
                    refresh();
                    break;
                case F11:
                    this.fullscreen(!window().isFullScreen());
                    break;
                case F12:
                    Fx.ShowScenicViewIfAvailable(scene());
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public void mouseEvent(FxMouse ms)
    {

    }

    @Override
    public void inputEvent(FxInput in)
    {

    }

    @Override
    public void scrollEvent(FxScroll sc)
    {

    }

    @Override
    public void contextEvent(FxContext ctx)
    {

    }

    @Override
    public void resized(int width, int height)
    {

    }

}
