package diskong;

import java.util.ArrayList;
import java.util.List;

import org.apache.tika.metadata.Property;

public class Arguments {

	StringBuffer buf = new StringBuffer();
	List<String> liste = new ArrayList<>();
	
	public void add(ArgAction actionTag, Property prop) {
		// TODO Auto-generated method stub
		StringBuffer buf = new StringBuffer();
		buf.append(actionTag.getString()).append(prop.getName().contains(":")?prop.getName().split(":")[1]:prop.getName());//.append(" ");
		liste.add(buf.toString());
		
	}
	public void add(ArgAction actionTag, Property prop, String value) {
		StringBuffer buf = new StringBuffer();
		
		buf.append(actionTag.getString()).append(prop.getName().contains(":")?prop.getName().split(":")[1]:prop.getName()).append("=").append(value);
		liste.add(buf.toString());
		
	}
	public String flatten() {
		// TODO Auto-generated method stub
		return buf.toString();
	}
	
	public List<String> getList() {
		// TODO Auto-generated method stub
		return liste;
	}
	
}
