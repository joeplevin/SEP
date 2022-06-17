package repositories;

import javafx.collections.ObservableList;
import models.Job;
import models.Task;
import models.User;

import java.sql.ResultSet;

public interface JobRepository {
    ObservableList<Job> getJobs();
    ObservableList<Job> getJobList(ResultSet rs);
    ObservableList<Task> getTasksbyJobId(int id);
    void deleteById(int job_id);
    void updateJob(Job job);
    ObservableList<User> getJobStaff(int jobId);
    void newJob(Job job);
    Job findJobById(int task_job);
}
