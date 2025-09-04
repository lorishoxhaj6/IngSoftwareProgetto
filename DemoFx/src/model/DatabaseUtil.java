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
	// metodo generico per INSERT/UPDATE/DELETE
    public static int executeUpdate(String sql, SQLConsumer<PreparedStatement> preparer) {
		try(Connection con = DatabaseUtil.connect();
				PreparedStatement ps = con.prepareStatement(sql)){
			if(preparer != null) {
				preparer.accept(ps);
			}
			
			return ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
    }
    public static int executeUpdateInsert(String sql, SQLConsumer<PreparedStatement> preparer) throws SQLException {
		try(Connection con = DatabaseUtil.connect();
				PreparedStatement ps = con.prepareStatement(sql)){
			if(preparer != null) {
				preparer.accept(ps);
			}
			
			ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) { // ottengo la primaryKey id della nuova misurazione
				if (rs.next())
					return rs.getInt(1);
			}
			return -1;
		}
		catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			return -1;
		}
    }
	
	
	// --- piccole interfacce funzionali di supporto ---

    @FunctionalInterface
    public interface RowMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
    
    public interface SQLConsumer<T> {  // ho dovuto creare questa interfaccia cosi gestiche eccezioni checked
    	// cosi non devo wrappare le lambda expression con try/catch
    	void accept(T t) throws SQLException;
    	// prende un campo generico T
    }

    
	
    
}
