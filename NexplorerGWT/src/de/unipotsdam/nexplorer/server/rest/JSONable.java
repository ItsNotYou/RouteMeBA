package de.unipotsdam.nexplorer.server.rest;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Classes that extends this class are JSONable which means
 * that they can be autocasted to JSON serialized object
 * BEWARE the fact that now the names of the variables and the exact structure MATTER!!
 * 
 * Wird von Jersey zum serialisieren von Klassen aufgerufen
 * dadurch, dass auto-beanutil verwendet wird, was heißt, dass automatisch
 * die Beans nach JSON autoserialisiert werden, genügt ein generischer Adapter
 * @author Julian
 *
 * @param <T>
 */
@XmlRootElement
public abstract class JSONable<T>  {
	public class Adapter extends XmlAdapter<T,T> {

		@Override
		public T unmarshal(T dataObject) throws Exception {
			return dataObject;
		}
		
		@Override
		public T marshal(T itemJson) throws Exception {			
			return itemJson;
		}
	}
}
