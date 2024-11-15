package sugarcube.formats.pdf.writer.core.object;

import sugarcube.formats.pdf.writer.PDFWriter;
import sugarcube.formats.pdf.writer.exception.PDFException;

public abstract class ContainerObject {
	private int id = -1;
	private PDFWriter writer; 
	
	public ContainerObject(PDFWriter writer){
		this.id = writer.computeID();
		this.writer = writer;
	}
	
	public abstract void write() throws PDFException;

	public int getID(){
		return id;
	}
	
	public PDFWriter pdfWriter() {
		return writer;
	}
	
}
