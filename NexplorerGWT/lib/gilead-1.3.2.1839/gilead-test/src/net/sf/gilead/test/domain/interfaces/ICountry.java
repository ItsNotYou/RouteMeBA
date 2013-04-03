package net.sf.gilead.test.domain.interfaces;

public interface ICountry {

	// Properties
	/**
	 * @return the id
	 */
	public abstract Integer getId();

	/**
	 * @param id the id to set
	 */
	public abstract void setId(Integer id);

	/**
	 * @return the version
	 */
	public abstract Integer getVersion();

	/**
	 * @param version the version to set
	 */
	public abstract void setVersion(Integer version);

	/**
	 * @return the name
	 */
	public abstract String getName();

	/**
	 * @param name the name to set
	 */
	public abstract void setName(String name);

}