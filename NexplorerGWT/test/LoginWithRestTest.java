import org.junit.AfterClass;
import org.junit.Test;

import de.unipotsdam.nexplorer.server.persistence.hibernate.HibernateSessions;
import de.unipotsdam.nexplorer.server.rest.Login;

public class LoginWithRestTest {

	@AfterClass
	public static void tearDown() {
		HibernateSessions.clearDatabase();
	}

	@Test
	public void testLoginPlayerMobile() {
		Login login = new Login();
		login.loginPlayerMobile("julian", "egalmussrefaktorisiertwerden");
	}
}
