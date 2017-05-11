package com.soulevans.proceduraldungeon.model.map;

import com.soulevans.proceduraldungeon.Game;
import com.soulevans.proceduraldungeon.logger.LogType;
import com.soulevans.proceduraldungeon.logger.Logger;
import com.soulevans.proceduraldungeon.model.entities.items.Item;
import com.soulevans.proceduraldungeon.model.entities.items.ItemEntity;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class Chest extends Tile{
    // Model
    private Item loot;
    private boolean open;

    // View
    private static Image imageOpen = new Image("imgs/chest_open.png"); // View
    private static Image imageClosed = new Image("imgs/chest_closed.png");

    public Chest(int x, int y, Item loot) {
        super(x, y);
        this.open = false;
        this.loot = loot;
    }

    // Control
    // TODO: [LATER] move to TileEntity abstract parent class
    public boolean interact(ArrayList<Item> inventory){
        boolean ret = open;

        if (!open) {
            open = true;

            if (this.entity != null)
                Logger.log(LogType.ERROR, "entity swapped with item, oops");
            Game.getInstance().getMap().addGameObject(x, y, new ItemEntity(this, loot));
            loot = null;
        }
        return ret;
    }

    // View
    @Override
    public void drawTile(GraphicsContext gc) {
        Floor.drawFloor(gc, x, y);
        if(open)
            gc.drawImage(imageOpen, x * Game.TILESIZE, y *Game.TILESIZE, Game.TILESIZE, Game.TILESIZE);
        else
            gc.drawImage(imageClosed, x * Game.TILESIZE, y *Game.TILESIZE, Game.TILESIZE, Game.TILESIZE);
    }
}
