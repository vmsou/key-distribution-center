package secure;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Global {
    Users users;
    Messages messages;

    // Constructors
    public Global() {
        users = getUsersData("data/users.csv");
        // messages = getMessagesData("data/messages.csv");
        messages = new Messages();
    }

    // Methods
    public void add(UserEntity u) { users.put(u.getId(), u); }
    public void add(Message m) { messages.put(m.getId(), m); }

    public Users getUsersData(String filename) {
        int id = 1;
        Users users = new Users();
        String sc = Engine.fstream(filename);

        UserEntity.count = id;
        return users;
    }

    public void showUsers() {
        for (UserEntity u : users.values())
            System.out.println(u);
    }

    public void showMessages() {
        for (Message m : messages.values())
            System.out.println(m);
    }

}
