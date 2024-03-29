package com.soulevans.proceduraldungeon.model.entities.living;

import com.soulevans.proceduraldungeon.Game;
import com.soulevans.proceduraldungeon.model.base.MPoint;
import com.soulevans.proceduraldungeon.model.entities.GameObject;
import com.soulevans.proceduraldungeon.model.entities.items.ItemEntity;
import com.soulevans.proceduraldungeon.model.entities.items.Sword;
import com.soulevans.proceduraldungeon.model.map.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;

public class Player extends Living {
    private static Image image = new Image("imgs/link.png");

    public Player(Tile pos, int hp){
        super(pos, hp);
        this.weapon = new Sword(100);
    }

    @Override
    public void drawObject(GraphicsContext gc){ // View
        gc.drawImage(image, pos.x * Game.TILESIZE, pos.y *Game.TILESIZE, Game.TILESIZE, Game.TILESIZE);
    }

    // Control
    public void onKeyReleased(KeyEvent event){
        String key = event.getCode().getName();

        if(!"WASD".toLowerCase().contains(key.toLowerCase())) {
            return;
        }

        MPoint dir = getKeyDir(key);

        Tile tile = Game.getInstance().getMap().getTile(pos.x + dir.x, pos.y + dir.y);
        if(tile == null)
            // empty cell, no action needed
            return;

        GameObject entity = tile.getEntity();
        if(entity == null){

            // TODO: make TileEntity parent class
            if(tile instanceof Door){
                boolean go = ((Door) tile).interact(this.inventory);
                if(go)
                    this.step(tile);
            }
            if(tile instanceof Chest){
                boolean go = ((Chest) tile).interact(this.inventory);
                if(go)
                    this.step(tile);
            }
            if(tile instanceof ExitStair){
                this.step(tile);
                Game.getInstance().nextLevel();
            }

            if(tile instanceof Floor)
                this.step(tile);
        } else {
            if(entity instanceof Enemy){
                this.attack((Enemy) entity);
            }
            if(entity instanceof ItemEntity){
                this.inventory.add(tile.pickUpItem());

                this.step(tile);
                // TODO: give Living inventory
            }
        }
    }

    // TODO: use Dir class
    private MPoint getKeyDir(String key){
        MPoint dir = new MPoint(0, 0);
        switch (key){
            case "W": dir.y = -1;
                break;
            case "S": dir.y = 1;
                break;
            case "D": dir.x = 1;
                break;
            case "A": dir.x = -1;
                break;
        }
        return dir;
    }

    @Override
    public void tick(){
        // poison wears off, etc
    }

    @Override
    public String toString() {
        return "Player::" + pos.toString();
    }
}
