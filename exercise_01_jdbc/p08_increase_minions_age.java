package exercise_01_jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static exercise_01_jdbc.Preconditions.*;

public class p08_increase_minions_age {
    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);

        int[] minionsId = Arrays.stream(sc.nextLine().split("\\s+")).mapToInt(Integer::parseInt).toArray();

        Properties properties = getProperties();
        Connection connection = createConnection(properties);

        String sqlUpdate = "UPDATE minions SET age = age + 1 , name = LOWER(name) WHERE id IN (?);";

        PreparedStatement statement = connection.prepareStatement(sqlUpdate);

        for (int id : minionsId) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }

        String sqlSelect = "SELECT name, age FROM minions;";

        PreparedStatement statement1 = connection.prepareStatement(sqlSelect);
        ResultSet resultSet = statement1.executeQuery();

        while (resultSet.next()) {
            System.out.println(resultSet.getString("name") + " " + resultSet.getInt("age"));

        }
    }

}
