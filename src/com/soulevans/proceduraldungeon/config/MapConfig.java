package com.soulevans.proceduraldungeon.config;

import com.soulevans.proceduraldungeon.logger.Logger;

import java.io.BufferedWriter;
import java.io.IOException;

public class MapConfig {
    public static final double TILESIZE = 16;
    public static int MAP_WIDTH = 37;
    public static int MAP_HEIGHT = 37;

    public static int ROOM_ATTEMPTS = getOptimalRoomAttempts();
    public static int ROOMS_MADE = -1;

    public static int MIN_ROOM_WIDTH = 3;
    public static int MIN_ROOM_HEIGHT = 3;
    public static int ROOM_WIDTH_AMPL = 5;
    public static int ROOM_HEIGHT_AMPL = 5;
    public static int MIN_ROOM_DIST = 1;
    public static int ROOM_DIST_AMPL = 2;

    public static int WINDINESS_PERCENT = 70;
    public static int MAX_DOOR_COUNT = 3;
    public static int DOOR_CHANCE = 20;

    public static int FILL_DEADENDS = 100;
    public static double CHEST_CHANCE = 80;

    public static int ENEMY_COUNT = 10;
    public static int CHEST_COUNT = 10;

    public static final char WALL_CHAR = '#';
    public static final char DOOR_CHAR = 'd';
    public static final char CHEST_CHAR = 'c';
    public static final char ENTRY_CHAR = 's';
    public static final char EXIT_CHAR = 'x';
    public static final char FLOOR_CHAR = '_';
    public static final char ENEMY_CHAR = 'e';
    public static final char PLAYER_CHAR = 'p';
    public static final char OUT_OF_BOUNDS_CHAR = '?';
    public static final char SHADOW_CHAR = '¤';


    public static int getOptimalRoomAttempts(){
        // plus 6 because of the error margin
        return (int) Math.round(0.0137 * MapConfig.MAP_WIDTH * MapConfig.MAP_WIDTH + 0.4237 * MapConfig.MAP_WIDTH - 6.256) + 6;
    }

    private static String ROOM_LOG_File = "roomAttempts("+MAP_WIDTH+"x"+MAP_HEIGHT+")."+
            MIN_ROOM_WIDTH+"."+MIN_ROOM_HEIGHT+"."+ROOM_WIDTH_AMPL+"."+ROOM_HEIGHT_AMPL+"."+MIN_ROOM_DIST+"."+ROOM_DIST_AMPL+".csv";

    private static void startRoomAttemptsFile(){
        if(!Logger.logExist(MapConfig.ROOM_LOG_File)) {
            Logger.logFile(MapConfig.ROOM_LOG_File, ";;;;min room width:;"+MapConfig.MIN_ROOM_HEIGHT+";min room height:;"+MapConfig.MIN_ROOM_HEIGHT);
            Logger.logFile(MapConfig.ROOM_LOG_File, ";;;;room width ampl:;"+MapConfig.ROOM_WIDTH_AMPL+";room height aml:;"+MapConfig.ROOM_HEIGHT_AMPL);
            Logger.logFile(MapConfig.ROOM_LOG_File, ";;;;min room dist:;"+MapConfig.MIN_ROOM_DIST+";room dist aml:;"+MapConfig.ROOM_DIST_AMPL);
            Logger.logFile(MapConfig.ROOM_LOG_File, ";;;;failed:;\"=DARABTELI(B2:B2000;\"\"<2\"\")\";");
            Logger.logFile(MapConfig.ROOM_LOG_File, "room attempts;made;;;avg:;=ÁTLAG(B2:B2000);max:;=MAX(B2:B2000);lines:;=2000-DARABÜRES(B2:B2000);");
        }
    }

    public static void writeRoomAttemptsFile(){
        MapConfig.ROOM_LOG_File = "roomAttempts("+MapConfig.MAP_WIDTH+"x"+MapConfig.MAP_HEIGHT+")."+
                MapConfig.MIN_ROOM_WIDTH+"."+MapConfig.MIN_ROOM_HEIGHT+"."+MapConfig.ROOM_WIDTH_AMPL+"."+
                MapConfig.ROOM_HEIGHT_AMPL+"."+MapConfig.MIN_ROOM_DIST+"."+MapConfig.ROOM_DIST_AMPL+".csv";
        MapConfig.startRoomAttemptsFile();
        Logger.logFile(MapConfig.ROOM_LOG_File, MapConfig.ROOM_ATTEMPTS + ";" + MapConfig.ROOMS_MADE+";");
    }

    public static void saveConfig(BufferedWriter writer) throws IOException {
        writer.write("map size, width: " + MAP_WIDTH + ", height: " + MAP_HEIGHT); writer.newLine();
        writer.write("room attempts: " + ROOM_ATTEMPTS + ", made: " + ROOMS_MADE); writer.newLine();
        writer.write("room size, min width: " + MIN_ROOM_WIDTH + ", min height: " + MIN_ROOM_HEIGHT); writer.newLine();
        writer.write("room size amplitude, width: " + ROOM_WIDTH_AMPL + ", height: " + ROOM_HEIGHT_AMPL); writer.newLine();
        writer.write("room min distance: " + MIN_ROOM_DIST + ", amplitude: " + ROOM_DIST_AMPL); writer.newLine();
        writer.write("maze windiness: " + WINDINESS_PERCENT); writer.newLine();
        writer.write("max door count: " + MAX_DOOR_COUNT); writer.newLine();
        writer.write("door chance: " + DOOR_CHANCE); writer.newLine();
        writer.write("fill dead ends: " + FILL_DEADENDS); writer.newLine();
        writer.write("chest chance at dead ends: " + CHEST_CHANCE); writer.newLine();
        writer.write("enemy count: " + ENEMY_COUNT); writer.newLine();
        writer.write("chest count: " + CHEST_COUNT); writer.newLine();
    }
}
