package com.soulevans.proceduraldungeon.model.map;

import com.soulevans.proceduraldungeon.Game;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Wall extends Tile {

    private static Image image = new Image("imgs/wall.png");

    public Wall(int x, int y) {
        super(x, y);
    }

    @Override
    public void drawTile(GraphicsContext gc) {
//        gc.setFill(Color.valueOf("#666666"));
//        gc.fillRect(x * Game.TILESIZE, y *Game.TILESIZE, Game.TILESIZE, Game.TILESIZE);
        gc.drawImage(image, x * Game.TILESIZE, y *Game.TILESIZE, Game.TILESIZE, Game.TILESIZE);
    }
}
