package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import models.Job;
import models.User;
import repositories.JobRepository;
import repositories.JobRepositoryImpl;

import java.sql.Date;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;

public class NewJobController {

    @FXML
    public AnchorPane jnew_main;
    @FXML
    public TextField tf_newjob_name;
    @FXML
    public TextArea ta_newjob_notes;
    @FXML
    public TextField tf_newjob_duration;
    @FXML
    public TextField tf_newjob_cost;
    @FXML
    public DatePicker dp_newjob_datestart;
    @FXML
    public DatePicker dp_newjob_dateend;
    @FXML
    public Button button_save_newjob;

    //Error Handling

    @FXML
    public Label label_newjobduration_null;
    @FXML
    public Label label_newjobname_null;
    @FXML
    public Label label_newjobcost_null;
    @FXML
    public Label label_newjob_nullerror;
    @FXML
    public Label label_duration_type_error;
    @FXML
    public Label label_duration_length_error;
    @FXML
    public Label label_cost_format_error;
    @FXML
    public Label label_cost_value_error;

    private DashboardController dashboardController;

    private JobRepository jobRepository = new JobRepositoryImpl();

    public void initialize(){
        dp_newjob_datestart.setValue(LocalDate.now());
        ta_newjob_notes.setCache(false);
        tf_newjob_duration.textProperty().addListener((obsval,oldval,newval)->{
            if(newval.matches("[0-9]+")) {
                dp_newjob_dateend.setValue(LocalDate.now().plusDays(Long.parseLong(newval)));
                label_duration_type_error.setVisible(false);
                if (Integer.parseInt(newval) > 10) {
                    label_duration_type_error.setManaged(false);
                    label_duration_length_error.setVisible(true);
                } else{
                    label_duration_length_error.setVisible(false);
                }
            } else{
                if(!tf_newjob_duration.getText().equals("")) {
                    label_duration_type_error.setManaged(true);
                    label_duration_type_error.setVisible(true);
                } else{
                    label_duration_type_error.setVisible(false);
                    label_duration_length_error.setVisible(false);
                }
            }
        });
        tf_newjob_cost.textProperty().addListener((obsval,oldval,newval)->{
            if(!newval.matches("^[0-9]+\\.[0-9][0-9]$")){
                label_cost_format_error.setManaged(true);
                label_cost_format_error.setVisible(true);
            } else if(Float.parseFloat(newval) > 1000){
                label_cost_format_error.setVisible(false);
                label_cost_format_error.setManaged(false);
                label_cost_value_error.setVisible(true);
            } else{
                label_cost_value_error.setVisible(false);
                label_cost_format_error.setVisible(false);
            }
        });
    }

    public void saveJob(MouseEvent event) {
        if (tf_newjob_name.getText().isEmpty()) {
            label_newjobname_null.setVisible(true);
        } else {
            label_newjobname_null.setVisible(false);
        }
        if (tf_newjob_cost.getText().isEmpty()) {
            label_newjobcost_null.setVisible(true);
        } else {
            label_newjobcost_null.setVisible(false);
        }
        if (tf_newjob_duration.getText().isEmpty()) {
            label_newjobduration_null.setVisible(true);
        } else {
            label_newjobduration_null.setVisible(false);
        }
        if (label_newjobname_null.isVisible()
                || label_newjobcost_null.isVisible()
                || label_newjobduration_null.isVisible()) {
            label_newjob_nullerror.setVisible(true);
            return;
        } else {
            label_newjob_nullerror.setVisible(false);
        }
        if(label_duration_length_error.isVisible()
                || label_duration_type_error.isVisible()
                || label_cost_format_error.isVisible()
                || label_cost_value_error.isVisible()){
            return;
        }
        Job job = new Job();
        User cur = dashboardController.userData();
        job.setJob_name(tf_newjob_name.getText());
        job.setJob_creator(cur.getUser_id());
        job.setJob_duration_est(Integer.parseInt(tf_newjob_duration.getText()));
        job.setJob_cost_est(Float.parseFloat(tf_newjob_cost.getText()));
        job.setJob_created_date(Date.valueOf(LocalDate.now()));
        job.setJob_notes(ta_newjob_notes.getText());
        jobRepository.newJob(job);
        clearFields();
        dashboardController.newJob(job);
        dashboardController.showJobsPane();
    }

    public void clearFields(){
        tf_newjob_name.clear();
        tf_newjob_cost.clear();
        tf_newjob_duration.clear();
        ta_newjob_notes.clear();
        dp_newjob_dateend.setValue(null);
        label_newjob_nullerror.setVisible(false);
        label_newjobduration_null.setVisible(false);
        label_newjobcost_null.setVisible(false);
        label_newjobname_null.setVisible(false);
        label_duration_type_error.setVisible(false);
        label_duration_length_error.setVisible(false);
        label_cost_value_error.setVisible(false);
        label_cost_format_error.setVisible(false);
    }


    //Dashboard Navigation
    public void setDashboardController(DashboardController dashboardController){
        this.dashboardController = dashboardController;
    }
    public void goBack(MouseEvent e){
        clearFields();
        dashboardController.showJobsPane();
    }
    public void hidePage() {
        jnew_main.setVisible(false);
    }
    public void showPage() {
        jnew_main.setVisible(true);
        jnew_main.toFront();
    }
}
