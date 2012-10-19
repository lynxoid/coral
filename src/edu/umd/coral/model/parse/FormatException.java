package edu.umd.coral.model.parse;

public class FormatException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8492125473267161166L;

	public FormatException(String clustName, String modName, String moduleLine) {
		super("Bad file format in " + clustName + ": " + moduleLine);
	}
}
