package sugarcube.formats.ocd.analysis.text;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.A;
import sugarcube.common.data.collections.IntArray;
import sugarcube.common.data.collections.Map3;
import sugarcube.common.data.xml.CharRef;

public class Ligatures
{
    public static final Map3<IntArray, Integer> MAP = new Map3<>();

    static
    {
        //latin-derived alphabets ligatures
        add("ſs", '\u00DF');
        add("ſz", '\u00DF');
        add("ue", '\u1D6B');
        add("ff", '\uFB00');
        add("fi", '\uFB01');
        add("fl", '\uFB02');
        add("ffi", '\uFB03');
        add("ffl", '\uFB04');
        add("ſt", '\uFB05');
        add("st", '\uFB06');
        //phonetic transcription
        add("db", '\u0238');
        add("qp", '\u0239');
        add("cp", '\u0239');
        add("lʒ", '\u026E');
        add("lezh", '\u026E');
        add("dz", '\u02A3');
        add("dʒ", '\u02A4');
        add("dezh", '\u02A4');
        add("dʑ", '\u02A5');
        add("ts", '\u02A6');
        add("tʃ", '\u02A7');
        add("tesh", '\u02A7');
        add("tɕ", '\u02A8');
        add("fŋ", '\u02A9');
        add("ls", '\u02AA');
        add("lz", '\u02AB');
    }

    public static int[] resolve(int[] ligatures)
    {
        if (ligatures.length < 2)
            return ligatures;
        Integer u = MAP.get(new IntArray(ligatures));
        return u == null ? ligatures : A.Ints(u);
    }

    public static int ResolveChar(int[] ligatures)
    {
        if (ligatures.length < 2)
            return -1;
        Integer u = MAP.get(new IntArray(ligatures));
        return u == null ? -1 : u;
    }

    private static void add(String ligature, char unicode)
    {
        if (CharRef.isCtrl(unicode) || !CharRef.IsValid(unicode))
            Log.debug(Ligatures.class, ".add - non valid ligature unicode: " + unicode);
        else
        {
            int[] a = new int[ligature.length()];
            for (int i = 0; i < a.length; i++)
                a[i] = ligature.charAt(i);
            MAP.put(new IntArray(a), (int) unicode);
        }
    }
}
