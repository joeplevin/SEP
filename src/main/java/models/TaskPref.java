package models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import repositories.TaskPrefRepositoryImpl;

public class TaskPref {

    private SimpleIntegerProperty task_pref_id;
    private SimpleIntegerProperty user_id;
    private SimpleIntegerProperty proficiency;
    private SimpleStringProperty tasktype;

    public TaskPref(){
        this.task_pref_id = new SimpleIntegerProperty();
        this.user_id = new SimpleIntegerProperty();
        this.tasktype = new SimpleStringProperty();
        this.proficiency = new SimpleIntegerProperty();
    }

    public int getUser_id() {
        return user_id.get();
    }
    public SimpleIntegerProperty user_idProperty() {
        return user_id;
    }
    public void setUser_id(int user_id) {
        this.user_id.set(user_id);
    }

    public int getProficiency() {
        return proficiency.get();
    }
    public SimpleIntegerProperty proficiencyProperty() {
        return proficiency;
    }
    public void setProficiency(int proficiency) {
        this.proficiency.set(proficiency);
    }

    public String getTasktype() {
        return tasktype.get();
    }
    public SimpleStringProperty tasktypeProperty() {
        return tasktype;
    }
    public void setTasktype(String tasktype_name) {
        this.tasktype.set(tasktype_name);
    }


    public int getTask_pref_id() {
        return task_pref_id.get();
    }
    public SimpleIntegerProperty task_pref_idProperty() {
        return task_pref_id;
    }
    public void setTask_pref_id(int task_pref_id) {
        this.task_pref_id.set(task_pref_id);
    }

    @Override
    public String toString(){
        return "Id: " + task_pref_id + " Tasktype: " + this.tasktype + " User_id: " + this.user_id + " Proficiency: " + this.proficiency;
    }
}
