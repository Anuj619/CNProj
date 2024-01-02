package com.cnproj;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.Socket;

public class Client extends Application {
    private static Socket socket;
    private static DataInputStream in;
    private static Stage stage;
    private static ImageView imageView;


    @Override
    public void start(Stage primaryStage) {
        try {
            socket = new Socket("127.0.0.1", 5000);
            in = new DataInputStream(socket.getInputStream());

            stage = primaryStage;
            stage.setTitle("Image Viewer");

            // Create an ImageView for displaying images
            imageView = new ImageView();
            imageView.setFitWidth(640);
            imageView.setFitHeight(480);

            // Create a Group
            Group root = new Group(imageView);

            // Create a Scene with the Group and set background color
            Scene scene = new Scene(root, 640, 480, Color.BLACK);

            // Set the Scene to the Stage
            stage.setScene(scene);

            // Show the Stage
            stage.show();

            // Receive and display images continuously
            new Thread(() -> {
                while (true) {
                    receiveAndDisplayImage();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void receiveAndDisplayImage() {
        try {
            int imageSize = in.readInt();
            byte[] imageBytes = new byte[imageSize];

            // Read image bytes from the server
            in.readFully(imageBytes);

            // Convert byte array to BufferedImage
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            BufferedImage bufferedImage = ImageIO.read(bis);

            // Convert BufferedImage to JavaFX Image
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);

            // Display the image in the ImageView
            Platform.runLater(() -> imageView.setImage(image));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
