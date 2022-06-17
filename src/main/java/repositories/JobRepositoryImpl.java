package repositories;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Job;
import models.Task;
import models.User;
import util.MySqlHandler;

import javax.persistence.criteria.CriteriaBuilder;
import java.sql.*;
import java.util.List;

public class JobRepositoryImpl implements JobRepository{

    private MySqlHandler handler = new MySqlHandler();
    private Connection connection = handler.getConnection();
    private PreparedStatement preparedStatement;
    private TaskRepositoryImpl taskRepo = new TaskRepositoryImpl();

    public ObservableList<Job> getJobs() {
        String q = "SELECT j.* from job j ORDER BY j.job_id";
        ObservableList<Job> jobList = null;
        ResultSet rs = null;
        try {
            connection = handler.getConnection();
            preparedStatement = connection.prepareStatement(q);
            rs = preparedStatement.executeQuery();
            jobList = getJobList(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return jobList;
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
        return jobList;
    }

    public ObservableList<Job> getJobList(ResultSet rs)  {
        ObservableList<Job> jList = FXCollections.observableArrayList();
        try {
            while (rs.next()) {
                Job job = new Job();
                job.setJob_id(rs.getInt("job_id"));
                job.setJob_name(rs.getString("job_name"));
                job.setJob_creator(rs.getInt("job_created_by"));
                job.setJob_created_date(rs.getDate("job_created_date"));
                job.setJob_cost_est(rs.getFloat("job_cost_est"));
                job.setJob_duration_est(rs.getInt("job_duration_est"));
                job.setJob_completed(rs.getBoolean("job_completed"));
                job.setCompleted_date(rs.getDate("job_completed_date"));
                job.setJob_notes(rs.getString("job_notes"));
                jList.add(job);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return jList;
    }

    public ObservableList<Task> getTasksbyJobId(int id)  {
        String q = "SELECT t.* FROM  task t JOIN job_task jt ON t.task_id = jt.task_id WHERE jt.job_id = ?";
        ResultSet rs = null;
        ObservableList<Task> list = FXCollections.observableArrayList();
        int res = 0;
        try {
            connection = handler.getConnection();
            preparedStatement = connection.prepareStatement(q);
            preparedStatement.setInt(1, id);
            rs = preparedStatement.executeQuery();
            while(rs.next()) {
                Task task = new Task();
                task.setTask_id(rs.getInt("task_id"));
                task.setTask_name(rs.getString("task_name"));
                task.setTask_created_by(rs.getInt("task_created_by"));
                task.setTask_created_date(rs.getDate("task_created_date"));
                task.setTask_duration(rs.getInt("task_duration"));
                task.setTask_type(rs.getString("task_tasktype"));
                task.setPriority(rs.getInt("task_priority"));
                task.setCompleted(rs.getBoolean("task_completed"));
                task.setPass_fail(rs.getString("task_pass_fail"));
                list.add(task);
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
        return list;
    }

    public void deleteById(int job_id)  {
        String sql = "DELETE FROM job WHERE job_id = ?";
        try {
            connection = handler.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, job_id);
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

    public void updateJob(Job job)  {
        String q = "UPDATE job SET job_name = ?,job_created_by = ?, job_created_date = ?, job_cost_est = ?, job_duration_est = ?, job_completed = ?,job_notes = ?,job_completed_date = ? WHERE job_id = ?";
        try {
            connection = handler.getConnection();
            preparedStatement = connection.prepareStatement(q);
            preparedStatement.setString(1, job.getJob_name());
            preparedStatement.setInt(2, job.getJob_creator());
            preparedStatement.setDate(3, job.getJob_created_date());
            preparedStatement.setFloat(4, job.getJob_cost_est());
            preparedStatement.setInt(5, job.getJob_duration_est());
            preparedStatement.setBoolean(6, job.getJob_completed());
            preparedStatement.setString(7,job.getJob_notes());
            preparedStatement.setDate(8,job.getCompleted_date());
            preparedStatement.setInt(9, job.getJob_id());
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

    public ObservableList<User> getJobStaff(int jobId)  {
        ObservableList<Task> taskList = getTasksbyJobId(jobId);
        ObservableList<User> userList = FXCollections.observableArrayList();
        ResultSet rs = null;
        try {
            for (Task task : taskList) {
                connection = handler.getConnection();
                String q = "Select u.* FROM user u JOIN user_task ut ON ut.user_id = u.user_id WHERE ut.task_id = ?";
                preparedStatement = connection.prepareStatement(q);
                preparedStatement.setInt(1, task.getTask_id());
                rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    User user = new User();
                    user.setUser_id(rs.getInt("user_id"));
                    user.setFName(rs.getString("user_first_name"));
                    user.setSName(rs.getString("user_last_name"));
                    user.setRole(rs.getString("user_role"));
                    user.setEmail(rs.getString("user_email"));
                    user.setImage(rs.getString("user_image"));
                    if (!userList.stream().anyMatch(listUser -> listUser.getUser_id() == user.getUser_id())) {
                        userList.add(user);
                    }
                }
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
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
        return userList;

    }

    public void newJob(Job job) {
        String q = "INSERT INTO job(job_name, job_created_by, job_created_date, job_cost_est, job_duration_est, job_notes) VALUES(?,?,?,?,?,?)";
        try {
            connection = handler.getConnection();
            preparedStatement = connection.prepareStatement(q);
            preparedStatement.setString(1, job.getJob_name());
            preparedStatement.setInt(2, job.getJob_creator());
            preparedStatement.setDate(3, job.getJob_created_date());
            preparedStatement.setFloat(4, job.getJob_cost_est());
            preparedStatement.setInt(5,job.getJob_duration_est());
            preparedStatement.setString(6,job.getJob_notes());
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

    public Job findJobById(int task_job)  {
        String q = "SELECT * FROM job WHERE job_id = ?";
        ResultSet rs = null;
        Job job = new Job();
        try{
            connection = handler.getConnection();
            preparedStatement = connection.prepareStatement(q);
            preparedStatement.setInt(1,task_job);
            rs = preparedStatement.executeQuery();
            if(rs.next()){
                job.setJob_id(rs.getInt("job_id"));
                job.setJob_name(rs.getString("job_name"));
                job.setJob_creator(rs.getInt("job_created_by"));
                job.setJob_created_date(rs.getDate("job_created_date"));
                job.setJob_cost_est(rs.getFloat("job_cost_est"));
                job.setJob_duration_est(rs.getInt("job_duration_est"));
                job.setJob_completed(rs.getBoolean("job_completed"));
                job.setCompleted_date(rs.getDate("job_completed_date"));
                job.setJob_notes(rs.getString("job_notes"));
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
        return job;
    }
}
