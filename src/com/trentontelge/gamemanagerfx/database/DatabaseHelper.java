package com.trentontelge.gamemanagerfx.database;

import com.trentontelge.gamemanagerfx.Main;
import com.trentontelge.gamemanagerfx.prototypes.Circle;
import com.trentontelge.gamemanagerfx.prototypes.Game;
import javafx.application.Platform;

import java.io.File;
import java.sql.*;
import java.util.Objects;
import java.util.Vector;
import java.util.function.DoubleConsumer;

import static com.trentontelge.gamemanagerfx.database.DatafileHelper.getFile;

public class DatabaseHelper {

    public enum KnownTable{
        GAMES("GAMES"),
        CIRCLES("CIRCLES"),
        IMAGES("IMAGES");

        private final String sql;

        KnownTable(String sql){
            this.sql = sql;
        }

        public String getSql() {
            return sql;
        }
    }

    private static Connection createNewConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:derby:GM;create=true");
    }

    protected static boolean tableExists(String tableName){
        try {
            Connection conn = createNewConnection();
            DatabaseMetaData md = conn.getMetaData();
            ResultSet r = md.getTables(null, null, tableName, null);
            return r.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void cleanTable(String tableName){
        if (tableExists(tableName)) {
            try {
                Connection conn = createNewConnection();
                Statement s = conn.createStatement();
                s.execute("drop table " + tableName);
                System.out.println("Dropped pre-existing table " + tableName);
                s.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createTable(KnownTable table){
        try {
            Connection conn = createNewConnection();
            Statement s = conn.createStatement();
            switch (table) {
                case GAMES: {
                    s.execute("""
                            CREATE TABLE GAMES
                            (
                                GAMEID              integer
                                    primary key,
                                RJCODE              varchar(255) default NULL,
                                TITLE               varchar(255) default NULL,
                                FOLDERPATH          varchar(255) default NULL,
                                RATING              boolean      default NULL,
                                RELEASEDATE         date     default NULL,
                                ADDEDDATE           date     default NULL,
                                CIRCLEID            integer      default NULL,
                                CATEGORY            varchar(255) default NULL,
                                TAGS                varchar(32000)         default NULL,
                                COMMENTS            varchar(32000)         default NULL,
                                SIZE                integer      default NULL,
                                ISRPGMAKER          boolean not null,
                                LANGUAGE            varchar(255)
                            )""");
                    break;
                }
                case IMAGES: {
                    s.execute("""
                            create table IMAGES
                            (
                                IMAGEID      integer
                                    primary key,
                                IMAGEPATH    varchar(255),
                                ISLISTIMAGE  boolean not null,
                                ISCOVERIMAGE boolean not null,
                                GAMEID       integer not null
                            )""");
                    break;
                }
                case CIRCLES: {
                    s.execute("""
                            create table CIRCLES
                            (
                                CIRCLEID integer
                                    primary key,
                                RGCODE   varchar(255) default NULL,
                                NAME     varchar(255)
                                    unique
                            )""");
                    break;
                }
                default: {

                }
            }
            s.close();
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void importTables(File sqlite, DoubleConsumer progressUpdate){
        System.out.println("Reading from database " + sqlite.toString());
        double max = SQLiteHelper.countGames(sqlite.toString()) * 2;
        System.out.println((int)(max/2) + " games found in SQLite database.");
        double current = 0;
        Vector<Integer> ids;
        if (max > 0){
            ids = SQLiteHelper.getGameIDs(sqlite.toString());
            for (int id : ids){
                Game g = SQLiteHelper.getGameBySQLiteID(sqlite.toString(), id);
                current++;
                double fc1 = current;
                Platform.runLater(() -> progressUpdate.accept((fc1 / max) * 100));
                //TODO calculate images
                assert g != null;
                if (circleExists(Objects.requireNonNull(SQLiteHelper.getCircleBySQLiteID(sqlite.toString(), g.getCircleid()))) == -1) {
                    writeCircle(Objects.requireNonNull(SQLiteHelper.getCircleBySQLiteID(sqlite.toString(), g.getCircleid())));
                }
                g.setCircleid(circleExists(Objects.requireNonNull(SQLiteHelper.getCircleBySQLiteID(sqlite.toString(), g.getCircleid()))));

                writeGame(g);
                current++;
                double fc2 = current;
                Platform.runLater(() -> progressUpdate.accept((fc2 / max) * 100));
                System.out.println("Added " + g.getTitle());
            }
        }
        writeDBToFile();
        Platform.runLater(Main.callback);
    }

    public static void readDBFromFile(){
        try {
            Connection conn = createNewConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (?,?,?,?,?,?,?)");
            ps.setString(1, null);
            ps.setString(2, "GAMES");
            ps.setString(3, getFile(KnownTable.GAMES).toString());
            ps.setString(4, "%");
            ps.setString(5, null);
            ps.setString(6, null);
            ps.setInt(7, 0);
            ps.execute();
            System.out.println("Read backed up copy of GAMES from " + getFile(KnownTable.GAMES).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeDBToFile(){
        try {
            if (getFile(KnownTable.GAMES).exists()){
                getFile(KnownTable.GAMES).delete();
                System.out.println("Deleted existing database backup.");
            }
            Connection conn = createNewConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
            ps.setString(1, null);
            ps.setString(2, "GAMES");
            ps.setString(3, getFile(KnownTable.GAMES).toString());
            ps.setString(4, "%");
            ps.setString(5, null);
            ps.setString(6, null);
            ps.execute();
            System.out.println("Backed up GAMES to " + getFile(KnownTable.GAMES).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeGame(Game game){
        try {
            Connection conn = createNewConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO GAMES(RJCODE, TITLE, FOLDERPATH, RATING, RELEASEDATE, ADDEDDATE, CIRCLEID, CATEGORY, TAGS, COMMENTS, SIZE, ISRPGMAKER, 'LANGUAGE') " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, game.getRJCode());
            ps.setString(2, game.getTitle());
            ps.setString(3, game.getPath());
            ps.setBoolean(4, game.getRating());
            ps.setDate(5, game.getReleaseDate());
            ps.setDate(6, game.getAddedDate());
            ps.setInt(7, game.getCircleid());
            ps.setString(8, game.getCategory());
            ps.setString(9, game.getTags());
            ps.setString(10, game.getComments());
            ps.setInt(11, game.getSize());
            ps.setBoolean(12, game.isRPGMaker());
            ps.setString(13, game.getLanguage());
            ps.executeUpdate();
            ps.close();
            conn.close();
            System.out.println("Added game " + game.getTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeCircle(Circle circle){
        try {
            Connection conn = createNewConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO CIRCLES(RGCODE, 'NAME') VALUES (?, ?)");
            ps.setString(1, circle.getRgCode());
            ps.setString(2, circle.getName());
            ps.executeUpdate();
            ps.close();
            conn.close();
            System.out.println("Added circle " + circle.getName());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    protected static int circleExists(Circle circle){
        try {
            Connection conn = createNewConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT CIRCLEID FROM CIRCLES WHERE 'NAME'=?");
            ps.setString(1, circle.getName());
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return rs.getInt(1);
            }
            ps = conn.prepareStatement("SELECT CIRCLEID FROM CIRCLES WHERE RGCODE=?");
            ps.setString(1, circle.getRgCode());
            rs = ps.executeQuery();
            if (rs.next()){
                return rs.getInt(1);
            }
            ps.close();
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return -1;
    }

    public static Vector<Game> getAllGames(){
        Vector<Game> v = new Vector<>();
        try {
            Connection conn = createNewConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM GAMES");
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Game g = new Game(rs.getInt("GAMEID"),
                        rs.getInt("CIRCLEID"),
                        rs.getInt("SIZE"),
                        rs.getString("RJCODE"),
                        rs.getString("TITLE"),
                        rs.getString("FOLDERPATH"),
                        rs.getString("CATEGORY"),
                        rs.getString("TAGS"),
                        rs.getString("COMMENTS"),
                        rs.getString("LANGUAGE"),
                        rs.getBoolean("RATING"),
                        rs.getBoolean("ISRPGMAKER"),
                        rs.getDate("RELEASEDATE"),
                        rs.getDate("ADDEDDATE"));
                v.add(g);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println("Read "+ v.size() +" games to array.");
        return v;
    }
}
