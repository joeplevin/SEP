package repositories;

import javafx.collections.ObservableList;
import models.User;
import models.UserCreds;

import java.sql.ResultSet;

public interface UserCredsRepository {
    void clearCurrentUser();
    User getCurrentUser();
    UserCreds getCurrentUserCreds();
    void setCurrentUser(int id);
    UserCreds findById(int id);
    int authUser(String username);
    String authPass(int id);
    UserCreds getUserCredsFromResultSet(ResultSet rs);
    User getUserFromCredsResultSet(ResultSet rs);
    ObservableList<UserCreds> getUserCreds();
    ObservableList<UserCreds> getUserCredsList(ResultSet rs);
    void newCreds(UserCreds creds,int userId);
    UserCreds findByUserId(int id);
    void updateUserCreds(UserCreds creds);
    String findByPassById(int id);
}
