package com.trentontelge.gamemanagerfx.database;

import com.trentontelge.gamemanagerfx.Main;
import com.trentontelge.gamemanagerfx.prototypes.Circle;
import com.trentontelge.gamemanagerfx.prototypes.Game;
import com.trentontelge.gamemanagerfx.prototypes.Image;
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
                                GAMEID              integer PRIMARY KEY NOT NULL,
                                RJCODE              varchar(255) default NULL,
                                TITLE               varchar(255) default NULL,
                                FOLDERPATH          varchar(255) default NULL,
                                RATING              integer      default NULL,
                                RELEASEDATE         date     default NULL,
                                ADDEDDATE           date     default NULL,
                                CIRCLEID            integer      default NULL,
                                CATEGORY            varchar(255) default NULL,
                                TAGS                varchar(32000)         default NULL,
                                COMMENTS            varchar(32000)         default NULL,
                                SIZE                integer      default NULL,
                                ISRPGMAKER          boolean not null,
                                LANG            varchar(255)
                            )""");
                    break;
                }
                case IMAGES: {
                    s.execute("""
                            create table IMAGES
                            (
                                IMAGEID      integer PRIMARY KEY NOT NULL,
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
                                CIRCLEID integer PRIMARY KEY NOT NULL,
                                RGCODE   varchar(255) default NULL,
                                TITLE     varchar(255)
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
                System.out.print("[" + ((int)(current/2)) + "/" + ((int)(max/2)) + "]");
                Game g = SQLiteHelper.getGameBySQLiteID(sqlite.toString(), id);
                current++;
                double fc1 = current;
                Platform.runLater(() -> progressUpdate.accept((fc1 / max)));
                assert g != null;
                if (g.getCircleid() != 0) {
                    if (circleExists(Objects.requireNonNull(SQLiteHelper.getCircleBySQLiteID(sqlite.toString(), g.getCircleid()))) == -1) {
                        writeCircle(Objects.requireNonNull(SQLiteHelper.getCircleBySQLiteID(sqlite.toString(), g.getCircleid())));
                    }
                    g.setCircleid(circleExists(Objects.requireNonNull(SQLiteHelper.getCircleBySQLiteID(sqlite.toString(), g.getCircleid()))));

                }
                writeGame(g);
                Vector<Image> v = SQLiteHelper.getImagesBySQLiteGameID(sqlite.toString(), id);
                int newID = getGame(g.getTitle()).getId();
                if (v != null){
                    System.out.println("Found " + v.size() + " images.");
                    for (Image i : v){
                        i.setGameid(newID);
                        writeImage(i);
                    }
                }
                current++;
                double fc2 = current;
                Platform.runLater(() -> progressUpdate.accept((fc2 / max)));
            }
        }
        writeDBToFile();
        Platform.runLater(Main.callback);
        Platform.runLater(Main.importBarStage::close);
    }

    public static void readDBFromFile(){
        if (getFile(KnownTable.GAMES).exists()) {
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
                ps.close();
                conn.close();
                System.out.println("Read backed up copy of GAMES from " + getFile(KnownTable.GAMES).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No GAMES database backup found.");
        }
        if (getFile(KnownTable.CIRCLES).exists()) {
            try {
                Connection conn = createNewConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (?,?,?,?,?,?,?)");
                ps.setString(1, null);
                ps.setString(2, "CIRCLES");
                ps.setString(3, getFile(KnownTable.CIRCLES).toString());
                ps.setString(4, "%");
                ps.setString(5, null);
                ps.setString(6, null);
                ps.setInt(7, 0);
                ps.execute();
                ps.close();
                conn.close();
                System.out.println("Read backed up copy of CIRCLES from " + getFile(KnownTable.CIRCLES).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No CIRCLES database backup found.");
        }
        if (getFile(KnownTable.IMAGES).exists()) {
            try {
                Connection conn = createNewConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (?,?,?,?,?,?,?)");
                ps.setString(1, null);
                ps.setString(2, "IMAGES");
                ps.setString(3, getFile(KnownTable.IMAGES).toString());
                ps.setString(4, "%");
                ps.setString(5, null);
                ps.setString(6, null);
                ps.setInt(7, 0);
                ps.execute();
                ps.close();
                conn.close();
                System.out.println("Read backed up copy of IMAGES from " + getFile(KnownTable.IMAGES).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No IMAGES database backup found.");
        }
    }

    public static void writeDBToFile(){
        try {
            if (getFile(KnownTable.GAMES).exists()){
                getFile(KnownTable.GAMES).delete();
                System.out.println("Deleted existing GAMES database backup.");
            }
            if (getFile(KnownTable.CIRCLES).exists()){
                getFile(KnownTable.CIRCLES).delete();
                System.out.println("Deleted existing CIRCLES database backup.");
            }
            if (getFile(KnownTable.IMAGES).exists()){
                getFile(KnownTable.IMAGES).delete();
                System.out.println("Deleted existing IMAGES database backup.");
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
            ps = conn.prepareStatement(
                    "CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
            ps.setString(1, null);
            ps.setString(2, "CIRCLES");
            ps.setString(3, getFile(KnownTable.CIRCLES).toString());
            ps.setString(4, "%");
            ps.setString(5, null);
            ps.setString(6, null);
            ps.execute();
            ps = conn.prepareStatement(
                    "CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
            ps.setString(1, null);
            ps.setString(2, "IMAGES");
            ps.setString(3, getFile(KnownTable.IMAGES).toString());
            ps.setString(4, "%");
            ps.setString(5, null);
            ps.setString(6, null);
            ps.execute();
            ps.close();
            conn.close();
            System.out.println("Backed up databases to file");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeGame(Game game){
        int id = countOfGames() + 1;
        while (gameIDExists(id)){
            id++;
        }
        try {
            Connection conn = createNewConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO GAMES(GAMEID, RJCODE, TITLE, FOLDERPATH, RATING, RELEASEDATE, ADDEDDATE, CIRCLEID, CATEGORY, TAGS, COMMENTS, SIZE, ISRPGMAKER, LANG) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setInt(1, id);
            ps.setString(2, game.getRJCode());
            ps.setString(3, game.getTitle());
            ps.setString(4, game.getPath());
            ps.setInt(5, game.getRating());
            ps.setDate(6, game.getReleaseDate());
            ps.setDate(7, game.getAddedDate());
            ps.setInt(8, game.getCircleid());
            ps.setString(9, game.getCategory());
            ps.setString(10, game.getTags());
            ps.setString(11, game.getComments());
            ps.setInt(12, game.getSize());
            ps.setBoolean(13, game.isRPGMaker());
            ps.setString(14, game.getLanguage());
            ps.executeUpdate();
            ps.close();
            conn.close();
            System.out.println("Added game " + game.getTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Game getGame(String title){
        Game g = new Game();
        try {
            Connection conn = createNewConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM GAMES WHERE TITLE=?");
            ps.setString(1, title);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                g = new Game(rs.getInt("GAMEID"),
                        rs.getInt("CIRCLEID"),
                        rs.getInt("SIZE"),
                        rs.getString("RJCODE"),
                        rs.getString("TITLE"),
                        rs.getString("FOLDERPATH"),
                        rs.getString("CATEGORY"),
                        rs.getString("TAGS"),
                        rs.getString("COMMENTS"),
                        rs.getString("LANG"),
                        rs.getInt("RATING"),
                        rs.getBoolean("ISRPGMAKER"),
                        rs.getDate("RELEASEDATE"),
                        rs.getDate("ADDEDDATE"));
            }
            ps.close();
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return g;
    }

    public static void writeCircle(Circle circle){
        int id = countOfCircles() + 1;
        while (circleIDExists(id)){
            id++;
        }
        try {
            Connection conn = createNewConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO CIRCLES(CIRCLEID, RGCODE, TITLE) VALUES (?, ?, ?)");
            ps.setInt(1, id);
            ps.setString(2, circle.getRgCode());
            ps.setString(3, circle.getName());
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
            PreparedStatement ps = conn.prepareStatement("SELECT CIRCLEID FROM CIRCLES WHERE TITLE=?");
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

    protected static int countOfCircles() {
        int n = 0;
        try {
            Connection conn = createNewConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM CIRCLES");
            ResultSet rs = ps.executeQuery();
            rs.next();
            n = rs.getInt(1);
            ps.close();
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return n;
    }

    protected static boolean circleIDExists(int id){
        boolean e = false;
        try {
            Connection conn = createNewConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM CIRCLES WHERE CIRCLEID=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            e = rs.next();
            ps.close();
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return e;
    }

    public static Circle getCircle(int id){
        Circle c = new Circle();
        try {
            Connection conn = createNewConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM CIRCLES WHERE CIRCLEID=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                c = new Circle(rs.getInt("CIRCLEID"), rs.getString("RGCODE"), rs.getString("TITLE"));
            }
            ps.close();
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return c;
    }

    public static Vector<Circle> getAllCircles() {
        Vector<Circle> v = new Vector<>();
        try {
            Connection conn = createNewConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM CIRCLES");
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                v.add(new Circle(rs.getInt("CIRCLEID"), rs.getString("RGCODE"), rs.getString("TITLE")));
            }
            ps.close();
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return v;
    }

    protected static int countOfGames(){
        int n = 0;
        try {
            Connection conn = createNewConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM GAMES");
            ResultSet rs = ps.executeQuery();
            rs.next();
            n = rs.getInt(1);
            ps.close();
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return n;
    }

    protected  static  boolean gameIDExists(int id){
        boolean e = false;
        try {
            Connection conn = createNewConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM GAMES WHERE GAMEID=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            e = rs.next();
            ps.close();
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return e;
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
                        rs.getString("LANG"),
                        rs.getInt("RATING"),
                        rs.getBoolean("ISRPGMAKER"),
                        rs.getDate("RELEASEDATE"),
                        rs.getDate("ADDEDDATE"));
                v.add(g);
            }
            ps.close();
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println("Read "+ v.size() +" games to array.");
        return v;
    }

    protected static int countOfImages(){
        int n = 0;
        try {
            Connection conn = createNewConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM IMAGES");
            ResultSet rs = ps.executeQuery();
            rs.next();
            n = rs.getInt(1);
            ps.close();
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return n;
    }

    protected  static  boolean imageIDExists(int id){
        boolean e = false;
        try {
            Connection conn = createNewConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM IMAGES WHERE IMAGEID=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            e = rs.next();
            ps.close();
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return e;
    }

    public static void writeImage(Image image){
        int id = countOfImages() + 1;
        while (imageIDExists(id)){
            id++;
        }
        try {
            Connection conn = createNewConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO IMAGES(IMAGEID, IMAGEPATH, ISLISTIMAGE, ISCOVERIMAGE, GAMEID) VALUES(?, ?, ?, ?, ?)");
            ps.setInt(1, id);
            ps.setString(2, image.getImagepath());
            ps.setBoolean(3, image.isIslistimage());
            ps.setBoolean(4, image.isIscoverimage());
            ps.setInt(5, image.getGameid());
            ps.executeUpdate();
            ps.close();
            conn.close();
            System.out.println("Added image " + image.getImagepath());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static javafx.scene.image.Image getListImage(int gameID){
        javafx.scene.image.Image i = new javafx.scene.image.Image("img\\0.png");
        try {
            Connection conn = createNewConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM IMAGES WHERE GAMEID=? AND ISLISTIMAGE=true");
            ps.setInt(1, gameID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                i = new javafx.scene.image.Image("file:///" + Main.prefs.getLibraryHome() + System.getProperty("file.separator") +  rs.getString("IMAGEPATH"));
            }
            ps.close();
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return i;
    }

    public static Vector<javafx.scene.image.Image> getGameImages(int gameID) {
        Vector<javafx.scene.image.Image> v = new Vector<>();
        try {
            Connection conn = createNewConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM IMAGES WHERE GAMEID=?");
            ps.setInt(1, gameID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                v.add(new javafx.scene.image.Image("file:///" + Main.prefs.getLibraryHome() + System.getProperty("file.separator") + rs.getString("IMAGEPATH")));
            }
            ps.close();
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return v;
    }

    public static Vector<Image> getAllImages() {
        Vector<Image> v = new Vector<>();
        try {
            Connection conn = createNewConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM IMAGES");
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                v.add(new Image(rs.getInt("IMAGEID"), rs.getInt("GAMEID"), rs.getBoolean("ISLISTIMAGE"), rs.getBoolean("ISCOVERIMAGE"), rs.getString("IMAGEPATH")));
            }
            ps.close();
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return v;
    }
}
