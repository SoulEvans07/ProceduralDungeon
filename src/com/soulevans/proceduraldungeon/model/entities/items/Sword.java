package com.soulevans.proceduraldungeon.model.entities.items;

import com.soulevans.proceduraldungeon.model.damage.Damage;
import com.soulevans.proceduraldungeon.model.damage.DamageType;
import com.soulevans.proceduraldungeon.model.entities.living.Living;

import java.util.Random;

public class Sword extends Weapon {
    public Sword(int baseDamage) {
        super(DamageType.PHYSICAL, baseDamage);
    }

    @Override
    public Damage getDamage(Living user){
        // TODO: calculate damage from user and weapon
        Random random = new Random();
        int value = (int) (baseDamage *0.9) + random.nextInt((int) (baseDamage*0.1));

        return new Damage(damageType, value);
    }
}
