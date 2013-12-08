package de.unipotsdam.nexplorer.server.persistence.hibernate;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.hibernate.jdbc.Work;

public class TruncateGameTables implements Work {

	@Override
	public void execute(Connection conn) throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.execute("DELETE FROM aodv_data_packets");
		stmt.execute("DELETE FROM neighbours");
		stmt.execute("DELETE FROM players");
		stmt.execute("DELETE FROM settings");
		stmt.execute("DELETE FROM aodv_node_data");
		stmt.execute("DELETE FROM aodv_routing_messages");
		stmt.execute("DELETE FROM aodv_routing_table_entries");
		stmt.execute("DELETE FROM aodv_route_request_buffer_entries");
		stmt.execute("DELETE FROM items");
	}
}
