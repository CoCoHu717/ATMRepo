package utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConfig {
    private static String url;
    private static String user;
    private static String password;

    static {
        try {
            // 1. Create a Properties object
            Properties properties = new Properties();
            // 2. Read the db.properties file
            InputStream input = DBConfig.class.getClassLoader().getResourceAsStream("db.properties");
            // 3. Load the properties from the input stream into the properties object
            properties.load(input);
            // 4. Get the values from the properties object using the keys
            url = properties.getProperty("url");
            user = properties.getProperty("user");
            password = properties.getProperty("password");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            // Get a database connection (the arguments are: host and port, username, password)
            Connection connection = DriverManager.getConnection(url, user, password);
            // Return the database connection
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                System.out.println("Connected to the database!");
            } else {
                System.out.println("Failed to connect to the database.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
