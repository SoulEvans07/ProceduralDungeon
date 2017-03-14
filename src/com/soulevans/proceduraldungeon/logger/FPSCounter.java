package com.soulevans.proceduraldungeon.logger;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class FPSCounter extends AnimationTimer {
    private final long[] frameTimes = new long[100];
    private int frameTimeIndex = 0 ;
    private boolean arrayFilled = false ;
    private double frameRate;

    @Override
    public void handle(long now) {
        long oldFrameTime = frameTimes[frameTimeIndex] ;
        frameTimes[frameTimeIndex] = now ;
        frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length ;
        if (frameTimeIndex == 0) {
            arrayFilled = true ;
        }
        if (arrayFilled) {
            long elapsedNanos = now - oldFrameTime ;
            long elapsedNanosPerFrame = elapsedNanos / frameTimes.length ;
            frameRate = 1_000_000_000.0 / elapsedNanosPerFrame ;
        }
    }

    public void drawFPS(GraphicsContext gc, double canvasHeight){
        gc.setFill(Color.BLACK);
        gc.fillText(String.format("[%.3f]", frameRate), 0, canvasHeight-2);
    }
}