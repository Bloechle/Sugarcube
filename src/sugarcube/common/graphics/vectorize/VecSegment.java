package sugarcube.common.graphics.vectorize;

public class VecSegment
{
  public float x0;
  public float y0;
  public float cpx;
  public float cpy;
  public float x1;
  public float y1;
  public int type;

  public VecSegment(int type, DirPointPath path, int start, int end)
  {
    this.type = type;
    x0 = path.get(start).x;
    y0 = path.get(start).y;
    cpx = 0f;
    cpy = 0f;    
    x1 = path.get(end).x;
    y1 = path.get(end).y;
  }
  
  public void setControlPoints(float cpx, float cpy)
  {
    this.cpx = cpx;
    this.cpy = cpy;
  }

}
