package Client;

public class User {
    public String name;
    public String fullName;
    public String password;
    public String email;
    public String gender;
    public String phoneNo;
    public String imageFile;

    public String toString() {
        return name + "," + fullName + "," + password + "," + email + "," + gender + "," + phoneNo + "," + imageFile;
    }

    // Splitting the user reg info so it can be stored in a text file
    public static User fromString(String userStr) {
        String[] parts = userStr.split(",");
        User user = new User();
        user.name = parts[0];
        user.fullName = parts[1];
        user.password = parts[2];
        user.email = parts[3];
        user.gender = parts[4];
        user.phoneNo = parts[5];
        user.imageFile = parts.length > 6 ? parts[6] : "";
        return user;
    }
}