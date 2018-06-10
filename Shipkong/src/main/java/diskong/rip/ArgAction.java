package diskong.rip;

public enum ArgAction {
	CDDB("-a cddb"), CLEAN("-a clean"), EMBEDIMAGE("-B"), DEFAULT("-a default,getalbumart"), GETIMAGE("getalbumart");

	 private final String name;       
	 
	 ArgAction(String s) {
	        name = s;
	    }
	 
	public String getString() {
	
		return name;
	}

} 
