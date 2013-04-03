import org.junit.BeforeClass;
import org.junit.Test;

import de.unipotsdam.nexplorer.server.persistence.hibernate.HibernateSessions;
import de.unipotsdam.nexplorer.server.rest.Login;

public class LoginWithRestTest {

	@BeforeClass
	public static void setUp() {
		HibernateSessions.forceNewSessionFactory();
	}

	@Test
	public void testLoginPlayerMobile() {
		Login login = new Login();
		login.loginPlayerMobile("julian", "egalmussrefaktorisiertwerden");
	}
}
