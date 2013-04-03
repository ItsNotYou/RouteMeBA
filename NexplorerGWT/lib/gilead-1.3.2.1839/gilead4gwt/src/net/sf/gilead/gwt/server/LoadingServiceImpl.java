package net.sf.gilead.gwt.server;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import net.sf.gilead.core.PersistentBeanManager;
import net.sf.gilead.gwt.PersistentRemoteService;
import net.sf.gilead.gwt.client.LoadingService;
import net.sf.gilead.pojo.base.ILightEntity;
import net.sf.gilead.pojo.gwt.IGwtSerializableParameter;
import net.sf.gilead.services.BaseLoadingService;

/**
 * GWT remote loading service implementation.
 * It does not extends PersistentRemoteService since merge operation is not needed.
 * @author bruno.marchesson
 *
 */
public class LoadingServiceImpl<T extends ILightEntity> extends PersistentRemoteService
								implements LoadingService<T>
{
	//----
	// Attributes
	//----
	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 5714428833885668669L;
	
	/**
	 * The base loading service.
	 * The whole business logic is here !
	 */
	private BaseLoadingService<T> baseService;
	
	//----
	// Properties
	//---
	/**
	 * @return the beanManager
	 */
	public PersistentBeanManager getBeanManager()
	{
		return baseService.getBeanManager();
	}

	/**
	 * @param beanManager the beanManager to set
	 */
	public void setBeanManager(PersistentBeanManager beanManager)
	{
		baseService.setBeanManager(beanManager);
	}
	
	//-----------------------------------------------------------------------
	//
	// Constructors
	//
	//-----------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public LoadingServiceImpl()
	{
		baseService = new BaseLoadingService<T>();
	}

	//-------------------------------------------------------------------------
	//
	// Association loading implementation
	//
	//-------------------------------------------------------------------------
	/**
	 * Load an association from the parent entity
	 * @param parent the entity
	 * @param property the name of the property to load
	 * @return the loaded entity
	 */
	public <K extends ILightEntity> K loadEntityAssociation(T parent, String propertyName)
	{
		return (K) baseService.loadEntityAssociation(parent, propertyName);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.gilead.gwt.client.LoadingService#loadAssociationList(net.sf.gilead.pojo.base.ILightEntity, java.lang.String)
	 */
	public <K extends ILightEntity> List<K> loadListAssociation(T parent,
																String propertyName)
	{
		return baseService.loadListAssociation(parent, propertyName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.gilead.gwt.client.LoadingService#loadSetAssociation(net.sf.gilead.pojo.base.ILightEntity, java.lang.String)
	 */
	public <K extends ILightEntity> Set<K> loadSetAssociation(T parent,
															  String propertyName)
	{
		return baseService.loadSetAssociation(parent, propertyName);
	}
	
	//-------------------------------------------------------------------------
	//
	// Entity loading methods
	//
	//-------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * @see net.sf.gilead.gwt.client.LoadingService#loadEntity(java.lang.Integer)
	 */
	public T loadEntity(String className, IGwtSerializableParameter id)
	{
		return baseService.loadEntity(className, (Serializable)id.getUnderlyingValue());
	}
}
