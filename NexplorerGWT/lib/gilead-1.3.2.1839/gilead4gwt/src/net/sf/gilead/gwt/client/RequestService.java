package net.sf.gilead.gwt.client;

import java.util.List;
import java.util.Map;

import net.sf.gilead.pojo.gwt.IGwtSerializableParameter;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Remote request service declaration
 * @author bruno.marchesson
 * 
 *
 */
@RemoteServiceRelativePath("RequestService")
public interface RequestService<T extends IGwtSerializableParameter> extends RemoteService
{
	//-------------------------------------------------------------------------
	//
	// Public interface
	//
	//-------------------------------------------------------------------------
	/**
	 * Execute a request on server.
	 */
	public List<T> executeRequest(String query, List<IGwtSerializableParameter> parameters) throws SerializationException;
	
	/**
	 * Execute a request on server.
	 */
	public List<T> executeRequest(String query, Map<String, IGwtSerializableParameter> parameters) throws SerializationException;
}
