package com.soulevans.proceduraldungeon.model.map;

import com.soulevans.proceduraldungeon.Game;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class EntryStair extends Tile{
    private static Image image = new Image("imgs/entry.png");

    public EntryStair(int x, int y){
        super(x, y);
    }

    @Override
    public void drawTile(GraphicsContext gc) {
        gc.drawImage(image, x * Game.TILESIZE, y *Game.TILESIZE, Game.TILESIZE, Game.TILESIZE);
    }
}
