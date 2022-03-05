package exercise_01_jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static exercise_01_jdbc.Preconditions.*;

public class p05_change_town_names_casing {
    public static void main(String[] args) throws SQLException {

        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter country name below:");
        String countryName = sc.nextLine();

        Properties properties = getProperties();

        Connection connection = createConnection(properties);

        String updateSqlQuery = "UPDATE towns SET name = UPPER(name) WHERE country = ?;";
        String selectSqlQuery = "SELECT name FROM towns WHERE country = ?;";

        PreparedStatement statement1 = getPreparedStatement(connection, updateSqlQuery);
        statement1.setString(1, countryName);
        int rowsAffected = statement1.executeUpdate();
        PreparedStatement statement2 = getPreparedStatement(connection, selectSqlQuery);
        statement2.setString(1, countryName);

        ResultSet resultSet = statement2.executeQuery();
        Set<String> cities = new LinkedHashSet<>();

        while (resultSet.next()) {
            String name = resultSet.getString("name");
            cities.add(name);
        }

        if (rowsAffected != 0) {
            System.out.println(rowsAffected + " town names were affected.");
            System.out.println("[" + String.join(", ", cities) + "]");
        } else {
            System.out.println("No town names were affected.");
        }
    }
}
