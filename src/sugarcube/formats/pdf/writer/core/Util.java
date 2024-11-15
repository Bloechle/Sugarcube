package sugarcube.formats.pdf.writer.core;

import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.formats.pdf.writer.exception.PDFException;

import java.security.MessageDigest;
import java.util.Date;

public class Util {
	private static float PRECISION_FLOAT = 1000f;
	private static double PRECISION_DOUBLE = 1000.;	
	
	public final static float format(float number){
		return Math.round(number * PRECISION_FLOAT) / PRECISION_FLOAT;
	}
	public final static double format(double number){
		return Math.round(number * PRECISION_DOUBLE) / PRECISION_DOUBLE;
	}
	
	public final static Float[] rectangleToFloatArray(Rectangle3 rectangle, float scale, boolean reverse){
		//System.out.println("pdf.core.Util: Check rectangle compliance: problem with JPedal");
		Float[] values = new Float[4];
		int y1 = reverse ? 3 : 1;
		int y2 = reverse ? 1 : 3;
		values[0] = (float)rectangle.x * scale;
		values[y1] = (float)rectangle.y * scale;
		values[2] = (float)(rectangle.x + rectangle.width) * scale;
		values[y2] = (float)(rectangle.y + rectangle.height) * scale;
		return values;
	}
	
	public final static Float[] rectangleToFloatArray(Rectangle3 rectangle, boolean reverse){
		return rectangleToFloatArray(rectangle, 1, reverse);
	}
	
	public final static Float[] rectangleToFloatArray(Rectangle3 rectangle){
		return rectangleToFloatArray(rectangle, false);
	}
	
	public final static String generateFileID(String filename) throws PDFException{
		String text = new Date(System.currentTimeMillis()).toString();
		text += filename;
		MessageDigest msgDigest;
		try {
			msgDigest = MessageDigest.getInstance("MD5");
		    msgDigest.update(text.getBytes("ASCII"));
		    byte[] digest = msgDigest.digest();
		    String result = "";
		    String hexaValue;
		    for (int i = 0; i < digest.length; ++i) {
		        int value = digest[i];
		        if (value < 0) {
		            value += 256;
		        }
		        hexaValue = Integer.toHexString(value);
		        if (hexaValue.length() < 2)
		        	hexaValue = "0" + hexaValue;
		        result += hexaValue;
		    }
		    result = Lexic.LESS_THAN + result + Lexic.GREATER_THAN;
		    return result;
		} catch (Exception e) {
			throw new PDFException("FileTrailer: unable to generate document ID");
		}
		
	}

}
