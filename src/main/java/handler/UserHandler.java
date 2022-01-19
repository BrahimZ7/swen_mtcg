package handler;

import model.User;
import service.DatabaseService;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class UserHandler {
    private final DatabaseService databaseService;
    private final ArrayList<User> userList = new ArrayList<>();

    public UserHandler(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public void init() {
        if (databaseService != null) {
            List<User> userList = databaseService.getUsers();

            this.userList.addAll(userList);
        }
    }

    public boolean addUser(Map<String, String> userData) {
        User user = new User(userData.get("Username"));
        user.setAuthorizationToken(user.getUsername() + "-mtcgToken");
        user.setPasswordHash(hashPassword(userData.get("Password")));

        if (checkIfUsernameExists(user.getUsername())) {
            return false;
        }

        if (userList.size() == 0)
            user.setId("1");
        else
            user.setId(Integer.toString(Integer.parseInt(userList.get(userList.size() - 1).getId()) + 1));

        userList.add(user);
        if (databaseService != null)
            databaseService.saveUser(user);

        return true;
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


        if (userData.get("Name") != null && !userData.get("Name").isEmpty())
            userList.get(index).setUsername(userData.get("Name"));
        if (userData.get("Bio") != null && !userData.get("Bio").isEmpty())
            userList.get(index).setBio(userData.get("Bio"));
        if (userData.get("Image") != null && !userData.get("Image").isEmpty())
            userList.get(index).setImage(userData.get("Image"));
        if (databaseService != null)
            databaseService.updateUser(userList.get(index));
    }

    public void updateUser(User user) {
        int index = getIndexOfUser(user.getAuthorizationToken());

        userList.set(index, user);

        if (databaseService != null)
            databaseService.updateUser(user);
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

        User user = userList.get(index);

        String passwordHash = hashPassword(password);

        if (user.getPasswordHash().equals(passwordHash))
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

    private String hashPassword(final String password) {
        try {
            String hashtext = null;

            MessageDigest md = MessageDigest.getInstance("MD5");
            // Compute message digest of the input
            byte[] messageDigest = md.digest(password.getBytes());

            hashtext = convertToHex(messageDigest);

            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            return password;
        }
    }

    private String convertToHex(final byte[] messageDigest) {
        BigInteger bigint = new BigInteger(1, messageDigest);
        String hexText = bigint.toString(16);
        while (hexText.length() < 32) {
            hexText = "0".concat(hexText);
        }
        return hexText;
    }
}
