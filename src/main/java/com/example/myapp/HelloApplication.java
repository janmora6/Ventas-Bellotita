package com.example.myapp;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.bouncycastle.jcajce.provider.digest.BCMessageDigest;
import org.bouncycastle.util.encoders.Hex;
import org.mindrot.jbcrypt.BCrypt;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;

public class HelloApplication extends Application {
    private static Stage primaryStage;
    private static Properties databaseProperties;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        loadDatabaseProperties();
        HelloApplication.primaryStage = primaryStage;
        primaryStage.setTitle("Login Application");

        // Create UI components
        Label usernameLabel = new Label("Username:");
        Label passwordLabel = new Label("Password:");
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");
        Label messageLabel = new Label();

        // Set up the layout
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, loginButton, messageLabel);

        // Event handler for the login button
        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String enteredPassword = passwordField.getText();

            // Fetch the hashed password from the database
            String hashedPasswordFromDB = getHashedPasswordFromDB(username);

            if (hashedPasswordFromDB != null) {
                // Verify the entered password against the hashed password
                if (BCrypt.checkpw(enteredPassword, hashedPasswordFromDB)) {
                    messageLabel.setText("Login successful!");

                    // Open HelloController upon successful login
                    openHelloController();
                } else {
                    messageLabel.setText("Login failed. Incorrect password.");
                }
            } else {
                messageLabel.setText("Login failed. User not found.");
            }
        });

        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String getHashedPasswordFromDB(String username) {
        String dbUrl = databaseProperties.getProperty("db.url");
        String dbUser = databaseProperties.getProperty("db.username");
        String dbPassword = databaseProperties.getProperty("db.password");

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            String query = "SELECT userpassword FROM userlogin WHERE username=?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("userpassword");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void openHelloController() {
        HelloController helloController = new HelloController();
        helloController.start(primaryStage);
    }

    private void loadDatabaseProperties() {
        databaseProperties = new Properties();
        try (InputStream inputStream = getClass().getResourceAsStream("/database.properties")) {
            databaseProperties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
