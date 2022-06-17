package controllers;

import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import models.Job;
import models.User;
import models.UserCreds;
import org.kordamp.ikonli.javafx.FontIcon;
import repositories.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UserCredsController {

    @FXML
    TextField tf_username;
    @FXML
    PasswordField pf_password;
    @FXML
    Label label_user_not_found;
    @FXML
    Label label_password_not_found;
    @FXML
    Button button_login;
    @FXML
    FontIcon icon_account;
    @FXML
    FontIcon icon_pass;

    @FXML
    public ProgressIndicator pi_sceneProgress;

    @FXML
    private DashboardController dashboardController = new DashboardController();
    private UserListController userListController = new UserListController();
    private JobListController jobListController = new JobListController();

    private UserCredsRepository credsRepo = new UserCredsRepositoryImpl();
    private UserRepository userRepository = new UserRepositoryImpl();
    private JobRepository jobRepository = new JobRepositoryImpl();

    // Thread Executor
    private Executor executor = Executors.newCachedThreadPool(runnable -> {
        Thread t = new Thread(runnable);
        t.setDaemon(true);
        return t;
    });

    //User Credentials Id
    private int res = 0;

    public void initialize() throws SQLException {
        UserCreds creds = new UserCreds();
        credsRepo.clearCurrentUser();

        //Username check listener
        tf_username.textProperty().addListener((obsval,oldval,newval) -> {
            res = creds.checkUser(newval);
            usernameDisplay(res);

        });

        //Password check listener
        pf_password.textProperty().addListener((obsval,oldval,newval) -> {
            if(res > 0) {
                int id = creds.checkPass(res,newval);
                passDisplay(id);
            }
        });
    }

    @FXML
    public void signIn(ActionEvent event) throws IOException {
        //If no errors, setup for next pane
        if(!label_user_not_found.isVisible() && !label_password_not_found.isVisible()){
            User user = userRepository.getUserById(res);
            dashboardController.setCurrent(user);
            displayDashboard();
        }
    }

    @FXML
    private void displayDashboard() throws IOException {
        //Task for setup & displaying progress indicator in case of hangups/delay for user.
        javafx.concurrent.Task<Scene> sceneTask = new javafx.concurrent.Task<Scene>() {
            @Override
            protected Scene call() throws Exception {
                //Set next pane & Initialize dashboard controller
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard.fxml"));
                loader.setController(dashboardController);
                Parent root = loader.load();
                Scene scene = new Scene(root);
                return scene;
            }
        };
        sceneTask.setOnFailed(event ->{
        });
        sceneTask.setOnSucceeded(event ->{
            Stage stage = (Stage) button_login.getScene().getWindow();
            String style = getClass().getResource("/styles.css").toExternalForm();
            Scene scene = sceneTask.getValue();
            scene.getStylesheets().add(style);
            stage.setTitle("Vulture Service");
            stage.setScene(scene);
            pi_sceneProgress.progressProperty().unbind();
            pi_sceneProgress.setVisible(false);
        });
        pi_sceneProgress.progressProperty().bind(sceneTask.progressProperty());
        executor.execute(sceneTask);
        pi_sceneProgress.setVisible(true);
    }

    // Icon/Font display on user check
    private boolean usernameDisplay(int res) {
        if (res == -1) {
            label_user_not_found.setVisible(true);
            label_user_not_found.setText("*Username not found, please try again");
            icon_account.setIconLiteral("fltfmz-person-delete-20");
            icon_account.setIconColor(Color.LIGHTSALMON);
            pf_password.setEditable(false);
            pf_password.setPromptText("Enter Correct Username");
            return false;
        } else if (res == -9) {
            label_user_not_found.setVisible(true);
            label_user_not_found.setText("*Username incorrect, check your spelling");
            icon_account.setIconLiteral("fltfmz-person-delete-20");
            icon_account.setIconColor(Color.LIGHTSALMON);
            pf_password.setEditable(false);
            pf_password.setPromptText("Enter Correct Username");
            return false;
        } else {
            label_user_not_found.setVisible(false);
            icon_account.setIconLiteral("fltfmz-person-available-20");
            icon_account.setIconColor(Color.LIGHTGREEN);
            pf_password.setPromptText("Enter Password");
            pf_password.setEditable(true);
            return true;
        }
    }

    // Icon/Font display on password check
    private boolean passDisplay(int res){
            if (res == -2) {
                icon_pass.setIconLiteral("fltfal-lock-20");
                icon_pass.setIconColor(Color.LIGHTSALMON);
                label_password_not_found.setVisible(true);
                label_password_not_found.setText("*Incorrect password, please try again");
                button_login.setDisable(true);
                return false;
            }
            label_password_not_found.setVisible(false);
            icon_pass.setIconLiteral("fltfmz-unlock-20");
            icon_pass.setIconColor(Color.LIGHTGREEN);
            button_login.setDisable(false);
            return true;
        }
}



