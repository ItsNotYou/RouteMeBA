import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.junit.Test;

public class UrlDecodeTest {

	@Test
	public void shouldDecodeUrl() throws UnsupportedEncodingException {
		String example = "C:\\Tomcat%207.0\\webapps\\routeme\\WEB-INF\\settings.xml";
		String expected = "C:\\Tomcat 7.0\\webapps\\routeme\\WEB-INF\\settings.xml";

		String result = URLDecoder.decode(example, "UTF-8");

		assertEquals(expected, result);
	}
}
