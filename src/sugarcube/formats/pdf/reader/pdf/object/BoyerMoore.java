package sugarcube.formats.pdf.reader.pdf.object;

import java.io.RandomAccessFile;

public class BoyerMoore
{
  private static final int MAXCHAR = 256;  // Maximum chars in character set.
  private byte[] pattern;  // Byte representation of pattern
  private int partial;  // Bytes of a partial match found at the end of a text buffer
  private int[] skip;  // Internal BM table
  private int[] d;    // Internal BM table

  /**
   * Boyer-Moore text search <P> Scans text left to right using what it knows of the pattern quickly determine if a match has been made in the text. In addition
   * it knows how much of the text to skip if a match fails. This cuts down considerably on the number of comparisons between the pattern and text found in pure
   * brute-force compares This has some advantages over the Knuth-Morris-Pratt text search. <P>The particular version used here is from "Handbook of Algorithms
   * and Data Structures", G.H. Gonnet & R. Baeza-Yates.
   * <p/>
   * Example of use:
   * <PRE>
   * String pattern = "and ";
   * <BR>
   * BoyerMoore bm = new BoyerMoore();
   * bm.compile(pattern);
   * <p/>
   * int bcount; int search; while ((bcount = f.read(b)) >= 0) { System.out.println("New Block:"); search = 0; while ((search = bm.search(b, search,
   * bcount-search)) >= 0) { if (search >= 0) { System.out.println("full pattern found at " + search); <BR> search += pattern.length(); continue; } } if
   * ((search = bm.partialMatch()) >= 0) { System.out.println("Partial pattern found at " + search); } }
   * </PRE>
   */
  public BoyerMoore()
  {
    this.skip = new int[MAXCHAR];
    this.d = null;
  }

  /**
   * Shortcut constructor
   */
  public BoyerMoore(byte[] pattern)
  {
    this();
    compile(pattern);
  }

  /**
   * Shortcut constructor
   */
  public BoyerMoore(String pattern)
  {
    this();
    compile(pattern.getBytes());
  }

  /**
   * Compiles the text pattern for searching.
   *
   * @param pattern What we're looking for.
   */
  public void compile(String pattern)
  {
    compile(pattern.getBytes());
  }

  /**
   * Compiles the text pattern for searching.
   *
   * @param pattern What we're looking for.
   */
  public void compile(byte[] pattern)
  {
    this.pattern = pattern;

    int j, k, m, t, t1, q, q1;
    int f[] = new int[pattern.length];
    d = new int[pattern.length];

    m = pattern.length;
    for (k = 0; k < MAXCHAR; k++)
      skip[k] = m;

    for (k = 1; k <= m; k++)
    {
      d[k - 1] = (m << 1) - k;
      skip[(this.pattern[k - 1] & 0xff)] = m - k;    // cast to unsigned byte
    }

    t = m + 1;
    for (j = m; j > 0; j--)
    {
      f[j - 1] = t;
      while (t <= m && this.pattern[j - 1] != this.pattern[t - 1])
      {
        d[t - 1] = (d[t - 1] < m - j) ? d[t - 1] : m - j;
        t = f[t - 1];
      }
      t--;
    }
    q = t;
    t = m + 1 - q;
    q1 = 1;
    t1 = 0;

    for (j = 1; j <= t; j++)
    {
      f[j - 1] = t1;
      while (t1 >= 1 && this.pattern[j - 1] != this.pattern[t1 - 1])
        t1 = f[t1 - 1];
      t1++;
    }

    while (q < m)
    {
      for (k = q1; k <= q; k++)
        d[k - 1] = (d[k - 1] < m + q - k) ? d[k - 1] : m + q - k;
      q1 = q + 1;
      q = q + t - f[t - 1];
      t = f[t - 1];
    }
  }

  /**
   * Search for the compiled pattern in the given text. A side effect of the search is the notion of a partial match at the end of the searched buffer. This
   * partial match is helpful in searching text files when the entire file doesn't fit into memory.
   *
   * @param text Buffer containing the text
   * @param start Start position for search
   * @param end Ending position for search
   * @return position in buffer where the pattern was found.
   * @see #partialMatch()
   */
  public int search(byte text[], long lstart, long lend)
  {
    int start = (int) lstart;
    int end = (int) lend;
    partial = 0;  // assume no partial match

    if (d == null)
      return -1;  // no pattern compiled, nothing matches.

    int m = pattern.length;
    if (m == 0)
      return 0;

    int k, j = 0;
    int max = 0;  // used in calculation of partial match. Max distand we jumped.

    for (k = start + m - 1; k < end + m - 1;)
    {
      // set up possible partial match
      int save_k = k;
      if (k >= end)
        partial = m - (k - end + 1);

      // scan string vs. pattern
      // ignore positions beyond end of buffer
      for (j = m - 1; j >= 0; j--, k--)
        if (k < end && text[k] != pattern[j])
          break;

      // did we make it all the way through the string?
      if (j == -1)
        return (partial == 0 ? k + 1 : -1);   // full or partial match?

      // skip to next possible start
      int z = skip[(text[k] & 0xff)];    // cast to unsigned byte
      max = (z > d[j]) ? z : d[j];
      if (save_k < end)
        k += max;
      else
        k = save_k + 1;  // calculation doesn't work past end of buffer,
      // just do it by hand
      partial = 0;
    }

    /*
     if (k >= end && k < end+m-1) {    // if we're near end of buffer --
     k = end-1;     // i.e. k - (k-end+1)
     for (j = partial-1; j >= 0 && text[k] == pattern[j]; j--)
     k--;

     if (j >= 0)
     partial = 0;    // no partial match

     return -1;	// not a real match
     }
     */

    return -1;  // No match
  }

  public int search(RandomAccessFile file, long lstart, long lend)
  {
    int start = (int) lstart;
    int end = (int) lend;
    try
    {
      partial = 0;  // assume no partial match

      if (d == null)
        return -1;  // no pattern compiled, nothing matches.

      int m = pattern.length;
      if (m == 0)
        return 0;

      int k, j = 0;
      int max = 0;  // used in calculation of partial match. Max distand we jumped.


      for (k = start + m - 1; k < end + m - 1;)
      {
        // set up possible partial match
        int save_k = k;
        if (k >= end)
          partial = m - (k - end + 1);

        // scan string vs. pattern
        // ignore positions beyond end of buffer
        for (j = m - 1; j >= 0; j--, k--)
          if (k < end)
          {
            file.seek(k);
            if (file.readByte() != pattern[j])
              break;   // confirmed non-match
          }

        // did we make it all the way through the string?
        if (j == -1)
          return (partial == 0 ? k + 1 : -1);   // full or partial match?

        // skip to next possible start
        file.seek(k);
        int z = skip[(file.readByte() & 0xff)];    // cast to unsigned byte
        max = (z > d[j]) ? z : d[j];
        if (save_k < end)
          k += max;
        else
          k = save_k + 1;  // calculation doesn't work past end of buffer,
        // just do it by hand
        partial = 0;
      }

      /*
       if (k >= end && k < end+m-1) {    // if we're near end of buffer --
       k = end-1;     // i.e. k - (k-end+1)
       for (j = partial-1; j >= 0 && text[k] == pattern[j]; j--)
       k--;

       if (j >= 0)
       partial = 0;    // no partial match

       return -1;	// not a real match
       }
       */

      return -1;  // No match
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return -1;
  }

  public byte[] pattern()
  {
    return pattern;
  }
}
