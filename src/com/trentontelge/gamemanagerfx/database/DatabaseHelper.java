package com.trentontelge.gamemanagerfx.database;

import com.trentontelge.gamemanagerfx.prototypes.Game;

import java.io.File;
import java.sql.*;
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
                    s.execute("CREATE TABLE GAMES\n" +
                            "(\n" +
                            "    GAMEID              integer\n" +
                            "        primary key,\n" +
                            "    RJCODE              varchar(255) default NULL,\n" +
                            "    TITLE               varchar(255) default NULL,\n" +
                            "    FOLDERPATH          varchar(255) default NULL,\n" +
                            "    RATING              boolean      default NULL,\n" +
                            "    RELEASEDATE         date     default NULL,\n" +
                            "    ADDEDDATE           date     default NULL,\n" +
                            "    CIRCLEID            integer      default NULL,\n" +
                            "    CATEGORY            varchar(255) default NULL,\n" +
                            "    TAGS                varchar(32000)         default NULL,\n" +
                            "    COMMENTS            varchar(32000)         default NULL,\n" +
                            "    SIZE                integer      default NULL,\n" +
                            "    ISRPGMAKER          boolean not null,\n" +
                            "    LANGUAGE            varchar(255)\n" +
                            ")");
                    break;
                }
                case IMAGES: {
                    s.execute("create table IMAGES\n" +
                            "(\n" +
                            "    ImageID      integer\n" +
                            "        primary key,\n" +
                            "    ImagePath    varchar(255),\n" +
                            "    IsListImage  boolean not null,\n" +
                            "    IsCoverImage boolean not null,\n" +
                            "    GameID       integer not null\n" +
                            ")");
                    break;
                }
                case CIRCLES: {
                    s.execute("create table CIRCLES\n" +
                            "(\n" +
                            "    CircleID integer\n" +
                            "        primary key,\n" +
                            "    RGCode   varchar(255) default NULL,\n" +
                            "    Name     varchar(255)\n" +
                            "        unique\n" +
                            ")");
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
        //TODO copy entries from sqlite db to internal derby
        System.out.println("Reading from database " + sqlite.toString());
        int max = SQLiteHelper.countGames(sqlite.toString());
        System.out.println(max + " games found in SQLite database.");
    }

    protected void readDB(){

    }

    public static void writeDB(){
        try {
            System.out.println(getFile(KnownTable.GAMES).getPath());
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
            System.out.println("Added game " + game.getTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
