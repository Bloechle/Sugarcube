package sugarcube.formats.pdf.reader.pdf.node.colorspace;

import java.awt.color.ColorSpace;

public final class CMYKColorSpace extends ColorSpace
{
  public static final String TYPE_ADOBE = "adobe";
  public static final String TYPE_SIMPLE = "simple";
  public static final String TYPE_GENERIC = "generic";
  public static final String TYPE_PHOTO = "photo";
  public static final String TYPE_GHOSTSCRIPT = "ghostscript";
  public static final String TYPE_GS = "gs";
  public static final String TYPE_SWOP = "swop";
  private static final int[] b =
  { 16777215, 11909049, 7632248, 2301728, 16775867, 12104841, 7696983, 2104588, 16774517, 12169302, 7696435, 1841920, 16773632, 12233984, 7695872,
      1579520, 16235477, 11634588, 7492708, 2295057, 16431007, 11765109, 7492424, 2163968, 16495720, 11764298, 7491881, 1901824, 16560662, 11763721,
      7491584, 1770752, 15825583, 11359872, 7418192, 2228225, 15956100, 11425120, 7352633, 2031617, 15955801, 11425086, 7287070, 1900545, 16021025,
      11424785, 7287040, 1835009, 15466636, 11141221, 7274558, 1376257, 15535723, 11141196, 7209002, 1441793, 15538250, 11142706, 7143443, 1441793,
      15539236, 11144980, 7143424, 1441793, 10608377, 7709110, 4745078, 71714, 11327930, 8101513, 4941143, 5903, 11720317, 8297307, 5006135, 5888,
      12047411, 8427810, 5071113, 5888, 11117777, 8091802, 5065315, 655381, 11510431, 8288116, 5196360, 393219, 11772014, 8418638, 5195821, 512,
      11968308, 8483617, 5195525, 512, 11300526, 8277631, 5253969, 4, 11562629, 8408673, 5254202, 0, 11693406, 8408642, 5254179, 0, 11824178,
      8473885, 5254144, 0, 11411598, 8323431, 5308479, 0, 11609198, 8392014, 5308461, 0, 11610192, 8393782, 5242904, 0, 11676208, 8394778, 5242880,
      0, 181235, 495282, 23412, 4899, 4177849, 2788488, 350807, 4369, 5356673, 3574366, 1202234, 4096, 6076999, 4032562, 1595160, 3840, 5542606,
      4025240, 2048610, 278, 6328735, 4483700, 2310473, 3, 6786929, 4745553, 2507055, 0, 7179842, 4941868, 2572305, 0, 6972845, 5064575, 2958673, 7,
      7300487, 5261410, 3024700, 0, 7497058, 5326917, 3024934, 0, 7693372, 5457702, 3024906, 0, 7678864, 5640809, 3342402, 0, 7876209, 5642321,
      3342383, 0, 7876948, 5643065, 3276828, 0, 8008246, 5709088, 3277057, 0, 44783, 32687, 21107, 1316, 43959, 32391, 20823, 1299, 43139, 31840,
      20539, 1280, 42576, 31547, 20255, 1280, 34252, 24982, 14945, 279, 33950, 24692, 15177, 4, 33395, 24403, 14897, 0, 33097, 24115, 14872, 0,
      24493, 17023, 8529, 9, 24200, 16995, 9020, 0, 24164, 16967, 9256, 0, 1334849, 213547, 9232, 0, 3027090, 2038380, 393284, 0, 3355507, 2170195,
      393265, 0, 3487062, 2236220, 393246, 0, 3552825, 2367524, 459270, 0 };
  private static final int[] c =
  { 16121554, 7764840, 3158571, 592136, 14872696, 7238974, 2895642, 526598, 14083646, 6778658, 2698511, 526340, 13623067, 6449936, 2632456, 460804,
      11705226, 5787207, 2433310, 526086, 10982481, 5458731, 2236179, 460548, 10456876, 5130264, 2104843, 460548, 10128148, 4932876, 2039046, 460291,
      8871769, 4403502, 1905940, 460037, 8412213, 4206365, 1774861, 394500, 8149534, 4075025, 1709064, 394499, 7952399, 3943689, 1643525, 394755,
      6961206, 3480861, 1510669, 394244, 6764322, 3415315, 1445129, 394499, 6633236, 3284236, 1445126, 394499, 6502155, 3284231, 1445380, 394499,
      9742784, 4937312, 2106408, 460552, 8822894, 4477242, 1909273, 395014, 8231227, 4148512, 1777679, 395012, 7968028, 3951120, 1646344, 395012,
      6973312, 3684163, 1644573, 394758, 6513484, 3421481, 1513234, 394757, 6119210, 3158551, 1381899, 329220, 5922069, 3027212, 1316103, 329219,
      5258837, 2826797, 1249300, 328965, 4996404, 2629916, 1183501, 328964, 4733726, 2498577, 1117960, 328963, 4602384, 2432777, 1052165, 328963,
      4072502, 2167837, 985614, 328708, 3941154, 2102035, 985609, 328707, 3810069, 2036492, 920070, 328707, 3744524, 1970951, 920069, 328707,
      5862577, 3161434, 1383206, 329224, 5139815, 2767159, 1251608, 329222, 4613944, 2504223, 1120270, 329220, 4351004, 2307344, 1054473, 329220,
      4278137, 2369344, 1053212, 328966, 3818569, 2106664, 987410, 328965, 3490089, 1909783, 921611, 263428, 3292949, 1778445, 856071, 263427,
      3156819, 1775660, 855060, 263173, 2894131, 1644316, 789261, 263172, 2631710, 1513233, 723721, 263171, 2565904, 1447434, 723718, 263171,
      2431031, 1379870, 657166, 262916, 2234147, 1248788, 657162, 263172, 2103062, 1182989, 591623, 263171, 2037261, 1182984, 591621, 263171,
      3560103, 1977430, 922661, 329224, 2837602, 1648948, 791319, 263686, 2443062, 1386270, 725518, 263685, 2180124, 1254929, 659977, 263684,
      2569077, 1449277, 724507, 263174, 2109511, 1252134, 658705, 263429, 1781032, 1055255, 593163, 263428, 1649686, 989453, 527367, 263427, 1908562,
      1118764, 592148, 263173, 1580339, 921884, 526605, 263172, 1383198, 856081, 461065, 263171, 1252113, 790538, 461062, 263171, 1446201, 854559,
      460303, 262916, 1183780, 723476, 460298, 262916, 1052438, 657933, 394759, 197379, 921357, 592136, 394757, 197379 };
  private static final int[] d =
  { 16777215, 11251373, 6843242, 0, 16775608, 11512962, 6842448, 0, 16774263, 11446357, 6841909, 0, 16773384, 11379740, 6972951, 0, 15644363,
      10780815, 6573400, 0, 15839898, 10846064, 6573380, 0, 15839081, 10845001, 6441516, 0, 15772717, 10778656, 6441494, 0, 14972067, 10309747,
      6303558, 0, 14971260, 10244184, 6106676, 0, 14905688, 10243645, 6041124, 0, 14904875, 10112289, 5975571, 0, 14095742, 9768025, 5899573, 0,
      14163297, 9702979, 5572389, 0, 14164038, 9705265, 5441818, 0, 14165035, 9772323, 5705490, 0, 10541548, 7510183, 4349287, 0, 10867889, 7772033,
      4545614, 0, 11128953, 7770710, 4479285, 0, 11456058, 7704362, 4544542, 0, 10591939, 7631756, 4605783, 0, 10853525, 7630955, 4539456, 0,
      10918249, 7695690, 4473389, 0, 11048505, 7564072, 4473113, 0, 10446492, 7424112, 4336195, 0, 10577785, 7424086, 4270385, 0, 10642776, 7424061,
      4205091, 0, 10708277, 7358245, 4205076, 0, 10233981, 7082581, 3802158, 0, 10299489, 7018562, 3737890, 0, 10234697, 6953776, 3672855, 0,
      10300721, 6888992, 3673613, 0, 2276322, 2196640, 1200740, 0, 4372395, 2982523, 1658956, 0, 4830075, 3506006, 1789751, 0, 5747015, 3767602,
      2051616, 0, 5343419, 4088710, 2243410, 0, 5998994, 4285288, 2440255, 0, 6326121, 4415819, 2439725, 0, 6522177, 4480813, 2504987, 0, 6446490,
      4538733, 2368318, 0, 6577528, 4604243, 2499631, 0, 6774362, 4604222, 2434082, 0, 6773819, 4603943, 2434068, 0, 6630010, 4527954, 2229547, 0,
      6631008, 4463167, 2230560, 0, 6631497, 4398382, 2231062, 0, 6697012, 4398623, 2231305, 0, 42203, 30108, 18787, 0, 41126, 29302, 17994, 0,
      39799, 28758, 17462, 0, 39242, 28987, 18212, 0, 31928, 22402, 12878, 0, 31374, 21860, 12602, 0, 30311, 21577, 12330, 0, 30278, 21296, 12572, 0,
      21655, 14697, 6970, 0, 349302, 604496, 138540, 0, 1529177, 866619, 138271, 0, 1921853, 1128231, 138515, 0, 2567545, 1580372, 524331, 0,
      3026783, 1579067, 459808, 0, 2895944, 1645098, 263701, 0, 2895921, 1710619, 263943, 0 };
  private static final int[] e =
  { 16121554, 6712152, 2303262, 131842, 14609522, 6317876, 2106130, 131585, 13754684, 5857564, 1974538, 131584, 13360154, 5463053, 1908997, 66048,
      10653563, 4735289, 1644052, 131329, 9996360, 4538148, 1512717, 65793, 9536039, 4209426, 1381383, 65792, 9075986, 3946249, 1381124, 65792,
      7622985, 3351586, 1182988, 65793, 7163179, 3154709, 1051655, 65792, 6900504, 3023371, 985860, 65536, 6637579, 2891782, 920322, 65536, 5516073,
      2429715, 853767, 65536, 5384985, 2298379, 722436, 65536, 5253646, 2298631, 656898, 65536, 5188103, 2298628, 722434, 65536, 9019306, 4213070,
      1448219, 65794, 8033377, 3819056, 1316881, 65793, 7441460, 3424281, 1185289, 65792, 7178521, 3095564, 1119493, 256, 6118252, 3026229, 1052691,
      65793, 5592640, 2631967, 921355, 65793, 5198115, 2434833, 855558, 0, 4935440, 2237704, 789763, 0, 4206913, 2037792, 657419, 65793, 3878696,
      1840915, 591878, 0, 3681814, 1709579, 591620, 0, 3550475, 1643781, 526082, 0, 3021351, 1379089, 393989, 65536, 2890007, 1248011, 328451, 65536,
      2758670, 1182470, 328450, 65536, 2693127, 1116931, 328449, 65536, 5138326, 2437446, 922137, 257, 4415318, 2108713, 790543, 257, 3889456,
      1845782, 659209, 0, 3692311, 1714187, 593669, 0, 3357024, 1711407, 592401, 257, 3029050, 1514525, 526858, 1, 2766112, 1317392, 461062, 0,
      2568976, 1185800, 461059, 0, 2367549, 1117981, 328969, 1, 2104869, 986897, 328966, 0, 1973526, 921098, 263427, 0, 1776651, 855301, 263170, 0,
      1576741, 722448, 197125, 65537, 1445399, 591370, 197123, 0, 1314317, 525830, 131586, 0, 1248519, 525827, 131585, 0, 2966921, 1384768, 527640,
      1, 2310222, 1121829, 396046, 1, 1849641, 924693, 330248, 0, 1586708, 859148, 330501, 0, 1976411, 922411, 329231, 1, 1582390, 725529, 263688, 1,
      1253661, 659726, 198149, 0, 1188111, 593928, 198147, 0, 1250362, 592410, 131848, 1, 987939, 461072, 131845, 1, 856596, 395273, 131587, 0,
      725259, 329733, 131585, 0, 788771, 328720, 65540, 1, 657686, 262920, 65795, 1, 526349, 197381, 65793, 0, 460550, 197378, 256, 0 };
  private static final int[] f =
  { 16777215, 12105653, 6447458, 0, 16711126, 12039832, 6381138, 0, 16775303, 12104282, 6511920, 0, 16771328, 12231168, 6509056, 0, 16432361,
      12094634, 6507357, 0, 16563908, 12028815, 6507088, 261, 16694648, 12159318, 6441264, 514, 16755456, 12089600, 6504192, 5, 16736969, 12141711,
      6496589, 65536, 16672930, 12012150, 6366786, 263, 16607835, 11948100, 6237479, 514, 16340992, 11683072, 6235138, 5, 14352480, 10289225,
      5505064, 0, 14352460, 10223676, 5439523, 1, 14286888, 10289185, 5505042, 256, 14876672, 10616832, 5701632, 0, 10806777, 8234419, 4609632, 0,
      11068883, 8103578, 4544087, 8, 11264135, 8494941, 4674099, 0, 11717888, 8752128, 4932864, 262144, 11842281, 8750250, 4802653, 1024, 11907778,
      8552335, 4671312, 1284, 12104057, 8748118, 4867633, 1536, 12164352, 8940800, 4799232, 768, 12474822, 9126285, 4989515, 262147, 12344735,
      8930421, 4924482, 65800, 12345693, 8995910, 5056041, 131587, 12210688, 9190144, 5317381, 262148, 10485856, 7733320, 4259881, 196608, 10354765,
      7602236, 4259876, 131073, 10616877, 7929894, 4456470, 131328, 12255232, 9634048, 5180677, 327680, 51702, 37296, 85086, 0, 50891, 37526, 19798,
      8, 49276, 35416, 739634, 0, 4107264, 3176704, 2113281, 131072, 3906788, 2782120, 1785435, 1536, 4038078, 2584972, 1457742, 1796, 4495730,
      3107665, 1719598, 2048, 5603584, 4349696, 2308096, 1280, 6768830, 5058951, 2825032, 131077, 6573211, 4798067, 2760000, 9, 6639710, 4665668,
      2826282, 514, 6374912, 4795904, 3219714, 263171, 5767263, 4522056, 2883627, 196608, 5898320, 4784195, 2883626, 65539, 5439524, 4587554,
      3080219, 196864, 6553600, 5046272, 3802624, 656387, 39659, 27559, 13913, 0, 39358, 27790, 14417, 8, 38002, 26196, 13359, 0, 30976, 23048,
      12294, 0, 30168, 21147, 11349, 1024, 30384, 21121, 11080, 1027, 29549, 20561, 10536, 1024, 25610, 20739, 12289, 768, 10410, 7291, 3140, 3,
      11659, 7529, 3645, 7, 11602, 7998, 4390, 259, 11010, 8705, 7172, 262660, 2097256, 1835089, 1507379, 0, 2097242, 1769550, 1376304, 1, 2097205,
      1835056, 1572901, 0, 1769472, 2492416, 2097158, 0 };
  private static final int[] g =
  { 16121554, 7896164, 2237213, 65793, 14807675, 7239483, 2105874, 65793, 13624112, 6713366, 1908744, 65793, 12832777, 6119686, 1710851, 65793,
      12362407, 6444629, 1972762, 65793, 11706208, 5984817, 1841425, 65793, 10852384, 5656082, 1644295, 65793, 10389766, 5063429, 1512451, 65793,
      9854326, 4992826, 1511186, 65793, 9132859, 4599584, 1445644, 65793, 8607761, 4468491, 1314565, 65793, 7883780, 4008195, 1248514, 65793,
      5711899, 2888720, 919303, 65793, 5580814, 2823433, 919301, 65793, 5449989, 2823684, 919554, 65792, 6041346, 3021058, 985090, 65793, 10204610,
      5332064, 1579804, 65793, 9285235, 4806714, 1448211, 65793, 8233259, 4214548, 1316616, 65793, 7244552, 3686917, 1184515, 65793, 8420517,
      4539221, 1447450, 65793, 7567196, 3816241, 1315857, 66049, 6778655, 3487761, 1184519, 66049, 5987590, 3091972, 986883, 65793, 6176113, 3219768,
      1051666, 65793, 5454648, 2891807, 985868, 65793, 4929552, 2563338, 986118, 65793, 4468739, 2562819, 986115, 65793, 3151386, 1772560, 656647,
      65793, 3020302, 1772811, 656645, 65793, 3151877, 1838597, 722435, 65793, 4071682, 2495746, 919810, 65793, 5667258, 2965084, 987931, 65793,
      4682087, 2505525, 856594, 65793, 3695393, 1913616, 659463, 65792, 3166727, 1714693, 593411, 65793, 4608155, 2501714, 855833, 66049, 3754836,
      1910316, 724496, 66049, 2966297, 1516047, 593158, 66049, 2306565, 1383684, 592898, 65793, 3022953, 1642804, 657169, 65793, 2301237, 1314846,
      591628, 65793, 1973264, 986377, 460549, 131585, 1578499, 920578, 526082, 131585, 1247259, 787729, 393991, 65793, 1116176, 853516, 459783,
      65793, 853251, 590851, 394243, 65792, 1247745, 722433, 525570, 131586, 2703526, 1318738, 461336, 65792, 1653075, 859182, 330256, 65793, 995353,
      595983, 264198, 256, 729862, 529413, 264195, 65793, 2107786, 1054022, 395286, 65793, 1320008, 725800, 263949, 65793, 728598, 397325, 198405,
      65793, 661765, 463108, 264194, 65793, 1248597, 657197, 263183, 65793, 723499, 394778, 197387, 65793, 329740, 197640, 131844, 65793, 198402,
      197890, 197634, 65793, 590624, 393747, 196871, 65793, 459540, 394000, 197127, 65793, 197125, 131075, 197124, 66049, 131584, 328706, 197122,
      65793 };
//   public static CMYKColorSpace GENERIC = CMYKColorSpace.instance(TYPE_GENERIC);
  public static ColorSpace GENERIC = Profiles.ADOBE_CMYK_CS();
  // public static CMYKColorSpace PHOTO = CMYKColorSpace.instance(TYPE_PHOTO);
  // public static CMYKColorSpace ADOBE = CMYKColorSpace.instance(TYPE_ADOBE);
  private final int[] h;
  private final int[] i;
  private final int j;
  private int k;
  private int l;

  private CMYKColorSpace(int[] h, int[] i, int j)
  {
    super(TYPE_CMYK, 4);
    this.h = h;
    this.i = i;
    this.j = j;
  }

  private static CMYKColorSpace instance(String type)
  {
    if ((TYPE_SIMPLE.equalsIgnoreCase(type)) || (TYPE_ADOBE.equalsIgnoreCase(type)))
      return new CMYKColorSpace(null, null, 0);
    if (TYPE_GENERIC.equalsIgnoreCase(type))
      return new CMYKColorSpace(d, e, 0);
    if (TYPE_PHOTO.equalsIgnoreCase(type))
      return new CMYKColorSpace(f, g, 0);
    if ((TYPE_GHOSTSCRIPT.equalsIgnoreCase(type)) || (TYPE_GS.equalsIgnoreCase(type)))
      return new CMYKColorSpace(null, null, 1);
    if (TYPE_SWOP.equalsIgnoreCase(type))
      return new CMYKColorSpace(b, c, 0);
    return new CMYKColorSpace(d, e, 0);
  }

  @Override
  public float[] toRGB(float[] cmyk)
  {
    float[] rgb = new float[3];
    if (this.h == null)
    {
      float k;
      if (this.j == 1)
      {
        k = 1.0F - cmyk[3];
        rgb[0] = (k * (1.0F - cmyk[0]));
        rgb[1] = (k * (1.0F - cmyk[1]));
        rgb[2] = (k * (1.0F - cmyk[2]));
      } else
      {
        k = cmyk[3];
        rgb[0] = (1.0F - Math.min(1.0F, cmyk[0] + k));
        rgb[1] = (1.0F - Math.min(1.0F, cmyk[1] + k));
        rgb[2] = (1.0F - Math.min(1.0F, cmyk[2] + k));
      }
    } else
    {
      int m = a(Math.round(cmyk[0] * 255.0F), Math.round(cmyk[1] * 255.0F), Math.round(cmyk[2] * 255.0F), Math.round(cmyk[3] * 255.0F), this.h);
      rgb[0] = ((m >> 16 & 0xFF) / 255.0F);
      rgb[1] = ((m >> 8 & 0xFF) / 255.0F);
      rgb[2] = ((m & 0xFF) / 255.0F);
    }
    return rgb;
  }

  public int[] toRGB(int[] cmyk, int[] rgb)
  {
    int m;
    if (rgb == null)
      rgb = new int[3];
    if (this.h == null)
      if (this.j == 1)
      {
        m = 255 - cmyk[3];
        rgb[0] = (m * (255 - cmyk[0]) / 255);
        rgb[1] = (m * (255 - cmyk[1]) / 255);
        rgb[2] = (m * (255 - cmyk[2]) / 255);
      } else
      {
        rgb[0] = (255 - Math.min(255, cmyk[0] + cmyk[3]));
        rgb[1] = (255 - Math.min(255, cmyk[1] + cmyk[3]));
        rgb[2] = (255 - Math.min(255, cmyk[2] + cmyk[3]));
      }
    else
    {
      m = cmyk[0];
      int n = cmyk[1];
      int i1 = cmyk[2];
      int i2 = cmyk[3];
      int i3 = (m << 24) + (n << 16) + (i1 << 8) + i2;
      int i5;
      int i6;
      synchronized (this)
      {
        i5 = this.k;
        i6 = this.l;
      }
      int i4;
      if (i3 == i5)
        i4 = i6;
      else
      {
        i4 = a(m, n, i1, i2, this.h);
        synchronized (this)
        {
          this.k = i3;
          this.l = i4;
        }
      }
      rgb[0] = (i4 >> 16 & 0xFF);
      rgb[1] = (i4 >> 8 & 0xFF);
      rgb[2] = (i4 & 0xFF);
    }
    return rgb;
  }

  @Override
  public float[] fromRGB(float[] rgb)
  {
    float[] cmyk = new float[4];
    cmyk[3] = (rgb[0] > rgb[2] ? 1.0F - Math.max(rgb[0], rgb[1]) : 1.0F - Math.max(rgb[2], rgb[1]));
    if (cmyk[3] == 1.0F)
    {
      float tmp59_58 = (cmyk[2] = 0.0F);
      cmyk[1] = tmp59_58;
      cmyk[0] = tmp59_58;
    } else
    {
      cmyk[0] = ((1.0F - rgb[0] - cmyk[3]) / (1.0F - cmyk[3]));
      cmyk[1] = ((1.0F - rgb[1] - cmyk[3]) / (1.0F - cmyk[3]));
      cmyk[2] = ((1.0F - rgb[2] - cmyk[3]) / (1.0F - cmyk[3]));
    }
    if (cmyk[0] < 0.0F)
      cmyk[0] = 0.0F;
    else if (cmyk[0] > 1.0F)
      cmyk[0] = 1.0F;
    if (cmyk[1] < 0.0F)
      cmyk[1] = 0.0F;
    else if (cmyk[1] > 1.0F)
      cmyk[1] = 1.0F;
    if (cmyk[2] < 0.0F)
      cmyk[2] = 0.0F;
    else if (cmyk[2] > 1.0F)
      cmyk[2] = 1.0F;
    if (cmyk[3] < 0.0F)
      cmyk[3] = 0.0F;
    else if (cmyk[3] > 1.0F)
      cmyk[3] = 1.0F;
    return cmyk;
  }

  private static int a(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
  {
    int m = Math.min(paramInt1 / 85, 2);
    int n = Math.min(paramInt2 / 85, 2);
    int i1 = Math.min(paramInt3 / 85, 2);
    int i2 = Math.min(paramInt4 / 85, 2);
    int i3 = (paramInt1 - m * 85) / 2;
    int i4 = (paramInt2 - n * 85) / 2;
    int i5 = (paramInt3 - i1 * 85) / 2;
    int i6 = (paramInt4 - i2 * 85) / 2;
    int i7 = m * 64 + n * 16 + i1 * 4 + i2;
    int i8 = a(i3, i4, i5, i6, paramArrayOfInt, 16, i7);
    int i9 = a(i3, i4, i5, i6, paramArrayOfInt, 8, i7);
    int i10 = a(i3, i4, i5, i6, paramArrayOfInt, 0, i7);
    int i11 = (i8 << 16) + (i9 << 8) + i10;
    return i11;
  }

  private static int a(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5, int paramInt6)
  {
    int m = 42 - paramInt1;
    int n = 42 - paramInt2;
    int i1 = 42 - paramInt3;
    int i2 = 42 - paramInt4;
    int i3 = (paramArrayOfInt[paramInt6] >> paramInt5 & 0xFF) * m * n * i1 * i2 + (paramArrayOfInt[(paramInt6 + 1)] >> paramInt5 & 0xFF) * m * n * i1
        * paramInt4 + (paramArrayOfInt[(paramInt6 + 4)] >> paramInt5 & 0xFF) * m * n * paramInt3 * i2
        + (paramArrayOfInt[(paramInt6 + 5)] >> paramInt5 & 0xFF) * m * n * paramInt3 * paramInt4
        + (paramArrayOfInt[(paramInt6 + 16)] >> paramInt5 & 0xFF) * m * paramInt2 * i1 * i2 + (paramArrayOfInt[(paramInt6 + 17)] >> paramInt5 & 0xFF)
        * m * paramInt2 * i1 * paramInt4 + (paramArrayOfInt[(paramInt6 + 20)] >> paramInt5 & 0xFF) * m * paramInt2 * paramInt3 * i2
        + (paramArrayOfInt[(paramInt6 + 21)] >> paramInt5 & 0xFF) * m * paramInt2 * paramInt3 * paramInt4
        + (paramArrayOfInt[(paramInt6 + 64)] >> paramInt5 & 0xFF) * paramInt1 * n * i1 * i2 + (paramArrayOfInt[(paramInt6 + 65)] >> paramInt5 & 0xFF)
        * paramInt1 * n * i1 * paramInt4 + (paramArrayOfInt[(paramInt6 + 68)] >> paramInt5 & 0xFF) * paramInt1 * n * paramInt3 * i2
        + (paramArrayOfInt[(paramInt6 + 69)] >> paramInt5 & 0xFF) * paramInt1 * n * paramInt3 * paramInt4
        + (paramArrayOfInt[(paramInt6 + 80)] >> paramInt5 & 0xFF) * paramInt1 * paramInt2 * i1 * i2
        + (paramArrayOfInt[(paramInt6 + 81)] >> paramInt5 & 0xFF) * paramInt1 * paramInt2 * i1 * paramInt4
        + (paramArrayOfInt[(paramInt6 + 84)] >> paramInt5 & 0xFF) * paramInt1 * paramInt2 * paramInt3 * i2
        + (paramArrayOfInt[(paramInt6 + 85)] >> paramInt5 & 0xFF) * paramInt1 * paramInt2 * paramInt3 * paramInt4;
    return i3 / 3111696;
  }

  @Override
  public float[] toCIEXYZ(float[] colorvalue)
  {
    return toRGB(colorvalue);
  }

  @Override
  public float[] fromCIEXYZ(float[] colorvalue)
  {
    return colorvalue;
  }
}