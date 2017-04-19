package com.soulevans.proceduraldungeon.model.base;

public enum Dir {
    UP (new MPoint(0, -1)),
    RIGHT (new MPoint(1, 0)),
    DOWN (new MPoint(0, 1)),
    LEFT (new MPoint(-1, 0));

    public MPoint value;

    Dir(MPoint mPoint) {
        value = mPoint;
    }

    public MPoint mult(int val){
        return value.mult(val);
    }
}
