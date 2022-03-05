package exercise_01_jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static exercise_01_jdbc.Preconditions.createConnection;
import static exercise_01_jdbc.Preconditions.getProperties;

public class p07_print_all_minion_names_v2 {
    public static void main(String[] args) throws SQLException {
        Properties properties = getProperties();
        Connection connection = createConnection(properties);
        String sql = "SELECT id, name FROM minions;";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        List<String> minionNames = new ArrayList<>();

        while (resultSet.next()) {
            String name = resultSet.getString("name");
            minionNames.add(name);
        }

        List<String> orderedMinionNames = new ArrayList<>();

        double length = (minionNames.size() - 1 * 1.0) / 2;

        for (int i = 0; i < length; i++) {
            orderedMinionNames.add(minionNames.get(i));
            orderedMinionNames.add(minionNames.get(minionNames.size() - 1 - i));
        }
        for (String orderedMinionName : orderedMinionNames) {
            System.out.println(orderedMinionName);
        }
    }
}
