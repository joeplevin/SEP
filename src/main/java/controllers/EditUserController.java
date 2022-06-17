package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

import javax.swing.*;
import java.sql.SQLException;

public class EditUserController {

    //Dashboard Controller
    @FXML
    public Node uedit_main;
    @FXML
    DashboardController dashboardController;


    //Account Details Pane
    @FXML
    public Label label_edit_title;
    @FXML
    public TextField tf_username;
    @FXML
    public TextField tf_pass;
    @FXML
    public TextField tf_fname;
    @FXML
    public TextField tf_sname;
    @FXML
    public TextField tf_email;
    @FXML
    public ChoiceBox cb_role;
    @FXML
    public Button button_edit_back;
    @FXML
    public Label label_username_null;
    @FXML
    public Label label_fname_null;
    @FXML
    public Label label_sname_null;
    @FXML
    public Label label_pass_null;
    @FXML
    public Label label_null_error;
    @FXML
    public Label label_username_duplicate_error;

    //Technician Task Preferences Pane
    @FXML
    public AnchorPane pane_task_prefs;
    @FXML
    public ChoiceBox cb_task_type;
    @FXML
    public ChoiceBox cb_task_proficiency;
    @FXML
    public TableView<TaskPref> table_task_prefs;
    @FXML
    public TableColumn<TaskPref,String> tc_task_type;
    @FXML
    public TableColumn<TaskPref,Integer> tc_task_prof;
    @FXML
    public TableColumn<TaskPref,Boolean> tc_taskpref_delete;
    @FXML
    public Label label_tasktype_null;
    @FXML
    public Label label_taskprof_null;
    @FXML
    public Label label_taskpref_null;
    @FXML
    public Label label_tasktype_duplicate_error;

    //Data Display
    @FXML
    private UserCredsRepository credsRepository = new UserCredsRepositoryImpl();
    @FXML
    private UserRepository userRepository = new UserRepositoryImpl();
    @FXML
    private TaskPrefRepository taskPrefRepository = new TaskPrefRepositoryImpl();
    @FXML
    private User user = null;
    @FXML
    private int credsId = 0;
    @FXML
    private int taskPrefId = 0;
    @FXML
    private ObservableList<TaskPref> prefsData = null;
    @FXML
    private ObservableList<Label> nullList = FXCollections.observableArrayList(label_username_null,label_pass_null,label_fname_null,label_sname_null);


    public void initialize(){
        label_username_null.setVisible(false);
        label_pass_null.setVisible(false);
        label_fname_null.setVisible(false);
        label_sname_null.setVisible(false);
        label_null_error.setVisible(false);

    }

    public void userData(User u) {
        //Initialise user being edited
        user = u;
        // User Details
        userDataViewSetup(user);
        // Task Preference Details
        taskPrefsViewSetup(user);
    }

    private void userDataViewSetup(User user) {
        // set role options
        cb_role.setItems(userRepository.getRoleList());
        pane_task_prefs.visibleProperty().bind(cb_role.valueProperty().isEqualTo("Technician"));
        // get creds info
        UserCreds creds = null;
        creds = credsRepository.findByUserId(user.getUser_id());
        // set view with user & creds details
        label_edit_title.setText(user.getFName() + " " + user.getSName() +"'s Account");
        tf_username.setText(creds.getUsername());
        tf_fname.setText(user.getFName());
        tf_sname.setText(user.getSName());
        tf_email.setText(user.getEmail());
        cb_role.getSelectionModel().clearSelection();
        cb_role.getSelectionModel().select(user.getRole());
        String username = creds.getUsername();
    }
    @FXML
    private void taskPrefsViewSetup(User user) {
        // set task types
        cb_task_type.setItems(userRepository.getTaskTypeList());
        //set task proficiency options
        ObservableList<Integer> list = FXCollections.observableArrayList(1,2,3,4,5);
        cb_task_proficiency.setItems(list);
        //set task prefs table by user_id on tasktype_user table
        prefsData = taskPrefRepository.getTaskPrefsById(user.getUser_id());
        //set cell task type name from tasktype_user table
        tc_task_type.setCellValueFactory(cellData -> cellData.getValue().tasktypeProperty());
        //set cell task proficiency level from tasktype_user table
        tc_task_prof.setCellValueFactory(cellData -> cellData.getValue().proficiencyProperty().asObject());
        //set cell delete button for each row created with callback on delete column
        tc_taskpref_delete.setCellFactory(deleteColumn -> new TableCell<TaskPref,Boolean>() {
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
        table_task_prefs.setItems(prefsData);

    }
    @FXML
    private void saveTaskPref(MouseEvent event) {
        if(cb_task_type.getSelectionModel().getSelectedItem() == null){
            label_tasktype_null.setVisible(true);
        } else{
            label_tasktype_null.setVisible(false);
        }
        if(cb_task_proficiency.getSelectionModel().getSelectedItem() == null){
            label_taskprof_null.setVisible(true);
        } else{
            label_taskprof_null.setVisible(false);
        }
        if(label_tasktype_null.isVisible() || label_taskprof_null.isVisible()){
            label_taskpref_null.setVisible(true);
        } else {
            label_taskpref_null.setVisible(false);
            TaskPref taskPref = new TaskPref();
            ObservableList<TaskPref> prefsList = taskPrefRepository.getTaskPrefsById(user.getUser_id());
            for(TaskPref pref : prefsList){
                if(pref.getTasktype().equals(cb_task_type.getSelectionModel().getSelectedItem().toString())){
                    label_tasktype_duplicate_error.setVisible(true);
                }
            }
            if(!label_tasktype_duplicate_error.isVisible()) {
                taskPref.setUser_id(user.getUser_id());
                taskPref.setProficiency((int) cb_task_proficiency.getSelectionModel().getSelectedItem());
                taskPref.setTasktype(cb_task_type.getSelectionModel().getSelectedItem().toString());
                taskPrefId = taskPrefRepository.newTaskPref(taskPref);
                table_task_prefs.getItems().add(taskPref);
                cb_task_proficiency.getSelectionModel().clearSelection();
                cb_task_type.getSelectionModel().clearSelection();
            }
        }
    }
    @FXML
    private void saveUser(MouseEvent event) {
        UserCreds creds = credsRepository.findByUserId(user.getUser_id());
        if(!tf_username.getText().equals(creds.getUsername()) && credsRepository.authUser(tf_username.getText()) > 0){
                label_username_duplicate_error.setVisible(true);
            } else{
                label_username_duplicate_error.setVisible(false);
            }
        if(tf_username.getText().isEmpty()){
            label_username_null.setVisible(true);
        } else{
            label_username_null.setVisible(false);
        }
        if(tf_fname.getText().isEmpty()){
            label_fname_null.setVisible(true);
        } else{
            label_fname_null.setVisible(false);
        }
        if(tf_sname.getText().isEmpty()){
            label_sname_null.setVisible(true);
        } else{
            label_sname_null.setVisible(false);
        }
        if(label_username_null.isVisible()||label_pass_null.isVisible()||label_fname_null.isVisible()||label_sname_null.isVisible()){
            label_null_error.setVisible(true);
        }else if(label_username_duplicate_error.isVisible()){
            return;
        }else {
            label_null_error.setVisible(false);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to save changes to " + user.getFName() + "'s account?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                user.setFName(tf_fname.getText());
                user.setSName(tf_sname.getText());
                user.setEmail(tf_email.getText());
                user.setRole(cb_role.getSelectionModel().getSelectedItem().toString());
                creds.setUsername(tf_username.getText());
                if(tf_pass.getText().isEmpty()){
                    creds.setPassword(credsRepository.findByPassById(user.getUser_id()));
                    creds.getPassword();
                } else{
                    creds.setPassword(creds.encryptPass(tf_pass.getText()));
                    creds.getPassword();
                }
                userRepository.updateUser(user);
                credsRepository.updateUserCreds(creds);
                dashboardController.showUsersPane();
            }
        }
    }

    //Dashboard Navigation
    public void setDashboardController(DashboardController dashboardController){
        this.dashboardController = dashboardController;
    }
    public void goBack(MouseEvent e){
        label_sname_null.setVisible(false);
        label_fname_null.setVisible(false);
        label_pass_null.setVisible(false);
        label_username_null.setVisible(false);
        label_taskprof_null.setVisible(false);
        label_tasktype_null.setVisible(false);
        label_taskpref_null.setVisible(false);
        label_null_error.setVisible(false);
        dashboardController.showUsersPane();
    }
    public void hidePage() {
        uedit_main.setVisible(false);
    }
    public void showPage() {
        uedit_main.setVisible(true);
        uedit_main.toFront();
    }
    public void toUserList(){
        dashboardController.showUsersPane();
    }



}


