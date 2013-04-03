package net.sf.gilead.core.hibernate.jboss;

import java.lang.reflect.Method;

import javax.persistence.EntityManagerFactory;

import net.sf.gilead.core.hibernate.HibernateUtil;

import org.hibernate.SessionFactory;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.jboss.ejb3.entity.InjectedSessionFactory;
import org.jboss.jpa.injection.InjectedEntityManagerFactory;

public class HibernateJBossUtil extends HibernateUtil
{
	/**
	 * Set entity manager factory from JBoss
	 * @param entityManagerFactory
	 */
	public void setEntityManagerFactory(Object entityManagerFactory)
	{
	//	Manage InjectedEntityManagerFactory
	//
		if (entityManagerFactory instanceof InjectedEntityManagerFactory)
		{
		//	Need to call 'getDelegate' method
		//
			entityManagerFactory = ((InjectedEntityManagerFactory) entityManagerFactory).getDelegate();
		
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
	
	@Override
	public void setSessionFactory(SessionFactory sessionFactory)
	{
	//	Manage injected SessionFactory
	//
		if (sessionFactory instanceof InjectedSessionFactory)
		{
		//	The getDelegate method is protected :-(
		//
			try
			{
				Method getDelegate = InjectedSessionFactory.class.getDeclaredMethod("getDelegate", (Class<?>[])null);
				getDelegate.setAccessible(true);
				
				sessionFactory = (SessionFactory) getDelegate.invoke(sessionFactory, (Object[]) null);
			}
			catch(Exception ex)
			{
				throw new RuntimeException(ex);
			}
			
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
	public HibernateJBossUtil() 
	{
		super();
	}
	
	/**
	 * Session factory constructor
	 */
	public HibernateJBossUtil(SessionFactory sessionFactory)
	{
		super();
		setSessionFactory(sessionFactory);
	}
	
	/**
	 * Entity manager constructor
	 */
	public HibernateJBossUtil(EntityManagerFactory entityManagerFactory)
	{
		super();
		setEntityManagerFactory(entityManagerFactory);
	}
}
