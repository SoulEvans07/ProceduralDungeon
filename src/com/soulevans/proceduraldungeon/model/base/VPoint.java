package com.soulevans.proceduraldungeon.model.base;

import java.util.Objects;

public class VPoint {
    public double x,y;

    public VPoint(double _x, double _y){
        this.x = _x;
        this.y = _y;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(!(obj instanceof VPoint)) return false;

        VPoint point = (VPoint) obj;
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
