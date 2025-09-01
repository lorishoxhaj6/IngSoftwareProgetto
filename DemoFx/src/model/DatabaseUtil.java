package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DatabaseUtil {
	private static final String URL = "jdbc:sqlite:Database/Glico.db";
	
	public static Connection connect() throws SQLException {
		Connection conn = DriverManager.getConnection(URL);
		return conn;
	}
	
	// Converte tutte le righe in una ObservableList<T>
	
    public static <T> ObservableList<T> queryList( //'centralizza' tutte le query del database
            String sql,
            Consumer<PreparedStatement> binder,
            RowMapper<T> mapper) throws SQLException {

        try (Connection con = DatabaseUtil.connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (binder != null) binder.accept(ps);

            try (ResultSet rs = ps.executeQuery()) {
                ObservableList<T> out = FXCollections.observableArrayList();
                while (rs.next()) {
                    out.add(mapper.map(rs));
                }
                return out;
            }
        }
    }
	
	
	
	// --- piccole interfacce funzionali di supporto ---

    @FunctionalInterface
    public interface RowMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }

    
	
    
}
