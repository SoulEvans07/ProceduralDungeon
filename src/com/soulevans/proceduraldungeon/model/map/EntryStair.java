package com.soulevans.proceduraldungeon.model.map;

import com.soulevans.proceduraldungeon.Game;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class EntryStair extends Tile{
    private static Image image = new Image("imgs/door_open.png"); // View

    public EntryStair(int x, int y){
        super(x, y);
    }

    @Override
    public void drawTile(GraphicsContext gc) {
        gc.setFill(Color.valueOf("#e8e8e8"));
        gc.fillRect(x * Game.TILESIZE, y *Game.TILESIZE, Game.TILESIZE, Game.TILESIZE);

        gc.setFill(Color.GREEN);
        gc.fillRect(x * Game.TILESIZE, y *Game.TILESIZE, Game.TILESIZE, Game.TILESIZE);
    }
}
