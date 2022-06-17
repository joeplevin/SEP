package models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import repositories.UserCredsRepository;
import repositories.UserCredsRepositoryImpl;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Random;

public class UserCreds {

    private IntegerProperty user_id;
    private StringProperty username;
    private StringProperty password;
    private IntegerProperty logged_in;
    private UserCredsRepository credsRepository = new UserCredsRepositoryImpl();

    //using PBKDF2 for password hashing
    private static final Random random = new SecureRandom();
    private static final int iterations = 1000;
    private static final int keylength = 256;
    private static final String salt = "#~4$J";

    public UserCreds(){
        this.user_id = new SimpleIntegerProperty();
        this.username = new SimpleStringProperty();
        this.password = new SimpleStringProperty();
        this.logged_in = new SimpleIntegerProperty();
    }

    public String getUsername() {
        return username.get();
    }
    public void setUsername(String username) {
        this.username.set(username);
    }
    public StringProperty usernameProperty(){
        return username;
    }

    public String getPassword() {
        return password.get();
    }
    public void setPassword(String password) {
        this.password.set(password);
    }
    public StringProperty passwordProperty(){
        return password;
    }

    public int getUser_id() {
        return user_id.get();
    }
    public void setUser_id(int user_id) {
        this.user_id.set(user_id);
    }
    public IntegerProperty userIdProperty(){
        return user_id;
    }

    public int getLogged_in() {
        return logged_in.get();
    }
    public void setLogged_in(int logged_in) {
        this.logged_in.set(logged_in);
    }
    public IntegerProperty loggedinProperty(){
        return logged_in;
    }



    // METHODS FOR PASSWORD ENCRYPTION & AUTH
    //get hash
    public static byte[] hash(char[] pass){
        PBEKeySpec spec = new PBEKeySpec(pass,salt.getBytes(), iterations,keylength);
        try{
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        } catch(NoSuchAlgorithmException | InvalidKeySpecException e){
            throw new AssertionError("Can't create hash:" + e.getMessage(),e);
        } finally{
            spec.clearPassword();
        }
    }
    // encrypt hash
    public static String encryptPass(String pass){
        String res = null;
        byte[] securePass = hash(pass.toCharArray());
        res = Base64.getEncoder().encodeToString(securePass);
        return res;
    }
    // verify pass
    public static boolean verifyPass(String providedPass, String storedPass){
        boolean res = false;
        String newSecurePass = encryptPass(providedPass);
        return res = newSecurePass.equalsIgnoreCase(storedPass);
    }


    //METHODS FOR SIGN IN
    // check username
    public int checkUser(String username){
        int res = 0;
        res = credsRepository.authUser(username);
        return res;
    }
    // check password
    public int checkPass(int id, String pass) {
        String password = encryptPass(pass);
        String res = null;
        res = credsRepository.authPass(id);
        if (password.equals(res)) {
            credsRepository.setCurrentUser(id);
            return id;
        }else{
            return -2;
        }
    }
}
