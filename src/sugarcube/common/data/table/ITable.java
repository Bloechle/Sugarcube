package sugarcube.common.data.table;

public interface ITable
{

  public int rows();

  public int cols();

  public String get(int row, int col, String def);

  public default String get(String field, int row, String def)
  {
    String[] fields = fields();
    for (int col = 0; col < cols(); col++)
      if (field.equalsIgnoreCase(fields[col]))
        return get(row, col, def);
    return def;
  }

  public String[] fields();

  public default String[][] values()
  {
    String[][] data = new String[rows()][cols()];
    for (int row = 0; row < data.length; row++)
      for (int col = 0; col < data[0].length; col++)
        data[row][col] = get(row, col, null);
    return data;
  }
}
