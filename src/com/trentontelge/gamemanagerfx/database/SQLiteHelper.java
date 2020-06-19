package com.trentontelge.gamemanagerfx.database;

import com.trentontelge.gamemanagerfx.prototypes.Circle;
import com.trentontelge.gamemanagerfx.prototypes.Game;

import java.sql.*;
import java.util.Vector;

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

    protected static Vector<Integer> getGameIDs(String path) {
        Vector<Integer> v = new Vector<>();
        try {
            Connection conn = createNewConnection(path);
            PreparedStatement ps = conn.prepareStatement("SELECT GameID from game");
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                v.add(rs.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    protected  static Game getGameBySQLiteID(String path, int id) {
        try {
            Connection conn = createNewConnection(path);
            PreparedStatement ps = conn.prepareStatement("SELECT RJCode, Title, FolderPath, Rating, ReleaseDate, AddedDate, CircleID, Category, Tags, Comments, Size, IsRpgMakerGame, 'Language' FROM game WHERE GameID=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                String rjCode = rs.getString(1);
                String title = rs.getString(2);
                String folderPath = rs.getString(3);
                boolean rating = rs.getBoolean(4);
                Date releaseDate = java.sql.Date.valueOf(rs.getString(5) == null?null:rs.getString(5).substring(0, 9));
                Date addedDate = java.sql.Date.valueOf(rs.getString(6) == null?null:rs.getString(6).substring(0, 9));
                int circleID = rs.getInt(7);
                String category = rs.getString(8);
                String tags = rs.getString(9);
                String comments = rs.getString(10);
                int size = (int) rs.getLong(11);
                boolean isRPG = rs.getBoolean(12);
                String language = rs.getString(13);
                return new Game(circleID, size, rjCode, title, folderPath, category, tags, comments, language, rating, isRPG, releaseDate, addedDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected static Circle getCircleBySQLiteID(String path, int id) {
        try {
            Connection conn = createNewConnection(path);
            PreparedStatement ps = conn.prepareStatement("SELECT RGCode, 'Name' FROM circle WHERE CircleID=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                String rgCode = rs.getString(1);
                String name = rs.getString(2);
                return new Circle(rgCode, name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
