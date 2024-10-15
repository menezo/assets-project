package com.menezo.assetsproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MainApp extends Application {

    double x, y = 0;

    private ConfigurableApplicationContext applicationContext;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        applicationContext = SpringApplication.run(Startup.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = loader.load();

        primaryStage.initStyle(StageStyle.UNDECORATED);

        root.setOnMousePressed(evt -> {
            x = evt.getSceneX();
            y = evt.getSceneY();
        });
        root.setOnMouseDragged(evt -> {
            primaryStage.setX(evt.getScreenX() - x);
            primaryStage.setY(evt.getScreenY() - y);
        });
        primaryStage.setScene(new Scene(root));
        //primaryStage.setMaximized(true);
        primaryStage.show();
    }

    @Override
    public void stop() {
        applicationContext.close();
    }
}
