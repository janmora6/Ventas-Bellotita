package com.example.myapp;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import java.util.Optional;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;


public class HelloController extends Application {
    private Connection conn;
    private TableView<UserData> tableView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Load database properties from the file
        Properties properties = loadDatabaseProperties();

        // Connect to the database using the loaded properties
        connectToDatabase(properties);

        primaryStage.setTitle("My App");

        TabPane tabPane = new TabPane();
        tabPane.getTabs().add(createTab1());
        tabPane.getTabs().add(createTab2());
        tabPane.getTabs().add(createTab3());
        tabPane.getTabs().add(createTab4());

        Background background = new Background(new BackgroundFill(javafx.scene.paint.Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY));
        tabPane.setBackground(background);

        Scene scene = new Scene(tabPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Properties loadDatabaseProperties() {
        Properties properties = new Properties();
        try {
            // Load properties from the database.properties file
            properties.load(getClass().getResourceAsStream("/database.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    private void connectToDatabase(Properties properties) {
        try {
            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.username");
            String password = properties.getProperty("db.password");
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Tab createTab1() {
        Tab tab1 = new Tab("Tab 1");
        tab1.setClosable(false);

        VBox tab1Layout = new VBox();
        tab1Layout.setSpacing(10);
        tab1Layout.setPadding(new Insets(10));

        Label tab1Label = new Label("Welcome to Tab 1!");
        tab1Label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        tableView = createTableView();
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(event -> refreshTable(tableView)); // Pass tableView as an argument

        Button addButton = new Button("Add New Item");
        addButton.setOnAction(event -> showNewItemDialog()); // Show the dialog when the button is clicked

        Button insertDataButton = new Button("Insert Data into Historical Summary");
        insertDataButton.setOnAction(event -> insertDataIntoHistoricalSummary());

        tab1Layout.getChildren().addAll(tab1Label, tableView, refreshButton, addButton, insertDataButton);
        tab1.setContent(tab1Layout);

        return tab1;
    }

    private TableView<UserData> createTableView() {
        TableView<UserData> tableView = new TableView<>();

        TableColumn<UserData, UUID> productIdColumn = new TableColumn<>("Product ID");
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));


        TableColumn<UserData, String> productnameColumn = new TableColumn<>("Product Name");
        productnameColumn.setCellValueFactory(new PropertyValueFactory<>("productname"));

        TableColumn<UserData, String> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        // Make the "Price" column editable with TextField cells
        priceColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        // Define how to commit the edited value to the model
        priceColumn.setOnEditCommit(event -> {
            String newValue = event.getNewValue();
            UserData userData = event.getTableView().getItems().get(event.getTablePosition().getRow());
            userData.setPrice(newValue); // Update the model

            // Call a method to update the corresponding record in the database
            updatePriceInDatabase(userData.getProductId().toString(), newValue);
        });

        TableColumn<UserData, String> heightColumn = new TableColumn<>("Height");
        heightColumn.setCellValueFactory(new PropertyValueFactory<>("height"));

        TableColumn<UserData, String> weightColumn = new TableColumn<>("Weight");
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));

        TableColumn<UserData, String> lengthColumn = new TableColumn<>("Length");
        lengthColumn.setCellValueFactory(new PropertyValueFactory<>("length"));

        TableColumn<UserData, String> colorColumn = new TableColumn<>("Color");
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));

        TableColumn<UserData, Void> deleteColumn = new TableColumn<>("Delete");
        deleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    UserData userData = getTableView().getItems().get(getIndex());
                    deleteRow(userData); // Call method to delete the row and record
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        TableColumn<UserData, Void> updateColumn = new TableColumn<>("Update");
        updateColumn.setCellFactory(param -> new TableCell<>() {
            private final Button updateButton = new Button("Update");

            {
                updateButton.setOnAction(event -> {
                    UserData userData = getTableView().getItems().get(getIndex());
                    // Open a dialog or input field for the user to enter the new price
                    String newPrice = showUpdatePriceDialog(userData.getPrice());
                    if (newPrice != null) {
                        userData.setPrice(newPrice); // Update the model

                        // Convert the UUID to a String before passing it to the updatePriceInDatabase method
                        String productIdString = userData.getProductId().toString();
                        updatePriceInDatabase(productIdString, newPrice);
                    }
                });

            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(updateButton);
                }
            }
        });
        TableColumn<UserData, Integer> inventoryColumn = new TableColumn<>("Inventory");
        inventoryColumn.setCellValueFactory(new PropertyValueFactory<>("inventory"));
        inventoryColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        inventoryColumn.setOnEditCommit(event -> {
            int newValue = event.getNewValue();
            UserData userData = event.getTableView().getItems().get(event.getTablePosition().getRow());
            userData.setInventory(newValue);

        });

        TableColumn<UserData, Date> orderDateColumn = new TableColumn<>("Order Date");
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));


        tableView.getColumns().addAll(productIdColumn, productnameColumn, priceColumn, heightColumn, weightColumn, lengthColumn,
                colorColumn, deleteColumn, updateColumn, inventoryColumn, orderDateColumn);

        return tableView;
    }

    private String showUpdatePriceDialog(String currentPrice) {
        TextInputDialog dialog = new TextInputDialog(currentPrice);
        dialog.setTitle("Update Price");
        dialog.setHeaderText("Enter the new price:");
        dialog.setContentText("New Price:");

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }


    private void refreshTable(TableView<UserData> tableView) {
        tableView.getItems().clear();

        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM storage");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                UUID productId = UUID.fromString(rs.getString("productid"));
                String productname = rs.getString("productname");
                String price = rs.getString("price");
                String height = rs.getString("height");
                String weight = rs.getString("weight");
                String length = rs.getString("length");
                String color = rs.getString("color");
                String inventoryStr = rs.getString("inventory");
                String orderDateStr = rs.getString("orderdate");
                Date orderDate = null;

                int inventory = 0;
                try {
                    inventory = Integer.parseInt(inventoryStr);
                } catch (NumberFormatException e) {
                    // Handle the case where the inventory value cannot be parsed as an int
                    e.printStackTrace(); // Handle this error appropriately
                }
                if (orderDateStr != null) {
                    // Parse the date if it's not null
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        orderDate = dateFormat.parse(orderDateStr);
                    } catch (ParseException e) {
                        // Handle the case where date parsing fails
                        e.printStackTrace(); // Handle this error appropriately
                    }
                }

                UserData userData = new UserData(productId, productname, price, height, weight, length, color, inventory, orderDate);
                //userData.setInventory(inventory); // Set quantity

                userData.setOrderDate(orderDate);

                tableView.getItems().add(userData);
            }

            rs.close();
            stmt.close();

           ///// insertDataIntoHistoricalSummary(); /////////
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    private void deleteRow(UserData userData) {
        try {
            // Prepare a DELETE statement
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM storage WHERE productid = ?");

            // Assuming `productid` is of type UUID
            UUID productId = userData.getProductId();

            stmt.setObject(1, productId); // Use setObject to set UUID
            stmt.executeUpdate();
            stmt.close();

            // Remove the row from the TableView
            tableView.getItems().remove(userData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updatePriceInDatabase(String userId, String newPrice) {
        try {
            // Assuming userId is a String
            PreparedStatement stmt = conn.prepareStatement("UPDATE storage SET price = ? WHERE productid = ?");
            stmt.setString(1, newPrice);
            stmt.setString(2, userId);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidData(String productId, String productname, String price, String height, String weight, String length, String color, String inventory) {
        // Implement your validation logic here
        // For example, you can check if the fields are not empty and if the price is a valid number.

        boolean isPriceValid = isValidPrice(price); // Example: Check if price is valid
        boolean areFieldsNotEmpty = !productId.isEmpty() && !productname.isEmpty() && !price.isEmpty() &&
                !height.isEmpty() && !weight.isEmpty() && !length.isEmpty() && !color.isEmpty();

        return isPriceValid && areFieldsNotEmpty;
    }

    private boolean isValidPrice(String price) {
        try {
            // Try to parse the price as a BigDecimal (assuming a valid price format)
            new BigDecimal(price);
            return true;
        } catch (NumberFormatException e) {
            // Price is not a valid number
            return false;
        }
    }

    private void addItemToDatabase(UUID productId, String productname, String price, String height, String weight, String length, String color, String inventory, Date orderDate) {
        try {
            // Convert the UUID to a string for insertion into the database
            String productIdStr = productId.toString();

            // Prepare an INSERT statement
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO storage (productid, productname, price, height, weight, length, color, inventory, orderdate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            stmt.setString(1, productIdStr); // Use the string representation of UUID
            stmt.setString(2, productname);
            stmt.setString(3, price);
            stmt.setString(4, height);
            stmt.setString(5, weight);
            stmt.setString(6, length);
            stmt.setString(7, color);
            stmt.setString(8, inventory);

            // Set the orderDate in the INSERT statement
            if (orderDate != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String orderDateStr = dateFormat.format(orderDate);
                stmt.setString(9, orderDateStr);
            } else {
                stmt.setNull(9, java.sql.Types.DATE);
            }
            // Execute the INSERT statement
            stmt.executeUpdate();
            stmt.close();

          //  updateHistoricalData(productId, new BigDecimal(price), orderDate);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void insertDataIntoHistoricalSummary() {
        try {
            // Create a SQL query to insert data into historical_product_summary
            String insertQuery =
                    "INSERT INTO historical_product_summary2 (orderid, productid, month, productname, total_orders, total_sales, inventory) " +
                    "SELECT orderid, productid, month, productname, total_orders, total_sales, inventory " +
                    "FROM product_summary4;";

            // Create a PreparedStatement and execute the query
            PreparedStatement stmt = conn.prepareStatement(insertQuery);
            stmt.executeUpdate();
            stmt.close();

            System.out.println("Data inserted into historical_product_summary successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void showNewItemDialog() {
        // Generate a random UUID for the new item
        UUID newProductId = UUID.randomUUID();

        // Create a dialog for adding a new item
        Dialog<UserData> dialog = new Dialog<>();
        dialog.setTitle("Add New Item");

        // Create the dialog content (a form for entering item details)
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        //  TextField productIdField = new TextField();
        //  productIdField.setPromptText("Product ID");
        TextField productnameField = new TextField();
        productnameField.setPromptText("Type of Christo");
        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        TextField heightField = new TextField();
        heightField.setPromptText("Height");
        TextField weightField = new TextField();
        weightField.setPromptText("Weight");
        TextField lengthField = new TextField();
        lengthField.setPromptText("Length");
        TextField colorField = new TextField();
        colorField.setPromptText("Color");
        TextField inventoryField = new TextField();
        inventoryField.setPromptText("Inventory");
        TextField orderDateField = new TextField(); // Add a text field for order date
        orderDateField.setPromptText("Order Date (yyyy-MM-dd)"); // Prompt the user for the date format

        //     grid.add(new Label("Product ID:"), 0, 0);
        //    grid.add(productIdField, 1, 0);
        grid.add(new Label("Type of Christo:"), 0, 1);
        grid.add(productnameField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Height:"), 0, 3);
        grid.add(heightField, 1, 3);
        grid.add(new Label("Weight:"), 0, 4);
        grid.add(weightField, 1, 4);
        grid.add(new Label("Length:"), 0, 5);
        grid.add(lengthField, 1, 5);
        grid.add(new Label("Color:"), 0, 6);
        grid.add(colorField, 1, 6);
        grid.add(new Label("Inventory:"), 0, 7);
        grid.add(inventoryField, 1, 7);
        grid.add(new Label("Order Date:"), 0, 8); // Add a label for the order date
        grid.add(orderDateField, 1, 8); // Add the order date text field

        dialog.getDialogPane().setContent(grid);

        // Add buttons to the dialog
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        // Handle the "Add" button click
        // Handle the "Add" button click
        dialog.setResultConverter(buttonType -> {
            if (buttonType == addButton) {
                // Retrieve the entered data from the fields
                String productname = productnameField.getText();
                String price = priceField.getText();
                String height = heightField.getText();
                String weight = weightField.getText();
                String length = lengthField.getText();
                String color = colorField.getText();
                String inventory = inventoryField.getText();
                String orderDateStr = orderDateField.getText(); // Get the order date string

                // Validate the data (add your validation logic here)
                if (isValidData(newProductId.toString(), productname, price, height, weight, length, color, inventory)) {

                    Date orderDate = null;
                    if (!orderDateStr.isEmpty()) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            orderDate = dateFormat.parse(orderDateStr);
                        } catch (ParseException e) {
                            // Handle the case where date parsing fails
                            e.printStackTrace(); // Handle this error appropriately
                        }
                    }

                    // Add the new item to the database with the generated UUID
                    addItemToDatabase(newProductId, productname, price, height, weight, length, color, inventory, orderDate);

                    // Refresh the table to display the new item
                    refreshTable(tableView);
                } else {
                    // Show an error message or handle validation failure
                    // You can use Alert or another method to display an error message
                    // For example:
                    showErrorAlert("Invalid data", "Please enter valid data.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private Node createSalesPopularityChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Month");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Value");

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Sales and Popularity Score Over Months");

        // Fetch data from the database and populate the series
        XYChart.Series<String, Number> salesSeries = new XYChart.Series<>();
        salesSeries.setName("Total Sales");

    //    XYChart.Series<String, Number> popularitySeries = new XYChart.Series<>();
     //   popularitySeries.setName("Popularity Score");

        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT month, total_sales FROM product_summary"); //, popularity_score
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String month = rs.getString("month");
                int sales = rs.getInt("total_sales");
                //int popularity = rs.getInt("popularity_score");

                salesSeries.getData().add(new XYChart.Data<>(month, sales));
               // popularitySeries.getData().add(new XYChart.Data<>(month)); //, popularity
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        lineChart.getData().addAll(salesSeries);  //, popularitySeries
        return lineChart;
    }

    private Tab createTab2() {
        Tab tab2 = new Tab("Tab 2");
        tab2.setClosable(false);

        VBox tab2Layout = new VBox();
        tab2Layout.setSpacing(10);
        tab2Layout.setPadding(new Insets(10));

        Label tab2Label = new Label("Welcome to Tab 2!");
        tab2Label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Load and display the image
        Image image = new Image("file:C:/Users/Janet/Desktop/Senior Project/virginmary.JPG");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(500);
        imageView.setFitHeight(900);

        tab2Layout.getChildren().addAll(tab2Label, imageView);
        tab2.setContent(tab2Layout);

        return tab2;
    }


    private Tab createTab3() {
        Tab tab3 = new Tab("Tab 3");
        tab3.setClosable(false);

        VBox tab3Layout = new VBox();
        tab3Layout.setSpacing(10);
        tab3Layout.setPadding(new Insets(10));

        Label tab3Label = new Label("Welcome to Tab 3!");
        tab3Label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        LineChart<String, Number> lineChart = createSalesLineChart();
        Node chartNode = createSalesPopularityChart();
        tab3Layout.getChildren().addAll(tab3Label, chartNode, lineChart);
        tab3.setContent(tab3Layout);

        return tab3;
    }

    private LineChart<String, Number> createSalesLineChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Month");
        yAxis.setLabel("Total Sales");

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Monthly Sales");

        // Fetch data from the database and populate the series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT month, total_sales FROM monthly_sales");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String month = rs.getString("month");
                int sales = rs.getInt("total_sales");
                series.getData().add(new XYChart.Data<>(month, sales));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        lineChart.getData().add(series);
        return lineChart;
    }
    private Tab createTab4() {
        Tab tab4 = new Tab("Tab 4");
        tab4.setClosable(false);

        VBox tab4Layout = new VBox();
        tab4Layout.setSpacing(10);
        tab4Layout.setPadding(new Insets(10));

        Label tab4Label = new Label("Welcome to Tab 4!");
        tab4Label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");


        tab4Layout.getChildren().addAll(tab4Label);
        tab4.setContent(tab4Layout);

        return tab4;
    }

}