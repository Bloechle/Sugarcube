package sugarcube.formats.ocd.objects.metadata.dc;

public enum DC
{
  dc, title, creator, subject, description, publisher, contributor, date, type, format, identifier, source, language, relation, coverage, rights;
  public static final String NAMESPACE = "dc:";
  public final String nsName;

  DC()
  {
    this.nsName = NAMESPACE + super.name().toLowerCase().replaceAll("_", "-");
  }

  public String nsName()
  {
    return nsName;
  }

  public static boolean isDC(String name)
  {
    for (DC dc : DC.values())
    {
      if (dc.name().equalsIgnoreCase(name) || dc.nsName().equalsIgnoreCase(name))
        return true;
    }
    return false;
  }

  public static String nsName(String name)
  {
    return name.startsWith(NAMESPACE) ? name : NAMESPACE + name;
  }

  public static String name(String nsName)
  {
    return nsName.startsWith(NAMESPACE) ? nsName.substring(NAMESPACE.length()) : nsName;
  }

  public static boolean contains(String tagName)
  {
    for (DC tag : DC.values())
      if (tag.is(tagName))
        return true;
    //maurizio: extensions for meta
    if (tagName.equals("meta"))
    	return true;
    return false;
  }

  public boolean is(DC tag)
  {
    return this.equals(tag);
  }

  public boolean is(String name)
  {
    return this.nsName.equalsIgnoreCase(name) || this.nsName.equalsIgnoreCase(NAMESPACE + name);
  }

  public static DC get(String name)
  {
    String nsName = DC.nsName(name);
    for (DC dc : DC.values())
      if (dc.nsName.equalsIgnoreCase(nsName))
        return dc;
    return null;
  }

  @Override
  public String toString()
  {
    return nsName;
  }
}