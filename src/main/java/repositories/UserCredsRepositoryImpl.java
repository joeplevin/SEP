package repositories;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.User;
import models.UserCreds;
import util.MySqlHandler;

import java.sql.*;

public class UserCredsRepositoryImpl implements UserCredsRepository {

    private MySqlHandler handler = new MySqlHandler();
    private Connection connection = handler.getConnection();
    private PreparedStatement preparedStatement;

    public UserCredsRepositoryImpl() {
    }

    public void clearCurrentUser(){
        String q = "UPDATE user_cred SET cred_logged_in = 0";
        try {
            MySqlHandler.dbUpdate(q);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getCurrentUser(){
        String q = "SELECT u.* FROM user u INNER JOIN user_cred uc ON u.user_id = uc.user_id WHERE uc.cred_logged_in = 1";
        User user = new User();
        try {
            ResultSet rs = MySqlHandler.dbQuery(q);
            user = getUserFromCredsResultSet(rs);
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public UserCreds getCurrentUserCreds(){
        String q = "SELECT * FROM user_cred WHERE cred_logged_in = 1";
        UserCreds userCreds = new UserCreds();
        try{
            ResultSet rs = MySqlHandler.dbQuery(q);
            userCreds = getUserCredsFromResultSet(rs);
            return userCreds;
        } catch(SQLException e){
        }
        return userCreds;
    }

    public void setCurrentUser(int id){
        String q = "UPDATE user_cred SET cred_logged_in = 1 WHERE user_id = ?";
        connection = handler.getConnection();
        try {
            preparedStatement = connection.prepareStatement(q);
            preparedStatement.setInt(1,id);
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

    public UserCreds findById(int id) {
        String q = "SELECT * from user_cred where user_id = ?";
        UserCreds creds = null;
        try {
            preparedStatement = connection.prepareStatement(q);
            preparedStatement.setInt(1,id);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                creds = getUserCredsFromResultSet(rs);
            }
            return creds;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return creds;
    }

    public int authUser(String username) {
        String q = "SELECT user_id,cred_username from user_cred where cred_username = ?";
        int user = 0;
        ResultSet rs = null;
        connection = handler.getConnection();
        try {
            preparedStatement = connection.prepareStatement(q);
            preparedStatement.setString(1, username);
            rs = preparedStatement.executeQuery();
            if (rs.next()) {
                if(rs.getString("cred_username").equals(username)) {
                    user = rs.getInt("user_id");
                    return user;
                } else{
                    return -9;
                }
            } else {
                return -1;
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
        return -1;
    }

    public String authPass(int id) {
        String q = "SELECT cred_password from user_cred where user_id = ?";
        connection = handler.getConnection();
        String pass = "";
        ResultSet rs = null;
        try {
            preparedStatement = connection.prepareStatement(q);
            preparedStatement.setInt(1, id);
            rs = preparedStatement.executeQuery();
            int count = 0;
            if(rs.next()) {
                pass = rs.getString("cred_password");
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
        return pass;
    }

    public UserCreds getUserCredsFromResultSet(ResultSet rs){
        UserCreds user = null;
        try {
            if(rs.next()){
                user = new UserCreds();
                user.setUser_id(rs.getInt("user_id"));
                user.setPassword(rs.getString("cred_password"));
                user.setUsername(rs.getString("cred_username"));
                user.setLogged_in(rs.getInt("cred_logged_in"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public User getUserFromCredsResultSet(ResultSet rs){
        User user = null;
        try {
            if(rs.next()){
                user = new User();
                user.setUser_id(rs.getInt("user_id"));
                user.setFName(rs.getString("user_first_name"));
                user.setSName(rs.getString("user_last_name"));
                user.setEmail(rs.getString("user_email"));
                user.setImage(rs.getString("user_image"));
                user.setRole(rs.getString("user_role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public ObservableList<UserCreds> getUserCreds(){
        String q = "SELECT * FROM user_cred";
        ObservableList<UserCreds> userList = FXCollections.observableArrayList();
        try{
            ResultSet rs = MySqlHandler.dbQuery(q);
            userList = getUserCredsList(rs);
            return userList;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return userList;
    }

    public ObservableList<UserCreds> getUserCredsList(ResultSet rs) {
        ObservableList<UserCreds> userList = FXCollections.observableArrayList();
        try {
            while (rs.next()) {
                UserCreds user = new UserCreds();
                user.setUser_id(rs.getInt("user_id"));
                user.setPassword(rs.getString("cred_password"));
                user.setUsername(rs.getString("cred_username"));
                user.setLogged_in(rs.getInt("cred_logged_in"));
                userList.add(user);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return userList;
    }

    public void newCreds(UserCreds creds,int userId) {
        connection = handler.getConnection();
        String q = "INSERT INTO user_cred(user_id,cred_password,cred_username,cred_logged_in) VALUES (?,?,?,?)";
        try {
            preparedStatement = connection.prepareStatement(q);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, creds.getPassword());
            preparedStatement.setString(3, creds.getUsername());
            preparedStatement.setInt(4, 0);
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

    public UserCreds findByUserId(int id) {
        String q = "SELECT * FROM user_cred where user_id = ?";
        ResultSet rs = null;
        UserCreds creds = null;
        connection = handler.getConnection();
        try {
            preparedStatement = connection.prepareStatement(q);
            preparedStatement.setInt(1, id);
            rs = preparedStatement.executeQuery();
            creds = getUserCredsFromResultSet(rs);
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
        return creds;
    }

    public void updateUserCreds(UserCreds creds){
        connection = handler.getConnection();
        String q =  "UPDATE user_cred SET cred_username = ?,cred_password = ? WHERE user_id = ?";
        try {
            preparedStatement = connection.prepareStatement(q);
            preparedStatement.setString(1,creds.getUsername());
            preparedStatement.setString(2, creds.getPassword());
            preparedStatement.setInt(3,creds.getUser_id());
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

    public String findByPassById(int id){
        connection = handler.getConnection();
        String q = "SELECT cred_password FROM user_cred where user_id = ?";
        ResultSet rs = null;
        String pass = null;
        try {
            preparedStatement = connection.prepareStatement(q);
            preparedStatement.setInt(1, id);
            rs = preparedStatement.executeQuery();
            while(rs.next()){
                pass = rs.getString("cred_password");
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
        return pass;
    }
}

