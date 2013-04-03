/**
 * 
 */
package net.sf.gilead.gwt.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.gilead.core.PersistentBeanManager;
import net.sf.gilead.core.serialization.GwtSerializer;
import net.sf.gilead.gwt.PersistentRemoteService;
import net.sf.gilead.gwt.client.RequestService;
import net.sf.gilead.pojo.gwt.IGwtSerializableParameter;
import net.sf.gilead.services.BaseRequestService;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Request service implementation.
 * @author bruno.marchesson
 *
 */
public class RequestServiceImpl<T extends IGwtSerializableParameter> extends PersistentRemoteService
	implements RequestService<T>
{
	//----
	// Attributes
	//----
	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 814725549964949202L;

	/**
	 * The associated base service.
	 * The whole business logic is here !
	 */
	private BaseRequestService baseService;
	
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
	
	//-------------------------------------------------------------------------
	//
	// Constructor
	//
	//-------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public RequestServiceImpl()
	{
		baseService = new BaseRequestService();
	}
	
	//-------------------------------------------------------------------------
	//
	// Request service implementation
	//
	//-------------------------------------------------------------------------
	/**
	 * @see net.sf.gilead.gwt.client.RequestService#executeRequest(java.lang.String, java.util.List)
	 */
	@SuppressWarnings("unchecked")
	public List<T> executeRequest(String query,
								  				  List<IGwtSerializableParameter> parameters)
								  				  throws SerializationException
	{	
	//	Convert parameters if needed
	//
		List<Object> queryParameters = null;
		if ((parameters != null) &&
			(parameters.isEmpty() == false))
		{
			queryParameters = new ArrayList<Object>(parameters.size());
			for (IGwtSerializableParameter parameter : parameters)
			{
				queryParameters.add(parameter.getUnderlyingValue());
			}
		}
		
	//	Execute query
	// 	Note : double case is mandatory due to Java 6 compiler issue 6548436
	//
		List<Serializable> result = baseService.executeRequest(query, queryParameters);
		if (result == null)
		{
			return null;
		}
		
		// convert result
		List<T> serializableResult = new ArrayList<T>(result.size());
		GwtSerializer serializer = new GwtSerializer();
		for (Serializable serializable : result)
		{
			serializableResult.add((T)serializer.convertToGwt(serializable));
		}
		return serializableResult; 
	}

	/**
	 * @see net.sf.gilead.gwt.client.RequestService#executeRequest(java.lang.String, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public List<T> executeRequest(String query,
				  				  Map<String, IGwtSerializableParameter> parameters)
				  				  throws SerializationException
	{
	//	Convert parameters if needed
	//
		Map<String,Object> queryParameters = null;
		if ((parameters != null) &&
			(parameters.isEmpty() == false))
		{
			queryParameters = new HashMap<String, Object>(parameters.size());
			for (Map.Entry<String, IGwtSerializableParameter> parameter : parameters.entrySet())
			{
				queryParameters.put(parameter.getKey(), parameter.getValue().getUnderlyingValue());
			}
		}
		
	//	Execute query
	//	Note : double case is mandatory due to Java 6 compiler issue 6548436
	//
		List<Serializable> result = (List<Serializable>)(Object) baseService.executeRequest(query, queryParameters);
		if (result == null)
		{
			return null;
		}
		
		// convert result
		List<T> serializableResult = new ArrayList<T>(result.size());
		GwtSerializer serializer = new GwtSerializer();
		for (Serializable serializable : result)
		{
			serializableResult.add((T)serializer.convertToGwt(serializable));
		}
		return serializableResult; 
	}
}
