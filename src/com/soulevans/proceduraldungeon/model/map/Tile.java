package com.soulevans.proceduraldungeon.model.map;

import com.soulevans.proceduraldungeon.Game;
import com.soulevans.proceduraldungeon.model.entities.GameObject;
import com.soulevans.proceduraldungeon.model.entities.items.Item;
import com.soulevans.proceduraldungeon.model.entities.items.ItemEntity;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class Tile {
    // View
    protected static Image image;

    // Model
    public int x;
    public int y;

    public Tile(int x, int y){
        this.x = x;
        this.y = y;
    }

    protected GameObject entity;

    // Control
    public Item pickUpItem(){
        Item returnItem = null;
        if(entity != null && entity instanceof ItemEntity){
            returnItem = ((ItemEntity) entity).getItem();
            Game.getInstance().getMap().removeGameObject(this.entity);
            this.entity = null;
        }
        return returnItem;
    }

    // Model
    public void setEntity(GameObject entity){
        this.entity = entity;
    }

    public void removeEntity(){
        this.entity = null;
    }

    public GameObject getEntity(){
        return entity;
    }

    // View
    public abstract void drawTile(GraphicsContext gc);
}
