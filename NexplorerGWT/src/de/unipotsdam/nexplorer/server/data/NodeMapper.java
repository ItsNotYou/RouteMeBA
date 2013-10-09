package de.unipotsdam.nexplorer.server.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;

import de.unipotsdam.nexplorer.server.aodv.AodvDataPacket;
import de.unipotsdam.nexplorer.server.aodv.Locator;
import de.unipotsdam.nexplorer.server.persistence.DataFactory;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Item;
import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.server.persistence.Setting;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvDataPackets;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Items;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;
import de.unipotsdam.nexplorer.shared.DataPacketLocatable;
import de.unipotsdam.nexplorer.shared.MessageDescription;
import de.unipotsdam.nexplorer.shared.Messager;

public class NodeMapper {

	private DatabaseImpl dbAccess;
	private Locator locator;
	private DataFactory data;

	@Inject
	public NodeMapper(DatabaseImpl dbAccess, Locator locator, DataFactory data) {
		this.dbAccess = dbAccess;
		this.locator = locator;
		this.data = data;
	}

	/**
	 * 
	 * 
	 * @param p
	 * @return
	 * @throws PlayerDoesNotExistException
	 */
	public Players from(Players p) throws PlayerDoesNotExistException {
		return from(p, new HashMap<Long, Players>());
	}

	/**
	 * Necessary to prevent circular references.
	 * 
	 * @param p
	 * @param existing
	 * @return
	 * @throws PlayerDoesNotExistException
	 */
	public Players from(Players p, Map<Long, Players> existing) throws PlayerDoesNotExistException {
		if (existing == null || p == null) {
			throw new PlayerDoesNotExistException("avoiding nullpointer in NodeMapper");
		}
		if (!existing.containsKey(p.getId())) {
			Players node = new Players();
			existing.put(p.getId(), node);

			Player player = data.create(p);
			List<Item> items = player.getVisibleItems();
			Item nextItem = items.size() >= 1 ? items.get(0) : null;
			List<Items> itemStats = new LinkedList<Items>();
			for (Item item : items) {
				itemStats.add(item.inner());
			}

			Set<Player> n = player.getNeighbours();
			List<Players> neighbours = new LinkedList<Players>();
			for (Player neigh : n) {
				GetInternal internal = new GetInternal();
				neigh.execute(internal);
				Players neighAsNode = from(internal.get(), existing);
				neighbours.add(neighAsNode);
			}

			int packetCount = dbAccess.getAllDataPacketsSortedByDate(player).size();

			node.setBattery(p.getBattery());
			node.setHasSignalRangeBooster(p.getHasSignalRangeBooster() != null && p.getHasSignalRangeBooster() != 0 ? 1l : 0l);
			node.setId(p.getId());
			node.setItemInCollectionRange(nextItem == null ? null : nextItem.getId());
			node.setLastPositionUpdate(p.getLastPositionUpdate());
			node.setLatitude(p.getLatitude());
			node.setLongitude(p.getLongitude());
			node.setName(p.getName());
			node.setNearbyItems(itemStats);
			node.setNearbyItemsCount(itemStats.size());
			node.setNeighbourCount(neighbours.size());
			node.setNeighbours(neighbours);
			// TODO: the distance of the closest item should be listed even if it is not in range
			node.setNextItemDistance(nextItem == null ? null : (long) (locator.distance(player, nextItem) * 1000));
			node.setItemInCollectionRange(node.getNearbyItemsCount() > 0 ? 1l : 0l);
			node.setPacketCount(packetCount);
			node.setRange(player.getRange());
			node.setScore(p.getScore());
		}

		return existing.get(p.getId());
	}

	public Players toJSON(Player node) throws PlayerDoesNotExistException {
		Setting gameSettings = dbAccess.getSettings();

		List<Items> items = new LinkedList<Items>();
		for (Item item : dbAccess.getAllItemsNear(node, gameSettings.inner().getItemCollectionRange())) {
			Items stats = item.inner();
			items.add(stats);
		}

		// Node result = new Node(p.getId(), p.getName(), p.getLatitude(), p.getLongitude(), p.getLastPositionUpdate(), p.getScore(), p.getBattery(), p.getHasSignalRangeBooster(), node.getRange(), items, p.getPacketCount());
		// return result;
		node.execute(new SetRange(node));
		node.execute(new SetNearbyItems(items));
		MapInternal mapping = new MapInternal();
		node.execute(mapping);
		return mapping.get();
	}

	public DataPacketLocatable toJSON(AodvDataPacket packet) throws PlayerDoesNotExistException {
		AodvDataPackets p = packet.inner();
		Players o = p.getPlayersByOwnerId();

		Players currentNode = from(p.getPlayersByCurrentNodeId());
		MessageDescription description = new MessageDescription(p.getPlayersBySourceId().getId(), p.getPlayersByDestinationId().getId(), p.getPlayersByOwnerId().getId());
		Messager owner = new Messager(o.getId(), o.getName(), o.getScore());

		DataPacketLocatable result = new DataPacketLocatable(owner, p.getId(), currentNode, p.getStatus(), description);
		return result;
	}
}
