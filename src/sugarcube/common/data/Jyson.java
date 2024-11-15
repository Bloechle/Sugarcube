package sugarcube.common.data;


import sugarcube.common.data.json.Json;

public class Jyson {

    public static String Pretty(Object object)
    {
        return Json.Stringify(object);
    }

}
