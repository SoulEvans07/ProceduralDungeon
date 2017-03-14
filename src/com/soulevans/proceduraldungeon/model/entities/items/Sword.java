package com.soulevans.proceduraldungeon.model.entities.items;

import com.soulevans.proceduraldungeon.model.damage.Damage;
import com.soulevans.proceduraldungeon.model.damage.DamageType;
import com.soulevans.proceduraldungeon.model.entities.living.Living;
import javafx.scene.image.Image;

import java.util.Random;

public class Sword extends Weapon {
    private static Image image = new Image("img/ironsword.png");

    public Sword(int baseDamage){
        this("Sword", baseDamage);
    }

    public Sword(String name, int baseDamage) {
        super(name, DamageType.PHYSICAL, baseDamage);
    }

    @Override
    public Damage getDamage(Living user){
        // TODO: calculate damage from user and weapon
        Random random = new Random();
        int value = (int) (baseDamage *0.9) + random.nextInt((int) (baseDamage*0.1));

        return new Damage(damageType, value);
    }

    @Override
    public Image getImage() {
        return Sword.image;
    }
}
