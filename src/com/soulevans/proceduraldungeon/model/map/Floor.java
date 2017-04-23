package com.soulevans.proceduraldungeon.model.map;

import com.soulevans.proceduraldungeon.Game;
import com.soulevans.proceduraldungeon.model.entities.GameObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Floor extends Tile {
    private static Image image = new Image("imgs/floor.png");

    public Floor(int x, int y){
        this(x, y, null);
    }

    public Floor(int x, int y, GameObject entity){
        super(x, y);
        this.entity = entity;
    }

    public static void drawFloor(GraphicsContext gc, int x, int y){
        gc.drawImage(image, x * Game.TILESIZE, y *Game.TILESIZE, Game.TILESIZE, Game.TILESIZE);
    }

    @Override
    public void drawTile(GraphicsContext gc) {
//        gc.setFill(Color.valueOf("#e8e8e8"));
//        gc.fillRect(x * Game.TILESIZE, y *Game.TILESIZE, Game.TILESIZE, Game.TILESIZE);
        gc.drawImage(image, x * Game.TILESIZE, y *Game.TILESIZE, Game.TILESIZE, Game.TILESIZE);
    }

    @Override
    public String toString() {
        return "floor[" + x + ", " + y + "]";
    }
}
