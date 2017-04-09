package com.soulevans.proceduraldungeon.model.map.maploader;

import com.soulevans.proceduraldungeon.logger.LogType;
import com.soulevans.proceduraldungeon.logger.Logger;
import com.soulevans.proceduraldungeon.model.base.MPoint;
import com.soulevans.proceduraldungeon.model.map.Wall;

import java.util.ArrayList;
import java.util.Arrays;

public class Room {
    public int offsetX;
    public int offsetY;
    private int width;
    private int height;
    public ArrayList<MPoint> walls;
    private ArrayList<MPoint> doors;
    private ArrayList<MPoint> enemies;
    private ArrayList<MPoint> chests;

    public Room(int w, int h){
        this(0, 0, w, h);
    }

    public Room(int x, int y, int w, int h){
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
        for(MPoint wall : walls){
            MapLoader.replaceTile(this.offsetX + wall.x, this.offsetY + wall.y, '#', stringMap);
        }
        for(MPoint door : doors){
            MapLoader.replaceTile(this.offsetX + door.x, this.offsetY + door.y, 'd', stringMap);
        }
        for(MPoint enemy : enemies){
            MapLoader.replaceTile(this.offsetX + enemy.x, this.offsetY + enemy.y, 'e', stringMap);
        }
        for(MPoint chest : chests){
            MapLoader.replaceTile(this.offsetX + chest.x, this.offsetY + chest.y, 'c', stringMap);
        }
    }

    public void placeShadow(int border, ArrayList<String> shadowMap){
        for(int y = -border; y <= height+border; y++){
            for(int x = -border; x <= width+border; x++){
                MapLoader.replaceTile(this.offsetX + x, this.offsetY + y, '¤', shadowMap);
            }
        }
        for(MPoint wall : walls){
            MapLoader.replaceTile(this.offsetX + wall.x, this.offsetY + wall.y, '#', shadowMap);
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

    public void addDoor(int x, int y){
        MPoint door = new MPoint(x, y);
        if(walls.contains(door))
            doors.add(door);
        else
            Logger.log(LogType.ERROR, "Cannot place DOOR on " + door + " in room " + this);
    }

    public void setOffset(int x, int y){
        this.offsetX = x;
        this.offsetY = y;
    }

    private void initWalls(){
        walls.add(new MPoint(0, 0));
        walls.add(new MPoint(width, 0));
        walls.add(new MPoint(width, height));
        walls.add(new MPoint(0, height));

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
        return "[["+ offsetX +", "+ offsetY +"]:["+(offsetX +width)+", "+(offsetY +height)+"]]";
    }
}
