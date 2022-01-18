package handler;

import model.User;

import java.util.ArrayList;
import java.util.Map;

public class UserHandler {
    final ArrayList<User> userList = new ArrayList<>();

    public String addUser(Map<String, String> userData) {
        User user = new User(userData.get("Username"));
        user.setAuthorizationToken(user.getUsername() + "-mtcgToken");

        //TODO save the UserModel in the SQL Database and save the newly added ID

        userList.add(user);

        return "Added successfully";
    }

    public User getUser(String authorizationToken) throws ArrayIndexOutOfBoundsException {
        int index = getIndexOfUser(authorizationToken);
        return userList.get(index);
    }

    public void updateUser(Map<String, String> userData, String authorizationToken) {
        int index = getIndexOfUser(authorizationToken);

        //TODO update the UserModel in the SQL Database

        if (userData.get("Name") != null && !userData.get("Name").isEmpty())
            userList.get(index).setUsername(userData.get("Name"));
        if (userData.get("Bio") != null && !userData.get("Bio").isEmpty())
            userList.get(index).setBio(userData.get("Bio"));
        if (userData.get("Image") != null && !userData.get("Image").isEmpty())
            userList.get(index).setImage(userData.get("Image"));
    }

    public boolean checkIfUsernameExists(String username) {
        return userList.stream().anyMatch(u -> u.getUsername().equals(username));
    }

    public boolean checkIfUserExists(String authorizationToken) {
        return userList.stream().anyMatch(u -> u.getAuthorizationToken().equals(authorizationToken));
    }

    private int getIndexOfUser(String authorizationToken) {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getAuthorizationToken().equals(authorizationToken)) return i;
        }
        return -1;
    }

}
