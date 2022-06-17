package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import models.Job;
import models.Task;
import models.User;
import repositories.TaskRepository;
import repositories.TaskRepositoryImpl;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;


public class NewTaskController {
    @FXML
    Node tnew_main;
    @FXML
    public TextField tf_newtask_name;
    @FXML
    public ChoiceBox cb_newtask_priority;
    @FXML
    public ChoiceBox cb_newtask_duration;
    @FXML
    public ChoiceBox cb_newtask_numbertechs;
    @FXML
    public ChoiceBox cb_newtask_tasktype;
    @FXML
    public ChoiceBox cb_newtask_taskjob;
    @FXML
    public TextArea ta_newtask_notes;
    @FXML
    public DatePicker dp_newtask_datestart;
    @FXML
    public Button button_newtask_back;
    @FXML
    public Button button_newtask_save;
    @FXML
    public Label label_taskname_null;
    @FXML
    public Label label_tasktype_null;
    @FXML
    public Label label_taskduration_null;
    @FXML
    public Label label_taskjob_null;
    @FXML
    public Label label_taskpriority_null;
    @FXML
    public Label label_tasknumbertechs_null;
    @FXML
    public Label label_newtask_null_error;

    private DashboardController dashboardController;
    private ObservableList<Job> jobList = FXCollections.observableArrayList();
    private ObservableList<Task> taskList = FXCollections.observableArrayList();
    private TaskRepository taskRepo = new TaskRepositoryImpl();

    public void setNewTask(ObservableList<Job> jList,ObservableList<Task> tList){
        ObservableList<String> taskJob = FXCollections.observableArrayList("");
        jobList = jList;
        taskList = tList;
        for(Job j : jobList){
            taskJob.add(j.getJob_id() + ": "+j.getJob_name());
        }
        cb_newtask_taskjob.setItems(taskJob);
        ObservableList<String> taskTypes = FXCollections.observableArrayList("");
        for(Task t : taskList){
            if(!taskTypes.stream().anyMatch(taskType -> taskType.equals(t.getTask_type()))) {
                taskTypes.add(t.getTask_type());
            }
        }
        cb_newtask_tasktype.setItems(taskTypes);
        ObservableList<String> techsreq = FXCollections.observableArrayList("","1","2");
        cb_newtask_numbertechs.setItems(techsreq);
        ObservableList<String> duration = FXCollections.observableArrayList("","1","2","3","4","5","6","7","8");
        cb_newtask_duration.setItems(duration);
        ObservableList<String> priority = FXCollections.observableArrayList("","1","2","3","4","5");
        cb_newtask_priority.setItems(priority);
        dp_newtask_datestart.setValue(LocalDate.now());

    }

    public void saveTask() throws SQLException {
        errorCheck();
        if(!label_newtask_null_error.isVisible()){
            User user = dashboardController.getCurrent();
            Task task = new Task();
            task.setTask_name(tf_newtask_name.getText());
            String job = cb_newtask_taskjob.getSelectionModel().getSelectedItem().toString();
            int jobId = Integer.valueOf(job.substring(0,job.indexOf(":")));
            task.setTask_job(jobId);
            task.setTask_techs_required(Integer.valueOf(cb_newtask_numbertechs.getSelectionModel().getSelectedItem().toString()));
            task.setTask_created_date(Date.valueOf(dp_newtask_datestart.getValue()));
            task.setTask_created_by(user.getUser_id());
            task.setTask_type(cb_newtask_tasktype.getSelectionModel().getSelectedItem().toString());
            task.setPriority(Integer.valueOf(cb_newtask_priority.getSelectionModel().getSelectedItem().toString()));
            task.setTask_duration(Integer.valueOf(cb_newtask_duration.getSelectionModel().getSelectedItem().toString()));
            int res = taskRepo.newTask(task);
            taskRepo.newJobTask(jobId,res);
            dashboardController.newTask(task);
            dashboardController.showTasksPane();
        }
    }

    public void errorCheck(){
        if(tf_newtask_name.getText().equals("")){
            label_taskname_null.setVisible(true);
        }else{
            label_taskname_null.setVisible(false);
        }
        if(cb_newtask_priority.getSelectionModel().getSelectedItem() == null || cb_newtask_priority.getSelectionModel().getSelectedItem().toString().equals("")){
            label_taskpriority_null.setVisible(true);
        }else{
            label_taskpriority_null.setVisible(false);
        }
        if(cb_newtask_duration.getSelectionModel().getSelectedItem() == null || cb_newtask_duration.getSelectionModel().getSelectedItem().toString().equals("")){
            label_taskduration_null.setVisible(true);
        }else{
            label_taskduration_null.setVisible(false);
        }
        if(cb_newtask_tasktype.getSelectionModel().getSelectedItem() == null || cb_newtask_tasktype.getSelectionModel().getSelectedItem().toString().equals("")){
            label_tasktype_null.setVisible(true);
        }else{
            label_tasktype_null.setVisible(false);
        }
        if(cb_newtask_taskjob.getSelectionModel().getSelectedItem() == null || cb_newtask_taskjob.getSelectionModel().getSelectedItem().toString().equals("")){
            label_taskjob_null.setVisible(true);
        }else{
            label_taskjob_null.setVisible(false);
        }
        if(cb_newtask_numbertechs.getSelectionModel().getSelectedItem() == null || cb_newtask_numbertechs.getSelectionModel().getSelectedItem().toString().equals("")){
            label_tasknumbertechs_null.setVisible(true);
        }else{
            label_tasknumbertechs_null.setVisible(false);
        }
        if(label_taskname_null.isVisible()||label_taskpriority_null.isVisible()||label_taskduration_null.isVisible()||label_tasktype_null.isVisible()||label_taskjob_null.isVisible()||label_tasknumbertechs_null.isVisible()){
            label_newtask_null_error.setVisible(true);
        }else{
            label_newtask_null_error.setVisible(false);
        }
    }
    public void clearFields(){
        tf_newtask_name.clear();
        cb_newtask_duration.getSelectionModel().clearSelection();
        cb_newtask_numbertechs.getSelectionModel().clearSelection();
        cb_newtask_taskjob.getSelectionModel().clearSelection();
        cb_newtask_tasktype.getSelectionModel().clearSelection();
        cb_newtask_priority.getSelectionModel().clearSelection();
        dp_newtask_datestart.setValue(null);
        label_taskname_null.setVisible(false);
        label_taskduration_null.setVisible(false);
        label_taskpriority_null.setVisible(false);
        label_tasktype_null.setVisible(false);
        label_taskjob_null.setVisible(false);
        label_tasknumbertechs_null.setVisible(false);
        label_newtask_null_error.setVisible(false);
    }

    //Dashboard Navigation
    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }
    public void goBack(){
        clearFields();
        dashboardController.showTasksPane();
    }
    public void hidePage(){
        tnew_main.setVisible(false);
    }
    public void showPage(){
        tnew_main.setVisible(true);
        tnew_main.toFront();
    }


}
