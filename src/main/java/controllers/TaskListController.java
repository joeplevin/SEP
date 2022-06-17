package controllers;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import models.Job;
import models.Task;
import models.TaskPref;
import models.User;
import org.kordamp.ikonli.javafx.FontIcon;
import repositories.*;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

public class TaskListController {

    @FXML
    public Node tmain;
    @FXML
    public TextField tf_tlist_filter;
    @FXML
    public Button button_new_task;
    @FXML
    public CheckBox check_newtasks;
    @FXML
    public CheckBox check_pasttasks;
    @FXML
    public CheckBox check_completetasks;
    @FXML
    public CheckBox check_opentasks;
    @FXML
    public CheckBox check_mytasks;


    @FXML
    public TableView<Task> table_tlist;
    @FXML
    public TableColumn<Task,Number> tablec_taskJob;
    @FXML
    public TableColumn<Task,String> tablec_taskName;
    @FXML
    public TableColumn<Task,String> tablec_taskType;
    @FXML
    public TableColumn<Task, List<User>> tablec_taskTech;
    @FXML
    public TableColumn<Task, Number> tablec_taskTechRequired;
    @FXML
    public TableColumn<Task, Date> tablec_taskDate;
    @FXML
    public TableColumn<Task,Number> tablec_taskDuration;
    @FXML
    public TableColumn<Task,Number> tablec_taskPriority;
    @FXML
    public TableColumn<Task, String> tablec_taskPassFail;
    @FXML
    public TableColumn<Task, Boolean> tablec_taskComplete;
    @FXML
    public TableColumn<Task, Boolean> tablec_taskEdit;
    @FXML
    public TableColumn<Task,Boolean> tablec_taskDelete;

    //Focused Pane
    // Task Details
    @FXML
    public AnchorPane anchor_task_details;
    @FXML
    public Label label_focus_taskname;
    @FXML
    public Label label_focus_tasktype;
    @FXML
    public Label label_focus_tasktypeval;
    @FXML
    public Label label_focus_taskcreated;
    @FXML
    public Label label_focus_taskcreatedval;
    @FXML
    public Label label_focus_tasktechreq;
    @FXML
    public Label label_focus_tasktechreqval;
    @FXML
    public Label label_focus_taskpriority;
    @FXML
    public Label label_focus_taskpriorityval;
    @FXML
    public Label label_focus_taskstatus;
    @FXML
    public Label label_focus_taskstatusval;
    @FXML
    public Label label_focus_taskjob;
    @FXML
    public Label label_focus_taskjobval;
    @FXML
    public Label label_assigntask;
    @FXML
    public Label label_assignedtechs;

    //Available Technicians Table
    @FXML
    public TableView<User> table_availabletechs;
    @FXML
    public TableColumn<User,String> tc_avtech_name;
    @FXML
    public TableColumn<User,Number> tc_avtech_timeav;
    @FXML
    public Button button_addtech;

    //Technician Task Prefs Table
    @FXML
    public TableView<TaskPref> table_avtechtaskprefs;
    @FXML
    public TableColumn<TaskPref,String> tc_techtp_taskpref;
    @FXML
    public TableColumn<TaskPref,Number> tc_techtp_prof;

    //Assigned Technician(s) Table
    @FXML
    public TableView<User> table_tasktech;
    @FXML
    public TableColumn<User,String> tc_tasktech_name;
    @FXML
    public TableColumn<User,Boolean> tc_tasktech_delete;
    @FXML
    public StackPane sp_tasklistprogress;
    @FXML
    public ProgressIndicator pi_tasklistprogress;

    @FXML
    private DashboardController dashboardController;

    private ObservableList<Task> taskData = FXCollections.observableArrayList();
    private ObservableList<User> staff = FXCollections.observableArrayList();
    private ObservableList<Predicate<Task>> predicates = FXCollections.observableArrayList();
    private TaskRepository taskRepo = new TaskRepositoryImpl();
    private JobRepository jobRepo = new JobRepositoryImpl();
    private TaskPrefRepository taskPrefsRepo = new TaskPrefRepositoryImpl();
    private UserRepository userRepo = new UserRepositoryImpl();


    public void initialize(){
        //Focus pane listener
        ChangeListener<Object> listener = (obsval, oldval, newval) ->{
            if(table_tlist.isFocused()){
                try {
                    showFocusedTask();
                    button_addtech.setVisible(true);
                    label_assignedtechs.setVisible(true);
                    label_assigntask.setVisible(true);
                    label_focus_taskname.setVisible(true);
                    anchor_task_details.setVisible(true);
                    table_availabletechs.setVisible(true);
                    table_avtechtaskprefs.setVisible(true);
                    table_tasktech.setVisible(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        table_tlist.focusedProperty().addListener(listener);
        table_tlist.getSelectionModel().selectedItemProperty().addListener(listener);
    }

    public void setTasksTable(ObservableList<Task> data, ObservableList<User> userList){
        staff = userList;
        taskData = data;
        //set focused pane visibility
        for(Task task : taskData){
            // Initial Inspection Completion Listener - checks if tasks have been added to job & prompt user to add more if not.
            if(task.getTask_type().equals("Initial Inspection")){
                task.completedProperty().addListener((obsval,oldval,newval)->{
                    if(newval == true) {
                        if (task.getPass_fail().equals("Pass")) {
                            Job job = null;
                            try {
                                job = jobRepo.findJobById(task.getTask_job());
                                job.setJobTask(jobRepo.getTasksbyJobId(job.getJob_id()));
                                if (job.getJobTask().size() == 1) {
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Additional tasks for job need to be added, add now?", ButtonType.YES, ButtonType.NO);
                                    alert.showAndWait();
                                    if (alert.getResult() == ButtonType.YES) {
                                        dashboardController.showEditJobPane(job);
                                    }
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            //If initial inspection not failed, cannot otherwise be completed.
                        } else if(task.getPass_fail() == null || task.getPass_fail().equals("")){
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Inspection has not been passed");
                            task.setCompleted(false);
                            alert.showAndWait();
                        }
                    }
                });
            }
            if(task.getTask_type().equals("Final Inspection")){
                task.pass_failProperty().addListener((obsval,oldval,newval)->{
                    Job job = null;
                    if(newval == "Fail"){
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Failing this inspection will mark other tasks as complete. Would you like to add appropriate new tasks now?",ButtonType.YES,ButtonType.NO,ButtonType.CANCEL);
                        alert.showAndWait();
                        if(alert.getResult() != ButtonType.CANCEL){
                            job = jobRepo.findJobById(task.getTask_job());
                            job.setJobTask(jobRepo.getTasksbyJobId(job.getJob_id()));
                            ObservableList<Task> taskList = FXCollections.observableArrayList();
                            for(Task t : job.getJobTask()){
                                if(!t.getTask_type().equals("Final Inspection")) {
                                    t.setCompleted(true);
                                    t.setCompleted_date(Date.valueOf(LocalDate.now()));
                                }
                                taskRepo.updateTask(t);
                                taskList.add(t);
                            }
                            job.setJobTask(taskList);
                            jobRepo.updateJob(job);
                        }
                        if(alert.getResult() == ButtonType.YES){
                            try {
                                dashboardController.showEditJobPane(job);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }

        // Filter Checkbox listeners
        check_completetasks.selectedProperty().addListener((obsval,oldval,newval)->{
            Predicate<Task> complete = task -> task.isCompleted();
            if(newval == true){
                check_opentasks.setDisable(true);
                predicates.add(complete);
            } else{
                check_opentasks.setDisable(false);
                predicates.remove(complete);
            }
            filterTaskList();
        });
        check_opentasks.selectedProperty().addListener((obsval,oldval,newval)->{
            Predicate<Task> open = task -> !task.isCompleted();
            if(newval == true){
                check_completetasks.setDisable(true);
                predicates.add(open);
            } else{
                check_completetasks.setDisable(false);
                predicates.remove(open);
            }
            filterTaskList();
        });
        check_newtasks.selectedProperty().addListener((obsval,oldval,newval)->{
            Predicate<Task> newTask = task -> task.getTask_created_date().toLocalDate().equals(LocalDate.now());
            if(newval == true){
                check_pasttasks.setDisable(true);
                predicates.add(newTask);
            } else{
                check_pasttasks.setDisable(false);
                predicates.remove(newTask);
            }
            filterTaskList();
        });
        check_pasttasks.selectedProperty().addListener((obsval,oldval,newval)->{
            Predicate<Task> past = task -> !task.getTask_created_date().toLocalDate().equals(LocalDate.now());
            if(newval == true){
                check_newtasks.setDisable(true);
                predicates.add(past);
            } else{
                check_newtasks.setDisable(false);
                predicates.remove(past);
            }
            filterTaskList();
        });

        //Filter My Task checkbox
        Predicate<Task> my = task -> {
            if(task.getTask_created_by() == dashboardController.getCurrent().getUser_id()){
                return true;
            }if(task.getTask_tech() != null){
                if(task.getTask_tech().stream().anyMatch(tasktech -> tasktech.getUser_id() == dashboardController.getCurrent().getUser_id())){
                    return true;
                }
            }
            return false;
        };
        check_mytasks.selectedProperty().addListener((obsval,oldval,newval)->{
            if(newval == true){
                predicates.add(my);
            }else{
                predicates.remove(my);
            }
            filterTaskList();
        });

        //Filter Textfield not working on Technician name without this
        Predicate<Task> text = task -> {
            String search = tf_tlist_filter.getText().toLowerCase();
            String name = "";
            if(task.getTask_tech() != null) {
                for (User user : task.getTask_tech()) {
                    name = user.getFName() + " " + user.getSName();
                    if (name.toLowerCase().contains(search)) {
                        return true;
                    }
                }
            }
            if (task.task_nameProperty().toString().toLowerCase().indexOf(search) > -1) {
                return true;
            }else if (task.task_created_dateProperty().toString().toLowerCase().indexOf(search) > -1) {
                return true;
            }else if (task.priorityProperty().toString().toLowerCase().indexOf(search) > -1) {
                return true;
            }else if (task.task_jobProperty().toString().toLowerCase().indexOf(search) > -1) {
                return true;
            }else if (task.pass_failProperty().toString().toLowerCase().indexOf(search) > -1) {
                return true;
            }else if (task.task_typeProperty().toString().toLowerCase().indexOf(search) > -1) {
                return true;
            }else {
                return false;
            }
        };
        tf_tlist_filter.textProperty().addListener((obsval,oldval,newval)->{
            if (newval != null) {
                predicates.add(text);
            } else{
                predicates.remove(text);
            }
            filterTaskList();
        });

        //set columns
        tablec_taskJob.setCellValueFactory(cellData -> cellData.getValue().task_jobProperty());
        tablec_taskName.setCellValueFactory(cellData -> cellData.getValue().task_nameProperty());
        tablec_taskType.setCellValueFactory(cellData -> cellData.getValue().task_typeProperty());
        tablec_taskDuration.setCellValueFactory(cellData -> cellData.getValue().task_durationProperty());
        tablec_taskPriority.setCellValueFactory(cellData -> cellData.getValue().priorityProperty());
        tablec_taskDate.setCellValueFactory(cellData -> cellData.getValue().task_created_dateProperty());
        tablec_taskTechRequired.setCellValueFactory(cellData -> cellData.getValue().task_techs_requiredProperty());

        //Task Complete Checkbox
        tablec_taskComplete.setCellValueFactory(cellData -> cellData.getValue().completedProperty());
        tablec_taskComplete.setCellFactory(CheckBoxTableCell.forTableColumn(tablec_taskComplete));

        //Task Pass/Fail Dropdown
        ObservableList<String> passFail = FXCollections.observableArrayList("","Pass","Fail");
        tablec_taskPassFail.setCellValueFactory(cellData -> cellData.getValue().pass_failProperty());
        tablec_taskPassFail.setCellFactory(ChoiceBoxTableCell.forTableColumn(passFail));

        //Task Technicians List cell
        PropertyValueFactory<Task,List<User>> techColFactory = new PropertyValueFactory<>("task_tech");
        tablec_taskTech.setCellValueFactory(techColFactory);
        tablec_taskTech.setCellFactory(techColumn -> {
            ListView<User> techList = new ListView<>();
            techList.setMaxHeight(55);
            techList.setPrefHeight(55);
            techList.setCellFactory(techListView -> new ListCell<User>(){
                @Override
                public void updateItem(User user, boolean empty){
                    techListView.setMaxHeight(55);
                    techListView.setPrefHeight(55);
                    super.updateItem(user,empty);
                    if(empty){
                        setText(null);
                        setGraphic(null);
                    } else{
                        setText(user.getFName() + " " + user.getSName());
                        setTextFill(Color.WHITE);
                    }
                }
            });
            return new TableCell<Task, List<User>>(){
                @Override
                public void updateItem(List<User> task_tech, boolean empty){
                    super.updateItem(task_tech,empty);
                    if(empty){
                        setGraphic(null);
                        setText(null);
                    } else{
                        Task task = getTableView().getItems().get(getIndex());
                        if(task.getTask_tech() != null) {
                            techList.getItems().setAll(task_tech);
                            setGraphic(techList);
                        } else{
                            setGraphic(null);
                            setText(null);
                        }
                    }
                }
            };
        });

        //boolean cell value so cell will only show on !empty rows
        tablec_taskEdit.setCellValueFactory(params -> new SimpleBooleanProperty(params.getValue() != null));
        tablec_taskDelete.setCellValueFactory(params -> new SimpleBooleanProperty(params.getValue() != null));

        //create cell factory with update button & pass params to new controller
        tablec_taskEdit.setCellFactory(updateTaskColumn -> new TableCell<Task,Boolean>(){
            //set button to cell
            final Button button_edit_task = new Button();
            //override cell factory
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                //if no row data, do not display
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else{
                    //button set up
                    button_edit_task.setGraphic(new FontIcon("fltfal-edit-16"));
                    button_edit_task.setId("button_edit_task");

                    setGraphic(button_edit_task);
                    setText(null);
                    //update on click
                    button_edit_task.setOnAction(event -> {
                        //get user to pass to update view
                        Task task = getTableView().getItems().get(getIndex());
                        //update view
                        editTask(task);
                    });
                }
            }
        });

        //create cell factory with delete button & pass params for deletion
        tablec_taskDelete.setCellFactory(deleteColumn -> new TableCell<Task,Boolean>() {
            Button button_delete_task = new Button();
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else{
                    button_delete_task.setGraphic(new FontIcon("fltral-delete-16"));
                    button_delete_task.setId("button_delete_task");
                    button_delete_task.setOnAction(event -> {
                        Task task = getTableView().getItems().get(getIndex());
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " + task.getTask_name() + "?", ButtonType.YES, ButtonType.NO);
                        alert.showAndWait();
                        if(alert.getResult() == ButtonType.YES) {
                            // Remove from tableview
                            taskData.remove(task);
                            // Remove from db
                            taskRepo.deleteById(task.getTask_id());
                            // User tasks
                            if(task.getTask_tech() != null) {
                                for (User u : task.getTask_tech()){
                                    taskRepo.deleteUserTask(task.getTask_id(),u.getUser_id());
                                    u.setUser_available_time(u.getUser_available_time() + task.getTask_duration());
                                    userRepo.updateUser(u);
                                }
                            }
                        }
                    });
                    setGraphic(button_delete_task);
                    setText(null);
                }
            }
        });
        table_tlist.setEditable(true);
        table_tlist.setItems(taskData);
    }

    private void editTask(Task task) {
        dashboardController.showEditTaskPane(task);
    }

    public void filterTaskList(){
        FilteredList<Task> filteredTaskList = new FilteredList<>(taskData);
        SortedList<Task> sortedList = new SortedList<>(filteredTaskList);
        table_tlist.setItems(taskData);
        filteredTaskList.setPredicate(predicates.stream().reduce(x -> true, Predicate::and));
        sortedList.comparatorProperty().bind(table_tlist.comparatorProperty());
        table_tlist.setEditable(true);
        table_tlist.setItems(taskData);
        table_tlist.setItems(sortedList);
    }

    public void showFocusedTask() throws IOException{
        Task task = table_tlist.getSelectionModel().getSelectedItem();
        if(task != null){
            label_focus_taskname.setText(task.getTask_name());
            label_focus_taskcreatedval.setText(task.getTask_created_date().toString());
            label_focus_tasktechreqval.setText(String.valueOf(task.getTask_techs_required()));
            label_focus_taskpriorityval.setText(String.valueOf(task.getPriority()));
            label_focus_taskjobval.setText(String.valueOf(task.getTask_job()));
            label_focus_tasktypeval.setText(task.getTask_type());
            String status = null;
            if(task.getPass_fail() == null || task.getPass_fail().equals("")){
                if(task.getTask_tech() == null){
                    status = "Unassigned";
                } else{
                    status = "Assigned";
                }
                if(task.isCompleted()){
                    status = "Complete";
                }
            } else{
                status = task.getPass_fail();
            }
            label_focus_taskstatusval.setText(status);
            ObservableList<User> availableTechs = FXCollections.observableArrayList();
            ObservableMap<Integer, ObservableList<TaskPref>> taskPrefs = FXCollections.observableHashMap();
            for(User u : staff){
                if(u.getRole().equals("Technician")){
                    taskPrefs.put(u.getUser_id(),taskPrefsRepo.getTaskPrefsById(u.getUser_id()));
                    availableTechs.add(u);
                }
            }
            table_availabletechs.getSelectionModel().selectedItemProperty().addListener((obsval,oldval,newval) ->{
                if(newval != null) {
                    User u = table_availabletechs.getSelectionModel().getSelectedItem();
                    ObservableList<TaskPref> userPrefs = taskPrefs.get(u.getUser_id());
                    table_avtechtaskprefs.setItems(userPrefs);
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
                                //Remove task from user
                                userRepo.updateUser(tech);
                                //Remove user from task
                                taskRepo.updateTask(task);
                                //Remove user-task from db
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
    }

    public void saveTaskTech(MouseEvent event) throws SQLException {
        User tech = table_availabletechs.getSelectionModel().getSelectedItem();
        ObservableList<User> techList = FXCollections.observableArrayList();
        ObservableList<Task> userTaskList = FXCollections.observableArrayList();
        int techsRequired = table_tlist.getSelectionModel().getSelectedItem().getTask_techs_required();
        Task task = table_tlist.getSelectionModel().getSelectedItem();
        // if technician is selected from available technicians
        if( tech != null){
            task.setAssigned_date(Date.valueOf(LocalDate.now()));
            // if required number of techs is set
            if(techsRequired > 0){
                //if technician list is set
                if (task.getTask_tech() != null) {
                    techList = task.getTask_tech();
                    // if current techs is lower than required number, and technician to add is not already present in current techs list
                    if(techList.size() < techsRequired) {
                        if (!task.getTask_tech().stream().anyMatch(listtech -> listtech.getUser_id() == tech.getUser_id())) {
                            if(task.getTask_duration() <= tech.getUser_available_time()) {
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
                            } else{
                                Alert alert = new Alert(Alert.AlertType.ERROR, "This technician does not have time for this task");
                                alert.showAndWait();
                            }
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "This technician is already assigned to task");
                            alert.showAndWait();
                        }
                    }else{
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot add more technicians, maximum allowed is " + techsRequired);
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

    // Dashboard Navigation
    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }
    public void newTask(MouseEvent event) {
        dashboardController.showNewTaskPane();
    }
    public void hidePage(){
        tmain.setVisible(false);
    }
    public void showPage(){
        tmain.toFront();
        tmain.setVisible(true);
    }
}
