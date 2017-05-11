package com.soulevans.proceduraldungeon.model.map.maploader;

import com.soulevans.proceduraldungeon.Game;
import com.soulevans.proceduraldungeon.config.MapConfig;
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


// TODO: group methods together by generation step

public class MapLoader {
    private static boolean GENERATE_MIDDLE_ROOM;

    private static Random random = new Random();
    private static PerlinNoise noise = new PerlinNoise();
    private static float xo, yo, zo;

    public static ArrayList<String> _stringMap;
    public static ArrayList<ArrayList<MPoint>>  _mazes;
    public static ArrayList<MPoint> _mazeCells;

    public static HashMap<MPoint, Double> noiseSpace = new HashMap<>();

    // TODO: [LATER] change to "levelZZZ" format
    private static final String[] levels = {"level_one", "level_two", "level_03"};


    public static DungeonMap loadMap(int lvl, Player player) {
        DungeonMap dungeon = null;
        Map<MPoint, Tile> map = new HashMap<>();

        String line;
        try (
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            MapLoader.class.getClassLoader().getResourceAsStream("maps/" + levels[lvl - 1] + ".map")))
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

    // TODO: refact .startFromMap(..)
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
        MapConfig.MAP_WIDTH = stringMap.get(0).length();
        MapConfig.MAP_HEIGHT = stringMap.size();

        resetNoise(MapConfig.MAP_WIDTH * Game.TILESIZE, MapConfig.MAP_HEIGHT * Game.TILESIZE);
        fillNoiseSpace(MapConfig.MAP_WIDTH, MapConfig.MAP_HEIGHT, Game.getInstance().canvas.getGraphicsContext2D());

        GENERATE_MIDDLE_ROOM = false;
        ArrayList<Room> rooms = generateRooms(MapConfig.MAP_WIDTH, MapConfig.MAP_HEIGHT, stringMap, shadowMap);

        ArrayList<ArrayList<MPoint>> mazes = new ArrayList<>();
        for (int y = 1; y < MapConfig.MAP_HEIGHT; y += 2) {
            for (int x = 1; x < MapConfig.MAP_WIDTH; x += 2) {
                MPoint pos = new MPoint(x, y);
                if (getTile(pos.x, pos.y, stringMap) != MapConfig.WALL_CHAR) continue;
                if (getTile(pos.x, pos.y, shadowMap) != MapConfig.FLOOR_CHAR) continue;
                mazes.add(generateMaze(pos, stringMap, mask));
            }
        }

        placeDoors(mazes, rooms, stringMap);
        fillDeadEnds(mazes, stringMap);
        generateStaircase(rooms, stringMap);

        ArrayList<MPoint> free = new ArrayList<>();
        for(int y = 0; y < MapConfig.MAP_HEIGHT; y++){
            for(int x = 0; x < MapConfig.MAP_WIDTH; x++){
                MPoint tmp = new MPoint(x, y);
                if(stringMap.get(y).charAt(x) != MapConfig.FLOOR_CHAR) {
                    noiseSpace.replace(tmp, Double.MAX_VALUE);
                } else {
                    free.add(tmp);
                }
            }
        }

        placeEnemysAndChests(free, stringMap);
        _stringMap = new ArrayList<String>(stringMap);
        _mazes = new ArrayList<ArrayList<MPoint>>(mazes);
        _mazeCells = new ArrayList<>();
        for(ArrayList<MPoint> maze : MapLoader._mazes){
            _mazeCells.addAll(maze);
        }
        return parseMap(stringMap, player);
    }

    public static DungeonMap loadRandom(int lvl, Player player) {
        DungeonMap dungeon = null;
        ArrayList<String> stringMap = new ArrayList<>();

        resetNoise(MapConfig.MAP_WIDTH * Game.TILESIZE, MapConfig.MAP_HEIGHT * Game.TILESIZE);
        fillNoiseSpace(MapConfig.MAP_WIDTH, MapConfig.MAP_HEIGHT, Game.getInstance().canvas.getGraphicsContext2D());
        for (int y = 0; y < MapConfig.MAP_HEIGHT; y++) {
            String line = "";
            for (int x = 0; x < MapConfig.MAP_WIDTH; x++) {
                line += MapConfig.WALL_CHAR;
            }
            stringMap.add(line);
        }

        GENERATE_MIDDLE_ROOM = true;
        ArrayList<Room> rooms = generateRooms(MapConfig.MAP_WIDTH, MapConfig.MAP_HEIGHT, stringMap);
        ArrayList<ArrayList<MPoint>> mazes = new ArrayList<>();
        for (int y = 1; y < MapConfig.MAP_HEIGHT; y += 2) {
            for (int x = 1; x < MapConfig.MAP_WIDTH; x += 2) {
                MPoint pos = new MPoint(x, y);
                if (getTile(pos.x, pos.y, stringMap) != MapConfig.WALL_CHAR) continue;
                mazes.add(generateMaze(pos, stringMap));
            }
        }
        placeDoors(mazes, rooms, stringMap);
        fillDeadEnds(mazes, stringMap);
        if(rooms.size() >= 2)
            generateStaircase(rooms, stringMap);

        ArrayList<MPoint> free = new ArrayList<>();
        for(int y = 0; y < MapConfig.MAP_HEIGHT; y++){
            for(int x = 0; x < MapConfig.MAP_WIDTH; x++){
                MPoint tmp = new MPoint(x, y);
                if(stringMap.get(y).charAt(x) != MapConfig.FLOOR_CHAR) {
                    noiseSpace.replace(tmp, Double.MAX_VALUE);
                } else {
                    free.add(tmp);
                }
            }
        }

        placeEnemysAndChests(free, stringMap);
        _stringMap = new ArrayList<String>(stringMap);
        _mazes = new ArrayList<ArrayList<MPoint>>(mazes);
        _mazeCells = new ArrayList<>();
        for(ArrayList<MPoint> maze : MapLoader._mazes){
            _mazeCells.addAll(maze);
        }
        return parseMap(stringMap, player);
    }

    public static DungeonMap loadStringMap(ArrayList<String> stringMap, Player player){
        return parseMap(stringMap, player);
    }

    private static void placeEnemysAndChests(ArrayList<MPoint> free, ArrayList<String> stringMap){
        for(int i = 0; i < MapConfig.ENEMY_COUNT; i++){
            placeOnRandomFree(free, stringMap, MapConfig.ENEMY_CHAR);
        }

        for(int i = 0; i < MapConfig.CHEST_COUNT; i++){
            placeOnRandomFree(free, stringMap, MapConfig.CHEST_CHAR);
        }
    }

    private static void placeOnRandomFree(ArrayList<MPoint> free, ArrayList<String> stringMap, char tile){
        if(free.size() <= 0)
            return;
        int pos = random.nextInt(free.size());
        MPoint e = free.get(pos);
        double val = noiseSpace.get(e);
        replaceTile(e.x, e.y, tile, stringMap);
        free.remove(pos);
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
        replaceTile(x, y, MapConfig.ENTRY_CHAR, stringMap);
        replaceTile(x + 1, y, MapConfig.PLAYER_CHAR, stringMap);

        x = exitRoom.offsetX + (int) Math.floor(exitRoom.width / 2.0);
        y = exitRoom.offsetY + (int) Math.floor(exitRoom.height / 2.0);
        replaceTile(x, y, MapConfig.EXIT_CHAR, stringMap);
    }

    private static void fillDeadEnds(ArrayList<ArrayList<MPoint>> mazes, ArrayList<String> stringMap){
        // unite all maze lists
        ArrayList<MPoint> mazeCells = new ArrayList<>();
        for(ArrayList<MPoint> maze : mazes){
            mazeCells.addAll(maze);
        }

        ArrayList<MPoint> deadEnds = new ArrayList<>();
        for(int i = 0; i < MapConfig.FILL_DEADENDS; i++) {
            deadEnds.clear();
            // search for dead ends
            for (MPoint cell : mazeCells) {
                int wallCount = 0;
                for (Dir dir : Dir.values()) {
                    MPoint neighbour = cell.add(dir.value);
                    if(neighbour.y < stringMap.size() && neighbour.x < stringMap.get(neighbour.y).length())
                        if (stringMap.get(neighbour.y).charAt(neighbour.x) == MapConfig.WALL_CHAR)
                            wallCount++;
                }

                if (wallCount >= 3) {
                    deadEnds.add(cell);
                }
            }

            // fill up random dead end
            if(deadEnds.size() > 0) {
                MPoint chosen = deadEnds.get(random.nextInt(deadEnds.size()));
                replaceTile(chosen.x, chosen.y, MapConfig.WALL_CHAR, stringMap);
                mazeCells.remove(chosen);
                removeFromMazes(chosen, mazes);
                deadEnds.remove(chosen);
            }
        }

        // aboid negative possibility spaces
        for(MPoint ch : deadEnds){
            if(random.nextInt(100) < MapConfig.CHEST_CHANCE)
                replaceTile(ch.x, ch.y, MapConfig.CHEST_CHAR, stringMap);
        }
    }

    // TODO: [LATER] check how much slower would be if we count all the potential deadends
    public static void fillDeadEndStep(ArrayList<MPoint> mazeCells, ArrayList<ArrayList<MPoint>> mazes, ArrayList<String> stringMap){
        ArrayList<MPoint> deadEnds = new ArrayList<>();
        deadEnds.clear();
        // search for dead ends
        for (MPoint cell : mazeCells) {
            int wallCount = 0;
            for (Dir dir : Dir.values()) {
                MPoint neighbour = cell.add(dir.value);
                if(neighbour.y < stringMap.size() && neighbour.x < stringMap.get(neighbour.y).length())
                    if (stringMap.get(neighbour.y).charAt(neighbour.x) == MapConfig.WALL_CHAR)
                        wallCount++;
            }

            if (wallCount >= 3) {
                deadEnds.add(cell);
            }
        }

        // fill up random dead end
        if(deadEnds.size() > 0) {
            MPoint chosen = deadEnds.get(random.nextInt(deadEnds.size()));
            replaceTile(chosen.x, chosen.y, MapConfig.WALL_CHAR, stringMap);
            mazeCells.remove(chosen);
            removeFromMazes(chosen, mazes);
            deadEnds.remove(chosen);
        }
    }

    private static void removeFromMazes(MPoint point, ArrayList<ArrayList<MPoint>> mazes){
        for(ArrayList<MPoint> maze : mazes){
            if(maze.contains(point))
                maze.remove(point);
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
                    Room room = whichRoom(one, rooms);

                    if(room != null) {
                        connectors.add(one);
//                        only for demo
//                        room.addRelativeDoor(one.x - room.offsetX, one.y - room.offsetY);
                    }
                }
            }
        }

        // go through rooms
        for(Room room : rooms){
            // get relevant connections for room
            ArrayList<MPoint> conn = roomConnectors(connectors, room);
            if(conn.size() <= 0)
                continue;
            int index = random.nextInt(conn.size());
            MPoint open = conn.get(index);
            removeNextDoors(index, conn, connectors);

            connectors.remove(open);
            conn.remove(open);
            room.addRelativeDoor(open.x - room.offsetX, open.y - room.offsetY);
            // place more doors randomly to achive a non-perfect maze
            if(conn.size() > 0) {
                for (int i = 1; i < MapConfig.MAX_DOOR_COUNT; i++) {
                    if (random.nextInt(100) < MapConfig.DOOR_CHANCE && conn.size() > 0) {
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
        if (index - 1 < 0)
            rem = conn.remove(conn.size() - 1);
        else
            rem = conn.remove(index - 1);
        connectors.remove(rem);

        if(conn.size() > 0) {
            if (index + 1 >= conn.size())
                rem = conn.remove(0);
            else
                rem = conn.remove(index + 1);
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

    private static Room whichRoom(MPoint cell, ArrayList<Room> rooms){
        if(rooms == null)
            return null;
        for(Room r : rooms){
            if(r.containsAbsoluteWall(cell))
                return r;
        }
        return null;
    }

    private static ArrayList<MPoint> generateMaze(MPoint start, ArrayList<String> stringMap, ArrayList<String> mask){
        ArrayList<MPoint> maze = new ArrayList<>();
        ArrayList<MPoint> open = new ArrayList<>();
        Dir lastDir = null;

        // carve start
        replaceTile(start.x, start.y, MapConfig.FLOOR_CHAR, stringMap);
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
                if(dirs.contains(lastDir) && random.nextInt(100) > MapConfig.WINDINESS_PERCENT)
                    dir = lastDir;
                else
                    dir = dirs.get(random.nextInt(dirs.size()));

                MPoint next = node.add(dir.value);
                MPoint secondnext = node.add(dir.mult(2));

                // carve node next and second next from it
                replaceTile(next.x, next.y, MapConfig.FLOOR_CHAR, stringMap);
                replaceTile(secondnext.x, secondnext.y, MapConfig.FLOOR_CHAR, stringMap);
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
        replaceTile(start.x, start.y, MapConfig.FLOOR_CHAR, stringMap);
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
                if(dirs.contains(lastDir) && random.nextInt(100) > MapConfig.WINDINESS_PERCENT)
                    dir = lastDir;
                else
                    dir = dirs.get(random.nextInt(dirs.size()));

                MPoint next = node.add(dir.value);
                MPoint secondnext = node.add(dir.mult(2));

                // carve node next and second next from it
                replaceTile(next.x, next.y, MapConfig.FLOOR_CHAR, stringMap);
                replaceTile(secondnext.x, secondnext.y, MapConfig.FLOOR_CHAR, stringMap);
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

        return getTile(over.x, over.y, stringMap) != MapConfig.OUT_OF_BOUNDS_CHAR && getTile(next.x, next.y, stringMap) == MapConfig.WALL_CHAR &&
                getTile(one.x, one.y, stringMap) == MapConfig.WALL_CHAR && getTile(next.x, next.y, mask) == MapConfig.FLOOR_CHAR;
    }

    private static boolean shouldCarve(MPoint pos, Dir dir, ArrayList<String> stringMap){
        MPoint one = pos.add(dir.value);
        MPoint next = pos.add(dir.mult(2));
        MPoint over = pos.add(dir.mult(3));

        return getTile(over.x, over.y, stringMap) != MapConfig.OUT_OF_BOUNDS_CHAR && getTile(next.x, next.y, stringMap) == MapConfig.WALL_CHAR &&
                getTile(one.x, one.y, stringMap) == MapConfig.WALL_CHAR;
    }

    public static char getTile(int x, int y, ArrayList<String> stringMap){
        if(y >= stringMap.size() || y < 0)
            return MapConfig.OUT_OF_BOUNDS_CHAR;

        String line = stringMap.get(y);
        if(x >= line.length() || x < 0)
            return MapConfig.OUT_OF_BOUNDS_CHAR;

        return line.charAt(x);
    }

    private static ArrayList<Room> generateRooms(int mapWidth, int mapHeight, ArrayList<String> stringMap) {
        ArrayList<String> shadowMap = new ArrayList<>();
        for (int y = 0; y < MapConfig.MAP_HEIGHT; y++) {
            String line = "";
            for (int x = 0; x < MapConfig.MAP_WIDTH; x++) {
                line += MapConfig.FLOOR_CHAR;
            }
            shadowMap.add(line);
        }
        return generateRooms(mapWidth, mapHeight, stringMap, shadowMap);
    }

    private static Room placeMiddleRoom(int mapWidth, int mapHeight, ArrayList<String> stringMap, ArrayList<String> shadowMap){
        int centerX = (int) (mapWidth / 2.0);
        int centerY = (int) (mapHeight / 2.0);
        MPoint center = new MPoint(centerX, centerY);

        int w = MapConfig.MIN_ROOM_WIDTH + (MapConfig.ROOM_WIDTH_AMPL > 0 ? random.nextInt(MapConfig.ROOM_WIDTH_AMPL) : 0);
        w = w % 2 == 1 ? w+1 : w;
        int h = MapConfig.MIN_ROOM_HEIGHT + (MapConfig.ROOM_HEIGHT_AMPL > 0 ? random.nextInt(MapConfig.ROOM_HEIGHT_AMPL) : 0);
        h = h % 2 == 1 ? h+1 : h;
        int dist = MapConfig.MIN_ROOM_DIST + (MapConfig.ROOM_DIST_AMPL > 0 ? random.nextInt(MapConfig.ROOM_DIST_AMPL) : 0);
        Room room = new Room(w, h);
        int offX = centerX - (w / 2);
        int offY = centerY - (h / 2);
        if(offX % 2 == 1) offX--;
        if(offY % 2 == 1) offY--;
        room.setOffset(offX, offY);

        room.placeShadow(dist, shadowMap);
        if (Logger.DEBUG)
            printMap(shadowMap);

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
        for (int i = 0; i < MapConfig.ROOM_ATTEMPTS - 1; i++) {
            w = MapConfig.MIN_ROOM_WIDTH + (MapConfig.ROOM_WIDTH_AMPL > 0 ? random.nextInt(MapConfig.ROOM_WIDTH_AMPL) : 0);
            w = w % 2 == 1 ? w+1 : w;
            h = MapConfig.MIN_ROOM_HEIGHT + (MapConfig.ROOM_HEIGHT_AMPL > 0 ? random.nextInt(MapConfig.ROOM_HEIGHT_AMPL) : 0);
            h = h % 2 == 1 ? h+1 : h;
            dist = MapConfig.MIN_ROOM_DIST + (MapConfig.ROOM_DIST_AMPL > 0 ? random.nextInt(MapConfig.ROOM_DIST_AMPL) : 0);
            room = new Room(w, h);

            // drop around the edge
            int shift = i * ((2 * w + 2 * h));
            shift = shift % 2 == 0 ? shift+1 : shift;
            ArrayList<MPoint> drops = rotateList(getDrop(mapWidth - w - 1, mapHeight - h - 1), shift);
            for (MPoint drop : drops) {
                MPoint lastOff = new MPoint(drop);
                VPoint off = new VPoint(lastOff);
                room.setOffset((int) Math.floor(off.x), (int) Math.floor(off.y));

                // drop down
                while (room.checkCollision(shadowMap)) {
                    ArrayList<String> testMap = new ArrayList<>(shadowMap);
                    lastOff = new MPoint(off);

                    double d = off.dist(center);
                    if (d < 1) {
                        break;
                    }

                    off.x += (center.x - off.x) / d;
                    off.x = (int) Math.round(off.x);
                    off.x = off.x % 2 == 1 ? (off.x + (off.x < center.x ? 1 : -1)) : off.x;

                    off.y += (center.y - off.y) / d;
                    off.y = (int) Math.round(off.y);
                    off.y = off.y % 2 == 1 ? (off.y + (off.y < center.y ? 1 : -1)) : off.y;

                    room.setOffset((int) off.x, (int) off.y);
                    if (Logger.DEBUG) {
                        room.placeShadow(dist, testMap);
                        printMap(testMap);
                    }

                }
                room.setOffset(lastOff.x, lastOff.y);

                // are we done?
                if (room.checkCollision(shadowMap)) {
                    break;
                }
            }

            // final check
            if (room.checkCollision(shadowMap)) {
                room.placeShadow(dist, shadowMap);
                rooms.add(room);
            }
        }

        for (Room r : rooms) {
            r.placeRoom(stringMap);
        }

        MapConfig.ROOMS_MADE = rooms.size();

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

    private static ArrayList<MPoint> getDrop(int width, int height) {
        ArrayList<MPoint> drop = new ArrayList<>();

        int i;
        for (i = 0; i < width; i+=2)
            drop.add(new MPoint(i, 0));

        int j;
        for (j = 2; j < height; j+=2)
            drop.add(new MPoint(i, j));

        for (i = i - 2; i > 0; i-=2)
            drop.add(new MPoint(i, j));

        for (j = j - 2; j > 0; j-=2)
            drop.add(new MPoint(0, j));

        return drop;
    }

    private static void printMap(ArrayList<String> map) {
        String array = Arrays.toString(map.toArray());
        array = array.replaceAll(", ", ",\n");
        array = array.substring(1);
        Logger.log(array);
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

    public static <T> ArrayList<T> rotateList(ArrayList<T> aL, int shift) {
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
                MPoint pos = new MPoint(x, y);
                Tile tmp;

                switch (c) {
                    case MapConfig.WALL_CHAR:
                        tmp = new Wall(x, y);
                        break;
                    case MapConfig.DOOR_CHAR:
                        tmp = new Door(x, y);
                        break;
                    case MapConfig.CHEST_CHAR:
                        tmp = new Chest(x, y, new Sword("Master Sword", 1000  + (int) Math.round(500 * (noiseSpace.get(pos) - 0.5))));
                        break;
                    case MapConfig.ENTRY_CHAR:
                        tmp = new EntryStair(x, y);
                        break;
                    case MapConfig.EXIT_CHAR:
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
                MPoint pos = new MPoint(x, y);
                if (c == MapConfig.ENEMY_CHAR) {
                    Enemy enemy = new Enemy(null, 600 + (int) Math.round(100 * (noiseSpace.get(pos) - 0.5)) );
                    dungeon.addGameObject(x, y, enemy);
                }
                if (c == MapConfig.PLAYER_CHAR)
                    dungeon.addGameObject(x, y, player);
            }
        }

        return dungeon;
    }


    // # Weighting #####################################################################################################

    public static void resetNoise(double width, double height){
        double offx = random.nextDouble() * width;
        double offy = random.nextDouble() * height;
        offsetNoise((float)offx, (float) offy, 0);
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
}
