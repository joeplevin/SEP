package repositories;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Job;
import models.Task;
import models.User;
import util.MySqlHandler;

import java.sql.*;

public class TaskRepositoryImpl implements TaskRepository{

    private MySqlHandler handler = new MySqlHandler();
    private Connection connection = handler.getConnection();
    private PreparedStatement preparedStatement;

    public ObservableList<String> getTaskTypeList() {
        Connection connection = handler.getConnection();
        ObservableList<String> taskTypeList = FXCollections.observableArrayList();
        String q = "SELECT * FROM tasktype";
        ResultSet rs = null;
        try {
            rs = MySqlHandler.dbQuery(q);
            String taskType = "";
            while (rs.next()) {
                taskType = (rs.getString("tasktype"));
                taskTypeList.add(taskType);
            }
            return taskTypeList;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
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
        return taskTypeList;
    }

    public void deleteById(int task_id) {
        String sql = "DELETE FROM task WHERE task_id = ?";
        try {
            connection = handler.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, task_id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
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

    public int newTask(Task task) {
        String q = "INSERT INTO task(task_name,task_created_by,task_created_date,task_duration,task_tasktype,task_priority,task_completed,task_pass_fail,task_techs_required) VALUES (?,?,?,?,?,?,?,?,?)";
        try {
            connection = handler.getConnection();
            preparedStatement = connection.prepareStatement(q, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, task.getTask_name());
            preparedStatement.setInt(2, task.getTask_created_by());
            preparedStatement.setDate(3, task.getTask_created_date());
            preparedStatement.setInt(4, task.getTask_duration());
            preparedStatement.setString(5, task.getTask_type());
            preparedStatement.setInt(6, task.getPriority());
            preparedStatement.setBoolean(7, task.isCompleted());
            preparedStatement.setString(8, task.getPass_fail());
            preparedStatement.setInt(9,task.getTask_techs_required());
            preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                return id;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
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

    public Task getTasksByTaskId(int id) {
        connection = handler.getConnection();
        String q = "SELECT * FROM task WHERE task_id = ?";
        Task task = new Task();
        ResultSet rs = null;
        try {
            preparedStatement = connection.prepareStatement(q);
            preparedStatement.setInt(1, id);
            rs = preparedStatement.executeQuery();
            if (rs.next()) {
                task.setTask_id(rs.getInt("id"));
                task.setTask_name(rs.getString("name"));
                task.setTask_created_by(rs.getInt("created_by"));
                task.setTask_created_date(rs.getDate("created_date"));
                task.setTask_duration(rs.getInt("duration"));
                task.setTask_type(rs.getString("task_tasktype"));
                task.setPriority(rs.getInt("priority"));
                task.setCompleted(rs.getBoolean("completed"));
                task.setPass_fail(rs.getString("pass_fail"));
                return task;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
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
            return task;
        }
    }

    public void updateTask(Task task) {
        String q = "UPDATE task SET task_name = ?, task_tasktype = ?, task_created_by = ?, task_created_date = ?, task_duration = ?, task_priority = ?,task_completed = ?,task_pass_fail = ?,task_completed_date = ?,task_notes = ?, task_techs_required = ?  WHERE task_id = ?";
        try {
            connection = handler.getConnection();
            preparedStatement = connection.prepareStatement(q);
            preparedStatement.setString(1, task.getTask_name());
            preparedStatement.setString(2, task.getTask_type());
            preparedStatement.setInt(3, task.getTask_created_by());
            preparedStatement.setDate(4, task.getTask_created_date());
            preparedStatement.setInt(5, task.getTask_duration());
            preparedStatement.setInt(6, task.getPriority());
            preparedStatement.setBoolean(7, task.isCompleted());
            preparedStatement.setString(8, task.getPass_fail());
            preparedStatement.setDate(9, task.getCompleted_date());
            preparedStatement.setString(10, task.getTask_notes());
            preparedStatement.setInt(11, task.getTask_techs_required());
            preparedStatement.setInt(12, task.getTask_id());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
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

    public void newJobTask(int job_id, int taskId) {
        String q = "INSERT INTO job_task(job_id,task_id) VALUES (?,?)";
        try {
            connection = handler.getConnection();
            preparedStatement = connection.prepareStatement(q);
            preparedStatement.setInt(1, job_id);
            preparedStatement.setInt(2, taskId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
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

    public ObservableList<Task> getTasks() {

        connection = handler.getConnection();
        String q = "SELECT u.user_id,u.user_first_name,u.user_last_name,u.user_available_time,u.user_role,ut.usertask_created_date,t.*,j.job_id from task t left join user_task ut on t.task_id = ut.task_id left join user u on u.user_id = ut.user_id join job_task jt on t.task_id = jt.task_id join job j on jt.job_id = j.job_id ORDER BY t.task_id";
        ResultSet rs = null;
        ObservableList<Task> taskList = null;
        try {
            preparedStatement = connection.prepareStatement(q);
            rs = preparedStatement.executeQuery();
            taskList = getTaskList(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
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
        return taskList;
    }

    public ObservableList<Task> getTaskList(ResultSet rs) {
        ObservableList<Task> taskList = FXCollections.observableArrayList();
        Task existingTask = null;
        try {
            while (rs.next()) {
                Task task = new Task();
                User user = new User();
                ObservableList<User> taskUserList = FXCollections.observableArrayList();
                task.setTask_id(rs.getInt("task_id"));
                task.setTask_name(rs.getString("task_name"));
                task.setTask_created_by(rs.getInt("task_created_by"));
                task.setTask_created_date(rs.getDate("task_created_date"));
                task.setTask_duration(rs.getInt("task_duration"));
                task.setTask_type(rs.getString("task_tasktype"));
                task.setTask_notes(rs.getString("task_notes"));
                task.setTask_techs_required(rs.getInt("task_techs_required"));
                task.setPriority(rs.getInt("task_priority"));
                task.setCompleted(rs.getBoolean("task_completed"));
                task.setPass_fail(rs.getString("task_pass_fail"));
                task.setCompleted_date(rs.getDate("task_completed_date"));
                task.setAssigned_date(rs.getDate("usertask_created_date"));
                task.setTask_job(rs.getInt("job_id"));
                if (!taskList.stream().anyMatch(listtask -> listtask.getTask_id() == task.getTask_id())) {
                    taskList.add(task);
                    existingTask = task;
                    if (rs.getInt("user_id") > 0) {
                        user.setUser_id(rs.getInt("user_id"));
                        user.setFName(rs.getString("user_first_name"));
                        user.setSName(rs.getString("user_last_name"));
                        user.setRole(rs.getString("user_role"));
                        user.setUser_available_time(rs.getInt("user_available_time"));
                        if (task.getTask_tech() == null) {
                            taskUserList.add(user);
                            task.setTask_tech(taskUserList);
                        } else if (!task.getTask_tech().stream().anyMatch(listuser -> listuser.getUser_id() == user.getUser_id())) {
                            for (User u : task.getTask_tech()) {
                                taskUserList.add(u);
                            }
                            taskUserList.add(user);
                            task.setTask_tech(taskUserList);
                        }
                    }
                } else if (taskList.stream().anyMatch(listtask -> listtask.getTask_id() == task.getTask_id())) {
                    if (rs.getInt("task_id") > 0) {
                        user.setUser_id(rs.getInt("user_id"));
                        user.setFName(rs.getString("user_first_name"));
                        user.setSName(rs.getString("user_last_name"));
                        user.setRole(rs.getString("user_role"));
                        user.setUser_available_time(rs.getInt("user_available_time"));
                        if (existingTask.getTask_tech() == null) {
                            taskUserList.add(user);
                            existingTask.setTask_tech(taskUserList);
                        } else if (!existingTask.getTask_tech().stream().anyMatch(listuser -> listuser.getUser_id() == user.getUser_id())) {
                            for (User u : existingTask.getTask_tech()) {
                                taskUserList.add(u);
                            }
                            taskUserList.add(user);
                            existingTask.setTask_tech(taskUserList);
                        }
                    }
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return taskList;
    }

    public void newUserTask(int user_id, int task_id, Date created_date){
        String q = "INSERT INTO user_task(user_id,task_id,usertask_created_date) VALUES (?,?,?)";
        try {
            connection = handler.getConnection();
            preparedStatement = connection.prepareStatement(q);
            preparedStatement.setInt(1, user_id);
            preparedStatement.setInt(2, task_id);
            preparedStatement.setDate(3, created_date);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
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

    public void deleteUserTask(int task_id,int user_id){
        String sql = "DELETE FROM user_task WHERE task_id = ? AND user_id = ?";
        try {
            connection = handler.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, task_id);
            preparedStatement.setInt(2, user_id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
        } finally {
            if (preparedStatement != null) {
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
}
