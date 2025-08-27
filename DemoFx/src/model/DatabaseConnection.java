package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	private static final String URL = "jdbc:sqlite:Glico.db";
	
	public static Connection connect() throws SQLException {
		Connection conn = DriverManager.getConnection(URL);
		return conn;
		
	}
}
