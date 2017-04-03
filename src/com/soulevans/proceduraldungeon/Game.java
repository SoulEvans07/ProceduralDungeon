package com.soulevans.proceduraldungeon;

import com.soulevans.proceduraldungeon.logger.Logger;
import com.soulevans.proceduraldungeon.model.base.VPoint;
import com.soulevans.proceduraldungeon.model.entities.GameObject;
import com.soulevans.proceduraldungeon.model.entities.living.Living;
import com.soulevans.proceduraldungeon.model.entities.living.Player;
import com.soulevans.proceduraldungeon.model.map.DungeonMap;
import com.soulevans.proceduraldungeon.model.map.MapLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class Game {
    private int mapWidth, mapHeight;
    private double width, height;
    public static double TILESIZE;
    private Canvas canvas;

    private DungeonMap map;

    private Player player;

    private static Game instance;

    private Game(){}

    public void init(double w, double h, Canvas canvas){
        this.canvas = canvas;

        player = new Player(null, 1000);
        map = MapLoader.loadMap(1, player);

        Game.TILESIZE = 30;

        this.width = map.mapWidth * Game.TILESIZE; // View
        this.height = map.mapHeight * Game.TILESIZE; // View

        canvas.setWidth(this.width);
        canvas.setHeight(this.height);

        for(GameObject object : map.entities){
            if(object instanceof Living) {
                ((Living) object).lookAround();
            }
        }
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


    public void onKeyPressed(KeyEvent event){
//        Logger.log(LogType.EVENT, "keyPressed: " + event.getCode().getName());
        Game.zoom(event.getCode().getName());

        if(event.getCode().getName().equals("R")) {
            Logger.log("Reset");
            init(width, height, canvas);
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


    VPoint canvasCenter = new VPoint(0, 0);
    VPoint lastPos = new VPoint(0, 0);
    public void onMousePressed(MouseEvent event){
//        Logger.log(LogType.EVENT,"mousePressed [" + event.getX() + ", " + event.getY() + "]");
        lastPos = new VPoint(event.getX(), event.getY());
    }

    public void onMouseDragged(MouseEvent event){
//        Logger.log(LogType.EVENT,"mouseDragged [" + event.getX() + ", " + event.getY() + "]");
//        Logger.log(LogType.EVENT, "move: [" + (lastPos.x - event.getX()) + ", " + (lastPos.y - event.getY())+ "]");
        canvasCenter.x -= (lastPos.x - event.getX()) * scale;
        canvasCenter.y -= (lastPos.y - event.getY()) * scale;
        lastPos = new VPoint(event.getX(), event.getY());
        canvas.setTranslateX(canvasCenter.x);
        canvas.setTranslateY(canvasCenter.y);
    }

    public void onMouseReleased(MouseEvent event){
//        Logger.log(LogType.EVENT,"mouseReleased");
    }

}
