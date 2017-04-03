package com.soulevans.proceduraldungeon.model.map;

import com.soulevans.proceduraldungeon.model.base.MPoint;

import java.util.HashMap;
import java.util.Map;

public class MapLoader {

    public MapLoader(){

    }

    public static DungeonMap loadMap(int level){
        DungeonMap dungeon = new DungeonMap();
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

        return new DungeonMap(map);
    }
}
