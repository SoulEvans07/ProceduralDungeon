package com.soulevans.proceduraldungeon.misc;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class PulseSquare {
    private double width, height;
    private double index = 1;
    private double speed = 1;
    private double opacity = 1;
    private double darken;
    private double hue = 0;
    private double diff;

    public PulseSquare(double w, double h){
        this.width = w;
        this.height = h;

        darken = 1 / w;
        diff = 270 / w;
    }

    public void drawPulse(GraphicsContext gc){
        gc.setFill(Color.hsb(hue, opacity, 1));
        gc.fillRect(width/2 - index/2, height/2 - index/2, index, index);

        if(index > width || index < 0)
            speed *= -1;
        if(opacity+darken < 0 || opacity+darken > 1)
            darken *= -1;
        if(hue < 0 || hue > 270)
            diff *= -1;

        index += speed;
        opacity += darken;
        hue += diff;
    }
}
