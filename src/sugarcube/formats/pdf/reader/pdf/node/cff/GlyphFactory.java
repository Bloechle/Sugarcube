/**
 * =========================================== Java Pdf Extraction Decoding Access Library ===========================================
 *
 * Project Info: http://www.jpedal.org (C) Copyright 1997-2008, IDRsolutions and Contributors.
 *
 * This file is part of JPedal
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 *
 * --------------- GlyphFactory.java ---------------
 */
package sugarcube.formats.pdf.reader.pdf.node.cff;

import sugarcube.common.graphics.geom.Path3;

public class GlyphFactory
{
  public boolean hasGlyph = true;
  private Path3 path = new Path3();
  /**
   * vertical positioning and scaling
   */
  private float ymin = 0;
  private int leftSideBearing = 0;
  private boolean closable = false;

  public void reinitialise(double[] fontMatrix)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public Path3 getPath()
  {
    return path;
  }

  public PdfGlyph getGlyph(boolean debug)
  {
    return null;
  }

  public void closePath()
  {
    if (closable)
      path.closePath();
    closable = false;
  }

  public void curveTo(float f, float g, float h, float i, float j, float k)
  {
    path.curveTo(f, g, h, i, j, k);
    closable = true;
  }

  public void moveTo(float f, float g)
  {
    path.moveTo(f, g);
    closable = false;
  }

  public void lineTo(float f, float g)
  {
    path.lineTo(f, g);
    closable = true;
  }

  /**
   * set ymin - ie vertical kern
   */
  public void setYMin(float ymin, float ymax)
  {

    this.ymin = ymin;
    //this.ymax=ymax;

  }

  public int getLSB()
  {
    return leftSideBearing;
  }
}
