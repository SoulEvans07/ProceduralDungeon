package com.soulevans.proceduraldungeon;

import com.soulevans.proceduraldungeon.logger.FPSCounter;
import com.soulevans.proceduraldungeon.misc.PerlinSquare;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {
    private static final int fps = 60;
    private GraphicsContext graphicsContext;
    private FPSCounter frameRateMeter;

    PerlinSquare perlinSquare;

    private Timeline frames = new Timeline(new KeyFrame(Duration.millis(1000 / Main.fps), new EventHandler<ActionEvent>() {
        private int tickCounter = 0;
        @Override
        public void handle(ActionEvent event) {
            drawLoop(graphicsContext);

            tickCounter++;
            if(tickCounter == 20){
                //Game.getInstance().tick();
                tickCounter = 0;
            }
        }
    }));

    private double width = 800;
    private double height = 800;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Hello World");
        Group root = new Group();

        Canvas canvas = new Canvas(width, height);
        graphicsContext = canvas.getGraphicsContext2D();

        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root, width, height));
        primaryStage.show();

//        canvas.setOnMousePressed(event -> Game.getInstance().onMousePressed(event));
//        canvas.setOnMouseDragged(event -> Game.getInstance().onMouseDragged(event));
//        canvas.setOnMouseReleased(event -> Game.getInstance().onMouseReleased(event));
//        primaryStage.getScene().setOnKeyPressed(event -> Game.getInstance().onKeyPressed(event));
//        primaryStage.getScene().setOnKeyReleased(event -> Game.getInstance().onKeyReleased(event));

        init(this.graphicsContext);
        frames.setCycleCount(Timeline.INDEFINITE);
        frameRateMeter = new FPSCounter();
        frameRateMeter.start();
        frames.play();
    }


    private void init(GraphicsContext gc){
        //gc.setFill(Color.BLACK);
        //gc.setStroke(Color.GRAY);

        perlinSquare = new PerlinSquare();
        //Game.getInstance().init(width, height);
    }

    int x = 0;
    int y = 0;

    private void drawLoop(GraphicsContext gc) {
//        clearCanvas(gc);

        //Game.getInstance().drawGame(gc);

        if(y < height)
            for(int i = 0; i < width; i++) {
                perlinSquare.drawNoise(gc, x, y);
                x++;
                if (x == width) {
                    x = 0;
                    y++;
                }
                if(y == height)
                    break;
            }

//        frameRateMeter.drawFPS(gc, height);
    }

    private void clearCanvas(GraphicsContext gc){
        gc.clearRect(0, 0, width, height);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
