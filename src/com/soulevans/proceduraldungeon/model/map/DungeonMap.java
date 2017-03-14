package com.soulevans.proceduraldungeon.model.map;

import com.soulevans.proceduraldungeon.model.base.MPoint;
import com.soulevans.proceduraldungeon.model.base.VPoint;
import com.soulevans.proceduraldungeon.model.entities.GameObject;
import com.soulevans.proceduraldungeon.model.living.Living;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DungeonMap {
    private int width;
    private int height;

    public Map<MPoint, Tile> map;
    public ArrayList<GameObject> entities;

    public DungeonMap(int _width, int _height){
        width = _width;
        height = _height;

        map = new HashMap<>();
        entities = new ArrayList<>();

        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                Tile tmp = new Floor(x, y);
                map.put(new MPoint(x, y), tmp);
            }
        }
    }

    public Tile getTile(int x, int y){
        return map.get(new MPoint(x, y));
    }

    public void addGameObject(int x, int y, GameObject entity){
        entities.add(entity);
        Tile tile = this.getTile(x, y);
        entity.setPos(tile);
        tile.setEntity(entity);
    }

    public void removeDead(){
        for(int i = entities.size()-1; i >= 0; i--){
            if(entities.get(i) instanceof Living && ((Living) entities.get(i)).getHP() < 0){
                entities.get(i).getPos().setEntity(null);
                entities.remove(i);
            }
        }
    }

    public ArrayList<VPoint> detect(Living viewer){
        int viewRange = viewer.getViewRange();

        // TODO: fill list with the correct info .Soul
        return new ArrayList<>();
    }

    //    View    ##########################################################################################################

    public void drawMap(GraphicsContext gc){
        // # First Layer - Tiles
        for(Map.Entry<MPoint, Tile> entry : map.entrySet()) {
            Tile tile = entry.getValue();
            tile.drawTile(gc);
        }

        // # Second Layer - Entities
        for(int i = 0; i < entities.size(); i++){
            entities.get(i).drawObject(gc);
        }
    }
}
