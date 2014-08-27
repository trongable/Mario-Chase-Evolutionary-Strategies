package de.manualoverri.mariochase.learning;

import com.sun.rowset.CachedRowSetImpl;

import javax.sql.rowset.CachedRowSet;
import java.sql.*;

/**
 * User: Trong
 * Date: 7/20/2014
 * Time: 10:15 PM
 */
public class DbHelper {

    private static String JDBC = "org.sqlite.JDBC";
    private static String DB_CONNECTION = "jdbc:sqlite:mariochase.db";

    // TODO: Change to connection pooling?

    public static void executeUpdate(String sql) {
        try {
            Class.forName(JDBC);
            Connection connection = DriverManager.getConnection(DB_CONNECTION);
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ResultSet executeQuery(String sql) {
        try {
            Class.forName(JDBC);
            Connection connection = DriverManager.getConnection(DB_CONNECTION);
            Statement statement = connection.createStatement();

            ResultSet rs = statement.executeQuery(sql);
            CachedRowSet rowset = new CachedRowSetImpl();
            rowset.populate(rs);

            statement.close();
            connection.close();

            return rowset;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int getScalar(String sql) {
        try {
            Class.forName(JDBC);
            Connection connection = DriverManager.getConnection(DB_CONNECTION);
            Statement statement = connection.createStatement();

            ResultSet rs = statement.executeQuery(sql);
            CachedRowSet rowset = new CachedRowSetImpl();
            rowset.populate(rs);

            statement.close();
            connection.close();

            while (rowset.next()) {
                return rowset.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
}
