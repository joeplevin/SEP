package models;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import repositories.TaskRepositoryImpl;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Map;

public class Task {

    private IntegerProperty task_id;
    private StringProperty task_name;
    private IntegerProperty task_created_by;
    private StringProperty task_creator_name;
    private ObjectProperty<Date> task_created_date;
    private IntegerProperty task_duration;
    private StringProperty task_type;
    private IntegerProperty priority;
    private BooleanProperty completed;
    private ObjectProperty<Date> completed_date;
    private ObjectProperty<Date> assigned_date;
    private StringProperty pass_fail;
    private IntegerProperty task_job;
    private ListProperty<User> task_tech;
    private StringProperty task_notes;
    private IntegerProperty task_techs_required;


    public Task() {
        this.task_id = new SimpleIntegerProperty();
        this.task_name = new SimpleStringProperty();
        this.task_created_by = new SimpleIntegerProperty();
        this.task_creator_name = new SimpleStringProperty();
        this.task_created_date = new SimpleObjectProperty<Date>();
        this.task_duration = new SimpleIntegerProperty();
        this.task_type = new SimpleStringProperty();
        this.priority = new SimpleIntegerProperty();
        this.completed = new SimpleBooleanProperty();
        this.completed_date = new SimpleObjectProperty<Date>();
        this.assigned_date = new SimpleObjectProperty<Date>();
        this.pass_fail = new SimpleStringProperty();
        this.task_job = new SimpleIntegerProperty();
        this.task_tech = new SimpleListProperty<User>();
        this.task_techs_required = new SimpleIntegerProperty();
        this.task_notes = new SimpleStringProperty();
    }

    public int getTask_id() {
        return task_id.get();
    }
    public IntegerProperty task_idProperty() {
        return task_id;
    }
    public void setTask_id(int task_id) {
        this.task_id.set(task_id);
    }

    public String getTask_name() {
        return task_name.get();
    }
    public StringProperty task_nameProperty() {
        return task_name;
    }
    public void setTask_name(String task_name) {
        this.task_name.set(task_name);
    }

    public int getTask_created_by() {
        return task_created_by.get();
    }
    public IntegerProperty task_created_byProperty() {
        return task_created_by;
    }
    public void setTask_created_by(int task_created_by) {
        this.task_created_by.set(task_created_by);
    }

    public Date getTask_created_date() {
        return task_created_date.get();
    }
    public ObjectProperty<Date> task_created_dateProperty() {
        return task_created_date;
    }
    public void setTask_created_date(Date task_created_date) {
        this.task_created_date.set(task_created_date);
    }

    public int getTask_duration() {
        return task_duration.get();
    }
    public IntegerProperty task_durationProperty() {
        return task_duration;
    }
    public void setTask_duration(int task_duration) {
        this.task_duration.set(task_duration);
    }

    public String getTask_type() {
        return task_type.get();
    }
    public StringProperty task_typeProperty() {
        return task_type;
    }
    public void setTask_type(String task_type_name) {
        this.task_type.set(task_type_name);
    }

    public int getPriority() {
        return priority.get();
    }
    public IntegerProperty priorityProperty() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority.set(priority);
    }

    public Boolean isCompleted() {
        return completed.get();
    }
    public BooleanProperty completedProperty() {
        return completed;
    }
    public void setCompleted(Boolean completed) {
        this.completed.set(completed);
    }

    public String getPass_fail() {
        return pass_fail.get();
    }
    public StringProperty pass_failProperty() {
        return pass_fail;
    }
    public void setPass_fail(String pass_fail) {
        this.pass_fail.set(pass_fail);
    }

    public Date getCompleted_date() {
        return completed_date.get();
    }
    public ObjectProperty<Date> completed_dateProperty() {
        return completed_date;
    }
    public void setCompleted_date(Date completed_date) {
        this.completed_date.set(completed_date);
    }

    public String getTask_creator_name() {
        return task_creator_name.get();
    }
    public StringProperty task_creator_nameProperty() {
        return task_creator_name;
    }
    public void setTask_creator_name(String task_creator_name) {
        this.task_creator_name.set(task_creator_name);
    }

    public Date getAssigned_date() {
        return assigned_date.get();
    }
    public ObjectProperty<Date> assigned_dateProperty() {
        return assigned_date;
    }
    public void setAssigned_date(Date assigned_date) {
        this.assigned_date.set(assigned_date);
    }

    public int getTask_job() {
        return task_job.get();
    }
    public IntegerProperty task_jobProperty() {
        return task_job;
    }
    public void setTask_job(int task_job) {
        this.task_job.set(task_job);
    }

    public ObservableList<User> getTask_tech() {
        return task_tech.get();
    }
    public ListProperty<User> task_techProperty() {
        return task_tech;
    }
    public void setTask_tech(ObservableList<User> task_tech) {
        this.task_tech.set(task_tech);
    }

    public int getTask_techs_required() {
        return task_techs_required.get();
    }
    public IntegerProperty task_techs_requiredProperty() {
        return task_techs_required;
    }
    public void setTask_techs_required(int task_techs_required) {
        this.task_techs_required.set(task_techs_required);
    }

    public String getTask_notes() {
        return task_notes.get();
    }
    public StringProperty task_notesProperty() {
        return task_notes;
    }
    public void setTask_notes(String task_notes) {
        this.task_notes.set(task_notes);
    }


    @Override
    public String toString(){
        return "id: " + this.task_id + "name: " + this.task_name;
    }




}
