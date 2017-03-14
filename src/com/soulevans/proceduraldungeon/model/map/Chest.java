package com.soulevans.proceduraldungeon.model.map;

import com.soulevans.proceduraldungeon.Game;
import com.soulevans.proceduraldungeon.logger.LogType;
import com.soulevans.proceduraldungeon.logger.Logger;
import com.soulevans.proceduraldungeon.model.entities.items.Item;
import com.soulevans.proceduraldungeon.model.entities.items.ItemEntity;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Chest extends Tile{
    private Item loot;
    private boolean open;

    private static Image imageOpen = new Image("img/chest_open.png"); // View
    private static Image imageClosed = new Image("img/chest_closed.png");

    public Chest(int x, int y, Item loot) {
        super(x, y);
        this.open = false;
        this.loot = loot;
    }

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
