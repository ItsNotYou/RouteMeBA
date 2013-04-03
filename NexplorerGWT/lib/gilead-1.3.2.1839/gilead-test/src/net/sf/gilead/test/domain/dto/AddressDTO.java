package net.sf.gilead.test.domain.dto;

import net.sf.gilead.pojo.java5.LightEntity;
import net.sf.gilead.test.domain.interfaces.IAddress;
import net.sf.gilead.test.domain.interfaces.ICountry;

/**
 * Embedded test class for address (depends on User)
 * @author bruno.marchesson
 *
 */
public class AddressDTO extends LightEntity implements IAddress
{
	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -6914495957511158133L;
	
	// Attributes
	private String street;
	private String city;
	private ICountry country;
	
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
	public ICountry getCountry() {
		return country;
	}
	/* (non-Javadoc)
	 * @see net.sf.gilead.test.domain.stateless.IAddress#setCountry(net.sf.gilead.test.domain.ICountry)
	 */
	public void setCountry(ICountry country) {
		this.country = country;
	}
}
