package com.soulevans.proceduraldungeon.config;

public class MapConfig {
    public static final double TILESIZE = 16;
    public static final int MAP_WIDTH = 37;
    public static final int MAP_HEIGHT = 37;

    public static final int ENEMY_COUNT = 10;
    public static final int CHEST_COUNT = 10;

    public static final int FILL_DEADENDS = MAP_WIDTH *2;
    public static final double CHEST_CHANCE = 60;

    public static int DOOR_CHANCE = 20;
    public static int MAX_DOOR_COUNT = 3;
    public static int WINDINESS_PERCENT = 70;
    public static int ROOM_ATTEMPTS = MAP_WIDTH;
    public static int MIN_ROOM_WIDTH = 3;
    public static int MIN_ROOM_HEIGHT = 3;
    public static int ROOM_WIDTH_AMPL = 5;
    public static int ROOM_HEIGHT_AMPL = 5;
    public static int MIN_ROOM_DIST = 1;
    public static int ROOM_DIST_AMPL = 2;

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
}
