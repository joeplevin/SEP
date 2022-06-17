package controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import models.Job;
import models.Task;
import models.User;
import org.kordamp.ikonli.javafx.FontIcon;
import repositories.JobRepository;
import repositories.JobRepositoryImpl;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

public class JobListController {
    @FXML
    public Node jmain;
    @FXML
    public CheckBox check_completejobs;
    @FXML
    public CheckBox check_openjobs;
    @FXML
    public CheckBox check_newjobs;
    @FXML
    public CheckBox check_pastjobs;
    @FXML
    public CheckBox check_myjobs;
    @FXML
    public Label label_check_error;
    @FXML
    public Button button_new_job;

    @FXML
    public TableView<Job> table_jlist;
    @FXML
    public TableColumn<Job,String> tablec_jobName;
    @FXML
    public TableColumn<Job, Date> tablec_jobDate;
    @FXML
    public TableColumn<Job,Number> tablec_jobDuration;
    @FXML
    public TableColumn<Job, Float> tablec_jobCost;
    @FXML
    public TableColumn<Job, List<Task>> tablec_jobTasks;
    @FXML
    private TableColumn<Job, List<User>> tablec_jobStaff;
    @FXML
    public TableColumn<Job, Boolean> tablec_jobComplete;
    @FXML
    public TableColumn<Job, Boolean> tablec_Jedit;
    @FXML
    public TableColumn<Job, Boolean> tablec_Jdelete;
    @FXML
    public TextField tf_jlist_filter;
    @FXML
    public StackPane sp_joblistprogress;
    @FXML
    public ProgressIndicator pi_joblistprogress;

    private DashboardController dashboardController;
    private JobRepository jobRepo = new JobRepositoryImpl();
    private ObservableList<Job> jobData = FXCollections.observableArrayList();
    private ObservableList<Predicate<Job>> predicates = FXCollections.observableArrayList();

    public void setJobsTable(ObservableList<Job> jobs){
        jobData = jobs;
        //Can't complete job with incomplete tasks listener
        for(Job job : jobData){
            job.job_completedProperty().addListener((obsval,oldval,newval) ->{
                if(newval == true) {
                    int complete = 0;
                    ObservableList<Task> taskList = job.getJobTask();
                    for (Task task : taskList) {
                        if (task.isCompleted()) {
                            complete++;
                        }
                    }
                    if (complete < taskList.size()) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot complete a job with incomplete tasks");
                        alert.showAndWait();
                        job.setJob_completed(false);
                    } else {
                        job.setJob_completed(newval);
                        jobRepo.updateJob(job);
                    }
                }
            });
        }

        //Filter Checkbox Listeners
        check_completejobs.selectedProperty().addListener((obsval,oldval,newval)->{
            Predicate<Job> complete = job -> job.getJob_completed();
            if(newval == true){
                check_openjobs.setDisable(true);
                predicates.add(complete);
            } else{
                check_openjobs.setDisable(false);
                predicates.remove(complete);
            }
            filterJobList();
        });
        check_openjobs.selectedProperty().addListener((obsval,oldval,newval)->{
            Predicate<Job> open = job -> !job.getJob_completed();
            if(newval == true){
                check_completejobs.setDisable(true);
                predicates.add(open);
            } else{
                check_completejobs.setDisable(false);
                predicates.remove(open);
            }
            filterJobList();
        });
        check_newjobs.selectedProperty().addListener((obsval,oldval,newval)->{
            Predicate<Job> newJob = job -> job.getJob_created_date().toLocalDate().equals(LocalDate.now());
            if(newval == true){
                check_pastjobs.setDisable(true);
                predicates.add(newJob);
            }else{
                check_pastjobs.setDisable(false);
                predicates.remove(newJob);
            }
            filterJobList();
        });
        check_pastjobs.selectedProperty().addListener((obsval,oldval,newval)->{
            Predicate<Job> past = job -> !job.getJob_created_date().toLocalDate().equals(LocalDate.now());
            if(newval == true){
                check_newjobs.setDisable(true);
                predicates.add(past);
            }else{
                check_newjobs.setDisable(false);
                predicates.remove(past);
            }
            filterJobList();
        });
        Predicate<Job> my = job -> {
            if(job.getJob_creator() == dashboardController.getCurrent().getUser_id()
                || job.getJobStaff().stream().anyMatch(jobstaff -> jobstaff.getUser_id() == dashboardController.getCurrent().getUser_id())){
                return true;
            }
            return false;
        };
        check_myjobs.selectedProperty().addListener((obsval,oldval,newval)->{
            if(newval == true){
                predicates.add(my);
            }else{
                predicates.remove(my);
            }
            filterJobList();
        });
        Predicate<Job> text = job -> {
            String search = tf_jlist_filter.getText().toLowerCase();
            String name = "";
            if(job.getJobStaff() != null){
                for(User u : job.getJobStaff()){
                    name = u.getFName() + " " + u.getSName();
                if (name.toLowerCase().contains(search)) {
                    return true;
                }
                }
            }
            if (job.job_nameProperty().toString().toLowerCase().indexOf(search) > -1) {
                return true;
            } else if (job.job_cost_estProperty().toString().toLowerCase().indexOf(search) > -1) {
                return true;
            } else if (job.jobTaskProperty().toString().toLowerCase().indexOf(search) > -1) {
                return true;
            } else {
                return false;
            }
        };
        tf_jlist_filter.textProperty().addListener((obsval,oldval,newval)->{
            if(newval != null) {
                predicates.add(text);
            } else{
                predicates.remove(text);
            }
            filterJobList();
        });

        //Table Setup
        tablec_jobName.setCellValueFactory(cellData -> cellData.getValue().job_nameProperty());
        tablec_jobDate.setCellValueFactory(cellData -> cellData.getValue().job_created_dateProperty());
        tablec_jobDuration.setCellValueFactory(cellData -> cellData.getValue().job_duration_estProperty());
        tablec_jobCost.setCellValueFactory(cellData -> cellData.getValue().job_cost_estProperty().asObject());

        //Display Cost with £
        tablec_jobCost.setCellFactory(costColumn -> {
            return new TableCell<Job, Float>() {
                @Override
                public void updateItem(Float cost, boolean empty) {
                    super.updateItem(cost, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText("£" + cost.floatValue());
                    }
                }
            };
        });

        //Display Days on Duration
        tablec_jobDuration.setCellFactory(durationColumn -> {
            return new TableCell<Job, Number>() {
                @Override
                public void updateItem(Number duration, boolean empty) {
                    super.updateItem(duration, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        if(duration.intValue() == 1){
                            setText(duration.intValue() + " day");
                        } else{
                            setText(duration.intValue() + " days");
                        }
                    }
                }
            };
        });

        //List of tasks for each job setup
        PropertyValueFactory<Job,List<Task>> taskColFactory = new PropertyValueFactory<>("jobTask");
        tablec_jobTasks.setCellValueFactory(taskColFactory);
        tablec_jobTasks.setCellFactory(tasksColumn -> {
            ListView<Task> taskList = new ListView<>();

            taskList.setMaxHeight(100);
            taskList.setPrefHeight(100);
            taskList.setCellFactory(taskListView -> {
                return new ListCell<Task>() {
                    @Override
                    public void updateItem(Task task, boolean empty) {
                        taskListView.setMaxHeight(100);
                        taskListView.setPrefHeight(100);
                        super.updateItem(task, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);

                        } else {
                            setText(task.getTask_name());
                            setTaskColour(task,this);
                        }
                    }
                };
            });
            return new TableCell<Job, List<Task>>(){
                @Override
                public void updateItem(List<Task> job_tasks, boolean empty){
                    super.updateItem(job_tasks,empty);
                    if(empty){
                        setGraphic(null);
                        setText(null);
                    } else{
                        Job job = getTableView().getItems().get(getIndex());
                        if(job.getJobTask() != null) {
                            taskList.getItems().setAll(job_tasks);
                            setGraphic(taskList);
                        } else{
                            setGraphic(null);
                            setText(null);
                        }
                    }
                }
            };
        });

        //List of users for each job setup
        PropertyValueFactory<Job,List<User>> staffColFactory = new PropertyValueFactory<>("jobStaff");
        tablec_jobStaff.setCellValueFactory(staffColFactory);
        tablec_jobStaff.setCellFactory(staffColumn -> {
            ListView<User> staffList = new ListView<>();
            staffList.setMaxHeight(100);
            staffList.setPrefHeight(100);
            staffList.setCellFactory(staffListView -> new ListCell<User>(){
                @Override
                public void updateItem(User user, boolean empty){
                    staffListView.setMaxHeight(100);
                    staffListView.setPrefHeight(100);
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
            return new TableCell<Job, List<User>>(){
                @Override
                public void updateItem(List<User> job_staff, boolean empty){
                    super.updateItem(job_staff,empty);
                    if(empty){
                        setGraphic(null);
                        setText(null);
                    } else{
                        Job job = getTableView().getItems().get(getIndex());
                        if(job.getJobStaff() != null) {
                            staffList.getItems().setAll(job_staff);
                            setGraphic(staffList);
                        } else{
                            setGraphic(null);
                            setText(null);
                        }
                    }
                }
            };
        });

        //Job complete checkbox
        tablec_jobComplete.setCellValueFactory(cellData -> cellData.getValue().job_completedProperty());
        tablec_jobComplete.setCellFactory(CheckBoxTableCell.forTableColumn(tablec_jobComplete));

        // create cell factory with update button & pass params to new controller
        tablec_Jedit.setCellValueFactory(params -> new SimpleBooleanProperty(params.getValue() != null));
        tablec_Jedit.setCellFactory(updateJobColumn -> new TableCell<Job,Boolean>(){
            //set button to cell
            final Button button_edit_job = new Button();
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
                    button_edit_job.setGraphic(new FontIcon("fltfal-edit-16"));
                    button_edit_job.setId("button_edit_job");
                    setGraphic(button_edit_job);
                    setText(null);
                    //update on click
                    button_edit_job.setOnAction(event -> {
                        //get user to pass to update view
                        Job job = getTableView().getItems().get(getIndex());
                        //update view
                        editJob(job);
                    });
                }
            }
        });

        //create cell factory with delete button & pass params for deletion
        tablec_Jdelete.setCellValueFactory(params -> new SimpleBooleanProperty(params.getValue() != null));
        tablec_Jdelete.setCellFactory((TableColumn<Job, Boolean> deleteColumn) -> new TableCell<Job,Boolean>() {
            Button button_delete_job = new Button();
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else{
                    button_delete_job.setGraphic(new FontIcon("fltral-delete-16"));
                    button_delete_job.setId("button_delete_job");
                    button_delete_job.setOnAction(event -> {
                        Job job = getTableView().getItems().get(getIndex());
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this job?", ButtonType.YES, ButtonType.NO);
                        alert.showAndWait();
                        if(alert.getResult() == ButtonType.YES) {
                            jobData.remove(job);
                            jobRepo.deleteById(job.getJob_id());
                        }
                    });
                    setGraphic(button_delete_job);
                    setText(null);
                }
            }
        });

        table_jlist.setEditable(true);
        table_jlist.setItems(jobData);
    }

    private void setTaskColour(Task task, ListCell<Task> listCell) {
        if(task.isCompleted()){
            listCell.setStyle("-fx-text-fill: lightgreen");
        } else if(!task.isCompleted()){
            listCell.setStyle("-fx-text-fill: khaki");
        }
        if (task.getPass_fail() != null && task.getPass_fail().equals("Fail")) {
                listCell.setStyle("-fx-text-fill: darksalmon");
        }
    }

    private void editJob(Job job) {
        try {
            dashboardController.showEditJobPane(job);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Filter function
    public void filterJobList(){
        FilteredList<Job> filteredJobList = new FilteredList<>(jobData, b -> true);
        SortedList<Job> sortedList = new SortedList<>(filteredJobList);
        filteredJobList.setPredicate(predicates.stream().reduce(Predicate::and).orElse(x -> true));
        sortedList.comparatorProperty().bind(table_jlist.comparatorProperty());
        table_jlist.setEditable(true);
        table_jlist.setItems(sortedList);
    }

    //Dashboard Navigation
    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }
    public void newJob(MouseEvent event) {
        dashboardController.showNewJobPane();
    }
    public void hidePage(){
        jmain.setVisible(false);
    }
    public void showPage(){
        jmain.toFront();
        jmain.setVisible(true);
    }

}
