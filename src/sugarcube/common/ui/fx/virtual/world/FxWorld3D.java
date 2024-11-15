package sugarcube.common.ui.fx.virtual.world;

import javafx.beans.value.ObservableNumberValue;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.FxRootPane;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.base.FxApp;
import sugarcube.common.ui.fx.base.FxGroup;
import sugarcube.common.ui.fx.base.FxSubScene;
import sugarcube.common.ui.fx.beans.PInteger;
import sugarcube.common.ui.fx.event.*;
import sugarcube.common.ui.fx.transform.FxCamera;
import sugarcube.common.ui.fx.virtual.FxAmbient;
import sugarcube.common.ui.fx.virtual.FxAxes;
import sugarcube.common.ui.fx.virtual.FxLight;
import sugarcube.common.ui.fx.virtual.FxMouse3D;

public class FxWorld3D extends FxGroup implements FxEventHandler
{

    public static double EPSILON = 0.001;
    // private static String PATH = "E:/copelab/fx/";

    protected FxSubScene subScene;
    protected FxMouse3D mouse = new FxMouse3D();
    protected FxAmbient ambiantLight = new FxAmbient().color(Color3.WHITE.darker());
    protected FxLight pointLight = new FxLight().pos(0, 10, 0).color(Color3.WHITE.darker(3));
    protected FxCamera camera = new FxCamera().nearFarClips(0.001, 10000).yUp();
    protected FxAxes cameraTarget = new FxAxes(0.1,0.01).hide();
    protected FxGrid3D grid = new FxGrid3D(EPSILON, 0.005, 25, 1);
    protected FxAxes axes = new FxAxes(1, 0.02).hide();

    public FxWorld3D(int width, int height)
    {
        this.subScene = new FxSubScene(this, width, height, true);
        subScene.setFill(Color3.ANTHRACITE.darker().darker().fx());
        subScene.setCamera(camera);
        this.add(ambiantLight, pointLight, grid, axes, cameraTarget);
        this.reset();
    }

    public FxGrid3D grid()
    {
        return grid;
    }

    public FxCamera camera()
    {
        return camera;
    }

    public FxWorld3D reset()
    {
        this.camera.fov(40).target(0, 1, 0).pos(7, 5.5, -5.0);
        return this;
    }

    public FxWorld3D bindSize(Pane parent, ObservableNumberValue dw, ObservableNumberValue dh)
    {
        subScene.bindSize(parent, dw, dh);
        return this;
    }

    public FxSubScene subScene()
    {
        return subScene;
    }

    public void keyEvent(KeyEvent e)
    {
        camera.keyEvent(e);
    }

    public void mouseDragged(MouseEvent e)
    {
        camera.onMouseDragged(e);
        axes.show();
        cameraTarget.show().setXYZ(camera.target());
    }

    public void mouseMoved(MouseEvent e)
    {
        camera.onMouseMoved(e);
    }

    public void mouseUp(MouseEvent e)
    {
        axes.hide();
        cameraTarget.hide();
    }

    public void scroll(ScrollEvent e)
    {
        camera.onScroll(e);
    }

    @Override
    public void mouseEvent(FxMouse ms)
    {
        switch (ms.state())
        {
            case FxMouse.DRAG:
                mouseDragged(ms.event());
                break;
            case FxMouse.MOVE:
                mouseMoved(ms.event());
                break;
            case FxMouse.UP:
                mouseUp(ms.event());
                break;
        }
    }


    @Override
    public void scrollEvent(FxScroll sc)
    {
        if (sc.isState(FxScroll.SCROLL))
            scroll(sc.event());
    }


    @Override
    public void keyEvent(FxKeyboard kb)
    {

    }

    @Override
    public void inputEvent(FxInput in)
    {

    }


    @Override
    public void contextEvent(FxContext ctx)
    {

    }

    public static void main(String... args)
    {
        FxRootPane pane = new FxRootPane("Sugarcube's 3D World");
        FxApp app = pane.launchFX();

        Fx.Run(() ->
        {
            FxWorld3D world = new FxWorld3D(600, 400);
            pane.add(world.subScene);
            world.bindSize(pane, PInteger.New(), PInteger.New());
            app.scene().handleEvents(world);
        });

    }

}
