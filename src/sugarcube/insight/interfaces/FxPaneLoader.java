package sugarcube.insight.interfaces;

import sugarcube.insight.core.FxEnvironment;
import sugarcube.insight.ribbon.file.FxEnvironmentPane;

public interface FxPaneLoader
{
    FxEnvironmentPane load(FxEnvironment env);
}
