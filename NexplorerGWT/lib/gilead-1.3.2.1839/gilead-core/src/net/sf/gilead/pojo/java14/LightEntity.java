/*
 * Copyright 2007 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sf.gilead.pojo.java14;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.sf.gilead.pojo.base.ILightEntity;

/**
 * Abstract POJO for lazy property handling.
 * This class is Java 1.4 and holds GWT xdoclets
 * @author bruno.marchesson
 *
 */
public abstract class LightEntity implements ILightEntity, Serializable
{
	//-----
	// Attributes
	//-----
	/**
	 * erialization ID
	 */
	private static final long serialVersionUID = 535611044803301746L;

	/**
	 * Map of proxy informations
	 * The key is the property name, the value is a map with
	 * persistence informations filled by the persistence util
	 * implementation.
	 * @gwt.typeArgs <java.lang.String, java.lang.String>
	 */
	protected Map _proxyInformations;
	
	/**
	 * Map of persistence initialisation state.
	 * The key is the property name, the value is a map with
	 * persistence informations filled by the persistence util
	 * implementation
	 * @gwt.typeArgs <java.lang.String, java.lang.Boolean>
	 */
	protected Map _initializationMap;
	
	//-------------------------------------------------------------------------
	//
	// Constructor
	//
	//-------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public LightEntity()
	{
		super();
	}

	//-------------------------------------------------------------------------
	//
	// Public interface
	//
	//-------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * @see net.sf.gilead.pojo.base.ILightEntity#addProxyInformations(java.lang.String, java.lang.Class)
	 */
	public void addProxyInformation(String property,
									Object proxyInformations)
	{
		if (_proxyInformations == null)
		{
			_proxyInformations = new HashMap();
		}
		_proxyInformations.put(property, proxyInformations);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.gilead.pojo.base.ILightEntity#removeProxyInformations(java.lang.String)
	 */
	public void removeProxyInformation(String property)
	{
		if (_proxyInformations != null)
		{
			_proxyInformations.remove(property);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.gilead.pojo.base.ILightEntity#getProxyInformations(java.lang.String)
	 */
	public Object getProxyInformation(String property)
	{
		if (_proxyInformations != null)
		{
			return _proxyInformations.get(property);
		}
		else
		{
			return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.gilead.pojo.base.ILightEntity#getDebugString()
	 */
	public String getDebugString()
	{
		if (_proxyInformations != null)
		{
			return _proxyInformations.toString();
		}
		else
		{
			return null;
		}
	}
	/*
	 * (non-Javadoc)
	 * @see net.sf.gilead.pojo.base.ILightEntity#isInitialized(java.lang.String)
	 */
	public boolean isInitialized(String property)
	{
		if (_initializationMap == null)
		{
			return true;
		}
		
		Boolean initialized = (Boolean) _initializationMap.get(property);
		if (initialized == null)
		{
			return true;
		}
		return initialized.booleanValue();
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.gilead.pojo.base.ILightEntity#setInitialized(java.lang.String, boolean)
	 */
	public void setInitialized(String property, boolean initialized)
	{
		if (_initializationMap == null)
		{
			_initializationMap = new HashMap();
		}
		_initializationMap.put(property, new Boolean(initialized));	
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.gilead.pojo.gwt.IRequestParameter#getValue()
	 */
	public Object getValue()
	{
		return this;
	}
}
