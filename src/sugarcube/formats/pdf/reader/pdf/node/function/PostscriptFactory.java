package sugarcube.formats.pdf.reader.pdf.node.function;

public class PostscriptFactory
{
  static final int[] scale =
  {
    1, 676, 17576, 456976
  };
  static final double toBase10 = Math.log(10.0D);
  private int level = 0;
  private static final byte START_BRACE = 123;
  private static final byte END_BRACE = 125;
  private byte[] stream;
  private int ptr;
  private int streamLength;
  private static final boolean debug = false;
  protected boolean testingFunction = false;
  protected double[] stack;
  private double[] safeStack;
  protected int[] stackType;
  private int[] safeStackType;
  protected int stkPtr = 0;
  private int safeStkPtr = 0;
  protected int stkTypePtr = 0;
  private int safeStkTypePrt = 0;
  protected int currentType = 0;
  boolean cont = false;
  static final double radiansToDegrees = 57.295779513082323D;
  protected static final double isTrue = 1.0D;
  protected static final double isFalse = 0.0D;
  protected static final int PS_INTEGER = 1;
  protected static final int PS_REAL = 2;
  protected static final int PS_BOOLEAN = 3;
  protected static final int PS_UNKNOWN = 0;
  protected static final int PS_abs = 317044;
  protected static final int PS_add = 54756;
  protected static final int PS_atan = 5953532;
  protected static final int PS_ceil = 5170050;
  protected static final int PS_cos = 325834;
  protected static final int PS_cvi = 154806;
  protected static final int PS_cvr = 312990;
  protected static final int PS_div = 374507;
  protected static final int PS_exp = 279192;
  protected static final int PS_floo = 6651169;
  protected static final int PS_idiv = 9739140;
  protected static final int PS_ln = 8799;
  protected static final int PS_log = 114931;
  protected static final int PS_mod = 62204;
  protected static final int PS_mul = 206868;
  protected static final int PS_neg = 108173;
  protected static final int PS_sin = 233914;
  protected static final int PS_sqrt = 8992170;
  protected static final int PS_sub = 31114;
  protected static final int PS_roun = 6301689;
  protected static final int PS_trun = 6303719;
  protected static final int PS_and = 61516;
  protected static final int PS_bits = 8564921;
  protected static final int PS_eq = 10820;
  protected static final int PS_fals = 8418909;
  protected static final int PS_ge = 2710;
  protected static final int PS_gt = 12850;
  protected static final int PS_le = 2715;
  protected static final int PS_lt = 12855;
  protected static final int PS_ne = 2717;
  protected static final int PS_not = 343421;
  protected static final int PS_or = 11506;
  protected static final int PS_true = 2190935;
  protected static final int PS_xor = 308279;
  protected static final int PS_if = 3388;
  protected static final int PS_ifel = 5100428;
  protected static final int PS_copy = 11240530;
  protected static final int PS_exch = 3249536;
  protected static final int PS_pop = 273119;
  protected static final int PS_dup = 277163;
  protected static final int PS_inde = 1889428;
  protected static final int PS_roll = 5229553;

  public PostscriptFactory(byte[] stream)
  {
    this.stream = stream;
    this.streamLength = stream.length;
  }

  protected static String toString(int operator)
  {
    String str = "";
    switch (operator)
    {
      case 317044:
        str = "abs";
        break;
      case 54756:
        str = "add";
        break;
      case 5953532:
        str = "atan";
        break;
      case 5170050:
        str = "ceiling";
        break;
      case 325834:
        str = "cos";
        break;
      case 154806:
        str = "cvi";
        break;
      case 312990:
        str = "cvr";
        break;
      case 374507:
        str = "div";
        break;
      case 279192:
        str = "exp";
        break;
      case 6651169:
        str = "floor";
        break;
      case 9739140:
        str = "idiv";
        break;
      case 8799:
        str = "ln";
        break;
      case 114931:
        str = "log";
        break;
      case 62204:
        str = "mod";
        break;
      case 206868:
        str = "mul";
        break;
      case 108173:
        str = "neg";
        break;
      case 233914:
        str = "sin";
        break;
      case 8992170:
        str = "sqrt";
        break;
      case 31114:
        str = "sub";
        break;
      case 6301689:
        str = "round";
        break;
      case 6303719:
        str = "truncate";
        break;
      case 61516:
        str = "and";
        break;
      case 8564921:
        str = "bitshift";
        break;
      case 10820:
        str = "eq";
        break;
      case 8418909:
        str = "false";
        break;
      case 2710:
        str = "ge";
        break;
      case 12850:
        str = "gt";
        break;
      case 2715:
        str = "le";
        break;
      case 12855:
        str = "lt";
        break;
      case 2717:
        str = "ne";
        break;
      case 343421:
        str = "not";
        break;
      case 11506:
        str = "or";
        break;
      case 2190935:
        str = "true";
        break;
      case 308279:
        str = "xor";
        break;
      case 3388:
        str = "if";
        break;
      case 5100428:
        str = "ifelse";
        break;
      case 11240530:
        str = "copy";
        break;
      case 3249536:
        str = "exch";
        break;
      case 273119:
        str = "pop";
        break;
      case 277163:
        str = "dup";
        break;
      case 1889428:
        str = "index";
        break;
      case 5229553:
        str = "roll";
        break;
      default:
        str = "UNKNOWN";
    }
    return str;
  }

  protected static int getCommandID(byte[] paramArrayOfByte)
  {
    int i = -1;
    int j = 0;
    int k = paramArrayOfByte.length;
    if (k > 4)
      k = 4;
    for (int m = 0; m < k; m++)
      j += (paramArrayOfByte[m] - 97) * scale[m];
    switch (j)
    {
      case 317044:
        i = 317044;
        break;
      case 54756:
        i = 54756;
        break;
      case 5953532:
        i = 5953532;
        break;
      case 5170050:
        i = 5170050;
        break;
      case 325834:
        i = 325834;
        break;
      case 154806:
        i = 154806;
        break;
      case 312990:
        i = 312990;
        break;
      case 374507:
        i = 374507;
        break;
      case 279192:
        i = 279192;
        break;
      case 6651169:
        i = 6651169;
        break;
      case 9739140:
        i = 9739140;
        break;
      case 8799:
        i = 8799;
        break;
      case 114931:
        i = 114931;
        break;
      case 62204:
        i = 62204;
        break;
      case 206868:
        i = 206868;
        break;
      case 108173:
        i = 108173;
        break;
      case 233914:
        i = 233914;
        break;
      case 8992170:
        i = 8992170;
        break;
      case 31114:
        i = 31114;
        break;
      case 6301689:
        i = 6301689;
        break;
      case 6303719:
        i = 6303719;
        break;
      case 61516:
        i = 61516;
        break;
      case 8564921:
        i = 8564921;
        break;
      case 10820:
        i = 10820;
        break;
      case 8418909:
        i = 8418909;
        break;
      case 2710:
        i = 2710;
        break;
      case 12850:
        i = 12850;
        break;
      case 2715:
        i = 2715;
        break;
      case 12855:
        i = 12855;
        break;
      case 2717:
        i = 2717;
        break;
      case 343421:
        i = 343421;
        break;
      case 11506:
        i = 11506;
        break;
      case 2190935:
        i = 2190935;
        break;
      case 308279:
        i = 308279;
        break;
      case 3388:
        i = 3388;
        break;
      case 5100428:
        i = 5100428;
        break;
      case 11240530:
        i = 11240530;
        break;
      case 3249536:
        i = 3249536;
        break;
      case 273119:
        i = 273119;
        break;
      case 277163:
        i = 277163;
        break;
      case 1889428:
        i = 1889428;
        break;
      case 5229553:
        i = 5229553;
    }
    return i;
  }

  protected int execute(int paramInt)
  {
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    double d1 = 0.0D;
    double d2 = 0.0D;
    int n;
    int i4;
    int i6;
    switch (paramInt)
    {
      case 317044:
        d1 = pop();
        if (d1 < 0.0D)
          push(-d1, 1);
        else
          push(d1, 1);
        break;
      case 54756:
        if (this.stack.length < 2)
          throw new RuntimeException("ADD - not enough elements on the stack");
        d1 = pop();
        j = this.currentType;
        d2 = pop();
        k = this.currentType;
        if ((j == 2) || (k == 2))
          push(d1 + d2, 2);
        else
          push(d1 + d2, 1);
        break;
      case 61516:
        d1 = pop();
        j = this.currentType;
        d2 = pop();
        k = this.currentType;
        if ((j == 1) && (k == 1))
          push((int) d1 & (int) d2, 1);
        else
        {
          if ((j != 3) || (k != 3))
            break;
          push((int) d1 & (int) d2, 3);
        }
        break;
      case 5953532:
        calculateAtan();
        break;
      case 8564921:
        int i1 = popInt();
        j = this.currentType;
        m = popInt();
        k = this.currentType;
        if (((j != 1) || (k == 1)) || (i1 > 0))
          m <<= i1;
        if (i1 < 0)
          m >>= -i1;
        push(m, 1);
        break;
      case 5170050:
        d1 = pop();
        j = this.currentType;
        if (d1 < 0.0D)
          push((int) d1, j);
        else
        {
          int i2 = (int) d1;
          if (d1 > i2)
            push(i2 + 1, j);
          else
            push(d1, j);
        }
        break;
      case 11240530:
        n = popInt();
        j = this.currentType;
        if ((j == 1) && (n > 0))
        {
          double[] arrayOfDouble = new double[n];
          int[] arrayOfInt = new int[n];
          for (int i3 = 0; i3 < arrayOfDouble.length; i3++)
          {
            arrayOfDouble[i3] = pop();
            arrayOfInt[i3] = this.currentType;
          }
          for (int i3 = arrayOfDouble.length; i3 > 0; i3--)
            push(arrayOfDouble[(i3 - 1)], arrayOfInt[(i3 - 1)]);
          for (int i3 = arrayOfDouble.length; i3 > 0; i3--)
            push(arrayOfDouble[(i3 - 1)], arrayOfInt[(i3 - 1)]);
        }
        else
          if ((j != 1) || (n != 0))
            break;
        break;
      case 325834:
        d1 = pop();
        double d3 = d1 / 57.295779513082323D;
        double d4 = Math.cos(d3);
        if ((d4 > 0.0D) && (d4 < 1.0E-007D))
          d4 = 0.0D;
        else if ((d4 < 0.0D) && (d4 > -1.0E-007D))
          d4 = 0.0D;
        push(d4, 2);
        break;
      case 154806:
        d1 = pop();
        push((int) d1, 1);
        break;
      case 312990:
        d1 = pop();
        push(d1, 2);
        break;
      case 374507:
        d1 = pop();
        d2 = pop();
        push(d2 / d1, 2);
        break;
      case 277163:
        calculateDup();
        break;
      case 10820:
        d1 = pop();
        d2 = pop();
        if (d1 == d2)
          push(1.0D, 3);
        else
          push(0.0D, 3);
        break;
      case 3249536:
        if (this.stack.length < 2)
          throw new RuntimeException("EXCH - not enough elements on the stack");
        d1 = pop();
        j = this.currentType;
        d2 = pop();
        k = this.currentType;
        push(d1, j);
        push(d2, k);
        break;
      case 279192:
        d1 = pop();
        d2 = pop();
        push(Math.pow(d2, d1), 2);
        break;
      case 8418909:
        push(0.0D, 3);
        break;
      case 6651169:
        d1 = pop();
        j = this.currentType;
        if (d1 > 0.0D)
          push((int) d1, j);
        else
        {
          i4 = (int) d1;
          if (i4 > d1)
            push(i4 - 1, j);
          else
            push(d1, j);
        }
        break;
      case 2710:
        d1 = pop();
        j = this.currentType;
        d2 = pop();
        k = this.currentType;
        if (((j != 1) && (j != 2)) || ((k != 1) && (k != 2)))
          break;
        if (d2 >= d1)
          push(1.0D, 3);
        else
          push(0.0D, 3);
        break;
      case 12850:
        d1 = pop();
        j = this.currentType;
        d2 = pop();
        k = this.currentType;
        if (((j != 1) && (j != 2)) || ((k != 1) && (k != 2)))
          break;
        if (d2 > d1)
          push(1.0D, 3);
        else
          push(0.0D, 3);
        break;
      case 9739140:
        i4 = popInt();
        int i5 = popInt();
        push(i5 / i4, 1);
        break;
      case 3388:
        if (!this.cont)
        {
          System.arraycopy(this.safeStack, 0, this.stack, 0, 100);
          System.arraycopy(this.safeStackType, 0, this.stackType, 0, 100);
          this.stkPtr = this.safeStkPtr;
          this.stkTypePtr = this.safeStkTypePrt;
        }
        this.cont = false;
        break;
      case 1889428:
        calculateIndex();
        break;
      case 2715:
        d1 = pop();
        j = this.currentType;
        d2 = pop();
        k = this.currentType;
        if (((j != 1) && (j != 2)) || ((k != 1) && (k != 2)))
          break;
        if (d2 <= d1)
          push(1.0D, 3);
        else
          push(0.0D, 3);
        break;
      case 12855:
        d1 = pop();
        j = this.currentType;
        d2 = pop();
        k = this.currentType;
        if (((j != 1) && (j != 2)) || ((k != 1) && (k != 2)))
          break;
        if (d2 < d1)
          push(1.0D, 3);
        else
          push(0.0D, 3);
        break;
      case 8799:
        d1 = pop();
        push(Math.log(d1), 2);
        break;
      case 114931:
        d1 = pop();
        push(Math.log(d1) / toBase10, 2);
        break;
      case 62204:
        if ((j != 1) || (k != 1))
          System.err.println("PS_mod - both values must be integers!");
        d1 = pop();
        j = this.currentType;
        d2 = pop();
        k = this.currentType;
        push(d2 % d1, 1);
        break;
      case 206868:
        if (this.stack.length < 2)
          throw new RuntimeException("MUL - not enough elements on the stack");
        d1 = pop();
        d2 = pop();
        push(d1 * d2, 1);
        break;
      case 2717:
        d1 = pop();
        d2 = pop();
        if (d1 != d2)
          push(1.0D, 3);
        else
          push(0.0D, 3);
        break;
      case 108173:
        double d5 = pop();
        j = this.currentType;
        if (d5 != 0.0D)
          push(-d5, j);
        else
          push(d5, j);
        break;
      case 343421:
        d1 = pop();
        j = this.currentType;
        if ((d1 == 0.0D) && (j == 3))
          push(1.0D, 3);
        else if ((d1 == 1.0D) && (j == 3))
          push(0.0D, 3);
        else
          push((int) d1 ^ 0xFFFFFFFF, 1);
        break;
      case 11506:
        d1 = pop();
        j = this.currentType;
        d2 = pop();
        j = this.currentType;
        if ((j == 3) && (k == 3))
          push((int) d1 | (int) d2, 3);
        else
        {
          if ((j != 1) || (k != 1))
            break;
          push((int) d1 | (int) d2, 1);
        }
        break;
      case 273119:
        pop();
        break;
      case 5229553:
        calculateRoll();
        break;
      case 6301689:
        d1 = pop();
        j = this.currentType;
        d1 += 0.5D;
        if (d1 > 0.0D)
          push((int) d1, j);
        else
        {
          i6 = (int) d1;
          if (i6 > d1)
            push(i6 - 1, j);
          else
            push((int) d1, j);
        }
        break;
      case 233914:
        d1 = pop();
        push(Math.sin(d1 / 57.295779513082323D), 2);
        break;
      case 8992170:
        d1 = pop();
        if (d1 >= 0.0D)
          push(Math.sqrt(d1), 2);
        else
          System.err.println("SQRT - cant sqrt a negative number!");
        break;
      case 31114:
        if (this.stack.length < 2)
          throw new RuntimeException("SUB - not enough elements on the stack");
        d1 = pop();
        j = this.currentType;
        d2 = pop();
        k = this.currentType;
        if ((j == 2) || (k == 2))
          push(d2 - d1, 2);
        else
          push(d2 - d1, 1);
        break;
      case 6303719:
        d1 = pop();
        j = this.currentType;
        push((int) d1, j);
        break;
      case 2190935:
        push(1.0D, 3);
        break;
      case 308279:
        n = popInt();
        j = this.currentType;
        i6 = popInt();
        k = this.currentType;
        if ((j == 3) && (k == 3))
          push(n ^ i6, 3);
        else
        {
          if ((j != 1) || (k != 1))
            break;
          push(n ^ i6, 1);
        }
        break;
      default:
        i = -1;
    }
    return i;
  }

  private void calculateAtan()
  {
    double d1 = pop();
    double d2 = pop();
    if ((d1 == 0.0D) && (d2 == 0.0D))
      System.err.println("ATAN - invalid parameters");
    double d3 = d2 / d1;
    if ((d1 >= 0.0D) && (d2 >= 0.0D))
      push(Math.toDegrees(Math.atan(d3)), 2);
    else
    {
      double d4;
      if ((d1 > 0.0D) && (d2 <= 0.0D))
      {
        d4 = Math.toDegrees(Math.atan(d3));
        if (d4 < 0.0D)
          d4 = -d4;
        push(d4 + 90.0D, 2);
      }
      else if ((d1 <= 0.0D) && (d2 <= 0.0D))
      {
        d4 = Math.toDegrees(Math.atan(d3));
        if (d4 < 0.0D)
          d4 = -d4;
        push(d4 + 180.0D, 2);
      }
      else if ((d1 <= 0.0D) && (d2 >= 0.0D))
      {
        d4 = Math.toDegrees(Math.atan(d3));
        if (d4 < 0.0D)
          d4 = -d4;
        push(d4 + 270.0D, 2);
      }
    }
  }

  private void calculateDup()
  {
    double d = pop();
    int i = this.currentType;
    push(d, i);
    push(d, i);
  }

  private void calculateIndex()
  {
    int i = popInt();
    if (i == 0)
      calculateDup();
    else if (i > 0)
    {
      double[] arrayOfDouble = new double[i];
      int[] arrayOfInt = new int[i];
      for (int j = 0; j < arrayOfDouble.length; j++)
      {
        arrayOfDouble[j] = pop();
        arrayOfInt[j] = this.currentType;
      }
      double d = pop();
      int k = this.currentType;
      push(d, k);
      for (int m = arrayOfDouble.length; m > 0; m--)
        push(arrayOfDouble[(m - 1)], arrayOfInt[(m - 1)]);
      push(d, k);
    }
    else if (i < 0)
      System.err.println("-> Index : critical error, n has to be nonnegative");
  }

  private void calculateRoll()
  {
    int i = popInt();
    int j = popInt();
    if ((j >= 0) || (j > this.stkPtr))
      j = this.stkPtr;
    double[] arrayOfDouble1;
    int[] arrayOfInt1;
    double[] arrayOfDouble2;
    int[] arrayOfInt2;
    int k;
    if (i > 0)
    {
      arrayOfDouble1 = new double[i];
      arrayOfInt1 = new int[i];
      if (j - i <= 0)
        return;
      arrayOfDouble2 = new double[j - i];
      arrayOfInt2 = new int[j - i];
      for (k = 0; k < arrayOfDouble1.length; k++)
      {
        arrayOfDouble1[k] = pop();
        arrayOfInt1[k] = this.currentType;
      }
      for (k = 0; k < arrayOfDouble2.length; k++)
      {
        arrayOfDouble2[k] = pop();
        arrayOfInt2[k] = this.currentType;
      }
      for (k = arrayOfDouble1.length; k > 0; k--)
        push(arrayOfDouble1[(k - 1)], arrayOfInt1[(k - 1)]);
      for (k = arrayOfDouble2.length; k > 0; k--)
        push(arrayOfDouble2[(k - 1)], arrayOfInt2[(k - 1)]);
    }
    else if (i < 0)
    {
      i = -i;
      arrayOfDouble1 = new double[j - i];
      arrayOfInt1 = new int[j - i];
      arrayOfDouble2 = new double[i];
      arrayOfInt2 = new int[i];
      for (k = 0; k < arrayOfDouble1.length; k++)
      {
        arrayOfDouble1[k] = pop();
        arrayOfInt1[k] = this.currentType;
      }
      for (k = 0; k < arrayOfDouble2.length; k++)
      {
        arrayOfDouble2[k] = pop();
        arrayOfInt2[k] = this.currentType;
      }
      for (k = arrayOfDouble1.length; k > 0; k--)
        push(arrayOfDouble1[(k - 1)], arrayOfInt1[(k - 1)]);
      for (k = arrayOfDouble2.length; k > 0; k--)
        push(arrayOfDouble2[(k - 1)], arrayOfInt2[(k - 1)]);
    }
  }

  public double[] executePostscript()
  {
    int i = 0;
    this.ptr = 0;
    this.level = 0;
    while (this.ptr < this.streamLength)
    {
      byte[] arrayOfByte = getNextValue();
      if (arrayOfByte != null)
        if ((arrayOfByte.length == 1) && ((arrayOfByte[0] == 123) || (arrayOfByte[0] == 125)))
        {
          if ((i != 0) && (arrayOfByte[0] == 123))
          {
            double d1 = pop();
            int m = this.currentType;
            this.safeStack = new double[100];
            this.safeStackType = new int[100];
            System.arraycopy(this.stack, 0, this.safeStack, 0, 100);
            System.arraycopy(this.stackType, 0, this.safeStackType, 0, 100);
            this.safeStkPtr = this.stkPtr;
            this.safeStkTypePrt = this.stkTypePtr;
            if (m == 3)
            {
              if (d1 > 0.0D)
                this.cont = true;
            }
            else
              throw new RuntimeException("Possible syntax error in PostScript stream!");
          }
          i = 1;
        }
        else
        {
          int j = getCommandID(arrayOfByte);
          if (j == -1)
            try
            {
              double d2 = convertToDouble(arrayOfByte);
              int n = (int) d2;
              if (n == d2)
                push(d2, 1);
              else
                push(d2, 2);
            }
            catch (Exception localException)
            {
            }
          else
          {
            int k = execute(j);
            if (k != -1);
          }
        }
      if (this.level == 0)
        break;
      if (this.ptr < this.streamLength)
        continue;
    }
    return this.stack;
  }

  public void showStack()
  {
    String str = "Stack now ";
    for (int i = 0; i < this.stkPtr; i++)
      str = str + this.stack[i] + ' ';
    System.out.println(str);
  }

  private void push(double paramDouble, int paramInt)
  {
    if ((this.stkPtr <= 99) && (this.stkTypePtr <= 99))
    {
      this.stack[this.stkPtr] = paramDouble;
      this.stackType[this.stkTypePtr] = paramInt;
    }
    this.stkPtr += 1;
    this.stkTypePtr += 1;
  }

  private double pop()
  {
    double d = 0.0D;
    this.stkPtr -= 1;
    this.stkTypePtr -= 1;
    if (this.stkTypePtr >= 0)
      this.currentType = this.stackType[this.stkTypePtr];
    if (this.stkPtr >= 0)
      d = this.stack[this.stkPtr];
    return d;
  }

  private int popInt()
  {
    return (int) pop();
  }

  private static double convertToDouble(byte[] paramArrayOfByte)
  {
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = 0.0D;
    int i = 0;
    int j = paramArrayOfByte.length;
    int k = j;
    int m = 0;
    int n = 0;
    int i1;
    for (i1 = j - 1; i1 > -1; i1--)
    {
      if (paramArrayOfByte[(i + i1)] != 46)
        continue;
      k = i1;
      break;
    }
    i1 = k;
    if (paramArrayOfByte[i] == 43)
    {
      i1--;
      m++;
    }
    else if (paramArrayOfByte[i] == 45)
    {
      m++;
      n = 1;
    }
    int i2 = i1 - m;
    int i3 = j - k;
    if (i2 > 3)
    {
      n = 0;
      d1 = Double.parseDouble(new String(paramArrayOfByte));
    }
    else
    {
      double d4 = 0.0D;
      double d5 = 0.0D;
      double d6 = 0.0D;
      double d7 = 0.0D;
      double d8 = 0.0D;
      double d9 = 0.0D;
      double d10 = 0.0D;
      double d11 = 0.0D;
      int i4;
      if (i2 > 2)
      {
        i4 = paramArrayOfByte[(i + m)] - 48;
        switch (i4)
        {
          case 1:
            d6 = 100.0D;
            break;
          case 2:
            d6 = 200.0D;
            break;
          case 3:
            d6 = 300.0D;
            break;
          case 4:
            d6 = 400.0D;
            break;
          case 5:
            d6 = 500.0D;
            break;
          case 6:
            d6 = 600.0D;
            break;
          case 7:
            d6 = 700.0D;
            break;
          case 8:
            d6 = 800.0D;
            break;
          case 9:
            d6 = 900.0D;
        }
        m++;
      }
      if (i2 > 1)
      {
        i4 = paramArrayOfByte[(i + m)] - 48;
        switch (i4)
        {
          case 1:
            d5 = 10.0D;
            break;
          case 2:
            d5 = 20.0D;
            break;
          case 3:
            d5 = 30.0D;
            break;
          case 4:
            d5 = 40.0D;
            break;
          case 5:
            d5 = 50.0D;
            break;
          case 6:
            d5 = 60.0D;
            break;
          case 7:
            d5 = 70.0D;
            break;
          case 8:
            d5 = 80.0D;
            break;
          case 9:
            d5 = 90.0D;
        }
        m++;
      }
      if (i2 > 0)
      {
        i4 = paramArrayOfByte[(i + m)] - 48;
        switch (i4)
        {
          case 1:
            d4 = 1.0D;
            break;
          case 2:
            d4 = 2.0D;
            break;
          case 3:
            d4 = 3.0D;
            break;
          case 4:
            d4 = 4.0D;
            break;
          case 5:
            d4 = 5.0D;
            break;
          case 6:
            d4 = 6.0D;
            break;
          case 7:
            d4 = 7.0D;
            break;
          case 8:
            d4 = 8.0D;
            break;
          case 9:
            d4 = 9.0D;
        }
      }
      if (i3 > 1)
      {
        k++;
        i4 = paramArrayOfByte[(i + k)] - 48;
        switch (i4)
        {
          case 1:
            d7 = 0.1D;
            break;
          case 2:
            d7 = 0.2D;
            break;
          case 3:
            d7 = 0.3D;
            break;
          case 4:
            d7 = 0.4D;
            break;
          case 5:
            d7 = 0.5D;
            break;
          case 6:
            d7 = 0.6D;
            break;
          case 7:
            d7 = 0.7D;
            break;
          case 8:
            d7 = 0.8D;
            break;
          case 9:
            d7 = 0.9D;
        }
      }
      if (i3 > 2)
      {
        k++;
        i4 = paramArrayOfByte[(i + k)] - 48;
        switch (i4)
        {
          case 1:
            d8 = 0.01D;
            break;
          case 2:
            d8 = 0.02D;
            break;
          case 3:
            d8 = 0.03D;
            break;
          case 4:
            d8 = 0.04D;
            break;
          case 5:
            d8 = 0.05D;
            break;
          case 6:
            d8 = 0.06D;
            break;
          case 7:
            d8 = 0.07000000000000001D;
            break;
          case 8:
            d8 = 0.08D;
            break;
          case 9:
            d8 = 0.09D;
        }
      }
      if (i3 > 3)
      {
        k++;
        i4 = paramArrayOfByte[(i + k)] - 48;
        switch (i4)
        {
          case 1:
            d9 = 0.001D;
            break;
          case 2:
            d9 = 0.002D;
            break;
          case 3:
            d9 = 0.003D;
            break;
          case 4:
            d9 = 0.004D;
            break;
          case 5:
            d9 = 0.005D;
            break;
          case 6:
            d9 = 0.006D;
            break;
          case 7:
            d9 = 0.007D;
            break;
          case 8:
            d9 = 0.008D;
            break;
          case 9:
            d9 = 0.008999999999999999D;
        }
      }
      if (i3 > 4)
      {
        k++;
        i4 = paramArrayOfByte[(i + k)] - 48;
        switch (i4)
        {
          case 1:
            d10 = 0.0001D;
            break;
          case 2:
            d10 = 0.0002D;
            break;
          case 3:
            d10 = 0.0003D;
            break;
          case 4:
            d10 = 0.0004D;
            break;
          case 5:
            d10 = 0.0005D;
            break;
          case 6:
            d10 = 0.0006D;
            break;
          case 7:
            d10 = 0.0007D;
            break;
          case 8:
            d10 = 0.0008D;
            break;
          case 9:
            d10 = 0.0009D;
        }
      }
      if (i3 > 5)
      {
        k++;
        i4 = paramArrayOfByte[(i + k)] - 48;
        switch (i4)
        {
          case 1:
            d11 = 1.E-005D;
            break;
          case 2:
            d11 = 2.E-005D;
            break;
          case 3:
            d11 = 3.E-005D;
            break;
          case 4:
            d11 = 4.E-005D;
            break;
          case 5:
            d11 = 5.E-005D;
            break;
          case 6:
            d11 = 6.E-005D;
            break;
          case 7:
            d11 = 6.999999999999999E-005D;
            break;
          case 8:
            d11 = 8.000000000000001E-005D;
            break;
          case 9:
            d11 = 9.000000000000001E-005D;
        }
      }
      d2 = d7 + d8 + d9 + d10 + d11;
      d3 = d6 + d5 + d4;
      d1 = d3 + d2;
    }
    if (n != 0)
      return -d1;
    return d1;
  }

  private byte[] getNextValue()
  {
    int k = 0;
    byte[] arrayOfByte = null;
    while ((this.ptr < this.streamLength) && ((this.stream[this.ptr] == 10) || (this.stream[this.ptr] == 13) || (this.stream[this.ptr] == 32)))
      this.ptr += 1;
    int i = this.ptr;
    while (this.ptr < this.streamLength)
    {
      k = this.stream[this.ptr];
      if (k == 123)
        break;
      this.ptr += 1;
      if (this.ptr >= this.streamLength)
        break;
      k = this.stream[this.ptr];
      if ((k == 32) || (k == 13) || (k == 10) || (k == 123))
        break;
      if (k != 125)
        continue;
    }
    if (this.stream[i] == 123)
    {
      this.ptr += 1;
      this.level += 1;
    }
    else if (this.stream[i] == 125)
      this.level -= 1;
    int j = this.ptr;
    if (j >= i)
    {
      while ((j - i > 1) && ((this.stream[(j - 1)] == 48) || (this.stream[(j - 1)] == 46)))
        j--;
      int m = j - i;
      arrayOfByte = new byte[m];
      System.arraycopy(this.stream, i, arrayOfByte, i - i, j - i);
    }
    return arrayOfByte;
  }

  public void resetStacks(float[] array)
  {
    this.stack = new double[100];
    this.stackType = new int[100];
    this.stkPtr = 0;
    this.stkTypePtr = 0;
    int i;
    for (i = 0; i < 100; i++)
      this.stack[i] = 0.0D;
    for (i = 0; i < 100; i++)
      this.stackType[i] = 0;
    i = array.length;
    for (int j = 0; j < i; j++)
      push(array[j], 2);
  }
}