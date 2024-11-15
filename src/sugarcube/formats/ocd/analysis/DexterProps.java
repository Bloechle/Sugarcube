package sugarcube.formats.ocd.analysis;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Str;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.system.reflection.Bean;
import sugarcube.common.numerics.Math3;
import sugarcube.formats.ocd.objects.OCDNode;
import sugarcube.formats.ocd.objects.OCDPaintable;
import sugarcube.formats.ocd.objects.OCDText;
import sugarcube.formats.ocd.objects.OCDTextBlock;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DexterProps extends Bean
{
    public static final String TAG = "canon-props";
    //  public transient boolean imageGray = false;
    public transient double imageQuality = 0.98;// 0.0-1.0, i.e., poor to high quality
    public transient double imageDpi = 300;// 72, 144, 300, etc
    public static boolean DO_KEEP_TEXTS = true;
    public static boolean DO_KEEP_PATHS = true;
    public static boolean DO_KEEP_IMAGES = true;
    public static boolean DO_KEEP_INVISIBLE_TEXTS = false;
    public transient int steps = 100;
    public boolean ocr = false;
    public double textClipped = 0.25;
    public boolean doTrustSpaces = true;
    public boolean doCheckClips = false;
    public double textShadow = 0.7;
    public double splitIndent = 0.5;
    public double splitIndentMax = 4.0;
    public double splitInterline = 0.10;
    public double mergeToken = 0.14;// 0.08
    public double mergeLine = 0.9;
    public double mergeBlock = 1.2; // 0.8 seems to really be the best one, so do
    // not change!!!
    public double sameAngle = 0.001;
    public double sameFontsize = 0.1;
    public double sameRun = 0.10;
    public double sameBaseline = 0.20;
    public double sameLuminosity = 0.1;
    public double sameColor = 0.1;
    public boolean useIndent = true;
    public boolean useInterline = true;
    public boolean useLayout = true;
    public boolean useTreeStructure = false;

    public boolean doClusterize = true;
    public boolean doMergeItems = true;
    public boolean doMergeTokens = true;
    public boolean doMergeRuns = true;
    public boolean doCleanRaws = true;

    public String[] items =
            {"\u2022"}; //do not add '-' (also used as hyphen and subtraction)
    public String rOrder = ROrder.LEFT_RIGHT;

    public DexterProps()
    {
        this("");
    }

    public DexterProps(String name)
    {
        super(TAG);
        this.setName(name);
    }

    @Override
    public void copyTo(Bean copy)
    {
        super.copyTo(copy);
    }

    public DexterProps copy()
    {
        DexterProps bean = new DexterProps();
        this.copyTo(bean);
        return bean;
    }

    public boolean runOK(OCDText a, OCDText b)
    {
        return FontsizeOK(a, b, sameRun);
    }

    public boolean basicsOK(OCDText a, OCDText b)
    {
        return a.groupID == b.groupID && angleOK(a, b) && fontsizeOK(a, b) && (!doCheckClips || a.clipID().equals(b.clipID()));
        // && fontbaseOK(a, b);
    }

//    public boolean isShadow(OCDText shadow, OCDText text)
//    {
//        if (shadow == null || text == null)
//            return false;
//        if (textShadow < 0 || textShadow > 1)
//            return false;
//        if (!text.unicodes().equals(shadow.unicodes()))
//            return false;
//        if (!fontsizeOK(text, shadow))
//            return false;
//        return text.bounds().overlap(shadow.bounds()) >= textShadow;
//    }

    public boolean baselineOK(OCDText a, OCDText b)
    {
        return ocr ? (a.bounds().overlapY(b.bounds()) > 0.5) : Math.abs(a.y() - b.y()) < sameBaseline * Math.min(a.scaledFontsize(), b.scaledFontsize());
    }

    public boolean fontsizeOK(double a, double b)
    {
        return FontsizeOK(a, b, sameFontsize);
    }

    public boolean fontsizeOK(OCDText a, OCDText b)
    {
        return FontsizeOK(a, b, sameFontsize);
    }

    public boolean colorsOK(OCDText a, OCDText b)
    {
        boolean isOK = ocr || sameColor < 0
                || a.fillColor().manhattanDistance(b.fillColor()) < sameColor && a.strokeColor().manhattanDistance(b.strokeColor()) < sameColor;
        //Log.debug(this, ".colorsOK - "+isOK+": "+a.string()+", "+b.string()+" -> "+a.fillColor()+", "+b.fillColor());
        return isOK;
    }

    public boolean angleOK(OCDText a, OCDText b)
    {
        return Math.abs(a.canon.radians - b.canon.radians) < 0.01;
    }

    public boolean shearOK(OCDText a, OCDText b)
    {
        Transform3 atm = a.transform();
        Transform3 btm = b.transform();
        return Math.abs(atm.shearX() - btm.shearX()) < 0.1 && Math.abs(atm.shearY() - btm.shearY()) < 0.1;
    }

    public boolean scriptOK(OCDText a, OCDText b)
    {
        return Math.abs(a.scriptDx() - b.scriptDx()) < 0.001 && Math.abs(a.scriptDy() - b.scriptDy()) < 0.001
                && Math.abs(a.scriptScale() - b.scriptScale()) < 0.001 && Str.Equals(a.decoration(), b.decoration());
    }

    private boolean dist(OCDText t1, OCDText t2, double threshold)
    {
        return t2.canon.minX() - t1.canon.maxX() < threshold;
    }

    public boolean linesOK(OCDText t1, OCDText t2)
    {
        return IsGroup(t1, t2) || mergeLine <= 0 || dist(t1, t2, mergeLine * Math.min(t1.scaledFontsize(), t2.scaledFontsize()));
    }

    public boolean tokensOK(OCDText t1, OCDText t2)
    {
        return dist(t1, t2, mergeToken * Math.min(t1.canon.height(), t2.canon.height()));
    }

    public boolean coverOK(OCDNode a, OCDNode b)
    {
        return a.bounds().overlapY(b.bounds()) > 0.5;
    }

    public static boolean FontsizeOK(OCDText a, OCDText b, double th)
    {
        return FontsizeOK(a.scaledFontsize(), b.scaledFontsize(), th);
    }

    public static boolean FontsizeOK(double a, double b, double th)
    {
        return Math.abs(a - b) < th * Math.min(a, b);
    }

    public static boolean IsGroup(OCDPaintable a, OCDPaintable b)
    {
        //groupID == 0 if undef
        return a.groupID >= 0 && a.groupID == b.groupID;
    }

    public static List3<OCDTextBlock> sortBlocks(List3<OCDTextBlock> blocks)
    {
        try
        {
            Collections.sort(blocks, (n1, n2) ->
            {
                Rectangle3 r1 = n1.bounds();
                Rectangle3 r2 = n2.bounds();
                if (Math.abs(r1.y - r2.y) < Math.min(n1.fontsize(), n2.fontsize()))
                    return r1.x < r2.x ? -1 : r1.x > r2.x ? 1 : 0;
                else
                    return r1.y < r2.y ? -1 : r1.y > r2.y ? 1 : 0;
            });
        } catch (Exception e)
        {
            Log.debug(DexterProps.class, ".sortBlocks - " + e.getMessage());
        }

        return blocks;
    }

    public static void SortCX(List<? extends OCDNode> list)
    {
        Collections.sort(list, Comparator.comparingDouble(a -> a.bounds().cx()));
    }

    public static void SortOX(List<? extends OCDNode> list)
    {
        Collections.sort(list, Comparator.comparingDouble(a -> a.bounds().x()));
    }

    public static void SortInvertedOX(List<? extends OCDNode> list)
    {
        Collections.sort(list, (a, b) -> Math3.SignInvert(a.bounds().x() - b.bounds().x()));
    }

    public static void SortOY(List<? extends OCDNode> list, BoundsMap map)
    {
        if (map == null)
            Collections.sort(list, (a, b) -> Math3.Sign(a.bounds().y() - b.bounds().y()));
        else
        {
            Collections.sort(list, (a, b) -> Math3.Sign(map.box(a).y() - map.box(b).y()));
        }
    }

    public static void SortCY(List<? extends OCDNode> list)
    {
        Collections.sort(list, Comparator.comparingDouble(a -> a.bounds().cy()));
    }

    // public static void sortRefsOrder(List3<OCDPaintable> nodes, List8
    // refOrders)
    // {
    // Map8<OCDPaintable> refs = new Map8<>();
    // for (OCDPaintable node : nodes)
    // {
    // String ref = node.needRef();
    // while (refs.has(ref))
    // ref = node.newRef();
    // refs.put(ref, node);
    // }
    // nodes.clear();
    // for (String ref : refOrders)
    // nodes.addNonNull(refs.remove(ref));
    // nodes.addAll(refs.values());
    // }

    public static int randomGroupID()
    {
        return (int) (Math.random() * (Integer.MAX_VALUE - 2));
    }

    public void populate(DexterProps props)
    {
        this.ocr = props.ocr;
        this.doTrustSpaces = props.doTrustSpaces;
        this.doCheckClips = props.doCheckClips;
        this.textShadow = props.textShadow;
        this.splitIndent = props.splitIndent;
        this.splitIndentMax = props.splitIndentMax;
        this.splitInterline = props.splitInterline;
        this.mergeToken = props.mergeToken;
        this.mergeLine = props.mergeLine;
        this.mergeBlock = props.mergeBlock;
        this.sameAngle = props.sameAngle;
        this.sameFontsize = props.sameFontsize;
        this.sameRun = props.sameRun;
        this.sameBaseline = props.sameBaseline;
        this.sameLuminosity = props.sameLuminosity;
        this.sameColor = props.sameColor;
        this.useIndent = props.useIndent;
        this.useInterline = props.useInterline;
        this.useLayout = props.useLayout;
        this.useTreeStructure = props.useTreeStructure;
        this.doClusterize = props.doClusterize;
        this.doMergeItems = props.doMergeItems;
        this.doMergeTokens = props.doMergeTokens;
        this.doMergeRuns = props.doMergeRuns;
        this.doCleanRaws = props.doCleanRaws;
        this.items = Arrays.copyOf(props.items, props.items.length);
        this.rOrder = props.rOrder;
    }

}
