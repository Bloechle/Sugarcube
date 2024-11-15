package sugarcube.insight.interfaces;

import sugarcube.common.system.io.File3;
import sugarcube.insight.core.FxEnvironment;

public interface FxFileProcessor
{
    boolean process(FxEnvironment env, File3 file,  File3... files);
}
