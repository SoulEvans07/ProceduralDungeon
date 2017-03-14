package com.soulevans.proceduraldungeon.model.damage;

public class Damage {
    private DamageType type;
    private int value;

    public Damage(DamageType type, int value){
        this.type = type;
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    public DamageType getType(){
        return type;
    }

    @Override
    public String toString(){
        return type.name() + "["+value+"]";
    }
}
