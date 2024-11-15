package sugarcube.common.interfaces;

import sugarcube.common.data.Jyson;

public interface Jsonable
{
    default String json()
    {
        return Jyson.Pretty(this);
    }
}
