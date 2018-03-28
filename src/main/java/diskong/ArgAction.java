package diskong;

public enum ArgAction {
	REMOVE_TAG("--remove-tag="), SET_TAG("--set-tag="); 

	 private final String name;       
	 
	 ArgAction(String s) {
	        name = s;
	    }
	 
	public String getString() {
	
		return name;
	}

} 
