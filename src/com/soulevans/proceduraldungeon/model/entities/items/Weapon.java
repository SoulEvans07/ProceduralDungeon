package com.soulevans.proceduraldungeon.model.entities.items;

import com.soulevans.proceduraldungeon.model.damage.Damage;
import com.soulevans.proceduraldungeon.model.damage.DamageType;
import com.soulevans.proceduraldungeon.model.entities.living.Living;

// TODO: after Tool, link this to it.
public abstract class Weapon extends Item{
    protected DamageType damageType;
    protected int baseDamage;

    public Weapon(String name, DamageType type, int baseDamage){
        this.name = name;
        this.damageType = type;
        this.baseDamage = baseDamage;
    }

    public abstract Damage getDamage(Living user);

    @Override
    public String toString(){
        return this.name + "::" + this.getClass().getSimpleName() + "::" + baseDamage + " " + damageType.name().toLowerCase();
    }
}
