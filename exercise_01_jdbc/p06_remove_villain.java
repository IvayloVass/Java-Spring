package exercise_01_jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

import static exercise_01_jdbc.Preconditions.*;

public class p06_remove_villain {
    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please input villain id below:");
        int villainId = Integer.parseInt(sc.nextLine());

        Properties properties = getProperties();
        Connection connection = createConnection(properties);
        String sqlDeleteMinions = "DELETE FROM minions_villains WHERE villain_id = ?;";
        PreparedStatement statement1 = connection.prepareStatement(sqlDeleteMinions);
        statement1.setInt(1, villainId);
        int affectedMinions = statement1.executeUpdate();
        if (affectedMinions == 0) {
            System.out.println("No such villain was found");
            return;
        }

        String villainName = getVillainNameByID(villainId);

        if (villainName == null) {
            System.out.println("No such villain was found");
            return;
        }
        String sqlDeleteVillains = "DELETE FROM villains WHERE id = ?;";

        PreparedStatement statement = connection.prepareStatement(sqlDeleteVillains);
        statement.setInt(1, villainId);
        statement.executeUpdate();

        System.out.println(villainName + " was deleted\n" +
                affectedMinions + " minions released");

        connection.close();

    }

}
