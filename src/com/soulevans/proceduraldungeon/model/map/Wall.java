package com.soulevans.proceduraldungeon.model.map;

import com.soulevans.proceduraldungeon.Game;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Wall extends Tile {

    private static Image image = new Image("imgs/wall.png");

    public Wall(int x, int y) {
        super(x, y);
    }

    // TODO: step on function that prevents stepping on wall

    @Override
    public void drawTile(GraphicsContext gc) {
        gc.drawImage(image, x * Game.TILESIZE, y *Game.TILESIZE, Game.TILESIZE, Game.TILESIZE);
    }
}
