package com.soulevans.proceduraldungeon.model.entities.items;

import com.soulevans.proceduraldungeon.Game;
import com.soulevans.proceduraldungeon.model.entities.GameObject;
import com.soulevans.proceduraldungeon.model.map.Tile;
import javafx.scene.canvas.GraphicsContext;

public class ItemEntity extends GameObject{
    private Item item;

    public ItemEntity(Tile pos, Item item) {
        super(pos);
        this.item = item;
    }

    public Item getItem(){
        return item;
    }

    @Override
    public void drawObject(GraphicsContext gc) {
        gc.drawImage(item.getImage(),pos.x * Game.TILESIZE, pos.y *Game.TILESIZE, Game.TILESIZE, Game.TILESIZE);
    }

    @Override
    public void tick() {

    }
}
