package com.soulevans.proceduraldungeon.model.map;

import com.soulevans.proceduraldungeon.model.base.MPoint;
import com.soulevans.proceduraldungeon.model.entities.items.Sword;
import com.soulevans.proceduraldungeon.model.entities.living.Enemy;
import com.soulevans.proceduraldungeon.model.entities.living.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapLoader {
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

    public static DungeonMap loadRandom(int lv, Player player){
        DungeonMap dungeon = null;
        Map<MPoint, Tile> map = new HashMap<>();

        int y = 0;
        int x = 0;

        dungeon = new DungeonMap(map);
        dungeon.mapWidth = x;
        dungeon.mapHeight = y;

        return dungeon;
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
