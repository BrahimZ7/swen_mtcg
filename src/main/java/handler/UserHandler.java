package handler;

import model.User;

import java.util.*;

public class UserHandler {
    final ArrayList<User> userList = new ArrayList<>();

    public String addUser(Map<String, String> userData) {
        User user = new User(userData.get("Username"));
        user.setAuthorizationToken(user.getUsername() + "-mtcgToken");
        user.setPasswordHash(userData.get("Password"));

        if (userList.size() == 0)
            user.setId("1");
        else
            user.setId(Integer.toString(Integer.parseInt(userList.get(userList.size() - 1).getId()) + 1));

        //TODO save the UserModel in the SQL Database and save the newly added ID

        userList.add(user);

        return "Added successfully";
    }

    public User getUser(String authorizationToken) throws ArrayIndexOutOfBoundsException {
        int index = getIndexOfUser(authorizationToken);
        return userList.get(index);
    }

    public User getUserByID(String id) {
        return userList.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
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

    public void updateUser(User user) {
        int index = getIndexOfUser(user.getAuthorizationToken());

        //TODO update the User in the SQL Database

        userList.set(index, user);
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

    private int getIndexOfUsername(String username) {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUsername().equals(username)) return i;
        }
        return -1;
    }

    public String loginUser(String username, String password) {
        int index = getIndexOfUsername(username);

        System.out.println(index);

        //TODO check the password hashes and not the normal passwords

        User user = userList.get(index);

        if (user.getPasswordHash().equals(password))
            return user.getAuthorizationToken();

        return null;
    }

    public String getScoreboard() {
        StringBuilder stringBuilder = new StringBuilder();

        List<User> sortedList = new ArrayList<User>();
        sortedList.addAll(userList);

        Collections.sort(sortedList, Comparator.comparing(User::getWon));

        for (int i = 0; i < sortedList.size(); i++) {
            stringBuilder.append(i + ": " + sortedList.get(i).getUsername() + " " + sortedList.get(i).getWon() + ", " + sortedList.get(i).getLost() + ", " + sortedList.get(i).getDraw() + "\n");
        }

        return stringBuilder.toString();
    }
}
