package net.sf.gilead.gwt.client;

import java.util.List;
import java.util.Set;

import net.sf.gilead.pojo.base.ILightEntity;
import net.sf.gilead.pojo.gwt.IGwtSerializableParameter;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Remote loading service declaration
 * @author bruno.marchesson
 * 
 *
 */
@RemoteServiceRelativePath("LoadingService")
public interface LoadingService<T extends ILightEntity> extends RemoteService
{
	//-------------------------------------------------------------------------
	//
	// Entity loading
	//
	//-------------------------------------------------------------------------
	/**
	 * Load an entity from its id
	 */
	public T loadEntity(String className, IGwtSerializableParameter id);

	//-------------------------------------------------------------------------
	//
	// Association loading
	//
	//-------------------------------------------------------------------------
	/**
	 * Load an association entity from the parent entity
	 * @param parent the entity
	 * @param property the name of the property to load
	 * @return the loaded entity
	 */
	public <K extends ILightEntity> K loadEntityAssociation(T parent, String propertyName);
	
	/**
	 * Load an association list from the parent entity
	 * @param parent the entity
	 * @param property the name of the property to load
	 * @return the loaded entity
	 */
	public <K extends ILightEntity> List<K> loadListAssociation(T parent, String propertyName);
	
	/**
	 * Load an association set from the parent entity
	 * @param parent the entity
	 * @param property the name of the property to load
	 * @return the loaded entity
	 */
	public <K extends ILightEntity> Set<K> loadSetAssociation(T parent, String propertyName);
}
