package com.soulevans.proceduraldungeon;

import com.soulevans.proceduraldungeon.model.entities.GameObject;
import com.soulevans.proceduraldungeon.model.entities.living.Enemy;
import com.soulevans.proceduraldungeon.model.entities.living.Living;
import com.soulevans.proceduraldungeon.model.entities.living.Player;
import com.soulevans.proceduraldungeon.logger.Logger;
import com.soulevans.proceduraldungeon.model.map.DungeonMap;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.Random;

public class Game {
    private int mapWidth, mapHeight;
    private double width, height;
    public static double TILESIZE;

    private DungeonMap map;

    private Player player;
    private ArrayList<GameObject> gameObjects;

    private static Game instance;

    private Game(){}

    public void init(double w, double h){
        this.mapWidth = 40; // Model
        this.mapHeight = 40; // Model

        map = new DungeonMap(mapWidth, mapHeight);

        this.width = w; // View
        this.height = h; // View

//        Game.TILESIZE = Math.min(width/mapWidth, height/mapHeight);
        Game.TILESIZE = 30;

        player = new Player(null, 1000);
        map.addGameObject(0,0, player);

        gameObjects = new ArrayList<>();
        ArrayList<Living> living = new ArrayList<>();

        Random rand = new Random();
        for(int i = 0; i < 5; i++){
            Enemy enemy = new Enemy(null, 600);
            map.addGameObject(rand.nextInt(mapWidth), rand.nextInt(mapHeight), enemy);
            living.add(enemy);
        }

        for(int i = 0; i < living.size(); i++){
            living.get(i).lookAround();
        }
        player.lookAround();
    }

    public static Game getInstance(){
        if(instance == null)
            instance = new Game();

        return instance;
    }

    public DungeonMap getMap(){
        return map;
    }


//    View    ##########################################################################################################

    public static double scale = 1;
    public void drawGame(GraphicsContext gc){
        gc.getCanvas().setScaleX(scale);
        gc.getCanvas().setScaleY(scale);
        map.drawMap(gc);

    }

    public static void zoom(String z){
        if(z.equals("Add")) {
            scale += 0.01;
        }
        if(z.equals("Minus") || z.equals("Subtract")) {
            scale -= 0.01;
        }
    }


//    Control    #######################################################################################################

    public void tick(){
//        Logger.log(LogType.EVENT, "tick");
        for(int i = 0; i < gameObjects.size(); i++){
            gameObjects.get(i).tick();
        }
    }

    public void onKeyPressed(KeyEvent event){
//        Logger.log(LogType.EVENT, "keyPressed: " + event.getCode().getName());
        Game.zoom(event.getCode().getName());

        if(event.getCode().getName().equals("R")) {
            Logger.log("Reset");
            init(width, height);
        }

        if(event.getCode().getName().equals("I")){
            Logger.log("[Inventory]");
            player.listInventory();
        }
    }

    public void onKeyReleased(KeyEvent event){
//        Logger.log(LogType.EVENT, "keyReleased: " + event.getCode().getName());
        player.onKeyReleased(event);
        map.removeDead();
    }

    public void onMousePressed(MouseEvent event){
//        Logger.log(LogType.EVENT,"mousePressed [" + event.getX() + ", " + event.getY() + "]");
    }

    public void onMouseDragged(MouseEvent event){
//        Logger.log(LogType.EVENT,"mouseDragged [" + event.getX() + ", " + event.getY() + "]");
    }

    public void onMouseReleased(MouseEvent event){
//        Logger.log(LogType.EVENT,"mouseReleased");
    }

}
