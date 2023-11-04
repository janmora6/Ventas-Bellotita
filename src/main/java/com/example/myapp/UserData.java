package com.example.myapp;
import javafx.beans.property.*;
import java.util.UUID;
import javafx.beans.property.SimpleObjectProperty;
import java.util.Date;
public class UserData {
    private final SimpleObjectProperty<UUID> productId;
    private final SimpleStringProperty productname;
    private final SimpleStringProperty price;
    private final SimpleStringProperty height;
    private final SimpleStringProperty weight;
    private final SimpleStringProperty length;
    private final SimpleStringProperty color;
    private final SimpleIntegerProperty inventory;


    private SimpleIntegerProperty salesCount; // Use SimpleIntegerProperty for sales count
    private SimpleDoubleProperty popularityScore; // Use SimpleDoubleProperty for popularity score
    private ObjectProperty<Date> orderDate; // Use ObjectProperty<Date> for orderDate


    public UserData(UUID productId, String productname, String price, String height, String weight, String length, String color, int inventory,  Date orderDate) {
        this.productId = new SimpleObjectProperty<>(productId);
        this.productname = new SimpleStringProperty(productname);
        this.price = new SimpleStringProperty(price);
        this.height = new SimpleStringProperty(height);
        this.weight = new SimpleStringProperty(weight);
        this.length = new SimpleStringProperty(length);
        this.color = new SimpleStringProperty(color);
        this.inventory = new SimpleIntegerProperty(inventory);
        // Initialize quantity, salesCount, and popularityScore
        this.salesCount = new SimpleIntegerProperty(0);
        this.popularityScore = new SimpleDoubleProperty(0.0);
        this.orderDate = new SimpleObjectProperty<>(null);

    }

    public UUID getProductId() {
        return productId.get();
    }

    public String getProductname() {
        return productname.get();
    }

    public String getPrice() {
        return price.get();
    }


    public String getHeight() {
        return height.get();
    }

    public String getWeight() {
        return weight.get();
    }

    public String getLength() {
        return length.get();
    }

    public String getColor() {
        return color.get();
    }

    public StringProperty productnameProperty() {
        return productname;
    }

    public StringProperty priceProperty() {
        return price;
    }

    public StringProperty heightProperty() {
        return height;
    }

    public StringProperty weightProperty() {
        return weight;
    }

    public StringProperty lengthProperty() {
        return length;
    }

    public StringProperty colorProperty() {
        return color;
    }

    public ObjectProperty<UUID> productIdProperty() {
        return productId;
    }
    public int getInventory() {
        return inventory.get();
    }

    public SimpleIntegerProperty inventoryProperty() {
        return inventory;
    }

    public void setInventory(int inventory) {
        this.inventory.set(inventory);
    }

    public int getSalesCount() {
        return salesCount.get();
    }

    public SimpleIntegerProperty salesCountProperty() {
        return salesCount;
    }

    public void setSalesCount(int salesCount) {
        this.salesCount.set(salesCount);
    }

    public double getPopularityScore() {
        return popularityScore.get();
    }

    public SimpleDoubleProperty popularityScoreProperty() {
        return popularityScore;
    }

    public void setPopularityScore(double popularityScore) {
        this.popularityScore.set(popularityScore);
    }

    public Date getOrderDate() {
        return orderDate.get();
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate.set(orderDate);
    }

    public ObjectProperty<Date> orderDateProperty() {
        return orderDate;
    }

    public void setPrice(String price) {
        this.price.set(price);
    }
}
