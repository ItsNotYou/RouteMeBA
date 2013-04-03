package de.unipotsdam.nexplorer.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonRootName;

import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Items;

/**
 * This class gives a hash like representation for the items for faster
 * client side processing
 * @author Julian
 *
 */
@XmlRootElement
@JsonRootName("ItemJSON")
public class ItemMap implements Serializable {

	@JsonProperty("items")
	HashMap<Long, Items> itemMap;

	public ItemMap() {
	}

	public void setItemMap(HashMap<Long, Items> itemMap) {
		this.itemMap = itemMap;
	}

	public HashMap<Long, Items> getItemMap() {
		return itemMap;
	}

	public ItemMap(HashMap<Long, Items> itemMap) {
		this.itemMap = itemMap;
	}

	public ItemMap(List<Items> items) {
		this.itemMap = new HashMap<Long, Items>();
		for (Items item : items) {
			this.itemMap.put(item.getId(), item);
		}
	}
}
