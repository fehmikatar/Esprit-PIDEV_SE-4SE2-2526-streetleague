import java.sql.Connection;
import java.sql.DriverManager;

public class TestJDBC {
    public static void main(String[] args) {
        try {
            System.out.println("Trying to connect to localhost...");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/piDB?user=root&password=");
            System.out.println("Success! Connected to " + conn.getCatalog());
            conn.close();
        } catch (Exception e) {
            System.err.println("Failed localhost connection: " + e.getMessage());
        }

        try {
            System.out.println("Trying to connect to 127.0.0.1...");
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/piDB?user=root&password=");
            System.out.println("Success! Connected to " + conn.getCatalog());
            conn.close();
        } catch (Exception e) {
            System.err.println("Failed 127.0.0.1 connection: " + e.getMessage());
        }
    }
}
