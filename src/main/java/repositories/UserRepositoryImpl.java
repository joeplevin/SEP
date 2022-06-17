package repositories;

import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Task;
import models.User;
import models.UserCreds;
import util.MySqlHandler;

import java.sql.*;

public class UserRepositoryImpl implements UserRepository{

    private MySqlHandler handler = new MySqlHandler();
    private Connection connection = handler.getConnection();
    private PreparedStatement preparedStatement;

    public User getUserById(int id){
        String q = "Select * FROM user WHERE user_id = ?";
        ResultSet rs = null;
        User user = new User();
        try{
            connection = handler.getConnection();
            preparedStatement = connection.prepareStatement(q);
            preparedStatement.setInt(1,id);
            rs = preparedStatement.executeQuery();
            if(rs.next()){
                user.setUser_id(rs.getInt("user_id"));
                user.setFName(rs.getString("user_first_name"));
                user.setSName(rs.getString("user_last_name"));
                user.setEmail(rs.getString("user_email"));
                user.setUser_active(rs.getBoolean("user_active"));
                user.setRole(rs.getString("user_role"));
                if(rs.getString("user_image") != null) {
                    user.setImage(rs.getString("user_image"));
                }
            }
        } catch(SQLException e){
            e.printStackTrace();
        }finally{
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(preparedStatement != null){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    public ObservableList<User> getUsers() {
        connection = handler.getConnection();
        String q =  "SELECT u.*,t.*,ut.usertask_created_date AS assigned_date FROM user_task ut RIGHT JOIN user u ON u.user_id = ut.user_id LEFT JOIN task t ON t.task_id = ut.task_id";
        ResultSet rs = null;
        ObservableList<User> userList = null;
        try {
            preparedStatement = connection.prepareStatement(q);
            rs = preparedStatement.executeQuery();
            userList = getUserList(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(preparedStatement != null){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return userList;
    }

    public ObservableList<User> getUserList(ResultSet rs) {
        ObservableList<User> userList = FXCollections.observableArrayList();
        User existingUser = null;
        try {
            while (rs.next()) {
                User user = new User();
                Task task = new Task();
                ObservableList<Task> taskList = FXCollections.observableArrayList();
                user.setUser_id(rs.getInt("user_id"));
                user.setFName(rs.getString("user_first_name"));
                user.setSName(rs.getString("user_last_name"));
                user.setEmail(rs.getString("user_email"));
                user.setRole(rs.getString("user_role"));
                user.setUser_active(rs.getBoolean("user_active"));
                user.setImage(rs.getString("user_image"));
                user.setUser_available_time(rs.getInt("user_available_time"));
                if (!userList.stream().anyMatch(listuser -> listuser.getUser_id() == user.getUser_id())) {
                    userList.add(user);
                    existingUser = user;
                    if (rs.getInt("task_id") > 0) {
                        task.setTask_id(rs.getInt("task_id"));
                        task.setTask_name(rs.getString("task_name"));
                        task.setTask_created_by(rs.getInt("task_created_by"));
                        task.setTask_created_date(rs.getDate("task_created_date"));
                        task.setTask_duration(rs.getInt("task_duration"));
                        task.setTask_type(rs.getString("task_tasktype"));
                        task.setPriority(rs.getInt("task_priority"));
                        task.setCompleted(rs.getBoolean("task_completed"));
                        task.setPass_fail(rs.getString("task_pass_fail"));
                        task.setCompleted_date(rs.getDate("task_completed_date"));
                        task.setAssigned_date(rs.getDate("assigned_date"));
                        task.setTask_techs_required(rs.getInt("task_techs_required"));
                        task.setTask_notes(rs.getString("task_notes"));
                        if (user.getUser_tasks() == null) {
                            taskList.add(task);
                            user.setUser_tasks(taskList);
                        } else if (!user.getUser_tasks().stream().anyMatch(listtask -> listtask.getTask_id() == task.getTask_id())) {
                            for (Task t : user.getUser_tasks()) {
                                taskList.add(t);
                            }
                            taskList.add(task);
                            user.setUser_tasks(taskList);
                        }
                    }
                } else if (userList.stream().anyMatch(listuser -> listuser.getUser_id() == user.getUser_id())) {
                    if (rs.getInt("task_id") > 0) {
                        task.setTask_id(rs.getInt("task_id"));
                        task.setTask_name(rs.getString("task_name"));
                        task.setTask_created_by(rs.getInt("task_created_by"));
                        task.setTask_created_date(rs.getDate("task_created_date"));
                        task.setTask_duration(rs.getInt("task_duration"));
                        task.setTask_type(rs.getString("task_tasktype"));
                        task.setPriority(rs.getInt("task_priority"));
                        task.setCompleted(rs.getBoolean("task_completed"));
                        task.setPass_fail(rs.getString("task_pass_fail"));
                        task.setCompleted_date(rs.getDate("task_completed_date"));
                        task.setAssigned_date(rs.getDate("assigned_date"));
                        task.setTask_techs_required(rs.getInt("task_techs_required"));
                        task.setTask_notes(rs.getString("task_notes"));
                        if (existingUser.getUser_tasks() == null) {
                            taskList.add(task);
                            existingUser.setUser_tasks(taskList);
                        } else if (!existingUser.getUser_tasks().stream().anyMatch(listtask -> listtask.getTask_id() == task.getTask_id())) {
                            for (Task t : existingUser.getUser_tasks()) {
                                taskList.add(t);
                            }
                            taskList.add(task);
                            existingUser.setUser_tasks(taskList);
                        }
                    }
                }

            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return userList;
    }

    public ObservableList<String> getRoleList(){
        String q = "SELECT role FROM role";
        ObservableList<String> roleList = FXCollections.observableArrayList();
        ResultSet rs = null;
        try{
            rs = MySqlHandler.dbQuery(q);
            while(rs.next()){
                roleList.add(rs.getString("role"));
            }
            return roleList;
        } catch(SQLException e){
        } finally{
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(preparedStatement != null){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return roleList;
    }

    public void updateUser(User user){
        String q =  "UPDATE user SET user_first_name = ?,user_last_name = ?, user_email = ?, user_role = ?,user_active = ?, user_available_time = ? WHERE user_id = ?";
        try {
            connection = handler.getConnection();
            preparedStatement = connection.prepareStatement(q);
            preparedStatement.setString(1,user.getFName());
            preparedStatement.setString(2,user.getSName());
            preparedStatement.setString(3,user.getEmail());
            preparedStatement.setString(4,user.getRole());
            preparedStatement.setBoolean(5,user.isUser_active());
            preparedStatement.setInt(6,user.getUser_available_time());
            preparedStatement.setInt(7,user.getUser_id());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            if(preparedStatement != null){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int newUser(User user){
        String q =  "INSERT INTO user(user_first_name,user_last_name,user_email,user_image,user_active,user_role) VALUES (?,?,?,?,?,?)";
        ResultSet rs = null;
        try {
            connection = handler.getConnection();
            preparedStatement = connection.prepareStatement(q, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,user.getFName());
            preparedStatement.setString(2,user.getSName());
            preparedStatement.setString(3,user.getEmail());
            preparedStatement.setString(4,"copeland.jpg");
            preparedStatement.setBoolean(5,true);
            preparedStatement.setString(6,user.getRole());
            preparedStatement.executeUpdate();
            rs = preparedStatement.getGeneratedKeys();
            if(rs.next()){
               int id = rs.getInt(1);
               return id;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(preparedStatement != null){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public void deleteById(int user_id) {
        String sql = "DELETE FROM user WHERE user_id = ?";
        try {
            connection = handler.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, user_id);
            preparedStatement.executeUpdate();
            } catch(SQLException e){
            e.printStackTrace();
            } finally{
            if(preparedStatement != null){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public ObservableList<String> getTaskTypeList(){
        String q = "SELECT tasktype FROM tasktype";
        ObservableList<String> taskTypeList = FXCollections.observableArrayList();
        try{
            ResultSet rs = MySqlHandler.dbQuery(q);
            while(rs.next()){
                taskTypeList.add(rs.getString("tasktype"));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return taskTypeList;
    }

}

