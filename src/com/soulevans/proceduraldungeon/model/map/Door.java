package com.soulevans.proceduraldungeon.model.map;

import com.soulevans.proceduraldungeon.Game;
import com.soulevans.proceduraldungeon.model.entities.items.Item;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Door extends Tile{
    private boolean open;

    private static Image imageOpen = new Image("imgs/door_open.png"); // View
    private static Image imageClosed = new Image("imgs/door_closed.png");

    public Door(int x, int y){
        super(x, y);
        this.open = false;
    }


    public boolean interact(ArrayList<Item> inventory){
        boolean ret = open;

        // TODO: serach for keys
        open = true;
        return ret;
    }

    @Override
    public void drawTile(GraphicsContext gc) {
        gc.setFill(Color.valueOf("#e8e8e8"));
        gc.fillRect(x * Game.TILESIZE, y *Game.TILESIZE, Game.TILESIZE, Game.TILESIZE);
        if(open)
            gc.drawImage(imageOpen, x * Game.TILESIZE, y *Game.TILESIZE, Game.TILESIZE, Game.TILESIZE);
        else
            gc.drawImage(imageClosed, x * Game.TILESIZE, y *Game.TILESIZE, Game.TILESIZE, Game.TILESIZE);
    }
}
