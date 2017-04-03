package com.soulevans.proceduraldungeon.model.map;

import com.soulevans.proceduraldungeon.model.base.MPoint;
import com.soulevans.proceduraldungeon.model.entities.items.Sword;
import com.soulevans.proceduraldungeon.model.entities.living.Enemy;
import com.soulevans.proceduraldungeon.model.entities.living.Living;
import com.soulevans.proceduraldungeon.model.entities.living.Player;

import java.io.*;
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
            int y = 0;
            int x = 0;
            ArrayList<String> stringMap = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                line = line.toLowerCase();
                stringMap.add(line);

                for(x = 0; x < line.length(); x++){
                    Tile tmp;
                    if(line.charAt(x) == '#')
                        tmp = new Wall(x, y);
                    else if(line.charAt(x) == 'd')
                        tmp = new Door(x, y);
                    else if(line.charAt(x) == 'c')
                        tmp = new Chest(x, y, new Sword("Master Sword", 10000));
                    else
                        tmp = new Floor(x, y);
                    map.put(new MPoint(x, y), tmp);
                }
                y++;
            }

            dungeon = new DungeonMap(map);
            dungeon.mapWidth = x;
            dungeon.mapHeight = y;

            ArrayList<Living> living = new ArrayList<>();
            for(y = 0; y < stringMap.size(); y++) {
                for (x = 0; x < stringMap.get(y).length(); x++) {
                    if(stringMap.get(y).charAt(x) == 'e') {
                        Enemy enemy = new Enemy(null, 600);
                        dungeon.addGameObject(x, y, enemy);
                    }
                    if(stringMap.get(y).charAt(x) == 'p')
                        dungeon.addGameObject(x,y, player);
                }
            }
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
}
