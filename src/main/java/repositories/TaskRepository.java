package repositories;

import javafx.collections.ObservableList;
import models.Task;

import java.sql.Date;
import java.sql.ResultSet;

public interface TaskRepository {
    ObservableList<String> getTaskTypeList();
    void deleteById(int task_id);
    int newTask(Task task);
    Task getTasksByTaskId(int id);
    void updateTask(Task task);
    void newJobTask(int job_id, int taskId);
    ObservableList<Task> getTasks();
    ObservableList<Task> getTaskList(ResultSet rs);
    void newUserTask(int user_id, int task_id, Date created_date);
    void deleteUserTask(int task_id,int user_id);
}
