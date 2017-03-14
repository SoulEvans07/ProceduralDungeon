package com.soulevans.proceduraldungeon.model.map;

import com.soulevans.proceduraldungeon.model.entities.GameObject;
import javafx.scene.canvas.GraphicsContext;

public abstract class Tile {

    public int x;
    public int y;

    protected GameObject entity;

    public void setEntity(GameObject entity){
        this.entity = entity;
    }

    public GameObject getEntity(){
        return entity;
    }

    public abstract void drawTile(GraphicsContext gc); // View
}
