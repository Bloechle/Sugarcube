package sugarcube.common.ui.fx.base;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sugarcube.common.system.log.Log;
import sugarcube.common.system.log.Logger.Level;
import sugarcube.common.system.util.Sys;
import sugarcube.common.data.collections.Str;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Image3;
import sugarcube.common.interfaces.OnClose;
import sugarcube.common.system.io.Class3;
import sugarcube.common.system.process.Arguments;
import sugarcube.common.ui.fx.dialogs.FxWindow;
import sugarcube.common.ui.fx.dnd.FileDnD;
import sugarcube.common.ui.fx.dnd.FileDroppable;
import sugarcube.common.ui.fx.skin.RS_Skin;

import java.awt.*;
import java.awt.geom.Point2D;
import java.net.URL;

public class FxApp extends Application
{
    protected static Thread FX_THREAD = null;
    protected static FxApp LAUNCH = null;
    protected Stage window;
    protected FxScene scene;
    protected String title;
    protected TrayIcon trayIcon;
    protected boolean createTrayIcon = false;
    protected OnClose onClose = null;
    protected int startWidth = -1;
    protected int startHeight = -1;
    protected Point3 startPos = null;

    public FxApp()
    {
    }

    @Override
    public void init()
    {
        // not called by the FX thread (Launcher thread)
    }

    @Override
    public void start(Stage window) throws Exception
    {
        FX_THREAD = Thread.currentThread();
        this.window = window;
        this.window.setOnCloseRequest(e -> dispose());
        LAUNCH = this;
        iconS3();

        Parent root = fxInit();
        position(startPos);

        if (scene == null && startWidth > -1 && startHeight > 1)
            this.root(root, startWidth, startHeight);

        if (scene != null)
            window.sizeToScene();

        window.toFront();
    }

    public Parent fxInit()
    {
        return null;
    }

    public Pane load()
    {
        Parent pane = Fx.Fxml(this);
        if (pane != null)
            pane.getStyleClass().add("sc-app");
        return pane == null ? null : (Pane) pane;
    }

    public FxApp iconS3()
    {
        return icon(new Image(Class3.Stream(RS_Skin.class, "sugarcube.png")));
    }

    public FxApp icon(Image icon)
    {
        window.getIcons().setAll(icon);
        return this;
    }

    public FxApp minSize(int w, int h)
    {
        window.setMinWidth(w);
        window.setMinHeight(h);
        return this;
    }

    public FxScene root(Parent root)
    {
        window.setScene(scene = new FxScene(root));
        return scene;
    }

    public FxScene root(Parent root, double w, double h)
    {
        window.setScene(scene = new FxScene(root, w, h));
        return scene;
    }

    public FxScene root(Parent root, String title, boolean isResizable, double w, double h)
    {
        this.title(title);
        this.resizable(isResizable);
        return root(root, w, h);
    }

    public FxApp show()
    {
        window.show();
        return this;
    }

    public FxApp setOnClose(OnClose listener)
    {
        this.onClose = listener;
        return this;
    }

    public void dispose()
    {
        Log.debug(this, ".dispose - onClose: " + onClose);
        if (onClose != null)
            onClose.onClose();
    }

    public void pleaseShow()
    {
        Fx.Run(() ->
        {
            window.sizeToScene();
            window.toFront();
            window.show();
        });
    }

    public boolean checkWindowOversize()
    {
        return FxWindow.CheckOversize(window);
    }

    public void createTrayIcon()
    {
        if (SystemTray.isSupported())
        {
            Platform.setImplicitExit(false);
            SystemTray tray = SystemTray.getSystemTray();
            Image3 image = Image3.read(Class3.Stream(RS_Skin.class, "sugarcube.png")).decimate(0.5);
            window.setOnCloseRequest(e -> hide(window));
            // create a popup menu
            PopupMenu popup = new PopupMenu();
            MenuItem showItem = new MenuItem("Show");
            showItem.addActionListener(e -> pleaseShow());
            popup.add(showItem);
            MenuItem closeItem = new MenuItem("Close");
            closeItem.addActionListener(e -> System.exit(0));
            popup.add(closeItem);
            trayIcon = new TrayIcon(image, "sugarcube IT", popup);
            trayIcon.addActionListener(e -> pleaseShow());
            try
            {
                tray.add(trayIcon);
            } catch (AWTException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void showProgramIsMinimizedMsg()
    {
        if (createTrayIcon)
        {
            trayIcon.displayMessage("sugarcube IT", "Running in background...", TrayIcon.MessageType.INFO);
            createTrayIcon = false;
        }
    }

    public FxApp showWindow(boolean doShow)
    {
        if (doShow)
            this.show();
        else
            this.hide();
        return this;
    }

    public void hide()
    {
        hide(window);
    }

    private void hide(final Stage stage)
    {
        Fx.Run(() ->
        {
            stage.hide();
            if (SystemTray.isSupported())
                showProgramIsMinimizedMsg();
        });
    }

    public void close()
    {
        Log.debug(this, ".close");
        Fx.Run(() ->
        {
            window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
            // Platform.runLater(() -> System.exit(0));
        });
    }

    public void stop()
    {
        Log.debug(this, ".stop");
    }

    public URL resource(String name)
    {
        return this.getClass().getResource(name);
    }

    public FxApp title(String title)
    {
        this.title = title;
        if (window != null && Str.HasData(title))
            window.setTitle(title);
        return this;
    }

    public FxApp resizable(boolean isResizable, int startWidth, int startHeight)
    {
        this.resizable(isResizable);
        this.startWidth = startWidth;
        this.startHeight = startHeight;
        return this;
    }

    public FxApp resizable(boolean isResizable)
    {
        this.window.setResizable(isResizable);
        return this;
    }

    public FxApp resizeWindow(double w, double h)
    {
        if (w > 10)
            window.setWidth(w);
        if (h > 10)
            window.setHeight(h);
        return this;
    }

    public FxApp sizeToScene()
    {
        window.sizeToScene();
        return this;
    }

    public FxApp position(Point2D p)
    {
        return p == null ? this : position(p.getX(), p.getY());
    }

    public FxApp position(double x, double y)
    {
        if (x > -1)
            window.setX(x);
        if (y > -1)
            window.setY(y);
        return this;
    }

    public FxApp setWindowBounds(Rectangle3 box)
    {
        position(box.x, box.y);
        resizeWindow(box.width, box.height);
        return this;
    }

    public Point3 windowPosition()
    {
        return new Point3(window.getX(), window.getY());
    }

    public Point3 windowDimension()
    {
        return new Point3(window.getWidth(), window.getHeight());
    }

    public Rectangle3 windowBounds()
    {
        return new Rectangle3(window.getX(), window.getY(), window.getWidth(), window.getHeight());
    }

    public Stage window()
    {
        return window;
    }

    public boolean isWindowShowing()
    {
        return window != null && window.isShowing();
    }

    public FxScene scene()
    {
        return scene;
    }

    public void handleFileDnD(FileDroppable droppable)
    {
        FileDnD.Handle(scene, droppable);
    }

    public Arguments args()
    {
        return new Arguments(getParameters().getRaw().toArray(new String[0]));
    }

    public static boolean IsOnFX()
    {
        return FX_THREAD != null && Thread.currentThread() == FX_THREAD;
    }

    public synchronized static FxApp Launch(String... args)
    {
        Sys.SleepWhile(50, () -> LAUNCH != null);
        Sys.Run(() -> Launch(FxApp.class, args));
        Sys.SleepWhile(50, () -> LAUNCH == null);
        FxApp app = LAUNCH;
        LAUNCH = null;
        return app;
    }

    public static void Launch(Class<? extends Application> cls, String... args)
    {
        Launch(cls, Level.DEBUG, new Arguments(args));
    }

    public static void Launch(Class<? extends Application> cls, Arguments args)
    {
        Launch(cls, Level.DEBUG, args);
    }

    public static void Launch(Class<? extends Application> cls, Level level, String... args)
    {
        Launch(cls, level, new Arguments(args));
    }

    public static void Launch(Class<? extends Application> cls, Level level, Arguments args)
    {
        Log.setLevel(args.logLevel(level.name()));
        Application.launch(cls, args.args);
    }

}
