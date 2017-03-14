package com.soulevans.proceduraldungeon.model.base;

import java.util.ArrayList;

public class Shape {
    public ArrayList<VPoint> points;

    public Shape(){
        points = new ArrayList<>();
    }

    public void addPoint(VPoint point){
        points.add(point);
    }
}
