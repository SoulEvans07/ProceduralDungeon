package com.soulevans.proceduraldungeon.model.map.maploader;

import com.soulevans.proceduraldungeon.Game;
import com.soulevans.proceduraldungeon.logger.LogType;
import com.soulevans.proceduraldungeon.logger.Logger;
import com.soulevans.proceduraldungeon.model.base.Dir;
import com.soulevans.proceduraldungeon.model.base.MPoint;
import com.soulevans.proceduraldungeon.model.base.PerlinNoise;
import com.soulevans.proceduraldungeon.model.base.VPoint;
import com.soulevans.proceduraldungeon.model.entities.items.Sword;
import com.soulevans.proceduraldungeon.model.entities.living.Enemy;
import com.soulevans.proceduraldungeon.model.entities.living.Player;
import com.soulevans.proceduraldungeon.model.map.*;
import javafx.scene.canvas.GraphicsContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class MapLoader {
    public static final boolean LOG_GEN = false;
    public static final boolean STEP_BY_STEP_GEN = false;
    private static PerlinNoise noise = new PerlinNoise();
    private static float xo, yo, zo;
    public static HashMap<MPoint, Double> noiseSpace = new HashMap<>();
    public static int width = 49;
    public static int height = 49;

    private static final String[] levels = {"level_one.map", "level_two.map"};

    public MapLoader() {

    }

    public static DungeonMap loadMap(int lvl, Player player) {
        DungeonMap dungeon = null;
        Map<MPoint, Tile> map = new HashMap<>();

        String line;
        try (
                BufferedReader br = new BufferedReader(new InputStreamReader(MapLoader.class.getClassLoader().getResourceAsStream("maps/" + levels[lvl - 1])))
        ) {
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

    public static DungeonMap loadEmpty(int _width, int _height) {
        Map<MPoint, Tile> map = new HashMap<>();

        for (int y = 0; y < _height; y++) {
            for (int x = 0; x < _width; x++) {
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

    public static DungeonMap loadRandom(int lvl, Player player) {
        DungeonMap dungeon = null;
        ArrayList<String> stringMap = new ArrayList<>();

//        resetNoise(width * Game.TILESIZE, height * Game.TILESIZE);
        for (int y = 0; y < height; y++) {
            String line = "";
            for (int x = 0; x < width; x++) {
                line += "#";
            }
            stringMap.add(line);
        }

//        fillNoiseSpace(width, height, Game.getInstance().canvas.getGraphicsContext2D());
        generateRooms(width, height, stringMap);
        for (int y = 1; y < height; y += 2) {
            for (int x = 1; x < width; x += 2) {
                MPoint pos = new MPoint(x, y);
                if (getTile(pos.x, pos.y, stringMap) != '#') continue;
                generateMaze(pos, stringMap);
            }
        }

        replaceTile(1, 1, 'p', stringMap);
        return parseMap(stringMap, player);
    }

    private static void generateMaze(MPoint start, ArrayList<String> stringMap){
        char EMPTY = '.';
        int windinessPercent = 70;

        Random random = new Random();
        ArrayList<MPoint> cells = new ArrayList<>();
        Dir lastDir = null;

        replaceTile(start.x, start.y, EMPTY, stringMap);
        cells.add(start);

        while(!cells.isEmpty()){
            MPoint cell = cells.get(cells.size()-1);    // cells.last
            ArrayList<Dir> dirs = new ArrayList<>();

            for(Dir dir : Dir.values()){
                if(shouldCarve(cell, dir, stringMap))
                    dirs.add(dir);
            }

            if(dirs.size() > 0){
                Dir dir;
                if(dirs.contains(lastDir) && random.nextInt(100) > windinessPercent)
                    dir = lastDir;
                else
                    dir = dirs.get(random.nextInt(dirs.size()));

                MPoint one = cell.add(dir.value);
                MPoint oneplus = cell.add(dir.mult(2));
                replaceTile(one.x, one.y, EMPTY, stringMap);
                replaceTile(oneplus.x, oneplus.y, EMPTY, stringMap);

                cells.add(oneplus);
                lastDir = dir;
            } else {
                cells.remove(cells.size() - 1 );    // cells.removeLast
                lastDir = null;
            }
        }

    }

    private static boolean shouldCarve(MPoint pos, Dir dir, ArrayList<String> stringMap){
        MPoint one = pos.add(dir.value);
        MPoint next = pos.add(dir.mult(2));
        MPoint over = pos.add(dir.mult(3));

        return getTile(over.x, over.y, stringMap) != '?' && getTile(next.x, next.y, stringMap) == '#'
                && getTile(one.x, one.y, stringMap) == '#';
    }

    public static char getTile(int x, int y, ArrayList<String> stringMap){
        if(y >= stringMap.size() || y < 0)
            return '?';

        String line = stringMap.get(y);
        if(x >= line.length() || x < 0)
            return '?';

        return line.charAt(x);
    }

    private static void generateRooms(int mapWidth, int mapHeight, ArrayList<String> stringMap) {
        ArrayList<Room> rooms = new ArrayList<>();
        ArrayList<String> shadowMap = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            String line = "";
            for (int x = 0; x < width; x++) {
                line += "_";
            }
            shadowMap.add(line);
        }

        int roomAttempt = 50;
        int minWidth = 3;
        int minHeight = 3;
        int widthRange = 10;
        int heightRange = 10;
        int minRoomDist = 1;
        int roomDistRange = 2;
        int maxDoorCount = 4;
        int centerX = (int) (mapWidth / 2.0);
        int centerY = (int) (mapHeight / 2.0);
        MPoint center = new MPoint(centerX, centerY);

        Random random = new Random();

        int w = minWidth + random.nextInt(widthRange);
        w = w % 2 == 1 ? w+1 : w;
        int h = minHeight + random.nextInt(heightRange);
        h = h % 2 == 1 ? h+1 : h;
        int dist = minRoomDist + random.nextInt(roomDistRange);
        Room room = new Room(w, h);
        int offX = centerX - (w / 2);
        int offY = centerY - (h / 2);
        if(offX % 2 == 1) offX--;
        if(offY % 2 == 1) offY--;
        room.setOffset(offX, offY);

        room.placeShadow(dist, shadowMap);
        if (LOG_GEN) {
            Logger.log("dist:" + dist);
            printMap(shadowMap);
        }

        rooms.add(room);

        for (int i = 0; i < roomAttempt - 1; i++) {
            w = minWidth + random.nextInt(widthRange);
            w = w % 2 == 1 ? w+1 : w;
            h = minHeight + random.nextInt(heightRange);
            h = h % 2 == 1 ? h+1 : h;
            dist = minRoomDist + random.nextInt(roomDistRange);
            room = new Room(w, h);

            // drop around the edge
            int shift = i * ((2 * w + 2 * h));
            shift = shift % 2 == 0 ? shift+1 : shift;
            ArrayList<MPoint> drops = rotate(getDrop(mapWidth - w - 1, mapHeight - h - 1), shift);
            for (MPoint drop : drops) {
                if (LOG_GEN) {
                    Logger.log("drop start: " + drop + " room: [" + w + ", " + h + "]");
                }

                MPoint lastOff = new MPoint(drop);
                VPoint off = new VPoint(lastOff);
                room.setOffset((int) Math.floor(off.x), (int) Math.floor(off.y));

                // drop down
                while (room.checkCollision(shadowMap)) {
                    ArrayList<String> testMap = new ArrayList<>(shadowMap);
                    lastOff = new MPoint(off);

                    double d = off.dist(center);
                    if (d < 1) {
                        if (LOG_GEN) {
                            Logger.log(LogType.NOTICE, "no further then: " + off);
                        }
                        break;
                    }

                    off.x += (center.x - off.x) / d;
                    off.x = (int) Math.round(off.x);
                    off.x = off.x % 2 == 1 ? (off.x + (off.x < center.x ? 1 : -1)) : off.x;

                    off.y += (center.y - off.y) / d;
                    off.y = (int) Math.round(off.y);
                    off.y = off.y % 2 == 1 ? (off.y + (off.y < center.y ? 1 : -1)) : off.y;

                    room.setOffset((int) off.x, (int) off.y);
                    if (STEP_BY_STEP_GEN & LOG_GEN) {
                        room.placeShadow(dist, testMap);
                        printMap(testMap);
                    }

                }
                room.setOffset(lastOff.x, lastOff.y);

                // are we done?
                if (room.checkCollision(shadowMap)) {
                    if (LOG_GEN) {
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
            if (LOG_GEN) {
                Logger.log("dist:" + dist);
                printMap(shadowMap);
            }
        }

        for (Room r : rooms) {
            r.placeRoom(stringMap);
            r.placeGray(noiseSpace);
        }
    }

    private static int getDoorNum(int max){
        int doors = 1;
        Random random = new Random();
        for(int i = 1; i < max; i++){
            int r = random.nextInt(100);
            if((100-r) > 25){
                doors++;
            }
        }
        return doors;
    }

    public static void offsetNoise(float x, float y, float z) {
        xo = x;
        yo = y;
        zo = z;
    }

    public static void fillNoiseSpace(int mapWidth, int mapHeight, GraphicsContext gc){
        double scale = 0.01;
        for(int y = 0; y < (mapHeight*Game.TILESIZE); y+=Game.TILESIZE){
            for(int x = 0; x < mapWidth*Game.TILESIZE; x+=Game.TILESIZE){
                MPoint temp = new MPoint((int)(x / Game.TILESIZE),(int) (y / Game.TILESIZE));
                double gray = (1 + noise.noise((float) (x*scale)+xo, (float) (y*scale)+yo, 0)) / 2;
                noiseSpace.put(temp, gray);
            }
        }
    }

    private static ArrayList<MPoint> getDrop(int width, int height) {
        ArrayList<MPoint> drop = new ArrayList<>();

        int i;
        for (i = 0; i < width; i+=2) {
            drop.add(new MPoint(i, 0));
        }

        int j;
        for (j = 2; j < height; j+=2) {
            drop.add(new MPoint(i, j));
        }

        for (i = i - 2; i > 0; i-=2) {
            drop.add(new MPoint(i, j));
        }

        for (j = j - 2; j > 0; j-=2) {
            drop.add(new MPoint(0, j));
        }

        return drop;
    }

    private static void printMap(ArrayList<String> map) {
        String array = Arrays.toString(map.toArray());
        array = array.replaceAll(", ", ",\n");
        array = array.substring(1);
        Logger.log(array);
    }

    public static void resetNoise(double width, double height){
        Random random = new Random();
        double offx = random.nextDouble() * width;
        double offy = random.nextDouble() * height;
        offsetNoise((float)offx, (float) offy, 0);
    }

    public static void replaceTile(int x, int y, char c, ArrayList<String> stringMap) {
        if (y >= 0 && y < stringMap.size()) {
            String line = stringMap.get(y);
            if (x >= 0 && x + 1 < line.length())
                line = line.substring(0, x) + c + line.substring(x + 1);
            else if (x >= 0 && x < line.length())
                line = line.substring(0, x) + c;
            stringMap.set(y, line);
        }
    }

    public static <T> ArrayList<T> rotate(ArrayList<T> aL, int shift) {
        if (aL.size() == 0)
            return aL;

        T element = null;
        for (int i = 0; i < shift; i++) {
            // remove last element, add it to front of the ArrayList
            element = aL.remove(aL.size() - 1);
            aL.add(0, element);
        }

        return aL;
    }

    private static DungeonMap parseMap(ArrayList<String> stringMap, Player player) {
        DungeonMap dungeon = null;
        Map<MPoint, Tile> map = new HashMap<>();

        int maxX = 0;
        for (int y = 0; y < stringMap.size(); y++) {
            String line = stringMap.get(y);
            if (maxX < line.length())
                maxX = line.length();

            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Tile tmp;

                switch (c) {
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

        for (int y = 0; y < stringMap.size(); y++) {
            String line = stringMap.get(y);
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                if (c == 'e') {
                    Enemy enemy = new Enemy(null, 600);
                    dungeon.addGameObject(x, y, enemy);
                }
                if (c == 'p')
                    dungeon.addGameObject(x, y, player);
            }
        }

        return dungeon;
    }
}
