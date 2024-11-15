package sugarcube.formats.pdf.reader.pdf.object;

import java.io.Serializable;

/**
 * Reference is an immutable object. Reference is used in PDFRoot as a key for a
 * HashMap, so it needs a hashCode.
 *
 * PDF Reference (nothing to do with this Reference...) says: Together, the
 * combination of an object number and a generation number uniquely identifies
 * an indirect object. The object retains the same object number and generation
 * number throughout its existence, even if its value is modified.
 *
 * An indirect reference to an undefined object is not an error; it is simply
 * treated as a reference to the null object. For example, if a file contains
 * the indirect reference 17 0 R but does not contain the corresponding
 * definition then the indirect reference is considered to refer to the null
 * object.
 *
 */
public class Reference implements Comparable<Reference>, Cloneable, Serializable
{
  public static final Reference UNDEF = new Reference();
  private final int hashCode;
  private final int id;
  private final int generation;

  /**
   * Instantiates an undefined reference
   */
  public Reference()
  {
    this(-1, -1);
  }

  public Reference(int id, int generation)
  {
    this.id = id;
    this.generation = generation;
    this.hashCode = generation == 0 ? id : id + generation * 10000000;
  }

  public Reference get(int id)
  {
    return new Reference(id, generation);
  }

  public Reference prev()
  {
    return new Reference(id - 1, generation);
  }

  public Reference next()
  {
    return new Reference(id + 1, generation);
  }

  public boolean is(int id)
  {
    return this.id == id;
  }

  public boolean is(int id, int generation)
  {
    return this.id == id && this.generation == generation;
  }

  public boolean isUndef()
  {
    return this == UNDEF || (id < 0 && generation < 0);
  }

  // hard to understand, isn't it?
  // do not confuse indirect reference with indirect reference :-) hehe
  // -1 -1 != 4 0 R
  public boolean isIndirectReference()
  {
    return id != -1 && generation != -1;
  }

  @Override
  public int hashCode()
  {
    return hashCode;
  }

  public int id()
  {
    return id;
  }

  public int generation()
  {
    return generation;
  }

  @Override
  public int compareTo(Reference reference)
  {
    if (generation - reference.generation == 0)
      return id - reference.id;
    else
      return generation - reference.generation;
  }

  @Override
  public boolean equals(Object object)
  {
    if (this == object)
      return true;
    if (object == null || this.getClass() != object.getClass())
      return false;
    return id == ((Reference) object).id && generation == ((Reference) object).generation;
  }

  public String stringValue()
  {
    if (id == -1)
      return "";
    else
      return "(" + id + " " + generation + ")";
  }

  @Override
  public String toString()
  {
    return stringValue();
  }
}
