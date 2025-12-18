import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbTest {
    public static void main(String[] args) {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
            return;
        }
        
        String url = "jdbc:mysql://localhost:3306/upi_pokit?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String username = "root";
        String password = "Codex@123";
        
        try {
            System.out.println("Attempting to connect to database...");
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Successfully connected to the database!");
            connection.close();
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database:");
            System.err.println("Error: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
        }
    }
}