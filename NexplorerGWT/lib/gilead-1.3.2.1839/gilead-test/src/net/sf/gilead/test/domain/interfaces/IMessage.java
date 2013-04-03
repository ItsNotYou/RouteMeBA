package net.sf.gilead.test.domain.interfaces;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Interface of the Java 1.4 domain message classes (stateful and stateless)
 * @author bruno.marchesson
 *
 */
public interface IMessage {

	// Properties (native int instead of Integer for test purpose)
	/**
	 * @return the id
	 */
	public int getId();

	/**
	 * @param id the id to set
	 */
	public void setId(int id);

	/**
	 * @return the version
	 */
	public Integer getVersion();

	/**
	 * @param version the version to set
	 */
	public void setVersion(Integer version);

	/**
	 * @return the message
	 */
	public String getMessage();

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message);

	/**
	 * @return the timeStamp
	 */
	public Date getDate();

	/**
	 * @param timeStamp the timeStamp to set
	 */
	public void setDate(Date timeStamp);
	
	/**
	 * @return the author
	 */
	public IUser getAuthor();
	
	/**
	 * @param author the author to set
	 */
	// public void setAuthor(IUser author);

	/**
	 * Add a keyword in the associated map
	 * @param key
	 * @param value
	 */
	public void addKeyword(String key, Integer value);
	
	/**
	 * Clear the keywords
	 */
	public void clearKeywords();
	
	/**
	 * Count the keywords
	 * @return
	 */
	public int countKeywords();
}