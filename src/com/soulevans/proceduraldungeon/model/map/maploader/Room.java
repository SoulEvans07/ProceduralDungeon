package com.soulevans.proceduraldungeon.model.map.maploader;

import com.soulevans.proceduraldungeon.Game;
import com.soulevans.proceduraldungeon.config.MapConfig;
import com.soulevans.proceduraldungeon.logger.LogType;
import com.soulevans.proceduraldungeon.logger.Logger;
import com.soulevans.proceduraldungeon.model.base.MPoint;
import com.soulevans.proceduraldungeon.model.map.Wall;

import java.util.*;

public class Room {
    public int offsetX;
    public int offsetY;
    public int width;
    public int height;
    public ArrayList<MPoint> corners;
    public ArrayList<MPoint> walls;
    private ArrayList<MPoint> doors;
    private ArrayList<MPoint> enemies;
    private ArrayList<MPoint> chests;

    private static final char WALL_CHAR = MapConfig.WALL_CHAR;
    private static final char DOOR_CHAR = MapConfig.DOOR_CHAR;
    private static final char CHEST_CHAR = MapConfig.CHEST_CHAR;
    private static final char FLOOR_CHAR = MapConfig.FLOOR_CHAR;
    private static final char ENEMY_CHAR = MapConfig.ENEMY_CHAR;
    private static final char SHADOW_CHAR = MapConfig.SHADOW_CHAR;

    public Room(int w, int h){
        this(0, 0, w, h);
    }

    public Room(int x, int y, int w, int h){
        corners = new ArrayList<>();
        walls = new ArrayList<>();
        doors = new ArrayList<>();
        enemies = new ArrayList<>();
        chests = new ArrayList<>();
        this.offsetX = x;
        this.offsetY = y;
        this.width = w;
        this.height = h;

        this.initWalls();
    }

    public void placeRoom(ArrayList<String> stringMap){
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                MapLoader.replaceTile(this.offsetX + x, this.offsetY + y, this.FLOOR_CHAR, stringMap);
            }
        }
        for(MPoint corner : corners){
            MapLoader.replaceTile(this.offsetX + corner.x, this.offsetY + corner.y, this.WALL_CHAR, stringMap);
        }
        for(MPoint wall : walls){
            MapLoader.replaceTile(this.offsetX + wall.x, this.offsetY + wall.y, this.WALL_CHAR, stringMap);
        }
        for(MPoint door : doors){
            MapLoader.replaceTile(this.offsetX + door.x, this.offsetY + door.y, this.DOOR_CHAR, stringMap);
        }
        for(MPoint enemy : enemies){
            MapLoader.replaceTile(this.offsetX + enemy.x, this.offsetY + enemy.y, this.ENEMY_CHAR, stringMap);
        }
        for(MPoint chest : chests){
            MapLoader.replaceTile(this.offsetX + chest.x, this.offsetY + chest.y, this.CHEST_CHAR, stringMap);
        }
    }

    public void placeShadow(int border, ArrayList<String> shadowMap){
        for(int y = -border; y <= height+border; y++){
            for(int x = -border; x <= width+border; x++){
                if(MapLoader.getTile(this.offsetX + x, this.offsetY + y, shadowMap) == this.FLOOR_CHAR )
                    MapLoader.replaceTile(this.offsetX + x, this.offsetY + y, this.SHADOW_CHAR, shadowMap);
            }
        }
        for(MPoint corner : corners){
            MapLoader.replaceTile(this.offsetX + corner.x, this.offsetY + corner.y, this.WALL_CHAR, shadowMap);
        }
        for(MPoint wall : walls){
            MapLoader.replaceTile(this.offsetX + wall.x, this.offsetY + wall.y, this.WALL_CHAR, shadowMap);
        }
    }

    public boolean checkCollision(ArrayList<String> shadowMap){
        String wall = Arrays.toString(walls.toArray());
        //Logger.log("checkCollision: " + wall);
        for(MPoint w : walls){
            MPoint temp = new MPoint(w);
            temp.x += offsetX;
            temp.y += offsetY;
            if(temp.y < shadowMap.size() && temp.x < shadowMap.get(temp.y).length()) {
                char c = shadowMap.get(temp.y).charAt(temp.x);
                if(c != '_')
                    return false;
            } else
                return false;
        }
        return true;
    }

    public void addEnemy(int x, int y){
        MPoint enemy = new MPoint(x, y);
        if(!walls.contains(enemy))
            enemies.add(enemy);
        else if(walls.contains(enemy))
            Logger.log(LogType.ERROR, "Cannot place ENEMY on wall: " + enemy + " in room " + this);
        else if(chests.contains(enemy))
            Logger.log(LogType.ERROR, "Cannot place ENEMY on chest: " + enemy + " in room " + this);
        else if(enemies.contains(enemy))
            Logger.log(LogType.ERROR, "Cannot place ENEMY on other enemy: " + enemy + " in room " + this);
        else
            Logger.log(LogType.ERROR, "Cannot place ENEMY, " + enemy + " in room " + this);
    }

    public void addChest(int x, int y){
        MPoint chest = new MPoint(x, y);
        if(!walls.contains(chest))
            chests.add(chest);
        else if(walls.contains(chest))
            Logger.log(LogType.ERROR, "Cannot place ENEMY on wall: " + chest + " in room " + this);
        else if(chests.contains(chest))
            Logger.log(LogType.ERROR, "Cannot place ENEMY on chest: " + chest + " in room " + this);
        else if(enemies.contains(chest))
            Logger.log(LogType.ERROR, "Cannot place ENEMY on other enemy: " + chest + " in room " + this);
        else
            Logger.log(LogType.ERROR, "Cannot place ENEMY, " + chest + " in room " + this);
    }

    public void addRelativeDoor(int x, int y){
        MPoint door = new MPoint(x, y);
        if(walls.contains(door))
            doors.add(door);
        else {
            Logger.log(LogType.ERROR, "Cannot place DOOR on " + door + " in room " + this);
            Logger.log(LogType.ERROR, "\tcorners: " + Arrays.toString(corners.toArray()));
            Logger.log(LogType.ERROR, "\twalls:   " + Arrays.toString(walls.toArray()));
        }
    }

    public boolean containsAbsoluteWall(MPoint cell){
        for(MPoint wall : walls){
            MPoint absWall = new MPoint(wall.x + offsetX, wall.y + offsetY);
//            System.out.println("from: " + wall + " to: " + absWall + " check for: " + cell);
            if(absWall.equals(cell)) {
                return true;
            }
        }
        return false;
    }

    public void setOffset(int x, int y){
        this.offsetX = x;
        this.offsetY = y;
    }

    public void randomDoor(){
        Random random = new Random();
        ArrayList<MPoint> temp = new ArrayList<>(walls);
        temp.removeAll(doors); // you cant delete corners this way
        // remove walls but not corners on the edge of the map
        for(int i = 4; i < walls.size(); i++){
            MPoint wall = walls.get(i);
            if(wall.x+offsetX == 0 || wall.y+offsetY == 0 || wall.x+offsetX == MapLoader.width-1 || wall.y+offsetY == MapLoader.height-1){
                temp.remove(wall);
            }
        }
        // remove doors and it's neighbours
        for(int i = 0; i < doors.size(); i++){
            int index = walls.indexOf(doors.get(i));
            int l = index-1;
            int r = index+1;

            if(l < 4)
                l = walls.size()-1;
            if(r >= walls.size())
                r = 4;

            MPoint left = walls.get(l);
            MPoint right = walls.get(r);

            temp.remove(left);
            temp.remove(right);
        }
        // get the remaining places and choose one random
        // first 4 is corner but you at least want to use the first wall so 5 piece is out
        int wallNum = temp.size() - 5;
        if(wallNum > 0) {
            int doorNum = 4 + (random.nextInt(wallNum));
            MPoint door = temp.get(doorNum);
            doors.add(door);
        }
    }

    private void initWalls(){
        // corners
        corners.add(new MPoint(0, 0));
        corners.add(new MPoint(width, 0));
        corners.add(new MPoint(width, height));
        corners.add(new MPoint(0, height));

        // walls
        for (int i = 1; i < width; i++) {
            walls.add(new MPoint(i, 0));
        }
        for (int i = 1; i < width; i++) {
            walls.add(new MPoint(i, height));
        }

        for (int j = 1; j < height; j++){
            walls.add(new MPoint(0, j));
        }
        for (int j = 1; j < height; j++){
            walls.add(new MPoint(width, j));
        }
    }

    @Override
    public String toString(){
        return "[ p1: ["+ offsetX +", "+ offsetY +"] - p2: ["+(offsetX +width)+", "+(offsetY +height)+"] - size: ["+width+", "+height+"]]";
    }
}
