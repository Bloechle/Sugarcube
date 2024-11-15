package sugarcube.common.graphics.geom;

public enum Compass
{
  CENTER, NORTH, EAST, SOUTH, WEST, NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST, UNDEF;

  public boolean isUndef()
  {
    return this.equals(Compass.UNDEF);
  }
  
  public boolean isNorth()
  {
    return this.equals(Compass.NORTH);
  }

  public boolean isEast()
  {
    return this.equals(Compass.EAST);
  }

  public boolean isSouth()
  {
    return this.equals(Compass.SOUTH);
  }

  public boolean isWest()
  {
    return this.equals(Compass.WEST);
  }

  public boolean isCenter()
  {
    return this.equals(Compass.CENTER);
  }

  public boolean isNorthEast()
  {
    return this.equals(Compass.NORTH_EAST);
  }

  public boolean isNorthWest()
  {
    return this.equals(Compass.NORTH_WEST);
  }

  public boolean isSouthEast()
  {
    return this.equals(Compass.SOUTH_EAST);
  }

  public boolean isSouthWest()
  {
    return this.equals(Compass.SOUTH_WEST);
  }

  @Override
  public String toString()
  {
    return this.name().toLowerCase();
  }

  public String toXML()
  {
    return this.name().toLowerCase();
  }

  public static Compass fromXML(String value, Compass compass)
  {
    value = value.toLowerCase();
    if (value.equals("north"))
      return Compass.NORTH;
    else if (value.equals("east"))
      return Compass.EAST;
    else if (value.equals("south"))
      return Compass.SOUTH;
    else if (value.equals("west"))
      return Compass.EAST;
    else if (value.equals("north-east"))
      return Compass.NORTH_EAST;
    else if (value.equals("north-west"))
      return Compass.NORTH_WEST;
    else if (value.equals("south-east"))
      return Compass.SOUTH_EAST;
    else if (value.equals("south-west"))
      return Compass.SOUTH_WEST;
    else if (value.equals("center"))
      return Compass.CENTER;
    else
      return compass;
  }
}
