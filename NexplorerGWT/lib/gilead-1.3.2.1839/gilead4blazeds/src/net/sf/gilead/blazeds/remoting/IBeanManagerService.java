/**
 * 
 */
package net.sf.gilead.blazeds.remoting;

import net.sf.gilead.core.PersistentBeanManager;

/**
 * Interface of BlazeDS remote service that needs a bean manager to work.
 * @author bruno.marchesson
 *
 */
public interface IBeanManagerService {
	
	/**
	 * @return the associated bean manager
	 */
	public abstract PersistentBeanManager getBeanManager();
	
	/**
	 * @param beanManager the manager to set.
	 */
	public abstract void setBeanManager(PersistentBeanManager beanManager);

}
