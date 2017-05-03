package com.soulevans.proceduraldungeon.model.map.maploader;

import com.soulevans.proceduraldungeon.Game;
import com.soulevans.proceduraldungeon.config.LogConfig;
import com.soulevans.proceduraldungeon.config.MapConfig;
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
    private static final boolean LOG_GEN = LogConfig.LOG_GEN;
    private static final boolean STEP_BY_STEP_GEN = LogConfig.STEP_BY_STEP_GEN;

    public static int width = MapConfig.MAP_WIDTH;
    public static int height = MapConfig.MAP_HEIGHT;

    private static int ENEMY_COUNT = MapConfig.ENEMY_COUNT;
    private static int CHEST_COUNT = MapConfig.CHEST_COUNT;
    private static int FILL_DEADENDS = MapConfig.FILL_DEADENDS;
    private static double CHEST_CHANCE = MapConfig.CHEST_CHANCE;
    private static int DOOR_CHANCE = MapConfig.DOOR_CHANCE;
    private static int maxDoorCount = MapConfig.MAX_DOOR_COUNT;
    private static int windinessPercent = MapConfig.WINDINESS_PERCENT;
    private static int roomAttempt = MapConfig.ROOM_ATTEMPTS;
    private static int minWidth = MapConfig.MIN_ROOM_WIDTH;
    private static int minHeight = MapConfig.MIN_ROOM_HEIGHT;
    private static int widthRange = MapConfig.ROOM_WIDTH_AMPL;
    private static int heightRange = MapConfig.ROOM_HEIGHT_AMPL;
    private static int minRoomDist = MapConfig.MIN_ROOM_DIST;
    private static int roomDistRange = MapConfig.ROOM_DIST_AMPL;

    private static final char WALL_CHAR = MapConfig.WALL_CHAR;
    private static final char DOOR_CHAR = MapConfig.DOOR_CHAR;
    private static final char CHEST_CHAR = MapConfig.CHEST_CHAR;
    private static final char ENTRY_CHAR = MapConfig.ENTRY_CHAR;
    private static final char EXIT_CHAR = MapConfig.EXIT_CHAR;
    private static final char FLOOR_CHAR = MapConfig.FLOOR_CHAR;
    private static final char ENEMY_CHAR = MapConfig.ENEMY_CHAR;
    private static final char PLAYER_CHAR = MapConfig.PLAYER_CHAR;
    private static final char OUT_OF_BOUNDS_CHAR = MapConfig.OUT_OF_BOUNDS_CHAR;

    private static boolean GENERATE_MIDDLE_ROOM;

    private static Random random = new Random();
    private static PerlinNoise noise = new PerlinNoise();
    private static float xo, yo, zo;

    public static HashMap<MPoint, Double> noiseSpace = new HashMap<>();

    private static final String[] levels = {"level_one", "level_two"};

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

    public static DungeonMap startFromMap(int lvl, Player player){
        DungeonMap dungeon = null;
        ArrayList<String> stringMap = new ArrayList<>();
        ArrayList<String> shadowMap = new ArrayList<>();
        ArrayList<String> mask = new ArrayList<>();

        String line;
        try (
            BufferedReader br_map = new BufferedReader(new InputStreamReader(MapLoader.class.getClassLoader().getResourceAsStream("maps/" + levels[lvl - 1] + ".map")));
            BufferedReader br_sha = new BufferedReader(new InputStreamReader(MapLoader.class.getClassLoader().getResourceAsStream("maps/" + levels[lvl - 1] + ".shadow")))
        ) {
            while ((line = br_map.readLine()) != null) {
                stringMap.add(line.toLowerCase());
            }
            while ((line = br_sha.readLine()) != null) {
                shadowMap.add(line.toLowerCase());
                mask.add(line.toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        width = stringMap.get(0).length();
        height = stringMap.size();

        resetNoise(width * Game.TILESIZE, height * Game.TILESIZE);
        fillNoiseSpace(width, height, Game.getInstance().canvas.getGraphicsContext2D());

        GENERATE_MIDDLE_ROOM = false;
        ArrayList<Room> rooms = generateRooms(width, height, stringMap, shadowMap);
//        printMap(shadowMap);

        ArrayList<ArrayList<MPoint>> mazes = new ArrayList<>();
        for (int y = 1; y < height; y += 2) {
            for (int x = 1; x < width; x += 2) {
                MPoint pos = new MPoint(x, y);
                if (getTile(pos.x, pos.y, stringMap) != WALL_CHAR) continue;
                if (getTile(pos.x, pos.y, shadowMap) != FLOOR_CHAR) continue;
                mazes.add(generateMaze(pos, stringMap, mask));
            }
        }

        placeDoors(mazes, rooms, stringMap);
        fillDeadEnds(mazes, stringMap);
        generateStaircase(rooms, stringMap);

        ArrayList<MPoint> free = new ArrayList<>();
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                MPoint tmp = new MPoint(x, y);
                if(stringMap.get(y).charAt(x) != FLOOR_CHAR) {
                    noiseSpace.replace(tmp, Double.MAX_VALUE);
                } else {
                    free.add(tmp);
                }
            }
        }

        placeEnemysAndChests(free, stringMap);
        //printMap(stringMap);
        return parseMap(stringMap, player);
    }

    public static DungeonMap loadRandom(int lvl, Player player) {
        DungeonMap dungeon = null;
        ArrayList<String> stringMap = new ArrayList<>();

        resetNoise(width * Game.TILESIZE, height * Game.TILESIZE);
        for (int y = 0; y < height; y++) {
            String line = "";
            for (int x = 0; x < width; x++) {
                line += WALL_CHAR;
            }
            stringMap.add(line);
        }

        fillNoiseSpace(width, height, Game.getInstance().canvas.getGraphicsContext2D());
        GENERATE_MIDDLE_ROOM = true;
        ArrayList<Room> rooms = generateRooms(width, height, stringMap);
        ArrayList<ArrayList<MPoint>> mazes = new ArrayList<>();
        for (int y = 1; y < height; y += 2) {
            for (int x = 1; x < width; x += 2) {
                MPoint pos = new MPoint(x, y);
                if (getTile(pos.x, pos.y, stringMap) != WALL_CHAR) continue;
                mazes.add(generateMaze(pos, stringMap));
            }
        }
        placeDoors(mazes, rooms, stringMap);
        fillDeadEnds(mazes, stringMap);
        generateStaircase(rooms, stringMap);

        //replaceTile(1, 1, 'p', stringMap);

        ArrayList<MPoint> free = new ArrayList<>();
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                MPoint tmp = new MPoint(x, y);
                if(stringMap.get(y).charAt(x) != FLOOR_CHAR) {
                    noiseSpace.replace(tmp, Double.MAX_VALUE);
                } else {
                    free.add(tmp);
                }
            }
        }

        placeEnemysAndChests(free, stringMap);
        //printMap(stringMap);
        return parseMap(stringMap, player);
    }

    private static void placeEnemysAndChests(ArrayList<MPoint> free, ArrayList<String> stringMap){
        for(int i = 0; i < ENEMY_COUNT; i++){
            int pos = random.nextInt(free.size());
            MPoint e = free.get(pos);
            double val = noiseSpace.get(e);
            replaceTile(e.x, e.y, ENEMY_CHAR, stringMap);
            free.remove(pos);
        }

        for(int i = 0; i < CHEST_COUNT; i++){
            int pos = random.nextInt(free.size());
            MPoint c = free.get(pos);
            double val = noiseSpace.get(c);
            replaceTile(c.x, c.y, CHEST_CHAR, stringMap);
            free.remove(pos);
        }
    }

    private static void generateStaircase(ArrayList<Room> rooms, ArrayList<String> stringMap){
        ArrayList<Room> tmpRoomList = new ArrayList<>(rooms);

        // get entry & exit room
        int entryRoomNum = random.nextInt(tmpRoomList.size());
        Room entryRoom = tmpRoomList.remove(entryRoomNum);
        int exitRoomNum = random.nextInt(tmpRoomList.size());
        Room exitRoom = tmpRoomList.remove(exitRoomNum);

        // place staircase in room
        int x = entryRoom.offsetX + (int) Math.floor(entryRoom.width / 2.0);
        int y = entryRoom.offsetY + (int) Math.floor(entryRoom.height / 2.0);
        replaceTile(x, y, ENTRY_CHAR, stringMap);
        replaceTile(x + 1, y, PLAYER_CHAR, stringMap);

        x = exitRoom.offsetX + (int) Math.floor(exitRoom.width / 2.0);
        y = exitRoom.offsetY + (int) Math.floor(exitRoom.height / 2.0);
        replaceTile(x, y, EXIT_CHAR, stringMap);
    }

    private static void fillDeadEnds(ArrayList<ArrayList<MPoint>> mazes, ArrayList<String> stringMap){
        // unite all maze lists
        ArrayList<MPoint> mazeCells = new ArrayList<>();
        for(ArrayList<MPoint> maze : mazes){
            mazeCells.addAll(maze);
        }

        ArrayList<MPoint> deadEnds = new ArrayList<>();
        for(int i = 0; i < FILL_DEADENDS; i++) {
            deadEnds.clear();
            // search for dead ends
            for (MPoint cell : mazeCells) {
                int wallCount = 0;
                for (Dir dir : Dir.values()) {
                    MPoint neighbour = cell.add(dir.value);
                    if(neighbour.y < stringMap.size() && neighbour.x < stringMap.get(neighbour.y).length())
                        if (stringMap.get(neighbour.y).charAt(neighbour.x) == WALL_CHAR)
                            wallCount++;
                }

                if (wallCount >= 3) {
                    deadEnds.add(cell);
                }
            }

            // fill up random dead end
            if(deadEnds.size() > 0) {
                MPoint chosen = deadEnds.get(random.nextInt(deadEnds.size()));
                replaceTile(chosen.x, chosen.y, WALL_CHAR, stringMap);
                mazeCells.remove(chosen); // TODO: remove from containing maze as well
                deadEnds.remove(chosen);
            }
        }

        // aboid negative possibility spaces
        for(MPoint ch : deadEnds){
            if(random.nextInt(100) < CHEST_CHANCE)
                replaceTile(ch.x, ch.y, CHEST_CHAR, stringMap);
        }
    }

    private static void placeDoors(ArrayList<ArrayList<MPoint>> mazes, ArrayList<Room> rooms, ArrayList<String> stringMap){
        // populate connections list
        ArrayList<MPoint> connectors = new ArrayList<>();
        for(ArrayList<MPoint> maze : mazes){
            for(MPoint cell : maze){
                ArrayList<Dir> dirs = new ArrayList<>();

                for(Dir dir : Dir.values()){
                    MPoint one = cell.add(dir.value);
                    Room room = isRoomWall(one, rooms);

                    if(room != null) {
                        connectors.add(one);
                    }
                }
            }
        }

        // go through rooms
        for(Room room : rooms){
            // get relevant connections for room
            ArrayList<MPoint> conn = roomConnectors(connectors, room);
            int index = random.nextInt(conn.size());
            MPoint open = conn.get(index);
            removeNextDoors(index, conn, connectors);

            connectors.remove(open);
            conn.remove(open);
            room.addRelativeDoor(open.x - room.offsetX, open.y - room.offsetY);
            // place more doors randomly to achive a non-perfect maze
            if(conn.size() > 0) {
                for (int i = 1; i < maxDoorCount; i++) {
                    if (random.nextInt(100) < DOOR_CHANCE && conn.size() > 0) {
                        int id = random.nextInt(conn.size());
                        MPoint other = conn.get(id);
                        removeNextDoors(id, conn, connectors);

                        connectors.remove(other);
                        conn.remove(other);
                        room.addRelativeDoor(other.x - room.offsetX, other.y - room.offsetY);
                    }
                }
            }
            connectors.removeAll(conn);
        }

        for (Room room : rooms) {
            room.placeRoom(stringMap);
        }
    }

    public static void removeNextDoors(int index, ArrayList<MPoint> conn, ArrayList<MPoint> connectors){
        MPoint rem = null;
        if (index - 1 < 0) {
            rem = conn.remove(conn.size() - 1);
        } else {
            rem = conn.remove(index - 1);
        }
        connectors.remove(rem);

        if(conn.size() > 0) {
            if (index + 1 >= conn.size()) {
                rem = conn.remove(0);
            } else {
                rem = conn.remove(index + 1);
            }
            connectors.remove(rem);
        }
    }

    private static ArrayList<MPoint> roomConnectors(ArrayList<MPoint> connectors, Room room){
        ArrayList<MPoint> subList = new ArrayList<>();
        MPoint offset = new MPoint(room.offsetX, room.offsetY);
        for(MPoint wall : room.walls){
            MPoint tmp = wall.add(offset);
            if (connectors.contains(tmp))
                subList.add(tmp);
        }

        return subList;
    }

    private static Room isRoomWall(MPoint cell, ArrayList<Room> rooms){
        for(Room r : rooms){
            if(r.containsAbsoluteWall(cell)) {
                return r;
            }
        }
        return null;
    }

    private static ArrayList<MPoint> generateMaze(MPoint start, ArrayList<String> stringMap, ArrayList<String> mask){
        ArrayList<MPoint> maze = new ArrayList<>();
        ArrayList<MPoint> open = new ArrayList<>();
        Dir lastDir = null;

        // carve start
        replaceTile(start.x, start.y, FLOOR_CHAR, stringMap);
        open.add(start);
        maze.add(start);

        while(!open.isEmpty()){
            // get last open node
            MPoint node = open.get(open.size()-1);
            ArrayList<Dir> dirs = new ArrayList<>();

            // search for viable directions
            for(Dir dir : Dir.values()){
                if(shouldCarve(node, dir, stringMap, mask))
                    dirs.add(dir);
            }

            if(dirs.size() > 0){
                Dir dir;
                // chose direction
                if(dirs.contains(lastDir) && random.nextInt(100) > windinessPercent)
                    dir = lastDir;
                else
                    dir = dirs.get(random.nextInt(dirs.size()));

                MPoint next = node.add(dir.value);
                MPoint secondnext = node.add(dir.mult(2));

                // carve node next and second next from it
                replaceTile(next.x, next.y, FLOOR_CHAR, stringMap);
                replaceTile(secondnext.x, secondnext.y, FLOOR_CHAR, stringMap);
                maze.add(next);
                maze.add(secondnext);

                // prepare for next cycle
                open.add(secondnext);
                lastDir = dir;
            } else {
                // remove node with no viable neighbour
                open.remove(node);
                lastDir = null;
            }
        }

        return maze;
    }

    private static ArrayList<MPoint> generateMaze(MPoint start, ArrayList<String> stringMap){
        ArrayList<MPoint> maze = new ArrayList<>();
        ArrayList<MPoint> open = new ArrayList<>();
        Dir lastDir = null;

        // carve start
        replaceTile(start.x, start.y, FLOOR_CHAR, stringMap);
        open.add(start);
        maze.add(start);

        while(!open.isEmpty()){
            // get last open node
            MPoint node = open.get(open.size()-1);
            ArrayList<Dir> dirs = new ArrayList<>();

            // search for viable directions
            for(Dir dir : Dir.values()){
                if(shouldCarve(node, dir, stringMap))
                    dirs.add(dir);
            }

            if(dirs.size() > 0){
                Dir dir;
                // chose direction
                if(dirs.contains(lastDir) && random.nextInt(100) > windinessPercent)
                    dir = lastDir;
                else
                    dir = dirs.get(random.nextInt(dirs.size()));

                MPoint next = node.add(dir.value);
                MPoint secondnext = node.add(dir.mult(2));

                // carve node next and second next from it
                replaceTile(next.x, next.y, FLOOR_CHAR, stringMap);
                replaceTile(secondnext.x, secondnext.y, FLOOR_CHAR, stringMap);
                maze.add(next);
                maze.add(secondnext);

                // prepare for next cycle
                open.add(secondnext);
                lastDir = dir;
            } else {
                // remove node with no viable neighbour
                open.remove(node);
                lastDir = null;
            }
        }

        return maze;
    }

    private static boolean shouldCarve(MPoint pos, Dir dir, ArrayList<String> stringMap, ArrayList<String> mask){
        MPoint one = pos.add(dir.value);
        MPoint next = pos.add(dir.mult(2));
        MPoint over = pos.add(dir.mult(3));

        return getTile(over.x, over.y, stringMap) != OUT_OF_BOUNDS_CHAR && getTile(next.x, next.y, stringMap) == WALL_CHAR &&
                getTile(one.x, one.y, stringMap) == WALL_CHAR && getTile(next.x, next.y, mask) == FLOOR_CHAR;
    }

    private static boolean shouldCarve(MPoint pos, Dir dir, ArrayList<String> stringMap){
        MPoint one = pos.add(dir.value);
        MPoint next = pos.add(dir.mult(2));
        MPoint over = pos.add(dir.mult(3));

        return getTile(over.x, over.y, stringMap) != OUT_OF_BOUNDS_CHAR && getTile(next.x, next.y, stringMap) == WALL_CHAR &&
                getTile(one.x, one.y, stringMap) == WALL_CHAR;
    }

    public static char getTile(int x, int y, ArrayList<String> stringMap){
        if(y >= stringMap.size() || y < 0)
            return OUT_OF_BOUNDS_CHAR;

        String line = stringMap.get(y);
        if(x >= line.length() || x < 0)
            return OUT_OF_BOUNDS_CHAR;

        return line.charAt(x);
    }

    private static ArrayList<Room> generateRooms(int mapWidth, int mapHeight, ArrayList<String> stringMap) {
        ArrayList<String> shadowMap = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            String line = "";
            for (int x = 0; x < width; x++) {
                line += FLOOR_CHAR;
            }
            shadowMap.add(line);
        }
        return generateRooms(mapWidth, mapHeight, stringMap, shadowMap);
    }

    private static Room placeMiddleRoom(int mapWidth, int mapHeight, ArrayList<String> stringMap, ArrayList<String> shadowMap){
        int centerX = (int) (mapWidth / 2.0);
        int centerY = (int) (mapHeight / 2.0);
        MPoint center = new MPoint(centerX, centerY);

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

        return room;
    }

    private static ArrayList<Room> generateRooms(int mapWidth, int mapHeight, ArrayList<String> stringMap, ArrayList<String> shadowMap) {
        ArrayList<Room> rooms = new ArrayList<>();

        int centerX = (int) (mapWidth / 2.0);
        int centerY = (int) (mapHeight / 2.0);
        MPoint center = new MPoint(centerX, centerY);

        if(GENERATE_MIDDLE_ROOM)
            rooms.add(placeMiddleRoom(mapWidth, mapHeight, stringMap, shadowMap));

        Room room;
        int w;
        int h;
        int dist;
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
        }
        return rooms;
    }

    private static int getDoorNum(int max){
        int doors = 1;
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
                    case WALL_CHAR:
                        tmp = new Wall(x, y);
                        break;
                    case DOOR_CHAR:
                        tmp = new Door(x, y);
                        break;
                    case CHEST_CHAR:
                        tmp = new Chest(x, y, new Sword("Master Sword", 10000));
                        break;
                    case ENTRY_CHAR:
                        tmp = new EntryStair(x, y);
                        break;
                    case EXIT_CHAR:
                        tmp = new ExitStair(x, y);
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
                if (c == ENEMY_CHAR) {
                    Enemy enemy = new Enemy(null, 600);
                    dungeon.addGameObject(x, y, enemy);
                }
                if (c == PLAYER_CHAR)
                    dungeon.addGameObject(x, y, player);
            }
        }

        return dungeon;
    }
}
