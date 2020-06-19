package com.trentontelge.gamemanagerfx.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SQLiteHelper {
    protected static Connection createNewConnection(String path) throws Exception {
        String dbURL = "jdbc:sqlite:".concat(path);
        return DriverManager.getConnection(dbURL);
    }
    protected static int countGames(String path) {
        try {
            Connection conn = createNewConnection(path);
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM game");
            ResultSet rs = ps.executeQuery();
            return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
