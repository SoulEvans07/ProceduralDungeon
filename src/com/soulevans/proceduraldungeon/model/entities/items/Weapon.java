package com.soulevans.proceduraldungeon.model.entities.items;

import com.soulevans.proceduraldungeon.model.damage.Damage;
import com.soulevans.proceduraldungeon.model.damage.DamageType;
import com.soulevans.proceduraldungeon.model.entities.living.Living;

public abstract class Weapon {
    protected DamageType damageType;
    protected int baseDamage;

    public Weapon(DamageType type, int baseDamage){
        this.damageType = type;
        this.baseDamage = baseDamage;
    }

    public abstract Damage getDamage(Living user);
}
