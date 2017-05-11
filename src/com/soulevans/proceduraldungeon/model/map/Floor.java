package com.soulevans.proceduraldungeon.model.map;

import com.soulevans.proceduraldungeon.Game;
import com.soulevans.proceduraldungeon.model.entities.GameObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Floor extends Tile {
    // View
    private static Image image = new Image("imgs/floor.png");

    public Floor(int x, int y){
        this(x, y, null);
    }

    public Floor(int x, int y, GameObject entity){
        super(x, y);
        this.entity = entity;
    }

    // View
    public static void drawFloor(GraphicsContext gc, int x, int y){
        gc.drawImage(image, x * Game.TILESIZE, y *Game.TILESIZE, Game.TILESIZE, Game.TILESIZE);
    }

    @Override
    public void drawTile(GraphicsContext gc) {
        gc.drawImage(image, x * Game.TILESIZE, y *Game.TILESIZE, Game.TILESIZE, Game.TILESIZE);
    }

    @Override
    public String toString() {
        return "floor[" + x + ", " + y + "]";
    }
}
