package com.soulevans.proceduraldungeon;

import com.soulevans.proceduraldungeon.logger.Logger;
import com.soulevans.proceduraldungeon.model.base.MPoint;
import com.soulevans.proceduraldungeon.model.base.VPoint;
import com.soulevans.proceduraldungeon.model.entities.GameObject;
import com.soulevans.proceduraldungeon.model.entities.living.Living;
import com.soulevans.proceduraldungeon.model.entities.living.Player;
import com.soulevans.proceduraldungeon.model.map.DungeonMap;
import com.soulevans.proceduraldungeon.model.map.maploader.MapLoader;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Game {
    private double width, height;
    public static final double MAGNITUDE = 0.01;
    public static double scale = 1;
    private VPoint centerMoved;
    private VPoint lastPos;
    private VPoint origo;
    public static double TILESIZE = 16;
    public Canvas canvas;

    private DungeonMap map;
    private Player player;

    private static Game instance;

    private Game(){}

    public void init(double w, double h, Canvas canvas){
        this.canvas = canvas;

        player = new Player(null, 1000);
        map = MapLoader.loadRandom(2, player);

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

    public void nextLevel(){
        init(width, height, canvas);
    }


//    View    ##########################################################################################################

    public void drawGame(GraphicsContext gc){
        gc.getCanvas().setScaleX(scale);
        gc.getCanvas().setScaleY(scale);
        map.drawMap(gc);

//        drawNoise(gc, 0.5);
    }

    public void drawNoise(GraphicsContext gc, double opacity){
        if(MapLoader.noiseSpace != null && MapLoader.noiseSpace.size() > 0) {
            for (int y = 0; y < map.mapHeight; y++) {
                for (int x = 0; x < map.mapWidth; x++) {
                    MPoint tmp = new MPoint(x, y);
                    double gray = MapLoader.noiseSpace.get(tmp);
                    Color g = Color.BLACK;
                    if(gray <= 1)
                        g = Color.gray(0, gray);
                    gc.setStroke(g);
                    gc.setFill(g);
                    gc.fillRect(x * Game.TILESIZE, y * Game.TILESIZE, Game.TILESIZE, Game.TILESIZE);
                }
            }
        }
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
            Logger.log("[--------------Reset--------------]");
            init(width, height, canvas);
//            this.savePNG();
        }

        if(event.getCode().getName().equals("P")) {
            this.savePNG();
        }

        if(event.getCode().getName().equals("I")){
            Logger.log("[Inventory]");
            player.listInventory();
        }

        player.onKeyReleased(event);
        map.removeDead();
    }

    public void savePNG(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HHmmss");
        Date date = new Date();

        File file = new File("printscreen\\"+dateFormat.format(date)+".png");
        Logger.log("Printscreen: " + file.getName())
        ;
        WritableImage writableImage = new WritableImage((int) width, (int) height);
        canvas.snapshot(null, writableImage);
        RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
        try {
            ImageIO.write(renderedImage, "png", file);
            Logger.log("Printscreen: " + file.getName());
        } catch (IOException e) {
            e.printStackTrace();
            Logger.log("Printscreen failed!");
        }
    }

    public void onKeyReleased(KeyEvent event){
//        Logger.log(LogType.EVENT, "keyReleased: " + event.getCode().getName());
//        player.onKeyReleased(event);
//        map.removeDead();
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
//        zoom(event.getTextDeltaY());
//
//        double distDiff = scale - 1.0;
//        VPoint now = new VPoint(event.getX(), event.getY());
//
//        VPoint diff = new VPoint(origo.x - now.x,origo.y - now.y);
//        diff.x *= distDiff;
//        diff.y *= distDiff;
//
//        canvas.setTranslateX(centerMoved.x + diff.x);
//        canvas.setTranslateY(centerMoved.y + diff.y);
//
////        now = translate(now);
////        origo = new VPoint(width/2 * scale, height/2 * scale);
//
////
////        System.out.println("------------------------------------------");
////        System.out.println("canvas: ["+ width+", "+height+"]");
////        System.out.println("window: [800, 800]");
////        System.out.println("D: "+diff);
////        System.out.println("scale: " + scale + " distDiff: " + distDiff);
//
////        System.out.println("deltaD: " + diff);
////        System.out.println("last: "+lastPos);
////        System.out.println("center bef: "+centerMoved);
////
////        centerMoved.x += diff.x;
////        centerMoved.y += diff.y;
////        lastPos = now;
////
////        System.out.println("center aft: "+centerMoved);
////        System.out.println("new: "+lastPos);
    }
}
