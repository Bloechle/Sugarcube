/*
 * CVS identifier:
 *
 * $Id: MsgPrinter.java,v 1.6 2000/09/05 09:25:24 grosbois Exp $
 *
 * Class:                   MsgPrinter
 *
 * Description:             Prints messages formatted for a specific
 *                          line width.
 *
 *
 *
 * COPYRIGHT:
 * 
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 * 
 * 
 * 
 */
package jj2000.j2k.util;

import java.io.PrintWriter;

/**
 * This utility class formats messages to the specified line width, by inserting line-breaks between words, and printing the resulting lines.
 *
 */
public class MsgPrinter
{
  /**
   * The line width to use
   */
  public int lw;
  /**
   * Signals that a newline was found
   */
  private static final int IS_NEWLINE = -2;
  /**
   * Signals that the end-of-string was reached
   */
  private static final int IS_EOS = -1;

  /**
   * Creates a new message printer with the specified line width and with the default locale.
   *
   * @param linewidth The line width for which to format (in characters)
   *
   *
   *
   */
  public MsgPrinter(int linewidth)
  {
    lw = linewidth;
  }

  /**
   * Returns the line width that is used for formatting.
   *
   * @return The line width used for formatting
   *
   *
   *
   */
  public int getLineWidth()
  {
    return lw;
  }

  /**
   * Sets the line width to the specified value. This new value will be used in subsequent calls to the print() message.
   *
   * @param linewidth The new line width to use (in cahracters)
   *
   *
   *
   */
  public void setLineWidth(int linewidth)
  {
    if (linewidth < 1)
      throw new IllegalArgumentException();
    lw = linewidth;
  }

  /**
   * Formats the message to print in the current line width, by breaking the message into lines between words. The number of spaces to indent the first line is
   * specified by 'flind' and the number of spaces to indent each of the following lines is specified by 'ind'. Newlines in 'msg' are respected. A newline is
   * always printed at the end.
   *
   * @param out Where to print the message.
   *
   * @param flind The indentation for the first line.
   *
   * @param ind The indentation for the other lines.
   *
   * @param msg The message to format and print.
   *
   *
   *
   */
  public void print(PrintWriter out, int flind, int ind,
    String msg)
  {
    int start, end, pend, efflw, lind, i;

    start = 0;
    end = 0;
    pend = 0;
    efflw = lw - flind;
    lind = flind;
    while ((end = nextLineEnd(msg, pend)) != IS_EOS)
    {
      if (end == IS_NEWLINE)
      { // Forced line break
        for (i = 0; i < lind; i++)
          out.print(" ");
        out.println(msg.substring(start, pend));
        if (nextWord(msg, pend) == msg.length())
        {
          // Traling newline => print it and done
          out.println("");
          start = pend;
          break;
        }
      }
      else
        if (efflw > end - pend)
        { // Room left on current line
          efflw -= end - pend;
          pend = end;
          continue;
        }
        else
        { // Filled-up current line => print it
          for (i = 0; i < lind; i++)
            out.print(" ");
          if (start == pend)
          { // Word larger than line width
            // Print anyways
            out.println(msg.substring(start, end));
            pend = end;
          }
          else
            out.println(msg.substring(start, pend));
        }
      // Initialize for next line
      lind = ind;
      efflw = lw - ind;
      start = nextWord(msg, pend);
      pend = start;
      if (start == IS_EOS)
        break; // Did all the string
    }
    if (pend != start)
    { // Part of a line left => print it
      for (i = 0; i < lind; i++)
        out.print(" ");
      out.println(msg.substring(start, pend));
    }

  }

  /**
   * Returns the index of the last character of the next word, plus 1, or IS_NEWLINE if a newline character is encountered before the next word, or IS_EOS if
   * the end of the string is ecnounterd before the next word. The method first skips all whitespace characters at or after 'from', except newlines. If a
   * newline is found IS_NEWLINE is returned. Then it skips all non-whitespace characters and returns the position of the last non-whitespace character, plus 1.
   * The returned index may be greater than the last valid index in the tsring, but it is always suitable to be used in the String.substring() method.
   *
   * <P>Non-whitespace characters are defined as in the Character.isWhitespace method (that method is used).
   *
   * @param str The string to parse
   *
   * @param from The index of the first position to search from
   *
   * @return The index of the last character in the next word, plus 1, IS_NEWLINE, or IS_EOS if there are no more words.
   *
   *
   *
   */
  private int nextLineEnd(String str, int from)
  {
    final int len = str.length();
    char c = '\0';
    // First skip all whitespace, except new line
    while (from < len && (c = str.charAt(from)) != '\n'
      && Character.isWhitespace(c))
      from++;
    if (c == '\n')
      return IS_NEWLINE;
    if (from >= len)
      return IS_EOS;
    // Now skip word characters
    while (from < len && !Character.isWhitespace(str.charAt(from)))
      from++;
    return from;
  }

  /**
   * Returns the position of the first character in the next word, starting from 'from', if a newline is encountered first then the index of the newline
   * character plus 1 is returned. If the end of the string is encountered then IS_EOS is returned. Words are defined as any concatenation of 1 or more
   * characters which are not whitespace. Whitespace characters are those for which Character.isWhitespace() returns true (that method is used).
   *
   * <P>Non-whitespace characters are defined as in the Character.isWhitespace method (that method is used).
   *
   * @param str The string to parse
   *
   * @param from The index where to start parsing
   *
   * @return The index of the first character of the next word, or the index of the newline plus 1, or IS_EOS.
   *
   *
   *
   */
  private int nextWord(String str, int from)
  {
    final int len = str.length();
    char c = '\0';
    // First skip all whitespace, but new lines
    while (from < len && (c = str.charAt(from)) != '\n'
      && Character.isWhitespace(c))
      from++;
    if (from >= len)
      return IS_EOS;
    else if (c == '\n')
      return from + 1;
    else
      return from;
  }
}
