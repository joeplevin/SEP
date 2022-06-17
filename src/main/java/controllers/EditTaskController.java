package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import models.Job;
import models.Task;
import models.TaskPref;
import models.User;
import org.kordamp.ikonli.javafx.FontIcon;
import repositories.*;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class EditTaskController {

    @FXML
    public Node tedit_main;
    @FXML
    public Label label_edittask_title;
    @FXML
    public Button button_edittask_back;
    @FXML
    public TextField tf_edittask_name;
    @FXML
    public ChoiceBox cb_edittask_tasktype;
    @FXML
    public ChoiceBox cb_edittask_priority;
    @FXML
    public ChoiceBox cb_edittask_duration;
    @FXML
    public ChoiceBox cb_edittask_passfail;
    @FXML
    public ChoiceBox cb_edittask_numbertechs;
    @FXML
    public DatePicker dp_edittask_datestart;
    @FXML
    public DatePicker dp_edittask_datecomplete;
    @FXML
    public CheckBox check_edittask_complete;
    @FXML
    public TextArea ta_edittask_notes;

    @FXML
    public TableView<User> table_availabletechs;
    @FXML
    public TableColumn<User,String> tc_avtech_name;
    @FXML
    public TableColumn<User,Number> tc_avtech_timeav;

    @FXML
    public Label label_tech_task_prefs;
    @FXML
    public TableView<TaskPref> table_avtechtaskprefs;
    @FXML
    public TableColumn<TaskPref,String> tc_techtp_taskpref;
    @FXML
    public TableColumn<TaskPref,Number> tc_techtp_prof;

    @FXML
    public TableView<User> table_tasktech;
    @FXML
    public TableColumn<User,String> tc_tasktech_name;
    @FXML
    public TableColumn<User, Boolean> tc_tasktech_delete;

    @FXML
    public Button button_addtech;
    @FXML
    public Button button_autoassign;

    @FXML
    public Button button_save_taskedit;

    @FXML
    public Label label_taskname_null;
    @FXML
    public Label label_taskpriority_null;
    @FXML
    public Label label_taskduration_null;
    @FXML
    public Label label_tasktype_null;
    @FXML
    public Label label_tasknumbertechs_null;
    @FXML
    public Label label_edittask_null_error;

    @FXML
    private DashboardController dashboardController;

    private Task task = null;
    private ObservableList<User> userList = FXCollections.observableArrayList();
    private TaskRepository taskRepo = new TaskRepositoryImpl();
    private TaskPrefRepository taskPrefsRepo = new TaskPrefRepositoryImpl();
    private UserRepository userRepo = new UserRepositoryImpl();

    public void initialize(){
        label_taskname_null.setVisible(false);
        label_taskpriority_null.setVisible(false);
        label_taskduration_null.setVisible(false);
        label_tasktype_null.setVisible(false);
        label_tasknumbertechs_null.setVisible(false);
        label_edittask_null_error.setVisible(false);
    }

    public void editTaskViewSetup(Task t,ObservableList<User> staff) {
        task = t;
        userList = staff;

        ObservableList<String> priorityList = FXCollections.observableArrayList("","1","2","3","4","5");
        ObservableList<String> durationList = FXCollections.observableArrayList("","1","2","3","4","5","6","7","8");
        ObservableList<String> typeList = FXCollections.observableArrayList("");
        for(String type : taskRepo.getTaskTypeList()){
                typeList.add(type);
    }
        ObservableList<String> passfailList = FXCollections.observableArrayList("","Pass","Fail");
        ObservableList<String> requiredTechsList = FXCollections.observableArrayList("","1","2");

        cb_edittask_priority.setItems(priorityList);
        cb_edittask_priority.getSelectionModel().select(task.getPriority());
        cb_edittask_duration.setItems(durationList);
        cb_edittask_duration.getSelectionModel().select(task.getTask_duration());
        cb_edittask_tasktype.setItems(typeList);
        cb_edittask_tasktype.getSelectionModel().select(task.getTask_type());
        cb_edittask_passfail.setItems(passfailList);
        cb_edittask_passfail.getSelectionModel().select(task.getPass_fail());
        cb_edittask_numbertechs.setItems(requiredTechsList);
        cb_edittask_numbertechs.getSelectionModel().select(task.getTask_techs_required());

        tf_edittask_name.setText(task.getTask_name());
        ta_edittask_notes.setText(task.getTask_notes());
        dp_edittask_datestart.setValue(task.getTask_created_date().toLocalDate());
        check_edittask_complete.setSelected(task.isCompleted());
        if(task.isCompleted()) {
            dp_edittask_datecomplete.setValue(task.getCompleted_date().toLocalDate());
        }
        ObservableList<User> availableTechs = FXCollections.observableArrayList();
        ObservableMap<Integer, ObservableList<TaskPref>> taskPrefs = FXCollections.observableHashMap();
        for(User u : staff){
            if(u.getRole().equals("Technician")){
                taskPrefs.put(u.getUser_id(),taskPrefsRepo.getTaskPrefsById(u.getUser_id()));
                availableTechs.add(u);
            }
        }
        table_availabletechs.getSelectionModel().selectedItemProperty().addListener((obsval,oldval,newval) ->{
            if(newval != null){
                User u = table_availabletechs.getSelectionModel().getSelectedItem();
                ObservableList<TaskPref> userPrefs = taskPrefs.get(u.getUser_id());
                table_avtechtaskprefs.setItems(userPrefs);
                label_tech_task_prefs.setText(u.getFName() + "'s Task Preferences");
            } else{
                label_tech_task_prefs.setText("Technician Task Preferences");
            }
        });

        tc_avtech_name.setCellValueFactory(cellData -> cellData.getValue().fNameProperty());
        tc_avtech_name.setCellFactory(nameCol -> {
            return new TableCell<User, String>() {
                @Override
                public void updateItem(String user, boolean empty) {
                    super.updateItem(user, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        User u = getTableView().getItems().get(getIndex());
                        setText(u.getFName() + " " + u.getSName());
                    }
                }
            };
        });
        tc_avtech_timeav.setCellValueFactory(cellData -> cellData.getValue().user_available_timeProperty());
        table_availabletechs.setItems(availableTechs);

        tc_techtp_taskpref.setCellValueFactory(cellData -> cellData.getValue().tasktypeProperty());
        tc_techtp_prof.setCellValueFactory(cellData -> cellData.getValue().proficiencyProperty());

        tc_tasktech_name.setCellValueFactory(cellData -> cellData.getValue().fNameProperty());
        tc_tasktech_name.setCellFactory(nameCol -> {
            return new TableCell<User, String>() {
                @Override
                public void updateItem(String user, boolean empty) {
                    super.updateItem(user, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        User u = getTableView().getItems().get(getIndex());
                        setText(u.getFName() + " " + u.getSName());
                    }
                }
            };
        });
        tc_tasktech_delete.setCellFactory(deleteColumn -> new TableCell<User,Boolean>() {
            Button button_delete_ttech = new Button();
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else{
                    button_delete_ttech.setGraphic(new FontIcon("fltral-delete-16"));
                    button_delete_ttech.setId("button_delete_ttech");
                    button_delete_ttech.setOnAction(event -> {
                        User tech = getTableView().getItems().get(getIndex());
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this technician?", ButtonType.YES, ButtonType.NO);
                        alert.showAndWait();
                        if(alert.getResult() == ButtonType.YES) {
                            ObservableList<User> list = task.getTask_tech();
                            list.remove(tech);
                            task.setTask_tech(list);
                            tech.setUser_available_time(tech.getUser_available_time() + task.getTask_duration());
                            userRepo.updateUser(tech);
                            taskRepo.updateTask(task);
                            taskRepo.deleteUserTask(task.getTask_id(),tech.getUser_id());
                        }
                    });
                    setGraphic(button_delete_ttech);
                    setText(null);
                }
            }
        });
        table_tasktech.setItems(task.getTask_tech());
    }

    public boolean taskErrorCheck(){
        if(tf_edittask_name.getText().isEmpty()){
            label_taskname_null.setVisible(true);
        } else{
            label_taskname_null.setVisible(false);
        }
        if(cb_edittask_duration.getSelectionModel().getSelectedItem() == null){
            label_taskduration_null.setVisible(true);
        } else{
            label_taskduration_null.setVisible(false);
        }
        if(cb_edittask_priority.getSelectionModel().getSelectedItem() == null){
            label_taskpriority_null.setVisible(true);
        } else{
            label_taskpriority_null.setVisible(false);
        }
        if(cb_edittask_tasktype.getSelectionModel().getSelectedItem() == null){
            label_tasktype_null.setVisible(true);
        } else{
            label_tasktype_null.setVisible(false);
        }
        if(cb_edittask_numbertechs.getSelectionModel().getSelectedItem() == null){
            label_tasknumbertechs_null.setVisible(true);
        } else{
            label_tasknumbertechs_null.setVisible(false);
        }
        if(label_taskname_null.isVisible()
                ||label_tasknumbertechs_null.isVisible()
                ||label_tasktype_null.isVisible()
                ||label_taskpriority_null.isVisible()
                ||label_taskduration_null.isVisible()){
            label_edittask_null_error.setVisible(true);
            return false;
        }
        return true;
    }

    public void saveTask(MouseEvent event) throws SQLException {
        if(taskErrorCheck()){
            task.setTask_name(tf_edittask_name.getText());
            task.setTask_created_date(Date.valueOf(dp_edittask_datestart.getValue()));
            task.setTask_techs_required(Integer.parseInt(cb_edittask_numbertechs.getSelectionModel().getSelectedItem().toString()));
            task.setTask_duration(Integer.parseInt(cb_edittask_duration.getSelectionModel().getSelectedItem().toString()));
            task.setPriority(Integer.parseInt(cb_edittask_priority.getSelectionModel().getSelectedItem().toString()));
            task.setTask_notes(ta_edittask_notes.getText());
            if(cb_edittask_passfail.getSelectionModel().getSelectedItem() != null) {
                task.setPass_fail(cb_edittask_passfail.getSelectionModel().getSelectedItem().toString());
            }
            task.setCompleted(check_edittask_complete.isSelected());
            if(task.isCompleted()) {
                task.setCompleted_date(Date.valueOf(dp_edittask_datecomplete.getValue()));
            }
            taskRepo.updateTask(task);
            dashboardController.showTasksPane();
        }
    }

    public void saveTaskTech(MouseEvent event) throws SQLException {
        User tech = table_availabletechs.getSelectionModel().getSelectedItem();
        ObservableList<User> techList = FXCollections.observableArrayList();
        ObservableList<Task> userTaskList = FXCollections.observableArrayList();
        String techsRequired = cb_edittask_numbertechs.getSelectionModel().getSelectedItem().toString();
        // if technician is selected from available technicians
        if( tech != null){
            task.setAssigned_date(Date.valueOf(LocalDate.now()));
            // if required number of techs is set
            if(!techsRequired.equals("") || techsRequired == null){
                //if technician list is set
                if (task.getTask_tech() != null) {
                    techList = task.getTask_tech();
                    // if current techs is lower than required number, and technician to add is not already present in current techs list
                    if(techList.size() < Integer.valueOf(techsRequired)) {
                        if (!task.getTask_tech().stream().anyMatch(listtech -> listtech.getUser_id() == tech.getUser_id())) {
                            if (tech.getUser_tasks() != null) {
                                userTaskList = tech.getUser_tasks();
                            }
                            userTaskList.add(task);
                            tech.setUser_tasks(userTaskList);
                            tech.setUser_available_time(tech.getUser_available_time() - task.getTask_duration());
                            userRepo.updateUser(tech);
                            techList.add(tech);
                            task.setTask_tech(techList);
                            taskRepo.newUserTask(tech.getUser_id(), task.getTask_id(), Date.valueOf(LocalDate.now()));
                            taskRepo.updateTask(task);
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "This technician is already assigned to task");
                            alert.showAndWait();
                        }
                    }else{
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot add more technicians, maximum allowed is " + cb_edittask_numbertechs.getSelectionModel().getSelectedItem().toString());
                        alert.showAndWait();
                    }
                } else{
                    techList.add(tech);
                    task.setTask_tech(techList);
                    userTaskList.add(task);
                    tech.setUser_tasks(userTaskList);
                    tech.setUser_available_time(tech.getUser_available_time() - task.getTask_duration());
                    userRepo.updateUser(tech);
                    taskRepo.newUserTask(tech.getUser_id(), task.getTask_id(), Date.valueOf(LocalDate.now()));
                    taskRepo.updateTask(task);
                }
            }
        }
    }

    //Dashboard Navigation
    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }
    public void hidePage(){
        tedit_main.setVisible(false);
    }
    public void showPage(){
        tedit_main.toFront();
        tedit_main.setVisible(true);
    }
    public void goBack(MouseEvent event){
        dashboardController.showTasksPane();
    }

}
