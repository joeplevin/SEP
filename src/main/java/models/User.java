package models;


import javafx.beans.property.*;
import javafx.collections.ObservableList;
import repositories.UserRepository;
import repositories.UserRepositoryImpl;

public class User {

    private IntegerProperty user_id;
    private StringProperty fname;
    private StringProperty sname;
    private StringProperty email;
    private StringProperty image;
    private StringProperty role;
    private BooleanProperty user_active;
    private ListProperty<Task> user_tasks;
    private IntegerProperty user_available_time;

    public User(){
        this.user_id = new SimpleIntegerProperty();
        this.fname = new SimpleStringProperty();
        this.sname = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
        this.role = new SimpleStringProperty();
        this.image = new SimpleStringProperty();
        this.user_active = new SimpleBooleanProperty();
        this.user_tasks = new SimpleListProperty<Task>();
        this.user_available_time = new SimpleIntegerProperty();
    }

    public int getUser_id() {
        return user_id.get();
    }
    public IntegerProperty user_idProperty() {
        return user_id;
    }
    public void setUser_id(int user_id) {
        this.user_id.set(user_id);
    }

    public String getFName() {
        return fname.get();
    }
    public StringProperty fNameProperty() {
        return fname;
    }
    public void setFName(String name) {
        this.fname.set(name);
    }

    public String getRole() {
        return role.get();
    }
    public StringProperty roleProperty() {
        return role;
    }
    public void setRole(String role_name) {
        this.role.set(role_name);
    }

    public String getSName() {
        return sname.get();
    }
    public StringProperty sNameProperty() {
        return sname;
    }
    public void setSName(String sname) {
        this.sname.set(sname);
    }

    public String getEmail() {
        return email.get();
    }
    public StringProperty emailProperty() {
        return email;
    }
    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getImage() {
        return image.get();
    }
    public StringProperty imageProperty() {
        return image;
    }
    public void setImage(String image) {
        this.image.set(image);
    }

    public Boolean isUser_active() {
        return user_active.get();
    }
    public BooleanProperty user_activeProperty() {
        return user_active;
    }
    public void setUser_active(Boolean user_active) {
        this.user_active.set(user_active);
    }

    public ObservableList<Task> getUser_tasks() {
        return user_tasks.get();
    }
    public ListProperty<Task> user_tasksProperty() {
        return user_tasks;
    }
    public void setUser_tasks(ObservableList<Task> user_tasks) {
        this.user_tasks.set(user_tasks);
    }

    public int getUser_available_time() {
        return user_available_time.get();
    }
    public IntegerProperty user_available_timeProperty() {
        return user_available_time;
    }
    public void setUser_available_time(int user_available_time) {
        this.user_available_time.set(user_available_time);
    }

    @Override
    public String toString(){
        return "Id: " + this.user_id + " First Name: " + this.fname + " Last Name: " + this.sname + " Email: " + this.email + "Role Id: " + this.role + "Image:" + this.image;
    }
}
