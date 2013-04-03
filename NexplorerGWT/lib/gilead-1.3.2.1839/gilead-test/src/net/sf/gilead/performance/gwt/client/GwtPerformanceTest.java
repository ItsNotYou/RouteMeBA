/**
 * 
 */
package net.sf.gilead.performance.gwt.client;

import net.sf.gilead.test.domain.interfaces.IUser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * Performance test for GWT
 * @author bruno.marchesson
 *
 */
public class GwtPerformanceTest extends GWTTestCase
{
	//-------------------------------------------------------------------------
	//
	// Test init
	//
	//-------------------------------------------------------------------------
	/**
	 * Get module name
	 */
	public String getModuleName()
	{
		return "net.sf.gilead.Test";
	}
	
	//-------------------------------------------------------------------------
	//
	// Test methods
	//
	//-------------------------------------------------------------------------
	/**
	 * Test clone user and messages in stateful mode
	 */
	public void testStatefulCloneUserAndMessage()
	{
		testLoadUserAndMessages("/StatefulPerformanceService");
	}
	
	/**
	 * Test clone user and messages in stateless mode
	 */
	public void testStatelessCloneUserAndMessage()
	{
		testLoadUserAndMessages("/StatelessPerformanceService");
	}
	
	//-------------------------------------------------------------------------
	//
	// Internal test method
	//
	//-------------------------------------------------------------------------
	/**
	 * Load User and messages
	 */
	protected void testLoadUserAndMessages(final String remoteServiceName)
	{
		// Setup an asynchronous event handler.
		Timer timer = new Timer()
		{
			public void run()
			{
				// Call performance service
				PerformanceServiceAsync remoteService = (PerformanceServiceAsync) GWT.create(PerformanceService.class);
				((ServiceDefTarget) remoteService).setServiceEntryPoint( GWT.getModuleBaseURL() + remoteServiceName);
				
				final long start = System.currentTimeMillis();
				remoteService.loadUserAndMessages(new AsyncCallback<IUser>()
				{
					public void onFailure(Throwable caught)
					{
						assertFalse(caught.toString(), false);

						// tell the test system the test is now done
						finishTest();
					}

					public void onSuccess(IUser result)
					{
						long end = System.currentTimeMillis();
						assertNotNull(result);
						
						System.out.println(remoteServiceName + " : received user [" + 
										   result.getClass().getName()+ "] after " + 
										   (end - start) + " ms.");
						
						// tell the test system the test is now done
						finishTest();
					}
			
				});

			}
		};

		// Set a delay period significantly longer than the
		// event is expected to take.
		delayTestFinish(500000);

		// Schedule the event and return control to the test system.
		timer.schedule(100);
	}
	
	/**
	 * Dry run, since first call seems longer (twice than others)
	 */
	protected void dryRun(final String remoteServiceName)
	{
		// Setup an asynchronous event handler.
		Timer timer = new Timer()
		{
			public void run()
			{
				// Call performance service
				PerformanceServiceAsync remoteService = (PerformanceServiceAsync) GWT.create(PerformanceService.class);
				((ServiceDefTarget) remoteService).setServiceEntryPoint( GWT.getModuleBaseURL() + remoteServiceName);
				
				remoteService.loadUserAndMessages(new AsyncCallback<IUser>()
				{
					public void onFailure(Throwable caught)
					{
						assertFalse(caught.toString(), false);

						// tell the test system the test is now done
						finishTest();
					}

					public void onSuccess(IUser result)
					{
						assertNotNull(result);
						
						// tell the test system the test is now done
						finishTest();
					}
			
				});

			}
		};

		// Set a delay period significantly longer than the
		// event is expected to take.
		delayTestFinish(5000);

		// Schedule the event and return control to the test system.
		timer.schedule(100);
	}
}
