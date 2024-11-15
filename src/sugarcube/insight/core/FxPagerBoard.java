package sugarcube.insight.core;

import javafx.scene.Node;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Map3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.interfaces.Visitor;
import sugarcube.common.ui.fx.FxBoard;
import sugarcube.common.ui.fx.base.FxGroup;
import sugarcube.common.ui.fx.event.FxMouse;
import sugarcube.insight.render.FxOCDNode;
import sugarcube.insight.render.FxPager;
import sugarcube.formats.ocd.objects.OCDNode;
import sugarcube.formats.ocd.objects.OCDPage;

public class FxPagerBoard extends FxBoard
{
    private FxPager pager;
    private FxEnvironment env;

    public FxMouse ms = null;

    public Transform3 tm = new Transform3();

    public FxGroup page = new FxGroup().style("ocd-page");
    // background is the document background heavy rendering surface, if any
    // (update)
    public FxGroup background = new FxGroup().style("layer-background");
    // content is the document heavy rendering surface (redrawn on update)
    public FxGroup content = new FxGroup().style("layer-content");
    // annots is the interactive purpose light rendering surface (redrawn on
    // refresh)
    public FxGroup annots = new FxGroup().style("layer-annots");
    // meta is a transient utility layer (dialogs such as Modeler use it)
    public FxGroup meta = new FxGroup().style("layer-meta");
    // glass is a very light ongoing action rendering surface
    public FxGroup glass = new FxGroup().style("layer-glass");

    public Map3<String, FxGroup> metaMap = new Map3<>();

    public FxPagerBoard(FxEnvironment env)
    {
        this.env = env;

        if (env.insight.config.whiteBackground)
            whiteBackground();

        page.add(background, content, annots, meta, glass);
        set(page);
    }

    public void setPager(FxPager pager)
    {
        this.pager = pager;
        this.clearMeta(true);
        this.pager.init();
    }

    public void restyle(String... styles)
    {
        this.canvas.getStyleClass().clear();
        if (styles.length > 0)
            this.canvas.getStyleClass().addAll(styles);
    }

    public void reset(String... styles)
    {
        this.restyle(styles);
        this.background.clear();
        this.content.clear();
        this.annots.clear();
        for (FxGroup g : metaMap)
            g.clear();
        this.glass.clear();
    }

    public FxOCDNode addContentNode(FxOCDNode node)
    {
        if (node != null)
            content.add(node);
        return node;
    }

    public FxGroup metaLayer(String name)
    {
        FxGroup g = metaMap.get(name);
        if (g == null)
        {
            Log.debug(this, ".metaLayer - new layer: " + name);
            metaMap.put(name, g = new FxGroup().style("layer-" + name));
            meta.add(g);
        }
        return g;
    }

    public void clearMeta(boolean deleteLayers)
    {
        if (deleteLayers)
        {
            metaMap.clear();
            meta.clear();
        } else
            for (FxGroup group : metaMap.values())
                group.clear();
    }

    public FxGroup metaLayer(String name, FxGroup g)
    {
        if (metaMap.get(name) == null)
        {
            metaMap.put(name, g);
            meta.add(g);
        }
        return g;
    }

    public FxGroup removeMetaLayer(String name)
    {
        FxGroup g = metaMap.remove(name);
        if (g != null)
            meta.remove(g);
        return g;
    }

    public FxOCDNode fxNode(OCDNode node)
    {
        return node == null ? null : visitContent(fx -> fx.node == node);
    }

    public FxOCDNode visitContent(Visitor<FxOCDNode> visitor)
    {
        return visit(content, visitor);
    }

    public FxOCDNode visit(FxGroup group, Visitor<FxOCDNode> visitor)
    {
        FxOCDNode fx;
        for (Node node : group.getChildren())
            if ((fx = FxOCDNode.Cast(node)) != null && (fx = fx.visit(visitor)) != null)
                return fx;
        return null;
    }

    public FxOCDNode refresh(OCDNode node)
    {
        return refresh(fxNode(node));
    }

    public FxOCDNode refresh(FxOCDNode node)
    {
        if (node != null)
            node.refresh();
        return node;
    }

    public void resizeAndRescale(OCDPage ocdPage)
    {
        Rectangle3 box = (ocdPage == null ? new Rectangle3(0, 0, 595, 842) : ocdPage.viewBounds());
//    Log.debug(this, ".resizeAndRescale - page="+(page==null ? null : (page.number()+" "+page.viewBox().toString())));
        resize(box.dimension());
        tm = ocdPage == null ? new Transform3() : ocdPage.viewTransform(true);
        page.translateAndScale(-box.x, -box.y, tm.scaleX(), tm.scaleY());
    }

}
