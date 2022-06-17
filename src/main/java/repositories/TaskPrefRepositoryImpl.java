package repositories;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.TaskPref;
import util.MySqlHandler;

import java.sql.*;

public class TaskPrefRepositoryImpl implements TaskPrefRepository{
    private MySqlHandler handler = new MySqlHandler();
    private Connection connection = handler.getConnection();
    private PreparedStatement preparedStatement;

    public ObservableList<TaskPref> getTaskPrefsById(int user_id) {
        connection = handler.getConnection();
        String q = "SELECT utp.* FROM user_taskpref utp where utp.user_id = ?";
        ResultSet rs = null;
        ObservableList<TaskPref> list = null;
        try {

            preparedStatement = connection.prepareStatement(q);
            preparedStatement.setInt(1, user_id);
            rs = preparedStatement.executeQuery();
            list = getTaskPrefsList(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ObservableList<TaskPref> getTaskPrefsList(ResultSet rs) {
        ObservableList<TaskPref> taskPrefsList = FXCollections.observableArrayList();
        try {
            while (rs.next()) {
                TaskPref taskPref = new TaskPref();
                taskPref.setProficiency(rs.getInt("taskpref_proficiency"));
                taskPref.setTask_pref_id(rs.getInt("taskpref_id"));
                taskPref.setUser_id(rs.getInt("user_id"));
                taskPref.setTasktype(rs.getString("taskpref_tasktype"));
                taskPrefsList.add(taskPref);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return taskPrefsList;
    }

    public void deleteTaskPrefById(int task_pref_id) {
            String sql = "DELETE FROM user_taskpref WHERE taskpref_id = ?";
            try {
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, task_pref_id);
                preparedStatement.executeUpdate();
            } catch(SQLException e){
                e.printStackTrace();
            }
    }


    public void saveTaskPref(TaskPref taskPref) {
    }

    public int newTaskPref(TaskPref taskPref) {
        String q =  "INSERT INTO user_taskpref(user_id,taskpref_tasktype,taskpref_proficiency) VALUES (?,?,?)";
        try {
            preparedStatement = connection.prepareStatement(q, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1,taskPref.getUser_id());
            preparedStatement.setString(2,taskPref.getTasktype());
            preparedStatement.setInt(3,taskPref.getProficiency());
            preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if(rs.next()){
                int id = rs.getInt(1);
                return id;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
