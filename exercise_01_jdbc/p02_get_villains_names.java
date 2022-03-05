package exercise_01_jdbc;

import java.sql.*;
import java.util.Properties;

import static exercise_01_jdbc.Preconditions.*;

public class p02_get_villains_names {

    private static final String CONNECTION = "jdbc:mysql://localhost:3306/";
    private static final String SCHEMA_NAME = "minions_db";
    public static final int MINIONS_COUNT = 15;

    public static void main(String[] args) throws SQLException {

        Properties properties = getProperties();

        Connection connection = DriverManager.getConnection(CONNECTION + SCHEMA_NAME, properties);
        PreparedStatement statement =
                connection.prepareStatement("""
                        SELECT v.name, COUNT(DISTINCT mv.minion_id) AS `minions_count` FROM villains AS v
                        JOIN minions_villains AS mv ON v.id = mv.villain_id
                        GROUP BY v.name
                        HAVING `minions_count` > ?
                        ORDER BY `minions_count` DESC;""");

        statement.setInt(1, MINIONS_COUNT);

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            System.out.println(resultSet.getString("v.name") + " " + resultSet.getString("minions_count"));
        }

    }
}
