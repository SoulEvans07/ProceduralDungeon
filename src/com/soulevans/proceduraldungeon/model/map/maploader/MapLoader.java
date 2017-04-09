package com.soulevans.proceduraldungeon.model.map.maploader;

import com.soulevans.proceduraldungeon.logger.LogType;
import com.soulevans.proceduraldungeon.logger.Logger;
import com.soulevans.proceduraldungeon.model.base.MPoint;
import com.soulevans.proceduraldungeon.model.base.VPoint;
import com.soulevans.proceduraldungeon.model.entities.items.Sword;
import com.soulevans.proceduraldungeon.model.entities.living.Enemy;
import com.soulevans.proceduraldungeon.model.entities.living.Player;
import com.soulevans.proceduraldungeon.model.map.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class MapLoader {
    public static final boolean LOG_GEN = false;
    public static final boolean STEP_BY_STEP_GEN = false;

    private static final String[] levels = {"level_one.map", "level_two.map"};

    public MapLoader(){

    }

    public static DungeonMap loadMap(int lvl, Player player){
        DungeonMap dungeon = null;
        Map<MPoint, Tile> map = new HashMap<>();

        String line;
        try (
                BufferedReader br = new BufferedReader(new InputStreamReader(MapLoader.class.getClassLoader().getResourceAsStream("maps/" + levels[lvl - 1])))
        ){
            ArrayList<String> stringMap = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                stringMap.add(line.toLowerCase());
            }

            dungeon = parseMap(stringMap, player);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dungeon;
    }

    public static DungeonMap loadEmpty(int _width, int _height){
        Map<MPoint, Tile> map = new HashMap<>();

        for(int y = 0; y < _height; y++){
            for(int x = 0; x < _width; x++){
                Tile tmp;
                tmp = new Floor(x, y);
                map.put(new MPoint(x, y), tmp);
            }
        }

        DungeonMap dungeon = new DungeonMap(map);
        dungeon.mapWidth = _width;
        dungeon.mapHeight = _height;

        return dungeon;
    }

    public static DungeonMap loadRandom(int lvl, Player player){
        DungeonMap dungeon = null;
        ArrayList<String> stringMap = new ArrayList<>();
        int width = 50;
        int height = 50;

        for(int y = 0; y < height; y++){
            String line = "";
            for(int x = 0; x < width; x++){
                line += "_";
            }
            stringMap.add(line);
        }

//        Room room = new Room(10, 10);
//        room.addDoor(9, 10);
//        room.addDoor(10, 5);
//        room.addEnemy(5, 5);
//        room.addEnemy(4, 3);
//        room.addChest(3, 3);
//        room.setOffset(3, 7);
//        room.placeRoom(stringMap);

        generateRooms(width, height, stringMap);


        replaceTile(1, 1, 'p', stringMap);
        return parseMap(stringMap, player);
    }

    private static void generateRooms(int mapWidth, int mapHeight, ArrayList<String> stringMap){
        ArrayList<Room> rooms = new ArrayList<>();
        ArrayList<String> shadowMap = new ArrayList<>(stringMap);
        int roomCount = 20;
        int minWidth = 3;
        int minHeight = 3;
        int widthRange = 4;
        int heightRange = 4;
        int minRoomDist = 1;
        int roomDistRange = 2;
        int centerX = (int)(mapWidth / 2.0);
        int centerY = (int)(mapHeight / 2.0);
        MPoint center = new MPoint(centerX, centerY);

        Random random = new Random();

        int w = minWidth + random.nextInt(widthRange);
        int h = minHeight + random.nextInt(heightRange);
        int dist = minRoomDist + random.nextInt(roomDistRange);
        Room room = new Room(w, h);
        room.setOffset(centerX - (w/2), centerY -(h/2));

        room.placeShadow(dist, shadowMap);
        if(LOG_GEN) {
            Logger.log("dist:"+dist);
            printMap(shadowMap);
        }

        rooms.add(room);

        for(int i = 0; i < roomCount-1; i++){
            w = minWidth + random.nextInt(widthRange);
            h = minHeight + random.nextInt(heightRange);
            dist = minRoomDist + random.nextInt(roomDistRange);
            room = new Room(w, h);

            // drop around the edge
            ArrayList<MPoint> drops = rotate(getDrop(mapWidth-w-1, mapHeight-h-1), i*((2*w+2*h)));
            for(MPoint drop : drops) {
                if(LOG_GEN) {
                    Logger.log("drop start: " + drop + " room: [" +w+", "+h+"]");
                }

                MPoint lastOff = new MPoint(drop);
                VPoint off = new VPoint(lastOff);
                room.setOffset((int) Math.floor(off.x), (int) Math.floor(off.y));

                // drop down
                while (room.checkCollision(shadowMap)) {
                    ArrayList<String> testMap = new ArrayList<>(shadowMap);
                    lastOff = new MPoint(off);

                    double d = off.dist(center);
                    if(d < 1) {
                        if(LOG_GEN) {
                            Logger.log(LogType.NOTICE, "no further then: " + off);
                        }
                        break;
                    }

                    off.x += (center.x - off.x) / d;
                    off.y += (center.y - off.y) / d;

                    room.setOffset((int) Math.floor(off.x), (int) Math.floor(off.y));
                    if(STEP_BY_STEP_GEN & LOG_GEN) {
                       room.placeShadow(dist, testMap);
                        printMap(testMap);
                    }

                }
                room.setOffset(lastOff.x, lastOff.y);

                // are we done?
                if (room.checkCollision(shadowMap)) {
                    if(LOG_GEN) {
                        Logger.log("done: " + room);
                    }
                    break;
                }
            }

            // final check
            if (room.checkCollision(shadowMap)) {
                room.placeShadow(dist, shadowMap);
                rooms.add(room);
            }
            if(LOG_GEN) {
                Logger.log("dist:" + dist);
                printMap(shadowMap);
            }
        }

        for(Room r : rooms){
            r.placeRoom(stringMap);
        }
    }

    private static ArrayList<MPoint> getDrop(int width, int height){
        ArrayList<MPoint> drop = new ArrayList<>();

        drop.add(new MPoint(0, 0));
        for (int i = 1; i < width; i++) {
            drop.add(new MPoint(i, 0));
        }

        drop.add(new MPoint(width, 0));
        for (int j = 1; j < height; j++){
            drop.add(new MPoint(width, j));
        }

        drop.add(new MPoint(width, height));
        for (int i = width-1; i > 0; i--) {
            drop.add(new MPoint(i, height));
        }

        drop.add(new MPoint(0, height));
        for (int j = height-1; j > 0 ; j--){
            drop.add(new MPoint(0, j));
        }

        return drop;
    }

    private static void printMap(ArrayList<String> map){
        String array = Arrays.toString(map.toArray());
        array = array.replaceAll(", ", ",\n");
        array = array.substring(1);
//        array = array.replace("]]]", "]]\n");
        Logger.log(array);
    }

    public static void replaceTile(int x, int y, char c, ArrayList<String> stringMap){
        if(y >= 0 && y < stringMap.size()) {
            String line = stringMap.get(y);
            if (x >= 0 && x + 1 < line.length())
                line = line.substring(0, x) + c + line.substring(x + 1);
            else if (x >= 0 && x < line.length())
                line = line.substring(0, x) + c;
            stringMap.set(y, line);
        }
    }

    public static <T> ArrayList<T> rotate(ArrayList<T> aL, int shift)
    {
        if (aL.size() == 0)
            return aL;

        T element = null;
        for(int i = 0; i < shift; i++) {
            // remove last element, add it to front of the ArrayList
            element = aL.remove( aL.size() - 1 );
            aL.add(0, element);
        }

        return aL;
    }

    private static DungeonMap parseMap(ArrayList<String> stringMap, Player player){
        DungeonMap dungeon = null;
        Map<MPoint, Tile> map = new HashMap<>();

        int maxX = 0;
        for(int y = 0; y < stringMap.size(); y++){
            String line = stringMap.get(y);
            if(maxX < line.length())
                maxX = line.length();

            for(int x = 0; x < line.length(); x++){
                char c = line.charAt(x);
                Tile tmp;

                switch (c){
                    case '#':
                        tmp = new Wall(x, y);
                        break;
                    case 'd':
                        tmp = new Door(x, y);
                        break;
                    case 'c':
                        tmp = new Chest(x, y, new Sword("Master Sword", 10000));
                        break;
                    default:
                        tmp = new Floor(x, y);
                        break;
                }
                map.put(new MPoint(x, y), tmp);
            }
        }

        dungeon = new DungeonMap(map);
        dungeon.mapWidth = maxX;
        dungeon.mapHeight = stringMap.size();

        for(int y = 0; y < stringMap.size(); y++) {
            String line = stringMap.get(y);
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                if(c == 'e') {
                    Enemy enemy = new Enemy(null, 600);
                    dungeon.addGameObject(x, y, enemy);
                }
                if(c == 'p')
                    dungeon.addGameObject(x,y, player);
            }
        }

        return dungeon;
    }
}
