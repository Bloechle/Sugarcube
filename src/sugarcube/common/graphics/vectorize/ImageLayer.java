package sugarcube.common.graphics.vectorize;

import java.util.ArrayList;

public class ImageLayer
{
  public int[][] data;
  public ArrayList<EdgePointPath> paths = new ArrayList<>();
  public ArrayList<DirPointPath> internodePaths = new ArrayList<>();
  public ArrayList<VecSegmentPath> tracePaths = new ArrayList<>();

  public ImageLayer(int w, int h)
  {
    this.data = new int[h][w];
  }

  // 3. Walking through an edge node array, discarding edge node types 0 and 15
  // and creating paths from the rest.
  // Walk directions (dir): 0 > ; 1 ^ ; 2 < ; 3 v
  // Edge node types ( ▓:light or 1; ░:dark or 0 )
  // ░░ ▓░ ░▓ ▓▓ ░░ ▓░ ░▓ ▓▓ ░░ ▓░ ░▓ ▓▓ ░░ ▓░ ░▓ ▓▓
  // ░░ ░░ ░░ ░░ ░▓ ░▓ ░▓ ░▓ ▓░ ▓░ ▓░ ▓░ ▓▓ ▓▓ ▓▓ ▓▓
  // 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15
  //
  public void scanPath(float pathOmit)
  {

    int width = data[0].length;
    int height = data.length;

    for (int j = 0; j < height; j++)
    {
      for (int i = 0; i < width; i++)
      {
        if ((data[j][i] != 0) && (data[j][i] != 15))
        {
          int px = i;
          int py = j;

          EdgePointPath path = new EdgePointPath();
          paths.add(path);

          // fill paths will be drawn, but hole paths are also required to
          // remove unnecessary edge nodes
          int dir = PathScan.DIR_LOOKUP[data[py][px]];
          boolean isHolePath = PathScan.HOLEPATH_LOOKUP[data[py][px]];

          boolean isFinished = false;
          while (!isFinished)
          {
            path.add(new EdgePoint(px - 1, py - 1, data[py][px]));

            // Next: look up the replacement, direction and coordinate changes =
            // clear this cell, turn if required, walk forward
            byte[] lookupRow = PathScan.COMBINED_LOOKUP[data[py][px]][dir];
            data[py][px] = lookupRow[0];
            dir = lookupRow[1];
            px += lookupRow[2];
            py += lookupRow[3];

            // Close path
            if (((px - 1) == path.get(0).x) && ((py - 1) == path.get(0).y))
            {
              isFinished = true;
              // Discarding 'hole' type paths and paths shorter than pathomit
              if ((isHolePath) || (path.size() < pathOmit))
                paths.remove(path);
            }
          }
        }
      }
    }
  }

  // 4. interpolating between path points for nodes with 8 directions ( E,
  // SE, S, SW, W, NW, N, NE )
  public void interpolateInternodes()
  {
    DirPointPath internode;
    DirPoint c01;
    float[] c12 = new float[2];
    EdgePoint[] p = new EdgePoint[3];

    for (int i = 0; i < paths.size(); i++)
    {
      internodePaths.add(internode = new DirPointPath());
      int nbOfPoints = paths.get(i).size();

      for (int j = 0; j < nbOfPoints; j++)
      {
        // interpolate between two path points
        internode.add(c01 = new DirPoint());

        for (int k = 0; k < 3; k++)
          p[k] = paths.get(i).get((j + k) % nbOfPoints);

        c01.x = (p[0].x + p[1].x) / 2f;
        c01.y = (p[0].y + p[1].y) / 2f;
        c12[0] = (p[1].x + p[2].x) / 2f;
        c12[1] = (p[1].y + p[2].y) / 2f;

        // line segment direction to the next point
        if (c01.x < c12[0])
          c01.type = type(c01.y, c12[1], 1, 7, 0); // SE NE E
        else if (c01.x > c12[0])
          c01.type = type(c01.y, c12[1], 3, 5, 4); // SW NW W
        else
          c01.type = type(c01.y, c12[1], 2, 6, 8); // S N center (unexpected)
      }
    }
  }

  private int type(float a, float b, int lt, int eq, int gt)
  {
    return a < b ? lt : (a > b ? gt : eq);
  }

  // 5. Batch tracing paths
  public void traceAllPaths(float ltres, float qtres)
  {
    for (int k = 0; k < internodePaths.size(); k++)
      tracePaths.add(tracePath(internodePaths.get(k), ltres, qtres));

  }

  // 5. tracepath() : recursively trying to fit straight and quadratic spline
  // segments on the 8 direction internode path

  // 5.1. Find sequences of points with only 2 segment types
  // 5.2. Fit a straight line on the sequence
  // 5.3. If the straight line fails (an error>ltreshold), find the point with
  // the biggest error
  // 5.4. Fit a quadratic spline through errorpoint (project this to get
  // controlpoint), then measure errors on every point in the sequence
  // 5.5. If the spline fails (an error>qtreshold), find the point with the
  // biggest error, set splitpoint = (fitting point + errorpoint)/2
  // 5.6. Split sequence and recursively apply 5.2. - 5.7. to
  // startpoint-splitpoint and splitpoint-endpoint sequences
  // 5.7. TODO? If splitpoint-endpoint is a spline, try to add new points from
  // the next sequence

  // This returns an SVG Path segment as a double[7] where
  // segment[0] ==1.0 linear ==2.0 quadratic interpolation
  // segment[1] , segment[2] : x1 , y1
  // segment[3] , segment[4] : x2 , y2 ; middle point of Q curve, endpoint of L
  // line
  // segment[5] , segment[6] : x3 , y3 for Q curve, should be 0.0 , 0.0 for L
  // line
  //
  // path type is discarded, no check for path.size < 3 , which should not
  // happen

  public VecSegmentPath tracePath(DirPointPath path, float lineThreshold, float curveThreshold)
  {
    VecSegmentPath tracePath = new VecSegmentPath();
    int size = path.size();
    int index = 0;
    while (index < size)
    {
      // 5.1. Find sequences of points with only 2 segment types
      int t1 = path.get(index).type;
      int t2 = -1;
      int end = index + 1;
      while (((path.get(end).type == t1) || (path.get(end).type == t2) || (t2 == -1)) && (end < (size - 1)))
      {
        if ((path.get(end).type != t1) && (t2 == -1))
          t2 = path.get(end).type;
        end++;
      }

      if (end == (size - 1))
        end = 0;

      // 5.2. - 5.6. Split sequence and recursively apply 5.2. - 5.6. to
      // startpoint-splitpoint and splitpoint-endpoint sequences
      tracePath.addAll(fitSeqence(path, lineThreshold, curveThreshold, index, end));
      // 5.7. TODO? If splitpoint-endpoint is a spline, try to add new points
      // from the next sequence

      index = end > 0 ? end : size;
    }

    return tracePath;
  }

  // 5.2. - 5.6. recursively fitting a straight or quadratic line segment on
  // this sequence of path nodes,
  // called from tracepath()
  public VecSegmentPath fitSeqence(DirPointPath path, float lineThreshold, float curveThreshold, int start, int end)
  {
    VecSegmentPath segment = new VecSegmentPath();

    int size = path.size();

    // return if invalid seqend
    if ((end > size) || (end < 0))
    {
      return segment;
    }

    int errorpoint = start;
    boolean curvepass = true;
    float px, py, dist2, errorval = 0;
    float tl = (end - start);
    if (tl < 0)
    {
      tl += size;
    }
    float vx = (path.get(end).x - path.get(start).x) / tl, vy = (path.get(end).y - path.get(start).y) / tl;

    // 5.2. Fit a straight line on the sequence
    int pcnt = (start + 1) % size;
    float pl;
    while (pcnt != end)
    {
      pl = pcnt - start;
      if (pl < 0)
      {
        pl += size;
      }
      px = path.get(start).x + (vx * pl);
      py = path.get(start).y + (vy * pl);
      dist2 = ((path.get(pcnt).x - px) * (path.get(pcnt).x - px)) + ((path.get(pcnt).y - py) * (path.get(pcnt).y - py));
      if (dist2 > lineThreshold)
      {
        curvepass = false;
      }
      if (dist2 > errorval)
      {
        errorpoint = pcnt;
        errorval = dist2;
      }
      pcnt = (pcnt + 1) % size;
    }

    // return straight line if fits
    if (curvepass)
    {
      segment.addLine(path, start, end);
      return segment;
    }

    // 5.3. If the straight line fails (an error>ltreshold), find the point with
    // the biggest error
    int fitpoint = errorpoint;
    curvepass = true;
    errorval = 0;

    // 5.4. Fit a quadratic spline through this point, measure errors on every
    // point in the sequence
    // helpers and projecting to get control point
    float t = (fitpoint - start) / tl, t1 = (1f - t) * (1f - t), t2 = 2f * (1f - t) * t, t3 = t * t;
    float cpx = (((t1 * path.get(start).x) + (t3 * path.get(end).x)) - path.get(fitpoint).x) / -t2,
        cpy = (((t1 * path.get(start).y) + (t3 * path.get(end).y)) - path.get(fitpoint).y) / -t2;

    // Check every point
    pcnt = start + 1;
    while (pcnt != end)
    {

      t = (pcnt - start) / tl;
      t1 = (1f - t) * (1f - t);
      t2 = 2f * (1f - t) * t;
      t3 = t * t;
      px = (t1 * path.get(start).x) + (t2 * cpx) + (t3 * path.get(end).x);
      py = (t1 * path.get(start).y) + (t2 * cpy) + (t3 * path.get(end).y);

      dist2 = ((path.get(pcnt).x - px) * (path.get(pcnt).x - px)) + ((path.get(pcnt).y - py) * (path.get(pcnt).y - py));

      if (dist2 > curveThreshold)
      {
        curvepass = false;
      }
      if (dist2 > errorval)
      {
        errorpoint = pcnt;
        errorval = dist2;
      }
      pcnt = (pcnt + 1) % size;
    }

    // return spline if fits
    if (curvepass)
    {
      segment.addQuadraticCurve(path, start, end, cpx, cpy);
      return segment;
    }

    // 5.5. If the spline fails (an error>qtreshold), find the point with the
    // biggest error,
    // set splitpoint = (fitting point + errorpoint)/2
    int splitpoint = (fitpoint + errorpoint) / 2;

    // 5.6. Split sequence and recursively apply 5.2. - 5.6. to
    // startpoint-splitpoint and splitpoint-endpoint sequences
    segment = fitSeqence(path, lineThreshold, curveThreshold, start, splitpoint);
    segment.addAll(fitSeqence(path, lineThreshold, curveThreshold, splitpoint, end));
    return segment;
  }

  public int width()
  {
    return data[0].length;
  }

  public int height()
  {
    return data.length;
  }

}
