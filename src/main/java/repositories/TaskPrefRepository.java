package repositories;

import javafx.collections.ObservableList;
import models.TaskPref;

import java.sql.ResultSet;

public interface TaskPrefRepository {
    ObservableList<TaskPref> getTaskPrefsById(int user_id);
    ObservableList<TaskPref> getTaskPrefsList(ResultSet rs);
    void deleteTaskPrefById(int task_pref_id);
    void saveTaskPref(TaskPref taskPref);
    int newTaskPref(TaskPref taskPref);
}
