package com.soulevans.proceduraldungeon.misc;

import com.soulevans.proceduraldungeon.model.base.Shape;
import com.soulevans.proceduraldungeon.model.base.VPoint;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class DrawBoard {
    private ArrayList<Shape> shapes;
    private Shape tempShape;
    ArrayList<String> pressed = new ArrayList<>();

    public DrawBoard(){
        shapes = new ArrayList<>();
        tempShape = new Shape();
    }


    public void onKeyPressed(KeyEvent event){
        if(!pressed.contains(event.getCode().getName())) {
            pressed.add(event.getCode().getName());
            System.out.println(pressed);
        }
    }

    public void onKeyReleased(KeyEvent event){
        if(pressed.size() == 2 && pressed.get(0).equals("Ctrl") && pressed.get(1).equals("Z")){
            if(tempShape.points.size() > 0)
                tempShape.points.clear();
            else if (shapes.size() > 0)
                shapes.remove(shapes.size() -1);
        }
        pressed.remove(event.getCode().getName());
        System.out.println(pressed);
    }

    public void onMouseDragged(MouseEvent event){
        tempShape.addPoint(new VPoint(event.getX(), event.getY()));
    }

    public void onMousePressed(MouseEvent event){
        tempShape.addPoint(new VPoint(event.getX(), event.getY()));
    }

    public void onMouseReleased(MouseEvent event){
        shapes.add(tempShape);
        tempShape = new Shape();
    }

    public void drawShapes(GraphicsContext gc){
        gc.setStroke(Color.BLACK);
        drawShape(gc, tempShape);

        for(int i = 0; i < shapes.size(); i++){
            drawShape(gc, shapes.get(i));
        }
    }

    private void drawShape(GraphicsContext gc, Shape shape){
        gc.setLineWidth(3);
        int n = shape.points.size();
        double xPoints[] = new double[n];
        double yPoints[] = new double[n];

        for(int i = 0; i < shape.points.size(); i++){
            xPoints[i] = shape.points.get(i).x;
            yPoints[i] = shape.points.get(i).y;
        }
        gc.strokePolyline(xPoints, yPoints, n);
    }
}
