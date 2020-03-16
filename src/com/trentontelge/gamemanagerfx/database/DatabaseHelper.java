package com.trentontelge.gamemanagerfx.database;

import java.sql.*;

public class DatabaseHelper {

    public enum KnownTable{
        GAMES("GAMES"),
        CIRCLES("CIRCLE"),
        IMAGES("IMAGES");

        private String sql;

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

    protected static void cleanTable(String tableName){
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
                            "    RATING              tinyint      default NULL,\n" +
                            "    RELEASEDATE         datetime     default NULL,\n" +
                            "    ADDEDDATE           datetime     default NULL,\n" +
                            "    CIRCLEID            integer      default NULL,\n" +
                            "    CATEGORY            varchar(255) default NULL,\n" +
                            "    TAGS                text         default NULL,\n" +
                            "    COMMENTS            text         default NULL,\n" +
                            "    SIZE                integer      default NULL,\n" +
                            "    ISRPGMAKER          tinyint not null,\n" +
                            "    LANGUAGE            varchar(255)\n" +
                            ")");
                    break;
                }
                case IMAGES: {
                    s.execute("create table image\n" +
                            "(\n" +
                            "    ImageID      integer\n" +
                            "        primary key,\n" +
                            "    ImagePath    varchar(255),\n" +
                            "    IsListImage  tinyint not null,\n" +
                            "    IsCoverImage tinyint not null,\n" +
                            "    GameID       integer not null\n" +
                            ")");
                    break;
                }
                case CIRCLES: {
                    s.execute("create table circle\n" +
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

    protected static void importTables(){

    }
}
