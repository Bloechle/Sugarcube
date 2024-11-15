package sugarcube.common.interfaces;

import sugarcube.common.system.log.Logger;

public interface Loggable
{
    // Loggable object should get String data from source and message, keeping
    // reference to objects is never a good idea
    Loggable log(Object source, Object message, Logger.Level level);
}
