package com.soulevans.proceduraldungeon.model.entities;

import com.soulevans.proceduraldungeon.model.base.VPoint;
import com.soulevans.proceduraldungeon.model.map.Tile;
import javafx.scene.canvas.GraphicsContext;

public abstract class GameObject {
    // TODO: make size static so its settable once and used in the entire project .Soul
    private static double size; // View
    protected Tile pos;

    protected boolean obstacle = false;

    public GameObject(Tile pos){
        this.pos = pos;
    }

    public void setPos(Tile pos){
        this.pos = pos;
    }

    public Tile getPos(){
        return pos;
    }

    public VPoint getTilePos(){
        return new VPoint(this.pos.x * size, this.pos.y * size);
    } // View

    public static void setSize(double _size){ // View
        size = _size;
    }

    public double getSize(){
        return size;
    }   // View

    public abstract void drawObject(GraphicsContext gc); // View

    public abstract void tick();
}
