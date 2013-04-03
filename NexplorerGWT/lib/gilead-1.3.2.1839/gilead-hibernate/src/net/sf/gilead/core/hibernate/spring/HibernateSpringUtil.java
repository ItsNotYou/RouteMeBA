/**
 * 
 */
package net.sf.gilead.core.hibernate.spring;

import javax.persistence.EntityManagerFactory;

import net.sf.gilead.core.hibernate.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxy;

/**
 * Spring handling of injected session factories
 * @author bruno.marchesson
 *	
 */
public class HibernateSpringUtil extends HibernateUtil
{
	//----
	// Attributes
	//----
	/**
	 * Log channel
	 */
	private static Logger _log = LoggerFactory.getLogger(HibernateSpringUtil.class);
	
	/**
	 * Spring wrapper for session factory (needed to get current session)
	 */
	private SessionFactory _springSessionFactory;
	
	//----
	// Setters
	//----
	/**
	 * Set entity manager factory from JBoss
	 * @param entityManagerFactory
	 */
	public void setEntityManagerFactory(Object entityManagerFactory)
	{
	//	Manage Injected EntityManagerFactory
	//
		if (entityManagerFactory instanceof AopProxy)
		{
		//	Need to call 'getProxy' method
		//
			entityManagerFactory = ((AopProxy) entityManagerFactory).getProxy();
		
		}
		
	//	Just get session factory for Hibernate emf implementation
	//
		if (entityManagerFactory instanceof HibernateEntityManagerFactory)
		{
		//	Base class
		//
			setSessionFactory(((HibernateEntityManagerFactory) entityManagerFactory).getSessionFactory());
		}
		else
		{
			throw new IllegalArgumentException("Cannot find Hibernate entity manager factory implementation for " + entityManagerFactory);
		}	 
	}
	
	/**
	 * Sets the session factory
	 */
	public void setSessionFactory(SessionFactory sessionFactory)
	{
		_springSessionFactory = sessionFactory;
	//	Manage injected SessionFactory
	//
		if (sessionFactory instanceof AopProxy)
		{
		//	Need to call 'getProxy' method
		//
			sessionFactory = (SessionFactory) ((AopProxy) sessionFactory).getProxy();
		}
		
		// Call base class
		super.setSessionFactory(sessionFactory);
	}
	
	//-------------------------------------------------------------------------
	//
	// Constructors
	//
	//-------------------------------------------------------------------------
	/**
	 * Empty constructor.
	 */
	public HibernateSpringUtil()
	{
		super();
	}
	
	/**
	 * Session factory constructor.
	 */
	public HibernateSpringUtil(SessionFactory sessionFactory)
	{
		super();
		setSessionFactory(sessionFactory);
	}
	
	/**
	 * Entity Manager factory constructor.
	 */
	public HibernateSpringUtil(EntityManagerFactory entityManagerFactory)
	{
		super();
		setEntityManagerFactory(entityManagerFactory);
	}
	
	//-------------------------------------------------------------------------
	//
	// Overridden method
	//
	//-------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * @see net.sf.gilead.core.hibernate.HibernateUtil#getCurrentSession()
	 */
	protected Session getCurrentSession()
	{
		if (_springSessionFactory != null)
		{
			try
			{
				Session session = _springSessionFactory.getCurrentSession();
				if ((session != null) &&
					(session.isConnected()))
				{
					// return only active session
					return session;
				}
				else
				{
					return null;
				}
			}
			catch(Exception ex)
			{
				_log.debug("Exception during getCurrentSession", ex);
				return null;
			}
		}
		else
		{
			_log.warn("No Spring session factory found !");
			return super.getCurrentSession();
		}
	}
}
