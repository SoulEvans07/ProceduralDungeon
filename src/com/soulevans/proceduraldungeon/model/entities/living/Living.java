package com.soulevans.proceduraldungeon.model.entities.living;

import com.soulevans.proceduraldungeon.Game;
import com.soulevans.proceduraldungeon.model.base.VPoint;
import com.soulevans.proceduraldungeon.model.damage.Damage;
import com.soulevans.proceduraldungeon.model.damage.DamageType;
import com.soulevans.proceduraldungeon.model.entities.GameObject;
import com.soulevans.proceduraldungeon.model.entities.items.Weapon;
import com.soulevans.proceduraldungeon.logger.Logger;
import com.soulevans.proceduraldungeon.model.map.Tile;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

public abstract class Living extends GameObject {
    protected int viewRange;
    protected ArrayList<VPoint> visible;

    protected int healthPoints;
    protected int baseHP;

    protected Weapon weapon;

    public Living(Tile pos, int hp) {
        super(pos);
        this.baseHP = hp;
        this.healthPoints = this.baseHP;
    }

    public int getBaseHP(){
        return baseHP;
    }

    public int getHP(){
        return healthPoints;
    }

    public void lookAround(){
        visible = Game.getInstance().getMap().detect(this);
    }

    protected void attack(Living opponent){
        Damage dmg;
        if(weapon != null)
            dmg = weapon.getDamage(this);
        else
            dmg = new Damage(DamageType.PHYSICAL, 10);

        opponent.hit(dmg);
        Logger.log(this + " hit " + opponent + " with " + dmg);
        Logger.log("\t" + this + " HP: " + this.getHP());
        Logger.log("\t" + opponent + " HP: " + opponent.getHP());
    }

    private void hit(Damage damage){
        this.healthPoints -= damage.getValue();
    }

    protected void step(Tile tile){
        Tile prev = this.pos;
        pos = tile;
        if(prev != null)
            prev.setEntity(null);
        if(tile != null)
            tile.setEntity(this);
    }

    public int getViewRange() {
        return viewRange;
    }

    @Override
    public abstract void drawObject(GraphicsContext gc);

    @Override
    public abstract void tick();
}
