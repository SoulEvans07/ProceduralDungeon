package com.soulevans.proceduraldungeon.model.map;

import com.soulevans.proceduraldungeon.Game;
import com.soulevans.proceduraldungeon.model.entities.items.Item;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Door extends Tile{

    private boolean open;

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
        if(open)
            gc.setFill(Color.valueOf("#aa8f7d"));
        else
            gc.setFill(Color.valueOf("#6d472f"));
        gc.fillRect(x * Game.TILESIZE, y *Game.TILESIZE, Game.TILESIZE, Game.TILESIZE);
    }
}
