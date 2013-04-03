package net.sf.gilead.gwt.client;

import net.sf.gilead.pojo.gwt.IGwtSerializableParameter;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Remote service for stateful proxy information handling.
 * Useful to know if a property was null or lazy loaded from client side, or delete
 * a lazy loaded, nullified, property.
 * @author bruno.marchesson
 * 
 *
 */
@RemoteServiceRelativePath("ProxyInformationService")
public interface ProxyInformationService extends RemoteService
{
	//-------------------------------------------------------------------------
	//
	// Public interface
	//
	//-------------------------------------------------------------------------
	/**
	 * Indicates if a property was null or lazy loaded.
	 */
	public boolean isInitialized(String className, IGwtSerializableParameter id, String propertyName);
	
	/**
	 * Update proxy information when a field (lazy loaded) is initialized from client side.
	 */
	public void setInitialized(String className, IGwtSerializableParameter id, String propertyName);
}
