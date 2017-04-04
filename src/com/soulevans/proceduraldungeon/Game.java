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
import javafx.scene.input.ScrollEvent;

public class Game {
    private int mapWidth, mapHeight;
    private double width, height;
    public static final double MAGNITUDE = 0.01;
    public static double scale = 1;
    private VPoint centerMoved;
    private VPoint lastPos;
    private VPoint origo;
    public static double TILESIZE;
    private Canvas canvas;

    private DungeonMap map;

    private Player player;

    private static Game instance;

    private Game(){}

    public void init(double w, double h, Canvas canvas){
        this.canvas = canvas;

        player = new Player(null, 1000);
        map = MapLoader.loadMap(2, player);

        Game.TILESIZE = 32;
        this.width = map.mapWidth * Game.TILESIZE; // View
        this.height = map.mapHeight * Game.TILESIZE; // View

        scale = 1.0;
        centerMoved = new VPoint(0, 0);
        //lastPos = new VPoint(0, 0);
        origo = new VPoint(width/2, height/2);
        canvas.setTranslateX(centerMoved.x);
        canvas.setTranslateY(centerMoved.y);

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

    public void drawGame(GraphicsContext gc){
        gc.getCanvas().setScaleX(scale);
        gc.getCanvas().setScaleY(scale);
        map.drawMap(gc);

    }

    public static void zoom(String z){
//        if(z.equals("Add")) {
//            scale += MAGNITUDE;
//        }
//        if(z.equals("Minus") || z.equals("Subtract")) {
//            scale -= MAGNITUDE;
//        }
    }

    private static void zoom(double scroll){
        scale += MAGNITUDE * scroll;
    }

//    private void updateLast(double eventx, double eventy){
//        lastPos = new VPoint(eventx - origo.x, - eventy + origo.y);
//    }

    private VPoint translate(VPoint vect){
        return new VPoint(
                -origo.x + centerMoved.x + vect.x,
                -origo.y + centerMoved.y + vect.y
        );
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


    public void onMousePressed(MouseEvent event){
//        Logger.log(LogType.EVENT,"mousePressed [" + event.getX() + ", " + event.getY() + "]");
        lastPos = new VPoint(event.getX(), event.getY());
    }

    public void onMouseDragged(MouseEvent event){
//        Logger.log(LogType.EVENT,"mouseDragged [" + event.getX() + ", " + event.getY() + "]");
//        Logger.log(LogType.EVENT, "move from: " +lastPos+ ", to: " +now);
//        Logger.log(LogType.EVENT, "move: [" + (now.x - lastPos.x) + ", " + (now.y - lastPos.y)+ "]");
//        Logger.log(LogType.EVENT, "move: [" + event.getX() + ", " + event.getY() + "]");

        VPoint now = new VPoint(event.getX(), event.getY());
        centerMoved = new VPoint(centerMoved.x + (now.x - lastPos.x), centerMoved.y + (now.y - lastPos.y));
        canvas.setTranslateX(centerMoved.x);
        canvas.setTranslateY(centerMoved.y);
        lastPos = now;
    }

    public void onMouseReleased(MouseEvent event){
//        Logger.log(LogType.EVENT,"mouseReleased");
    }

    public void onScroll(ScrollEvent event){
//        Logger.log(LogType.EVENT,"scroll: " + event.getTextDeltaY());
//        Logger.log(LogType.EVENT,"scrollon: [" + event.getX() + ", " + event.getY() + "][" + centerMoved.x + ", " + centerMoved.y + "]");
        zoom(event.getTextDeltaY());

        double distDiff = scale - 1.0;
        VPoint now = new VPoint(event.getX(), event.getY());

        VPoint diff = new VPoint(origo.x - now.x,origo.y - now.y);
        diff.x *= distDiff;
        diff.y *= distDiff;

        canvas.setTranslateX(centerMoved.x + diff.x);
        canvas.setTranslateY(centerMoved.y + diff.y);

//        now = translate(now);
//        origo = new VPoint(width/2 * scale, height/2 * scale);

//
//        System.out.println("------------------------------------------");
//        System.out.println("canvas: ["+ width+", "+height+"]");
//        System.out.println("window: [800, 800]");
//        System.out.println("D: "+diff);
//        System.out.println("scale: " + scale + " distDiff: " + distDiff);

//        System.out.println("deltaD: " + diff);
//        System.out.println("last: "+lastPos);
//        System.out.println("center bef: "+centerMoved);
//
//        centerMoved.x += diff.x;
//        centerMoved.y += diff.y;
//        lastPos = now;
//
//        System.out.println("center aft: "+centerMoved);
//        System.out.println("new: "+lastPos);
//


    }
}
