package com.soulevans.proceduraldungeon.misc;

import com.soulevans.proceduraldungeon.model.base.PerlinNoise;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class PerlinSquare {
    private float xo, yo, zo;
    private PerlinNoise noise;

    public PerlinSquare(){
        noise = new PerlinNoise();
        xo = yo = zo = 0;
    }

    public void offset(float x, float y, float z) {
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }


    public void drawPerlin(GraphicsContext gc, double _x, double _y, double _w, double _h){
        gc.setStroke(Color.BLACK);
        double scale = 0.01;

        for (double y = _y; y < (_y + _h); y+=scale){
            for (double x = _x; x < (_x + _w); x+=scale){
//                gc.strokeOval(x, y, 1, 1);
                double gray = (1 + noise.noise((float) x, (float) y, 0)) / 2;
//                double gray = (1 + noise.turbulentNoise((float) x, (float) y, 0, 12)) / 2;
//                double gray = (1 + noise.smoothNoise((float) x, (float) y, 0, 12)) / 2;
                gc.setStroke(Color.gray(gray));
//                System.out.println(noise.noise((float) x, (float) y, 0));
                gc.strokeRect(x/scale, y/scale, 1, 1);
            }
        }
    }

    public double drawNoise(GraphicsContext gc, double x, double y, double size){
        double scale = 0.01;
        double gray = (1 + noise.noise((float) (x*scale)+xo, (float) (y*scale)+yo, 0)) / 2;
        gc.setStroke(Color.gray(gray));
        gc.setFill(Color.gray(gray));
        gc.fillRect(x, y, size, size);
        return gray;
    }
}
