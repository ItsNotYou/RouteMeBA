/**
 * 
 */
package net.sf.gilead.core.loading.gwt.client;

import net.sf.gilead.gwt.client.ProxyInformationService;
import net.sf.gilead.gwt.client.ProxyInformationServiceAsync;
import net.sf.gilead.pojo.gwt.basic.IntegerParameter;
import net.sf.gilead.test.domain.stateful.Message;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Remote loading test for stateful GWT
 * @author bruno.marchesson
 *
 */
public class GwtStatefulTest extends GWTTestCase
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
	 * Test lazy association checking
	 */
	public void testLazyAssociationChecking()
	{
	//	1. Load test message
	//
		// Setup an asynchronous event handler.
		Timer timer = new Timer()
		{
			public void run()
			{
				// Call remote init service
				StatefulInitServiceAsync remoteService = (StatefulInitServiceAsync) GWT.create(StatefulInitService.class);
				remoteService.loadTestMessage(new AsyncCallback<Message>()
				{
					public void onFailure(Throwable caught)
					{
						fail(caught.toString());
						finishTest();
					}

					public void onSuccess(Message result)
					{
						testCheckAssociation(result);
					}
			
				});
			}
		};

		// Set a delay period significantly longer than the
		// event is expected to take.
		delayTestFinish(60000);

		// Schedule the event and return control to the test system.
		timer.schedule(100);
	}
	
	
	//-------------------------------------------------------------------------
	//
	// Internal methods
	//
	//-------------------------------------------------------------------------
	/**
	 * Test load simple association
	 */
	protected void testCheckAssociation(final Message message)
	{
		// Setup an asynchronous event handler.
		Timer timer = new Timer()
		{
			public void run()
			{
				// Call remote loading service
				ProxyInformationServiceAsync remoteService = (ProxyInformationServiceAsync) GWT.create(ProxyInformationService.class);
				remoteService.isInitialized(message.getClass().getName(), 
											new IntegerParameter(message.getId()), 
											"author", 
											new AsyncCallback<Boolean>()
				{
					public void onFailure(Throwable caught)
					{
						fail(caught.toString());

						// tell the test system the test is now done
						finishTest();
					}

					public void onSuccess(Boolean result)
					{
						assertNotNull(result);
						assertFalse(result.booleanValue());
						
						// tell the test system the test is now done
						finishTest();
					}
			
				});

			}
		};

		// Schedule the event and return control to the test system.
		timer.schedule(100);
	}
}
