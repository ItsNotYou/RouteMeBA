package de.unipotsdam.nexplorer.server.rest;


import com.sun.jersey.api.json.JSONJAXBContext;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import com.sun.jersey.api.json.JSONConfiguration;

import de.unipotsdam.nexplorer.server.rest.dto.OK;

import javax.xml.bind.JAXBContext;

/**
 * A JAXBContextResolver embeds any non-trivial types into the JAXB context so that
 * the JAXB marshaller takes all used types into account.
 * @author Martin Biermann
 * @version 0.1
 *
 */
@Provider
public class JAXBContextResolver implements ContextResolver<JAXBContext> {
	
	/**
	 * The context in which our custom types will be included.
	 */
    private JAXBContext context;
    
    /**
     * List the types that should be known to the JAXB marshalling context.
     * Note that no interfaces should appear in this list, where JAXB may not be able
     * to handle them. Better put their local (JAXB-annotated) implementations here.
     */
    private Class<?>[] types = {OK.class};

    /**
     * On construction setup the context.
     * @author Martin Biermann
     * @version 0.1
     * @throws Exception
     */
    public JAXBContextResolver() throws Exception {
    	System.out.println("JAXBContentResolver called.");
        JSONConfiguration config = JSONConfiguration.natural().build();
        context = new JSONJAXBContext(config, types);
    } 

    /**
     * The extended context is supplied when the type it shall be used for is embedded.
     * @author Martin Biermann
     * @version 0.1
     * @return JAXBContext
     */
    public JAXBContext getContext(Class<?> objectType) {
    	System.out.println(objectType.toString());
        for (Class<?> type : types) {
            if (type == objectType) {
                return context;
            }
        }
        return null;
    }
}