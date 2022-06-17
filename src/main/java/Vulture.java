import controllers.DashboardController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import models.User;
import repositories.UserRepositoryImpl;

import java.sql.SQLException;

public class Vulture extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Vulture.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1400, 750);
        stage.setTitle("Vulture Service - Login");
        stage.getIcons().add(new Image("vulture_black.png"));
        stage.setScene(scene);
        String style = getClass().getResource("/styles.css").toExternalForm();
        scene.getStylesheets().add(style);
        stage.show();
    }

}
