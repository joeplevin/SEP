package controllers;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import models.Job;
import models.Task;
import org.kordamp.ikonli.javafx.FontIcon;
import repositories.JobRepository;
import repositories.JobRepositoryImpl;
import repositories.TaskRepository;
import repositories.TaskRepositoryImpl;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class EditJobController {
    @FXML
    public VBox jedit_main;
    @FXML
    public Button button_editjob_back;
    @FXML
    public Button button_save_jobedit;
    @FXML
    public Label label_editjob_title;

    //Job Details
    @FXML
    public TextField tf_editjob_name;
    @FXML
    public TextField tf_editjob_duration;
    @FXML
    public TextField tf_editjob_cost;
    @FXML
    public DatePicker dp_editjob_datestart;
    @FXML
    public DatePicker dp_editjob_dateend;
    @FXML
    public TextArea ta_editjob_notes;
    @FXML
    public CheckBox check_editjob_complete;
    @FXML
    public CheckBox check_editjob_delay;

    //Task Details
    @FXML
    public TextField tf_editjob_taskname;
    @FXML
    public ChoiceBox cb_editjob_tasktype;
    @FXML
    public ChoiceBox cb_editjob_tasktech;
    @FXML
    public ChoiceBox cb_editjob_taskduration;
    @FXML
    public ChoiceBox cb_editjob_taskpriority;
    @FXML
    public Button button_editjob_addtask;
    @FXML
    public TableView<Task> table_editjob_tasks;
    @FXML
    public TableColumn<Task, String> tc_editjob_tname;
    @FXML
    public TableColumn<Task, String> tc_editjob_ttype;
    @FXML
    public TableColumn<Task, Number> tc_editjob_tduration;
    @FXML
    public TableColumn<Task, Number> tc_editjob_tpriority;
    @FXML
    public TableColumn<Task, Boolean> tc_editjob_tcomplete;
    @FXML
    public TableColumn<Task, String> tc_editjob_tpassfail;
    @FXML
    public TableColumn<Task, Boolean> tc_editjob_tdelete;

    //Job Error Handling
    @FXML
    public Label label_jobname_null;
    @FXML
    public Label label_jobduration_null;
    @FXML
    public Label label_jobcost_null;
    @FXML
    public Label label_durationlength_error;
    @FXML
    public Label label_durationtype_error;
    @FXML
    public Label label_costvalue_error;
    @FXML
    public Label label_costformat_error;
    @FXML
    public Label label_jobcomplete_error;
    @FXML
    public Label label_editjob_null_error;

    //Task Error Handling
    @FXML
    public Label label_jobtname_null;
    @FXML
    public Label label_jobttype_null;
    @FXML
    public Label label_jobtpriority_null;
    @FXML
    public Label label_jobtduration_null;
    @FXML
    public Label label_jobttech_null;
    @FXML
    private Label label_editjobtasks_null_error;

    @FXML
    private DashboardController dashboardController;
    private TaskRepository taskRepo = new TaskRepositoryImpl();
    private JobRepository jobRepo = new JobRepositoryImpl();
    private Job currentJob = null;

    public void jobData(Job job){
        //Set current job data
        currentJob = job;
        //Set view
        jobDataViewSetup(job);
        jobTaskSetup(job);
    }

    private void jobDataViewSetup(Job job) {
        //Set data in edit job view
        //Listener to check if tasks have been completed before job completed
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
        label_editjob_title.setText("Edit Job: " + job.getJob_name());
        tf_editjob_name.setText(job.getJob_name());
        tf_editjob_cost.setText(Float.toString(job.getJob_cost_est()));
        //Listener to check cost formats & values are sensible
        tf_editjob_cost.textProperty().addListener((obsval,oldval,newval)->{
            if(!newval.matches("^[0-9]+\\.[0-9][0-9]$")){
                label_costformat_error.setManaged(true);
                label_costformat_error.setVisible(true);
            } else if(Float.parseFloat(newval) > 1000){
                label_costformat_error.setVisible(false);
                label_costformat_error.setManaged(false);
                label_costvalue_error.setVisible(true);
            } else{
                label_costvalue_error.setVisible(false);
                label_costformat_error.setVisible(false);
            }
        });
        tf_editjob_duration.setText(Integer.toString(job.getJob_duration_est()));
        //Listener to check that duration format & values are sensible
        tf_editjob_duration.textProperty().addListener((obsval,oldval,newval)-> {
            if (newval.matches("[0-9]+")) {
                dp_editjob_dateend.setValue(LocalDate.now().plusDays(Long.parseLong(newval)));
                label_durationtype_error.setVisible(false);
                label_durationtype_error.setManaged(false);
                if (Integer.parseInt(newval) > 10) {
                    label_durationtype_error.setManaged(false);
                    label_durationlength_error.setManaged(true);
                    label_durationlength_error.setVisible(true);
                } else {
                    label_durationlength_error.setVisible(false);
                    label_durationlength_error.setManaged(false);
                }
            } else {
                if (!tf_editjob_duration.getText().equals("")) {
                    label_durationtype_error.setManaged(true);
                    label_durationtype_error.setVisible(true);
                } else {
                    label_durationtype_error.setVisible(false);
                    label_durationlength_error.setVisible(false);
                }
            }
        });
        dp_editjob_datestart.setValue(job.getJob_created_date().toLocalDate());
        dp_editjob_dateend.setValue(LocalDate.now().plusDays(Integer.parseInt(tf_editjob_duration.getText())));
        ta_editjob_notes.setText(job.getJob_notes());
        check_editjob_complete.setSelected(job.getJob_completed());
    }

    private void jobTaskSetup(Job job) {
        ObservableList<Task> taskData = currentJob.getJobTask();
        ObservableList<String> taskTypeList = FXCollections.observableArrayList();
        for(Task task : taskData){
            //Listeners to update task completed value & task pass/fail value
            task.completedProperty().addListener((obsval,oldval,newval)->{
                taskRepo.updateTask(task);
            });
            task.pass_failProperty().addListener((obsval,oldval,newval)->{
                taskRepo.updateTask(task);
            });
        }
        taskTypeList.add("");
        for( String taskType : taskRepo.getTaskTypeList()){
            taskTypeList.add(taskType);
        }
        ObservableList<String> numTechs = FXCollections.observableArrayList("","1","2");
        ObservableList<String> taskDuration = FXCollections.observableArrayList("","1","2","3","4","5","6","7","8");
        ObservableList<String> taskPriority = FXCollections.observableArrayList("","1","2","3","4","5");
        cb_editjob_tasktype.setItems(taskTypeList);
        cb_editjob_tasktech.setItems(numTechs);
        cb_editjob_taskduration.setItems(taskDuration);
        cb_editjob_taskpriority.setItems(taskPriority);

        //Task table set up
        tc_editjob_tname.setCellValueFactory(cellData -> cellData.getValue().task_nameProperty());
        tc_editjob_tduration.setCellValueFactory(cellData -> cellData.getValue().task_durationProperty());
        //Format duration column
        tc_editjob_tduration.setCellFactory(durationColumn -> {
            return new TableCell<Task, Number>() {
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

        tc_editjob_tpriority.setCellValueFactory(cellData -> cellData.getValue().priorityProperty());
        tc_editjob_ttype.setCellValueFactory(cellData -> cellData.getValue().task_typeProperty());

        ObservableList<String> passFail = FXCollections.observableArrayList("","Pass","Fail");
        tc_editjob_tpassfail.setCellValueFactory(cellData -> cellData.getValue().pass_failProperty());
        tc_editjob_tpassfail.setCellFactory(ChoiceBoxTableCell.forTableColumn(passFail));

        tc_editjob_tcomplete.setCellValueFactory(cellData -> cellData.getValue().completedProperty());
        tc_editjob_tcomplete.setCellFactory(CheckBoxTableCell.forTableColumn(tc_editjob_tcomplete));

        tc_editjob_tdelete.setCellValueFactory(params -> new SimpleBooleanProperty(params.getValue() != null));
        tc_editjob_tdelete.setCellFactory((TableColumn<Task, Boolean> deleteColumn) -> new TableCell<Task,Boolean>() {
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
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this task?", ButtonType.YES, ButtonType.NO);
                        alert.showAndWait();
                        if(alert.getResult() == ButtonType.YES) {
                            taskData.remove(task);
                            dashboardController.taskList.remove(task);
                            taskRepo.deleteById(task.getTask_id());
                            table_editjob_tasks.setItems(taskData);
                        }
                    });
                    setGraphic(button_delete_task);
                    setText(null);
                }
            }
        });

        table_editjob_tasks.setItems(taskData);
        table_editjob_tasks.setEditable(true);
    }

    public void goBack(MouseEvent e){
        clearJobFields();
        clearJobTaskFields();
        dashboardController.showJobsPane();
    }

    private void clearJobFields() {
        tf_editjob_name.clear();
        tf_editjob_duration.clear();
        tf_editjob_cost.clear();
        dp_editjob_dateend.setValue(null);
        ta_editjob_notes.clear();
        check_editjob_complete.setSelected(false);
        check_editjob_delay.setSelected(false);
        label_jobname_null.setVisible(false);
        label_jobduration_null.setVisible(false);
        label_jobcost_null.setVisible(false);
        label_durationlength_error.setVisible(false);
        label_durationtype_error.setVisible(false);
        label_costvalue_error.setVisible(false);
        label_costformat_error.setVisible(false);
        label_jobcomplete_error.setVisible(false);
        label_editjob_null_error.setVisible(false);
    }

    public void clearJobTaskFields(){
        tf_editjob_taskname.clear();
        cb_editjob_tasktype.getSelectionModel().clearSelection();
        cb_editjob_tasktech.getSelectionModel().clearSelection();
        cb_editjob_taskduration.getSelectionModel().clearSelection();
        cb_editjob_taskpriority.getSelectionModel().clearSelection();
        label_jobtname_null.setVisible(false);
        label_jobttype_null.setVisible(false);
        label_jobtpriority_null.setVisible(false);
        label_jobtduration_null.setVisible(false);
        label_jobttech_null.setVisible(false);
        label_editjobtasks_null_error.setVisible(false);

    }

    private void jobErrorCheck(){
        if(tf_editjob_name.getText().isEmpty()){
            label_jobname_null.setVisible(true);
        } else{
            label_jobname_null.setVisible(false);
        }
        if(tf_editjob_cost.getText().isEmpty()){
            label_jobcost_null.setVisible(true);
        } else{
            label_jobcost_null.setVisible(false);
        }
        if(tf_editjob_duration.getText().isEmpty()){
            label_jobduration_null.setVisible(true);
        } else{
            label_jobduration_null.setVisible(false);
        }
        if(label_jobname_null.isVisible() || label_jobcost_null.isVisible() || label_jobtduration_null.isVisible()){
            label_editjob_null_error.setVisible(true);
        } else{
            label_editjob_null_error.setVisible(false);
        }
    }
    private void taskErrorCheck(){
        if(tf_editjob_taskname.getText().isEmpty()){
            label_jobtname_null.setVisible(true);
        } else{
            label_jobtname_null.setVisible(false);
        }
        if(cb_editjob_taskpriority.getSelectionModel().getSelectedItem() == null || cb_editjob_taskpriority.getSelectionModel().getSelectedItem() == ""){
            label_jobtpriority_null.setVisible(true);
        } else{
            label_jobtpriority_null.setVisible(false);
        }
        if(cb_editjob_taskduration.getSelectionModel().getSelectedItem() == null || cb_editjob_taskduration.getSelectionModel().getSelectedItem() == ""){
            label_jobtduration_null.setVisible(true);
        } else{
            label_jobtduration_null.setVisible(false);
        }
        if(cb_editjob_tasktech.getSelectionModel().getSelectedItem() == null || cb_editjob_tasktech.getSelectionModel().getSelectedItem() == ""){
            label_jobttech_null.setVisible(true);
        } else{
            label_jobttech_null.setVisible(false);
        }
        if(cb_editjob_tasktype.getSelectionModel().getSelectedItem() == null || cb_editjob_tasktype.getSelectionModel().getSelectedItem() == ""){
            label_jobttype_null.setVisible(true);
        } else{
            label_jobttype_null.setVisible(false);
        }
        if(label_jobtname_null.isVisible() || label_jobtpriority_null.isVisible() || label_jobtduration_null.isVisible() || label_jobttype_null.isVisible() || label_jobttech_null.isVisible()){
            label_editjobtasks_null_error.setVisible(true);
        } else{
            label_editjobtasks_null_error.setVisible(false);
        }
    }

    public void saveJob(MouseEvent event) {
        jobErrorCheck();
        Alert alert = new Alert(Alert.AlertType.ERROR,"Please remember to add a final inspection task");
        if(currentJob.getJobTask().stream().anyMatch(jobtask -> jobtask.getTask_type().equals("Final Inspection"))){
            if(!label_editjob_null_error.isVisible()) {
                currentJob.setJob_name(tf_editjob_name.getText());
                currentJob.setJob_cost_est(Float.parseFloat(tf_editjob_cost.getText()));
                currentJob.setJob_duration_est(Integer.parseInt(tf_editjob_duration.getText()));
                currentJob.setJob_completed(check_editjob_complete.isSelected());
                currentJob.setJob_notes(ta_editjob_notes.getText());
                currentJob.setJob_created_date(Date.valueOf(dp_editjob_datestart.getValue()));
                jobRepo.updateJob(currentJob);
                dashboardController.showJobsPane();
            }
        } else{
            alert.showAndWait();
        }
    }
    public void saveJobTask(MouseEvent event){
        if(currentJob.getJob_completed()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot add tasks to a complete job");
            alert.showAndWait();
        }else{
            Task task = new Task();
            ObservableList<Task> taskList = currentJob.getJobTask();
            taskErrorCheck();
            if(!label_editjobtasks_null_error.isVisible()) {
                task.setTask_name(tf_editjob_taskname.getText());
                task.setTask_type(cb_editjob_tasktype.getSelectionModel().getSelectedItem().toString());
                task.setTask_duration(Integer.valueOf(cb_editjob_taskduration.getSelectionModel().getSelectedItem().toString()));
                task.setTask_created_by(dashboardController.userData().getUser_id());
                task.setTask_created_date(Date.valueOf(LocalDate.now()));
                task.setPriority(Integer.parseInt(cb_editjob_taskpriority.getSelectionModel().getSelectedItem().toString()));
                task.setPass_fail("");
                task.setTask_techs_required(Integer.parseInt(cb_editjob_tasktech.getSelectionModel().getSelectedItem().toString()));
                task.setCompleted(check_editjob_complete.isSelected());
                for(Task listTask: taskList){
                    if(listTask.getTask_name().equals(task.getTask_name())) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Duplicate Found: Task with the same name already exists, proceed?", ButtonType.YES, ButtonType.NO);
                        alert.showAndWait();
                        if (alert.getResult() == ButtonType.YES) {
                            break;
                        }else{
                            return;
                        }
                    }
                    if(listTask.getTask_type().equals(task.getTask_type())){
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Duplicate Found: Task with the same task type already exists, proceed?", ButtonType.YES, ButtonType.NO);
                        alert.showAndWait();
                        if (alert.getResult() == ButtonType.YES) {
                            break;
                        }else{
                            return;
                        }
                    }
                }
                int taskId = taskRepo.newTask(task);
                taskRepo.newJobTask(currentJob.getJob_id(), taskId);
                dashboardController.taskList.add(task);
                table_editjob_tasks.getItems().add(task);
                clearJobTaskFields();
            }
        }
    }

    public void setDashboardController(DashboardController dashboardController){
        this.dashboardController = dashboardController;
    }
    public void hidePage() {
        jedit_main.setVisible(false);
    }
    public void showPage() {
        jedit_main.setVisible(true);
        jedit_main.toFront();
    }
}
