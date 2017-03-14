package com.soulevans.proceduraldungeon.model.entities.items;

import com.soulevans.proceduraldungeon.model.entities.GameObject;
import com.soulevans.proceduraldungeon.model.map.Tile;
import javafx.scene.canvas.GraphicsContext;

public class Item extends GameObject{

    public Item(Tile pos) {
        super(pos);
    }

    @Override
    public void drawObject(GraphicsContext gc) {

    }

    @Override
    public void tick() {

    }
}
