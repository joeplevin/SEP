package controllers;

import com.sun.glass.ui.Clipboard;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import models.Job;
import models.User;
import models.UserCreds;
import repositories.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DashboardController {
    @FXML
    public AnchorPane anchor_dash_mydetails;
    @FXML
    public Circle circle_acc_image;
    @FXML
    public Label label_dash_user;
    @FXML
    public Label label_dash_role;
    @FXML
    public Button button_dash_users;
    @FXML
    public Button button_dash_jobs;
    @FXML
    public Button button_dash_tasks;
    @FXML
    public AnchorPane anchor_dash_title;
    @FXML
    public Label label_dash_title;
    @FXML
    public StackPane dash_stackpane;

    //Users
    @FXML
    private UserListController userListController;
    @FXML
    private EditUserController editUserController;
    @FXML
    private NewUserController newUserController;
    //Jobs
    @FXML
    private JobListController jobListController;
    @FXML
    private NewJobController newJobController;
    @FXML
    private EditJobController editJobController;
    //Tasks
    @FXML
    private TaskListController taskListController;
    @FXML
    private EditTaskController editTaskController;
    @FXML
    private NewTaskController newTaskController;


    //Repositories
    private UserCredsRepository credsRepository = new UserCredsRepositoryImpl();
    private UserRepository userRepository = new UserRepositoryImpl();
    private JobRepository jobRepository = new JobRepositoryImpl();
    private TaskRepository taskRepository = new TaskRepositoryImpl();

    //Task executor
    private Executor executor = Executors.newCachedThreadPool(runnable ->{
        Thread t = new Thread(runnable);
        t.setDaemon(true);
        return t;
    });

    //Current User
    User current = new User();

    //List Data
    ObservableList<Job> jobList = FXCollections.observableArrayList();
    ObservableList<User> userList = FXCollections.observableArrayList();
    ObservableList<models.Task> taskList = FXCollections.observableArrayList();

    public void initialize(){

        //User Panes
        userListController.setDashboardController(this);
        editUserController.setDashboardController(this);
        newUserController.setDashboardController(this);
        //Job Panes
        jobListController.setDashboardController(this);
        newJobController.setDashboardController(this);
        editJobController.setDashboardController(this);

        //Task Panes
        taskListController.setDashboardController(this);
        editTaskController.setDashboardController(this);
        newTaskController.setDashboardController(this);


        //Dashboard View setup depending on user
        label_dash_user.setText(current.getFName());
        String role = current.roleProperty().get();
        switch(role){
            case "Manager":
                userListController.tablec_Udelete.setVisible(true);
                userListController.tablec_Uedit.setVisible(true);
                userListController.table_userTable.setEditable(true);
                userListController.tablec_Uactive.setEditable(true);
                taskListController.button_new_task.setVisible(false);
                jobListController.button_new_job.setVisible(false);
                taskListController.check_mytasks.setDisable(true);
                jobListController.check_myjobs.setDisable(true);
                break;
            case "Technician":
                userListController.tablec_Uedit.setVisible(false);
                userListController.tablec_Udelete.setVisible(false);
                userListController.table_userTable.setEditable(false);
                userListController.tablec_Uactive.setEditable(false);
                userListController.button_new_user.setVisible(false);
                taskListController.button_new_task.setVisible(true);
                taskListController.check_mytasks.setDisable(false);
                jobListController.check_myjobs.setDisable(false);
                break;
            case "Customer Service":
                userListController.table_userTable.setEditable(false);
                userListController.tablec_Uactive.setEditable(false);
                userListController.tablec_Uedit.setVisible(false);
                userListController.tablec_Udelete.setVisible(false);
                userListController.button_new_user.setVisible(false);
                taskListController.tablec_taskEdit.setVisible(false);
                taskListController.tablec_taskDelete.setVisible(false);
                taskListController.button_new_task.setVisible(false);
                taskListController.check_mytasks.setDisable(true);
                jobListController.button_new_job.setVisible(true);
                jobListController.check_myjobs.setDisable(false);
        }
        label_dash_role.setText(role);
        editUserController.cb_role.valueProperty().addListener((obsval,oldval,newval)->{
            label_dash_role.setText((String)newval);
        });
        Image image;
        if(current.getImage() != null){
            image = new Image(current.getImage());
        } else{
            image = new Image("vulture_white.png");
        }
        circle_acc_image.setFill(new ImagePattern(image));

        //Set List Data
        setUpTask();
        setUpUser();
        setUpJob();
        //Show Jobs on startup
        showJobsPane();
    }

    public User userData(){
        return current;
    }
    public User getCurrent(){
        return current;
    }
    public void setCurrent(User user){
        current = user;
    }
    // Menu Links
    @FXML
    public void showUsersOnClick(MouseEvent e){
        showUsersPane();
    }
    @FXML
    public void showJobsOnClick(MouseEvent e){
        showJobsPane();
    }
    @FXML
    public void showTasksOnClick(MouseEvent e){
        showTasksPane();
    }
    //Account
    @FXML
    public void showMyAccount(MouseEvent event){
            User user = credsRepository.getCurrentUser();
            showEditUserPane(user);
            label_dash_title.setText(user.getFName() + " " + user.getSName());
    }

    //Users
    public void setUserList(ObservableList<User> uList){
        this.userList = uList;
    }
    public void setUpUser() {
        //Data intensive task to relieve application thread
        javafx.concurrent.Task<ObservableList<User>> userListTask = new javafx.concurrent.Task<ObservableList<User>>() {
            @Override
            public ObservableList<User> call() throws Exception {
                userListController.sp_userlistprogress.setVisible(true);
                userListController.sp_userlistprogress.toFront();
                ObservableList<User> users = userRepository.getUsers();
                return users;
            }
        };
        userListTask.setOnFailed(event -> {
            userListTask.getException().printStackTrace();
        });
        userListTask.setOnSucceeded(event -> {
            userListController.pi_userlistprogress.progressProperty().unbind();
            userListController.sp_userlistprogress.setVisible(false);
            setUserList(userListTask.getValue());
            userListController.setUsersTable(userListTask.getValue());
        });
        executor.execute(userListTask);
        userListController.pi_userlistprogress.progressProperty().bind(userListTask.progressProperty());
    }
    public void newUser(User user) {
        userList.add(user);
    }
    @FXML
    public void showUsersPane(){
        jobListController.hidePage();
        newJobController.hidePage();
        editJobController.hidePage();
        taskListController.hidePage();
        editTaskController.hidePage();
        newTaskController.hidePage();
        newUserController.hidePage();
        editUserController.hidePage();
        userListController.setUsersTable(userList);
        userListController.showPage();
        label_dash_title.setText("Users");
    }
    @FXML
    public void showNewUserPane(){
        jobListController.hidePage();
        newJobController.hidePage();
        newTaskController.hidePage();
        editJobController.hidePage();
        taskListController.hidePage();
        editTaskController.hidePage();
        newUserController.showPage();
        editUserController.hidePage();
        userListController.hidePage();
        label_dash_title.setText("New User");

    }
    @FXML
    public void showEditUserPane(User user){
        jobListController.hidePage();
        newJobController.hidePage();
        editJobController.hidePage();
        taskListController.hidePage();
        editTaskController.hidePage();
        newTaskController.hidePage();
        newUserController.hidePage();
        userListController.hidePage();
        editUserController.userData(user);
        editUserController.showPage();
        label_dash_title.setText("Edit User");
    }


    //Jobs
    public void setJobList(ObservableList<Job> jList){
        this.jobList = jList;
    }
    public void setUpJob(){
        //Data intensive task to relieve application thread
        javafx.concurrent.Task<ObservableList<Job>> jobListTask = new javafx.concurrent.Task<ObservableList<Job>>() {
            @Override
            protected ObservableList<Job> call() throws Exception {
                jobListController.sp_joblistprogress.setVisible(true);
                jobListController.sp_joblistprogress.toFront();
                ObservableList<Job> jList = jobRepository.getJobs();
                for(Job j : jList){
                    j.setJobTask(jobRepository.getTasksbyJobId(j.getJob_id()));
                    j.setJobStaff(jobRepository.getJobStaff(j.getJob_id()));
                }
                return jList;
            }
        };
        jobListTask.setOnFailed(event ->{
            jobListTask.getException().printStackTrace();
        });
        jobListTask.setOnSucceeded(event ->{
            jobListController.pi_joblistprogress.progressProperty().unbind();
            jobListController.sp_joblistprogress.setVisible(false);
            setJobList(jobListTask.getValue());
            jobListController.setJobsTable(jobListTask.getValue());
        });
        executor.execute(jobListTask);
        jobListController.pi_joblistprogress.progressProperty().bind(jobListTask.progressProperty());

    }
    public void newJob(Job job){
        jobList.add(job);
    }
    @FXML
    public void showJobsPane(){
        newUserController.hidePage();
        editUserController.hidePage();
        userListController.hidePage();
        taskListController.hidePage();
        editTaskController.hidePage();
        newJobController.hidePage();
        editJobController.hidePage();
        jobListController.setJobsTable(jobList);
        jobListController.showPage();
        label_dash_title.setText("Jobs");
    }
    @FXML
    public void showNewJobPane(){
        newUserController.hidePage();
        editUserController.hidePage();
        userListController.hidePage();
        taskListController.hidePage();
        editTaskController.hidePage();
        jobListController.hidePage();
        editJobController.hidePage();
        newJobController.showPage();
        label_dash_title.setText("New Job");
    }
    @FXML
    public void showEditJobPane(Job job) throws SQLException {
        newUserController.hidePage();
        editUserController.hidePage();
        userListController.hidePage();
        taskListController.hidePage();
        editTaskController.hidePage();
        jobListController.hidePage();
        newJobController.hidePage();
        editJobController.jobData(job);
        editJobController.showPage();
    }

    //Tasks
    public void setTaskList(ObservableList<models.Task> tList){
        this.taskList = tList;
    }
    public void setUpTask() {
        //Data intensive task to relieve application thread
        javafx.concurrent.Task<ObservableList<models.Task>> taskListTask = new javafx.concurrent.Task<ObservableList<models.Task>>() {
            @Override
            public ObservableList<models.Task> call() throws Exception {
                taskListController.sp_tasklistprogress.setVisible(true);
                taskListController.sp_tasklistprogress.toFront();
                ObservableList<models.Task> tasks = taskRepository.getTasks();
                return tasks;
            }
        };
        taskListTask.setOnFailed(event -> {
            taskListTask.getException().printStackTrace();
        });
        taskListTask.setOnSucceeded(event -> {
            taskListController.pi_tasklistprogress.progressProperty().unbind();
            taskListController.sp_tasklistprogress.setVisible(false);
            setTaskList(taskListTask.getValue());
            taskListController.setTasksTable(taskListTask.getValue(),userList);
        });
        executor.execute(taskListTask);
        taskListController.pi_tasklistprogress.progressProperty().bind(taskListTask.progressProperty());
    }
    public void newTask(models.Task task){
        taskList.add(task);
        for(Job job : jobList){
            if(job.getJob_id() == task.getTask_job()){
                ObservableList<models.Task> taskList = job.getJobTask();
                taskList.add(task);
                job.setJobTask(taskList);
                jobRepository.updateJob(job);
            }
        }
    }
    @FXML
    public void showTasksPane(){
        newUserController.hidePage();
        editUserController.hidePage();
        userListController.hidePage();
        newJobController.hidePage();
        jobListController.hidePage();
        editJobController.hidePage();
        newTaskController.hidePage();
        editTaskController.hidePage();
        taskListController.showPage();
        label_dash_title.setText("Tasks");
    }
    @FXML
    public void showNewTaskPane(){
        newUserController.hidePage();
        editUserController.hidePage();
        userListController.hidePage();
        newJobController.hidePage();
        jobListController.hidePage();
        editJobController.hidePage();
        editTaskController.hidePage();
        taskListController.hidePage();
        newTaskController.setNewTask(jobList,taskList);
        newTaskController.showPage();
    }
    @FXML
    public void showEditTaskPane(models.Task task){
        newUserController.hidePage();
        editUserController.hidePage();
        userListController.hidePage();
        newJobController.hidePage();
        jobListController.hidePage();
        editJobController.hidePage();
        taskListController.hidePage();
        newTaskController.hidePage();
        editTaskController.editTaskViewSetup(task, userList);
        editTaskController.showPage();
        label_dash_title.setText("Edit " + task.getTask_name());
    }

    //Logout
    public void logout(MouseEvent event) {
        UserCredsRepository credsRepository = new UserCredsRepositoryImpl();
        //Clear current user tag
        credsRepository.clearCurrentUser();
        //Load Login
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        String style = getClass().getResource("/styles.css").toExternalForm();
        scene.getStylesheets().add(style);
        stage.setScene(scene);
    }

}
