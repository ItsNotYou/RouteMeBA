package de.unipotsdam.nexplorer.server.aodv;

import com.google.inject.assistedinject.Assisted;

import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvDataPackets;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRouteRequestBufferEntries;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingMessages;

public interface AodvFactory {

	AodvDataPacket create(AodvDataPackets inner);

	Link create(@Assisted("from") AodvNode from, @Assisted("to") AodvNode to);

	AodvNode create(Player inner);

	AodvRoutingMessage create(AodvRoutingMessages inner);

	AodvRouteRequestBufferEntry create(AodvRouteRequestBufferEntries inner);

	RREQDestination create(AodvNode node, long destinationId);
}
