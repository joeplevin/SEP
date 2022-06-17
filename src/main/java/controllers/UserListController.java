package controllers;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import models.Job;
import models.Task;
import models.User;
import org.kordamp.ikonli.javafx.FontIcon;
import repositories.TaskRepository;
import repositories.TaskRepositoryImpl;
import repositories.UserRepository;
import repositories.UserRepositoryImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;

public class UserListController {

    @FXML
    public Node ulist_main;

    //User List View
    @FXML
    public Label label_usersList;
    @FXML
    public TableView<User> table_userTable;
    @FXML
    public TableColumn<User,String> tablec_Ufname;
    @FXML
    public TableColumn<User,String> tablec_Usname;
    @FXML
    public TableColumn<User,String> tablec_Uemail;
    @FXML
    public TableColumn<User,String> tablec_Urole;
    @FXML
    public TableColumn<User,Boolean> tablec_Uedit;
    @FXML
    public TableColumn<User,Boolean> tablec_Udelete;
    @FXML
    public TableColumn<User,Boolean> tablec_Uactive;
    @FXML
    public TextField tf_filter;
    @FXML
    public Button button_new_user;
    @FXML
    public Button button_back;
    @FXML
    public Button updateButton;
    @FXML
    public AnchorPane anchor_user_details;
    @FXML
    public Label label_selecteduser_name;
    @FXML
    public Label label_selecteduser_role;
    @FXML
    public Label label_selecteduser_email;
    @FXML
    public StackPane sp_userlistprogress;
    @FXML
    public ProgressIndicator pi_userlistprogress;
    @FXML
    public Circle circle_selecteduser_image;
    @FXML
    public PieChart pie_userfocus;
    @FXML
    public LineChart<String,Integer> line_userfocus;
    @FXML
    private DashboardController dashboardController;

    private UserRepository userRepo = new UserRepositoryImpl();
    private TaskRepository taskRepo = new TaskRepositoryImpl();

    private ObservableList<User> userData = FXCollections.observableArrayList();

    public void initialize(){
        //Listener for Focus Pane
        ChangeListener<Object> listener = (obsval,oldval,newval) ->{
            if(table_userTable.isFocused()){
                try {
                    showFocusedUser();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        table_userTable.focusedProperty().addListener(listener);
        table_userTable.getSelectionModel().selectedItemProperty().addListener(listener);
    }

    public void setUsersTable(ObservableList<User> data){

        userData = data;
        //set focused pane visibility
        for(User user : userData){
            user.user_activeProperty().addListener((obsval,oldval,newval) ->{
                userRepo.updateUser(user);
            });
        }
        anchor_user_details.setVisible(false);
        pie_userfocus.setVisible(false);
        line_userfocus.setVisible(false);

        //set columns
        tablec_Ufname.setCellValueFactory(cellData -> cellData.getValue().fNameProperty());
        tablec_Usname.setCellValueFactory(cellData -> cellData.getValue().sNameProperty());
        tablec_Uemail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        tablec_Urole.setCellValueFactory(cellData -> cellData.getValue().roleProperty());

        //User Active Checkbox
        tablec_Uactive.setCellValueFactory(cellData -> cellData.getValue().user_activeProperty());
        tablec_Uactive.setCellFactory(CheckBoxTableCell.forTableColumn(tablec_Uactive));

        //boolean cell value so cell will only show on !empty rows
        tablec_Uedit.setCellValueFactory(params -> new SimpleBooleanProperty(params.getValue() != null));
        tablec_Udelete.setCellValueFactory(params -> new SimpleBooleanProperty(params.getValue() != null));

        //create cell factory with update button & pass params to new controller
        tablec_Uedit.setCellFactory(updateUserColumn -> new TableCell<User,Boolean>(){
            //set button to cell
            final Button button_edit_user = new Button();
            //override cell factory
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                //if no row data, do not display
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else{
                    //button set up
                    button_edit_user.setGraphic(new FontIcon("fltfal-edit-16"));
                    button_edit_user.setId("button_edit_user");
                    setGraphic(button_edit_user);
                    setText(null);
                    //update on click
                    button_edit_user.setOnAction(event -> {
                        //get user to pass to update view
                        User user = getTableView().getItems().get(getIndex());
                        //update view
                        editUser(user);
                    });
                }
            }
        });

        //create cell factory with delete button & pass params for deletion
        tablec_Udelete.setCellFactory(deleteColumn -> new TableCell<User,Boolean>() {
            Button button_delete_user = new Button();
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else{
                    button_delete_user.setGraphic(new FontIcon("fltral-delete-16"));
                    button_delete_user.setId("button_delete_user");
                    button_delete_user.setOnAction(event -> {
                        User user = getTableView().getItems().get(getIndex());
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " + user.getFName() + "?", ButtonType.YES, ButtonType.NO);
                        alert.showAndWait();
                        if(alert.getResult() == ButtonType.YES) {
                            userData.remove(user);
                            userRepo.deleteById(user.getUser_id());
                        }
                    });
                    setGraphic(button_delete_user);
                    setText(null);
                }
            }
        });
        table_userTable.setEditable(true);
        table_userTable.setItems(userData);
    }

    public void showFocusedUser() throws IOException {
        //Set Focus Pane
        if(table_userTable.getSelectionModel().getSelectedItem() != null) {
            anchor_user_details.setVisible(true);
            User selectedUser = table_userTable.getSelectionModel().getSelectedItem();
            label_selecteduser_name.setText(selectedUser.getFName() + " " + selectedUser.getSName());
            label_selecteduser_role.setText(selectedUser.getRole());
            // No image, show placeholder
            Image image;
            if (selectedUser.getImage() != null) {
                image = new Image(selectedUser.getImage());
            } else {
                image = new Image("vulture_white.png",30,30,false,false);
            }
            circle_selecteduser_image.setFill(new ImagePattern(image));

            ObservableMap<String,Integer> taskTypeAssigned = FXCollections.observableHashMap();
            // Technician Pie & Line
            if(selectedUser.getUser_tasks() != null) {
                if(!selectedUser.getUser_tasks().isEmpty()) {
                    pie_userfocus.getData().clear();
                    pie_userfocus.setVisible(true);
                    line_userfocus.getData().clear();
                    line_userfocus.setVisible(true);

                    for (Task task : selectedUser.getUser_tasks()) {
                        String tasktype = task.getTask_type();
                        taskTypeAssigned.merge(tasktype, 1, Integer::sum);
                    }
                    for (Map.Entry<String, Integer> entry : taskTypeAssigned.entrySet()) {
                        PieChart.Data data = new PieChart.Data(entry.getKey(), entry.getValue());
                        pie_userfocus.getData().add(data);
                    }

                    XYChart.Series assignedSeries = new XYChart.Series();
                    assignedSeries.setName("Assigned Tasks");
                    XYChart.Series completedSeries = new XYChart.Series();
                    completedSeries.setName("Completed Tasks");
                    for (int i = 7; i > 0; i--) {
                        LocalDate date = LocalDate.now().minusDays(i);
                        int assigned = 0;
                        int completed = 0;
                        for (Task task : selectedUser.getUser_tasks()) {
                            if (task.getAssigned_date() != null) {
                                if (task.getAssigned_date().toLocalDate().isEqual(date)) {
                                    assigned++;
                                }
                            }
                            if (task.isCompleted() && task.getCompleted_date() != null) {
                                if (task.getCompleted_date().toLocalDate().isEqual(date)) {
                                    completed++;
                                }
                            }
                        }
                        assignedSeries.getData().add(new XYChart.Data(date.toString().replace("2022-", ""), assigned));
                        completedSeries.getData().add(new XYChart.Data(date.toString().replace("2022-", ""), completed));
                    }
                    line_userfocus.getData().add(assignedSeries);
                    line_userfocus.getData().add(completedSeries);
                }else{
                    line_userfocus.setVisible(false);
                    pie_userfocus.setVisible(false);
                }
            } else{
                line_userfocus.setVisible(false);
                pie_userfocus.setVisible(false);
            }

        }

    }

    public void newUser(MouseEvent mouseEvent){
        dashboardController.showNewUserPane();
    }

    // Filter function
    public void filterList(MouseEvent e){
        tf_filter.clear();
//        ObservableList<User> userData = getUserData();
        FilteredList<User> filteredList = new FilteredList<>(userData);
        SortedList<User> sortedList = new SortedList<>(filteredList);
        table_userTable.setItems(userData);
        //Initial filtered list
        tf_filter.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(user -> {
                if (newValue.isEmpty() || newValue == null) {
                    return true;
                }
                String seach = newValue.toLowerCase();
                if (user.fNameProperty().toString().toLowerCase().indexOf(seach) > -1) {
                    return true;
                } else if (user.sNameProperty().toString().toLowerCase().indexOf(seach) > -1) {
                    return true;
                } else if (user.emailProperty().toString().toLowerCase().indexOf(seach) > -1) {
                    return true;
                } else if (user.roleProperty().toString().toLowerCase().indexOf(seach) > -1) {
                    return true;
                } else {
                    return false;
                }
            });
        });
        sortedList.comparatorProperty().bind(table_userTable.comparatorProperty());
        table_userTable.setEditable(true);
        table_userTable.setItems(sortedList);
    }

    //Dashboard Navigation
    public void setDashboardController(DashboardController dashboardController){
        this.dashboardController = dashboardController;
    }
    public void showPage() {
        ulist_main.setVisible(true);
        ulist_main.toFront();
    }
    public void hidePage() {
        ulist_main.setVisible(false);
    }
    public void toNewUser(){
        dashboardController.showNewUserPane();
    }
    public void editUser(User user){
        dashboardController.showEditUserPane(user);
    }

}

