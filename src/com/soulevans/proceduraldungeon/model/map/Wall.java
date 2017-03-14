package com.soulevans.proceduraldungeon.model.map;

import com.soulevans.proceduraldungeon.Game;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Wall extends Tile {

    @Override
    public void drawTile(GraphicsContext gc) {
        gc.setFill(Color.valueOf("#666666"));
        gc.fillRect(x * Game.TILESIZE, y *Game.TILESIZE, Game.TILESIZE, Game.TILESIZE);
    }
}
