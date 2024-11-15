package sugarcube.insight.core;

import sugarcube.common.graphics.Metric;
import sugarcube.common.system.io.File3;

public class FxConfiguration
{
    public String softName = "Insight";
    public String logTitle = "Events Log";
    public String sidePagesTitle = "Page Thumbnails";
    public boolean whiteBackground = true;
    public boolean showSideNav = true;
    public boolean showSideDom = true;
    public boolean showSideLog = true;
    public boolean showSearchBt = true;
    public boolean showValidBt = true;
    public boolean showConfigBt = true;
    public boolean showRightSide = true;
    public boolean debugMode = false;
    public int nbOfDecimalsDisplayed = 2;
    public int openingTabIndex = 1;
    public boolean useTmpFile = true;
    public boolean createTmpOCD = false;
    public boolean developerMode = false;

    public String ocrHotfolderPath = "//SPARTACUS/HotDrive/hot/";
    public File3 directory = File3.userDesktop();
    public Metric metric = new Metric();
    public int decimals = 2;
}
