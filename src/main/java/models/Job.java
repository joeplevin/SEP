package models;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import repositories.JobRepositoryImpl;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class Job {

    private IntegerProperty job_id;
    private StringProperty job_name;
    private IntegerProperty job_creator;
    private ObjectProperty<Date> job_created_date;
    private FloatProperty job_cost_est;
    private IntegerProperty job_duration_est;
    private StringProperty job_notes;
    private BooleanProperty job_completed;
    private ListProperty<Task> jobTask;
    private ListProperty<User> jobStaff;
    private ObjectProperty<Date> job_completed_date;

    public Job(){
        this.job_id = new SimpleIntegerProperty();
        this.job_name = new SimpleStringProperty();
        this.job_creator = new SimpleIntegerProperty();
        this.job_created_date = new SimpleObjectProperty<Date>();
        this.job_cost_est = new SimpleFloatProperty();
        this.job_duration_est = new SimpleIntegerProperty();
        this.job_notes = new SimpleStringProperty();
        this.job_completed = new SimpleBooleanProperty();
        this.jobTask = new SimpleListProperty<Task>();
        this.jobStaff = new SimpleListProperty<User>();
        this.job_completed_date = new SimpleObjectProperty<Date>();
    }

    public int getJob_id() {
        return job_id.get();
    }
    public IntegerProperty job_idProperty() {
        return job_id;
    }
    public void setJob_id(int job_id) {
        this.job_id.set(job_id);
    }

    public String getJob_name() {
        return job_name.get();
    }
    public StringProperty job_nameProperty() {
        return job_name;
    }
    public void setJob_name(String job_name) {
        this.job_name.set(job_name);
    }

    public int getJob_creator() {
        return job_creator.get();
    }
    public IntegerProperty job_creatorProperty() {
        return job_creator;
    }
    public void setJob_creator(int job_creator) {
        this.job_creator.set(job_creator);
    }

    public Date getJob_created_date() {
        return job_created_date.get();
    }
    public ObjectProperty<Date> job_created_dateProperty() {
        return job_created_date;
    }
    public void setJob_created_date(Date job_created_date) {
        this.job_created_date.set(job_created_date);
    }

    public float getJob_cost_est() {
        return job_cost_est.get();
    }
    public FloatProperty job_cost_estProperty() {
        return job_cost_est;
    }
    public void setJob_cost_est(float job_cost_est) {
        this.job_cost_est.set(job_cost_est);
    }

    public int getJob_duration_est() {
        return job_duration_est.get();
    }
    public IntegerProperty job_duration_estProperty() {
        return job_duration_est;
    }
    public void setJob_duration_est(int job_duration_est) {
        this.job_duration_est.set(job_duration_est);
    }

    public String getJob_notes() {
        return job_notes.get();
    }
    public StringProperty job_notesProperty() {
        return job_notes;
    }
    public void setJob_notes(String job_notes) {
        this.job_notes.set(job_notes);
    }

    public Boolean getJob_completed() {
        return job_completed.get();
    }
    public BooleanProperty job_completedProperty() {
        return job_completed;
    }
    public void setJob_completed(Boolean job_completed) {
        this.job_completed.set(job_completed);
    }

    public ObservableList<Task> getJobTask() {
        return jobTask.get();
    }
    public ListProperty<Task> jobTaskProperty() {
        return jobTask;
    }
    public void setJobTask(ObservableList<Task> jobTask) {
        this.jobTask.set(jobTask);
    }

    public ObservableList<User> getJobStaff() {
    return jobStaff.get();
    }
    public ListProperty<User> jobStaffProperty() {
    return jobStaff;
    }
    public void setJobStaff(ObservableList<User> jobStaff) {
        this.jobStaff.set(jobStaff);
    }

    public Date getCompleted_date() {
        return job_completed_date.get();
    }
    public ObjectProperty<Date> completed_dateProperty() {
        return job_completed_date;
    }
    public void setCompleted_date(Date completed_date) {
        this.job_completed_date.set(completed_date);
    }

    @Override
    public String toString(){
        return "id: " + this.job_id;
    }


}

