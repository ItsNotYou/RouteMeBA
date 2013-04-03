/**
 * 
 */
package net.sf.gilead.gwt.client;

import java.util.List;
import java.util.Set;

import net.sf.gilead.pojo.base.ILightEntity;
import net.sf.gilead.pojo.gwt.IGwtSerializableParameter;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Loading remote service implementation
 * @author bruno.marchesson
 * @generated generated asynchronous callback interface to be used on the client side
 *
 */

public interface LoadingServiceAsync<T extends ILightEntity>
{
	//-------------------------------------------------------------------------
	//
	// Entity loading
	//
	//-------------------------------------------------------------------------
	/**
	 * Load an entity from its id
	 */
	public void loadEntity(String className, IGwtSerializableParameter id, AsyncCallback<T> callback);
	
	//-------------------------------------------------------------------------
	//
	// Association loading
	//
	//-------------------------------------------------------------------------
	/**
	 * Load an association from the parent entity
	 * @param <K>
	 * @param parent the entity
	 * @param property the name of the property to load
	 * @return the loaded entity
	 */
	public <K extends ILightEntity> void loadEntityAssociation(T parent, String propertyName, AsyncCallback<K> callback);

	/**
	 * Load an association list from the parent entity
	 * @param <K>
	 * @param parent the entity
	 * @param property the name of the property to load
	 * @return the loaded entity
	 */
	public <K extends ILightEntity> void loadListAssociation(ILightEntity parent, String propertyName,
															 AsyncCallback<List<K>> callback);
	
	/**
	 * Load an association set from the parent entity
	 * @param <K>
	 * @param parent the entity
	 * @param property the name of the property to load
	 * @return the loaded entity
	 */
	public <K extends ILightEntity> void loadSetAssociation(ILightEntity parent, String propertyName,
															AsyncCallback<Set<K>> callback);

}
