package com.soulevans.proceduraldungeon.model.base;

import java.util.Objects;

public class MPoint {
    public int x,y;

    public MPoint(VPoint vp){
        this.x = (int)Math.floor(vp.x);
        this.y = (int)Math.floor(vp.y);
    }

    public MPoint(MPoint mp){
        this.x = mp.x;
        this.y = mp.y;
    }

    public MPoint(int _x, int _y){
        this.x = _x;
        this.y = _y;
    }

    public double dist(MPoint p){
        return Math.sqrt(Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2));
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(!(obj instanceof MPoint)) return false;

        MPoint point = (MPoint) obj;
        return (this.x == point.x) && (this.y == point.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString(){
        return "[" + x + ", " + y + "]";
    }
}
