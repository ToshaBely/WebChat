package chat.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class ConnectionManager {
    private static final String URL = "jdbc:mysql://localhost:3306/WebChat";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "password";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Properties properties=new Properties();
            properties.setProperty("user","root");
            properties.setProperty("password","password");
            properties.setProperty("useUnicode","true");
            properties.setProperty("characterEncoding","utf8");
            connection = DriverManager.getConnection(URL, properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
}
