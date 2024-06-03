// Room.java
package Client;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class Room extends Thread implements Initializable {
    @FXML
    public Label clientName;
    @FXML
    public Button chatBtn;
    @FXML
    public Pane chat;
    @FXML
    public TextField msgField;
    @FXML
    public TextArea msgRoom;
    @FXML
    public Label online;
    @FXML
    public Label fullName;
    @FXML
    public Label email;
    @FXML
    public Label phoneNo;
    @FXML
    public Label gender;
    @FXML
    public Pane profile;
    @FXML
    public Button profileBtn;
    @FXML
    public TextField fileChoosePath;
    @FXML
    public Button btnChooseFile;
    @FXML
    public ImageView imageView;
    @FXML
    public Circle imageCircle;
    @FXML
    public Button sendImage;
    @FXML
    public ScrollPane scrollPane;
    @FXML
    public Button logoutBtn;
    @FXML
    public ImageView profileImage;

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private FileChooser fileChooser;
    private File filePath;
    private static final Path MESSAGE_HISTORY_FILE = Paths.get("src", "message.txt");
    private User loggedInUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            this.socket = new Socket("127.0.0.1", 8889);
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(socket.getOutputStream(), true);
            loadMessageHistory();
            start();

            // Load the default profile picture
            InputStream inputStream = getClass().getResourceAsStream("/icons/bussiness-man.png");
            if (inputStream != null) {
                Image defaultProfilePicture = new Image(inputStream);
                profileImage.setImage(defaultProfilePicture);
                imageCircle.setFill(new ImagePattern(defaultProfilePicture));
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception (show an error message to the user)
        }

        fileChooser = new FileChooser();
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        clientName.setText(loggedInUser.name);
        fullName.setText(loggedInUser.fullName);
        email.setText(loggedInUser.email);
        phoneNo.setText(loggedInUser.phoneNo);
        gender.setText(loggedInUser.gender);
    }

    @FXML
    private void handleSendEvent(MouseEvent event) {
        sendMessage();
    }

    @FXML
    public void handleLogout(ActionEvent actionEvent) {
        try {
            writer.println("exit");
            socket.close();
            Stage stage = (Stage) logoutBtn.getScene().getWindow();
            stage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void send(MouseEvent mouseEvent) {
        sendMessage();
    }

    @FXML
    public void sendKey(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case ENTER:         // Send message when enter key is pressed on keyboard
                sendMessage();
                break;
        }
    }

    private void sendMessage() {
        String message = msgField.getText().trim();
        if (!message.isEmpty()) {
            if (writer != null) {
                writer.println(loggedInUser.name + ": " + message);
                msgField.clear();
                msgRoom.appendText(loggedInUser.name + ": " + message + "\n");
                saveMessage(loggedInUser.name + ": " + message);
            } else {
                System.out.println("error");
                // Handle the case when the writer is null
            }
        }
    }

    // Read the message history from the file
    private void loadMessageHistory() {
        try {
            Files.createDirectories(MESSAGE_HISTORY_FILE.getParent());
            if (Files.exists(MESSAGE_HISTORY_FILE)) {
                BufferedReader reader = new BufferedReader(new FileReader(MESSAGE_HISTORY_FILE.toFile()));
                String line;
                while ((line = reader.readLine()) != null) {
                    msgRoom.appendText(line + "\n");
                }
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Save the message to the file
    private void saveMessage(String message) {
        try {
            Files.createDirectories(MESSAGE_HISTORY_FILE.getParent());
            PrintWriter writer = new PrintWriter(new FileWriter(MESSAGE_HISTORY_FILE.toFile(), true));
            writer.println(message);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String msg;
        try {
            while ((msg = reader.readLine()) != null) {
//                msgRoom.appendText(msg + "\n");
//                saveMessage(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Handle the event when the user clicks on the "Choose File" button to change profile picture
    @FXML
    private void chooseFile(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        fileChooser.setTitle("Choose Profile Picture");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            filePath = file;
            fileChoosePath.setText(file.getName());
        }
    }

    @FXML
    private void handleChatBtn(ActionEvent event) {
        chat.toFront(); // Switch to the chat pane
    }

    private void handleUpdateButton(ActionEvent event) {
        // Switch back to the chat room
        chat.toFront();
    }

    @FXML
    private void handleProfileBtn(ActionEvent event) {
        profile.toFront(); // Switch to the profile pane
    }

    @FXML
    private void sendMessageByKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            sendMessage();
            event.consume(); // Consume the event to prevent further handling
        }
    }

    @FXML
    private void saveImage(ActionEvent event) {
        if (filePath != null) {
            try {
                BufferedImage bufferedImage = ImageIO.read(filePath);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                imageCircle.setFill(new ImagePattern(image));
                chat.toFront();
                // Save the image file path in the loggedInUser object
                loggedInUser.imageFile = filePath.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        chat.toFront();
    }
}