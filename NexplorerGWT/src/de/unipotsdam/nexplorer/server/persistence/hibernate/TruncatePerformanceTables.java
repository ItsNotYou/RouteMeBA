package de.unipotsdam.nexplorer.server.persistence.hibernate;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.hibernate.jdbc.Work;

public class TruncatePerformanceTables implements Work {

	@Override
	public void execute(Connection conn) throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.execute("DELETE FROM performance");
		stmt.execute("DELETE FROM position_backlog");
	}
}
