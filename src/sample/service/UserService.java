package sample.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class UserService {

    private final File usersFile = new File("Data", "users.txt");

    public boolean isAuthorized(String userName, String password) {
        if (("admin".equalsIgnoreCase(userName) || "user".equalsIgnoreCase(userName))
                && "1234".equals(password)) {
            return true;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(usersFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2
                        && parts[0].equalsIgnoreCase(userName)
                        && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException ignored) {
        }
        return false;
    }

    public boolean userNameExists(String userName) {
        if ("admin".equalsIgnoreCase(userName) || "user".equalsIgnoreCase(userName)) {
            return true;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(usersFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2 && parts[0].equalsIgnoreCase(userName)) {
                    return true;
                }
            }
        } catch (IOException ignored) {
        }
        return false;
    }

    public boolean registerNewUser(String userName, String password) {
        if (userName == null || userName.trim().isEmpty()
                || password == null || password.isEmpty()
                || userNameExists(userName)) {
            return false;
        }

        File parent = usersFile.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFile, true))) {
            writer.write(userName + "," + password);
            writer.newLine();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
