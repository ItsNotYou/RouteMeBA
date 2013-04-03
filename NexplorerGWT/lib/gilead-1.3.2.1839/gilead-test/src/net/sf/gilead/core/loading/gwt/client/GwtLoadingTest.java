/**
 * 
 */
package net.sf.gilead.core.loading.gwt.client;

import java.util.Set;

import net.sf.gilead.gwt.client.LoadingService;
import net.sf.gilead.gwt.client.LoadingServiceAsync;
import net.sf.gilead.pojo.gwt.basic.IntegerParameter;
import net.sf.gilead.test.domain.gwt.Message;
import net.sf.gilead.test.domain.gwt.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Remote lazy loading test for GWT
 * @author bruno.marchesson
 *
 */
public class GwtLoadingTest extends GWTTestCase
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
	 * Test loading of a simple association
	 */
	public void testLoadSimpleAssociation()
	{
	//	1. Load test message
	//
		// Setup an asynchronous event handler.
		Timer timer = new Timer()
		{
			public void run()
			{
				// Call remote init service
				StatelessInitServiceAsync remoteService = (StatelessInitServiceAsync) GWT.create(StatelessInitService.class);
				remoteService.loadTestMessage(new AsyncCallback<Message>()
				{
					public void onFailure(Throwable caught)
					{
						fail(caught.toString());
						finishTest();
					}

					public void onSuccess(Message result)
					{
						testLoadSimpleAssociation(result);
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
	
	/**
	 * Test loading of a list association
	 */
	public void testLoadSetAssociation()
	{
	//	1. Load test user
	//
		// Setup an asynchronous event handler.
		Timer timer = new Timer()
		{
			public void run()
			{
				// Call remote init service
				StatelessInitServiceAsync remoteService = (StatelessInitServiceAsync) GWT.create(StatelessInitService.class);
				remoteService.loadTestUser(new AsyncCallback<User>()
				{
					public void onFailure(Throwable caught)
					{
						fail(caught.toString());
						finishTest();
					}

					public void onSuccess(User result)
					{
						testLoadSetAssociation(result);
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
	
	/**
	 * Test loading of a simple entity
	 */
	public void testLoadEntity()
	{
	//	1. Load test message
	//
		// Setup an asynchronous event handler.
		Timer timer = new Timer()
		{
			public void run()
			{
				// Call remote init service
				StatelessInitServiceAsync remoteService = (StatelessInitServiceAsync) GWT.create(StatelessInitService.class);
				remoteService.loadTestMessage(new AsyncCallback<Message>()
				{
					public void onFailure(Throwable caught)
					{
						fail(caught.toString());
						finishTest();
					}

					public void onSuccess(Message result)
					{
						testLoadMessage(result.getId());
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
	protected void testLoadSimpleAssociation(final Message message)
	{
		// Setup an asynchronous event handler.
		Timer timer = new Timer()
		{
			public void run()
			{
				// Call remote loading service
				LoadingServiceAsync<Message> remoteService = (LoadingServiceAsync<Message>) GWT.create(LoadingService.class);
				remoteService.loadEntityAssociation(message, "author", new AsyncCallback<User>()
				{
					public void onFailure(Throwable caught)
					{
						fail(caught.toString());

						// tell the test system the test is now done
						finishTest();
					}

					public void onSuccess(User result)
					{
						assertNotNull(result);
						
						// tell the test system the test is now done
						finishTest();
					}
			
				});

			}
		};

		// Schedule the event and return control to the test system.
		timer.schedule(100);
	}
	
	/**
	 * Test load list association
	 */
	protected void testLoadSetAssociation(final User user)
	{
		// Setup an asynchronous event handler.
		Timer timer = new Timer()
		{
			public void run()
			{
				// Call remote loading service
				LoadingServiceAsync<User> remoteService = (LoadingServiceAsync<User>) GWT.create(LoadingService.class);
				remoteService.loadSetAssociation(user, "messageList", new AsyncCallback<Set<Message>>()
				{
					public void onFailure(Throwable caught)
					{
						fail(caught.toString());

						// tell the test system the test is now done
						finishTest();
					}

					public void onSuccess(Set<Message> result)
					{
						assertNotNull(result);
						assertFalse(result.isEmpty());
						
						// tell the test system the test is now done
						finishTest();
					}
			
				});

			}
		};

		// Schedule the event and return control to the test system.
		timer.schedule(100);
	}
	
	/**
	 * Test load simple association
	 */
	protected void testLoadMessage(final Integer id)
	{
		// Setup an asynchronous event handler.
		Timer timer = new Timer()
		{
			public void run()
			{
				// Call remote loading service
				LoadingServiceAsync<Message> remoteService = (LoadingServiceAsync<Message>) GWT.create(LoadingService.class);
				remoteService.loadEntity(Message.class.getName(), new IntegerParameter(id), new AsyncCallback<Message>()
				{
					public void onFailure(Throwable caught)
					{
						fail(caught.toString());

						// tell the test system the test is now done
						finishTest();
					}

					public void onSuccess(Message result)
					{
						assertNotNull(result);
						assertEquals(id, Integer.valueOf(result.getId()));
						
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
