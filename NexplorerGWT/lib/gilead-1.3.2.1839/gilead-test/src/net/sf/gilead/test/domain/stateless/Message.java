package net.sf.gilead.test.domain.stateless;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.gilead.pojo.java5.LightEntity;
import net.sf.gilead.test.domain.interfaces.IMessage;
import net.sf.gilead.test.domain.interfaces.IUser;

/**
 * Message domain class for stateless pojo store
 * This class just has to inherit from LightEntity;
 * The 'Keywords' property has private getter and setter
 * 
 * @author bruno.marchesson
 *
 */
public class Message extends LightEntity implements IMessage
{
	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = 3445339493203407152L;
	
	//	Fields    
    private int id;
    private Integer version;
    private String message;
    private Date date;
    
    private IUser author;
    
    private Map<String, Integer> keywords;
    
    // Properties
	/**
	 * @return the id
	 */
	public final int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public final void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the version
	 */
	public Integer getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the timeStamp
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * @param timeStamp the timeStamp to set
	 */
	public void setDate(Date timeStamp) {
		this.date = timeStamp;
	}
	/**
	 * @return the author
	 */
	public IUser getAuthor() {
		return author;
	}
	/**
	 * @param author the author to set
	 */
	public void setAuthor(IUser author) {
		this.author = (IUser) author;
	}
	
	/**
	 * @see net.sf.gilead.test.domain.interfaces.IMessage#getKeywords()
	 */
	private Map<String, Integer> getKeywords() {
		return keywords;
	}
	
	/**
	 * @see net.sf.gilead.test.domain.interfaces.IMessage#setKeywords(java.util.Map)
	 */
	private void setKeywords(Map<String, Integer> keywords)
	{
		this.keywords = keywords;
	}
	
	/**
	 * Add a keyword in the associated map
	 * @param key
	 * @param value
	 */
	public void addKeyword(String key, Integer value)
	{
		if (keywords == null)
		{
			keywords = new HashMap<String, Integer>();
		}
		keywords.put(key, value);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.gilead.test.domain.IMessage#clearKeywords()
	 */
	public void clearKeywords()
	{
		if (keywords != null)
		{
			keywords.clear();
		}	
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.gilead.test.domain.IMessage#countKeywords()
	 */
	public int countKeywords()
	{
		if (keywords != null)
		{
			return keywords.size();
		}
		else
		{
			return 0;
		}
	}
	
	/**
	 * Equality function
	 */
	public boolean equals(Object obj)
	{
		if ((obj == null) ||
			(obj instanceof Message == false))
		{
			return false;
		}
		else if (this == obj)
		{
			return true;
		}
		
		// ID comparison
		IMessage other = (IMessage) obj;
		return (id == other.getId());
	}
}