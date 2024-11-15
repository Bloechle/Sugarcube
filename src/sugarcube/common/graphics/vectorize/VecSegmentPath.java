package sugarcube.common.graphics.vectorize;

import java.util.ArrayList;

public class VecSegmentPath extends ArrayList<VecSegment>
{

  public VecSegment addLine(DirPointPath path, int start, int end)
  {
    VecSegment seg = new VecSegment(1, path, start, end);
    this.add(seg);
    return seg;
  }

  public VecSegment addQuadraticCurve(DirPointPath path, int start, int end, float cpx, float cpy)
  {
    VecSegment seg = new VecSegment(2, path, start, end);
    seg.setControlPoints(cpx, cpy);
    this.add(seg);
    return seg;

  }
}
