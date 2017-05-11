package com.soulevans.proceduraldungeon;

import com.soulevans.proceduraldungeon.config.MapConfig;
import com.soulevans.proceduraldungeon.logger.LogType;
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
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Game {
    public static final double MAGNITUDE = 0.01;
    public static double scale = 1;

    public static double TILESIZE = MapConfig.TILESIZE;

    private double width, height;
    private VPoint centerMoved;
    private VPoint lastPos;
    private VPoint origo;
    public Canvas canvas;

    private DungeonMap map;
    private Player player;

    private static Game instance;

    private Game(){}

    public void init(double w, double h, Canvas canvas){
        this.canvas = canvas;

        player = new Player(null, 1000);
        map = MapLoader.loadRandom(2, player);
//        map = MapLoader.loadMap(3, player);
//        map = MapLoader.startFromMap(1, player);

        this.width = map.mapWidth * Game.TILESIZE; // View
        this.height = map.mapHeight * Game.TILESIZE; // View

        scale = 1.0;
        centerMoved = new VPoint(0, 0);
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

    public static boolean NOISE_MAP = false;
    public void drawGame(GraphicsContext gc){
        gc.getCanvas().setScaleX(scale);
        gc.getCanvas().setScaleY(scale);
        map.drawMap(gc);

        if(NOISE_MAP)
            drawNoise(gc, 1);
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
        if(z.equals("Add")) {
            scale += MAGNITUDE;
        }
        if(z.equals("Minus") || z.equals("Subtract")) {
            scale -= MAGNITUDE;
        }
    }

    private static void zoom(double scroll){
        scale += MAGNITUDE * scroll;
    }

    private VPoint translatePoint(VPoint vect){
        return new VPoint(
                -origo.x + centerMoved.x + vect.x,
                -origo.y + centerMoved.y + vect.y
        );
    }


//    Control    #######################################################################################################

    public void onKeyPressed(KeyEvent event){
        Logger.logDebug(LogType.EVENT, "keyPressed: " + event.getCode().getName());
        Game.zoom(event.getCode().getName());

        if(event.getCode().getName().equals("R")) {
            Logger.logDebug("[--------------Reset--------------]");
            init(width, height, canvas);
        }

        // FILL_DEAD_END test cases.
        // fills dead ends one by one
        if(event.getCode().getName().equals("H")){
            MapLoader.fillDeadEndStep(MapLoader._mazeCells, MapLoader._mazes, MapLoader._stringMap);
            map = MapLoader.loadStringMap(MapLoader._stringMap, player);
            mapSnapshot(false, MapLoader._mazeCells.size()+"_left");
            canvas.setVisible(true);
        }

        player.onKeyReleased(event);
        map.removeDead();
    }

    public void onKeyReleased(KeyEvent event){
        Logger.logDebug(LogType.EVENT, "keyReleased: " + event.getCode().getName());
        if(event.getCode().getName().equals("P")) {
            this.savePNG(true, null);
        }

        // Minimum ROOM_ATTEMTS test case
        // checks from 15x15 to 99x99 whats the most room it can fit in
        // room size amplitude is 1/4 of the map size.
        // writes date to corresponding .csv file
        if(event.getCode().getName().equals("J")) {
            for(int size = 15; size < 100; size+=2) {
                MapConfig.MAP_WIDTH = MapConfig.MAP_HEIGHT = size;
                MapConfig.MIN_ROOM_WIDTH = MapConfig.MIN_ROOM_HEIGHT = 3;
                MapConfig.MIN_ROOM_DIST = 1;
                MapConfig.ROOM_WIDTH_AMPL = MapConfig.ROOM_HEIGHT_AMPL = size/4;
                for (int i = 0; i < 50; i++) {
                    init(width, height, canvas);
                    MapConfig.writeRoomAttemptsFile();
                }
            }
        }

        // Minimum ROOM_ATTEMTS test case
        // checks from 15x15 to 99x99 whats the most room it can fit in
        // works with the smallest roomsize 3x3 with 1 distance between them.
        if(event.getCode().getName().equals("K")) {
            for(int size = 15; size < 100; size+=2) {
                MapConfig.MAP_WIDTH = MapConfig.MAP_HEIGHT = size;
                MapConfig.ROOM_ATTEMPTS = MapConfig.getOptimalRoomAttempts();
                MapConfig.MIN_ROOM_WIDTH = MapConfig.MIN_ROOM_HEIGHT = 3;
                MapConfig.MIN_ROOM_DIST = 1;
                MapConfig.ROOM_WIDTH_AMPL = MapConfig.ROOM_HEIGHT_AMPL = 0;
                init(width, height, canvas);
                Logger.logFile("maxRoom.txt", "[" + size + "; " + size +"] max Room: " +MapConfig.ROOMS_MADE);
            }
        }

        // WINDINESS_PERCENT test case
        // makes snapshots from different values of windiness
        if(event.getCode().getName().equals("L")) {
            MapConfig.MAP_WIDTH = MapConfig.MAP_HEIGHT = 37;
            MapConfig.ROOM_ATTEMPTS = MapConfig.getOptimalRoomAttempts();
            MapConfig.MIN_ROOM_WIDTH = MapConfig.MIN_ROOM_HEIGHT = 3;
            MapConfig.MIN_ROOM_DIST = 1;
            MapConfig.ROOM_WIDTH_AMPL = MapConfig.ROOM_HEIGHT_AMPL = 37/4;
            for(int windy = 0; windy <= 100; windy+=10) {
                MapConfig.WINDINESS_PERCENT = windy;
                init(width, height, canvas);
                mapSnapshot(true, windy+"_windi");
            }
        }

        if(event.getCode().getName().equals("N")){
            NOISE_MAP = !NOISE_MAP;
            Logger.log("[NOISE_MAP]: " + NOISE_MAP);
        }

        if(event.getCode().getName().equals("I")){
            Logger.log("[Inventory]");
            player.listInventory();
        }
    }

    public void onMousePressed(MouseEvent event){
        Logger.logDebug(LogType.EVENT,"mousePressed [" + event.getX() + ", " + event.getY() + "]");
        lastPos = new VPoint(event.getX(), event.getY());
    }

    public void onMouseDragged(MouseEvent event){
        Logger.logDebug(LogType.EVENT,"mouseDragged [" + event.getX() + ", " + event.getY() + "]");

        VPoint now = new VPoint(event.getX(), event.getY());
        centerMoved = new VPoint(centerMoved.x + (now.x - lastPos.x), centerMoved.y + (now.y - lastPos.y));
        canvas.setTranslateX(centerMoved.x);
        canvas.setTranslateY(centerMoved.y);
        lastPos = now;
    }

    public void onMouseReleased(MouseEvent event){
        Logger.logDebug(LogType.EVENT,"mouseReleased");
    }

    private void mapSnapshot(boolean log, Object stamp){
        canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
        map.drawMap(canvas.getGraphicsContext2D());
        savePNG(log, stamp);
    }

    public void savePNG(boolean log, Object stamp){
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HHmmss");
        Date date = new Date();

        File saveFolder = new File("printscreen");
        if(!saveFolder.exists() && !saveFolder.isDirectory())
            saveFolder.mkdir();

        String stampString = stamp == null ? "" : " "+stamp.toString();
        File file = new File("printscreen\\"+dateFormat.format(date) + stampString +".png");
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

        if(log) {
            File logFile = new File("printscreen\\"+dateFormat.format(date) + stampString + ".txt");

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile))) {
                MapConfig.saveConfig(writer);
            } catch (IOException e) {
                e.printStackTrace();
                Logger.log("Printscreen Log failed!");
            }
        }
    }
}
