package de.unipotsdam.nexplorer.shared;

/**
 * This class shows the status of a data packet
 * @author Julian
 *
 */
public class Aodv {

	public static final byte DATA_PACKET_STATUS_UNDERWAY = 1;
	public static final byte DATA_PACKET_STATUS_ARRIVED = 2;
	public static final byte DATA_PACKET_STATUS_ERROR = 3;
	public static final byte DATA_PACKET_STATUS_WAITING_FOR_ROUTE = 4;
	public static final byte DATA_PACKET_STATUS_NODE_BUSY = 5;
	public static final byte DATA_PACKET_STATUS_CANCELLED = 6;

	public static final String ROUTING_MESSAGE_TYPE_RREQ = "RREQ";
	public static final String ROUTING_MESSAGE_TYPE_RERR = "RERR";
}
