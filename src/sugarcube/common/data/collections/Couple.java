package sugarcube.common.data.collections;

public class Couple<F, S>
{
  private F first;
  private S second;

  public Couple()
  {
  }

  public Couple(F first, S second)
  {
    this.first = first;
    this.second = second;
  }

  public Couple clear()
  {
    this.first = null;
    this.second = null;
    return this;
  }

  public F left()
  {
    return first;
  }

  public S right()
  {
    return second;
  }

  public F first()
  {
    return first;
  }

  public S second()
  {
    return second;
  }
  
    public F key()
  {
    return first;
  }

  public S value()
  {
    return second;
  }

  public void set(F first, S second)
  {
    this.first = first;
    this.second = second;
  }
  
  public boolean isKey(F key)
  {
    return isFirst(key);
  }

  public boolean isValue(S value)
  {
    return isSecond(value);
  }  

  public boolean isFirst(F first)
  {
    return first == this.first || first.equals(this.first);
  }

  public boolean isSecond(S second)
  {
    return second == this.second || second.equals(this.second);
  }

  public void setFirst(F first)
  {
    this.first = first;
  }

  public void setSecond(S second)
  {
    this.second = second;
  }
  
  public void setKey(F first)
  {
    this.first = first;
  }

  public void setValue(S second)
  {
    this.second = second;
  }  
}
