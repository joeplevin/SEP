package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import models.TaskPref;
import models.User;
import models.UserCreds;
import org.kordamp.ikonli.javafx.FontIcon;
import repositories.*;

import java.sql.SQLException;

public class NewUserController {
    @FXML
    public Node unew_main;
    //New User View
    @FXML
    public TextField tf_new_user_fname;
    @FXML
    public TextField tf_new_user_sname;
    @FXML
    public TextField tf_new_user_email;
    @FXML
    public TextField tf_new_user_username;
    @FXML
    public TextField tf_new_user_password;
    @FXML
    public ChoiceBox cb_new_user_role;
    @FXML
    public Button button_save_new_user;
    @FXML
    public Label label_username_duplicate_error;

    // Null Error Handling
    @FXML
    public Label label_new_username_null;
    @FXML
    public Label label_new_password_null;
    @FXML
    public Label label_new_fname_null;
    @FXML
    public Label label_new_sname_null;
    @FXML
    public Label label_new_role_null;
    @FXML
    public Label label_new_null_error;

    //Task Prefs for Technicians
    @FXML
    public Label label_tech_saved;
    @FXML
    public AnchorPane pane_newtech_taskprefs;
    @FXML
    public ChoiceBox cb_newuser_task_type;
    @FXML
    public ChoiceBox cb_newuser_task_proficiency;
    @FXML
    public Button button_add_newuser_task_pref;
    @FXML
    public TableView tableview_newuser_task_prefs;
    @FXML
    public Label label_newuser_tasktype_null;
    @FXML
    public Label label_newuser_taskprof_null;
    @FXML
    public Label label_newuser_taskpref_null;
    @FXML
    private Label label_tasktype_duplicate_error;

    @FXML
    private DashboardController dashboardController;
    @FXML
    private UserRepository userRepository = new UserRepositoryImpl();
    private UserCredsRepository credsRepo = new UserCredsRepositoryImpl();
    @FXML
    public TableColumn<TaskPref, String> tc_new_task_type;
    @FXML
    public TableColumn<TaskPref, Integer> tc_new_task_prof;
    @FXML
    public TableColumn<TaskPref, Boolean> tc_new_taskpref_delete;
    @FXML
    public Button button_save_tech;

    //Data
    private int userId = 0;

    @FXML
    public void initialize(){
        tf_new_user_username.textProperty().addListener((obsval,oldval,newval)->{
            if (credsRepo.authUser(newval) > 0 && !tf_new_user_username.getText().isEmpty()) {
                label_username_duplicate_error.setVisible(true);
            } else {
                label_username_duplicate_error.setVisible(false);
            }
        });
        ObservableList<String> roleList = FXCollections.observableArrayList();
        roleList.add("");
        for(String role: userRepository.getRoleList()){
            roleList.add(role);
        }
        cb_new_user_role.setItems(roleList);
//            pane_newtech_taskprefs.visibleProperty().bind(cb_new_user_role.valueProperty().isEqualTo("Technician"));
        ObservableList<String> taskTypeList = FXCollections.observableArrayList();
        taskTypeList.add("");
        for(String taskType: userRepository.getTaskTypeList()){
            taskTypeList.add(taskType);
        }
        cb_new_user_role.setItems(roleList);
        cb_newuser_task_type.setItems(taskTypeList);
        ObservableList<Integer> list = FXCollections.observableArrayList(null,1,2,3,4,5);
        cb_newuser_task_proficiency.setItems(list);
    }

    @FXML
    public int saveUser(MouseEvent mouseEvent) throws SQLException {
        int id = 0;
        if(tf_new_user_username.getText().isEmpty()){
            label_new_username_null.setVisible(true);
        } else{
            label_new_username_null.setVisible(false);
        }
        if(tf_new_user_password.getText().isEmpty()){
            label_new_password_null.setVisible(true);
        } else{
            label_new_password_null.setVisible(false);
        }
        if(tf_new_user_fname.getText().isEmpty()){
            label_new_fname_null.setVisible(true);
        } else{
            label_new_fname_null.setVisible(false);
        }
        if(tf_new_user_sname.getText().isEmpty()){
            label_new_sname_null.setVisible(true);
        } else{
            label_new_sname_null.setVisible(false);
        }
        if(cb_new_user_role.getSelectionModel().getSelectedItem() == null || cb_new_user_role.getSelectionModel().getSelectedItem().equals("")){
            label_new_role_null.setVisible(true);
        } else{
            label_new_role_null.setVisible(false);
        }
        if(label_new_username_null.isVisible()
                ||label_new_password_null.isVisible()
                ||label_new_fname_null.isVisible()
                ||label_new_sname_null.isVisible()
                ||label_new_role_null.isVisible()){
            label_new_null_error.setVisible(true);
        } else if(label_username_duplicate_error.isVisible()){
            return id;
        } else {
            label_new_null_error.setVisible(false);
            User user = new User();
            UserCreds creds = new UserCreds();
            UserRepository userRepo = new UserRepositoryImpl();
            UserCredsRepository userCredsRepo = new UserCredsRepositoryImpl();
            user.setRole(cb_new_user_role.getSelectionModel().getSelectedItem().toString());
            user.setFName(tf_new_user_fname.getText());
            user.setSName(tf_new_user_sname.getText());
            user.setEmail(tf_new_user_email.getText());
            String pass = creds.encryptPass(tf_new_user_password.getText());
            creds.setPassword(pass);
            creds.setUsername(tf_new_user_username.getText());
            userId = userRepo.newUser(user);
            if (userId != 0) {
                userCredsRepo.newCreds(creds, userId);
            }
            if(user.getRole().equals("Technician")){
                label_tech_saved.setVisible(true);
                pane_newtech_taskprefs.setVisible(true);
                button_save_tech.setVisible(true);
                button_save_tech.toFront();
                button_save_new_user.setVisible(false);
                taskPrefsSetup(user);
            } else{
                tf_new_user_fname.clear();
                tf_new_user_sname.clear();
                tf_new_user_email.clear();
                tf_new_user_username.clear();
                tf_new_user_password.clear();
                cb_new_user_role.getSelectionModel().clearSelection();
                cb_newuser_task_type.getSelectionModel().clearSelection();
                cb_newuser_task_proficiency.getSelectionModel().clearSelection();
                tableview_newuser_task_prefs.getItems().clear();
                label_new_username_null.setVisible(false);
                label_new_role_null.setVisible(false);
                label_new_fname_null.setVisible(false);
                label_new_sname_null.setVisible(false);
                label_new_password_null.setVisible(false);
                label_new_null_error.setVisible(false);
                dashboardController.newUser(user);
                dashboardController.showUsersPane();
            }
        }
        return id;
    }
    public void showTaskPrefs(ActionEvent e) {
        if (cb_new_user_role.getSelectionModel().getSelectedItem() != null) {
            if (cb_new_user_role.getSelectionModel().getSelectedItem().toString().equals("Technician")) {
                button_save_new_user.setText("Next");
            } else {
                button_save_new_user.setText("Save");
            }
        }
    }

    public void taskPrefsSetup(User user){
        //set task prefs table by user_id on tasktype_user table
        TaskPrefRepository taskPrefRepository = new TaskPrefRepositoryImpl();
        ObservableList<TaskPref> prefsData = taskPrefRepository.getTaskPrefsById(user.getUser_id());
        //set cell task type name from tasktype_user table
        tc_new_task_type.setCellValueFactory(cellData -> cellData.getValue().tasktypeProperty());
        //set cell task proficiency level from tasktype_user table
        tc_new_task_prof.setCellValueFactory(cellData -> cellData.getValue().proficiencyProperty().asObject());
        //set cell delete button for each row created with callback on delete column
        tc_new_taskpref_delete.setCellFactory(deleteColumn -> new TableCell<TaskPref,Boolean>() {
            //create delete button
            Button button_delete_task_pref = new Button();
            //override table cell factory
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                //if no data in row, display null
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else{
                    // set delete button
                    button_delete_task_pref.setGraphic(new FontIcon("fltral-delete-16"));
                    button_delete_task_pref.setId("button_delete_task_pref");
                    setGraphic(button_delete_task_pref);
                    setText(null);
                    //on delete
                    button_delete_task_pref.setOnAction(event -> {
                        TaskPref taskPref = getTableView().getItems().get(getIndex());
                        // alert confirmation dialogue pop-up
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                                "Are you sure you want to delete '" + taskPref.getTasktype() + "' task preference?", ButtonType.YES, ButtonType.NO);
                        alert.showAndWait();
                        if(alert.getResult() == ButtonType.YES) {
                            // get taskPref
                            int taskPrefId = taskPref.getTask_pref_id();
                            //remove from database
                            //if user adds task pref, but deletes in same process, id is not generated
                            if(taskPref.getTask_pref_id() == 0) {
                                taskPrefRepository.deleteTaskPrefById(taskPrefId);
                            } else{
                                taskPrefRepository.deleteTaskPrefById(taskPref.getTask_pref_id());
                            }
                            // remove from list
                            prefsData.remove(taskPref);
                        }
                    });
                }
            }
        });
        // set table data
        tableview_newuser_task_prefs.setItems(prefsData);

    }

    public void saveTaskPref(MouseEvent event) throws SQLException {
        if(cb_newuser_task_type.getSelectionModel().getSelectedItem() == null){
            label_newuser_tasktype_null.setVisible(true);
        } else{
            label_newuser_tasktype_null.setVisible(false);
        }
        if(cb_newuser_task_proficiency.getSelectionModel().getSelectedItem() == null){
            label_newuser_taskprof_null.setVisible(true);
        } else{
            label_newuser_taskprof_null.setVisible(false);
        }
        if(label_newuser_tasktype_null.isVisible() || label_newuser_taskprof_null.isVisible()){
            label_newuser_taskpref_null.setVisible(true);
        } else{
            label_newuser_taskpref_null.setVisible(false);
            TaskPref taskPref = new TaskPref();
            TaskPrefRepository taskPrefRepository = new TaskPrefRepositoryImpl();
            ObservableList<TaskPref> prefsList = taskPrefRepository.getTaskPrefsById(userId);
            for(TaskPref pref : prefsList){
                if(pref.getTasktype().equals(cb_newuser_task_type.getSelectionModel().getSelectedItem().toString())){
                    label_tasktype_duplicate_error.setVisible(true);
                }
            }
            if(!label_tasktype_duplicate_error.isVisible()){
                taskPref.setUser_id(userId);
                taskPref.setProficiency((int) cb_newuser_task_proficiency.getSelectionModel().getSelectedItem());
                int taskPrefId = taskPrefRepository.newTaskPref(taskPref);
                taskPref.setTasktype(cb_newuser_task_type.getSelectionModel().getSelectedItem().toString());
                tableview_newuser_task_prefs.getItems().add(taskPref);
            }
        }
    }

    public void saveTech(MouseEvent event){
        tf_new_user_fname.clear();
        tf_new_user_sname.clear();
        tf_new_user_email.clear();
        tf_new_user_username.clear();
        tf_new_user_password.clear();
        cb_new_user_role.getSelectionModel().clearAndSelect(0);
        cb_newuser_task_type.getSelectionModel().clearAndSelect(0);
        cb_newuser_task_proficiency.getSelectionModel().clearAndSelect(0);
        tableview_newuser_task_prefs.getItems().clear();
        pane_newtech_taskprefs.setVisible(false);
        label_newuser_taskpref_null.setVisible(false);
        label_newuser_taskprof_null.setVisible(false);
        label_newuser_tasktype_null.setVisible(false);
        label_new_null_error.setVisible(false);
        label_tech_saved.setVisible(false);
        dashboardController.showUsersPane();
    }


    //Dashboard Navigation
    public void goBack(MouseEvent e){
        tf_new_user_fname.clear();
        tf_new_user_sname.clear();
        tf_new_user_email.clear();
        tf_new_user_username.clear();
        tf_new_user_password.clear();
        cb_new_user_role.getSelectionModel().clearAndSelect(0);
        cb_newuser_task_type.getSelectionModel().clearAndSelect(0);
        cb_newuser_task_proficiency.getSelectionModel().clearAndSelect(0);
        tableview_newuser_task_prefs.getItems().clear();
        pane_newtech_taskprefs.setVisible(false);
        label_newuser_taskpref_null.setVisible(false);
        label_newuser_taskprof_null.setVisible(false);
        label_newuser_tasktype_null.setVisible(false);
        label_tech_saved.setVisible(false);
        label_new_null_error.setVisible(false);
        label_new_username_null.setVisible(false);
        label_new_role_null.setVisible(false);
        label_new_fname_null.setVisible(false);
        label_new_sname_null.setVisible(false);
        label_new_password_null.setVisible(false);
        dashboardController.showUsersPane();
    }
    public void hidePage() {
        unew_main.setVisible(false);
    }
    public void showPage() {
        unew_main.setVisible(true);
        unew_main.toFront();
    }
    public void setDashboardController(DashboardController dashboardController){
        this.dashboardController = dashboardController;
    }
    public void toUserList(){
        dashboardController.showUsersPane();
    }
}
