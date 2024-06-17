package org.nterlearning.usermgmt.persistence;

/**
 * @author mfrazier
 *
 */
public class PasswordHistoryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Thrown when the password history rule has been violated
	 * @param s
	 */
	public PasswordHistoryException(String s)
	{
		super(s);
	}
	

}
