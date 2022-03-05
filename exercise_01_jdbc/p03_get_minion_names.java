package exercise_01_jdbc;

import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

import static exercise_01_jdbc.Preconditions.*;

public class p03_get_minion_names {

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter below villain's ID you want to search for:");
        int villainId = Integer.parseInt(sc.nextLine());

        Properties properties = getProperties();

        Connection connection = createConnection(properties);

        String sql = "SELECT v.name, m.name, m.age" +
                " FROM minions AS m" +
                " JOIN minions_villains AS mv ON m.id = mv.minion_id" +
                " JOIN villains AS v ON mv.villain_id = v.id " +
                " WHERE v.id = ?;";

        PreparedStatement statement = getPreparedStatement(connection, sql);

        statement.setInt(1, villainId);

        ResultSet resultSet = statement.executeQuery();

        int counter = 0;
        boolean hasResult = false;

        while (resultSet.next()) {
            hasResult = true;
            counter++;

            String villainName = resultSet.getString(1);
            String minionName = resultSet.getString(2);
            int minionAge = resultSet.getInt(3);

            if (counter == 1) {
                System.out.println("Villain: " + villainName);
            }

            System.out.printf("%d. %s %d %n", counter, minionName, minionAge);

        }

        if (!hasResult) {
            System.out.println("No villain with ID " + villainId + " exists in the database.");
        }

    }

}
