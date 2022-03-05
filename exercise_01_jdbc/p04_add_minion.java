package exercise_01_jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

import static exercise_01_jdbc.Preconditions.*;

public class p04_add_minion {

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);

        String[] minionData = sc.nextLine().split("\\s+");
        String minionName = minionData[1];
        int minionAge = Integer.parseInt(minionData[2]);
        String minionTown = minionData[3];

        String villainName = sc.nextLine().split("\\s+")[1];

        Properties properties = getProperties();
        Connection connection = createConnection(properties);

        if (townSearch(minionTown, connection) == -1) {
            String sqlInsertTowns = "INSERT INTO towns (name) VALUES (?);";
            PreparedStatement statement = connection.prepareStatement(sqlInsertTowns);
            statement.setString(1, minionTown);
            int affectedRows = statement.executeUpdate();
            if (affectedRows != 0) {
                System.out.println("Town " + minionTown + " was added to the database.");
                insertMinions(minionName, minionAge, minionTown, connection);
            }

        }

        if (villainSearch(villainName, connection) == -1) {
            insertVillain(villainName, connection);
        }

        connectMinionToVillain(minionName, villainName, connection);

        connection.close();

    }

    private static int townSearch(String minionTown, Connection connection) throws SQLException {
        String sqlTownSearch = "SELECT id FROM towns WHERE name = ?";
        PreparedStatement statement = connection.prepareStatement(sqlTownSearch);
        statement.setString(1, minionTown);
        ResultSet resultSet = statement.executeQuery();
        return resultSet.next() ? resultSet.getInt(1) : -1;

    }

    private static void insertMinions(String minionName, int minionAge, String minionTown, Connection connection) throws SQLException {
        String sqlInsertMinions = "INSERT INTO minions (name, age, town_id) VALUES (?, ?, (Select id FROM towns WHERE name = ?));";
        PreparedStatement statement2 = connection.prepareStatement(sqlInsertMinions);
        statement2.setString(1, minionName);
        statement2.setInt(2, minionAge);
        statement2.setString(3, minionTown);
        statement2.executeUpdate();
    }

    private static int villainSearch(String villainName, Connection connection) throws SQLException {
        String sqlVillainSearch = "SELECT id FROM villains WHERE name = ?";
        PreparedStatement statement = connection.prepareStatement(sqlVillainSearch);
        statement.setString(1, villainName);
        ResultSet resultSet = statement.executeQuery();
        return resultSet.next() ? resultSet.getInt(1) : -1;
    }

    private static void insertVillain(String villainName, Connection connection) throws SQLException {
        String sqlInsertVillain = "INSERT INTO villains(name, evilness_factor) VALUES (?, + 3);";
        PreparedStatement statement4 = connection.prepareStatement(sqlInsertVillain);
        statement4.setString(1, villainName);
        if (statement4.executeUpdate() != 0) {
            System.out.println("Villain " + villainName + " was added to the database.");
        }

    }

    private static void connectMinionToVillain(String minionName, String villainName, Connection connection) throws SQLException {
        String sqlAddMinionToVillain = "INSERT INTO minions_villains VALUES" +
                " ((SELECT m.id FROM minions AS m WHERE m.name = ?),(SELECT v.id FROM villains AS v WHERE v.name = ? ));";

        PreparedStatement statement5 = connection.prepareStatement(sqlAddMinionToVillain);
        statement5.setString(1, minionName);
        statement5.setString(2, villainName);

        if (statement5.executeUpdate() != 0) {
            System.out.println("Successfully added " + minionName + " to be minion of " + villainName);
        }
    }
}
