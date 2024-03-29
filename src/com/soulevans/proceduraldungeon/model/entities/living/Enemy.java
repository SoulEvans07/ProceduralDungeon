package com.soulevans.proceduraldungeon.model.entities.living;

import com.soulevans.proceduraldungeon.Game;
import com.soulevans.proceduraldungeon.model.entities.items.Sword;
import com.soulevans.proceduraldungeon.model.map.Tile;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Enemy extends Living {
    private static Image image = new Image("imgs/enemy.png");

    public Enemy(Tile pos, int hp){
        super(pos, hp);
        this.weapon = new Sword(70);
    }

    @Override
    public void drawObject(GraphicsContext gc){ // View
        drawHealthBar(gc);

        gc.drawImage(image, pos.x * Game.TILESIZE, pos.y *Game.TILESIZE, Game.TILESIZE, Game.TILESIZE);
    }

    public void drawHealthBar(GraphicsContext gc){
        gc.setFill(Color.RED);
        gc.fillRect(pos.x * Game.TILESIZE, pos.y * Game.TILESIZE - Game.TILESIZE*0.4, Game.TILESIZE, Game.TILESIZE*0.2);
        gc.setFill(Color.GREEN);
        gc.fillRect(pos.x * Game.TILESIZE, pos.y * Game.TILESIZE - Game.TILESIZE*0.4, Game.TILESIZE * ((double)healthPoints / (double)baseHP), Game.TILESIZE*0.2);
    }

    @Override
    public void tick() {
        // TODO: [LATER] implement Enemy AI
    }

    @Override
    public String toString() {
        return "Enemy::" + pos.toString();
    }
}
