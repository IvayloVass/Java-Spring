package exercise_01_jdbc;

import java.sql.*;
import java.util.Properties;

public class Preconditions {

    // ToDo: Please change your localhost port if necessary
    private static final String CONNECTION = "jdbc:mysql://localhost:3306/";
    private static final String SCHEMA_NAME = "minions_db";

    protected static Properties getProperties() {
        Properties properties = new Properties();
        // ToDo: Please set user and password
        properties.setProperty("user", "root");
        properties.setProperty("password", "");
        return properties;
    }


    protected static PreparedStatement getPreparedStatement(Connection connection, String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    protected static Connection createConnection(Properties properties) throws SQLException {
        return DriverManager.getConnection(CONNECTION + SCHEMA_NAME, properties);
    }

    protected static String getVillainNameByID(int villainId) throws SQLException {
        Properties properties = getProperties();
        Connection connection = createConnection(properties);
        String sql = "SELECT name FROM villains WHERE id = ?;";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, villainId);
        ResultSet resultSet = statement.executeQuery();
        String villainName = null;
        while (resultSet.next()) {
            villainName = resultSet.getString(1);
        }
        return villainName;

    }
}
