package com.soulevans.proceduraldungeon.model.map;

import com.soulevans.proceduraldungeon.Game;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Door extends Tile{

    @Override
    public void drawTile(GraphicsContext gc) {
        gc.setFill(Color.valueOf("#6d472f"));
        gc.fillRect(x * Game.TILESIZE, y *Game.TILESIZE, Game.TILESIZE, Game.TILESIZE);
    }
}
