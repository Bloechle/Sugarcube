package sugarcube.common.data;

import sugarcube.common.data.collections.Str;

import java.util.Scanner;

public class Scan
{
  public Scanner scan;
  public String data;

  public Scan(String data)
  {
    this.data = data;
    this.scan = new Scanner(data);
  }
  
  public Scan sep(String regex)
  {
    scan.useDelimiter(regex);
    return this;
  }
  
  public boolean isVoid()
  {
    return Str.IsVoid(data);
  }
  
  public String value()
  {
    return scan.next();
  }
  
  public boolean hasInt()
  {
    return scan.hasNextInt();
  }
  
  public int integer()
  {
    return scan.nextInt();
  }
  
  public int integer(int def)
  {
    return hasInt() ? integer() : def;
  }

  public boolean hasReal()
  {
    return scan.hasNextDouble();
  }
  
  public float real()
  {
    return (float)scan.nextDouble();
  }
  
  public float real(float def)
  {
    return hasReal() ? real() : def;
  }
  
  public void close()
  {
    scan.close();    
  }
  
  public static Scan Get(String data)
  {
    return new Scan(data);
  }
}
