module com.example.myapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop; // Add this line if needed
    requires java.sql; // Add this line if needed

    requires org.bouncycastle.provider;
    // Allow access to the BCrypt module
   // requires org.mindrot.jbcrypt;
    opens com.example.myapp to javafx.fxml;
    exports com.example.myapp;
}