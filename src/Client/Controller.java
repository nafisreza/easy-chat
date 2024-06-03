package Client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.event.ActionEvent;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Controller {
    @FXML
    public Pane pnSignIn;
    @FXML
    public Pane pnSignUp;
    @FXML
    public Button btnSignUp;
    @FXML
    public Button getStarted;
    @FXML
    public TextField regName;
    @FXML
    public TextField regPass;
    @FXML
    public TextField regEmail;
    @FXML
    public TextField regFirstName;
    @FXML
    public TextField regPhoneNo;
    @FXML
    public RadioButton male;
    @FXML
    public RadioButton female;
    @FXML
    public Label controlRegLabel;
    @FXML
    public Label success;
    @FXML
    public Label goBack;
    @FXML
    public TextField userName;
    @FXML
    public TextField passWord;
    @FXML
    public Label loginNotifier;
    @FXML
    public Label nameExists;
    @FXML
    public Label checkEmail;
    public static String username, password, gender;
    public static ArrayList<User> loggedInUser = new ArrayList<>();
    public static ArrayList<User> users = new ArrayList<>();

    private static final Path USER_FILE = Paths.get("src", "users.txt");

    // Handle registration
    public void registration() {
        loadUsers(); // Load existing users from the file
        if (!regName.getText().isEmpty()
                && !regPass.getText().isEmpty()
                && !regEmail.getText().isEmpty()
                && !regFirstName.getText().isEmpty()
                && !regPhoneNo.getText().isEmpty()
                && (male.isSelected() || female.isSelected())) {
            if (checkUser(regName.getText())) {
                if (checkEmail(regEmail.getText())) {
                    User newUser = new User();
                    newUser.name = regName.getText();
                    newUser.password = regPass.getText();
                    newUser.email = regEmail.getText();
                    newUser.fullName = regFirstName.getText();
                    newUser.phoneNo = regPhoneNo.getText();
                    newUser.gender = male.isSelected() ? "Male" : "Female";
                    users.add(newUser);
                    saveUsers(); // Save users to the file
                    goBack.setOpacity(1);
                    success.setOpacity(1);
                    makeDefault();
                    setOpacity(controlRegLabel, nameExists, checkEmail);
                } else {
                    checkEmail.setOpacity(1);
                    setOpacity(nameExists, goBack, controlRegLabel, success);
                }
            } else {
                nameExists.setOpacity(1);
                setOpacity(success, goBack, controlRegLabel, checkEmail);
            }
        } else {
            controlRegLabel.setOpacity(1);
            setOpacity(success, goBack, nameExists, checkEmail);
        }
    }

    // Handle login
    public void login() {
        loadUsers(); // Load existing users from the file
        username = userName.getText();
        password = passWord.getText();
        boolean login = false;
        for (User user : users) {
            if (user.name.equalsIgnoreCase(username) && user.password.equalsIgnoreCase(password)) {
                login = true;
                loggedInUser.add(user);
                gender = user.gender;
                break;
            }
        }
        if (login) {
            changeWindow();
        } else {
            loginNotifier.setOpacity(1);
        }
    }

    // Load existing user if found
    private void loadUsers() {
        users.clear();
        try {
            Files.createDirectories(USER_FILE.getParent());
            if (Files.exists(USER_FILE)) {
                BufferedReader reader = new BufferedReader(new FileReader(USER_FILE.toFile()));
                users.addAll(reader.lines().map(User::fromString).collect(Collectors.toList()));
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

// Save new user to the file
    private void saveUsers() {
        try {
            Files.createDirectories(USER_FILE.getParent());
            PrintWriter writer = new PrintWriter(new FileWriter(USER_FILE.toFile()));
            for (User user : users) {
                writer.println(user.toString());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

// Check if the user already exists
    private boolean checkUser(String username) {
        return users.stream().noneMatch(user -> user.name.equalsIgnoreCase(username));
    }
// Check if the email already exists
    private boolean checkEmail(String email) {
        return users.stream().noneMatch(user -> user.email.equalsIgnoreCase(email));
    }

    private void makeDefault() {
        regName.setText("");
        regPass.setText("");
        regEmail.setText("");
        regFirstName.setText("");
        regPhoneNo.setText("");
        male.setSelected(true);
        setOpacity(controlRegLabel, checkEmail, nameExists);
    }

    private void setOpacity(Label a, Label b, Label c, Label d) {
        if (a.getOpacity() == 1 || b.getOpacity() == 1 || c.getOpacity() == 1 || d.getOpacity() == 1) {
            a.setOpacity(0);
            b.setOpacity(0);
            c.setOpacity(0);
            d.setOpacity(0);
        }
    }

    private void setOpacity(Label controlRegLabel, Label checkEmail, Label nameExists) {
        controlRegLabel.setOpacity(0);
        checkEmail.setOpacity(0);
        nameExists.setOpacity(0);
    }

    public void changeWindow() {
        try {
            Stage stage = (Stage) userName.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Room.fxml"));
            Parent root = loader.load();
            Room roomController = loader.getController();
            roomController.setLoggedInUser(loggedInUser.get(0));
            stage.setScene(new Scene(root, 330, 560));
            stage.setTitle(username);
            stage.setOnCloseRequest(event -> {
                saveUsers(); // Save users on application exit
                System.exit(0);
            });
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getSource().equals(btnSignUp)) {
            pnSignUp.toFront();
        }
        if (event.getSource().equals(getStarted)) {
            pnSignIn.toFront();
        }
        loginNotifier.setOpacity(0);
        userName.setText("");
        passWord.setText("");
    }

    @FXML
    private void handleMouseEvent(MouseEvent event) {
        if (event.getSource() == goBack) {
            pnSignIn.toFront();
        }
        regName.setText("");
        regPass.setText("");
        regEmail.setText("");
    }
}