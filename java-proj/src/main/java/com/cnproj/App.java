package com.cnproj;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class App {
    private ServerSocket server;
    private Socket client;
    private DataOutputStream out;

    public App(int port) {
        try {
            Webcam webcam = Webcam.getDefault();
            if (webcam != null) {
                System.out.println("Webcam: " + webcam.getName());
            } else {
                System.out.println("No webcam detected");
                return;
            }

            webcam.setViewSize(WebcamResolution.VGA.getSize());
            System.out.print(WebcamResolution.VGA.getSize());
            webcam.open();
            server = new ServerSocket(port);
            System.out.println("Server Started. Waiting For Client");
            client = server.accept();
            System.out.println("Client connected");
            out = new DataOutputStream(client.getOutputStream());

            // Capture and send images every 500ms
            while (true) {
                captureAndSendImage(webcam);
                Thread.sleep(50); // Adjust the sleep duration as needed
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    private void captureAndSendImage(Webcam webcam) {
        try {
            System.out.println("Capturing image...");
            BufferedImage buf = webcam.getImage();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(buf, "jpg", byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            out.writeInt(imageBytes.length);
            out.write(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeResources() {
        try {
            if (out != null) {
                out.close();
            }
            if (client != null) {
                client.close();
            }
            if (server != null) {
                server.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        App server = new App(5000);
    }
}
