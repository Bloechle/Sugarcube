package sugarcube.common.system.process;

import sugarcube.common.system.log.Logger.Level;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.Props;
import sugarcube.common.data.collections.StringList;
import sugarcube.common.system.io.File3;

public class Arguments
{
  public static final String OPT_FLAG = "-";
  public static final String OPT_EQUAL = "=";
  public Props options = new Props();
  public StringList params = new StringList();
  public String[] args;

  public Arguments(String... args)
  {
    this.args = args;
    if (args != null)
      for (String arg : args)
        if (arg != null && !arg.trim().isEmpty())
          if (arg.startsWith(OPT_FLAG))
            addOption(arg);
          else
            params.add(arg);
  }

  public Arguments addParam(String param)
  {
    if (Zen.hasChar(param))
      this.params.add(param);
    return this;
  }

  public Arguments addParam(String param, String def)
  {
    if (Zen.hasChar(param))
      this.params.add(param);
    else
      this.params.add(def);
    return this;
  }

  public Arguments addOption(String key, Object value)
  {
    String str = value == null ? null : value.toString();
    if (Zen.hasChar(str))
      this.options.put(key, str);
    return this;
  }

  public boolean hasParams()
  {
    return !this.params.isEmpty();
  }

  public boolean hasOptions()
  {
    return !this.options.isEmpty();
  }
  
  public String need(String key, Object def)
  {
    return options.need(key, def.toString());
  }
  
  public Props put(String key, Object value)
  {
    options.put(key, value.toString());
    return options;
  }

  public String get(String key, String def)
  {
    String val = options.get(key);
    return val == null ? def : val.toString();
  }  
  
  public int integer(String key, int def)
  {
    return options.integer(key, def);
  }

  public boolean bool(String key, boolean def)
  {
    String val = options.get(key);
    return val == null ? def : val.trim().toLowerCase().equals("true");
  }

  public boolean is(String key, String value)
  {
    String val = options.get(key);
    return val == null ? false : value.trim().equalsIgnoreCase(val.trim());
  }

  public int nbOfParams()
  {
    return params.size();
  }

  public String firstParam()
  {
    return params.first();
  }

  public String secondParam()
  {
    return params.second();
  }

  public String thirdParam()
  {
    return params.third();
  }
  
  public String firstParam(String def)
  {
    return Zen.avoid(firstParam(), def);
  }

  public String secondParam(String def)
  {
    return Zen.avoid(secondParam(), def);
  }

  public String thirdParam(String def)
  {
    return Zen.avoid(thirdParam(), def);
  }

  public File3 firstFile(String... exts)
  {
    return filterFile(params.first(), exts);
  }

  public File3 secondFile(String... exts)
  {
    return filterFile(params.second(), exts);
  }

  public String firstPath(String... exts)
  {
    return filterPath(params.first(), exts);
  }

  public String secondPath(String... exts)
  {
    return filterPath(params.second(), exts);
  }

  public File3 filterFile(String path, String... exts)
  {
    String name = filterPath(path, exts);
    return name == null ? null : new File3(path);
  }

  public String filterPath(String path, String... exts)
  {
    if (path == null)
      return null;
    if (exts != null && exts.length > 0)
    {
      File3 file = new File3(path);
      for (String ext : exts)
        if (file.isDirectory() && ext.equals("."))
          return path;
        else if (File3.HasExtension(path, ext))
          return path;
      return null;
    }
    return path;
  }

  public final void addOption(String opt)
  {
    opt = opt.startsWith(OPT_FLAG) ? opt.substring(1).trim() : opt.trim();
    int index = opt.indexOf(OPT_EQUAL);
    String value = index < 0 ? "true" : opt.substring(index + 1).trim();
    if (value.endsWith("\""))
      value = value.substring(0, value.length() - 1);
    if (value.startsWith("\""))
      value = value.substring(1);
    options.put(index < 0 ? opt : opt.substring(0, index).trim(), value);
  }
  
  public Level logLevel(String def)
  {
    return Level.instance(options.get("log_level", def));
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("params").append(params).append(", ");
    sb.append(options.toString());
    return sb.toString();
  }
}
