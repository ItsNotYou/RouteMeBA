package net.sf.gilead.test.domain.java5;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import net.sf.gilead.pojo.java5.LightEntity;
import net.sf.gilead.test.domain.interfaces.IAddress;
import net.sf.gilead.test.domain.interfaces.ICountry;

/**
 * Embedded test class for address (depends on User)
 * @author bruno.marchesson
 *
 */
@Embeddable
public class Address extends LightEntity implements Serializable, IAddress
{
	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -6914495957511158133L;
	
	// Attributes
	@Column(nullable=false)
	private String street;
	
	@Column(nullable=false)
	private String city;
	
	@ManyToOne(cascade= { CascadeType.PERSIST }, fetch=FetchType.LAZY)
	@JoinColumn(name="COUNTRY_ID")
	private Country country;
	
	// Properties
	/* (non-Javadoc)
	 * @see net.sf.gilead.test.domain.stateless.IAddress#getStreet()
	 */
	public String getStreet() {
		return street;
	}
	/* (non-Javadoc)
	 * @see net.sf.gilead.test.domain.stateless.IAddress#setStreet(java.lang.String)
	 */
	public void setStreet(String street) {
		this.street = street;
	}
	/* (non-Javadoc)
	 * @see net.sf.gilead.test.domain.stateless.IAddress#getCity()
	 */
	public String getCity() {
		return city;
	}
	/* (non-Javadoc)
	 * @see net.sf.gilead.test.domain.stateless.IAddress#setCity(java.lang.String)
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/* (non-Javadoc)
	 * @see net.sf.gilead.test.domain.stateless.IAddress#getCountry()
	 */
	public Country getCountry() {
		return country;
	}
	/* (non-Javadoc)
	 * @see net.sf.gilead.test.domain.stateless.IAddress#setCountry(net.sf.gilead.test.domain.ICountry)
	 */
	public void setCountry(ICountry country) {
		this.country = (Country)country;
	}
}
