package com.soulevans.proceduraldungeon.model.map;

import com.soulevans.proceduraldungeon.model.base.MPoint;
import com.soulevans.proceduraldungeon.model.base.VPoint;
import com.soulevans.proceduraldungeon.model.entities.GameObject;
import com.soulevans.proceduraldungeon.model.entities.living.Living;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DungeonMap {
    public Map<MPoint, Tile> map;
    public ArrayList<GameObject> entities;
    public int mapWidth;
    public int mapHeight;

    public DungeonMap(){
        map = new HashMap<>();
        entities = new ArrayList<>();
    }

    public DungeonMap(Map<MPoint, Tile> _map){
        this.map = _map;
        this.entities = new ArrayList<>();
    }

    public void clearMap(){
        map.clear();
        entities.clear();
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

    public void removeGameObject(GameObject entity){
        entity.getPos().removeEntity();
        entities.remove(entity);
    }

    public void removeDead(){
        for(int i = entities.size()-1; i >= 0; i--){
            if(entities.get(i) instanceof Living && ((Living) entities.get(i)).getHP() < 0){
                entities.get(i).getPos().removeEntity();
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
