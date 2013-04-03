package net.sf.gilead.test.domain.java5;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import net.sf.gilead.test.domain.interfaces.IMessage;

import org.hibernate.annotations.CollectionOfElements;


/**
 * Message domain class for Java5 Hibernate POJO
 * This class has no inheritance on hibernate4gwt, and will be converted as a DTO in HibernateRemoteService
 * @author bruno.marchesson
 *
 */
@Entity
@Table(name="message")
public class Message implements Serializable, IMessage
{
	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = -1067096371173906324L;
	
	//	Fields    
    private int id;
    private Integer version;
    private String message;
    private Date date;
    
    private User author;
    
    private Map<String, Integer> keywords;
    
    // Properties
	/* (non-Javadoc)
	 * @see net.sf.gilead.testApplication.domain.IMessage#getId()
	 */
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="ID")
	public final int getId() {
		return id;
	}
	/* (non-Javadoc)
	 * @see net.sf.gilead.testApplication.domain.IMessage#setId(java.lang.Integer)
	 */
	public final void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the version
	 */
	@Version
	@Column(name="VERSION")
	public Integer getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}
	/* (non-Javadoc)
	 * @see net.sf.gilead.testApplication.domain.IMessage#getMessage()
	 */
	@Column(name="MESSAGE", nullable=false, length=255)
	public String getMessage() {
		return message;
	}
	/* (non-Javadoc)
	 * @see net.sf.gilead.testApplication.domain.IMessage#setMessage(java.lang.String)
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/* (non-Javadoc)
	 * @see net.sf.gilead.testApplication.domain.IMessage#getDate()
	 */
	@Column(name="DATE", nullable=false)
	public Date getDate() {
		return date;
	}
	/* (non-Javadoc)
	 * @see net.sf.gilead.testApplication.domain.IMessage#setDate(java.util.Date)
	 */
	public void setDate(Date timeStamp) {
		this.date = timeStamp;
	}
	/**
	 * @return the author
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="USER_ID")
	public User getAuthor() {
		return author;
	}
	
	/**
	 * @param author the author to set
	 */
	public void setAuthor(User author) {
		this.author = (User) author;
	}
	
	/**
	 * @see net.sf.gilead.test.domain.interfaces.IMessage#getKeywords()
	 */
	@CollectionOfElements(fetch = FetchType.LAZY)
	@org.hibernate.annotations.MapKey(columns={@Column(name="word", nullable=false)})
	@Column(name="value", nullable=false, columnDefinition="INTEGER")
	@org.hibernate.annotations.Cascade(value=org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@JoinTable(name="message_keywords")
	private Map<String, Integer> getKeywords()
	{
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
}
