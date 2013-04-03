package net.sf.gilead.test.domain.annotated;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

import net.sf.gilead.pojo.java5.LightEntity;
import net.sf.gilead.test.domain.interfaces.ICountry;

/**
 * Country domain class
 * @author bruno.marchesson
 */
@Entity
public class Country extends LightEntity implements Serializable, ICountry
{
	/**
	 * Serialization ID 
	 */
	private static final long serialVersionUID = -7655536246743775766L;

	// Fields
	@Id
	private Integer id;
	
	@Version
	private Integer version;
	
	@Column
	private String name;
	
	// Properties
	/* (non-Javadoc)
	 * @see net.sf.gilead.test.domain.stateless.ICountry#getId()
	 */
	public Integer getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see net.sf.gilead.test.domain.stateless.ICountry#setId(java.lang.Integer)
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see net.sf.gilead.test.domain.stateless.ICountry#getVersion()
	 */
	public Integer getVersion() {
		return version;
	}

	/* (non-Javadoc)
	 * @see net.sf.gilead.test.domain.stateless.ICountry#setVersion(java.lang.Integer)
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}

	/* (non-Javadoc)
	 * @see net.sf.gilead.test.domain.stateless.ICountry#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see net.sf.gilead.test.domain.stateless.ICountry#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}	
}
