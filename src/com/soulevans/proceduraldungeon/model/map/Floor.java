package com.soulevans.proceduraldungeon.model.map;

import com.soulevans.proceduraldungeon.Game;
import com.soulevans.proceduraldungeon.model.entities.GameObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Floor extends Tile {

    public Floor(int x, int y){
        this(x, y, null);
    }

    public Floor(int x, int y, GameObject entity){
        super(x, y);
        this.entity = entity;
    }

    @Override
    public void drawTile(GraphicsContext gc) {
        gc.setFill(Color.valueOf("#e8e8e8"));
        gc.fillRect(x * Game.TILESIZE, y *Game.TILESIZE, Game.TILESIZE, Game.TILESIZE);
    }

    @Override
    public String toString() {
        return "floor[" + x + ", " + y + "]";
    }
}
