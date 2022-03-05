package exercise_01_jdbc;

import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

import static exercise_01_jdbc.Preconditions.*;

public class p09_increase_age_stored_procedure {
    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter minion id below:");
        int minionId = Integer.parseInt(sc.nextLine());

        Properties properties = getProperties();
        Connection connection = createConnection(properties);

        String sqlCallProcedure = "CALL usp_get_older(?);";
        CallableStatement callableStatement = connection.prepareCall(sqlCallProcedure);
        callableStatement.setInt(1, minionId);

        callableStatement.executeUpdate();

        String sqlResult = "SELECT name, age FROM minions WHERE id = ?;";

        PreparedStatement statement = connection.prepareStatement(sqlResult);
        statement.setInt(1, minionId);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            System.out.println(resultSet.getString("name") + " " + resultSet.getString("age"));
        }

    }
}
