package exercise_01_jdbc;

import java.sql.*;
import java.util.*;

import static exercise_01_jdbc.Preconditions.*;

public class p07_print_all_minion_names {
    public static void main(String[] args) throws SQLException {

        Properties properties = getProperties();
        Connection connection = createConnection(properties);
        String sql = "SELECT id, name FROM minions;";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        ArrayDeque<String> queueMinions = new ArrayDeque<>();

        while (resultSet.next()) {
            String name = resultSet.getString("name");
            queueMinions.offer(name);

        }
        while (!queueMinions.isEmpty()) {
            System.out.println(queueMinions.poll());
            System.out.println(queueMinions.removeLast());
        }

    }
}
