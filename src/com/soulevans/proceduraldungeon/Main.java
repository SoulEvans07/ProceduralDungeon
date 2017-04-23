package com.soulevans.proceduraldungeon;

import com.soulevans.proceduraldungeon.logger.FPSCounter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {
    private static final int fps = 60;
    private GraphicsContext graphicsContext;
    private Canvas canvas;
    private FPSCounter frameRateMeter;

    private Timeline frames = new Timeline(new KeyFrame(Duration.millis(1000 / Main.fps), new EventHandler<ActionEvent>() {
        private int tickCounter = 0;
        @Override
        public void handle(ActionEvent event) {
            drawLoop(graphicsContext);

            tickCounter++;
            if(tickCounter == 20){
                tickCounter = 0;
            }
        }
    }));

    private double width = 592;
    private double height = 592;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Procedural Dungeon");
        Group root = new Group();

        canvas = new Canvas(width, height);
        graphicsContext = canvas.getGraphicsContext2D();

        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root, width, height));
        primaryStage.show();

        primaryStage.getScene().setOnMousePressed(event -> Game.getInstance().onMousePressed(event));
        primaryStage.getScene().setOnMouseDragged(event -> Game.getInstance().onMouseDragged(event));
        primaryStage.getScene().setOnMouseReleased(event -> Game.getInstance().onMouseReleased(event));
        primaryStage.getScene().setOnKeyPressed(event -> Game.getInstance().onKeyPressed(event));
        primaryStage.getScene().setOnKeyReleased(event -> Game.getInstance().onKeyReleased(event));
        primaryStage.getScene().setOnScroll(event -> Game.getInstance().onScroll(event));

        init(this.graphicsContext);
        frames.setCycleCount(Timeline.INDEFINITE);
        frameRateMeter = new FPSCounter();
        frameRateMeter.start();
        frames.play();
    }

    private void init(GraphicsContext gc){
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.GRAY);

        Game.getInstance().init(width, height, gc.getCanvas());
    }

    private void drawLoop(GraphicsContext gc) {
        clearCanvas(gc);

        Game.getInstance().drawGame(gc);

        //frameRateMeter.drawFPS(gc, height);
    }

    private void clearCanvas(GraphicsContext gc){
        gc.clearRect(0, 0, width, height);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
