package sugarcube.formats.epub.structure.meta;

import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Map3;

//maurizio ext for meta
public class Meta {
	private Map3<String, String> attributes = new Map3<>();
	private String cdata;
	
	public void setData(String cdata){
		this.cdata = cdata;
	}

	public String getData(){
		return this.cdata;
	}
	
	public void add(String attributeName, String attributeValue){
		attributes.put(attributeName, attributeValue);
	}
	
	public List3<String> attributeNames(){
		return attributes.keyList();
	}

	public String value(String attributeName){
		return attributes.get(attributeName);
	}
	
	public String[] asStringArray(){
		String[] values = new String[attributes.size() * 2 + 1];
		List3<String> names = attributeNames(); 
		for (int n = 0, i = 0; n < names.size(); n++, i += 2){
			values[i] = names.get(n);
			values[i + 1] = value(values[i]);
		}
		values[values.length - 1] = cdata;
		return values;
	}
}
