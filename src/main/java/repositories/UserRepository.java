package repositories;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import models.User;

import java.sql.ResultSet;

public interface UserRepository {

    User getUserById(int id);
    ObservableList<User> getUsers();
    ObservableList<User> getUserList(ResultSet rs);
    ObservableList<String> getRoleList();
    void updateUser(User user);
    int newUser(User user);
    void deleteById(int user_id);
    ObservableList<String> getTaskTypeList();

}
