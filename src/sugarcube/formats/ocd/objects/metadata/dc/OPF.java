package sugarcube.formats.ocd.objects.metadata.dc;

public class OPF
{
  public static final String NAMESPACE = "opf:";
  
  public enum Role
  {
    aut, edt, bkp;//author, editor, book producer
    public static final String KEY = NAMESPACE+"role";
    public final String value;

    Role()
    {
      this.value = super.name().toLowerCase().replaceAll("_", "-");
    }

    public boolean is(Role role)
    {
      return this.value.equals(role.value);
    }

    public boolean is(String value)
    {
      return this.value.equals(value);
    }

    @Override
    public String toString()
    {
      return value;
    }
  }
  
  public enum Event
  {
    creation, publication, modification;//author, editor, book producer
    public static final String KEY = NAMESPACE+"event";
    public final String value;

    Event()
    {
      this.value = super.name().toLowerCase().replaceAll("_", "-");
    }

    public boolean is(Role role)
    {
      return this.value.equals(role.value);
    }

    public boolean is(String value)
    {
      return this.value.equals(value);
    }

    @Override
    public String toString()
    {
      return value;
    }
  }  
}
