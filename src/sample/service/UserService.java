package sample.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    private final File usersFile = new File("Data", "users.txt");

    public boolean isAuthorized(String userName, String password) {
        boolean foundBuiltInOverride = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2 && parts[0].equalsIgnoreCase(userName)) {
                    if ("admin".equalsIgnoreCase(userName) || "user".equalsIgnoreCase(userName)) {
                        foundBuiltInOverride = true;
                    }
                    if (parts[1].equals(password)) {
                        return true;
                    }
                }
            }
        } catch (IOException ignored) {
        }

        if (!foundBuiltInOverride
                && ("admin".equalsIgnoreCase(userName) || "user".equalsIgnoreCase(userName))
                && "1234".equals(password)) {
            return true;
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

    public boolean changePassword(String userName, String newPassword) {
        if (userName == null || userName.trim().isEmpty()
                || newPassword == null || newPassword.isEmpty()) {
            return false;
        }

        File parent = usersFile.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            return false;
        }

        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(usersFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2
                        && parts[0].equalsIgnoreCase(userName)) {
                    lines.add(userName + "," + newPassword);
                    found = true;
                } else if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException ignored) {
        }

        if (!found) {
            lines.add(userName + "," + newPassword);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFile, false))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
