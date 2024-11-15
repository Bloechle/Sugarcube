package sugarcube.formats.pdf.writer;


import sugarcube.formats.pdf.resources.pdf.RS_PDF;

import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;

public class PDFColorSpaceManager {
	public final static int CYMK_COATED_FOGRA27 = 0;
	public final static int CYMK_COATED_FOGRA39 = 1;
	public final static int CYMK_COATED_GRAC_O_L2006 = 2; 
	public final static int CYMK_JAPAN_COLOR_2001_COATED = 3; 
	public final static int CYMK_JAPAN_COLOR_2001_UNCOATED = 4; 
	public final static int CYMK_JAPAN_COLOR_2002_NEWSPAPER = 5;
	public final static int CYMK_JAPAN_COLOR_2003_WEB_COATED = 6;  
	public final static int CYMK_JAPAN_WEB_COATED = 7; 
	public final static int CYMK_UNCOATED_FOGRA29 = 8;
	public final static int CYMK_US_WEB_COATED_SWOP = 9; 
	public final static int CYMK_US_WEB_UNCOATED = 10; 
	public final static int CYMK_WEB_COATED_FOGRA28 = 11;
	public final static int CYMK_WEB_COATED_SWOP_2006_GRADE_3 = 12;
	public final static int CYMK_WEB_COATED_SWOP_2006_GRADE_5 = 13;
	public final static String CYMK_COATED_FOGRA27_FILE = "CoatedFOGRA27.icc";
	public final static String CYMK_COATED_FOGRA39_FILE = "CoatedFOGRA39.icc";
	public final static String CYMK_COATED_GRAC_O_L2006_FILE = "CoatedGRACoL2006.icc"; 
	public final static String CYMK_JAPAN_COLOR_2001_COATED_FILE = "JapanColor2001Coated.icc"; 
	public final static String CYMK_JAPAN_COLOR_2001_UNCOATED_FILE = "JapanColor2001Uncoated.icc"; 
	public final static String CYMK_JAPAN_COLOR_2002_NEWSPAPER_FILE = "JapanColor2002Newspaper.icc";
	public final static String CYMK_JAPAN_COLOR_2003_WEB_COATED_FILE = "JapanColor2003WebCoated.icc";  
	public final static String CYMK_JAPAN_WEB_COATED_FILE = "JapanWebCoated.icc"; 
	public final static String CYMK_UNCOATED_FOGRA29_FILE = "UncoatedFOGRA29.icc";
	public final static String CYMK_US_WEB_COATED_SWOP_FILE = "USWebCoatedSWOP.icc"; 
	public final static String CYMK_US_WEB_UNCOATED_FILE = "USWebUncoated.icc"; 
	public final static String CYMK_WEB_COATED_FOGRA28_FILE = "WebCoatedFOGRA28.icc";
	public final static String CYMK_WEB_COATED_SWOP_2006_GRADE_3_FILE = "WebCoatedSWOP2006Grade3.icc";
	public final static String CYMK_WEB_COATED_SWOP_2006_GRADE_5_FILE = "WebCoatedSWOP2006Grade5.icc";
	
	private static ICC_Profile cmykProfile;
	private static ICC_ColorSpace cmykColorSpace;
	private static boolean checkProfile = true;
	private static String choosenColorSpace = CYMK_COATED_FOGRA27_FILE;
	
	public static void setICCColorSpace(int id) {
		switch (id) {
		case CYMK_COATED_FOGRA27:
			choosenColorSpace = CYMK_COATED_FOGRA27_FILE;
			break;
		case CYMK_COATED_FOGRA39:
			choosenColorSpace = CYMK_COATED_FOGRA39_FILE;
			break;
		case CYMK_COATED_GRAC_O_L2006:
			choosenColorSpace = CYMK_COATED_GRAC_O_L2006_FILE;
			break;
		case CYMK_JAPAN_COLOR_2001_COATED:
			choosenColorSpace = CYMK_JAPAN_COLOR_2001_COATED_FILE;
			break;
		case CYMK_JAPAN_COLOR_2001_UNCOATED:
			choosenColorSpace = CYMK_JAPAN_COLOR_2001_UNCOATED_FILE;
			break;
		case CYMK_JAPAN_COLOR_2002_NEWSPAPER:
			choosenColorSpace = CYMK_JAPAN_COLOR_2002_NEWSPAPER_FILE;
			break;
		case CYMK_JAPAN_COLOR_2003_WEB_COATED:
			choosenColorSpace = CYMK_JAPAN_COLOR_2003_WEB_COATED_FILE;
			break;
		case CYMK_JAPAN_WEB_COATED:
			choosenColorSpace = CYMK_JAPAN_WEB_COATED_FILE;
			break;
		case CYMK_UNCOATED_FOGRA29:
			choosenColorSpace = CYMK_UNCOATED_FOGRA29_FILE;
			break;
		case CYMK_US_WEB_COATED_SWOP:
			choosenColorSpace = CYMK_US_WEB_COATED_SWOP_FILE;
			break;
		case CYMK_US_WEB_UNCOATED:
			choosenColorSpace = CYMK_US_WEB_UNCOATED_FILE;
			break;
		case CYMK_WEB_COATED_FOGRA28:
			choosenColorSpace = CYMK_WEB_COATED_FOGRA28_FILE;
			break;
		case CYMK_WEB_COATED_SWOP_2006_GRADE_3:
			choosenColorSpace = CYMK_WEB_COATED_SWOP_2006_GRADE_3_FILE;
			break;
		case CYMK_WEB_COATED_SWOP_2006_GRADE_5:
			choosenColorSpace = CYMK_WEB_COATED_SWOP_2006_GRADE_5_FILE;
			break;
		default:
			System.out.println("sugarcube.formats.pdf.writer.PDFColorSpaceManager: CMYK profile unknown " + choosenColorSpace);
				
		}
	}
	
	public static float[] getCYMKColor(float[] components) {
		checkCMYKProfile();
		return cmykColorSpace.fromRGB(components);
	}
	
	public static ICC_ColorSpace getCMKYColorSpace() {
		checkCMYKProfile();
		return cmykColorSpace;
	}
		
	private static void checkCMYKProfile() {
		if (cmykProfile != null || !checkProfile) {
			return;
		}
		try {
			cmykProfile = ICC_Profile.getInstance(RS_PDF.stream("adobe/colorspace/cmyk/" + choosenColorSpace));
			cmykColorSpace = new ICC_ColorSpace(cmykProfile);
			System.out.println("loaded colorspace " + choosenColorSpace);
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("sugarcube.formats.pdf.writer.PDFColorSpaceManager: CMYK profile file not found " + choosenColorSpace);
		}
		checkProfile = false;
	}

}
