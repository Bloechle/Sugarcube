package sugarcube.formats.pdf.writer.core;

public interface Lexic {
	//white spaces characters
	public final static String NULL = "" + (char)0;
	public final static String HORIZONTAL_TAB = "" + (char)9;
	public final static String LINE_FEED = "" + (char)10;
	public final static String FORM_FEED = "" + (char)12;
	public final static String CARRIAGE_RETURN = "" + (char)13;
	public final static String SPACE = "" + (char)32;
	//delimiters
	public final static char LEFT_PARENTHESIS_CHAR = (char)40;
	public final static char RIGHT_PARENTHESIS_CHAR = (char)41;
	public final static String LEFT_PARENTHESIS = "" + LEFT_PARENTHESIS_CHAR;
	public final static String RIGHT_PARENTHESIS = "" + RIGHT_PARENTHESIS_CHAR;
	public final static String LESS_THAN = "" + (char)60;
	public final static String GREATER_THAN = "" + (char)62;
	public final static String LEFT_SQUARE_BRACKET = "" + (char)91;
	public final static String RIGHT_SQUARE_BRACKET = "" + (char)93;
	public final static String LEFT_CURLY_BRACKET = "" + (char)123;
	public final static String RIGHT_CURLY_BRACKET = "" + (char)125;
	public final static String SOLIDUS = "" + (char)47;
	public final static String REVERSED_SOLIDUS = "" + (char)92;
	public final static String PERCENT_SIGN = "" + (char)37;
	public final static String NUMBER_SIGN = "" + (char)35;
}
